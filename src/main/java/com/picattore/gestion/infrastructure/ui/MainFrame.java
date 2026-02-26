package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.IdiomaService;
import com.picattore.gestion.infrastructure.SqliteIdiomaRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private JDesktopPane desktopPane;
    private IdiomaService idiomaService;

    public MainFrame() {
        super("Gestión de Aplicación");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa

        // Inicializar servicios
        SqliteIdiomaRepository idiomaRepository = new SqliteIdiomaRepository();
        idiomaService = new IdiomaService(idiomaRepository);

        // Configurar el panel de escritorio
        desktopPane = new JDesktopPane();
        this.add(desktopPane, BorderLayout.CENTER);

        // Crear menú
        crearMenu();

        this.setVisible(true);
    }

    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Menú Configuración
        JMenu menuConfiguracion = new JMenu("Configuración");
        JMenuItem itemIdiomas = new JMenuItem("Idiomas");

        itemIdiomas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirVentanaIdiomas();
            }
        });

        menuConfiguracion.add(itemIdiomas);
        menuBar.add(menuConfiguracion);

        this.setJMenuBar(menuBar);
    }

    private void abrirVentanaIdiomas() {
        // Verificar si ya está abierta
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof IdiomaInternalFrame) {
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        IdiomaInternalFrame idiomaFrame = new IdiomaInternalFrame(idiomaService);
        desktopPane.add(idiomaFrame);
        idiomaFrame.setVisible(true);
        try {
            idiomaFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }
}
