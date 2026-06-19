package Concurrencia;

import Log.LoggerFederacion;
import Modelo.EstadoGalaxia;
import Modelo.PatrullaFederal;
import Modelo.Saqueador;
import Modelo.Zona;

import java.util.ArrayList;
import java.util.List;

/**
 * Hilo que ejecuta el ciclo de vida de una patrulla
 *
 * Ciclo:
 *   1. Preparar sistemas en el Hangar (3-6s) 
 *   2. Seleccionar aleatoriamente una zona a patrullar
 *   3. Patrullar la zona (4-8s)
 *   4. Si hay saqueador activo en la zona -> combatir (50/50):
 *        - Victoria: saqueador interrumpido, delegados expulsados, saqueador va a la base (20s penalizacion)
 *        - Derrota: patrulla va al Hangar (8-12  recuperacion)
 *   5. Volver al paso 2 si sigue activa
 */
public class HiloPatrulla extends Thread {

    private static final int T_HANGAR_MIN = 3000;
    private static final int T_HANGAR_RANGO = 3000;
    private static final int T_PATRULLA_MIN = 4000;
    private static final int T_PATRULLA_RANGO = 4000;
    private static final int T_RECUPERACION_MIN = 8000;
    private static final int T_RECUPERACION_RANGO = 4000;

    private final PatrullaFederal patrulla;
    private final EstadoGalaxia estado;
    private final SincronizacionZonas sincZonas;
    private final SincronizacionSaqueadores sincSaqueadores;
    private final ControlSimulacion control;
    private final LoggerFederacion logger;

    public HiloPatrulla(PatrullaFederal patrulla, SincronizacionZonas sincZonas, SincronizacionSaqueadores sincSaqueadores, ControlSimulacion control, LoggerFederacion logger) {
        this.patrulla = patrulla;
        this.estado = EstadoGalaxia.getInstancia();
        this.sincZonas = sincZonas;
        this.sincSaqueadores = sincSaqueadores;
        this.control = control;
        this.logger = logger;
        setName("Hilo-" + patrulla.getId());
    }

