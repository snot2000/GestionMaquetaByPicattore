package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.TipoVehiculoService;
import com.picattore.gestion.domain.TipoVehiculo;
import com.picattore.gestion.domain.TipoVehiculoTr;
import com.picattore.gestion.domain.Idioma;
import com.picattore.gestion.application.IdiomaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TipoVehiculoDialog extends JDialog {

    private final TipoVehiculoService tipoVehiculoService;
    private final IdiomaService idiomaService;
    private final TipoVehiculo tipoVehiculoExistente;

    private JTextField txtCodigo;
    private JTable tableTraducciones;
    private DefaultTableModel tableModel;

    public TipoVehiculoDialog(Frame owner, TipoVehiculoService tipoVehiculoService, IdiomaService idiomaService, TipoVehiculo tipoVehiculoExistente) {
        super(owner, tipoVehiculoExistente == null ? "Nuevo Tipo de Vehículo" : "Editar Tipo de Vehículo", true);
        this.tipoVehiculoService = tipoVehiculoService;
        this.idiomaService = idiomaService;
        this.tipoVehiculoExistente = tipoVehiculoExistente;

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

        if (tipoVehiculoExistente != null) {
            txtCodigo.setText(tipoVehiculoExistente.getCodigo());

            for (Idioma idioma : idiomas) {
                String nombre = "";
                String descripcion = "";
                for (TipoVehiculoTr tr : tipoVehiculoExistente.getTraducciones()) {
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

        List<TipoVehiculoTr> traducciones = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int idIdioma = (int) tableModel.getValueAt(i, 0);
            String nombre = (String) tableModel.getValueAt(i, 2);
            String descripcion = (String) tableModel.getValueAt(i, 3);

            if (nombre != null && !nombre.trim().isEmpty()) {
                traducciones.add(new TipoVehiculoTr(idIdioma, nombre, descripcion));
            }
        }

        if (tipoVehiculoExistente == null) {
            tipoVehiculoService.crearTipoVehiculo(codigo, traducciones);
        } else {
            tipoVehiculoService.actualizarTipoVehiculo(tipoVehiculoExistente.getIdTipoVehiculo(), codigo, traducciones);
        }

        dispose();
    }
}
