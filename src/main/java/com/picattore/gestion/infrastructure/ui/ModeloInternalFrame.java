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

public class ModeloInternalFrame extends JInternalFrame implements LanguageChangeListener {

    private final ModeloService modeloService;
    private final DecoderService decoderService;
    private final ReferenciaModeloService referenciaModeloService;
    private final DuenoService duenoService;
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

    public ModeloInternalFrame(ModeloService modeloService, DecoderService decoderService, ReferenciaModeloService referenciaModeloService, DuenoService duenoService, FabricanteService fabricanteService, VehiculoRealService vehiculoRealService, EscalaService escalaService, TipoVehiculoService tipoVehiculoService, PaisService paisService, EpocaService epocaService, EsquemaPinturaService esquemaService, OperadoraService operadoraService, IdiomaService idiomaService) {
        super("Gestión de Modelos", true, true, true, true);
        this.modeloService = modeloService;
        this.decoderService = decoderService;
        this.referenciaModeloService = referenciaModeloService;
        this.duenoService = duenoService;
        this.fabricanteService = fabricanteService;
        this.vehiculoRealService = vehiculoRealService;
        this.escalaService = escalaService;
        this.tipoVehiculoService = tipoVehiculoService;
        this.paisService = paisService;
        this.epocaService = epocaService;
        this.esquemaService = esquemaService;
        this.operadoraService = operadoraService;
        this.idiomaService = idiomaService;

        this.setSize(1200, 700); // Pantalla grande
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        String[] columnNames = {"ID", "Dueño", "Dir. Decoder", "Ref. Fabricante", "Referencia", "Tipo", "Época", "Esquema", "Operadora"};
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
        List<Modelo> modelos = modeloService.obtenerTodosLosModelos();

        Optional<Idioma> idiomaPrincipalOpt = idiomaService.obtenerIdiomaPrincipal();
        int idIdiomaPrincipal = idiomaPrincipalOpt.map(Idioma::getId).orElse(-1);

        for (Modelo m : modelos) {
            String nombreDueno = "N/A";
            if (m.getIdDueno() != null) {
                nombreDueno = duenoService.obtenerDuenoPorId(m.getIdDueno()).map(Dueno::getNombre).orElse("Desconocido");
            }

            String dirDecoder = "N/A";
            if (m.getIdDecoder() != null) {
                dirDecoder = decoderService.obtenerDecoderPorId(m.getIdDecoder()).map(Decoder::getDireccion).orElse("Desconocida");
            }

            String nombreFab = "N/A";
            String referencia = "N/A";
            String nombreTipo = "N/A";
            String nombreEpoca = "N/A";
            String nombreEsquema = "N/A";
            String nombreOperadora = "N/A";

            if (m.getIdReferenciaModelo() != null) {
                Optional<ReferenciaModelo> refOpt = referenciaModeloService.obtenerReferenciaPorId(m.getIdReferenciaModelo());
                if (refOpt.isPresent()) {
                    ReferenciaModelo ref = refOpt.get();
                    referencia = ref.getReferencia();
                    
                    if (ref.getIdFabricante() != null) {
                        nombreFab = fabricanteService.obtenerFabricantePorId(ref.getIdFabricante()).map(Fabricante::getNombre).orElse("Desconocido");
                    }

                    if (ref.getIdVehiculoReal() != null) {
                        Optional<VehiculoReal> vrOpt = vehiculoRealService.obtenerVehiculoRealPorId(ref.getIdVehiculoReal());
                        if (vrOpt.isPresent()) {
                            VehiculoReal vr = vrOpt.get();

                            if (vr.getIdTipoVehiculo() != null) {
                                Optional<TipoVehiculo> tipoOpt = tipoVehiculoService.obtenerTipoVehiculoPorId(vr.getIdTipoVehiculo());
                                if (tipoOpt.isPresent()) {
                                    nombreTipo = tipoOpt.get().getCodigo();
                                    if (idIdiomaPrincipal != -1) {
                                        for (TipoVehiculoTr tr : tipoOpt.get().getTraducciones()) {
                                            if (tr.getIdIdioma() == idIdiomaPrincipal) { nombreTipo = tr.getNombre(); break; }
                                        }
                                    }
                                }
                            }

                            if (vr.getIdEpoca() != null) {
                                Optional<Epoca> epocaOpt = epocaService.obtenerEpocaPorId(vr.getIdEpoca());
                                if (epocaOpt.isPresent()) {
                                    nombreEpoca = epocaOpt.get().getCodigo();
                                    if (idIdiomaPrincipal != -1) {
                                        for (EpocaTr tr : epocaOpt.get().getTraducciones()) {
                                            if (tr.getIdIdioma() == idIdiomaPrincipal) { nombreEpoca = tr.getNombre(); break; }
                                        }
                                    }
                                }
                            }

                            if (vr.getIdEsquemaPintura() != null) {
                                nombreEsquema = esquemaService.obtenerEsquemaPorId(vr.getIdEsquemaPintura()).map(EsquemaPintura::getNombre).orElse("Desconocido");
                            }

                            if (vr.getIdOperadora() != null) {
                                nombreOperadora = operadoraService.obtenerOperadoraPorId(vr.getIdOperadora()).map(Operadora::getCodigo).orElse("Desconocida");
                            }
                        }
                    }
                }
            }

            tableModel.addRow(new Object[]{
                    m.getId(),
                    nombreDueno,
                    dirDecoder,
                    nombreFab,
                    referencia,
                    nombreTipo,
                    nombreEpoca,
                    nombreEsquema,
                    nombreOperadora
            });
        }
    }

    private void abrirDialogo(Modelo modelo) {
        ModeloDialog dialog = new ModeloDialog((Frame) SwingUtilities.getWindowAncestor(this), modeloService, decoderService, referenciaModeloService, duenoService, fabricanteService, vehiculoRealService, escalaService, tipoVehiculoService, paisService, epocaService, esquemaService, operadoraService, idiomaService, modelo);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        cargarDatos();
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            modeloService.obtenerModeloPorId(id).ifPresent(this::abrirDialogo);
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar este modelo?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    modeloService.eliminarModelo(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un modelo para borrar.");
        }
    }

    @Override
    public void onLanguageChanged() {
        cargarDatos();
    }
}
