package Modelo;

/**
 * Deposito donde los delegados almacenan los recursos extraidos de los planetas
 *
 * Tiene dos limites diferenciados:
 *   Acceso: max 3 delegados depositando a la vez
 *   Capacidad: max de unidades que puede almacenar
 *
 * Depositos del sistema:
 *   Cristal -> capacidad 250 u.
 *   Mineral -> capacidad 200 u.
 *   Plasma  -> capacidad 150 u.
 */
public class DepositoOrbital extends Zona {

    private static final int MAX_DELEGADOS_DEPOSITO = 3;

    private TipoRecurso tipoRecurso;
    private int capacidadMaximaUnidades;
    private int unidadesActuales;

    public DepositoOrbital(String nombre, TipoRecurso tipoRecurso, int capacidadMaximaUnidades, int unidadesIniciales) {
        super(nombre, MAX_DELEGADOS_DEPOSITO);
        this.tipoRecurso = tipoRecurso;
        this.capacidadMaximaUnidades = capacidadMaximaUnidades;
        this.unidadesActuales = unidadesIniciales;
    }

    // --- Getters y Setters ---

    public TipoRecurso getTipoRecurso() {
        return tipoRecurso;
    }

    public int getCapacidadMaximaUnidades() {
        return capacidadMaximaUnidades;
    }

    public int getUnidadesActuales() {
        return unidadesActuales;
    }

    public void setUnidadesActuales(int unidadesActuales) {
        this.unidadesActuales = unidadesActuales;
    }

    // --- Metodos de consulta de estado ---

    
    //Devuelve el espacio libre disponible en unidades
    public int getEspacioDisponible() {
        return capacidadMaximaUnidades - unidadesActuales;
    }

    
    //Indica si hay espacio suficiente para almacenar la cantidad dada
    public boolean tieneEspacio(int cantidad) {
        return (unidadesActuales + cantidad) <= capacidadMaximaUnidades;
    }

    //Indica si el deposito esta por debajo del umbral de alerta de desabastecimiento (10% de la capacidad max)
    //Usado por el generador de delegados para decidir si crear uno nuevo
    public boolean estaPorDebajoUmbral() {
        return unidadesActuales < (capacidadMaximaUnidades * 0.10);
    }
}