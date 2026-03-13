package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.FabricanteService;
import com.picattore.gestion.application.PaisService;
import com.picattore.gestion.application.IdiomaService;
import com.picattore.gestion.domain.Fabricante;
import com.picattore.gestion.domain.Pais;
import com.picattore.gestion.domain.PaisTr;
import com.picattore.gestion.domain.Idioma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

public class FabricanteInternalFrame extends JInternalFrame implements LanguageChangeListener {

    private final FabricanteService fabricanteService;
    private final PaisService paisService;
    private final IdiomaService idiomaService;
    private JTable table;
    private DefaultTableModel tableModel;

    public FabricanteInternalFrame(FabricanteService fabricanteService, PaisService paisService, IdiomaService idiomaService) {
        super("Gestión de Fabricantes", true, true, true, true);
        this.fabricanteService = fabricanteService;
        this.paisService = paisService;
        this.idiomaService = idiomaService;
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Tabla
        String[] columnNames = {"ID", "Nombre", "País", "Web", "Teléfono", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        
        // Ocultar columna ID
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
        List<Fabricante> fabricantes = fabricanteService.obtenerTodosLosFabricantes();
        
        Optional<Idioma> idiomaPrincipalOpt = idiomaService.obtenerIdiomaPrincipal();
        int idIdiomaPrincipal = idiomaPrincipalOpt.map(Idioma::getId).orElse(-1);

        for (Fabricante fab : fabricantes) {
            String nombrePais = "Desconocido";
            if (fab.getIdPais() != null) {
                Optional<Pais> paisOpt = paisService.obtenerPaisPorId(fab.getIdPais());
                if (paisOpt.isPresent()) {
                    Pais pais = paisOpt.get();
                    nombrePais = pais.getCodigo();
                    if (idIdiomaPrincipal != -1) {
                        for (PaisTr tr : pais.getTraducciones()) {
                            if (tr.getIdIdioma() == idIdiomaPrincipal) {
                                nombrePais = tr.getNombre();
                                break;
                            }
                        }
                    }
                }
            }

            tableModel.addRow(new Object[]{
                    fab.getIdFabricante(),
                    fab.getNombre(),
                    nombrePais,
                    fab.getPaginaWeb(),
                    fab.getTelefono(),
                    fab.getEmail()
            });
        }
    }

    private void abrirDialogo(Fabricante fabricante) {
        FabricanteDialog dialog = new FabricanteDialog((Frame) SwingUtilities.getWindowAncestor(this), fabricanteService, paisService, idiomaService, fabricante);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        cargarDatos();
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            fabricanteService.obtenerFabricantePorId(id).ifPresent(this::abrirDialogo);
        } else {
            // No mostrar mensaje si no hay selección
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            String nombre = (String) tableModel.getValueAt(modelRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el fabricante '" + nombre + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    fabricanteService.eliminarFabricante(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar. Verifique que no tenga referencias.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un fabricante para borrar.");
        }
    }

    @Override
    public void onLanguageChanged() {
        cargarDatos();
    }
}
