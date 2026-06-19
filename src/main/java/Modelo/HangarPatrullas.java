package Modelo;

/**
 * Zona donde las patrullas federales inician su actividad,
 * preparando sus sistemas entre 3 y 6 segundos, y a donde
 * regresan tras ser derrotadas en combate
 * Sin limite de ocupacion
 */
public class HangarPatrullas extends Zona {

    public HangarPatrullas() {
        super("Hangar de Patrullas", Integer.MAX_VALUE);
    }
}
