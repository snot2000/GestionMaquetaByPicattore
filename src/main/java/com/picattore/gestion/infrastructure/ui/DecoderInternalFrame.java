package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.DecoderService;
import com.picattore.gestion.application.FabricanteService;
import com.picattore.gestion.domain.Decoder;
import com.picattore.gestion.domain.Fabricante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DecoderInternalFrame extends JInternalFrame {

    private final DecoderService decoderService;
    private final FabricanteService fabricanteService;

    private JTable table;
    private DefaultTableModel tableModel;

    public DecoderInternalFrame(DecoderService decoderService, FabricanteService fabricanteService) {
        super("Gestión de Decoders", true, true, true, true);
        this.decoderService = decoderService;
        this.fabricanteService = fabricanteService;
        
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        String[] columnNames = {"ID", "Fabricante", "Dirección", "Comp. Carga", "Sonido", "Tipo Conector"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3 || columnIndex == 4) return Boolean.class;
                return super.getColumnClass(columnIndex);
            }
        };
        table = new JTable(tableModel);
        
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0)); // Ocultar ID

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

        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Añadir");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Borrar");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        this.add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> abrirDialogo(null));
        btnEdit.addActionListener(e -> editarSeleccionado());
        btnDelete.addActionListener(e -> eliminarSeleccionado());
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<Decoder> decoders = decoderService.obtenerTodosLosDecoders();

        for (Decoder d : decoders) {
            String nombreFabricante = "N/A";
            if (d.getIdFabricante() != null) {
                nombreFabricante = fabricanteService.obtenerFabricantePorId(d.getIdFabricante())
                        .map(Fabricante::getNombre)
                        .orElse("Desconocido");
            }

            tableModel.addRow(new Object[]{
                    d.getId(),
                    nombreFabricante,
                    d.getDireccion(),
                    d.isCompCarga(),
                    d.isSonido(),
                    d.getTipoConector()
            });
        }
    }

    private void abrirDialogo(Decoder decoder) {
        DecoderDialog dialog = new DecoderDialog((Frame) SwingUtilities.getWindowAncestor(this), decoderService, fabricanteService, decoder);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        cargarDatos();
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            decoderService.obtenerDecoderPorId(id).ifPresent(this::abrirDialogo);
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar este decoder?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    decoderService.eliminarDecoder(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar el decoder.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un decoder para borrar.");
        }
    }
}
