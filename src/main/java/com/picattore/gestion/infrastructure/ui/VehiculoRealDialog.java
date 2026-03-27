package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.*;
import com.picattore.gestion.domain.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VehiculoRealDialog extends JDialog {

    private final VehiculoRealService vehiculoRealService;
    private final TipoVehiculoService tipoVehiculoService;
    private final PaisService paisService;
    private final EpocaService epocaService;
    private final EsquemaPinturaService esquemaService;
    private final OperadoraService operadoraService;
    private final IdiomaService idiomaService;
    private final VehiculoReal vehiculoExistente;

    private JTextField txtNombre, txtApodo, txtNumeracion, txtUid;
    private JTextField txtFechaFabricacion, txtFechaBaja, txtFechaInicioPintura, txtFechaFinalPintura, txtVelocidadMaxima;
    private JTextArea txtDescripcionTecnica;
    private JComboBox<TipoVehiculo> comboTipoVehiculo;
    private JComboBox<Pais> comboPais;
    private JComboBox<Epoca> comboEpoca;
    private JComboBox<EsquemaPintura> comboEsquema;
    private JComboBox<Operadora> comboOperadora;

    private List<EsquemaPintura> todosLosEsquemas;
    private List<Operadora> todasLasOperadoras;

    private boolean isProgrammaticUpdate = false;

    public VehiculoRealDialog(Frame owner, VehiculoRealService vehiculoRealService, TipoVehiculoService tipoVehiculoService, PaisService paisService, EpocaService epocaService, EsquemaPinturaService esquemaService, OperadoraService operadoraService, IdiomaService idiomaService, VehiculoReal vehiculoExistente) {
        super(owner, vehiculoExistente == null ? "Nuevo Vehículo Real" : "Editar Vehículo Real", true);
        this.vehiculoRealService = vehiculoRealService;
        this.tipoVehiculoService = tipoVehiculoService;
        this.paisService = paisService;
        this.epocaService = epocaService;
        this.esquemaService = esquemaService;
        this.operadoraService = operadoraService;
        this.idiomaService = idiomaService;
        this.vehiculoExistente = vehiculoExistente;

        this.setSize(1200, 700); // Ancho duplicado y alto ajustado
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de formulario con 4 columnas (2 pares de Label-Campo)
        JPanel formPanel = new JPanel(new GridLayout(0, 4, 15, 10));

        formPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        formPanel.add(txtNombre);

        formPanel.add(new JLabel("Apodo:"));
        txtApodo = new JTextField();
        formPanel.add(txtApodo);

        formPanel.add(new JLabel("Numeración:"));
        txtNumeracion = new JTextField();
        formPanel.add(txtNumeracion);

        formPanel.add(new JLabel("UID:"));
        txtUid = new JTextField();
        formPanel.add(txtUid);

        formPanel.add(new JLabel("Tipo Vehículo:"));
        comboTipoVehiculo = new JComboBox<>();
        comboTipoVehiculo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TipoVehiculo) {
                    TipoVehiculo tipo = (TipoVehiculo) value;
                    String nombre = tipo.getCodigo();
                    Optional<Idioma> idiomaPrincipalOpt = idiomaService.obtenerIdiomaPrincipal();
                    if (idiomaPrincipalOpt.isPresent()) {
                        int idIdiomaPrincipal = idiomaPrincipalOpt.get().getId();
                        for (TipoVehiculoTr tr : tipo.getTraducciones()) {
                            if (tr.getIdIdioma() == idIdiomaPrincipal) {
                                nombre = tr.getNombre();
                                break;
                            }
                        }
                    }
                    setText(nombre + " (" + tipo.getCodigo() + ")");
                }
                return this;
            }
        });
        formPanel.add(comboTipoVehiculo);

        formPanel.add(new JLabel("Época:"));
        comboEpoca = new JComboBox<>();
        comboEpoca.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Epoca) {
                    Epoca epoca = (Epoca) value;
                    String nombre = epoca.getCodigo();
                    Optional<Idioma> idiomaPrincipalOpt = idiomaService.obtenerIdiomaPrincipal();
                    if (idiomaPrincipalOpt.isPresent()) {
                        int idIdiomaPrincipal = idiomaPrincipalOpt.get().getId();
                        for (EpocaTr tr : epoca.getTraducciones()) {
                            if (tr.getIdIdioma() == idIdiomaPrincipal) {
                                nombre = tr.getNombre();
                                break;
                            }
                        }
                    }
                    setText(nombre + " (" + epoca.getCodigo() + ")");
                }
                return this;
            }
        });
        formPanel.add(comboEpoca);

        formPanel.add(new JLabel("País:"));
        comboPais = new JComboBox<>();
        comboPais.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Pais) {
                    Pais pais = (Pais) value;
                    String nombre = pais.getCodigo();
                    Optional<Idioma> idiomaPrincipalOpt = idiomaService.obtenerIdiomaPrincipal();
                    if (idiomaPrincipalOpt.isPresent()) {
                        int idIdiomaPrincipal = idiomaPrincipalOpt.get().getId();
                        for (PaisTr tr : pais.getTraducciones()) {
                            if (tr.getIdIdioma() == idIdiomaPrincipal) {
                                nombre = tr.getNombre();
                                break;
                            }
                        }
                    }
                    setText(nombre + " (" + pais.getCodigo() + ")");
                }
                return this;
            }
        });
        formPanel.add(comboPais);

        formPanel.add(new JLabel("Operadora:"));
        comboOperadora = new JComboBox<>();
        formPanel.add(comboOperadora);

        formPanel.add(new JLabel("Esquema Pintura:"));
        comboEsquema = new JComboBox<>();
        comboEsquema.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof EsquemaPintura) {
                    EsquemaPintura esquema = (EsquemaPintura) value;
                    setText(esquema.getNombre());
                }
                return this;
            }
        });
        formPanel.add(comboEsquema);

        formPanel.add(new JLabel("Velocidad Máxima:"));
        txtVelocidadMaxima = new JTextField();
        formPanel.add(txtVelocidadMaxima);

        formPanel.add(new JLabel("Fecha Fabricación:"));
        txtFechaFabricacion = new JTextField();
        formPanel.add(txtFechaFabricacion);

        formPanel.add(new JLabel("Fecha Baja:"));
        txtFechaBaja = new JTextField();
        formPanel.add(txtFechaBaja);

        formPanel.add(new JLabel("Fecha Inicio Pintura:"));
        txtFechaInicioPintura = new JTextField();
        formPanel.add(txtFechaInicioPintura);

        formPanel.add(new JLabel("Fecha Final Pintura:"));
        txtFechaFinalPintura = new JTextField();
        formPanel.add(txtFechaFinalPintura);

        mainPanel.add(formPanel, BorderLayout.NORTH);

        // Nuevo campo descripción técnica ocupando el resto (doble de alto)
        txtDescripcionTecnica = new JTextArea(10, 20); // Altura incrementada
        JScrollPane scrollDesc = new JScrollPane(txtDescripcionTecnica);
        scrollDesc.setBorder(BorderFactory.createTitledBorder("Descripción Técnica"));
        mainPanel.add(scrollDesc, BorderLayout.CENTER);

        this.add(mainPanel, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        this.add(panelBotones, BorderLayout.SOUTH);

        comboPais.addActionListener(e -> {
            if (!isProgrammaticUpdate) {
                filtrarOperadorasYEsquemas();
            }
        });

        comboOperadora.addActionListener(e -> {
            if (!isProgrammaticUpdate) {
                filtrarEsquemas();
            }
        });
    }

    private void cargarDatos() {
        isProgrammaticUpdate = true;

        todosLosEsquemas = esquemaService.obtenerTodosLosEsquemas();
        todasLasOperadoras = operadoraService.obtenerTodasLasOperadoras();

        comboTipoVehiculo.setModel(new DefaultComboBoxModel<>(tipoVehiculoService.obtenerTodosLosTiposVehiculo().toArray(new TipoVehiculo[0])));
        comboEpoca.setModel(new DefaultComboBoxModel<>(epocaService.obtenerTodasLasEpocas().toArray(new Epoca[0])));
        
        List<Pais> paises = paisService.obtenerTodosLosPaises();
        comboPais.setModel(new DefaultComboBoxModel<>(paises.toArray(new Pais[0])));

        comboTipoVehiculo.setSelectedIndex(-1);
        comboEpoca.setSelectedIndex(-1);
        comboPais.setSelectedIndex(-1);
        
        comboOperadora.setModel(new DefaultComboBoxModel<>(todasLasOperadoras.toArray(new Operadora[0])));
        comboOperadora.setSelectedIndex(-1);
        comboEsquema.setModel(new DefaultComboBoxModel<>(todosLosEsquemas.toArray(new EsquemaPintura[0])));
        comboEsquema.setSelectedIndex(-1);

        if (vehiculoExistente != null) {
            txtNombre.setText(vehiculoExistente.getNombre());
            txtApodo.setText(vehiculoExistente.getApodo());
            txtNumeracion.setText(vehiculoExistente.getNumeracion());
            txtUid.setText(vehiculoExistente.getUid());
            txtVelocidadMaxima.setText(vehiculoExistente.getVelocidadMaxima() != null ? String.valueOf(vehiculoExistente.getVelocidadMaxima()) : "");
            txtFechaFabricacion.setText(vehiculoExistente.getFechaFabricacion());
            txtFechaBaja.setText(vehiculoExistente.getFechaBaja());
            txtFechaInicioPintura.setText(vehiculoExistente.getFechaInicioPintura());
            txtFechaFinalPintura.setText(vehiculoExistente.getFechaFinalPintura());
            txtDescripcionTecnica.setText(vehiculoExistente.getDescripcionTecnica());

            if (vehiculoExistente.getIdTipoVehiculo() != null) {
                for (int i = 0; i < comboTipoVehiculo.getItemCount(); i++) {
                    if (comboTipoVehiculo.getItemAt(i).getIdTipoVehiculo() == vehiculoExistente.getIdTipoVehiculo()) {
                        comboTipoVehiculo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (vehiculoExistente.getIdEpoca() != null) {
                for (int i = 0; i < comboEpoca.getItemCount(); i++) {
                    if (comboEpoca.getItemAt(i).getIdEpoca() == vehiculoExistente.getIdEpoca()) {
                        comboEpoca.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            if (vehiculoExistente.getIdPais() != null) {
                for (int i = 0; i < comboPais.getItemCount(); i++) {
                    if (comboPais.getItemAt(i).getIdPais() == vehiculoExistente.getIdPais()) {
                        comboPais.setSelectedIndex(i);
                        break;
                    }
                }
                
                isProgrammaticUpdate = false;
                filtrarOperadoras();
                isProgrammaticUpdate = true;
            }

            if (vehiculoExistente.getIdOperadora() != null) {
                for (int i = 0; i < comboOperadora.getItemCount(); i++) {
                    if (comboOperadora.getItemAt(i).getIdOperadora() == vehiculoExistente.getIdOperadora()) {
                        comboOperadora.setSelectedIndex(i);
                        break;
                    }
                }
                
                isProgrammaticUpdate = false;
                filtrarEsquemas();
                isProgrammaticUpdate = true;
            }

            if (vehiculoExistente.getIdEsquemaPintura() != null) {
                for (int i = 0; i < comboEsquema.getItemCount(); i++) {
                    if (comboEsquema.getItemAt(i).getIdEsquemaPintura() == vehiculoExistente.getIdEsquemaPintura()) {
                        comboEsquema.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
        
        isProgrammaticUpdate = false;
    }

    private void filtrarOperadorasYEsquemas() {
        filtrarOperadoras();
        filtrarEsquemas();
    }

    private void filtrarOperadoras() {
        Pais paisSeleccionado = (Pais) comboPais.getSelectedItem();
        List<Operadora> operadorasFiltradas;

        if (paisSeleccionado == null) {
            operadorasFiltradas = todasLasOperadoras;
        } else {
            operadorasFiltradas = todasLasOperadoras.stream()
                    .filter(op -> op.getIdPais() != null && op.getIdPais() == paisSeleccionado.getIdPais())
                    .collect(Collectors.toList());
        }

        Operadora seleccionPrevia = (Operadora) comboOperadora.getSelectedItem();
        
        isProgrammaticUpdate = true;
        comboOperadora.setModel(new DefaultComboBoxModel<>(operadorasFiltradas.toArray(new Operadora[0])));
        comboOperadora.setSelectedIndex(-1);

        if (seleccionPrevia != null) {
            for (int i = 0; i < comboOperadora.getItemCount(); i++) {
                if (comboOperadora.getItemAt(i).getIdOperadora() == seleccionPrevia.getIdOperadora()) {
                    comboOperadora.setSelectedIndex(i);
                    break;
                }
            }
        }
        isProgrammaticUpdate = false;
    }

    private void filtrarEsquemas() {
        Pais paisSeleccionado = (Pais) comboPais.getSelectedItem();
        Operadora operadoraSeleccionada = (Operadora) comboOperadora.getSelectedItem();
        
        List<EsquemaPintura> esquemasFiltrados = todosLosEsquemas;

        if (paisSeleccionado != null) {
            esquemasFiltrados = esquemasFiltrados.stream()
                    .filter(eq -> eq.getIdPais() == paisSeleccionado.getIdPais())
                    .collect(Collectors.toList());
        }

        if (operadoraSeleccionada != null) {
            esquemasFiltrados = esquemasFiltrados.stream()
                    .filter(eq -> eq.getIdOperadora() == operadoraSeleccionada.getIdOperadora())
                    .collect(Collectors.toList());
        }

        EsquemaPintura seleccionPrevia = (EsquemaPintura) comboEsquema.getSelectedItem();

        isProgrammaticUpdate = true;
        comboEsquema.setModel(new DefaultComboBoxModel<>(esquemasFiltrados.toArray(new EsquemaPintura[0])));
        comboEsquema.setSelectedIndex(-1);

        if (seleccionPrevia != null) {
            for (int i = 0; i < comboEsquema.getItemCount(); i++) {
                if (comboEsquema.getItemAt(i).getIdEsquemaPintura() == seleccionPrevia.getIdEsquemaPintura()) {
                    comboEsquema.setSelectedIndex(i);
                    break;
                }
            }
        }
        isProgrammaticUpdate = false;
    }


    private void guardar() {
        TipoVehiculo tipo = (TipoVehiculo) comboTipoVehiculo.getSelectedItem();
        Pais pais = (Pais) comboPais.getSelectedItem();
        Epoca epoca = (Epoca) comboEpoca.getSelectedItem();
        EsquemaPintura esquema = (EsquemaPintura) comboEsquema.getSelectedItem();
        Operadora operadora = (Operadora) comboOperadora.getSelectedItem();

        Integer idTipoVehiculo = tipo != null ? tipo.getIdTipoVehiculo() : null;
        Integer idPais = pais != null ? pais.getIdPais() : null;
        Integer idEpoca = epoca != null ? epoca.getIdEpoca() : null;
        Integer idEsquema = esquema != null ? esquema.getIdEsquemaPintura() : null;
        Integer idOperadora = operadora != null ? operadora.getIdOperadora() : null;

        Integer velocidadMaxima = null;
        if (!txtVelocidadMaxima.getText().isEmpty()) {
            try {
                velocidadMaxima = Integer.parseInt(txtVelocidadMaxima.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "La Velocidad Máxima debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (vehiculoExistente == null) {
            vehiculoRealService.crearVehiculoReal(txtNombre.getText(), txtApodo.getText(), txtNumeracion.getText(), txtUid.getText(), idTipoVehiculo, idPais, idEpoca, idEsquema, idOperadora, txtFechaFabricacion.getText(), txtFechaBaja.getText(), txtFechaInicioPintura.getText(), txtFechaFinalPintura.getText(), txtDescripcionTecnica.getText(), velocidadMaxima);
        } else {
            vehiculoRealService.actualizarVehiculoReal(vehiculoExistente.getId(), txtNombre.getText(), txtApodo.getText(), txtNumeracion.getText(), txtUid.getText(), idTipoVehiculo, idPais, idEpoca, idEsquema, idOperadora, txtFechaFabricacion.getText(), txtFechaBaja.getText(), txtFechaInicioPintura.getText(), txtFechaFinalPintura.getText(), txtDescripcionTecnica.getText(), velocidadMaxima);
        }
        dispose();
    }
}
