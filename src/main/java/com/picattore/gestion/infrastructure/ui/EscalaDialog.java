package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.EscalaService;
import com.picattore.gestion.domain.Escala;

import javax.swing.*;
import java.awt.*;

public class EscalaDialog extends JDialog {

    private final EscalaService escalaService;
    private final Escala escalaExistente;

    private JTextField txtCodigo;
    private JTextField txtEscala;

    public EscalaDialog(Frame owner, EscalaService escalaService, Escala escalaExistente) {
        super(owner, escalaExistente == null ? "Nueva Escala" : "Editar Escala", true);
        this.escalaService = escalaService;
        this.escalaExistente = escalaExistente;

        this.setSize(400, 200);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Panel de datos
        JPanel panelDatos = new JPanel(new GridLayout(2, 2));
        panelDatos.add(new JLabel("Código:"));
        txtCodigo = new JTextField();
        panelDatos.add(txtCodigo);

        panelDatos.add(new JLabel("Escala:"));
        txtEscala = new JTextField();
        panelDatos.add(txtEscala);

        this.add(panelDatos, BorderLayout.CENTER);

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
        if (escalaExistente != null) {
            txtCodigo.setText(escalaExistente.getCodigo());
            txtEscala.setText(escalaExistente.getEscala());
        }
    }

    private void guardar() {
        String codigo = txtCodigo.getText();
        String escala = txtEscala.getText();

        if (codigo.isEmpty() || escala.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (escalaExistente == null) {
            escalaService.crearEscala(codigo, escala);
        } else {
            escalaService.actualizarEscala(escalaExistente.getIdEscala(), codigo, escala);
        }

        dispose();
    }
}
