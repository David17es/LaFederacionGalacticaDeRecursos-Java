package Concurrencia;

import Log.LoggerFederacion;
import Modelo.DelegadoComercial;
import Modelo.DepositoOrbital;
import Modelo.EstadoGalaxia;
import Modelo.PlanetaMinero;
import Modelo.TipoRecurso;
import Modelo.Zona;

import java.util.List;

/**
 * Ejecuta el ciclo de vida de un delegado
 *
 * Ciclo:
 *   1. Esperar en el Centro de Coordinacion (2-4s)
 *   2. Seleccionar aleatoriamente un planeta dependiendo del recurso
 *   3. Viajar al planeta y extraer recursos (3-7s, entre 10-30uds)
 *   4. Depositar en deposito orbital
 *   5. Volver al centro y repetir
 *
 * Si es expulsado durante un ataque:
 *   -> Zona de recuperacion (5-15s)
 *   -> Volver al centro y continuar
 */
public class HiloDelegado extends Thread {

    // Tiempos en ms
    private static final int T_ESPERA_CENTRO_MIN = 2000;
    private static final int T_ESPERA_CENTRO_RANGO = 2000;
    private static final int T_EXTRACCION_MIN = 3000;
    private static final int T_EXTRACCION_RANGO = 4000;
    private static final int T_DEPOSITO_MIN = 1000;
    private static final int T_DEPOSITO_RANGO = 2000;
    private static final int T_RECUPERACION_MIN = 5000;
    private static final int T_RECUPERACION_RANGO = 10000;

    // Unidades extraidas por ciclo
    private static final int EXTRACCION_MIN = 10;
    private static final int EXTRACCION_RANGO = 21; // (10, 30)

    private final DelegadoComercial delegado;
    private final EstadoGalaxia estado;
    private final SincronizacionZonas sincZonas;
    private final SincronizacionRecursos sincRecursos;
    private final ControlSimulacion control;
    private final LoggerFederacion logger;

    public HiloDelegado(DelegadoComercial delegado, SincronizacionZonas sincZonas, SincronizacionRecursos sincRecursos, ControlSimulacion control, LoggerFederacion logger) {
        this.delegado = delegado;
        this.estado = EstadoGalaxia.getInstancia();
        this.sincZonas = sincZonas;
        this.sincRecursos = sincRecursos;
        this.control = control;
        this.logger = logger;
        setName("Hilo-" + delegado.getId());
    }

