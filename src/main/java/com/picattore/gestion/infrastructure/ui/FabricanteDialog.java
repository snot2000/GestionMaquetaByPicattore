package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.FabricanteService;
import com.picattore.gestion.application.PaisService;
import com.picattore.gestion.application.IdiomaService;
import com.picattore.gestion.domain.Fabricante;
import com.picattore.gestion.domain.Pais;
import com.picattore.gestion.domain.PaisTr;
import com.picattore.gestion.domain.Idioma;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class FabricanteDialog extends JDialog {

    private final FabricanteService fabricanteService;
    private final PaisService paisService;
    private final IdiomaService idiomaService;
    private final Fabricante fabricanteExistente;

    private JTextField txtNombre, txtPaginaWeb, txtTelefono, txtEmail, txtFechaAlta, txtFechaBaja;
    private JTextArea txtDescripcion;
    private JComboBox<Pais> comboPais;

    public FabricanteDialog(Frame owner, FabricanteService fabricanteService, PaisService paisService, IdiomaService idiomaService, Fabricante fabricanteExistente) {
        super(owner, fabricanteExistente == null ? "Nuevo Fabricante" : "Editar Fabricante", true);
        this.fabricanteService = fabricanteService;
        this.paisService = paisService;
        this.idiomaService = idiomaService;
        this.fabricanteExistente = fabricanteExistente;

        this.setSize(600, 500);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        formPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        formPanel.add(txtNombre);

        formPanel.add(new JLabel("País:"));
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
        formPanel.add(comboPais);

        formPanel.add(new JLabel("Página Web:"));
        txtPaginaWeb = new JTextField();
        formPanel.add(txtPaginaWeb);

        formPanel.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        formPanel.add(txtTelefono);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Fecha Alta:"));
        txtFechaAlta = new JTextField();
        formPanel.add(txtFechaAlta);

        formPanel.add(new JLabel("Fecha Baja:"));
        txtFechaBaja = new JTextField();
        formPanel.add(txtFechaBaja);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        txtDescripcion = new JTextArea(5, 20);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setBorder(BorderFactory.createTitledBorder("Descripción"));
        mainPanel.add(scrollDesc);

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
        List<Pais> paises = paisService.obtenerTodosLosPaises();
        comboPais.setModel(new DefaultComboBoxModel<>(paises.toArray(new Pais[0])));
        comboPais.setSelectedIndex(-1);

        if (fabricanteExistente != null) {
            txtNombre.setText(fabricanteExistente.getNombre());
            txtDescripcion.setText(fabricanteExistente.getDescripcion());
            txtPaginaWeb.setText(fabricanteExistente.getPaginaWeb());
            txtTelefono.setText(fabricanteExistente.getTelefono());
            txtEmail.setText(fabricanteExistente.getEmail());
            txtFechaAlta.setText(fabricanteExistente.getFechaAlta());
            txtFechaBaja.setText(fabricanteExistente.getFechaBaja());

            if (fabricanteExistente.getIdPais() != null) {
                for (int i = 0; i < comboPais.getItemCount(); i++) {
                    if (comboPais.getItemAt(i).getIdPais() == fabricanteExistente.getIdPais()) {
                        comboPais.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void guardar() {
        String nombre = txtNombre.getText();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Pais pais = (Pais) comboPais.getSelectedItem();
        Integer idPais = pais != null ? pais.getIdPais() : null;

        if (fabricanteExistente == null) {
            fabricanteService.crearFabricante(nombre, txtDescripcion.getText(), idPais, txtPaginaWeb.getText(), txtTelefono.getText(), txtEmail.getText(), txtFechaAlta.getText(), txtFechaBaja.getText());
        } else {
            fabricanteService.actualizarFabricante(fabricanteExistente.getIdFabricante(), nombre, txtDescripcion.getText(), idPais, txtPaginaWeb.getText(), txtTelefono.getText(), txtEmail.getText(), txtFechaAlta.getText(), txtFechaBaja.getText());
        }
        dispose();
    }
}
