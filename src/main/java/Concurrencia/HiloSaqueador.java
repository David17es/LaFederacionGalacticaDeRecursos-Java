package Concurrencia;

import Log.LoggerFederacion;
import Modelo.DepositoOrbital;
import Modelo.EstadoGalaxia;
import Modelo.PlanetaMinero;
import Modelo.Saqueador;
import Modelo.Zona;

import java.util.List;

/**
 * Hilo que ejecuta el ciclo de vida de un saqueador espacial
 *
 * Ciclo:
 *   1. Esperar en la Base (3-6s)
 *   2. Seleccionar objetivo:
 *        - 70% -> depósito orbital aleatorio
 *        - 30% -> planeta minero aleatorio
 *   3. Desplazarse al objetivo (2-5s)
 *   4. Intentar el ataque:
 *        - Si hay patrulla -> combate (50/50):
 *            Victoria saqueador: expulsa delegados, saquea recursos
 *            Derrota saqueador: va a la base (20s penalizacion)
 *        - Sin patrulla -> saquea directamente
 *   5. Esperar en la base (10s obligatorios entre ataques)
 *   6. Repetir
 */
public class HiloSaqueador extends Thread {

    private static final int T_BASE_MIN = 3000;
    private static final int T_BASE_RANGO = 3000;
    private static final int T_DESPLAZAMIENTO_MIN = 2000;
    private static final int T_DESPLAZAMIENTO_RANGO = 3000;
    private static final int T_SAQUEO_MIN = 2000;
    private static final int T_SAQUEO_RANGO = 3000;
    private static final int T_ESPERA_ENTRE_ATAQUES_MS = 10000;
    private static final int T_PENALIZACION_DERROTA_MS = 20000;

    private static final double PROB_DEPOSITO = 0.70;

    private final Saqueador saqueador;
    private final EstadoGalaxia estado;
    private final SincronizacionZonas sincZonas;
    private final SincronizacionRecursos sincRecursos;
    private final SincronizacionSaqueadores sincSaqueadores;
    private final ControlSimulacion control;
    private final LoggerFederacion logger;

    public HiloSaqueador(Saqueador saqueador, SincronizacionZonas sincZonas, SincronizacionRecursos sincRecursos, SincronizacionSaqueadores sincSaqueadores, ControlSimulacion control, LoggerFederacion logger) {
        this.saqueador = saqueador;
        this.estado = EstadoGalaxia.getInstancia();
        this.sincZonas = sincZonas;
        this.sincRecursos = sincRecursos;
        this.sincSaqueadores = sincSaqueadores;
        this.control = control;
        this.logger = logger;
        setName("Hilo-" + saqueador.getId());
    }

