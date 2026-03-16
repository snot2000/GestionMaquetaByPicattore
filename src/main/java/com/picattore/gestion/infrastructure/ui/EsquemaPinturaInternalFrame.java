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
        this.setSize(1000, 600); // Aumento el ancho para ver mejor los datos
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
            // Nombre del país traducido
            String nombrePais = "Desconocido";
            Optional<Pais> paisOpt = paisService.obtenerPaisPorId(esquema.getIdPais());
            if (paisOpt.isPresent()) {
                Pais pais = paisOpt.get();
                nombrePais = pais.getCodigo(); // Fallback
                if (idIdiomaPrincipal != -1) {
                    for (PaisTr tr : pais.getTraducciones()) {
                        if (tr.getIdIdioma() == idIdiomaPrincipal) {
                            nombrePais = tr.getNombre();
                            break;
                        }
                    }
                }
            }

            // Nombre de la operadora (código por defecto)
            String nombreOperadora = operadoraService.obtenerOperadoraPorId(esquema.getIdOperadora()).map(Operadora::getCodigo).orElse("Desconocida");

            // Nombre del esquema (campo nombre, NO descripción)
            String nombreEsquema = esquema.getNombre();

            tableModel.addRow(new Object[]{
                    esquema.getIdEsquemaPintura(),
                    nombrePais,
                    nombreOperadora,
                    nombreEsquema,
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
            String nombre = (String) tableModel.getValueAt(modelRow, 3);

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