    @Override
    public void run() {
        logger.registrarEvento(delegado.getId() + " comienza su actividad como delegado comercial.");

        while (control.isSimulacionActiva()) {
            try {
                control.comprobarPausa();

                // -----------------------------------------------
                // FASE 1: Esperar en el Centro de Coordinacion
                // -----------------------------------------------
                delegado.setZonaActual(estado.getCentroCoordinacion());
                delegado.setEstado("En Centro de Coordinación");
                logger.registrarEvento(delegado.getId() + " espera en el Centro de Coordinación Federal.");

                long tEspera = T_ESPERA_CENTRO_MIN + (long)(Math.random() * T_ESPERA_CENTRO_RANGO);
                Thread.sleep(tEspera);
                control.comprobarPausa();

                // -----------------------------------------------
                // FASE 2: Seleccionar planeta objetivo
                // -----------------------------------------------
                TipoRecurso recurso = seleccionarRecursoAleatorio();
                delegado.setRecursoObjetivo(recurso);
                PlanetaMinero planeta = seleccionarPlaneta(recurso);

                // -----------------------------------------------
                // FASE 3: Extraer recursos del planeta
                // -----------------------------------------------
                delegado.setEstado("Viajando a " + planeta.getNombre());
                logger.registrarEvento(delegado.getId() + " viaja al planeta " + planeta.getNombre() + " a extraer " + recurso + ".");

                sincZonas.entrarZona(planeta);
                delegado.setZonaActual(planeta);
                delegado.setEstado("Extrayendo en " + planeta.getNombre());

                try {
                    long tExtraccion = T_EXTRACCION_MIN + (long)(Math.random() * T_EXTRACCION_RANGO);
                    Thread.sleep(tExtraccion);
                    control.comprobarPausa();

                    // Comprobar si fue expulsado durante la extraccion
                    if (delegado.isExpulsado()) {
                        manejarExpulsion(planeta);
                        continue;
                    }

                    int unidades = EXTRACCION_MIN + (int)(Math.random() * EXTRACCION_RANGO);
                    delegado.setUnidadesTransportadas(unidades);

                    logger.registrarEvento(delegado.getId() + " ha extraído " + unidades + " u. de " + recurso + " en " + planeta.getNombre() + ".");
                } finally {
                    sincZonas.salirZona(planeta);
                }

                control.comprobarPausa();

                // -----------------------------------------------
                // FASE 4: Depositar en el deposito
                // -----------------------------------------------
                DepositoOrbital deposito = getDeposito(recurso);
                delegado.setEstado("Viajando a " + deposito.getNombre());
                logger.registrarEvento(delegado.getId() + " viaja al " + deposito.getNombre() + " con " + delegado.getUnidadesTransportadas() + " u. de " + recurso + ".");

                sincZonas.entrarZona(deposito);
                delegado.setZonaActual(deposito);
                delegado.setEstado("Depositando en " + deposito.getNombre());

                try {
                    long tDeposito = T_DEPOSITO_MIN + (long)(Math.random() * T_DEPOSITO_RANGO);
                    Thread.sleep(tDeposito);
                    control.comprobarPausa();

                    if (delegado.isExpulsado()) {
                        manejarExpulsion(deposito);
                        continue;
                    }

                    sincRecursos.depositar(recurso, delegado.getUnidadesTransportadas());
                    logger.registrarEvento(delegado.getId() + " ha depositado " + delegado.getUnidadesTransportadas() + " u. de " + recurso + " en " + deposito.getNombre() + ".");
                    delegado.setUnidadesTransportadas(0);
                } finally {
                    sincZonas.salirZona(deposito);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.registrarEvento(delegado.getId() + " finaliza su actividad.");
    }

    // =========================================================
    // METODOS AUXILIARES
    // =========================================================

    //Gestiona expulsion del delegado enviandolo a recuperacion
    //Depende de quien lo expulsó (5-15s)
    private void manejarExpulsion(Zona zonaActual) throws InterruptedException {
        delegado.setExpulsado(false);
        delegado.setUnidadesTransportadas(0);
        delegado.setZonaActual(estado.getZonaRecuperacion());
        delegado.setEstado("Recuperándose");
        delegado.setEnRecuperacion(true);

        logger.registrarEvento(delegado.getId() + " ha sido expulsado de " + zonaActual.getNombre() + " y va a la Zona de Recuperación.");

        long tRecuperacion = T_RECUPERACION_MIN + (long)(Math.random() * T_RECUPERACION_RANGO);
        Thread.sleep(tRecuperacion);
        
        delegado.setEnRecuperacion(false);
        delegado.setEstado("Volviendo al Centro");
    }

    //Selecciona aleatoriamente uno de los tres tipos de recurso
    private TipoRecurso seleccionarRecursoAleatorio() {
        TipoRecurso[] tipos = TipoRecurso.values();
        return tipos[(int)(Math.random() * tipos.length)];
    }

    //Selecciona aleatoriamente un planeta que produzca el tipo de recurso indicado
    private PlanetaMinero seleccionarPlaneta(TipoRecurso recurso) {
        List<PlanetaMinero> candidatos = new java.util.ArrayList<>();
        for (PlanetaMinero p : estado.getPlanetas()) {
            if (p.getTipoRecurso() == recurso) {
                candidatos.add(p);
            }
        }
        return candidatos.get((int)(Math.random() * candidatos.size()));
    }

    //Devuelve el deposito correspondiente al recurso
    private DepositoOrbital getDeposito(TipoRecurso recurso) {
        switch (recurso) {
            case CRISTAL: return estado.getDepositoCristal();
            case MINERAL: return estado.getDepositoMineral();
            case PLASMA: return estado.getDepositoPlasma();
            default: throw new IllegalArgumentException("Recurso desconocido: " + recurso);
        }
    }
}