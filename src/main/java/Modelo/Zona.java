package Modelo;

/**
 * Representa una zona del sistema galáctico.
 * Las zonas tienen nombre, capacidad máxima de actores
 * y estado de ataque.
 */
public abstract class Zona {

    private String nombre;
    private int capacidadMaxima;
    private int ocupacionActual;
    private boolean bajoAtaque;

    public Zona(String nombre, int capacidadMaxima) {
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.ocupacionActual = 0;
        this.bajoAtaque = false;
    }

    // --- Getters y Setters ---

    public String getNombre() {
        return nombre;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int getOcupacionActual() {
        return ocupacionActual;
    }

    public void setOcupacionActual(int ocupacionActual) {
        this.ocupacionActual = ocupacionActual;
    }

    public boolean isBajoAtaque() {
        return bajoAtaque;
    }

    public void setBajoAtaque(boolean bajoAtaque) {
        this.bajoAtaque = bajoAtaque;
    }

    public boolean estaLlena() {
        return ocupacionActual >= capacidadMaxima;
    }

    @Override
    public String toString() {
        return nombre;
    }
}