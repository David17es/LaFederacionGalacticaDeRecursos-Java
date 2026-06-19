package Modelo;

/**
 * Zona central donde los delegados comerciales esperan entre ciclos de trabajo, preparando su siguiente solicitud de recursos
 * Sin limite de ocupacion
 */
public class CentroCoordinacionFederal extends Zona {

    public CentroCoordinacionFederal() {
        super("Centro de Coordinación Federal", Integer.MAX_VALUE);
    }
}