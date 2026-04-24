package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.OperadoraService;
import com.picattore.gestion.application.PaisService;
import com.picattore.gestion.application.IdiomaService;
import com.picattore.gestion.domain.Operadora;
import com.picattore.gestion.domain.Pais;
import com.picattore.gestion.domain.PaisTr;
import com.picattore.gestion.domain.Idioma;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OperadoraInternalFrame extends JInternalFrame implements LanguageChangeListener {

    private final OperadoraService operadoraService;
    private final PaisService paisService;
    private final IdiomaService idiomaService;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    // Filtros
    private JTextField txtFiltro;

    public OperadoraInternalFrame(OperadoraService operadoraService, PaisService paisService, IdiomaService idiomaService) {
        super("Gestión de Operadoras", true, true, true, true);
        this.operadoraService = operadoraService;
        this.paisService = paisService;
        this.idiomaService = idiomaService;
        
        // Aumentar el ancho en un 50% (de 800 a 1200)
        this.setSize(1200, 600);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // --- Panel Superior (Filtro) ---
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltro.setBorder(BorderFactory.createTitledBorder("Filtro de Búsqueda"));
        
        panelFiltro.add(new JLabel("Buscar en cualquier campo:"));
        txtFiltro = new JTextField(30);
        panelFiltro.add(txtFiltro);
        this.add(panelFiltro, BorderLayout.NORTH);

        txtFiltro.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        // --- Tabla ---
        // Añadida la columna País
        String[] columnNames = {"ID", "País", "Código", "Nombre", "Año Creación", "Año Disolución"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                // Años pueden ser Integer para mejor ordenación (índices del modelo)
                if (column == 4 || column == 5) return Integer.class;
                return String.class;
            }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
        // Ocultar la columna ID (índice 0) de la vista
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0));
        
        // Ajustar anchos para que "Nombre" ocupe todo el espacio restante
        // Usamos AUTO_RESIZE_LAST_COLUMN para que la última (Año D.) se adapte si es necesario, 
        // pero asignamos un preferredWidth muy alto a Nombre
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        
        // Índices en vista: País(0), Código(1), Nombre(2), Año Creación(3), Año Disolución(4)
        TableColumn colPais = table.getColumnModel().getColumn(0);
        colPais.setPreferredWidth(120);
        colPais.setMaxWidth(200);

        TableColumn colCodigo = table.getColumnModel().getColumn(1);
        colCodigo.setPreferredWidth(100);
        colCodigo.setMaxWidth(150);
        
        TableColumn colNombre = table.getColumnModel().getColumn(2);
        colNombre.setPreferredWidth(800); // Darle el mayor peso posible a la columna "Nombre"
        
        // Reducir tamaño de las columnas de año a aprox. un 50% de lo normal para caber el texto (un año son 4 digitos ~30px + padding + título)
        TableColumn colAnioC = table.getColumnModel().getColumn(3);
        colAnioC.setPreferredWidth(80); 
        colAnioC.setMaxWidth(100); 
        
        TableColumn colAnioD = table.getColumnModel().getColumn(4);
        colAnioD.setPreferredWidth(90);
        colAnioD.setMaxWidth(110);

        // Ordenar por defecto por Nombre ascendente (Compatible Java 8+)
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING)); // 3 es el índice de Nombre en el MODELO
        sorter.setSortKeys(sortKeys);
        
        // Añadir doble clic para editar
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

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Añadir");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Borrar");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        this.add(buttonPanel, BorderLayout.SOUTH);

        // Acciones
        btnAdd.addActionListener(e -> abrirDialogo(null));
        btnEdit.addActionListener(e -> editarSeleccionado());
        btnDelete.addActionListener(e -> eliminarSeleccionado());
    }

    private void filtrar() {
        String text = txtFiltro.getText();
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // (?i) para case-insensitive
        }
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<Operadora> operadoras = operadoraService.obtenerTodasLasOperadoras();
        
        Optional<Idioma> idiomaPrincipalOpt = idiomaService.obtenerIdiomaPrincipal();
        int idIdiomaPrincipal = idiomaPrincipalOpt.map(Idioma::getId).orElse(-1);

        for (Operadora operadora : operadoras) {
            String nombrePais = "N/A";
            if (operadora.getIdPais() != null) {
                Optional<Pais> paisOpt = paisService.obtenerPaisPorId(operadora.getIdPais());
                if (paisOpt.isPresent()) {
                    Pais pais = paisOpt.get();
                    nombrePais = pais.getCodigo();
                    if (idIdiomaPrincipal != -1) {
                        for (PaisTr tr : pais.getTraducciones()) {
                            if (tr.getIdIdioma() == idIdiomaPrincipal) {
                                nombrePais = tr.getNombre();
                                break;
                            }
                        }
                    }
                }
            }

            tableModel.addRow(new Object[]{
                    operadora.getIdOperadora(),
                    nombrePais,
                    operadora.getCodigo(),
                    operadora.getNombre(),
                    operadora.getAnioCreacion(),
                    operadora.getAnioDisolucion()
            });
        }
    }

    private void abrirDialogo(Operadora operadora) {
        OperadoraDialog dialog = new OperadoraDialog((Frame) SwingUtilities.getWindowAncestor(this), operadoraService, paisService, idiomaService, operadora);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        cargarDatos();
    }

    private void editarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            // Convertir índice de vista a modelo porque la columna ID está oculta y puede estar filtrada/ordenada
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            operadoraService.obtenerOperadoraPorId(id).ifPresent(this::abrirDialogo);
        }
    }

    private void eliminarSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            String nombre = (String) tableModel.getValueAt(modelRow, 3); // Nombre está en índice 3 del modelo

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar la operadora '" + nombre + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    operadoraService.eliminarOperadora(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar. Verifique que no tenga referencias.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una operadora para borrar.");
        }
    }

    @Override
    public void onLanguageChanged() {
        cargarDatos();
    }
}
