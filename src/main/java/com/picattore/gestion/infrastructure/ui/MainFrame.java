package com.picattore.gestion.infrastructure.ui;

import com.picattore.gestion.application.*;
import com.picattore.gestion.domain.Idioma;
import com.picattore.gestion.infrastructure.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.Optional;

public class MainFrame extends JFrame {

    private JDesktopPane desktopPane;
    private IdiomaService idiomaService;
    private EpocaService epocaService;
    private PaisService paisService;
    private EscalaService escalaService;
    private TipoVehiculoService tipoVehiculoService;
    private OperadoraService operadoraService;
    private EsquemaPinturaService esquemaPinturaService;
    private FabricanteService fabricanteService;
    private TipoModeloService tipoModeloService;
    private VehiculoRealService vehiculoRealService;
    private ReferenciaModeloService referenciaModeloService;
    private DecoderService decoderService;
    private DuenoService duenoService;
    private ModeloService modeloService;
    private JMenu menuIdioma;

    public MainFrame() {
        super("Gestión de Aplicación");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa

        // Inicializar servicios
        SqliteIdiomaRepository idiomaRepository = new SqliteIdiomaRepository();
        idiomaService = new IdiomaService(idiomaRepository);

        SqliteEpocaRepository epocaRepository = new SqliteEpocaRepository();
        epocaService = new EpocaService(epocaRepository);

        SqlitePaisRepository paisRepository = new SqlitePaisRepository();
        paisService = new PaisService(paisRepository);

        SqliteEscalaRepository escalaRepository = new SqliteEscalaRepository();
        escalaService = new EscalaService(escalaRepository);

        SqliteTipoVehiculoRepository tipoVehiculoRepository = new SqliteTipoVehiculoRepository();
        tipoVehiculoService = new TipoVehiculoService(tipoVehiculoRepository);

        SqliteOperadoraRepository operadoraRepository = new SqliteOperadoraRepository();
        operadoraService = new OperadoraService(operadoraRepository);

        SqliteEsquemaPinturaRepository esquemaPinturaRepository = new SqliteEsquemaPinturaRepository();
        esquemaPinturaService = new EsquemaPinturaService(esquemaPinturaRepository);

        SqliteFabricanteRepository fabricanteRepository = new SqliteFabricanteRepository();
        fabricanteService = new FabricanteService(fabricanteRepository);

        SqliteTipoModeloRepository tipoModeloRepository = new SqliteTipoModeloRepository();
        tipoModeloService = new TipoModeloService(tipoModeloRepository);

        SqliteVehiculoRealRepository vehiculoRealRepository = new SqliteVehiculoRealRepository();
        vehiculoRealService = new VehiculoRealService(vehiculoRealRepository);

        SqliteReferenciaModeloRepository referenciaModeloRepository = new SqliteReferenciaModeloRepository();
        referenciaModeloService = new ReferenciaModeloService(referenciaModeloRepository);

        SqliteDecoderRepository decoderRepository = new SqliteDecoderRepository();
        decoderService = new DecoderService(decoderRepository);

        SqliteDuenoRepository duenoRepository = new SqliteDuenoRepository();
        duenoService = new DuenoService(duenoRepository);

        SqliteModeloRepository modeloRepository = new SqliteModeloRepository();
        modeloService = new ModeloService(modeloRepository);

        // Configurar el panel de escritorio
        desktopPane = new JDesktopPane();
        this.add(desktopPane, BorderLayout.CENTER);

        // Crear menú
        crearMenu();

        this.setVisible(true);
    }

    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Menú Material Real
        JMenu menuMaterialReal = new JMenu("Material Real");
        JMenuItem itemModelos = new JMenuItem("Modelos");
        itemModelos.addActionListener(e -> abrirVentanaModelos());
        menuMaterialReal.add(itemModelos);

        JMenuItem itemVehiculosReales = new JMenuItem("Vehículos reales");
        itemVehiculosReales.addActionListener(e -> abrirVentanaVehiculosReales());
        menuMaterialReal.add(itemVehiculosReales);
        
        JMenuItem itemReferencias = new JMenuItem("Referencias Modelos");
        itemReferencias.addActionListener(e -> abrirVentanaReferencias());
        menuMaterialReal.add(itemReferencias);

        JMenuItem itemDecoders = new JMenuItem("Decoders");
        itemDecoders.addActionListener(e -> abrirVentanaDecoders());
        menuMaterialReal.add(itemDecoders);
        
        menuBar.add(menuMaterialReal);

        // Menú Datos
        JMenu menuDatos = new JMenu("Datos");
        
