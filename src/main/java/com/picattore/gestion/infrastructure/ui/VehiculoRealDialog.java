package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.*;
import com.picattore.gestion.domain.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class VehiculoRealDialog extends JDialog {

    private final VehiculoRealService vehiculoRealService;
    private final TipoVehiculoService tipoVehiculoService;
    private final PaisService paisService;
    private final EpocaService epocaService;
    private final EsquemaPinturaService esquemaService;
    private final OperadoraService operadoraService;
    private final IdiomaService idiomaService;
    private final VehiculoReal vehiculoExistente;

    private JTextField txtNumeracion, txtUid;
    private JComboBox<TipoVehiculo> comboTipoVehiculo;
    private JComboBox<Pais> comboPais;
    private JComboBox<Epoca> comboEpoca;
    private JComboBox<EsquemaPintura> comboEsquema;
    private JComboBox<Operadora> comboOperadora;

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

        this.setSize(500, 400);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        formPanel.add(new JLabel("Esquema Pintura:"));
        comboEsquema = new JComboBox<>();
        formPanel.add(comboEsquema);

        formPanel.add(new JLabel("Operadora:"));
        comboOperadora = new JComboBox<>();
        formPanel.add(comboOperadora);

        this.add(formPanel, BorderLayout.CENTER);

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
        comboTipoVehiculo.setModel(new DefaultComboBoxModel<>(tipoVehiculoService.obtenerTodosLosTiposVehiculo().toArray(new TipoVehiculo[0])));
        comboPais.setModel(new DefaultComboBoxModel<>(paisService.obtenerTodosLosPaises().toArray(new Pais[0])));
        comboEpoca.setModel(new DefaultComboBoxModel<>(epocaService.obtenerTodasLasEpocas().toArray(new Epoca[0])));
        comboEsquema.setModel(new DefaultComboBoxModel<>(esquemaService.obtenerTodosLosEsquemas().toArray(new EsquemaPintura[0])));
        comboOperadora.setModel(new DefaultComboBoxModel<>(operadoraService.obtenerTodasLasOperadoras().toArray(new Operadora[0])));

        comboTipoVehiculo.setSelectedIndex(-1);
        comboPais.setSelectedIndex(-1);
        comboEpoca.setSelectedIndex(-1);
        comboEsquema.setSelectedIndex(-1);
        comboOperadora.setSelectedIndex(-1);

        if (vehiculoExistente != null) {
            txtNumeracion.setText(vehiculoExistente.getNumeracion());
            txtUid.setText(vehiculoExistente.getUid());

            if (vehiculoExistente.getIdTipoVehiculo() != null) {
                for (int i = 0; i < comboTipoVehiculo.getItemCount(); i++) {
                    if (comboTipoVehiculo.getItemAt(i).getIdTipoVehiculo() == vehiculoExistente.getIdTipoVehiculo()) {
                        comboTipoVehiculo.setSelectedIndex(i);
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
            }
            if (vehiculoExistente.getIdEpoca() != null) {
                for (int i = 0; i < comboEpoca.getItemCount(); i++) {
                    if (comboEpoca.getItemAt(i).getIdEpoca() == vehiculoExistente.getIdEpoca()) {
                        comboEpoca.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (vehiculoExistente.getIdEsquemaPintura() != null) {
                for (int i = 0; i < comboEsquema.getItemCount(); i++) {
                    if (comboEsquema.getItemAt(i).getIdEsquemaPintura() == vehiculoExistente.getIdEsquemaPintura()) {
                        comboEsquema.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (vehiculoExistente.getIdOperadora() != null) {
                for (int i = 0; i < comboOperadora.getItemCount(); i++) {
                    if (comboOperadora.getItemAt(i).getIdOperadora() == vehiculoExistente.getIdOperadora()) {
                        comboOperadora.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
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

        if (vehiculoExistente == null) {
            vehiculoRealService.crearVehiculoReal(txtNumeracion.getText(), txtUid.getText(), idTipoVehiculo, idPais, idEpoca, idEsquema, idOperadora);
        } else {
            vehiculoRealService.actualizarVehiculoReal(vehiculoExistente.getId(), txtNumeracion.getText(), txtUid.getText(), idTipoVehiculo, idPais, idEpoca, idEsquema, idOperadora);
        }
        dispose();
    }
}
