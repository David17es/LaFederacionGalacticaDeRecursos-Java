package GUI;

import Modelo.DelegadoComercial;
import Modelo.EstadoGalaxia;
import Modelo.PatrullaFederal;
import Modelo.Saqueador;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;


//Tablas que muestran en tiempo real el estado de todos los actores activos en la simulacion
public class PanelActores extends JPanel {

    // --- Columnas de cada tabla ---
    private static final String[] COLS_DELEGADO  = {"ID", "Zona actual", "Recurso", "Transportando", "Estado"};
    private static final String[] COLS_PATRULLA  = {"ID", "Zona actual", "C. Ganados", "C. Perdidos", "Estado"};
    private static final String[] COLS_SAQUEADOR = {"ID", "Zona actual", "Objetivo", "U. robadas", "Estado"};

    private final DefaultTableModel modeloDelegados;
    private final DefaultTableModel modeloPatrullas;
    private final DefaultTableModel modeloSaqueadores;

    private final EstadoGalaxia estado;

    public PanelActores() {
        this.estado = EstadoGalaxia.getInstancia();

        modeloDelegados = crearModelo(COLS_DELEGADO);
        modeloPatrullas = crearModelo(COLS_PATRULLA);
        modeloSaqueadores = crearModelo(COLS_SAQUEADOR);

        setLayout(new GridLayout(3, 1, 0, 6));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        add(crearPanelTabla("Delegados Comerciales", modeloDelegados,  new Color(60, 120, 200)));
        add(crearPanelTabla("Patrullas Federales", modeloPatrullas,  new Color(40, 160, 80)));
        add(crearPanelTabla("Saqueadores", modeloSaqueadores, new Color(200, 60, 60)));
    }

    // =========================================================
    // CONSTRUCCION
    // =========================================================

    private JPanel crearPanelTabla(String titulo, DefaultTableModel modelo, Color colorCabecera) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(titulo));

        JTable tabla = new JTable(modelo);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(20);
        tabla.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        tabla.setGridColor(new Color(220, 220, 220));
        tabla.setSelectionBackground(new Color(210, 225, 245));
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Cabecera con color propio de cada actor
        tabla.getTableHeader().setBackground(colorCabecera);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));

        // Renderizador de color en columna "Estado"
        int colEstado = modelo.getColumnCount() - 1;
        tabla.getColumnModel().getColumn(colEstado).setCellRenderer(new EstadoCellRenderer());

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(0, 100));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // ACTUALIZACION
    // =========================================================

    //Refresca las tres tablas con los datos actuales de EstadoGalaxia
    public void refrescar() {
        refrescarDelegados();
        refrescarPatrullas();
        refrescarSaqueadores();
    }

    private void refrescarDelegados() {
        modeloDelegados.setRowCount(0);
        List<DelegadoComercial> lista = estado.getDelegados();
        for (DelegadoComercial d : lista) {
            modeloDelegados.addRow(new Object[]{
                d.getId(),
                d.getZonaActual() != null ? d.getZonaActual().getNombre() : "—",
                d.getRecursoObjetivo() != null ? d.getRecursoObjetivo().name() : "—",
                d.getUnidadesTransportadas(),
                d.getEstado()
            });
        }
    }

    private void refrescarPatrullas() {
        modeloPatrullas.setRowCount(0);
        List<PatrullaFederal> lista = estado.getPatrullas();
        for (PatrullaFederal p : lista) {
            modeloPatrullas.addRow(new Object[]{
                p.getId(),
                p.getZonaActual() != null
                    ? p.getZonaActual().getNombre() : "—",
                p.getCombatesGanados(),
                p.getCombatesPerdidos(),
                p.getEstado()
            });
        }
    }

    private void refrescarSaqueadores() {
        modeloSaqueadores.setRowCount(0);
        List<Saqueador> lista = estado.getSaqueadores();
        for (Saqueador s : lista) {
            modeloSaqueadores.addRow(new Object[]{
                s.getId(),
                s.getZonaActual() != null ? s.getZonaActual().getNombre() : "—",
                s.getObjetivo() != null ? s.getObjetivo().getNombre() : "—",
                s.getUnidadesRobadas(),
                s.getEstado()
            });
        }
    }

    // =========================================================
    // AUXILIARES
    // =========================================================

    //Crea un DefaultTableModel no editable con las columnas dadas
    private DefaultTableModel crearModelo(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
    }

    // =========================================================
    // RENDERIZADOR DE COLUMNA ESTADO
    // =========================================================

    /**
     * Colorea el texto de la celda "Estado" segun su contenido:
     *   - Rojo: Derrotado / Expulsado / Atacando
     *   - Naranja: Recuperándose / Penalización
     *   - Azul: Transportando / Patrullando
     *   - Verde: Listo / En Centro
     *   - Gris: Resto
     */
    private static class EstadoCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value == null) {
                setForeground(Color.GRAY);
                return this;
            }

            String estado = value.toString().toLowerCase();

            if (estado.contains("derrota") || estado.contains("expulsado") || estado.contains("atacando") || estado.contains("saqueando")) {
                setForeground(new Color(190, 40, 40));
            } else if (estado.contains("recuper") || estado.contains("penaliz") || estado.contains("combate")) {
                setForeground(new Color(200, 130, 0));
            } else if (estado.contains("transport") || estado.contains("patrullando") || estado.contains("viajando")) {
                setForeground(new Color(30, 100, 200));
            } else if (estado.contains("listo") || estado.contains("centro") || estado.contains("preparando")) {
                setForeground(new Color(0, 150, 60));
            } else {
                setForeground(Color.DARK_GRAY);
            }
            return this;
        }
    }
}