    @Override
    public void run() {
        logger.registrarEvento(patrulla.getId() + " comienza su actividad como patrulla federal.");

        try {
            // --- Fase inicial: preparar sistemas en el hangar ---
            patrulla.setZonaActual(estado.getHangarPatrullas());
            patrulla.setEstado("Preparando sistemas en Hangar");
            logger.registrarEvento(patrulla.getId() + " prepara sus sistemas en el Hangar de Patrullas.");

            long tHangar = T_HANGAR_MIN + (long)(Math.random() * T_HANGAR_RANGO);
            Thread.sleep(tHangar);

            // --- Ciclo principal de patrullaje ---
            while (control.isSimulacionActiva()) {
                control.comprobarPausa();

                //FASE 1: Seleccionar zona a patrullar
                Zona zonaObjetivo = seleccionarZonaAleatoria();
                patrulla.setEstado("Viajando a " + zonaObjetivo.getNombre());
                logger.registrarEvento(patrulla.getId() + " se dirige a patrullar " + zonaObjetivo.getNombre() + ".");

                //FASE 2: Entrar y patrullar la zona
                sincZonas.entrarZona(zonaObjetivo);
                patrulla.setZonaActual(zonaObjetivo);
                patrulla.setEstado("Patrullando " + zonaObjetivo.getNombre());

                try {
                    long tPatrulla = T_PATRULLA_MIN + (long)(Math.random() * T_PATRULLA_RANGO);
                    Thread.sleep(tPatrulla);
                    control.comprobarPausa();

                    //FASE 3: Comprobar si hay saqueador en esa zona
                    if (sincSaqueadores.estasiendoAtacada(zonaObjetivo)) {
                        combatir(zonaObjetivo);
                        // Si fue derrotada, salir del try para ir al hangar
                        if (patrulla.getEstado().equals("Derrotada")) {
                            sincZonas.salirZona(zonaObjetivo);
                            irARecuperacionHangar();
                            continue;
                        }
                    }
                } finally {
                    //Solo liberar si no fue liberada en combate
                    if (!patrulla.getEstado().equals("Derrotada") && patrulla.getZonaActual() == zonaObjetivo) {
                        sincZonas.salirZona(zonaObjetivo);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.registrarEvento(patrulla.getId() + " finaliza su actividad.");
    }

    // =========================================================
    // METODOS AUXILIARES
    // =========================================================

    //Resuelve el combate entre la patrulla y el saqueador activo en la zona indicada. Probabilidad 50/50 para cada bando
    private void combatir(Zona zona) throws InterruptedException {
        // Buscar el saqueador activo en la zona
        Saqueador objetivo = buscarSaqueadorEnZona(zona);
        if (objetivo == null) return;

        patrulla.setEnCombate(true);
        objetivo.setFaseActual(Saqueador.FaseAtaque.EN_COMBATE);
        logger.registrarEvento(patrulla.getId() + " inicia combate con " + objetivo.getId() + " en " + zona.getNombre() + ".");

        boolean victoriaPatrulla = Math.random() < 0.5;

        if (victoriaPatrulla) {
            // --- Victoria de la patrulla ---
            patrulla.registrarVictoria();
            patrulla.setEnCombate(false);
            patrulla.setEstado("Patrullando " + zona.getNombre());

            // El saqueador es derrotado: libera la zona y va a la base
            sincSaqueadores.finalizarAtaque(zona);
            objetivo.setFaseActual(Saqueador.FaseAtaque.EN_BASE);
            objetivo.setEstado("Derrotado - recuperándose en Base");

            // Expulsar delegados que estuvieran en la zona
            expulsarDelegadosDeZona(zona);

            logger.registrarEvento(patrulla.getId() + " derrota a " + objetivo.getId() + " en " + zona.getNombre() + ". Delegados de la zona expulsados.");

        } else {
            // --- Derrota de la patrulla ---
            patrulla.registrarDerrota();
            patrulla.setEnCombate(false);
            patrulla.setEstado("Derrotada");

            logger.registrarEvento(patrulla.getId() + " es derrotada por " + objetivo.getId() + " en " + zona.getNombre() + ". Va al Hangar a recuperarse.");
        }
    }

    //Envia la patrulla derrotada al hangar a recuperarse (8-12s)
    private void irARecuperacionHangar() throws InterruptedException {
        patrulla.setZonaActual(estado.getHangarPatrullas());
        patrulla.setEstado("Recuperándose en Hangar");
        patrulla.setEnRecuperacion(true);
        logger.registrarEvento(patrulla.getId() + " se recupera en el Hangar de Patrullas.");

        long tRec = T_RECUPERACION_MIN + (long)(Math.random() * T_RECUPERACION_RANGO);
        Thread.sleep(tRec);

        patrulla.setEnRecuperacion(false);
        patrulla.setEstado("Listo");
    }

    //Expulsa todos los delegados presentes en la zona atacada, e iran a la Zona de Recuperacion.
    private void expulsarDelegadosDeZona(Zona zona) {
        for (Modelo.DelegadoComercial d : estado.getDelegados()) {
            if (d.getZonaActual() != null && d.getZonaActual().equals(zona) && !d.isExpulsado()) {
                d.setExpulsado(true);
            }
        }
    }

    //Busca el saqueador activo (en fase SAQUEANDO o DESPLAZANDOSE) cuya zona objetivo coincide con la zona indicada
    private Saqueador buscarSaqueadorEnZona(Zona zona) {
        for (Saqueador s : estado.getSaqueadores()) {
            if (zona.equals(s.getObjetivo()) && s.estaAtacando()) {
                return s;
            }
        }
        return null;
    }

    //Selecciona aleatoriamente una zona entre los planetas y depositos del sistema
    private Zona seleccionarZonaAleatoria() {
        List<Zona> zonas = new ArrayList<>();
        zonas.addAll(estado.getPlanetas());
        zonas.addAll(estado.getDepositos());
        return zonas.get((int)(Math.random() * zonas.size()));
    }
}