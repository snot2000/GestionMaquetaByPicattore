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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

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
    private TableRowSorter<DefaultTableModel> sorter;

    // Filtros
    private JTextField txtNumeracion;
    private JComboBox<String> comboPais, comboOperadora, comboEsquema, comboTipo, comboEpoca;
    
    private boolean isProgrammaticUpdate = false;

    public VehiculoRealInternalFrame(VehiculoRealService vehiculoRealService, TipoVehiculoService tipoVehiculoService, PaisService paisService, EpocaService epocaService, EsquemaPinturaService esquemaService, OperadoraService operadoraService, IdiomaService idiomaService) {
        super("Gestión de Vehículos Reales", true, true, true, true);
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
        // --- Panel Superior (Filtros) ---
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltro.setBorder(BorderFactory.createTitledBorder("Filtros de Búsqueda"));

        panelFiltro.add(new JLabel("País:"));
        comboPais = new JComboBox<>();
        comboPais.setPreferredSize(new Dimension(150, 25)); // +50%
        panelFiltro.add(comboPais);

        panelFiltro.add(new JLabel("Operadora:"));
        comboOperadora = new JComboBox<>();
        comboOperadora.setPreferredSize(new Dimension(150, 25)); // +50%
        panelFiltro.add(comboOperadora);

        panelFiltro.add(new JLabel("Esquema:"));
        comboEsquema = new JComboBox<>();
        comboEsquema.setPreferredSize(new Dimension(150, 25)); // +50%
        panelFiltro.add(comboEsquema);

        panelFiltro.add(new JLabel("Tipo:"));
        comboTipo = new JComboBox<>();
        comboTipo.setPreferredSize(new Dimension(150, 25)); // +50%
        panelFiltro.add(comboTipo);

        panelFiltro.add(new JLabel("Época:"));
        comboEpoca = new JComboBox<>();
        comboEpoca.setPreferredSize(new Dimension(120, 25)); // +50%
        panelFiltro.add(comboEpoca);
        
        panelFiltro.add(new JLabel("Numeración:"));
        txtNumeracion = new JTextField(15); // +50%
        panelFiltro.add(txtNumeracion);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panelFiltro.add(btnLimpiar);

        this.add(panelFiltro, BorderLayout.NORTH);

        // Listeners para filtros
        txtNumeracion.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });
        
        ActionListener comboListener = e -> {
            if (!isProgrammaticUpdate) {
                aplicarFiltros();
            }
        };
        
        // Listener especial para el combo de País que filtra las operadoras y esquemas
        comboPais.addActionListener(e -> {
            if (!isProgrammaticUpdate) {
                actualizarCombosDependientes();
                aplicarFiltros();
            }
        });
        
        // Listener especial para operadora que filtra esquemas
        comboOperadora.addActionListener(e -> {
            if (!isProgrammaticUpdate) {
                actualizarComboEsquemas();
                aplicarFiltros();
            }
        });
        
        comboEsquema.addActionListener(comboListener);
        comboTipo.addActionListener(comboListener);
        comboEpoca.addActionListener(comboListener);


        // --- Tabla ---
        String[] columnNames = {"ID", "País", "Operadora", "Numeración", "UID", "Nombre", "Apodo", "Esquema Pintura", "Tipo Vehículo", "Época"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0)); // Ocultar ID

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(7).setPreferredWidth(200); // Tipo Vehiculo (ahora indice 7, original 8)

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

        // --- Panel Botones ---
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
        isProgrammaticUpdate = true;
        txtNumeracion.setText("");
        if (comboPais.getItemCount() > 0) comboPais.setSelectedIndex(0);
        
        // Al limpiar país, recargamos todas las operadoras y esquemas
        actualizarCombosDependientesTodas();
        
        if (comboTipo.getItemCount() > 0) comboTipo.setSelectedIndex(0);
        if (comboEpoca.getItemCount() > 0) comboEpoca.setSelectedIndex(0);
        isProgrammaticUpdate = false;
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

        // Filtro País (Columna Vista: 0, Columna Modelo: 1)
        if (comboPais.getSelectedItem() != null && comboPais.getSelectedIndex() > 0) {
            filters.add(RowFilter.regexFilter("(?i)^" + comboPais.getSelectedItem().toString() + "$", 1));
        }

        // Filtro Operadora (Columna Vista: 1, Columna Modelo: 2)
        if (comboOperadora.getSelectedItem() != null && comboOperadora.getSelectedIndex() > 0) {
            filters.add(RowFilter.regexFilter("(?i)^" + comboOperadora.getSelectedItem().toString() + "$", 2));
        }

        // Filtro Numeración (Columna Vista: 2, Columna Modelo: 3)
        if (!txtNumeracion.getText().trim().isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + txtNumeracion.getText(), 3));
        }

        // Filtro Esquema (Columna Vista: 6, Columna Modelo: 7)
        if (comboEsquema.getSelectedItem() != null && comboEsquema.getSelectedIndex() > 0) {
            filters.add(RowFilter.regexFilter("(?i)^" + comboEsquema.getSelectedItem().toString() + "$", 7));
        }

        // Filtro Tipo (Columna Vista: 7, Columna Modelo: 8)
        if (comboTipo.getSelectedItem() != null && comboTipo.getSelectedIndex() > 0) {
            filters.add(RowFilter.regexFilter("(?i)^" + comboTipo.getSelectedItem().toString() + "$", 8));
        }

        // Filtro Época (Columna Vista: 8, Columna Modelo: 9)
        if (comboEpoca.getSelectedItem() != null && comboEpoca.getSelectedIndex() > 0) {
            filters.add(RowFilter.regexFilter("(?i)^" + comboEpoca.getSelectedItem().toString() + "$", 9));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }
    
    private void actualizarCombosDependientes() {
        isProgrammaticUpdate = true;
        
        String seleccionOperadoraPrevia = (String) comboOperadora.getSelectedItem();
        comboOperadora.removeAllItems();
        comboOperadora.addItem("-- Todas --");
        
        String paisStr = (String) comboPais.getSelectedItem();
        boolean filtrarPais = paisStr != null && !paisStr.equals("-- Todos --");

        Set<String> operadorasSet = new TreeSet<>();
        
        // Iteramos sobre todos los vehículos en el modelo de la tabla
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String p = (String) tableModel.getValueAt(i, 1); // País
            String o = (String) tableModel.getValueAt(i, 2); // Operadora
            
            if (!o.equals("N/A")) {
                if (!filtrarPais || p.equals(paisStr)) {
                    operadorasSet.add(o);
                }
            }
        }
        
        for (String op : operadorasSet) {
            comboOperadora.addItem(op);
        }
        
        if (seleccionOperadoraPrevia != null && operadorasSet.contains(seleccionOperadoraPrevia)) {
            comboOperadora.setSelectedItem(seleccionOperadoraPrevia);
        } else {
             comboOperadora.setSelectedIndex(0);
        }
        
        isProgrammaticUpdate = false;
        
        // Después de actualizar las operadoras, actualizamos los esquemas
        actualizarComboEsquemas();
    }
    
    private void actualizarComboEsquemas() {
        isProgrammaticUpdate = true;
        
        String seleccionEsquemaPrevia = (String) comboEsquema.getSelectedItem();
        comboEsquema.removeAllItems();
        comboEsquema.addItem("-- Todos --");
        
        String paisStr = (String) comboPais.getSelectedItem();
        boolean filtrarPais = paisStr != null && !paisStr.equals("-- Todos --");
        
        String operadoraStr = (String) comboOperadora.getSelectedItem();
        boolean filtrarOperadora = operadoraStr != null && !operadoraStr.equals("-- Todas --");
        
        Set<String> esquemasSet = new TreeSet<>();
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String p = (String) tableModel.getValueAt(i, 1); // País
            String o = (String) tableModel.getValueAt(i, 2); // Operadora
            String e = (String) tableModel.getValueAt(i, 7); // Esquema Pintura
            
            if (!e.equals("N/A")) {
                boolean coincidePais = !filtrarPais || p.equals(paisStr);
                boolean coincideOperadora = !filtrarOperadora || o.equals(operadoraStr);
                
                if (coincidePais && coincideOperadora) {
                    esquemasSet.add(e);
                }
            }
        }
        
        for (String esq : esquemasSet) {
            comboEsquema.addItem(esq);
        }
        
        if (seleccionEsquemaPrevia != null && esquemasSet.contains(seleccionEsquemaPrevia)) {
            comboEsquema.setSelectedItem(seleccionEsquemaPrevia);
        } else {
             comboEsquema.setSelectedIndex(0);
        }
        
        isProgrammaticUpdate = false;
    }
    
    private void actualizarCombosDependientesTodas() {
        isProgrammaticUpdate = true;
        
        // Todas las operadoras
        comboOperadora.removeAllItems();
        comboOperadora.addItem("-- Todas --");
        Set<String> ops = new TreeSet<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String o = (String) tableModel.getValueAt(i, 2);
            if (!o.equals("N/A")) ops.add(o);
        }
        for (String o : ops) comboOperadora.addItem(o);
        
        // Todos los esquemas
        comboEsquema.removeAllItems();
        comboEsquema.addItem("-- Todos --");
        Set<String> esqs = new TreeSet<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String e = (String) tableModel.getValueAt(i, 7);
            if (!e.equals("N/A")) esqs.add(e);
        }
        for (String e : esqs) comboEsquema.addItem(e);
        
        isProgrammaticUpdate = false;
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<VehiculoReal> vehiculos = vehiculoRealService.obtenerTodosLosVehiculosReales();
        
        Optional<Idioma> idiomaPrincipalOpt = idiomaService.obtenerIdiomaPrincipal();
        int idIdiomaPrincipal = idiomaPrincipalOpt.map(Idioma::getId).orElse(-1);

        Set<String> paisesSet = new TreeSet<>();
        Set<String> operadorasSet = new TreeSet<>();
        Set<String> esquemasSet = new TreeSet<>();
        Set<String> tiposSet = new TreeSet<>();
        Set<String> epocasSet = new TreeSet<>();

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
                    tiposSet.add(nombreTipo);
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
                    paisesSet.add(nombrePais);
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
                    epocasSet.add(nombreEpoca);
                }
            }

            String nombreEsquema = "N/A";
            if (v.getIdEsquemaPintura() != null) {
                Optional<EsquemaPintura> esquemaOpt = esquemaService.obtenerEsquemaPorId(v.getIdEsquemaPintura());
                if (esquemaOpt.isPresent()) {
                    nombreEsquema = esquemaOpt.get().getNombre();
                    esquemasSet.add(nombreEsquema);
                }
            }

            String nombreOperadora = "N/A";
            if (v.getIdOperadora() != null) {
                Optional<Operadora> opOpt = operadoraService.obtenerOperadoraPorId(v.getIdOperadora());
                if (opOpt.isPresent()) {
                    nombreOperadora = opOpt.get().getCodigo();
                    operadorasSet.add(nombreOperadora);
                }
            }

            tableModel.addRow(new Object[]{
                    v.getId(),
                    nombrePais,
                    nombreOperadora,
                    v.getNumeracion(),
                    v.getUid(),
                    v.getNombre(),
                    v.getApodo(),
                    nombreEsquema,
                    nombreTipo,
                    nombreEpoca
            });
        }

        // Inicializar combos principales
        actualizarComboFiltro(comboPais, paisesSet);
        actualizarComboFiltro(comboTipo, tiposSet);
        actualizarComboFiltro(comboEpoca, epocasSet);
        
        // Cascadas
        actualizarCombosDependientes();
        
        aplicarFiltros();
    }

    private void actualizarComboFiltro(JComboBox<String> combo, Set<String> items) {
        isProgrammaticUpdate = true;
        String seleccionActual = (String) combo.getSelectedItem();
        combo.removeAllItems();
        combo.addItem("-- Todos --");
        for (String item : items) {
            combo.addItem(item);
        }
        if (seleccionActual != null && items.contains(seleccionActual)) {
            combo.setSelectedItem(seleccionActual);
        }
        isProgrammaticUpdate = false;
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
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            String nombre = (String) tableModel.getValueAt(modelRow, 5); // Nombre está ahora en índice 5

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el vehículo real '" + nombre + "'?",
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
