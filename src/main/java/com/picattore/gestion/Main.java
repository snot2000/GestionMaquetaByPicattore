package com.picattore.gestion;

import com.picattore.gestion.infrastructure.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Ejecutar la interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame();
            }
        });
    }
}
