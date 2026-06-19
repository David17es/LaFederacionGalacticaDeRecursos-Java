package Remoto;

import java.io.Serializable;

/**
 * DTO (Data Transfer Object) serializable con el estado actual de los tres depositos
 * Se envia desde el servidor al cliente remoto
 */
public class EstadoRecursosDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int unidadesCristal;
    private final int capacidadMaxCristal;
    private final int unidadesMineral;
    private final int capacidadMaxMineral;
    private final int unidadesPlasma;
    private final int capacidadMaxPlasma;

    public EstadoRecursosDTO(int unidadesCristal, int capacidadMaxCristal, int unidadesMineral, int capacidadMaxMineral, int unidadesPlasma,  int capacidadMaxPlasma) {
        this.unidadesCristal = unidadesCristal;
        this.capacidadMaxCristal = capacidadMaxCristal;
        this.unidadesMineral = unidadesMineral;
        this.capacidadMaxMineral = capacidadMaxMineral;
        this.unidadesPlasma = unidadesPlasma;
        this.capacidadMaxPlasma = capacidadMaxPlasma;
    }

    public int getUnidadesCristal() {return unidadesCristal;}
    public int getCapacidadMaxCristal() {return capacidadMaxCristal;}
    public int getUnidadesMineral() {return unidadesMineral;}
    public int getCapacidadMaxMineral() {return capacidadMaxMineral;}
    public int getUnidadesPlasma() {return unidadesPlasma;}
    public int getCapacidadMaxPlasma() {return capacidadMaxPlasma;}
}