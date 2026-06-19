package Modelo;

/**
 * Ataca planetas mineros y depositos orbitales
 *
 * Se identifican como SXXX
 * Su ciclo de trabajo lo ejecuta HiloSaqueador en Concurrencia
 *
 * Probabilidad de objetivo:
 *   70% -> deposito
 *   30% -> planeta minero
 *
 * Probabilidad de victoria en combate: 50% para cada bando
 */
public class Saqueador extends Actor {

    //Fases del ciclo de vida de un saqueador
    public enum FaseAtaque {
        EN_BASE,
        DESPLAZANDOSE,  
        EN_COMBATE,
        SAQUEANDO,     
        REGRESANDO      
    }

    private Zona objetivo;
    private FaseAtaque faseActual;
    private int unidadesRobadas;

    public Saqueador(String id) {
        super(id);
        this.objetivo = null;
        this.faseActual = FaseAtaque.EN_BASE;
        this.unidadesRobadas = 0;
    }

    // --- Getters y Setters ---

    public Zona getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(Zona objetivo) {
        this.objetivo = objetivo;
    }

    public FaseAtaque getFaseActual() {
        return faseActual;
    }

    public void setFaseActual(FaseAtaque faseActual) {
        this.faseActual = faseActual;
    }

    public int getUnidadesRobadas() {
        return unidadesRobadas;
    }

    public void setUnidadesRobadas(int unidadesRobadas) {
        this.unidadesRobadas = unidadesRobadas;
    }

    
    //Indica si el saqueador esta atacando alguna zona (en combate o en fase de saqueo activo)
    public boolean estaAtacando() {
        return faseActual == FaseAtaque.EN_COMBATE || faseActual == FaseAtaque.SAQUEANDO;
    }
}