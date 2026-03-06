package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.TipoVehiculoService;
import com.picattore.gestion.application.IdiomaService;
import com.picattore.gestion.domain.TipoVehiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TipoVehiculoInternalFrame extends JInternalFrame {

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
        String[] columnNames = {"ID", "Código"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
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
        for (TipoVehiculo tipo : tipos) {
            tableModel.addRow(new Object[]{tipo.getIdTipoVehiculo(), tipo.getCodigo()});
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
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            tipoVehiculoService.obtenerTipoVehiculoPorId(id).ifPresent(this::abrirDialogo);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de vehículo para editar.");
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String codigo = (String) tableModel.getValueAt(selectedRow, 1);

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
}
