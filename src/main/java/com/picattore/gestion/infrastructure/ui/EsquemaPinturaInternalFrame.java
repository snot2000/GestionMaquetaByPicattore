package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.*;
import com.picattore.gestion.domain.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

public class EsquemaPinturaInternalFrame extends JInternalFrame implements LanguageChangeListener {

    private final EsquemaPinturaService esquemaService;
    private final PaisService paisService;
    private final OperadoraService operadoraService;
    private final IdiomaService idiomaService;
    private JTable table;
    private DefaultTableModel tableModel;

    public EsquemaPinturaInternalFrame(EsquemaPinturaService esquemaService, PaisService paisService, OperadoraService operadoraService, IdiomaService idiomaService) {
        super("Gestión de Esquemas de Pintura", true, true, true, true);
        this.esquemaService = esquemaService;
        this.paisService = paisService;
        this.operadoraService = operadoraService;
        this.idiomaService = idiomaService;
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Tabla
        String[] columnNames = {"ID", "País", "Operadora", "Nombre", "Año Inicio", "Año Fin"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        
        // Ocultar la columna ID (índice 0) de la vista
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0));

        // Añadir doble clic para editar
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarSeleccionado();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Añadir");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Borrar");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        this.add(buttonPanel, BorderLayout.SOUTH);

        // Acciones
        btnAdd.addActionListener(e -> abrirDialogo(null));
        btnEdit.addActionListener(e -> editarSeleccionado());
        btnDelete.addActionListener(e -> eliminarSeleccionado());
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<EsquemaPintura> esquemas = esquemaService.obtenerTodosLosEsquemas();
        
        Optional<Idioma> idiomaPrincipalOpt = idiomaService.obtenerIdiomaPrincipal();
        int idIdiomaPrincipal = idiomaPrincipalOpt.map(Idioma::getId).orElse(-1);

        for (EsquemaPintura esquema : esquemas) {
            String nombreMostrado = esquema.getNombre();
            
            // Intentar mostrar la descripción traducida en lugar del nombre genérico si existe
            if (idIdiomaPrincipal != -1) {
                for (EsquemaPinturaTr tr : esquema.getTraducciones()) {
                    if (tr.getIdIdioma() == idIdiomaPrincipal && tr.getDescripcion() != null && !tr.getDescripcion().isEmpty()) {
                        nombreMostrado = tr.getDescripcion();
                        break;
                    }
                }
            }

            // Obtener nombres de país y operadora (esto podría optimizarse con un mapa o cache)
            String nombrePais = paisService.obtenerPaisPorId(esquema.getIdPais()).map(Pais::getCodigo).orElse("Desconocido");
            String nombreOperadora = operadoraService.obtenerOperadoraPorId(esquema.getIdOperadora()).map(Operadora::getCodigo).orElse("Desconocida");

            tableModel.addRow(new Object[]{
                    esquema.getIdEsquemaPintura(),
                    nombrePais,
                    nombreOperadora,
                    nombreMostrado,
                    esquema.getAnioInicio(),
                    esquema.getAnioFin()
            });
        }
    }

    private void abrirDialogo(EsquemaPintura esquema) {
        EsquemaPinturaDialog dialog = new EsquemaPinturaDialog((Frame) SwingUtilities.getWindowAncestor(this), esquemaService, paisService, operadoraService, idiomaService, esquema);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        cargarDatos();
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            esquemaService.obtenerEsquemaPorId(id).ifPresent(this::abrirDialogo);
        } else {
            // No mostrar mensaje si no hay selección
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            String nombre = (String) tableModel.getValueAt(modelRow, 3); // Nombre mostrado

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el esquema '" + nombre + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    esquemaService.eliminarEsquema(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar. Verifique que no tenga referencias.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un esquema para borrar.");
        }
    }

    @Override
    public void onLanguageChanged() {
        cargarDatos();
    }
}
