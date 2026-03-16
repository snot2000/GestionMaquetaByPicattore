package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.OperadoraService;
import com.picattore.gestion.application.PaisService;
import com.picattore.gestion.application.IdiomaService;
import com.picattore.gestion.domain.Operadora;
import com.picattore.gestion.domain.Pais;
import com.picattore.gestion.domain.PaisTr;
import com.picattore.gestion.domain.Idioma;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OperadoraDialog extends JDialog {

    private final OperadoraService operadoraService;
    private final PaisService paisService;
    private final IdiomaService idiomaService;
    private final Operadora operadoraExistente;

    private JTextField txtCodigo, txtNombre, txtAnioCreacion, txtAnioDisolucion;
    private JTextArea txtInformacion;
    private JComboBox<Pais> comboPais;
    private JList<Operadora> listPredecesoras, listSucesoras;
    private DefaultListModel<Operadora> modelPredecesoras, modelSucesoras;

    public OperadoraDialog(Frame owner, OperadoraService operadoraService, PaisService paisService, IdiomaService idiomaService, Operadora operadoraExistente) {
        super(owner, operadoraExistente == null ? "Nueva Operadora" : "Editar Operadora", true);
        this.operadoraService = operadoraService;
        this.paisService = paisService;
        this.idiomaService = idiomaService;
        this.operadoraExistente = operadoraExistente;

        this.setSize(900, 700);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Sección Datos Generales ---
        JPanel panelGeneral = new JPanel(new BorderLayout(5, 5));
        panelGeneral.setBorder(new TitledBorder("Datos Generales"));
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Código:"));
        txtCodigo = new JTextField();
        formPanel.add(txtCodigo);
        
        formPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        formPanel.add(txtNombre);
        
        formPanel.add(new JLabel("País:"));
        comboPais = new JComboBox<>();
        
        // Renderer para mostrar el nombre del país en el idioma principal
        comboPais.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Pais) {
                    Pais pais = (Pais) value;
                    String nombrePais = pais.getCodigo(); // Fallback al código

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
                    setText(nombrePais);
                }
                return this;
            }
        });
        formPanel.add(comboPais);

        formPanel.add(new JLabel("Año Creación:"));
        txtAnioCreacion = new JTextField();
        formPanel.add(txtAnioCreacion);
        
        formPanel.add(new JLabel("Año Disolución:"));
        txtAnioDisolucion = new JTextField();
        formPanel.add(txtAnioDisolucion);
        
        panelGeneral.add(formPanel, BorderLayout.NORTH);

        txtInformacion = new JTextArea(5, 20);
        JScrollPane scrollInfo = new JScrollPane(txtInformacion);
        scrollInfo.setBorder(BorderFactory.createTitledBorder("Información Adicional"));
        panelGeneral.add(scrollInfo, BorderLayout.CENTER);

        mainPanel.add(panelGeneral);
        mainPanel.add(Box.createVerticalStrut(10));

        // --- Sección Relaciones ---
        JPanel panelRelaciones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelRelaciones.setBorder(new TitledBorder("Relaciones"));

        // Predecesoras
        JPanel panelPredecesoras = new JPanel(new BorderLayout());
        panelPredecesoras.add(new JLabel("Compañías Predecesoras:"), BorderLayout.NORTH);
        
        modelPredecesoras = new DefaultListModel<>();
        listPredecesoras = new JList<>(modelPredecesoras);
        listPredecesoras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelPredecesoras.add(new JScrollPane(listPredecesoras), BorderLayout.CENTER);
        
        JButton btnModificarPredecesoras = new JButton("Modificar Selección");
        btnModificarPredecesoras.addActionListener(e -> abrirSeleccionPredecesoras());
        panelPredecesoras.add(btnModificarPredecesoras, BorderLayout.SOUTH);
        
        panelRelaciones.add(panelPredecesoras);

        // Sucesoras
        JPanel panelSucesoras = new JPanel(new BorderLayout());
        panelSucesoras.add(new JLabel("Compañías Sucesoras:"), BorderLayout.NORTH);
        
        modelSucesoras = new DefaultListModel<>();
        listSucesoras = new JList<>(modelSucesoras);
        listSucesoras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelSucesoras.add(new JScrollPane(listSucesoras), BorderLayout.CENTER);
        
        JButton btnModificarSucesoras = new JButton("Modificar Selección");
        btnModificarSucesoras.addActionListener(e -> abrirSeleccionSucesoras());
        panelSucesoras.add(btnModificarSucesoras, BorderLayout.SOUTH);
        
        panelRelaciones.add(panelSucesoras);

        // Ajustar altura del panel de relaciones
        panelRelaciones.setPreferredSize(new Dimension(800, 250));

        mainPanel.add(panelRelaciones);

        this.add(mainPanel, BorderLayout.CENTER);

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
        // Cargar combo de países
        List<Pais> todosPaises = paisService.obtenerTodosLosPaises();
        DefaultComboBoxModel<Pais> modelPais = new DefaultComboBoxModel<>();
        for (Pais p : todosPaises) {
            modelPais.addElement(p);
        }
        comboPais.setModel(modelPais);
        comboPais.setSelectedIndex(-1);

        if (operadoraExistente != null) {
            txtCodigo.setText(operadoraExistente.getCodigo());
            txtNombre.setText(operadoraExistente.getNombre());
            txtInformacion.setText(operadoraExistente.getInformacion());
            txtAnioCreacion.setText(operadoraExistente.getAnioCreacion() != null ? String.valueOf(operadoraExistente.getAnioCreacion()) : "");
            txtAnioDisolucion.setText(operadoraExistente.getAnioDisolucion() != null ? String.valueOf(operadoraExistente.getAnioDisolucion()) : "");

            if (operadoraExistente.getIdPais() != null) {
                for (int i = 0; i < comboPais.getItemCount(); i++) {
                    Pais p = comboPais.getItemAt(i);
                    if (p.getIdPais() == operadoraExistente.getIdPais()) {
                        comboPais.setSelectedIndex(i);
                        break;
                    }
                }
            }

            // Cargar relaciones seleccionadas
            List<Operadora> todasOperadoras = operadoraService.obtenerTodasLasOperadoras();
            
            // Predecesoras
            modelPredecesoras.clear();
            for (Integer id : operadoraExistente.getPredecesorasIds()) {
                todasOperadoras.stream()
                        .filter(op -> op.getIdOperadora() == id)
                        .findFirst()
                        .ifPresent(modelPredecesoras::addElement);
            }

            // Sucesoras
            modelSucesoras.clear();
            for (Integer id : operadoraExistente.getSucesorasIds()) {
                todasOperadoras.stream()
                        .filter(op -> op.getIdOperadora() == id)
                        .findFirst()
                        .ifPresent(modelSucesoras::addElement);
            }
        }
    }

    private void abrirSeleccionPredecesoras() {
        List<Operadora> todasOperadoras = operadoraService.obtenerTodasLasOperadoras();
        if (operadoraExistente != null) {
            todasOperadoras.removeIf(op -> op.getIdOperadora() == operadoraExistente.getIdOperadora());
        }

        List<Operadora> seleccionadas = new ArrayList<>();
        for (int i = 0; i < modelPredecesoras.size(); i++) {
            seleccionadas.add(modelPredecesoras.get(i));
        }

        MultiSelectionDialog dialog = new MultiSelectionDialog(this, "Seleccionar Predecesoras", todasOperadoras, seleccionadas);
        dialog.setVisible(true);

        List<Operadora> nuevasSeleccionadas = dialog.getSelectedItems();
        if (nuevasSeleccionadas != null) {
            modelPredecesoras.clear();
            nuevasSeleccionadas.forEach(modelPredecesoras::addElement);
        }
    }

    private void abrirSeleccionSucesoras() {
        List<Operadora> todasOperadoras = operadoraService.obtenerTodasLasOperadoras();
        if (operadoraExistente != null) {
            todasOperadoras.removeIf(op -> op.getIdOperadora() == operadoraExistente.getIdOperadora());
        }

        List<Operadora> seleccionadas = new ArrayList<>();
        for (int i = 0; i < modelSucesoras.size(); i++) {
            seleccionadas.add(modelSucesoras.get(i));
        }

        MultiSelectionDialog dialog = new MultiSelectionDialog(this, "Seleccionar Sucesoras", todasOperadoras, seleccionadas);
        dialog.setVisible(true);

        List<Operadora> nuevasSeleccionadas = dialog.getSelectedItems();
        if (nuevasSeleccionadas != null) {
            modelSucesoras.clear();
            nuevasSeleccionadas.forEach(modelSucesoras::addElement);
        }
    }

    private void guardar() {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        Integer anioCreacion = null;
        Integer anioDisolucion = null;
        
        try {
            if (!txtAnioCreacion.getText().isEmpty()) anioCreacion = Integer.parseInt(txtAnioCreacion.getText());
            if (!txtAnioDisolucion.getText().isEmpty()) anioDisolucion = Integer.parseInt(txtAnioDisolucion.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Los años deben ser números enteros.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Pais paisSeleccionado = (Pais) comboPais.getSelectedItem();
        Integer idPais = paisSeleccionado != null ? paisSeleccionado.getIdPais() : null;

        List<Integer> predecesorasIds = new ArrayList<>();
        for (int i = 0; i < modelPredecesoras.size(); i++) {
            predecesorasIds.add(modelPredecesoras.get(i).getIdOperadora());
        }

        List<Integer> sucesorasIds = new ArrayList<>();
        for (int i = 0; i < modelSucesoras.size(); i++) {
            sucesorasIds.add(modelSucesoras.get(i).getIdOperadora());
        }

        if (operadoraExistente == null) {
            operadoraService.crearOperadora(codigo, nombre, txtInformacion.getText(), anioCreacion, anioDisolucion, idPais, predecesorasIds, sucesorasIds);
        } else {
            operadoraService.actualizarOperadora(operadoraExistente.getIdOperadora(), codigo, nombre, txtInformacion.getText(), anioCreacion, anioDisolucion, idPais, predecesorasIds, sucesorasIds);
        }
        dispose();
    }
}
