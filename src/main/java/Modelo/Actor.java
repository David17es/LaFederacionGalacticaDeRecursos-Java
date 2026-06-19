package Modelo;

/**
 * Representa a cualquier actor del sistema (delegado, patrulla o saqueador)
 * Todos los actores tienen un id unico y pertenecen a una zona en cada momento
 */
public abstract class Actor {

    private String id;
    private Zona zonaActual;
    private String estado;
    private boolean enRecuperacion;

    public Actor(String id) {
        this.id = id;
        this.zonaActual = null;
        this.estado = "Iniciando";
        this.enRecuperacion = false;
    }

    // --- Getters y Setters ---

    public String getId() {
        return id;
    }

    public Zona getZonaActual() {
        return zonaActual;
    }

    public void setZonaActual(Zona zonaActual) {
        this.zonaActual = zonaActual;
    }
    
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isEnRecuperacion() {
        return enRecuperacion;
    }

    public void setEnRecuperacion(boolean enRecuperacion) {
        this.enRecuperacion = enRecuperacion;
    }

    @Override
    public String toString() {
        return id + " [" + estado + "]" + (zonaActual != null ? " en " + zonaActual.getNombre() : "");
    }
}