        JMenuItem itemOperadoras = new JMenuItem("Operadoras");
        itemOperadoras.addActionListener(e -> abrirVentanaOperadoras());
        menuDatos.add(itemOperadoras);

        JMenuItem itemEsquemas = new JMenuItem("Esquemas de Pintura");
        itemEsquemas.addActionListener(e -> abrirVentanaEsquemas());
        menuDatos.add(itemEsquemas);

        JMenuItem itemFabricantes = new JMenuItem("Fabricantes");
        itemFabricantes.addActionListener(e -> abrirVentanaFabricantes());
        menuDatos.add(itemFabricantes);

        JMenuItem itemTiposModelo = new JMenuItem("Tipos de Modelo");
        itemTiposModelo.addActionListener(e -> abrirVentanaTiposModelo());
        menuDatos.add(itemTiposModelo);

        menuBar.add(menuDatos);

        // Menú Configuración
        JMenu menuConfiguracion = new JMenu("Configuración");
        
        JMenuItem itemDuenos = new JMenuItem("Dueños");
        itemDuenos.addActionListener(e -> abrirVentanaDuenos());
        menuConfiguracion.add(itemDuenos);

        JMenuItem itemIdiomas = new JMenuItem("Idiomas");
        itemIdiomas.addActionListener(e -> abrirVentanaIdiomas());
        menuConfiguracion.add(itemIdiomas);

        JMenuItem itemEpocas = new JMenuItem("Épocas");
        itemEpocas.addActionListener(e -> abrirVentanaEpocas());
        menuConfiguracion.add(itemEpocas);

        JMenuItem itemPaises = new JMenuItem("Países");
        itemPaises.addActionListener(e -> abrirVentanaPaises());
        menuConfiguracion.add(itemPaises);

        JMenuItem itemEscalas = new JMenuItem("Escalas");
        itemEscalas.addActionListener(e -> abrirVentanaEscalas());
        menuConfiguracion.add(itemEscalas);

        JMenuItem itemTiposVehiculo = new JMenuItem("Tipos de Vehículo");
        itemTiposVehiculo.addActionListener(e -> abrirVentanaTiposVehiculo());
        menuConfiguracion.add(itemTiposVehiculo);

        menuBar.add(menuConfiguracion);

        // Añadir espacio flexible para empujar el idioma a la derecha
        menuBar.add(Box.createHorizontalGlue());

        // Menú de Idioma Principal
        menuIdioma = new JMenu("Idioma");
        menuBar.add(menuIdioma);
        actualizarMenuIdiomas();

