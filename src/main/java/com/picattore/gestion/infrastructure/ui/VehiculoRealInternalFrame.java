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

public class VehiculoRealInternalFrame extends JInternalFrame implements LanguageChangeListener {

    private final VehiculoRealService vehiculoRealService;
    private final TipoVehiculoService tipoVehiculoService;
    private final PaisService paisService;
    private final EpocaService epocaService;
    private final EsquemaPinturaService esquemaService;
    private final OperadoraService operadoraService;
    private final IdiomaService idiomaService;

    private JTable table;
    private DefaultTableModel tableModel;

    public VehiculoRealInternalFrame(VehiculoRealService vehiculoRealService, TipoVehiculoService tipoVehiculoService, PaisService paisService, EpocaService epocaService, EsquemaPinturaService esquemaService, OperadoraService operadoraService, IdiomaService idiomaService) {
        super("Gestión de Vehículos Reales", true, true, true, true);
        this.vehiculoRealService = vehiculoRealService;
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
        String[] columnNames = {"ID", "Numeración", "UID", "Tipo Vehículo", "País", "Época", "Esquema Pintura", "Operadora"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
        List<VehiculoReal> vehiculos = vehiculoRealService.obtenerTodosLosVehiculosReales();
        
        Optional<Idioma> idiomaPrincipalOpt = idiomaService.obtenerIdiomaPrincipal();
        int idIdiomaPrincipal = idiomaPrincipalOpt.map(Idioma::getId).orElse(-1);

        for (VehiculoReal v : vehiculos) {
            String nombreTipo = "N/A";
            if (v.getIdTipoVehiculo() != null) {
                Optional<TipoVehiculo> tipoOpt = tipoVehiculoService.obtenerTipoVehiculoPorId(v.getIdTipoVehiculo());
                if (tipoOpt.isPresent()) {
                    nombreTipo = tipoOpt.get().getCodigo();
                    if (idIdiomaPrincipal != -1) {
                        for (TipoVehiculoTr tr : tipoOpt.get().getTraducciones()) {
                            if (tr.getIdIdioma() == idIdiomaPrincipal) {
                                nombreTipo = tr.getNombre();
                                break;
                            }
                        }
                    }
                }
            }

            String nombrePais = "N/A";
            if (v.getIdPais() != null) {
                Optional<Pais> paisOpt = paisService.obtenerPaisPorId(v.getIdPais());
                if (paisOpt.isPresent()) {
                    nombrePais = paisOpt.get().getCodigo();
                    if (idIdiomaPrincipal != -1) {
                        for (PaisTr tr : paisOpt.get().getTraducciones()) {
                            if (tr.getIdIdioma() == idIdiomaPrincipal) {
                                nombrePais = tr.getNombre();
                                break;
                            }
                        }
                    }
                }
            }

            String nombreEpoca = "N/A";
            if (v.getIdEpoca() != null) {
                Optional<Epoca> epocaOpt = epocaService.obtenerEpocaPorId(v.getIdEpoca());
                if (epocaOpt.isPresent()) {
                    nombreEpoca = epocaOpt.get().getCodigo();
                    if (idIdiomaPrincipal != -1) {
                        for (EpocaTr tr : epocaOpt.get().getTraducciones()) {
                            if (tr.getIdIdioma() == idIdiomaPrincipal) {
                                nombreEpoca = tr.getNombre();
                                break;
                            }
                        }
                    }
                }
            }

            String nombreEsquema = "N/A";
            if (v.getIdEsquemaPintura() != null) {
                Optional<EsquemaPintura> esquemaOpt = esquemaService.obtenerEsquemaPorId(v.getIdEsquemaPintura());
                if (esquemaOpt.isPresent()) {
                    nombreEsquema = esquemaOpt.get().getNombre(); // Mostramos el nombre, no la descripción
                }
            }

            String nombreOperadora = "N/A";
            if (v.getIdOperadora() != null) {
                Optional<Operadora> opOpt = operadoraService.obtenerOperadoraPorId(v.getIdOperadora());
                if (opOpt.isPresent()) {
                    nombreOperadora = opOpt.get().getCodigo();
                }
            }

            tableModel.addRow(new Object[]{
                    v.getId(),
                    v.getNumeracion(),
                    v.getUid(),
                    nombreTipo,
                    nombrePais,
                    nombreEpoca,
                    nombreEsquema,
                    nombreOperadora
            });
        }
    }

    private void abrirDialogo(VehiculoReal vehiculo) {
        VehiculoRealDialog dialog = new VehiculoRealDialog((Frame) SwingUtilities.getWindowAncestor(this), vehiculoRealService, tipoVehiculoService, paisService, epocaService, esquemaService, operadoraService, idiomaService, vehiculo);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        cargarDatos();
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            vehiculoRealService.obtenerVehiculoRealPorId(id).ifPresent(this::abrirDialogo);
        } else {
            //
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            String numeracion = (String) tableModel.getValueAt(modelRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el vehículo real '" + numeracion + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    vehiculoRealService.eliminarVehiculoReal(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar. Verifique que no tenga referencias.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo real para borrar.");
        }
    }

    @Override
    public void onLanguageChanged() {
        cargarDatos();
    }
}
