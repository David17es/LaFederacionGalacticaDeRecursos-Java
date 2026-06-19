package GUI;

import Concurrencia.ControlSimulacion;
import Concurrencia.SincronizacionRecursos;
import Log.LoggerFederacionImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//Ventana principal de la simulación del servidor
public class VentanaPrincipalServidor extends JFrame {

    private static final int INTERVALO_REFRESCO_MS = 500;

    // Paneles
    private PanelMapaGalaxia mapa;
    private PanelRecursos recursos;
    private PanelActores actores;
    private PanelLog log;

    // Control
    private final ControlSimulacion controlSimulacion;
    private final SincronizacionRecursos sincRecursos;
    private final LoggerFederacionImpl logger;

    // Botones
    private JButton btnPausar;
    private JButton btnReanudar;
    private JLabel lblEstado;

    // Timer de refresco
    private Timer timerRefresco;

    public VentanaPrincipalServidor(ControlSimulacion controlSimulacion, SincronizacionRecursos sincRecursos, LoggerFederacionImpl logger) {
        super("Federación Galáctica de Recursos — Servidor");
        this.controlSimulacion = controlSimulacion;
        this.sincRecursos = sincRecursos;
        this.logger = logger;

        inicializarComponentes();
        inicializarTimer();
        configurarVentana();
    }

    // =========================================================
    // INICIALIZACION
    // =========================================================

    private void inicializarComponentes() {
        setLayout(new BorderLayout(4, 4));

        // --- Barra superior de control ---
        add(crearPanelControl(), BorderLayout.NORTH);

        // --- Panel central: mapa + pestañas ---
        mapa = new PanelMapaGalaxia();
        recursos = new PanelRecursos(sincRecursos);
        actores = new PanelActores();
        log = new PanelLog();

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("📦 Recursos", recursos);
        pestanas.addTab("👥 Actores", actores);
        pestanas.addTab("📋 Log", log);
        pestanas.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapa, pestanas);
        split.setDividerLocation(0.55); //55% de espacio para el mapa
        split.setResizeWeight(0.55); //al redimensionar pestaña, crece en la misma proporcion
        split.setDividerSize(6);
        split.setBorder(new EmptyBorder(4, 4, 4, 4));
        
        setSize(1280, 780);
        setMinimumSize(new Dimension(900, 620));

        add(split, BorderLayout.CENTER);

        // --- Barra de estado inferior ---
        add(crearBarraEstado(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelControl() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        //Titulo
        JLabel titulo = new JLabel("🌌 Federación Galáctica de Recursos");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(titulo);

        //Separador
        panel.add(Box.createHorizontalStrut(20));

        //Boton pausar
        btnPausar = new JButton("⏸  Pausar");
        btnPausar.setBackground(new Color(220, 160, 40));
        btnPausar.setForeground(Color.WHITE);
        btnPausar.setFocusPainted(false);
        btnPausar.setPreferredSize(new Dimension(120, 30));
        btnPausar.addActionListener(e -> pausarSimulacion());
        panel.add(btnPausar);

        //Boton reanudar
        btnReanudar = new JButton("▶  Reanudar");
        btnReanudar.setBackground(new Color(40, 160, 80));
        btnReanudar.setForeground(Color.WHITE);
        btnReanudar.setFocusPainted(false);
        btnReanudar.setPreferredSize(new Dimension(120, 30));
        btnReanudar.setEnabled(false);
        btnReanudar.addActionListener(e -> reanudarSimulacion());
        panel.add(btnReanudar);

        //Etiqueta de estado
        lblEstado = new JLabel("● EN MARCHA");
        lblEstado.setForeground(new Color(0, 150, 60));
        lblEstado.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        panel.add(Box.createHorizontalStrut(16));
        panel.add(lblEstado);

        return panel;
    }

    private JPanel crearBarraEstado() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        JLabel lbl = new JLabel("Refresco cada " + INTERVALO_REFRESCO_MS + " ms  |" + "  Log → federaciongalactica.txt");
        lbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        lbl.setForeground(Color.GRAY);
        panel.add(lbl);
        return panel;
    }

    // =========================================================
    // TIMER DE REFRESCO
    // =========================================================

    private void inicializarTimer() {
        timerRefresco = new Timer(INTERVALO_REFRESCO_MS, e -> refrescarPaneles());
        timerRefresco.setCoalesce(true); // descarta ticks acumulados
        timerRefresco.start();
    }

    //Refresca todos los paneles con los datos mas recientes
    private void refrescarPaneles() {
        mapa.refrescar();
        recursos.refrescar();
        actores.refrescar();
    }

    // =========================================================
    // CONTROL DE SIMULACION
    // =========================================================

    private void pausarSimulacion() {
        controlSimulacion.pausar();
        btnPausar.setEnabled(false);
        btnReanudar.setEnabled(true);
        lblEstado.setText("● PAUSADA");
        lblEstado.setForeground(new Color(200, 130, 0));
        logger.registrarEvento("[GUI] Simulación pausada por el usuario.");
    }

    private void reanudarSimulacion() {
        controlSimulacion.reanudar();
        btnPausar.setEnabled(true);
        btnReanudar.setEnabled(false);
        lblEstado.setText("● EN MARCHA");
        lblEstado.setForeground(new Color(0, 150, 60));
        logger.registrarEvento("[GUI] Simulación reanudada por el usuario.");
    }

    // =========================================================
    // METODO PUBLICO PARA EL LOG
    // =========================================================

    //Añade una linea al PanelLog visual
    public void agregarLineaLog(String linea) {
        log.agregarLinea(linea);
    }

    // =========================================================
    // CONFIGURACION
    // =========================================================

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timerRefresco.stop();
                controlSimulacion.detener();
                dispose();
                System.exit(0);
            }
        });
        setSize(1024, 680);
        setMinimumSize(new Dimension(800, 560));
        setLocationRelativeTo(null);
        setResizable(true);
    }
}