        this.setJMenuBar(menuBar);
    }

    public void actualizarMenuIdiomas() {
        menuIdioma.removeAll();
        ButtonGroup group = new ButtonGroup();
        List<Idioma> idiomas = idiomaService.obtenerTodosLosIdiomas();
        Optional<Idioma> idiomaPrincipal = idiomaService.obtenerIdiomaPrincipal();

        for (Idioma idioma : idiomas) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(idioma.getNombre() + " (" + idioma.getCodigo() + ")");
            if (idioma.isPrincipal()) {
                item.setSelected(true);
                menuIdioma.setText("Idioma: " + idioma.getCodigo());
            }
            
            item.addActionListener(e -> cambiarIdiomaPrincipal(idioma));
            group.add(item);
            menuIdioma.add(item);
        }
        
        if (!idiomaPrincipal.isPresent()) {
            menuIdioma.setText("Idioma: -");
        }
    }

    private void cambiarIdiomaPrincipal(Idioma nuevoPrincipal) {
        List<Idioma> idiomas = idiomaService.obtenerTodosLosIdiomas();
        for (Idioma idioma : idiomas) {
            boolean esPrincipal = (idioma.getId() == nuevoPrincipal.getId());
            if (idioma.isPrincipal() != esPrincipal) {
                idiomaService.actualizarIdioma(idioma.getId(), idioma.getCodigo(), idioma.getNombre(), esPrincipal);
            }
        }
        
        menuIdioma.setText("Idioma: " + nuevoPrincipal.getCodigo());
        notificarCambioIdioma();
    }

    private void notificarCambioIdioma() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof LanguageChangeListener) {
                ((LanguageChangeListener) frame).onLanguageChanged();
            }
        }
        actualizarMenuIdiomas();
    }

    private void abrirVentanaIdiomas() {
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

        IdiomaInternalFrame idiomaFrame = new IdiomaInternalFrame(idiomaService, this::actualizarMenuIdiomas);
        desktopPane.add(idiomaFrame);
        idiomaFrame.setVisible(true);
        try {
            idiomaFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaEpocas() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof EpocaInternalFrame) {
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        EpocaInternalFrame epocaFrame = new EpocaInternalFrame(epocaService, idiomaService);
        desktopPane.add(epocaFrame);
        epocaFrame.setVisible(true);
        try {
            epocaFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaPaises() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof PaisInternalFrame) {
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        PaisInternalFrame paisFrame = new PaisInternalFrame(paisService, idiomaService);
        desktopPane.add(paisFrame);
        paisFrame.setVisible(true);
        try {
            paisFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaEscalas() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof EscalaInternalFrame) {
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        EscalaInternalFrame escalaFrame = new EscalaInternalFrame(escalaService);
        desktopPane.add(escalaFrame);
        escalaFrame.setVisible(true);
        try {
            escalaFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaTiposVehiculo() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof TipoVehiculoInternalFrame) {
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        TipoVehiculoInternalFrame tipoVehiculoFrame = new TipoVehiculoInternalFrame(tipoVehiculoService, idiomaService);
        desktopPane.add(tipoVehiculoFrame);
        tipoVehiculoFrame.setVisible(true);
        try {
            tipoVehiculoFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaOperadoras() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof OperadoraInternalFrame) {
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        OperadoraInternalFrame operadoraFrame = new OperadoraInternalFrame(operadoraService, paisService, idiomaService);
        desktopPane.add(operadoraFrame);
        operadoraFrame.setVisible(true);
        try {
            operadoraFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaEsquemas() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof EsquemaPinturaInternalFrame) {
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        EsquemaPinturaInternalFrame esquemaFrame = new EsquemaPinturaInternalFrame(esquemaPinturaService, paisService, operadoraService, idiomaService);
        desktopPane.add(esquemaFrame);
        esquemaFrame.setVisible(true);
        try {
            esquemaFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaFabricantes() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof FabricanteInternalFrame) {
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        FabricanteInternalFrame fabricanteFrame = new FabricanteInternalFrame(fabricanteService, paisService, idiomaService);
        desktopPane.add(fabricanteFrame);
        fabricanteFrame.setVisible(true);
        try {
            fabricanteFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaTiposModelo() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof TipoModeloInternalFrame) {
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        TipoModeloInternalFrame tipoModeloFrame = new TipoModeloInternalFrame(tipoModeloService, idiomaService);
        desktopPane.add(tipoModeloFrame);
        tipoModeloFrame.setVisible(true);
        try {
            tipoModeloFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaVehiculosReales() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof VehiculoRealInternalFrame) {
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        VehiculoRealInternalFrame vehiculoFrame = new VehiculoRealInternalFrame(vehiculoRealService, tipoVehiculoService, paisService, epocaService, esquemaPinturaService, operadoraService, idiomaService);
        desktopPane.add(vehiculoFrame);
        vehiculoFrame.setVisible(true);
        try {
            vehiculoFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaReferencias() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof ReferenciaModeloInternalFrame) {
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        ReferenciaModeloInternalFrame refFrame = new ReferenciaModeloInternalFrame(referenciaModeloService, fabricanteService, vehiculoRealService, escalaService, tipoVehiculoService, paisService, epocaService, esquemaPinturaService, operadoraService, idiomaService);
        desktopPane.add(refFrame);
        refFrame.setVisible(true);
        try {
            refFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaDecoders() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof DecoderInternalFrame) {
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        DecoderInternalFrame decoderFrame = new DecoderInternalFrame(decoderService, fabricanteService);
        desktopPane.add(decoderFrame);
        decoderFrame.setVisible(true);
        try {
            decoderFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaDuenos() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof DuenoInternalFrame) {
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        DuenoInternalFrame duenoFrame = new DuenoInternalFrame(duenoService);
        desktopPane.add(duenoFrame);
        duenoFrame.setVisible(true);
        try {
            duenoFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaModelos() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof ModeloInternalFrame) {
                try {
                    frame.setSelected(true);
                    frame.setMaximum(true); // Se maximiza si ya está abierta
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        ModeloInternalFrame modeloFrame = new ModeloInternalFrame(modeloService, decoderService, referenciaModeloService, duenoService, fabricanteService, vehiculoRealService, escalaService, tipoVehiculoService, paisService, epocaService, esquemaPinturaService, operadoraService, idiomaService);
        desktopPane.add(modeloFrame);
        modeloFrame.setVisible(true);
        try {
            modeloFrame.setSelected(true);
            modeloFrame.setMaximum(true); // Se maximiza al abrirla por primera vez
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }
}
