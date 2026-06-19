package GUI;

import Modelo.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;

/**
 * Mapa visual de la galaxia
 * Dibuja todas las zonas como nodos con su nombre y numero
 * de actores actuales, con colores distintos por tipo de zona.
 */
public class PanelMapaGalaxia extends JPanel {

    // --- Colores por tipo de zona ---
    private static final Color COLOR_CENTRO = new Color(70,  130, 200);
    private static final Color COLOR_PLANETA = new Color(50,  160, 80);
    private static final Color COLOR_DEPOSITO = new Color(180, 140, 40);
    private static final Color COLOR_HANGAR = new Color(100, 80,  180);
    private static final Color COLOR_BASE_SAQ = new Color(200, 60,  60);
    private static final Color COLOR_RECUPERACION = new Color(140, 140, 140);
    private static final Color COLOR_FONDO = new Color(15,  20,  40);
    private static final Color COLOR_TEXTO = new Color(230, 230, 230);
    private static final Color COLOR_TEXTO_CUENTA = new Color(255, 230, 100);

    // --- Posiciones fijas de cada zona (porcentaje del panel) ---
    //Se calculan en paintComponent() escalando al tamaño real
    private static final double[][] POSICIONES = {
        //{cx%, cy%, radio%} -> una entrada por zona
        {0.50, 0.45, 0.08}, //0: Centro coordinacion federal
        {0.20, 0.20, 0.07}, //1: Planeta A
        {0.50, 0.12, 0.07}, //2: Planeta B
        {0.80, 0.20, 0.07}, //3: Planeta C
        {0.20, 0.70, 0.07}, //4: Deposito A
        {0.80, 0.70, 0.07}, //5: Deposito B
        {0.50, 0.82, 0.07}, //6: Deposito C
        {0.10, 0.45, 0.06}, //7: Hangar patrullas
        {0.90, 0.45, 0.06}, //8: Base saqueadores
        {0.50, 0.92, 0.05}, //9: Zona recuperacion
    };

    private final EstadoGalaxia estado;

    public PanelMapaGalaxia() {
        this.estado = EstadoGalaxia.getInstancia();
        setBackground(COLOR_FONDO);
        setPreferredSize(new Dimension(480, 360));
        setToolTipText("Mapa de la galaxia");
    }

