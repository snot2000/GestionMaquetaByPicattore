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

public class ReferenciaModeloDialog extends JDialog {

    private final ReferenciaModeloService referenciaService;
    private final FabricanteService fabricanteService;
    private final VehiculoRealService vehiculoRealService;
    private final EscalaService escalaService;
    private final TipoVehiculoService tipoVehiculoService;
    private final PaisService paisService;
    private final EpocaService epocaService;
    private final EsquemaPinturaService esquemaService;
    private final OperadoraService operadoraService;
    private final IdiomaService idiomaService;
    private final ReferenciaModelo referenciaExistente;

    private JComboBox<Fabricante> comboFabricante;
    private JTextField txtReferencia;
    private JComboBox<VehiculoReal> comboVehiculoReal;
    private JComboBox<Escala> comboEscala;
    private JTextField txtFechaSalida;
    private JTextField txtFechaDescontinuado;

    // Campos de solo lectura para el Vehículo Real
    private JTextField txtVrNombre, txtVrApodo, txtVrNumeracion, txtVrUid, txtVrTipo, txtVrPais, txtVrEpoca, txtVrEsquema, txtVrOperadora;

    public ReferenciaModeloDialog(Frame owner, ReferenciaModeloService referenciaService, FabricanteService fabricanteService, VehiculoRealService vehiculoRealService, EscalaService escalaService, TipoVehiculoService tipoVehiculoService, PaisService paisService, EpocaService epocaService, EsquemaPinturaService esquemaService, OperadoraService operadoraService, IdiomaService idiomaService, ReferenciaModelo referenciaExistente) {
        super(owner, referenciaExistente == null ? "Nueva Referencia" : "Editar Referencia", true);
        this.referenciaService = referenciaService;
        this.fabricanteService = fabricanteService;
        this.vehiculoRealService = vehiculoRealService;
        this.escalaService = escalaService;
        this.tipoVehiculoService = tipoVehiculoService;
        this.paisService = paisService;
        this.epocaService = epocaService;
        this.esquemaService = esquemaService;
        this.operadoraService = operadoraService;
        this.idiomaService = idiomaService;
        this.referenciaExistente = referenciaExistente;

        this.setSize(800, 600);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Referencia ---
        JPanel panelRef = new JPanel(new GridLayout(0, 2, 5, 5));
        panelRef.setBorder(new TitledBorder("Datos de la Referencia"));

        panelRef.add(new JLabel("Fabricante:"));
        comboFabricante = new JComboBox<>();
        panelRef.add(comboFabricante);

        panelRef.add(new JLabel("Referencia:"));
        txtReferencia = new JTextField();
        panelRef.add(txtReferencia);

        panelRef.add(new JLabel("Escala:"));
        comboEscala = new JComboBox<>();
        comboEscala.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Escala) {
                    Escala escala = (Escala) value;
                    setText(escala.getCodigo() + " (" + escala.getEscala() + ")");
                }
                return this;
            }
        });
        panelRef.add(comboEscala);

        panelRef.add(new JLabel("Fecha Salida:"));
        txtFechaSalida = new JTextField();
        panelRef.add(txtFechaSalida);

        panelRef.add(new JLabel("Fecha Descontinuado:"));
        txtFechaDescontinuado = new JTextField();
        panelRef.add(txtFechaDescontinuado);

        panelRef.add(new JLabel("Vehículo Real (ID - Num):"));
        comboVehiculoReal = new JComboBox<>();
        comboVehiculoReal.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof VehiculoReal) {
                    VehiculoReal vr = (VehiculoReal) value;
                    setText(vr.getId() + " - " + vr.getNumeracion() + " (" + vr.getNombre() + ")");
                }
                return this;
            }
        });
        panelRef.add(comboVehiculoReal);

        mainPanel.add(panelRef);
        mainPanel.add(Box.createVerticalStrut(10));

        // --- Panel Vehículo Real (Solo Lectura) ---
        JPanel panelVR = new JPanel(new GridLayout(0, 2, 5, 5));
        panelVR.setBorder(new TitledBorder("Detalles del Vehículo Real Seleccionado"));

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

        panelVR.add(new JLabel("Esquema:"));
        txtVrEsquema = new JTextField(); txtVrEsquema.setEditable(false);
        panelVR.add(txtVrEsquema);

        panelVR.add(new JLabel("Operadora:"));
        txtVrOperadora = new JTextField(); txtVrOperadora.setEditable(false);
        panelVR.add(txtVrOperadora);

        mainPanel.add(panelVR);

        this.add(mainPanel, BorderLayout.CENTER);

        // --- Botones ---
        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        this.add(panelBotones, BorderLayout.SOUTH);

        // Listener para actualizar detalles del VR
        comboVehiculoReal.addActionListener(e -> actualizarDetallesVehiculoReal());
    }

    private void cargarDatos() {
        comboFabricante.setModel(new DefaultComboBoxModel<>(fabricanteService.obtenerTodosLosFabricantes().toArray(new Fabricante[0])));
        comboEscala.setModel(new DefaultComboBoxModel<>(escalaService.obtenerTodasLasEscalas().toArray(new Escala[0])));
        comboVehiculoReal.setModel(new DefaultComboBoxModel<>(vehiculoRealService.obtenerTodosLosVehiculosReales().toArray(new VehiculoReal[0])));

        comboFabricante.setSelectedIndex(-1);
        comboEscala.setSelectedIndex(-1);
        comboVehiculoReal.setSelectedIndex(-1);

        if (referenciaExistente != null) {
            txtReferencia.setText(referenciaExistente.getReferencia());
            txtFechaSalida.setText(referenciaExistente.getFechaSalida());
            txtFechaDescontinuado.setText(referenciaExistente.getFechaDescontinuado());

            if (referenciaExistente.getIdFabricante() != null) {
                for (int i = 0; i < comboFabricante.getItemCount(); i++) {
                    if (comboFabricante.getItemAt(i).getIdFabricante() == referenciaExistente.getIdFabricante()) {
                        comboFabricante.setSelectedIndex(i);
                        break;
                    }
                }
            }

            if (referenciaExistente.getIdEscala() != null) {
                for (int i = 0; i < comboEscala.getItemCount(); i++) {
                    if (comboEscala.getItemAt(i).getIdEscala() == referenciaExistente.getIdEscala()) {
                        comboEscala.setSelectedIndex(i);
                        break;
                    }
                }
            }

            if (referenciaExistente.getIdVehiculoReal() != null) {
                for (int i = 0; i < comboVehiculoReal.getItemCount(); i++) {
                    if (comboVehiculoReal.getItemAt(i).getId() == referenciaExistente.getIdVehiculoReal()) {
                        comboVehiculoReal.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void actualizarDetallesVehiculoReal() {
        VehiculoReal vr = (VehiculoReal) comboVehiculoReal.getSelectedItem();
        if (vr == null) {
            txtVrNombre.setText("");
            txtVrApodo.setText("");
            txtVrNumeracion.setText("");
            txtVrUid.setText("");
            txtVrTipo.setText("");
            txtVrPais.setText("");
            txtVrEpoca.setText("");
            txtVrEsquema.setText("");
            txtVrOperadora.setText("");
            return;
        }

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

    private void guardar() {
        Fabricante fabricante = (Fabricante) comboFabricante.getSelectedItem();
        Escala escala = (Escala) comboEscala.getSelectedItem();
        VehiculoReal vr = (VehiculoReal) comboVehiculoReal.getSelectedItem();

        Integer idFabricante = fabricante != null ? fabricante.getIdFabricante() : null;
        Integer idEscala = escala != null ? escala.getIdEscala() : null;
        Integer idVr = vr != null ? vr.getId() : null;

        if (referenciaExistente == null) {
            referenciaService.crearReferencia(idFabricante, txtReferencia.getText(), idVr, idEscala, txtFechaSalida.getText(), txtFechaDescontinuado.getText());
        } else {
            referenciaService.actualizarReferencia(referenciaExistente.getId(), idFabricante, txtReferencia.getText(), idVr, idEscala, txtFechaSalida.getText(), txtFechaDescontinuado.getText());
        }
        dispose();
    }
}
