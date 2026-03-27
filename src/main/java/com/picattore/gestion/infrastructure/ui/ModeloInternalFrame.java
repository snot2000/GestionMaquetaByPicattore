package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.*;
import com.picattore.gestion.domain.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
    private TableRowSorter<DefaultTableModel> sorter;

    // Filtros
    private JTextField txtApodo, txtReferencia;
    private JComboBox<String> comboTipo, comboPais, comboOperadora, comboFabricante;

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

        this.setSize(1200, 700);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // --- Panel Superior (Filtros Avanzados) ---
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltro.setBorder(BorderFactory.createTitledBorder("Filtros de Búsqueda"));

        panelFiltro.add(new JLabel("Apodo:"));
        txtApodo = new JTextField(15); 
        panelFiltro.add(txtApodo);

        panelFiltro.add(new JLabel("Referencia:"));
        txtReferencia = new JTextField(15); 
        panelFiltro.add(txtReferencia);

        panelFiltro.add(new JLabel("Tipo:"));
        comboTipo = new JComboBox<>();
        comboTipo.setPreferredSize(new Dimension(150, 25)); // +50%
        panelFiltro.add(comboTipo);

        panelFiltro.add(new JLabel("País:"));
        comboPais = new JComboBox<>();
        comboPais.setPreferredSize(new Dimension(150, 25)); // +50%
        panelFiltro.add(comboPais);

        panelFiltro.add(new JLabel("Operadora:"));
        comboOperadora = new JComboBox<>();
        comboOperadora.setPreferredSize(new Dimension(150, 25)); // +50%
        panelFiltro.add(comboOperadora);

        panelFiltro.add(new JLabel("Fabricante:"));
        comboFabricante = new JComboBox<>();
        comboFabricante.setPreferredSize(new Dimension(150, 25)); // +50%
        panelFiltro.add(comboFabricante);

        JButton btnLimpiar = new JButton("Limpiar Filtros");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panelFiltro.add(btnLimpiar);

        this.add(panelFiltro, BorderLayout.NORTH);

        // Listeners para filtros
        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        };
        txtApodo.getDocument().addDocumentListener(docListener);
        txtReferencia.getDocument().addDocumentListener(docListener);
        
        ActionListener comboListener = e -> aplicarFiltros();
        comboTipo.addActionListener(comboListener);
        comboPais.addActionListener(comboListener);
        comboOperadora.addActionListener(comboListener);
        comboFabricante.addActionListener(comboListener);

        // --- Tabla ---
        String[] columnNames = {"ID", "Dir. Decoder", "País", "Apodo", "Numeración", "UID", "Dueño", "Ref. Fabricante", "Referencia", "Tipo", "Época", "Esquema", "Operadora"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                // Para que la ordenación numérica funcione en "Dir. Decoder" (columna 1 visible)
                if (column == 1) {
                    return Integer.class;
                }
                return String.class;
            }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0)); // Ocultar ID

        // Ajustar ancho de "Dir. Decoder" (ahora es la columna 0 en la vista)
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(0).setMaxWidth(120);

        // Ordenar por defecto por "Dir. Decoder" ascendente (Compatible con Java 8+)
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING)); // 1 porque la ordenación se hace en base al modelo
        sorter.setSortKeys(sortKeys);

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

        // --- Panel Inferior (Botones) ---
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

    private void limpiarFiltros() {
        txtApodo.setText("");
        txtReferencia.setText("");
        if (comboTipo.getItemCount() > 0) comboTipo.setSelectedIndex(0);
        if (comboPais.getItemCount() > 0) comboPais.setSelectedIndex(0);
        if (comboOperadora.getItemCount() > 0) comboOperadora.setSelectedIndex(0);
        if (comboFabricante.getItemCount() > 0) comboFabricante.setSelectedIndex(0);
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

        // Filtro Apodo (Columna Modelo: 3)
        if (!txtApodo.getText().trim().isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + txtApodo.getText(), 3));
        }

        // Filtro Referencia (Columna Modelo: 8)
        if (!txtReferencia.getText().trim().isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + txtReferencia.getText(), 8));
        }

        // Filtro Tipo (Columna Modelo: 9)
        if (comboTipo.getSelectedItem() != null && comboTipo.getSelectedIndex() > 0) {
            filters.add(RowFilter.regexFilter("(?i)^" + comboTipo.getSelectedItem().toString() + "$", 9));
        }

        // Filtro País (Columna Modelo: 2)
        if (comboPais.getSelectedItem() != null && comboPais.getSelectedIndex() > 0) {
            filters.add(RowFilter.regexFilter("(?i)^" + comboPais.getSelectedItem().toString() + "$", 2));
        }

        // Filtro Operadora (Columna Modelo: 12)
        if (comboOperadora.getSelectedItem() != null && comboOperadora.getSelectedIndex() > 0) {
            filters.add(RowFilter.regexFilter("(?i)^" + comboOperadora.getSelectedItem().toString() + "$", 12));
        }

        // Filtro Fabricante (Columna Modelo: 7)
        if (comboFabricante.getSelectedItem() != null && comboFabricante.getSelectedIndex() > 0) {
            filters.add(RowFilter.regexFilter("(?i)^" + comboFabricante.getSelectedItem().toString() + "$", 7));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<Modelo> modelos = modeloService.obtenerTodosLosModelos();

        Optional<Idioma> idiomaPrincipalOpt = idiomaService.obtenerIdiomaPrincipal();
        int idIdiomaPrincipal = idiomaPrincipalOpt.map(Idioma::getId).orElse(-1);

        // Sets for ComboBoxes (to avoid duplicates)
        java.util.Set<String> tiposSet = new java.util.TreeSet<>();
        java.util.Set<String> paisesSet = new java.util.TreeSet<>();
        java.util.Set<String> operadorasSet = new java.util.TreeSet<>();
        java.util.Set<String> fabricantesSet = new java.util.TreeSet<>();

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
                        // Ignore
                    }
                }
            }

            String nombreFab = "N/A";
            String referencia = "N/A";
            String nombreTipo = "N/A";
            String nombreEpoca = "N/A";
            String nombreEsquema = "N/A";
            String nombreOperadora = "N/A";
            String apodo = "N/A";
            String numeracion = "N/A";
            String uid = "N/A";
            String nombrePais = "N/A";

            if (m.getIdReferenciaModelo() != null) {
                Optional<ReferenciaModelo> refOpt = referenciaModeloService.obtenerReferenciaPorId(m.getIdReferenciaModelo());
                if (refOpt.isPresent()) {
                    ReferenciaModelo ref = refOpt.get();
                    referencia = ref.getReferencia();
                    
                    if (ref.getIdFabricante() != null) {
                        nombreFab = fabricanteService.obtenerFabricantePorId(ref.getIdFabricante()).map(Fabricante::getNombre).orElse("Desconocido");
                        if (!nombreFab.equals("Desconocido")) fabricantesSet.add(nombreFab);
                    }

                    if (ref.getIdVehiculoReal() != null) {
                        Optional<VehiculoReal> vrOpt = vehiculoRealService.obtenerVehiculoRealPorId(ref.getIdVehiculoReal());
                        if (vrOpt.isPresent()) {
                            VehiculoReal vr = vrOpt.get();
                            
                            apodo = vr.getApodo() != null ? vr.getApodo() : "N/A";
                            numeracion = vr.getNumeracion() != null ? vr.getNumeracion() : "N/A";
                            uid = vr.getUid() != null ? vr.getUid() : "N/A";

                            if (vr.getIdPais() != null) {
                                Optional<Pais> paisOpt = paisService.obtenerPaisPorId(vr.getIdPais());
                                if (paisOpt.isPresent()) {
                                    nombrePais = paisOpt.get().getCodigo();
                                    if (idIdiomaPrincipal != -1) {
                                        for (PaisTr tr : paisOpt.get().getTraducciones()) {
                                            if (tr.getIdIdioma() == idIdiomaPrincipal) { nombrePais = tr.getNombre(); break; }
                                        }
                                    }
                                    paisesSet.add(nombrePais);
                                }
                            }

                            if (vr.getIdTipoVehiculo() != null) {
                                Optional<TipoVehiculo> tipoOpt = tipoVehiculoService.obtenerTipoVehiculoPorId(vr.getIdTipoVehiculo());
                                if (tipoOpt.isPresent()) {
                                    nombreTipo = tipoOpt.get().getCodigo();
                                    if (idIdiomaPrincipal != -1) {
                                        for (TipoVehiculoTr tr : tipoOpt.get().getTraducciones()) {
                                            if (tr.getIdIdioma() == idIdiomaPrincipal) { nombreTipo = tr.getNombre(); break; }
                                        }
                                    }
                                    tiposSet.add(nombreTipo);
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
                                if (!nombreOperadora.equals("Desconocida")) operadorasSet.add(nombreOperadora);
                            }
                        }
                    }
                }
            }

            tableModel.addRow(new Object[]{
                    m.getId(),
                    dirDecoder, // Col 1 (Integer)
                    nombrePais, // Col 2
                    apodo,      // Col 3
                    numeracion, // Col 4
                    uid,        // Col 5
                    nombreDueno,// Col 6
                    nombreFab,  // Col 7
                    referencia, // Col 8
                    nombreTipo, // Col 9
                    nombreEpoca,// Col 10
                    nombreEsquema,// Col 11
                    nombreOperadora// Col 12
            });
        }

        // Actualizar ComboBoxes de filtro
        actualizarComboFiltro(comboTipo, tiposSet);
        actualizarComboFiltro(comboPais, paisesSet);
        actualizarComboFiltro(comboOperadora, operadorasSet);
        actualizarComboFiltro(comboFabricante, fabricantesSet);
        
        aplicarFiltros(); // Reaplicar por si había algo seleccionado antes de recargar
    }

    private void actualizarComboFiltro(JComboBox<String> combo, java.util.Set<String> items) {
        String seleccionActual = (String) combo.getSelectedItem();
        combo.removeAllItems();
        combo.addItem("-- Todos --");
        for (String item : items) {
            combo.addItem(item);
        }
        if (seleccionActual != null && items.contains(seleccionActual)) {
            combo.setSelectedItem(seleccionActual);
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
