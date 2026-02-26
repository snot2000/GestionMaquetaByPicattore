package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.PaisService;
import com.picattore.gestion.domain.Pais;
import com.picattore.gestion.domain.PaisTr;
import com.picattore.gestion.domain.Idioma;
import com.picattore.gestion.application.IdiomaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PaisDialog extends JDialog {

    private final PaisService paisService;
    private final IdiomaService idiomaService;
    private final Pais paisExistente;

    private JTextField txtCodigo;
    private JTable tableTraducciones;
    private DefaultTableModel tableModel;

    public PaisDialog(Frame owner, PaisService paisService, IdiomaService idiomaService, Pais paisExistente) {
        super(owner, paisExistente == null ? "Nuevo País" : "Editar País", true);
        this.paisService = paisService;
        this.idiomaService = idiomaService;
        this.paisExistente = paisExistente;

        this.setSize(600, 400);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Panel de datos generales
        JPanel panelDatos = new JPanel(new GridLayout(1, 2));
        panelDatos.add(new JLabel("Código:"));
        txtCodigo = new JTextField();
        panelDatos.add(txtCodigo);

        this.add(panelDatos, BorderLayout.NORTH);

        // Tabla de traducciones
        String[] columnNames = {"ID Idioma", "Idioma", "Nombre"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Solo Nombre es editable
            }
        };
        tableTraducciones = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableTraducciones);
        this.add(scrollPane, BorderLayout.CENTER);

        // Botones
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

        if (paisExistente != null) {
            txtCodigo.setText(paisExistente.getCodigo());

            for (Idioma idioma : idiomas) {
                String nombre = "";
                for (PaisTr tr : paisExistente.getTraducciones()) {
                    if (tr.getIdIdioma() == idioma.getId()) {
                        nombre = tr.getNombre();
                        break;
                    }
                }
                tableModel.addRow(new Object[]{idioma.getId(), idioma.getNombre(), nombre});
            }
        } else {
            for (Idioma idioma : idiomas) {
                tableModel.addRow(new Object[]{idioma.getId(), idioma.getNombre(), ""});
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

        List<PaisTr> traducciones = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int idIdioma = (int) tableModel.getValueAt(i, 0);
            String nombre = (String) tableModel.getValueAt(i, 2);

            if (nombre != null && !nombre.trim().isEmpty()) {
                traducciones.add(new PaisTr(idIdioma, nombre));
            }
        }

        if (paisExistente == null) {
            paisService.crearPais(codigo, traducciones);
        } else {
            paisService.actualizarPais(paisExistente.getIdPais(), codigo, traducciones);
        }

        dispose();
    }
}
