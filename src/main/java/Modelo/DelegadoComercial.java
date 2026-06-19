package Modelo;

/**
 * Extraen recursos de los planetas mineros y los depositan en los depositos orbitales
 * Se identifican como DXXX (ej: D001)
 * Su ciclo de trabajo lo ejecuta HiloDelegado en Concurrencia
 */
public class DelegadoComercial extends Actor {

    private TipoRecurso recursoObjetivo;
    private int unidadesTransportadas;
    private boolean expulsado;

    public DelegadoComercial(String id) {
        super(id);
        this.recursoObjetivo = null;
        this.unidadesTransportadas = 0;
        this.expulsado = false;
    }

    // --- Getters y Setters ---

    public TipoRecurso getRecursoObjetivo() {
        return recursoObjetivo;
    }

    public void setRecursoObjetivo(TipoRecurso recursoObjetivo) {
        this.recursoObjetivo = recursoObjetivo;
    }

    public int getUnidadesTransportadas() {
        return unidadesTransportadas;
    }

    public void setUnidadesTransportadas(int unidadesTransportadas) {
        this.unidadesTransportadas = unidadesTransportadas;
    }

    public boolean isExpulsado() {
        return expulsado;
    }

    public void setExpulsado(boolean expulsado) {
        this.expulsado = expulsado;
    }
}