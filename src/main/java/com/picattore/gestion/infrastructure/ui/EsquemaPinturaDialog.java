package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.*;
import com.picattore.gestion.domain.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EsquemaPinturaDialog extends JDialog {

    private final EsquemaPinturaService esquemaService;
    private final PaisService paisService;
    private final OperadoraService operadoraService;
    private final IdiomaService idiomaService;
    private final EsquemaPintura esquemaExistente;

    private JComboBox<Pais> comboPais;
    private JComboBox<Operadora> comboOperadora;
    private JTextField txtNombre, txtAnioInicio, txtAnioFin;
    private JTable tableTraducciones;
    private DefaultTableModel tableModel;
    private List<Operadora> todasLasOperadoras;

    public EsquemaPinturaDialog(Frame owner, EsquemaPinturaService esquemaService, PaisService paisService, OperadoraService operadoraService, IdiomaService idiomaService, EsquemaPintura esquemaExistente) {
        super(owner, esquemaExistente == null ? "Nuevo Esquema" : "Editar Esquema", true);
        this.esquemaService = esquemaService;
        this.paisService = paisService;
        this.operadoraService = operadoraService;
        this.idiomaService = idiomaService;
        this.esquemaExistente = esquemaExistente;

        this.setSize(1000, 600);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JPanel panelDatos = new JPanel(new GridLayout(0, 2, 5, 5));
        panelDatos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelDatos.add(new JLabel("País:"));
        comboPais = new JComboBox<>();
        comboPais.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Pais) {
                    Pais pais = (Pais) value;
                    String nombrePais = pais.getCodigo();
                    String codigo = pais.getCodigo();

                    Optional<Idioma> idiomaPrincipalOpt = idiomaService.obtenerIdiomaPrincipal();
                    if (idiomaPrincipalOpt.isPresent()) {
                        int idIdiomaPrincipal = idiomaPrincipalOpt.get().getId();
                        for (PaisTr tr : pais.getTraducciones()) {
                            if (tr.getIdIdioma() == idIdiomaPrincipal) {
                                nombrePais = tr.getNombre();
                                break;
                            }
                        }
                    }
                    setText(nombrePais + " (" + codigo + ")");
                }
                return this;
            }
        });
        panelDatos.add(comboPais);

        panelDatos.add(new JLabel("Operadora:"));
        comboOperadora = new JComboBox<>();
        panelDatos.add(comboOperadora);

        panelDatos.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelDatos.add(txtNombre);

        panelDatos.add(new JLabel("Año Inicio:"));
        txtAnioInicio = new JTextField();
        panelDatos.add(txtAnioInicio);

        panelDatos.add(new JLabel("Año Fin:"));
        txtAnioFin = new JTextField();
        panelDatos.add(txtAnioFin);

        this.add(panelDatos, BorderLayout.NORTH);

        String[] columnNames = {"ID Idioma", "Idioma", "Descripción", "Código Colores", "Colores"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2;
            }
        };
        tableTraducciones = new JTable(tableModel);
        tableTraducciones.getColumnModel().removeColumn(tableTraducciones.getColumnModel().getColumn(0));

        this.add(new JScrollPane(tableTraducciones), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardar());
        panelBotones.add(btnGuardar);
        this.add(panelBotones, BorderLayout.SOUTH);
        
        // Listener para filtrar operadoras al cambiar país
        comboPais.addActionListener(e -> filtrarOperadoras());
    }

    private void cargarDatos() {
        // Cargar combos
        List<Pais> paises = paisService.obtenerTodosLosPaises();
        comboPais.setModel(new DefaultComboBoxModel<>(paises.toArray(new Pais[0])));
        
        todasLasOperadoras = operadoraService.obtenerTodasLasOperadoras();
        // Inicialmente cargamos todas, pero luego el filtro actuará si hay selección
        comboOperadora.setModel(new DefaultComboBoxModel<>(todasLasOperadoras.toArray(new Operadora[0])));

        List<Idioma> idiomas = idiomaService.obtenerTodosLosIdiomas();
        
        if (esquemaExistente != null) {
            txtNombre.setText(esquemaExistente.getNombre());
            txtAnioInicio.setText(esquemaExistente.getAnioInicio() != null ? String.valueOf(esquemaExistente.getAnioInicio()) : "");
            txtAnioFin.setText(esquemaExistente.getAnioFin() != null ? String.valueOf(esquemaExistente.getAnioFin()) : "");

            // Seleccionar país (esto disparará el listener y filtrará operadoras)
            for (int i = 0; i < comboPais.getItemCount(); i++) {
                if (comboPais.getItemAt(i).getIdPais() == esquemaExistente.getIdPais()) {
                    comboPais.setSelectedIndex(i);
                    break;
                }
            }
            
            // Seleccionar operadora (después de filtrar)
            for (int i = 0; i < comboOperadora.getItemCount(); i++) {
                if (comboOperadora.getItemAt(i).getIdOperadora() == esquemaExistente.getIdOperadora()) {
                    comboOperadora.setSelectedIndex(i);
                    break;
                }
            }

            for (Idioma idioma : idiomas) {
                String desc = "", codCol = "", col = "";
                for (EsquemaPinturaTr tr : esquemaExistente.getTraducciones()) {
                    if (tr.getIdIdioma() == idioma.getId()) {
                        desc = tr.getDescripcion();
                        codCol = tr.getCodigoColores();
                        col = tr.getColores();
                        break;
                    }
                }
                tableModel.addRow(new Object[]{idioma.getId(), idioma.getNombre(), desc, codCol, col});
            }
        } else {
            // Nuevo esquema
            comboPais.setSelectedIndex(-1); // Ningún país seleccionado
            filtrarOperadoras(); // Mostrar todas las operadoras
            
            for (Idioma idioma : idiomas) {
                tableModel.addRow(new Object[]{idioma.getId(), idioma.getNombre(), "", "", ""});
            }
        }
    }

    private void filtrarOperadoras() {
        Pais paisSeleccionado = (Pais) comboPais.getSelectedItem();
        List<Operadora> operadorasFiltradas;

        if (paisSeleccionado == null) {
            operadorasFiltradas = new ArrayList<>(todasLasOperadoras);
        } else {
            operadorasFiltradas = todasLasOperadoras.stream()
                    .filter(op -> op.getIdPais() != null && op.getIdPais() == paisSeleccionado.getIdPais())
                    .collect(Collectors.toList());
        }

        DefaultComboBoxModel<Operadora> model = new DefaultComboBoxModel<>(operadorasFiltradas.toArray(new Operadora[0]));
        comboOperadora.setModel(model);
        
        // Si estamos editando y acabamos de recargar, intentamos mantener la selección si es válida
        if (esquemaExistente != null) {
             for (int i = 0; i < comboOperadora.getItemCount(); i++) {
                if (comboOperadora.getItemAt(i).getIdOperadora() == esquemaExistente.getIdOperadora()) {
                    comboOperadora.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void guardar() {
        if (tableTraducciones.isEditing()) {
            tableTraducciones.getCellEditor().stopCellEditing();
        }

        Pais pais = (Pais) comboPais.getSelectedItem();
        Operadora operadora = (Operadora) comboOperadora.getSelectedItem();
        if (pais == null || operadora == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un país y una operadora.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer anioInicio = txtAnioInicio.getText().isEmpty() ? null : Integer.parseInt(txtAnioInicio.getText());
        Integer anioFin = txtAnioFin.getText().isEmpty() ? null : Integer.parseInt(txtAnioFin.getText());

        List<EsquemaPinturaTr> traducciones = new ArrayList<>();
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int idIdioma = (int) tableModel.getValueAt(i, 0); 
            String desc = (String) tableModel.getValueAt(i, 2);
            String codCol = (String) tableModel.getValueAt(i, 3);
            String col = (String) tableModel.getValueAt(i, 4);
            
            if ((desc != null && !desc.trim().isEmpty()) || (codCol != null && !codCol.trim().isEmpty()) || (col != null && !col.trim().isEmpty())) {
                traducciones.add(new EsquemaPinturaTr(idIdioma, desc, codCol, col));
            }
        }

        if (esquemaExistente == null) {
            esquemaService.crearEsquema(pais.getIdPais(), operadora.getIdOperadora(), txtNombre.getText(), anioInicio, anioFin, traducciones);
        } else {
            esquemaService.actualizarEsquema(esquemaExistente.getIdEsquemaPintura(), pais.getIdPais(), operadora.getIdOperadora(), txtNombre.getText(), anioInicio, anioFin, traducciones);
        }
        dispose();
    }
}
