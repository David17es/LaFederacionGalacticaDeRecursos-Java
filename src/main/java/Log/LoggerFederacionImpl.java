package Log;

import GUI.VentanaPrincipalServidor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;

/**
 * Implementacion thread-safe del logger, protegido por ReentrantLock explicito
 * El fichero se abre en modo append para no borrar el historial
 */
public class LoggerFederacionImpl implements LoggerFederacion {

    private static final String NOMBRE_FICHERO = "federacion_galactica.txt";

    private final Lock lock;

    public LoggerFederacionImpl() {
        this.lock = new ReentrantLock();
    }
    
    private VentanaPrincipalServidor ventana;

    public void setVentana(VentanaPrincipalServidor ventana) {
        this.ventana = ventana;
    }

    @Override
    public void registrarEvento(String descripcion) {
        EventoLog evento = new EventoLog(descripcion);
        String lineaFormateada = evento.getLineaFormateada();
        lock.lock();
        try {
            escribirEnFichero(evento.getLineaFormateada());
        } finally {
            lock.unlock();
        }
        
        
        if (ventana != null) {
        SwingUtilities.invokeLater(() -> ventana.agregarLineaLog(lineaFormateada));
        }
    }

     //Abre y cierra el fichero por cada registro para garantizar que los eventos queden registrados incluso si el programa se cierra de forma inesperada
    private void escribirEnFichero(String linea) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOMBRE_FICHERO, true))) {
            writer.write(linea);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }
}