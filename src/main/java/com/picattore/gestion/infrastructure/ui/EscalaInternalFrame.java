package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.EscalaService;
import com.picattore.gestion.domain.Escala;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EscalaInternalFrame extends JInternalFrame {

    private final EscalaService escalaService;
    private JTable table;
    private DefaultTableModel tableModel;

    public EscalaInternalFrame(EscalaService escalaService) {
        super("Gestión de Escalas", true, true, true, true);
        this.escalaService = escalaService;
        this.setSize(600, 400);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Tabla
        String[] columnNames = {"ID", "Código", "Escala"};
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
        List<Escala> escalas = escalaService.obtenerTodasLasEscalas();
        for (Escala escala : escalas) {
            tableModel.addRow(new Object[]{escala.getIdEscala(), escala.getCodigo(), escala.getEscala()});
        }
    }

    private void abrirDialogo(Escala escala) {
        EscalaDialog dialog = new EscalaDialog((Frame) SwingUtilities.getWindowAncestor(this), escalaService, escala);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        cargarDatos();
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            escalaService.obtenerEscalaPorId(id).ifPresent(this::abrirDialogo);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una escala para editar.");
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String codigo = (String) tableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar la escala '" + codigo + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    escalaService.eliminarEscala(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar. Verifique que no tenga referencias.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una escala para borrar.");
        }
    }
}