    // =========================================================
    // PINTADO
    // =========================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        dibujarLineasConexion(g2, w, h);
        dibujarZonas(g2, w, h);
        dibujarTitulo(g2, w);
    }

    // =========================================================
    // LINEAS DE CONEXION
    // =========================================================

    private void dibujarLineasConexion(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(60, 80, 120, 160));
        g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Centro -> Planetas (0->1, 0->2, 0->3)
        int[][] conexiones = {
            {0,1},{0,2},{0,3}, // Centro <-> planetas
            {0,4},{0,5},{0,6}, // Centro <-> depositos
            {0,7},{0,8}, // Centro <-> hangar y base
            {1,4},{2,5},{3,6}, // Planetas <-> depositos 
            {7,9},{8,9} // Hangar/base <-> Recuperacion
        };

        for (int[] par : conexiones) {
            double[] a = POSICIONES[par[0]];
            double[] b = POSICIONES[par[1]];
            int x1 = (int)(a[0] * w);
            int y1 = (int)(a[1] * h);
            int x2 = (int)(b[0] * w);
            int y2 = (int)(b[1] * h);
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    // =========================================================
    // ZONAS
    // =========================================================

    private void dibujarZonas(Graphics2D g2, int w, int h) {
        List<Zona> zonas = estado.getTodasLasZonas();

        for (int i = 0; i < zonas.size() && i < POSICIONES.length; i++) {
            Zona zona = zonas.get(i);
            double[] pos = POSICIONES[i];

            int cx = (int)(pos[0] * w);
            int cy = (int)(pos[1] * h);
            int radio = (int)(pos[2] * Math.min(w, h));

            Color colorZona = colorDeZona(zona);
            int actores = zona.getOcupacionActual();
            int capacidad = zona.getCapacidadMaxima();

            dibujarNodo(g2, cx, cy, radio, colorZona, zona.getNombre(), actores, capacidad);
        }
    }

    private void dibujarNodo(Graphics2D g2, int cx, int cy, int radio, Color color, String nombre, int actores, int capacidad) {
        //Sombra
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(cx - radio + 3, cy - radio + 3, radio * 2, radio * 2);

        //Relleno degradado radial
        RadialGradientPaint grad = new RadialGradientPaint(new Point2D.Float(cx - radio / 3f, cy - radio / 3f), radio * 1.2f, new float[]{ 0f, 1f }, new Color[]{ color.brighter(), color.darker() });
        g2.setPaint(grad);
        g2.fillOval(cx - radio, cy - radio, radio * 2, radio * 2);

        //Borde
        g2.setColor(color.brighter());
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(cx - radio, cy - radio, radio * 2, radio * 2);

        //Indicador de ocupacion
        if (capacidad > 0) {
            float fraccion = Math.min(1f, (float) actores / capacidad);
            g2.setColor(COLOR_TEXTO_CUENTA);
            g2.setStroke(new BasicStroke(3f));
            g2.drawArc(cx - radio, cy - radio, radio * 2, radio * 2, 90, -(int)(fraccion * 360));
        }

        //Nombre de la zona
        g2.setColor(COLOR_TEXTO);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 9));
        String[] partes = partirNombre(nombre);
        FontMetrics fm = g2.getFontMetrics();
        int lineH = fm.getHeight();
        int yBase = cy - (partes.length - 1) * lineH / 2;
        for (int i = 0; i < partes.length; i++) {
            int xTxt = cx - fm.stringWidth(partes[i]) / 2;
            g2.drawString(partes[i], xTxt, yBase + i * lineH);
        }

        //Contador de actores bajo el nodo
        g2.setColor(COLOR_TEXTO_CUENTA);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        String cuenta = actores + "/" + capacidad;
        fm = g2.getFontMetrics();
        g2.drawString(cuenta, cx - fm.stringWidth(cuenta) / 2, cy + radio + fm.getAscent() + 2);
    }

    private void dibujarTitulo(Graphics2D g2, int w) {
        g2.setColor(new Color(180, 200, 255, 180));
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        String titulo = "Federación Galáctica de Recursos";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(titulo, (w - fm.stringWidth(titulo)) / 2, 14);
    }

    // =========================================================
    // ACTUALIZACION
    // =========================================================

    //Fuerza un repintado del mapa con los datos actuales
    public void refrescar() {
        repaint();
    }

    // =========================================================
    // AUXILIARES
    // =========================================================

    private Color colorDeZona(Zona zona) {
        if (zona instanceof CentroCoordinacionFederal)
            return COLOR_CENTRO;
        if (zona instanceof PlanetaMinero)
            return COLOR_PLANETA;
        if (zona instanceof DepositoOrbital)
            return COLOR_DEPOSITO;
        if (zona instanceof HangarPatrullas)
            return COLOR_HANGAR;
        if (zona instanceof BaseSaqueadores)
            return COLOR_BASE_SAQ;
        if (zona instanceof ZonaRecuperacion)
            return COLOR_RECUPERACION;
        return Color.GRAY;
    }

    //Parte el nombre de una zona en dos lineas si hay mas de 10 caracteres
    private String[] partirNombre(String nombre) {
        if (nombre.length() <= 10) return new String[]{ nombre };
        int mitad = nombre.length() / 2;
        int espacio = nombre.lastIndexOf(' ', mitad);
        if (espacio < 0) espacio = nombre.indexOf(' ', mitad);
        if (espacio < 0) return new String[]{ nombre };
        return new String[]{
            nombre.substring(0, espacio),
            nombre.substring(espacio + 1)
        };
    }
}