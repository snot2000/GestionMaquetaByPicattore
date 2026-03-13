package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.TipoModeloService;
import com.picattore.gestion.application.IdiomaService;
import com.picattore.gestion.domain.TipoModelo;
import com.picattore.gestion.domain.TipoModeloTr;
import com.picattore.gestion.domain.Idioma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

public class TipoModeloInternalFrame extends JInternalFrame implements LanguageChangeListener {

    private final TipoModeloService tipoModeloService;
    private final IdiomaService idiomaService;
    private JTable table;
    private DefaultTableModel tableModel;

    public TipoModeloInternalFrame(TipoModeloService tipoModeloService, IdiomaService idiomaService) {
        super("Gestión de Tipos de Modelo", true, true, true, true);
        this.tipoModeloService = tipoModeloService;
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
        List<TipoModelo> tipos = tipoModeloService.obtenerTodosLosTiposModelo();
        
        Optional<Idioma> idiomaPrincipalOpt = idiomaService.obtenerIdiomaPrincipal();
        int idIdiomaPrincipal = idiomaPrincipalOpt.map(Idioma::getId).orElse(-1);

        for (TipoModelo tipo : tipos) {
            String nombre = "";
            String descripcion = "";

            if (idIdiomaPrincipal != -1) {
                for (TipoModeloTr tr : tipo.getTraducciones()) {
                    if (tr.getIdIdioma() == idIdiomaPrincipal) {
                        nombre = tr.getNombre();
                        descripcion = tr.getDescripcion();
                        break;
                    }
                }
            }

            tableModel.addRow(new Object[]{
                    tipo.getIdTipoModelo(), 
                    tipo.getCodigo(),
                    nombre,
                    descripcion
            });
        }
    }

    private void abrirDialogo(TipoModelo tipo) {
        TipoModeloDialog dialog = new TipoModeloDialog((Frame) SwingUtilities.getWindowAncestor(this), tipoModeloService, idiomaService, tipo);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        cargarDatos();
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            tipoModeloService.obtenerTipoModeloPorId(id).ifPresent(this::abrirDialogo);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de modelo para editar.");
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            String codigo = (String) tableModel.getValueAt(modelRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el tipo de modelo '" + codigo + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    tipoModeloService.eliminarTipoModelo(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar. Verifique que no tenga referencias.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de modelo para borrar.");
        }
    }

    @Override
    public void onLanguageChanged() {
        cargarDatos();
    }
}
