package Concurrencia;

import Log.LoggerFederacion;
import Modelo.EstadoGalaxia;
import Modelo.PatrullaFederal;

/**
 * Hilo generador de patrullas
 *
 * Responsabilidades:
 *   1. Crear las 2 patrullas iniciales
 *   2. Vigilar si hay 3 o mas saqueadores activos y menos de 5 patrullas en servicio
 *   3. Si se cumple la condicion, crear una nueva patrulla
 *
 * Intervalo de comprobacion: 8s
 * Limite de patrullas: 5
 * Saqueadores minimos para generar refuerzo: 3
 */
public class GeneradorPatrullas extends Thread {

    private static final int PATRULLAS_INICIALES = 2;
    private static final int MAX_PATRULLAS_ACTIVAS = 5;
    private static final int UMBRAL_SAQUEADORES = 3;
    private static final int INTERVALO_COMPROBACION_MS = 8000;

    private final EstadoGalaxia estado;
    private final SincronizacionZonas sincZonas;
    private final SincronizacionSaqueadores sincSaqueadores;
    private final ControlSimulacion control;
    private final LoggerFederacion logger;

    public GeneradorPatrullas(SincronizacionZonas sincZonas,SincronizacionSaqueadores sincSaqueadores, ControlSimulacion control, LoggerFederacion logger) {
        this.estado = EstadoGalaxia.getInstancia();
        this.sincZonas = sincZonas;
        this.sincSaqueadores = sincSaqueadores;
        this.control = control;
        this.logger = logger;
        setName("GeneradorPatrullas");
        setDaemon(true);
    }

    @Override
    public void run() {
        // --- Fase 1: patrullas iniciales ---
        for (int i = 0; i < PATRULLAS_INICIALES; i++) {
            crearYLanzarPatrulla();
        }
        logger.registrarEvento("GeneradorPatrullas: " + PATRULLAS_INICIALES + " patrullas iniciales creadas.");

        // --- Fase 2: vigilancia de refuerzos ---
        while (control.isSimulacionActiva()) {
            try {
                control.comprobarPausa();
                Thread.sleep(INTERVALO_COMPROBACION_MS);
                control.comprobarPausa();

                int numSaqueadores = estado.getNumeroSaqueadores();
                int numPatrullas = estado.getNumeroPatrullas();

                if (numSaqueadores >= UMBRAL_SAQUEADORES && numPatrullas < MAX_PATRULLAS_ACTIVAS) {
                    crearYLanzarPatrulla();
                    logger.registrarEvento("GeneradorPatrullas: refuerzo creado. " + "Saqueadores activos: " + numSaqueadores + " | Patrullas activas: " + (numPatrullas + 1));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    //Crea nueva patrulla, la registra en el estado global y lanza su hilo
    private void crearYLanzarPatrulla() {
        String id = estado.generarIdPatrulla();
        PatrullaFederal patrulla = new PatrullaFederal(id);
        estado.agregarPatrulla(patrulla);

        HiloPatrulla hilo = new HiloPatrulla(patrulla, sincZonas, sincSaqueadores, control, logger);
        hilo.start();
    }
}