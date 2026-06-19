package Concurrencia;

import Log.LoggerFederacion;
import Modelo.DelegadoComercial;
import Modelo.EstadoGalaxia;

/**
 * Hilo generador de delegados
 *
 * Responsabilidades:
 *   1. Crear 10 delegados iniciales
 *   2. Vigilar si algun deposito esta por debajo del umbral de alerta
 *   3. Si se cumple la condicion y hay menos de 20 delegados, crear un nuevo delegado y lanzar su hilo
 *
 * Intervalo comprobacion: 5s
 * Max delegados: 20
 */
public class GeneradorDelegados extends Thread {

    private static final int DELEGADOS_INICIALES = 10;
    private static final int MAX_DELEGADOS_ACTIVOS = 20;
    private static final int INTERVALO_COMPROBACION_MS = 5000;

    private final EstadoGalaxia estado;
    private final SincronizacionZonas sincZonas;
    private final SincronizacionRecursos sincRecursos;
    private final ControlSimulacion control;
    private final LoggerFederacion logger;

    public GeneradorDelegados(SincronizacionZonas sincZonas,SincronizacionRecursos sincRecursos, ControlSimulacion control, LoggerFederacion logger) {
        this.estado = EstadoGalaxia.getInstancia();
        this.sincZonas = sincZonas;
        this.sincRecursos = sincRecursos;
        this.control = control;
        this.logger = logger;
        setName("GeneradorDelegados");
        setDaemon(true);
    }

    @Override
    public void run() {
        // --- Fase 1: delegados iniciales ---
        for (int i = 0; i < DELEGADOS_INICIALES; i++) {
            crearYLanzarDelegado();
        }
        logger.registrarEvento("GeneradorDelegados: " + DELEGADOS_INICIALES + " delegados iniciales creados.");

        // --- Fase 2: vigilancia de umbrales ---
        while (control.isSimulacionActiva()) {
            try {
                control.comprobarPausa();
                Thread.sleep(INTERVALO_COMPROBACION_MS);
                control.comprobarPausa();

                if (sincRecursos.hayDepositoBajoUmbral()) {
                    int activos = estado.getNumeroDelegadosActivos();
                    if (activos < MAX_DELEGADOS_ACTIVOS) {
                        crearYLanzarDelegado();
                        logger.registrarEvento("GeneradorDelegados: nuevo delegado creado por umbral. " + "Total activos: " + (activos + 1));
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    //Crea un nuevo delegado, lo registra en el estado global y lanza su hilo
    private void crearYLanzarDelegado() {
        String id = estado.generarIdDelegado();
        DelegadoComercial delegado = new DelegadoComercial(id);
        estado.agregarDelegado(delegado);

        HiloDelegado hilo = new HiloDelegado(delegado, sincZonas, sincRecursos, control, logger);
        hilo.start();
    }
}