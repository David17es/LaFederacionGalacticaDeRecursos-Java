package Modelo;

/**
 * Cada planeta produce un recurso distinto y permite
 * maximo 4 delegados extrayendo a la vez
 * Los planetas disponen de recursos ilimitados.
 *
 * Planetas del sistema:
 *    Cryon -> CRISTAL
 *    Velora -> CRISTAL
 *    Ferrum -> MINERAL
 *    Drax -> MINERAL
 *    Ignis -> PLASMA
 */
public class PlanetaMinero extends Zona {

    private static final int MAX_DELEGADOS_PLANETA = 4;

    private TipoRecurso tipoRecurso;

    public PlanetaMinero(String nombre, TipoRecurso tipoRecurso) {
        super(nombre, MAX_DELEGADOS_PLANETA);
        this.tipoRecurso = tipoRecurso;
    }

    // --- Getters ---

    public TipoRecurso getTipoRecurso() {
        return tipoRecurso;
    }
}