    @Override
    public void run() {
        logger.registrarEvento(saqueador.getId() + " aparece en la galaxia.");

        try {
            while (control.isSimulacionActiva()) {
                control.comprobarPausa();

                // -----------------------------------------------
                // FASE 1: Esperar en la base
                // -----------------------------------------------
                saqueador.setZonaActual(estado.getBaseSaqueadores());
                saqueador.setFaseActual(Saqueador.FaseAtaque.EN_BASE);
                saqueador.setEstado("Esperando en Base");

                long tBase = T_BASE_MIN + (long)(Math.random() * T_BASE_RANGO);
                Thread.sleep(tBase);
                control.comprobarPausa();

                // -----------------------------------------------
                // FASE 2: Seleccionar objetivo
                // -----------------------------------------------
                Zona objetivo = seleccionarObjetivo();
                saqueador.setObjetivo(objetivo);
                saqueador.setFaseActual(Saqueador.FaseAtaque.DESPLAZANDOSE);
                saqueador.setEstado("Viajando a " + objetivo.getNombre());
                logger.registrarEvento(saqueador.getId() + " se dirige a atacar " + objetivo.getNombre() + ".");

                // -----------------------------------------------
                // FASE 3: Desplazarse al objetivo
                // -----------------------------------------------
                long tDespl = T_DESPLAZAMIENTO_MIN + (long)(Math.random() * T_DESPLAZAMIENTO_RANGO);
                Thread.sleep(tDespl);
                control.comprobarPausa();

                // -----------------------------------------------
                // FASE 4: Intentar el ataque
                // -----------------------------------------------
                objetivo.setBajoAtaque(true);
                
                sincSaqueadores.iniciarAtaque(objetivo);
                saqueador.setFaseActual(Saqueador.FaseAtaque.SAQUEANDO);
                saqueador.setEstado("Atacando " + objetivo.getNombre());

                try {
                    //Comprobar si hay patrulla en zona
                    PatrullaEnZona resultado = buscarPatrullaEnZona(objetivo);

                    if (resultado != null) {
                        boolean victoriaSaqueador = Math.random() < 0.5;
                        saqueador.setFaseActual(Saqueador.FaseAtaque.EN_COMBATE);
                        logger.registrarEvento(saqueador.getId() + " combate con " + resultado.getId() + " en " + objetivo.getNombre() + ".");

                        if (!victoriaSaqueador) {
                            // --- Derrota del saqueador ---
                            logger.registrarEvento(saqueador.getId() + " es derrotado por " + resultado.getId() + ". Penalización de 20 s en la Base.");
                            sincSaqueadores.finalizarAtaque(objetivo);
                            saqueador.setFaseActual(Saqueador.FaseAtaque.EN_BASE);
                            saqueador.setObjetivo(null);
                            saqueador.setZonaActual(estado.getBaseSaqueadores());
                            saqueador.setEstado("Derrotado - penalización");
                            Thread.sleep(T_PENALIZACION_DERROTA_MS);
                            continue;
                        }
                        // Victoria del saqueador: continua saqueando
                        logger.registrarEvento(saqueador.getId() + " vence a " + resultado.getId() + " en " + objetivo.getNombre() + ".");
                    }

                    // -----------------------------------------------
                    // FASE 5: Saquear
                    // -----------------------------------------------
                    expulsarDelegadosDeZona(objetivo);
                    saqueador.setFaseActual(Saqueador.FaseAtaque.SAQUEANDO);

                    long tSaqueo = T_SAQUEO_MIN + (long)(Math.random() * T_SAQUEO_RANGO);
                    Thread.sleep(tSaqueo);

                    int robado = ejecutarSaqueo(objetivo);
                    saqueador.setUnidadesRobadas(saqueador.getUnidadesRobadas() + robado);

                    logger.registrarEvento(saqueador.getId() + " ha saqueado " + robado + " u. de " + objetivo.getNombre() + ".");

                } finally {
                    sincSaqueadores.finalizarAtaque(objetivo);
                    objetivo.setBajoAtaque(false);
                }

                // -----------------------------------------------
                // FASE 6: Regresar a la base y esperar
                // -----------------------------------------------
                saqueador.setFaseActual(Saqueador.FaseAtaque.REGRESANDO);
                saqueador.setEstado("Regresando a la Base");
                saqueador.setObjetivo(null);

                Thread.sleep(T_ESPERA_ENTRE_ATAQUES_MS);
                control.comprobarPausa();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        estado.eliminarSaqueador(saqueador);
        logger.registrarEvento(saqueador.getId() + " abandona la galaxia.");
    }

    // =========================================================
    // METODOS AUXILIARES
    // =========================================================

    //Selecciona el objetivo del ataque:
    //   70% -> deposito
    //   30% -> planeta
    private Zona seleccionarObjetivo() {
        if (Math.random() < PROB_DEPOSITO) {
            List<DepositoOrbital> depositos = estado.getDepositos();
            return depositos.get((int)(Math.random() * depositos.size()));
        } else {
            List<PlanetaMinero> planetas = estado.getPlanetas();
            return planetas.get((int)(Math.random() * planetas.size()));
        }
    }

    //Ejecuta el saqueo sobre la zona objetivo
    //Si es deposito: retirar hasta 30% de las unidades
    //Si es planeta: interrumpe la extraccion
    private int ejecutarSaqueo(Zona zona) throws InterruptedException {
        if (zona instanceof DepositoOrbital) {
            DepositoOrbital deposito = (DepositoOrbital) zona;
            return sincRecursos.vaciarPorSaqueo(deposito.getTipoRecurso());
        }
        return 0;
    }

    //Expulsa todos los delegados presentes en la zona atacada
    private void expulsarDelegadosDeZona(Zona zona) {
        for (Modelo.DelegadoComercial d
                : estado.getDelegados()) {
            if (d.getZonaActual() != null && d.getZonaActual().equals(zona) && !d.isExpulsado()) {
                d.setExpulsado(true);
            }
        }
    }

    // Busca una patrulla activa en la zona indicada. Devuelve la primera que encuentre, o null si no hay ninguna
    private PatrullaEnZona buscarPatrullaEnZona(Zona zona) {
        for (Modelo.PatrullaFederal p
                : estado.getPatrullas()) {
            if (p.getZonaActual() != null && p.getZonaActual().equals(zona) && !p.isEnRecuperacion()) {
                return new PatrullaEnZona(p.getId());
            }
        }
        return null;
    }

    // DTO (Data Transfer Object) minimo para transportar el ID de la patrulla encontrada
    private static class PatrullaEnZona {
        private final String id;
        PatrullaEnZona(String id) {this.id = id;}
        String getId() {return id;}
    }
}