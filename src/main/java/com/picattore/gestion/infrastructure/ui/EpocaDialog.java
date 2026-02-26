package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.EpocaService;
import com.picattore.gestion.domain.Epoca;
import com.picattore.gestion.domain.EpocaTr;
import com.picattore.gestion.domain.Idioma;
import com.picattore.gestion.application.IdiomaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EpocaDialog extends JDialog {

    private final EpocaService epocaService;
    private final IdiomaService idiomaService;
    private final Epoca epocaExistente;

    private JTextField txtCodigo;
    private JTextField txtAnioInicio;
    private JTextField txtAnioFin;
    private JTable tableTraducciones;
    private DefaultTableModel tableModel;

    public EpocaDialog(Frame owner, EpocaService epocaService, IdiomaService idiomaService, Epoca epocaExistente) {
        super(owner, epocaExistente == null ? "Nueva Época" : "Editar Época", true);
        this.epocaService = epocaService;
        this.idiomaService = idiomaService;
        this.epocaExistente = epocaExistente;

        this.setSize(600, 400);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Panel de datos generales
        JPanel panelDatos = new JPanel(new GridLayout(3, 2));
        panelDatos.add(new JLabel("Código:"));
        txtCodigo = new JTextField();
        panelDatos.add(txtCodigo);

        panelDatos.add(new JLabel("Año Inicio:"));
        txtAnioInicio = new JTextField();
        panelDatos.add(txtAnioInicio);

        panelDatos.add(new JLabel("Año Fin:"));
        txtAnioFin = new JTextField();
        panelDatos.add(txtAnioFin);

        this.add(panelDatos, BorderLayout.NORTH);

        // Tabla de traducciones
        String[] columnNames = {"ID Idioma", "Idioma", "Nombre", "Descripción"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 3; // Solo Nombre y Descripción son editables
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

        if (epocaExistente != null) {
            txtCodigo.setText(epocaExistente.getCodigo());
            txtAnioInicio.setText(String.valueOf(epocaExistente.getAnioInicio()));
            txtAnioFin.setText(String.valueOf(epocaExistente.getAnioFin()));

            for (Idioma idioma : idiomas) {
                String nombre = "";
                String descripcion = "";
                for (EpocaTr tr : epocaExistente.getTraducciones()) {
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
        String codigo = txtCodigo.getText();
        String anioInicioStr = txtAnioInicio.getText();
        String anioFinStr = txtAnioFin.getText();

        if (codigo.isEmpty() || anioInicioStr.isEmpty() || anioFinStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int anioInicio;
        int anioFin;
        try {
            anioInicio = Integer.parseInt(anioInicioStr);
            anioFin = Integer.parseInt(anioFinStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Los años deben ser números enteros.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<EpocaTr> traducciones = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int idIdioma = (int) tableModel.getValueAt(i, 0);
            String nombre = (String) tableModel.getValueAt(i, 2);
            String descripcion = (String) tableModel.getValueAt(i, 3);

            if (!nombre.isEmpty()) { // Solo guardar si tiene nombre
                traducciones.add(new EpocaTr(idIdioma, nombre, descripcion));
            }
        }

        if (epocaExistente == null) {
            epocaService.crearEpoca(codigo, anioInicio, anioFin, traducciones);
        } else {
            epocaService.actualizarEpoca(epocaExistente.getIdEpoca(), codigo, anioInicio, anioFin, traducciones);
        }

        dispose();
    }
}
