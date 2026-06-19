package InterfazCliente;

import Remoto.EstadoRecursosDTO;
import Remoto.EstadoZonasDTO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Panel del cliente remoto
 * Muestra el estado de los depositos orbitales con barras de progreso y los contadores de actores activos en el sistema
 */
public class PanelEstadoRemoto extends JPanel {

    // --- Depositos: barras de progreso y etiquetas de texto ---
    private JProgressBar barCristal;
    private JProgressBar barMineral;
    private JProgressBar barPlasma;
    private JLabel lblCristalVal;
    private JLabel lblMineralVal;
    private JLabel lblPlasmaVal;

    // --- Contadores de actores ---
    private JLabel lblDelegados;
    private JLabel lblDelegadosActivos;
    private JLabel lblPatrullas;
    private JLabel lblSaqueadores;

    // --- Estado simulación ---
    private JLabel lblEstadoSim;

    public PanelEstadoRemoto() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(crearPanelDepositos(), BorderLayout.NORTH);
        add(crearPanelActores(),   BorderLayout.CENTER);
    }

    // =========================================================
    // CONSTRUCCION DE SUBPANELES
    // =========================================================

    private JPanel crearPanelDepositos() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Depósitos Orbitales"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Fila cabecera ---
        gbc.gridy = 0; gbc.gridx = 0;
        panel.add(new JLabel("Recurso"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("Nivel"), gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Unidades"), gbc);

        // --- Cristal ---
        barCristal = crearBarra(250, new Color(100, 200, 230));
        lblCristalVal = new JLabel("0 / 250");
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Cristal"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(barCristal, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(lblCristalVal, gbc);

        // --- Mineral ---
        barMineral = crearBarra(200, new Color(180, 140, 80));
        lblMineralVal = new JLabel("0 / 200");
        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Mineral"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(barMineral, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(lblMineralVal, gbc);

        // --- Plasma ---
        barPlasma = crearBarra(150, new Color(220, 100, 180));
        lblPlasmaVal = new JLabel("0 / 150");
        gbc.gridy = 3; gbc.gridx = 0;
        panel.add(new JLabel("Plasma"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(barPlasma, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(lblPlasmaVal, gbc);

        return panel;
    }

    private JPanel crearPanelActores() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Estado del Sistema"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.anchor = GridBagConstraints.WEST;

        //Delegados totales
        gbc.gridy = 0; gbc.gridx = 0;
        panel.add(new JLabel("Delegados totales:"), gbc);
        lblDelegados = new JLabel("—");
        lblDelegados.setFont(lblDelegados.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 1;
        panel.add(lblDelegados, gbc);

        //Delegados activos
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Delegados activos:"), gbc);
        lblDelegadosActivos = new JLabel("—");
        lblDelegadosActivos.setFont(lblDelegadosActivos.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 1;
        panel.add(lblDelegadosActivos, gbc);

        //Patrullas
        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Patrullas federales:"), gbc);
        lblPatrullas = new JLabel("—");
        lblPatrullas.setFont(lblPatrullas.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 1;
        panel.add(lblPatrullas, gbc);

        //Saqueadores
        gbc.gridy = 3; gbc.gridx = 0;
        panel.add(new JLabel("Saqueadores activos:"), gbc);
        lblSaqueadores = new JLabel("—");
        lblSaqueadores.setFont(lblSaqueadores.getFont().deriveFont(Font.BOLD, 13f));
        lblSaqueadores.setForeground(new Color(200, 50, 50));
        gbc.gridx = 1;
        panel.add(lblSaqueadores, gbc);

        //Estado simulacion
        gbc.gridy = 4; gbc.gridx = 0;
        panel.add(new JLabel("Estado simulación:"), gbc);
        lblEstadoSim = new JLabel("Desconocido");
        lblEstadoSim.setFont(lblEstadoSim.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 1;
        panel.add(lblEstadoSim, gbc);

        return panel;
    }

    // =========================================================
    // ACTUALIZACION DE DATOS
    // =========================================================

    //Actualiza todos los componentes con los datos recibidos del servidor RMI
    public void actualizarDatos(EstadoRecursosDTO recursos, EstadoZonasDTO zonas) {
        //Depositos
        barCristal.setMaximum(recursos.getCapacidadMaxCristal());
        barCristal.setValue(recursos.getUnidadesCristal());
        lblCristalVal.setText(recursos.getUnidadesCristal() + " / " + recursos.getCapacidadMaxCristal());

        barMineral.setMaximum(recursos.getCapacidadMaxMineral());
        barMineral.setValue(recursos.getUnidadesMineral());
        lblMineralVal.setText(recursos.getUnidadesMineral() + " / " + recursos.getCapacidadMaxMineral());

        barPlasma.setMaximum(recursos.getCapacidadMaxPlasma());
        barPlasma.setValue(recursos.getUnidadesPlasma());
        lblPlasmaVal.setText(recursos.getUnidadesPlasma() + " / " + recursos.getCapacidadMaxPlasma());

        //Actores
        lblDelegados.setText(String.valueOf(zonas.getNumeroDelegados()));
        lblDelegadosActivos.setText(String.valueOf(zonas.getNumeroDelegadosActivos()));
        lblPatrullas.setText(String.valueOf(zonas.getNumeroPatrullas()));
        lblSaqueadores.setText(String.valueOf(zonas.getNumeroSaqueadores()));

        // Estado simulación
        if (zonas.isSimulacionPausada()) {
            lblEstadoSim.setText("PAUSADA");
            lblEstadoSim.setForeground(new Color(200, 130, 0));
        } else {
            lblEstadoSim.setText("EN MARCHA");
            lblEstadoSim.setForeground(new Color(0, 150, 60));
        }
    }

    // =========================================================
    // AUXILIARES
    // =========================================================

    //Crea una barra de progreso
    private JProgressBar crearBarra(int maximo, Color color) {
        JProgressBar barra = new JProgressBar(0, maximo);
        barra.setValue(0);
        barra.setStringPainted(true);
        barra.setForeground(color);
        barra.setPreferredSize(new Dimension(200, 22));
        return barra;
    }
}