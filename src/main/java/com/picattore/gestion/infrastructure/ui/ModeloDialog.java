package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.*;
import com.picattore.gestion.domain.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class ModeloDialog extends JDialog {

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
    private final Modelo modeloExistente;

    private JComboBox<Dueno> comboDueno;
    private JComboBox<Decoder> comboDecoder;
    private JComboBox<ReferenciaModelo> comboReferencia;

    // Campos de detalle de Referencia Modelo
    private JTextField txtRefFabricante, txtRefEscala, txtRefFechaS, txtRefFechaD;
    
    // Campos de detalle de Vehículo Real
    private JTextField txtVrNombre, txtVrApodo, txtVrNumeracion, txtVrUid, txtVrTipo, txtVrPais, txtVrEpoca, txtVrEsquema, txtVrOperadora;

    public ModeloDialog(Frame owner, ModeloService modeloService, DecoderService decoderService, ReferenciaModeloService referenciaModeloService, DuenoService duenoService, FabricanteService fabricanteService, VehiculoRealService vehiculoRealService, EscalaService escalaService, TipoVehiculoService tipoVehiculoService, PaisService paisService, EpocaService epocaService, EsquemaPinturaService esquemaService, OperadoraService operadoraService, IdiomaService idiomaService, Modelo modeloExistente) {
        super(owner, modeloExistente == null ? "Nuevo Modelo" : "Editar Modelo", true);
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
        this.modeloExistente = modeloExistente;

        this.setSize(900, 700);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Datos Básicos ---
        JPanel panelBasico = new JPanel(new GridLayout(0, 2, 5, 5));
        panelBasico.setBorder(new TitledBorder("Datos Básicos del Modelo"));

        panelBasico.add(new JLabel("Dueño:"));
        comboDueno = new JComboBox<>();
        comboDueno.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Dueno) {
                    setText(((Dueno) value).getNombre());
                }
                return this;
            }
        });
        panelBasico.add(comboDueno);

        panelBasico.add(new JLabel("Decoder (Dirección):"));
        comboDecoder = new JComboBox<>();
        comboDecoder.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Decoder) {
                    Decoder d = (Decoder) value;
                    setText(d.getId() + " - " + d.getDireccion());
                }
                return this;
            }
        });
        panelBasico.add(comboDecoder);

        panelBasico.add(new JLabel("Referencia Modelo:"));
        comboReferencia = new JComboBox<>();
        comboReferencia.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ReferenciaModelo) {
                    ReferenciaModelo r = (ReferenciaModelo) value;
                    setText(r.getId() + " - " + r.getReferencia());
                }
                return this;
            }
        });
        panelBasico.add(comboReferencia);

        mainPanel.add(panelBasico);
        mainPanel.add(Box.createVerticalStrut(10));

        // --- Panel Detalles Referencia ---
        JPanel panelRef = new JPanel(new GridLayout(0, 2, 5, 5));
        panelRef.setBorder(new TitledBorder("Detalles de la Referencia Seleccionada"));

        panelRef.add(new JLabel("Fabricante:"));
        txtRefFabricante = new JTextField(); txtRefFabricante.setEditable(false);
        panelRef.add(txtRefFabricante);

        panelRef.add(new JLabel("Escala:"));
        txtRefEscala = new JTextField(); txtRefEscala.setEditable(false);
        panelRef.add(txtRefEscala);

        panelRef.add(new JLabel("Fecha Salida:"));
        txtRefFechaS = new JTextField(); txtRefFechaS.setEditable(false);
        panelRef.add(txtRefFechaS);

        panelRef.add(new JLabel("Fecha Descontinuado:"));
        txtRefFechaD = new JTextField(); txtRefFechaD.setEditable(false);
        panelRef.add(txtRefFechaD);

        mainPanel.add(panelRef);
        mainPanel.add(Box.createVerticalStrut(10));

        // --- Panel Detalles Vehículo Real ---
        JPanel panelVR = new JPanel(new GridLayout(0, 2, 5, 5));
        panelVR.setBorder(new TitledBorder("Detalles del Vehículo Real"));

        panelVR.add(new JLabel("Nombre:"));
        txtVrNombre = new JTextField(); txtVrNombre.setEditable(false);
        panelVR.add(txtVrNombre);

        panelVR.add(new JLabel("Apodo:"));
        txtVrApodo = new JTextField(); txtVrApodo.setEditable(false);
        panelVR.add(txtVrApodo);

        panelVR.add(new JLabel("Numeración:"));
        txtVrNumeracion = new JTextField(); txtVrNumeracion.setEditable(false);
        panelVR.add(txtVrNumeracion);

        panelVR.add(new JLabel("UID:"));
        txtVrUid = new JTextField(); txtVrUid.setEditable(false);
        panelVR.add(txtVrUid);

        panelVR.add(new JLabel("Tipo:"));
        txtVrTipo = new JTextField(); txtVrTipo.setEditable(false);
        panelVR.add(txtVrTipo);

        panelVR.add(new JLabel("País:"));
        txtVrPais = new JTextField(); txtVrPais.setEditable(false);
        panelVR.add(txtVrPais);

        panelVR.add(new JLabel("Época:"));
        txtVrEpoca = new JTextField(); txtVrEpoca.setEditable(false);
        panelVR.add(txtVrEpoca);

        panelVR.add(new JLabel("Esquema Pintura:"));
        txtVrEsquema = new JTextField(); txtVrEsquema.setEditable(false);
        panelVR.add(txtVrEsquema);

        panelVR.add(new JLabel("Operadora:"));
        txtVrOperadora = new JTextField(); txtVrOperadora.setEditable(false);
        panelVR.add(txtVrOperadora);

        mainPanel.add(panelVR);

        this.add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // --- Botones ---
        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        this.add(panelBotones, BorderLayout.SOUTH);

        // Listener para actualizar detalles cuando cambia la referencia
        comboReferencia.addActionListener(e -> actualizarDetallesReferencia());
    }

    private void cargarDatos() {
        comboDueno.setModel(new DefaultComboBoxModel<>(duenoService.obtenerTodosLosDuenos().toArray(new Dueno[0])));
        comboDecoder.setModel(new DefaultComboBoxModel<>(decoderService.obtenerTodosLosDecoders().toArray(new Decoder[0])));
        comboReferencia.setModel(new DefaultComboBoxModel<>(referenciaModeloService.obtenerTodasLasReferencias().toArray(new ReferenciaModelo[0])));

        comboDueno.setSelectedIndex(-1);
        comboDecoder.setSelectedIndex(-1);
        comboReferencia.setSelectedIndex(-1);

        if (modeloExistente != null) {
            if (modeloExistente.getIdDueno() != null) {
                for (int i = 0; i < comboDueno.getItemCount(); i++) {
                    if (comboDueno.getItemAt(i).getId() == modeloExistente.getIdDueno()) {
                        comboDueno.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (modeloExistente.getIdDecoder() != null) {
                for (int i = 0; i < comboDecoder.getItemCount(); i++) {
                    if (comboDecoder.getItemAt(i).getId() == modeloExistente.getIdDecoder()) {
                        comboDecoder.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (modeloExistente.getIdReferenciaModelo() != null) {
                for (int i = 0; i < comboReferencia.getItemCount(); i++) {
                    if (comboReferencia.getItemAt(i).getId() == modeloExistente.getIdReferenciaModelo()) {
                        comboReferencia.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void actualizarDetallesReferencia() {
        ReferenciaModelo ref = (ReferenciaModelo) comboReferencia.getSelectedItem();
        if (ref == null) {
            limpiarDetalles();
            return;
        }

        txtRefFechaS.setText(ref.getFechaSalida());
        txtRefFechaD.setText(ref.getFechaDescontinuado());

        if (ref.getIdFabricante() != null) {
            fabricanteService.obtenerFabricantePorId(ref.getIdFabricante()).ifPresent(f -> txtRefFabricante.setText(f.getNombre()));
        } else { txtRefFabricante.setText(""); }

        if (ref.getIdEscala() != null) {
            escalaService.obtenerEscalaPorId(ref.getIdEscala()).ifPresent(e -> txtRefEscala.setText(e.getCodigo() + " (" + e.getEscala() + ")"));
        } else { txtRefEscala.setText(""); }

        if (ref.getIdVehiculoReal() != null) {
            vehiculoRealService.obtenerVehiculoRealPorId(ref.getIdVehiculoReal()).ifPresent(this::actualizarDetallesVehiculoReal);
        } else {
            limpiarDetallesVehiculoReal();
        }
    }

    private void actualizarDetallesVehiculoReal(VehiculoReal vr) {
        txtVrNombre.setText(vr.getNombre());
        txtVrApodo.setText(vr.getApodo());
        txtVrNumeracion.setText(vr.getNumeracion());
        txtVrUid.setText(vr.getUid());

        Optional<Idioma> idm = idiomaService.obtenerIdiomaPrincipal();
        int idIdm = idm.map(Idioma::getId).orElse(-1);

        if (vr.getIdTipoVehiculo() != null) {
            tipoVehiculoService.obtenerTipoVehiculoPorId(vr.getIdTipoVehiculo()).ifPresent(t -> {
                String n = t.getCodigo();
                if (idIdm != -1) {
                    for (TipoVehiculoTr tr : t.getTraducciones()) {
                        if (tr.getIdIdioma() == idIdm) { n = tr.getNombre(); break; }
                    }
                }
                txtVrTipo.setText(n);
            });
        } else { txtVrTipo.setText(""); }

        if (vr.getIdPais() != null) {
            paisService.obtenerPaisPorId(vr.getIdPais()).ifPresent(p -> {
                String n = p.getCodigo();
                if (idIdm != -1) {
                    for (PaisTr tr : p.getTraducciones()) {
                        if (tr.getIdIdioma() == idIdm) { n = tr.getNombre(); break; }
                    }
                }
                txtVrPais.setText(n);
            });
        } else { txtVrPais.setText(""); }

        if (vr.getIdEpoca() != null) {
            epocaService.obtenerEpocaPorId(vr.getIdEpoca()).ifPresent(e -> {
                String n = e.getCodigo();
                if (idIdm != -1) {
                    for (EpocaTr tr : e.getTraducciones()) {
                        if (tr.getIdIdioma() == idIdm) { n = tr.getNombre(); break; }
                    }
                }
                txtVrEpoca.setText(n);
            });
        } else { txtVrEpoca.setText(""); }

        if (vr.getIdEsquemaPintura() != null) {
            esquemaService.obtenerEsquemaPorId(vr.getIdEsquemaPintura()).ifPresent(eq -> txtVrEsquema.setText(eq.getNombre()));
        } else { txtVrEsquema.setText(""); }

        if (vr.getIdOperadora() != null) {
            operadoraService.obtenerOperadoraPorId(vr.getIdOperadora()).ifPresent(o -> txtVrOperadora.setText(o.getCodigo()));
        } else { txtVrOperadora.setText(""); }
    }

    private void limpiarDetalles() {
        txtRefFabricante.setText("");
        txtRefEscala.setText("");
        txtRefFechaS.setText("");
        txtRefFechaD.setText("");
        limpiarDetallesVehiculoReal();
    }

    private void limpiarDetallesVehiculoReal() {
        txtVrNombre.setText("");
        txtVrApodo.setText("");
        txtVrNumeracion.setText("");
        txtVrUid.setText("");
        txtVrTipo.setText("");
        txtVrPais.setText("");
        txtVrEpoca.setText("");
        txtVrEsquema.setText("");
        txtVrOperadora.setText("");
    }

    private void guardar() {
        Dueno dueno = (Dueno) comboDueno.getSelectedItem();
        Decoder decoder = (Decoder) comboDecoder.getSelectedItem();
        ReferenciaModelo ref = (ReferenciaModelo) comboReferencia.getSelectedItem();

        Integer idDueno = dueno != null ? dueno.getId() : null;
        Integer idDecoder = decoder != null ? decoder.getId() : null;
        Integer idRef = ref != null ? ref.getId() : null;

        if (modeloExistente == null) {
            modeloService.crearModelo(idDecoder, idRef, idDueno);
        } else {
            modeloService.actualizarModelo(modeloExistente.getId(), idDecoder, idRef, idDueno);
        }
        dispose();
    }
}
