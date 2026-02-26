package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.IdiomaService;
import com.picattore.gestion.domain.Idioma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IdiomaInternalFrame extends JInternalFrame {

    private final IdiomaService idiomaService;
    private JTable table;
    private DefaultTableModel tableModel;

    public IdiomaInternalFrame(IdiomaService idiomaService) {
        super("Gestión de Idiomas", true, true, true, true);
        this.idiomaService = idiomaService;
        this.setSize(600, 400);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Tabla
        String[] columnNames = {"ID", "Código", "Nombre"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacemos que la tabla no sea editable directamente
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
        btnAdd.addActionListener(e -> abrirDialogoGuardar(null));
        btnEdit.addActionListener(e -> editarSeleccionado());
        btnDelete.addActionListener(e -> eliminarSeleccionado());
    }

    private void cargarDatos() {
        tableModel.setRowCount(0); // Limpiar tabla
        List<Idioma> idiomas = idiomaService.obtenerTodosLosIdiomas();
        for (Idioma idioma : idiomas) {
            tableModel.addRow(new Object[]{idioma.getId(), idioma.getCodigo(), idioma.getNombre()});
        }
    }

    private void abrirDialogoGuardar(Idioma idiomaExistente) {
        JTextField txtCodigo = new JTextField();
        JTextField txtNombre = new JTextField();

        if (idiomaExistente != null) {
            txtCodigo.setText(idiomaExistente.getCodigo());
            txtNombre.setText(idiomaExistente.getNombre());
        }

        Object[] message = {
                "Código:", txtCodigo,
                "Nombre:", txtNombre
        };

        int option = JOptionPane.showConfirmDialog(this, message,
                idiomaExistente == null ? "Nuevo Idioma" : "Editar Idioma",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String codigo = txtCodigo.getText();
            String nombre = txtNombre.getText();

            if (codigo.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El código y el nombre son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (idiomaExistente == null) {
                idiomaService.crearIdioma(codigo, nombre);
            } else {
                idiomaService.actualizarIdioma(idiomaExistente.getId(), codigo, nombre);
            }
            cargarDatos();
        }
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String codigo = (String) tableModel.getValueAt(selectedRow, 1);
            String nombre = (String) tableModel.getValueAt(selectedRow, 2);

            Idioma idioma = new Idioma(id, codigo, nombre);
            abrirDialogoGuardar(idioma);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un idioma para editar.");
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nombre = (String) tableModel.getValueAt(selectedRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el idioma '" + nombre + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    idiomaService.eliminarIdioma(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar. Verifique que no tenga referencias.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un idioma para borrar.");
        }
    }
}
