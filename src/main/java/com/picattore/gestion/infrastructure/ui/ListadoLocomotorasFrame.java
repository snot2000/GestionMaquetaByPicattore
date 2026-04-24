package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.*;
import com.picattore.gestion.domain.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListadoLocomotorasFrame extends JInternalFrame implements LanguageChangeListener {

    private final ModeloService modeloService;
    private final DecoderService decoderService;
    private final ReferenciaModeloService referenciaModeloService;
    private final DuenoService duenoService;
    private final FabricanteService fabricanteService;
    private final VehiculoRealService vehiculoRealService;
    private final TipoVehiculoService tipoVehiculoService;
    private final PaisService paisService;
    private final EpocaService epocaService;
    private final EsquemaPinturaService esquemaService;
    private final OperadoraService operadoraService;
    private final IdiomaService idiomaService;

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public ListadoLocomotorasFrame(ModeloService modeloService, DecoderService decoderService, ReferenciaModeloService referenciaModeloService, DuenoService duenoService, FabricanteService fabricanteService, VehiculoRealService vehiculoRealService, TipoVehiculoService tipoVehiculoService, PaisService paisService, EpocaService epocaService, EsquemaPinturaService esquemaService, OperadoraService operadoraService, IdiomaService idiomaService) {
        super("Listado de Locomotoras por CV", true, true, true, true);
        this.modeloService = modeloService;
        this.decoderService = decoderService;
        this.referenciaModeloService = referenciaModeloService;
        this.duenoService = duenoService;
        this.fabricanteService = fabricanteService;
        this.vehiculoRealService = vehiculoRealService;
        this.tipoVehiculoService = tipoVehiculoService;
        this.paisService = paisService;
        this.epocaService = epocaService;
        this.esquemaService = esquemaService;
        this.operadoraService = operadoraService;
        this.idiomaService = idiomaService;

        this.setSize(1200, 700);
        this.setLayout(new BorderLayout());

        try {
            this.setMaximum(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        String[] columnNames = {"Dir. Decoder", "Dueño", "Ref. Fabricante", "Referencia", "Tipo", "Numeración", "Apodo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) { // Dir. Decoder
                    return Integer.class;
                }
                return String.class;
            }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Ordenar por defecto por "Dir. Decoder" ascendente
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);
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

            Integer dirDecoder = null;
            if (m.getIdDecoder() != null) {
                Optional<Decoder> decOpt = decoderService.obtenerDecoderPorId(m.getIdDecoder());
                if(decOpt.isPresent()) {
                    try {
                        dirDecoder = Integer.parseInt(decOpt.get().getDireccion());
                    } catch (NumberFormatException e) {
                        // Ignorar si no es numérico
                    }
                }
            }

            String nombreFab = "N/A";
            String referencia = "N/A";
            String nombreTipo = "N/A";
            String apodo = "N/A";
            String numeracion = "N/A";

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
                            apodo = vr.getApodo() != null ? vr.getApodo() : "N/A";
                            numeracion = vr.getNumeracion() != null ? vr.getNumeracion() : "N/A";

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
                        }
                    }
                }
            }

            tableModel.addRow(new Object[]{
                    dirDecoder,
                    nombreDueno,
                    nombreFab,
                    referencia,
                    nombreTipo,
                    numeracion,
                    apodo
            });
        }
    }

    @Override
    public void onLanguageChanged() {
        cargarDatos();
    }
}
