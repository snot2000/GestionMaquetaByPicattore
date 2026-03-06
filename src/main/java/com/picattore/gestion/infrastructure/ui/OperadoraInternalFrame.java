package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.OperadoraService;
import com.picattore.gestion.application.PaisService;
import com.picattore.gestion.domain.Operadora;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OperadoraInternalFrame extends JInternalFrame {

    private final OperadoraService operadoraService;
    private final PaisService paisService;
    private JTable table;
    private DefaultTableModel tableModel;

    public OperadoraInternalFrame(OperadoraService operadoraService, PaisService paisService) {
        super("Gestión de Operadoras", true, true, true, true);
        this.operadoraService = operadoraService;
        this.paisService = paisService;
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Tabla
        String[] columnNames = {"ID", "Código", "Nombre", "Año Creación", "Año Disolución"};
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
        List<Operadora> operadoras = operadoraService.obtenerTodasLasOperadoras();
        for (Operadora operadora : operadoras) {
            tableModel.addRow(new Object[]{
                    operadora.getIdOperadora(),
                    operadora.getCodigo(),
                    operadora.getNombre(),
                    operadora.getAnioCreacion(),
                    operadora.getAnioDisolucion()
            });
        }
    }

    private void abrirDialogo(Operadora operadora) {
        OperadoraDialog dialog = new OperadoraDialog((Frame) SwingUtilities.getWindowAncestor(this), operadoraService, paisService, operadora);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        cargarDatos();
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            operadoraService.obtenerOperadoraPorId(id).ifPresent(this::abrirDialogo);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una operadora para editar.");
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nombre = (String) tableModel.getValueAt(selectedRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar la operadora '" + nombre + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    operadoraService.eliminarOperadora(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar. Verifique que no tenga referencias.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una operadora para borrar.");
        }
    }
}
