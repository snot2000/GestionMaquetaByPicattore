package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.IdiomaService;
import com.picattore.gestion.domain.Idioma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class IdiomaInternalFrame extends JInternalFrame {

    private final IdiomaService idiomaService;
    private final Runnable onIdiomaChanged;
    private JTable table;
    private DefaultTableModel tableModel;

    // Constructor antiguo para compatibilidad (aunque ya no se usa en MainFrame)
    public IdiomaInternalFrame(IdiomaService idiomaService) {
        this(idiomaService, null);
    }

    public IdiomaInternalFrame(IdiomaService idiomaService, Runnable onIdiomaChanged) {
        super("Gestión de Idiomas", true, true, true, true);
        this.idiomaService = idiomaService;
        this.onIdiomaChanged = onIdiomaChanged;
        this.setSize(600, 400);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Tabla
        String[] columnNames = {"ID", "Código", "Nombre", "Principal"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacemos que la tabla no sea editable directamente
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Boolean.class;
                return super.getColumnClass(columnIndex);
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
        btnAdd.addActionListener(e -> abrirDialogoGuardar(null));
        btnEdit.addActionListener(e -> editarSeleccionado());
        btnDelete.addActionListener(e -> eliminarSeleccionado());
    }

    private void cargarDatos() {
        tableModel.setRowCount(0); // Limpiar tabla
        List<Idioma> idiomas = idiomaService.obtenerTodosLosIdiomas();
        for (Idioma idioma : idiomas) {
            tableModel.addRow(new Object[]{
                    idioma.getId(),
                    idioma.getCodigo(),
                    idioma.getNombre(),
                    idioma.isPrincipal()
            });
        }
    }

    private void abrirDialogoGuardar(Idioma idiomaExistente) {
        JTextField txtCodigo = new JTextField();
        JTextField txtNombre = new JTextField();
        JCheckBox chkPrincipal = new JCheckBox("Es idioma principal");

        if (idiomaExistente != null) {
            txtCodigo.setText(idiomaExistente.getCodigo());
            txtNombre.setText(idiomaExistente.getNombre());
            chkPrincipal.setSelected(idiomaExistente.isPrincipal());
        }

        Object[] message = {
                "Código:", txtCodigo,
                "Nombre:", txtNombre,
                "Principal:", chkPrincipal
        };

        int option = JOptionPane.showConfirmDialog(this, message,
                idiomaExistente == null ? "Nuevo Idioma" : "Editar Idioma",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String codigo = txtCodigo.getText();
            String nombre = txtNombre.getText();
            boolean principal = chkPrincipal.isSelected();

            if (codigo.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El código y el nombre son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (idiomaExistente == null) {
                idiomaService.crearIdioma(codigo, nombre, principal);
            } else {
                idiomaService.actualizarIdioma(idiomaExistente.getId(), codigo, nombre, principal);
            }
            cargarDatos();
            notificarCambio();
        }
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            String codigo = (String) tableModel.getValueAt(modelRow, 1);
            String nombre = (String) tableModel.getValueAt(modelRow, 2);
            boolean principal = (boolean) tableModel.getValueAt(modelRow, 3);

            Idioma idioma = new Idioma(id, codigo, nombre, principal);
            abrirDialogoGuardar(idioma);
        } else {
            // No mostrar mensaje si no hay selección
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            String nombre = (String) tableModel.getValueAt(modelRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el idioma '" + nombre + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    idiomaService.eliminarIdioma(id);
                    cargarDatos();
                    notificarCambio();
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

    private void notificarCambio() {
        if (onIdiomaChanged != null) {
            onIdiomaChanged.run();
        }
    }
}
