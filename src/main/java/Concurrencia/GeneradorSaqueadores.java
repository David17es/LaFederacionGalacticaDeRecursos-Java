package Concurrencia;

import Log.LoggerFederacion;
import Modelo.EstadoGalaxia;
import Modelo.Saqueador;

/**
 * Hilo generador de saqueadores
 *
 * Genera un nuevo saqueador cada 10-20s), siempre que no existan mas de 40 saqueadores
 * El primer saqueador no se genera directamente, espera al intervalo
 */
public class GeneradorSaqueadores extends Thread {

    private static final int MAX_SAQUEADORES = 40;
    private static final int INTERVALO_MIN_MS = 10000;
    private static final int INTERVALO_RANGO_MS = 10000; // 10000 + random(10000)

    private final EstadoGalaxia estado;
    private final SincronizacionZonas sincZonas;
    private final SincronizacionRecursos sincRecursos;
    private final SincronizacionSaqueadores sincSaqueadores;
    private final ControlSimulacion control;
    private final LoggerFederacion logger;

    public GeneradorSaqueadores(SincronizacionZonas sincZonas,SincronizacionRecursos sincRecursos, SincronizacionSaqueadores sincSaqueadores, ControlSimulacion control, LoggerFederacion logger) {
        this.estado = EstadoGalaxia.getInstancia();
        this.sincZonas = sincZonas;
        this.sincRecursos = sincRecursos;
        this.sincSaqueadores = sincSaqueadores;
        this.control = control;
        this.logger = logger;
        setName("GeneradorSaqueadores");
        setDaemon(true);
    }

    @Override
    public void run() {
        while (control.isSimulacionActiva()) {
            try {
                // Espera aleatoria entre 10 y 20s
                long espera = INTERVALO_MIN_MS + (long)(Math.random() * INTERVALO_RANGO_MS);
                Thread.sleep(espera);
                control.comprobarPausa();

                int numActuales = estado.getNumeroSaqueadores();
                if (numActuales < MAX_SAQUEADORES) {
                    crearYLanzarSaqueador();
                    logger.registrarEvento("GeneradorSaqueadores: nuevo saqueador creado. " + "Total activos: " + (numActuales + 1));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    //Crea un saqueador, lo registra en el estado global y lanza su hilo
    private void crearYLanzarSaqueador() {
        String id = estado.generarIdSaqueador();
        Saqueador saqueador = new Saqueador(id);
        estado.agregarSaqueador(saqueador);

        HiloSaqueador hilo = new HiloSaqueador(
            saqueador, sincZonas, sincRecursos,
            sincSaqueadores, control, logger);
        hilo.start();
    }
}