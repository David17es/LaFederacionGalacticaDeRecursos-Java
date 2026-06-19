package Modelo;

/**
 * Zona a la que son enviados los delegados expulsados durante un ataque y las patrullas derrotadas en combate
 * Sin limite de ocupacion
 */
public class ZonaRecuperacion extends Zona {

    public ZonaRecuperacion() {
        super("Zona de Recuperación", Integer.MAX_VALUE);
    }
}
