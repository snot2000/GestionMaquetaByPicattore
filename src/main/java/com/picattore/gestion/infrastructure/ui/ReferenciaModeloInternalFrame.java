package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.*;
import com.picattore.gestion.domain.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

public class ReferenciaModeloInternalFrame extends JInternalFrame implements LanguageChangeListener {

    private final ReferenciaModeloService referenciaService;
    private final FabricanteService fabricanteService;
    private final VehiculoRealService vehiculoRealService;
    private final EscalaService escalaService;
    private final TipoVehiculoService tipoVehiculoService;
    private final PaisService paisService;
    private final EpocaService epocaService;
    private final EsquemaPinturaService esquemaService;
    private final OperadoraService operadoraService;
    private final IdiomaService idiomaService;

    private JTable table;
    private DefaultTableModel tableModel;

    public ReferenciaModeloInternalFrame(ReferenciaModeloService referenciaService, FabricanteService fabricanteService, VehiculoRealService vehiculoRealService, EscalaService escalaService, TipoVehiculoService tipoVehiculoService, PaisService paisService, EpocaService epocaService, EsquemaPinturaService esquemaService, OperadoraService operadoraService, IdiomaService idiomaService) {
        super("Gestión de Referencias de Modelos", true, true, true, true);
        this.referenciaService = referenciaService;
        this.fabricanteService = fabricanteService;
        this.vehiculoRealService = vehiculoRealService;
        this.escalaService = escalaService;
        this.tipoVehiculoService = tipoVehiculoService;
        this.paisService = paisService;
        this.epocaService = epocaService;
        this.esquemaService = esquemaService;
        this.operadoraService = operadoraService;
        this.idiomaService = idiomaService;
        
        this.setSize(1000, 600);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        String[] columnNames = {"ID", "Fabricante", "Referencia", "Vehículo Real", "Escala", "F. Salida", "F. Descontinuado"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0));

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
        List<ReferenciaModelo> referencias = referenciaService.obtenerTodasLasReferencias();

        for (ReferenciaModelo ref : referencias) {
            String nombreFabricante = "N/A";
            if (ref.getIdFabricante() != null) {
                nombreFabricante = fabricanteService.obtenerFabricantePorId(ref.getIdFabricante()).map(Fabricante::getNombre).orElse("Desconocido");
            }

            String nombreEscala = "N/A";
            if (ref.getIdEscala() != null) {
                nombreEscala = escalaService.obtenerEscalaPorId(ref.getIdEscala()).map(e -> e.getCodigo() + " (" + e.getEscala() + ")").orElse("Desconocida");
            }

            String nombreVr = "N/A";
            if (ref.getIdVehiculoReal() != null) {
                nombreVr = vehiculoRealService.obtenerVehiculoRealPorId(ref.getIdVehiculoReal())
                        .map(vr -> vr.getNumeracion() + " (" + vr.getNombre() + ")")
                        .orElse("Desconocido");
            }

            tableModel.addRow(new Object[]{
                    ref.getId(),
                    nombreFabricante,
                    ref.getReferencia(),
                    nombreVr,
                    nombreEscala,
                    ref.getFechaSalida(),
                    ref.getFechaDescontinuado()
            });
        }
    }

    private void abrirDialogo(ReferenciaModelo referencia) {
        ReferenciaModeloDialog dialog = new ReferenciaModeloDialog((Frame) SwingUtilities.getWindowAncestor(this), referenciaService, fabricanteService, vehiculoRealService, escalaService, tipoVehiculoService, paisService, epocaService, esquemaService, operadoraService, idiomaService, referencia);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        cargarDatos();
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            referenciaService.obtenerReferenciaPorId(id).ifPresent(this::abrirDialogo);
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            String referencia = (String) tableModel.getValueAt(modelRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar la referencia '" + referencia + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    referenciaService.eliminarReferencia(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar. Verifique que no tenga referencias.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una referencia para borrar.");
        }
    }

    @Override
    public void onLanguageChanged() {
        cargarDatos();
    }
}
