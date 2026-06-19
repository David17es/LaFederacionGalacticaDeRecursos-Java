package GUI;

import Concurrencia.SincronizacionRecursos;
import Modelo.DepositoOrbital;
import Modelo.EstadoGalaxia;
import Modelo.TipoRecurso;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Muestra el nivel de los tres depositos con barras de progreso y valores numericos
 *
 * El color de cada barra indica el nivel de llenado:
 *   - Verde (mas de 60%): nivel saludable
 *   - Naranja (30-59%): nivel bajo
 *   - Rojo (menos de 30%): nivel critico
 */
public class PanelRecursos extends JPanel {

    private static final Color COLOR_ALTO = new Color(50, 180, 80);
    private static final Color COLOR_MEDIO = new Color(220, 150, 30);
    private static final Color COLOR_BAJO = new Color(210, 50, 50);

    //Cristal
    private JProgressBar barCristal;
    private JLabel lblCristalVal;
    private JLabel lblCristalPct;

    //Mineral
    private JProgressBar barMineral;
    private JLabel lblMineralVal;
    private JLabel lblMineralPct;

    //Plasma
    private JProgressBar barPlasma;
    private JLabel lblPlasmaVal;
    private JLabel lblPlasmaPct;

    private final SincronizacionRecursos sincRecursos;
    private final EstadoGalaxia estado;

    public PanelRecursos(SincronizacionRecursos sincRecursos) {
        this.sincRecursos = sincRecursos;
        this.estado = EstadoGalaxia.getInstancia();

        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Depósitos Orbitales"));
        add(crearTablaRecursos(), BorderLayout.CENTER);
    }

    // =========================================================
    // CONSTRUCCION
    // =========================================================

    private JPanel crearTablaRecursos() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Cabecera ---
        gbc.gridy = 0;
        agregarCabecera(panel, gbc);

        // --- Cristal ---
        barCristal = crearBarra(new Color(100, 200, 230));
        lblCristalVal = new JLabel("0 / 0");
        lblCristalPct = new JLabel("0%");
        gbc.gridy = 1;
        agregarFilaRecurso(panel, gbc, "Cristal", barCristal, lblCristalVal, lblCristalPct);

        // --- Mineral ---
        barMineral = crearBarra(new Color(180, 140, 80));
        lblMineralVal = new JLabel("0 / 0");
        lblMineralPct = new JLabel("0%");
        gbc.gridy = 2;
        agregarFilaRecurso(panel, gbc, "Mineral", barMineral, lblMineralVal, lblMineralPct);

        // --- Plasma ---
        barPlasma = crearBarra(new Color(220, 100, 180));
        lblPlasmaVal = new JLabel("0 / 0");
        lblPlasmaPct = new JLabel("0%");
        gbc.gridy = 3;
        agregarFilaRecurso(panel, gbc, "Plasma",  barPlasma,  lblPlasmaVal,  lblPlasmaPct);

        return panel;
    }

    private void agregarCabecera(JPanel panel, GridBagConstraints gbc) {
        Font fuenteCab = new Font(Font.SANS_SERIF, Font.BOLD, 11);
        String[] cabeceras = {"Recurso", "Nivel", "Unidades", "%"};
        int[] anchos = {80, 220, 90, 50};

        for (int i = 0; i < cabeceras.length; i++) {
            JLabel lbl = new JLabel(cabeceras[i]);
            lbl.setFont(fuenteCab);
            lbl.setForeground(Color.GRAY);
            gbc.gridx = i;
            gbc.weightx = (i == 1) ? 1.0 : 0.0;
            gbc.ipadx = anchos[i];
            panel.add(lbl, gbc);
        }
        gbc.ipadx = 0;
    }

    private void agregarFilaRecurso(JPanel panel, GridBagConstraints gbc, String nombre, JProgressBar barra, JLabel lblVal, JLabel lblPct) {
        gbc.gridx = 0; gbc.weightx = 0;
        panel.add(new JLabel(nombre), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(barra, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(lblVal, gbc);

        gbc.gridx = 3;
        panel.add(lblPct, gbc);
    }

    // =========================================================
    // ACTUALIZACION
    // =========================================================

    //Refresca los valores de los depsitos consultando SincronizacionRecursos
    public void refrescar() {
        DepositoOrbital depCristal = estado.getDepositoCristal();
        DepositoOrbital depMineral = estado.getDepositoMineral();
        DepositoOrbital depPlasma  = estado.getDepositoPlasma();

        actualizarFila(barCristal, lblCristalVal, lblCristalPct, sincRecursos.getUnidadesActuales(TipoRecurso.CRISTAL), depCristal.getCapacidadMaximaUnidades());

        actualizarFila(barMineral, lblMineralVal, lblMineralPct, sincRecursos.getUnidadesActuales(TipoRecurso.MINERAL), depMineral.getCapacidadMaximaUnidades());

        actualizarFila(barPlasma, lblPlasmaVal, lblPlasmaPct, sincRecursos.getUnidadesActuales(TipoRecurso.PLASMA), depPlasma.getCapacidadMaximaUnidades());
    }

    // =========================================================
    // AUXILIARES
    // =========================================================

    private void actualizarFila(JProgressBar barra, JLabel lblVal, JLabel lblPct, int actual, int maximo) {
        if (maximo <= 0) return;

        barra.setMaximum(maximo);
        barra.setValue(actual);

        double pct = (actual * 100.0) / maximo;
        lblVal.setText(actual + " / " + maximo);
        lblPct.setText(String.format("%.0f%%", pct));

        // Color dinamico segun nivel de llenado
        if (pct >= 60) {
            barra.setForeground(COLOR_ALTO);
        } else if (pct >= 30) {
            barra.setForeground(COLOR_MEDIO);
        } else {
            barra.setForeground(COLOR_BAJO);
        }
    }

    private JProgressBar crearBarra(Color color) {
        JProgressBar barra = new JProgressBar(0, 100);
        barra.setValue(0);
        barra.setStringPainted(false);
        barra.setForeground(color);
        barra.setPreferredSize(new Dimension(200, 20));
        return barra;
    }
}