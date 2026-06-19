package Remoto;

import java.io.Serializable;

//DTO serializable con contadores de actores activos agrupados por tipo, para visualizacion en cliente remoto
public class EstadoZonasDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int numeroDelegados;
    private final int numeroDelegadosActivos;
    private final int numeroPatrullas;
    private final int numeroSaqueadores;
    private final boolean simulacionPausada;

    public EstadoZonasDTO(int numeroDelegados, int numeroDelegadosActivos, int numeroPatrullas, int numeroSaqueadores, boolean simulacionPausada) {
        this.numeroDelegados = numeroDelegados;
        this.numeroDelegadosActivos = numeroDelegadosActivos;
        this.numeroPatrullas = numeroPatrullas;
        this.numeroSaqueadores = numeroSaqueadores;
        this.simulacionPausada = simulacionPausada;
    }

    public int getNumeroDelegados() {return numeroDelegados;}
    public int getNumeroDelegadosActivos() {return numeroDelegadosActivos;}
    public int getNumeroPatrullas() {return numeroPatrullas;}
    public int getNumeroSaqueadores() {return numeroSaqueadores;}
    public boolean isSimulacionPausada() {return simulacionPausada;}
}