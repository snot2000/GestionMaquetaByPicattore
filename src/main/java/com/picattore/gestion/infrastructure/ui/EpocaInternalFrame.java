package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.EpocaService;
import com.picattore.gestion.application.IdiomaService;
import com.picattore.gestion.domain.Epoca;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EpocaInternalFrame extends JInternalFrame {

    private final EpocaService epocaService;
    private final IdiomaService idiomaService;
    private JTable table;
    private DefaultTableModel tableModel;

    public EpocaInternalFrame(EpocaService epocaService, IdiomaService idiomaService) {
        super("Gestión de Épocas", true, true, true, true);
        this.epocaService = epocaService;
        this.idiomaService = idiomaService;
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Tabla
        String[] columnNames = {"ID", "Código", "Año Inicio", "Año Fin"};
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
        List<Epoca> epocas = epocaService.obtenerTodasLasEpocas();
        for (Epoca epoca : epocas) {
            tableModel.addRow(new Object[]{epoca.getIdEpoca(), epoca.getCodigo(), epoca.getAnioInicio(), epoca.getAnioFin()});
        }
    }

    private void abrirDialogo(Epoca epoca) {
        EpocaDialog dialog = new EpocaDialog((Frame) SwingUtilities.getWindowAncestor(this), epocaService, idiomaService, epoca);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        cargarDatos(); // Recargar después de cerrar el diálogo
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            epocaService.obtenerEpocaPorId(id).ifPresent(this::abrirDialogo);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una época para editar.");
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String codigo = (String) tableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar la época '" + codigo + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    epocaService.eliminarEpoca(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar. Verifique que no tenga referencias.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una época para borrar.");
        }
    }
}
