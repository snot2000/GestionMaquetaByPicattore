package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.TipoVehiculoService;
import com.picattore.gestion.application.IdiomaService;
import com.picattore.gestion.domain.TipoVehiculo;
import com.picattore.gestion.domain.TipoVehiculoTr;
import com.picattore.gestion.domain.Idioma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

public class TipoVehiculoInternalFrame extends JInternalFrame implements LanguageChangeListener {

    private final TipoVehiculoService tipoVehiculoService;
    private final IdiomaService idiomaService;
    private JTable table;
    private DefaultTableModel tableModel;

    public TipoVehiculoInternalFrame(TipoVehiculoService tipoVehiculoService, IdiomaService idiomaService) {
        super("Gestión de Tipos de Vehículo", true, true, true, true);
        this.tipoVehiculoService = tipoVehiculoService;
        this.idiomaService = idiomaService;
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Tabla
        String[] columnNames = {"ID", "Código", "Nombre", "Descripción"};
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
        List<TipoVehiculo> tipos = tipoVehiculoService.obtenerTodosLosTiposVehiculo();
        
        // Obtener el idioma principal para mostrar las traducciones correspondientes
        Optional<Idioma> idiomaPrincipalOpt = idiomaService.obtenerIdiomaPrincipal();
        int idIdiomaPrincipal = idiomaPrincipalOpt.map(Idioma::getId).orElse(-1);

        for (TipoVehiculo tipo : tipos) {
            String nombre = "";
            String descripcion = "";

            if (idIdiomaPrincipal != -1) {
                for (TipoVehiculoTr tr : tipo.getTraducciones()) {
                    if (tr.getIdIdioma() == idIdiomaPrincipal) {
                        nombre = tr.getNombre();
                        descripcion = tr.getDescripcion();
                        break;
                    }
                }
            }

            tableModel.addRow(new Object[]{
                    tipo.getIdTipoVehiculo(), 
                    tipo.getCodigo(),
                    nombre,
                    descripcion
            });
        }
    }

    private void abrirDialogo(TipoVehiculo tipo) {
        TipoVehiculoDialog dialog = new TipoVehiculoDialog((Frame) SwingUtilities.getWindowAncestor(this), tipoVehiculoService, idiomaService, tipo);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        cargarDatos();
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            tipoVehiculoService.obtenerTipoVehiculoPorId(id).ifPresent(this::abrirDialogo);
        } else {
            // No mostrar mensaje si no hay selección (doble clic en vacío)
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            String codigo = (String) tableModel.getValueAt(modelRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el tipo de vehículo '" + codigo + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    tipoVehiculoService.eliminarTipoVehiculo(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar. Verifique que no tenga referencias.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de vehículo para borrar.");
        }
    }

    @Override
    public void onLanguageChanged() {
        cargarDatos();
    }
}
