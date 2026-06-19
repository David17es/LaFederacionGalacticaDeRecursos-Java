package Modelo;

/**
 * Esperan tras aparecer (3-6s) y tras completar un ataque (10s de espera obligatoria, o 20s si fueron derrotados)
 * Limite de 40 saqueadores
 */
public class BaseSaqueadores extends Zona {

    public BaseSaqueadores() {
        super("Base de Saqueadores", Integer.MAX_VALUE);
    }
}