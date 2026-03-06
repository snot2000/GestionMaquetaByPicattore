package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.OperadoraService;
import com.picattore.gestion.application.PaisService;
import com.picattore.gestion.domain.Operadora;
import com.picattore.gestion.domain.Pais;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class OperadoraDialog extends JDialog {

    private final OperadoraService operadoraService;
    private final PaisService paisService;
    private final Operadora operadoraExistente;

    private JTextField txtCodigo, txtNombre, txtAnioCreacion, txtAnioDisolucion;
    private JTextArea txtInformacion;
    private JList<Pais> listPaises;
    private JList<Operadora> listPredecesoras, listSucesoras;

    public OperadoraDialog(Frame owner, OperadoraService operadoraService, PaisService paisService, Operadora operadoraExistente) {
        super(owner, operadoraExistente == null ? "Nueva Operadora" : "Editar Operadora", true);
        this.operadoraService = operadoraService;
        this.paisService = paisService;
        this.operadoraExistente = operadoraExistente;

        this.setSize(800, 600);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Panel principal con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Pestaña de Datos Generales
        JPanel panelGeneral = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Código:"));
        txtCodigo = new JTextField();
        formPanel.add(txtCodigo);
        formPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Año Creación:"));
        txtAnioCreacion = new JTextField();
        formPanel.add(txtAnioCreacion);
        formPanel.add(new JLabel("Año Disolución:"));
        txtAnioDisolucion = new JTextField();
        formPanel.add(txtAnioDisolucion);
        panelGeneral.add(formPanel, BorderLayout.NORTH);

        txtInformacion = new JTextArea();
        panelGeneral.add(new JScrollPane(txtInformacion), BorderLayout.CENTER);
        tabbedPane.addTab("General", panelGeneral);

        // Pestaña de Países
        listPaises = new JList<>();
        tabbedPane.addTab("Países", new JScrollPane(listPaises));

        // Pestaña de Relaciones
        JPanel panelRelaciones = new JPanel(new GridLayout(1, 2));
        listPredecesoras = new JList<>();
        panelRelaciones.add(new JScrollPane(listPredecesoras));
        listSucesoras = new JList<>();
        panelRelaciones.add(new JScrollPane(listSucesoras));
        tabbedPane.addTab("Relaciones", panelRelaciones);

        this.add(tabbedPane, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardar());
        panelBotones.add(btnGuardar);
        this.add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        // Cargar listas de selección
        List<Pais> todosPaises = paisService.obtenerTodosLosPaises();
        listPaises.setListData(todosPaises.toArray(new Pais[0]));

        List<Operadora> todasOperadoras = operadoraService.obtenerTodasLasOperadoras();
        listPredecesoras.setListData(todasOperadoras.toArray(new Operadora[0]));
        listSucesoras.setListData(todasOperadoras.toArray(new Operadora[0]));

        if (operadoraExistente != null) {
            txtCodigo.setText(operadoraExistente.getCodigo());
            txtNombre.setText(operadoraExistente.getNombre());
            txtInformacion.setText(operadoraExistente.getInformacion());
            txtAnioCreacion.setText(operadoraExistente.getAnioCreacion() != null ? String.valueOf(operadoraExistente.getAnioCreacion()) : "");
            txtAnioDisolucion.setText(operadoraExistente.getAnioDisolucion() != null ? String.valueOf(operadoraExistente.getAnioDisolucion()) : "");

            // Seleccionar items en las listas
            listPaises.setSelectedIndices(getIndices(todosPaises, operadoraExistente.getPaisesIds()));
            listPredecesoras.setSelectedIndices(getIndices(todasOperadoras, operadoraExistente.getPredecesorasIds()));
            listSucesoras.setSelectedIndices(getIndices(todasOperadoras, operadoraExistente.getSucesorasIds()));
        }
    }

    private int[] getIndices(List<?> allItems, List<Integer> selectedIds) {
        return allItems.stream()
                .filter(item -> {
                    if (item instanceof Pais) return selectedIds.contains(((Pais) item).getIdPais());
                    if (item instanceof Operadora) return selectedIds.contains(((Operadora) item).getIdOperadora());
                    return false;
                })
                .mapToInt(allItems::indexOf)
                .toArray();
    }

    private void guardar() {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        Integer anioCreacion = txtAnioCreacion.getText().isEmpty() ? null : Integer.parseInt(txtAnioCreacion.getText());
        Integer anioDisolucion = txtAnioDisolucion.getText().isEmpty() ? null : Integer.parseInt(txtAnioDisolucion.getText());

        List<Integer> paisesIds = listPaises.getSelectedValuesList().stream().map(Pais::getIdPais).collect(Collectors.toList());
        List<Integer> predecesorasIds = listPredecesoras.getSelectedValuesList().stream().map(Operadora::getIdOperadora).collect(Collectors.toList());
        List<Integer> sucesorasIds = listSucesoras.getSelectedValuesList().stream().map(Operadora::getIdOperadora).collect(Collectors.toList());

        if (operadoraExistente == null) {
            operadoraService.crearOperadora(codigo, nombre, txtInformacion.getText(), anioCreacion, anioDisolucion, paisesIds, predecesorasIds, sucesorasIds);
        } else {
            operadoraService.actualizarOperadora(operadoraExistente.getIdOperadora(), codigo, nombre, txtInformacion.getText(), anioCreacion, anioDisolucion, paisesIds, predecesorasIds, sucesorasIds);
        }
        dispose();
    }
}
