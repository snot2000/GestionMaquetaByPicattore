package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.DecoderService;
import com.picattore.gestion.application.FabricanteService;
import com.picattore.gestion.domain.DecoCV;
import com.picattore.gestion.domain.DecoFuncion;
import com.picattore.gestion.domain.Decoder;
import com.picattore.gestion.domain.Fabricante;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DecoderDialog extends JDialog {

    private final DecoderService decoderService;
    private final FabricanteService fabricanteService;
    private final Decoder decoderExistente;

    private JComboBox<Fabricante> comboFabricante;
    private JTextField txtDireccion;
    private JCheckBox chkCompCarga;
    private JCheckBox chkSonido;
    private JTextField txtTipoConector;

    private JTable tableCvs;
    private DefaultTableModel modelCvs;
    
    private JTable tableFunciones;
    private DefaultTableModel modelFunciones;

    public DecoderDialog(Frame owner, DecoderService decoderService, FabricanteService fabricanteService, Decoder decoderExistente) {
        super(owner, decoderExistente == null ? "Nuevo Decoder" : "Editar Decoder", true);
        this.decoderService = decoderService;
        this.fabricanteService = fabricanteService;
        this.decoderExistente = decoderExistente;

        this.setSize(800, 600);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Datos Principales ---
        JPanel panelDatos = new JPanel(new GridLayout(0, 2, 5, 5));
        panelDatos.setBorder(new TitledBorder("Datos del Decoder"));

        panelDatos.add(new JLabel("Fabricante:"));
        comboFabricante = new JComboBox<>();
        panelDatos.add(comboFabricante);

        panelDatos.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        panelDatos.add(txtDireccion);

        panelDatos.add(new JLabel("Compensador de Carga:"));
        chkCompCarga = new JCheckBox();
        panelDatos.add(chkCompCarga);

        panelDatos.add(new JLabel("Sonido:"));
        chkSonido = new JCheckBox();
        panelDatos.add(chkSonido);

        panelDatos.add(new JLabel("Tipo Conector:"));
        txtTipoConector = new JTextField();
        panelDatos.add(txtTipoConector);

        mainPanel.add(panelDatos);
        mainPanel.add(Box.createVerticalStrut(10));

        // --- Panel Listas ---
        JPanel panelListas = new JPanel(new GridLayout(1, 2, 10, 0));

        // Tabla CVs
        JPanel panelCvs = new JPanel(new BorderLayout());
        panelCvs.setBorder(new TitledBorder("CVs"));
        
        String[] colsCv = {"CV", "Dato"};
        modelCvs = new DefaultTableModel(colsCv, 0);
        tableCvs = new JTable(modelCvs);
        panelCvs.add(new JScrollPane(tableCvs), BorderLayout.CENTER);
        
        JPanel btnPanelCvs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddCv = new JButton("+");
        JButton btnDelCv = new JButton("-");
        btnAddCv.addActionListener(e -> modelCvs.addRow(new Object[]{"", ""}));
        btnDelCv.addActionListener(e -> {
            int row = tableCvs.getSelectedRow();
            if (row >= 0) modelCvs.removeRow(row);
        });
        btnPanelCvs.add(btnAddCv);
        btnPanelCvs.add(btnDelCv);
        panelCvs.add(btnPanelCvs, BorderLayout.SOUTH);

        panelListas.add(panelCvs);

        // Tabla Funciones
        JPanel panelFuncs = new JPanel(new BorderLayout());
        panelFuncs.setBorder(new TitledBorder("Funciones"));
        
        String[] colsFunc = {"Función", "Tipo (on/off, switch)", "Descripción"};
        modelFunciones = new DefaultTableModel(colsFunc, 0);
        tableFunciones = new JTable(modelFunciones);
        
        // ComboBox para el tipo de función en la tabla
        JComboBox<String> comboTipoFunc = new JComboBox<>(new String[]{"on/off", "switch"});
        tableFunciones.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboTipoFunc));

        panelFuncs.add(new JScrollPane(tableFunciones), BorderLayout.CENTER);
        
        JPanel btnPanelFunc = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddFunc = new JButton("+");
        JButton btnDelFunc = new JButton("-");
        btnAddFunc.addActionListener(e -> modelFunciones.addRow(new Object[]{"", "on/off", ""}));
        btnDelFunc.addActionListener(e -> {
            int row = tableFunciones.getSelectedRow();
            if (row >= 0) modelFunciones.removeRow(row);
        });
        btnPanelFunc.add(btnAddFunc);
        btnPanelFunc.add(btnDelFunc);
        panelFuncs.add(btnPanelFunc, BorderLayout.SOUTH);

        panelListas.add(panelFuncs);

        mainPanel.add(panelListas);

        this.add(mainPanel, BorderLayout.CENTER);

        // --- Botones ---
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
        comboFabricante.setModel(new DefaultComboBoxModel<>(fabricanteService.obtenerTodosLosFabricantes().toArray(new Fabricante[0])));
        comboFabricante.setSelectedIndex(-1);

        if (decoderExistente != null) {
            txtDireccion.setText(decoderExistente.getDireccion());
            chkCompCarga.setSelected(decoderExistente.isCompCarga());
            chkSonido.setSelected(decoderExistente.isSonido());
            txtTipoConector.setText(decoderExistente.getTipoConector());

            if (decoderExistente.getIdFabricante() != null) {
                for (int i = 0; i < comboFabricante.getItemCount(); i++) {
                    if (comboFabricante.getItemAt(i).getIdFabricante() == decoderExistente.getIdFabricante()) {
                        comboFabricante.setSelectedIndex(i);
                        break;
                    }
                }
            }

            for (DecoCV cv : decoderExistente.getCvs()) {
                modelCvs.addRow(new Object[]{cv.getCv(), cv.getDato()});
            }

            for (DecoFuncion f : decoderExistente.getFunciones()) {
                modelFunciones.addRow(new Object[]{f.getFuncion(), f.getTipoFuncion(), f.getDescripcion()});
            }
        }
    }

    private void guardar() {
        if (tableCvs.isEditing()) tableCvs.getCellEditor().stopCellEditing();
        if (tableFunciones.isEditing()) tableFunciones.getCellEditor().stopCellEditing();

        Fabricante fabricante = (Fabricante) comboFabricante.getSelectedItem();
        Integer idFabricante = fabricante != null ? fabricante.getIdFabricante() : null;

        List<DecoCV> cvs = new ArrayList<>();
        for (int i = 0; i < modelCvs.getRowCount(); i++) {
            String cv = (String) modelCvs.getValueAt(i, 0);
            String dato = (String) modelCvs.getValueAt(i, 1);
            if (cv != null && !cv.trim().isEmpty()) {
                cvs.add(new DecoCV(cv, dato));
            }
        }

        List<DecoFuncion> funciones = new ArrayList<>();
        for (int i = 0; i < modelFunciones.getRowCount(); i++) {
            String funcion = (String) modelFunciones.getValueAt(i, 0);
            String tipo = (String) modelFunciones.getValueAt(i, 1);
            String desc = (String) modelFunciones.getValueAt(i, 2);
            if (funcion != null && !funcion.trim().isEmpty()) {
                funciones.add(new DecoFuncion(funcion, tipo, desc));
            }
        }

        if (decoderExistente == null) {
            decoderService.crearDecoder(idFabricante, txtDireccion.getText(), chkCompCarga.isSelected(), chkSonido.isSelected(), txtTipoConector.getText(), cvs, funciones);
        } else {
            decoderService.actualizarDecoder(decoderExistente.getId(), idFabricante, txtDireccion.getText(), chkCompCarga.isSelected(), chkSonido.isSelected(), txtTipoConector.getText(), cvs, funciones);
        }
        dispose();
    }
}
