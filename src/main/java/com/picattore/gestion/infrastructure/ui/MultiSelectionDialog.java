package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.domain.Operadora;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MultiSelectionDialog extends JDialog {

    private JList<Operadora> list;
    private List<Operadora> selectedItems;
    private boolean saved = false;

    public MultiSelectionDialog(Dialog owner, String title, List<Operadora> allItems, List<Operadora> currentSelection) {
        super(owner, title, true);
        this.setSize(400, 500);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(owner);

        // Lista con todos los elementos
        list = new JList<>(allItems.toArray(new Operadora[0]));
        
        // Configurar selección múltiple
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Marcar los elementos que ya estaban seleccionados
        int[] indices = allItems.stream()
                .filter(item -> currentSelection.stream().anyMatch(sel -> sel.getIdOperadora() == item.getIdOperadora()))
                .mapToInt(allItems::indexOf)
                .toArray();
        list.setSelectedIndices(indices);

        this.add(new JScrollPane(list), BorderLayout.CENTER);

        // Botones
        JPanel btnPanel = new JPanel();
        JButton btnOk = new JButton("Aceptar");
        JButton btnCancel = new JButton("Cancelar");

        btnOk.addActionListener(e -> {
            selectedItems = list.getSelectedValuesList();
            saved = true;
            dispose();
        });

        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnOk);
        btnPanel.add(btnCancel);
        this.add(btnPanel, BorderLayout.SOUTH);
    }

    public List<Operadora> getSelectedItems() {
        return saved ? selectedItems : null;
    }
}
