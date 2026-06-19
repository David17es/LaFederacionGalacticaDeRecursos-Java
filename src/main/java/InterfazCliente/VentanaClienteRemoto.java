package InterfazCliente;

import Remoto.EstadoRecursosDTO;
import Remoto.EstadoZonasDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal del cliente remoto
 *
 * Contiene:
 *   - PanelEstadoRemoto: estado de depositos y actores en tiempo real
 *   - Botones: pausar y reanudar la simulacion
 *   - Barra de estado: mensajes de conexion y errores RMI
 *
 * El controlador actualiza esta ventana
 */
public class VentanaClienteRemoto extends JFrame {

    private PanelEstadoRemoto panelEstado;
    private ClienteRemotoControlador controlador;

    //Botones
    private JButton btnPausar;
    private JButton btnReanudar;

    //Barra de estado inferior
    private JLabel lblBarraEstado;

    public VentanaClienteRemoto() {
        super("Monitor Remoto — Federación Galáctica de Recursos");
        inicializarComponentes();
        inicializarControlador();
        configurarVentana();
    }

    // =========================================================
    // INICIALIZACION
    // =========================================================

    private void inicializarComponentes() {
        setLayout(new BorderLayout(8, 8));

        // Panel principal de estado
        panelEstado = new PanelEstadoRemoto();
        add(panelEstado, BorderLayout.CENTER);

        // Panel de botones
        add(crearPanelBotones(), BorderLayout.SOUTH);

        // Barra de estado
        lblBarraEstado = new JLabel(" Conectando al servidor...");
        lblBarraEstado.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(3, 8, 3, 8))
        );
        lblBarraEstado.setFont(lblBarraEstado.getFont().deriveFont(11f));
        lblBarraEstado.setForeground(Color.DARK_GRAY);
        add(lblBarraEstado, BorderLayout.NORTH);
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        btnPausar = new JButton("⏸  Pausar simulacion");
        btnPausar.setBackground(new Color(220, 160, 40));
        btnPausar.setForeground(Color.WHITE);
        btnPausar.setFocusPainted(false);
        btnPausar.setPreferredSize(new Dimension(200, 36));
        btnPausar.addActionListener(e -> controlador.pausarRemoto());

        btnReanudar = new JButton("▶  Reanudar simulación");
        btnReanudar.setBackground(new Color(40, 160, 80));
        btnReanudar.setForeground(Color.WHITE);
        btnReanudar.setFocusPainted(false);
        btnReanudar.setPreferredSize(new Dimension(200, 36));
        btnReanudar.addActionListener(e -> controlador.reanudarRemoto());

        panel.add(btnPausar);
        panel.add(btnReanudar);
        return panel;
    }

    private void inicializarControlador() {
        controlador = new ClienteRemotoControlador(this);
        controlador.start();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controlador.detener();
                dispose();
            }
        });
        setSize(480, 380);
        setMinimumSize(new Dimension(420, 340));
        setLocationRelativeTo(null);
        setResizable(true);
    }

    // =========================================================
    // METODOS PUBLICOS (llamados desde ClienteRemotoControlador)
    // =========================================================

    //Actualiza todos los paneles con los datos recibidos del servidor
    public void actualizarEstado(EstadoRecursosDTO recursos, EstadoZonasDTO zonas) {
        panelEstado.actualizarDatos(recursos, zonas);

        // Sincronizar estado de botones con el estado real
        btnPausar.setEnabled(!zonas.isSimulacionPausada());
        btnReanudar.setEnabled(zonas.isSimulacionPausada());
    }

    //Muestra mensaje informativo en la barra de estado
    public void mostrarMensajeConexion(String mensaje) {
        lblBarraEstado.setText(" ✔  " + mensaje);
        lblBarraEstado.setForeground(new Color(0, 130, 60));
    }

    //Muestra mensaje de error en la barra de estado
    public void mostrarErrorConexion(String mensaje) {
        lblBarraEstado.setText(" ✖  " + mensaje);
        lblBarraEstado.setForeground(new Color(190, 40, 40));
        btnPausar.setEnabled(false);
        btnReanudar.setEnabled(false);
    }
}