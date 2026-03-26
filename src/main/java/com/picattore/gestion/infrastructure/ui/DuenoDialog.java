package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.DuenoService;
import com.picattore.gestion.domain.Dueno;

import javax.swing.*;
import java.awt.*;

public class DuenoDialog extends JDialog {

    private final DuenoService duenoService;
    private final Dueno duenoExistente;

    private JTextField txtNombre;

    public DuenoDialog(Frame owner, DuenoService duenoService, Dueno duenoExistente) {
        super(owner, duenoExistente == null ? "Nuevo Dueño" : "Editar Dueño", true);
        this.duenoService = duenoService;
        this.duenoExistente = duenoExistente;

        this.setSize(300, 150);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JPanel panelDatos = new JPanel(new GridLayout(1, 2, 5, 5));
        panelDatos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelDatos.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelDatos.add(txtNombre);

        this.add(panelDatos, BorderLayout.CENTER);

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
        if (duenoExistente != null) {
            txtNombre.setText(duenoExistente.getNombre());
        }
    }

    private void guardar() {
        String nombre = txtNombre.getText();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (duenoExistente == null) {
            duenoService.crearDueno(nombre);
        } else {
            duenoService.actualizarDueno(duenoExistente.getId(), nombre);
        }

        dispose();
    }
}
