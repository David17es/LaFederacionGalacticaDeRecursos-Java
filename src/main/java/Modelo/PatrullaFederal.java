package Modelo;

/**
 * Las patrullas defienden planetas y depositos frente a los
 * saqueadores, patrullando aleatoriamente entre zonas
 * Se identifican como PXXX (ej: P001)
 * Su ciclo de trabajo lo ejecuta HiloPatrulla en Concurrencia.
 */
public class PatrullaFederal extends Actor {

    private boolean enCombate;
    private int combatesGanados;
    private int combatesPerdidos;

    public PatrullaFederal(String id) {
        super(id);
        this.enCombate = false;
        this.combatesGanados = 0;
        this.combatesPerdidos = 0;
    }

    // --- Getters y Setters ---

    public boolean isEnCombate() {
        return enCombate;
    }

    public void setEnCombate(boolean enCombate) {
        this.enCombate = enCombate;
    }

    public int getCombatesGanados() {
        return combatesGanados;
    }

    public void setCombatesGanados(int combatesGanados) {
        this.combatesGanados = combatesGanados;
    }

    public int getCombatesPerdidos() {
        return combatesPerdidos;
    }

    public void setCombatesPerdidos(int combatesPerdidos) {
        this.combatesPerdidos = combatesPerdidos;
    }

    
    //Incrementa contador de victorias
    public void registrarVictoria() {
        this.combatesGanados++;
    }

    
    //Incrementa contador de derrotas
    public void registrarDerrota() {
        this.combatesPerdidos++;
    }
}