package Log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa un evento del sistema
 * Encapsula el timestamp, el actor implicado y la descripción del evento para ser escrito en el fichero log
 *
 * Formato de salida: "2026-06-14 12:35:04 Descripción del evento."
 */
public class EventoLog {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LocalDateTime timestamp;
    private String descripcion;

    public EventoLog(String descripcion) {
        this.timestamp = LocalDateTime.now();
        this.descripcion = descripcion;
    }

    // --- Getters ---

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescripcion() {
        return descripcion;
    }

    //Devuelve la línea completa lista para escribir en el fichero
    public String getLineaFormateada() {
        return timestamp.format(FORMATO) + " " + descripcion;
    }

    @Override
    public String toString() {
        return getLineaFormateada();
    }
}