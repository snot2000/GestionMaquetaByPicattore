package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.TipoModeloService;
import com.picattore.gestion.domain.TipoModelo;
import com.picattore.gestion.domain.TipoModeloTr;
import com.picattore.gestion.domain.Idioma;
import com.picattore.gestion.application.IdiomaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TipoModeloDialog extends JDialog {

    private final TipoModeloService tipoModeloService;
    private final IdiomaService idiomaService;
    private final TipoModelo tipoModeloExistente;

    private JTextField txtCodigo;
    private JTable tableTraducciones;
    private DefaultTableModel tableModel;

    public TipoModeloDialog(Frame owner, TipoModeloService tipoModeloService, IdiomaService idiomaService, TipoModelo tipoModeloExistente) {
        super(owner, tipoModeloExistente == null ? "Nuevo Tipo de Modelo" : "Editar Tipo de Modelo", true);
        this.tipoModeloService = tipoModeloService;
        this.idiomaService = idiomaService;
        this.tipoModeloExistente = tipoModeloExistente;

        this.setSize(600, 400);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JPanel panelDatos = new JPanel(new GridLayout(1, 2));
        panelDatos.add(new JLabel("Código:"));
        txtCodigo = new JTextField();
        panelDatos.add(txtCodigo);

        this.add(panelDatos, BorderLayout.NORTH);

        String[] columnNames = {"ID Idioma", "Idioma", "Nombre", "Descripción"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 3;
            }
        };
        tableTraducciones = new JTable(tableModel);
        
        // Ocultar columna ID Idioma
        tableTraducciones.getColumnModel().removeColumn(tableTraducciones.getColumnModel().getColumn(0));

        JScrollPane scrollPane = new JScrollPane(tableTraducciones);
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        this.add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        List<Idioma> idiomas = idiomaService.obtenerTodosLosIdiomas();

        if (tipoModeloExistente != null) {
            txtCodigo.setText(tipoModeloExistente.getCodigo());

            for (Idioma idioma : idiomas) {
                String nombre = "";
                String descripcion = "";
                for (TipoModeloTr tr : tipoModeloExistente.getTraducciones()) {
                    if (tr.getIdIdioma() == idioma.getId()) {
                        nombre = tr.getNombre();
                        descripcion = tr.getDescripcion();
                        break;
                    }
                }
                tableModel.addRow(new Object[]{idioma.getId(), idioma.getNombre(), nombre, descripcion});
            }
        } else {
            for (Idioma idioma : idiomas) {
                tableModel.addRow(new Object[]{idioma.getId(), idioma.getNombre(), "", ""});
            }
        }
    }

    private void guardar() {
        if (tableTraducciones.isEditing()) {
            tableTraducciones.getCellEditor().stopCellEditing();
        }

        String codigo = txtCodigo.getText();

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El código es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<TipoModeloTr> traducciones = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int idIdioma = (int) tableModel.getValueAt(i, 0);
            String nombre = (String) tableModel.getValueAt(i, 2);
            String descripcion = (String) tableModel.getValueAt(i, 3);

            if (nombre != null && !nombre.trim().isEmpty()) {
                traducciones.add(new TipoModeloTr(idIdioma, nombre, descripcion));
            }
        }

        if (tipoModeloExistente == null) {
            tipoModeloService.crearTipoModelo(codigo, traducciones);
        } else {
            tipoModeloService.actualizarTipoModelo(tipoModeloExistente.getIdTipoModelo(), codigo, traducciones);
        }

        dispose();
    }
}
