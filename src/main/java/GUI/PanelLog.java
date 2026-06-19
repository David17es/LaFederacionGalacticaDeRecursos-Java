package GUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

//Muestra en tiempo real los eventos del log
public class PanelLog extends JPanel {

    private static final int MAX_LINEAS = 500;

    private final JTextArea areaLog;
    private final JScrollPane scroll;

    public PanelLog() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Log de Eventos"));

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        areaLog.setBackground(new Color(20, 20, 20));
        areaLog.setForeground(new Color(180, 230, 180));
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(false);

        scroll = new JScrollPane(areaLog);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setPreferredSize(new Dimension(0, 160));
        add(scroll, BorderLayout.CENTER);

        //Boton para limpiar el panel
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(btnLimpiar.getFont().deriveFont(11f));
        btnLimpiar.addActionListener(e -> limpiar());
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2));
        panelBoton.add(btnLimpiar);
        add(panelBoton, BorderLayout.SOUTH);
    }

    //Añade una linea al area y hace scroll automatico al final
    public void agregarLinea(String linea) {
        // Limitar num de lineas
        if (areaLog.getLineCount() >= MAX_LINEAS) {
            String texto = areaLog.getText();
            int primerSalto = texto.indexOf('\n');
            if (primerSalto >= 0) {
                areaLog.setText(texto.substring(primerSalto + 1));
            }
        }
        areaLog.append(linea + "\n");
        // Scroll automatico al final
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    //Limpia el contenido del area de log visual (no afecta en memoria)
    public void limpiar() {
        areaLog.setText("");
    }
}