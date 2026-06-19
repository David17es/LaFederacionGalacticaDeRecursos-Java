package Concurrencia;

import Modelo.EstadoGalaxia;
import Modelo.PlanetaMinero;
import Modelo.DepositoOrbital;
import Modelo.Zona;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Gestiona el control de acceso por capacidad a todas las zonas de la galaxia mediante semaforos
 * Cada zona con limite (planetas: max 4, depositos: max 3) tiene su propio Semaphore
 *
 * Orden: FIFO
 */
public class SincronizacionZonas {

    // Mapa zona -> semaforo de capacidad
    private final Map<Zona, Semaphore> semaforosCapacidad;

    public SincronizacionZonas() {
        semaforosCapacidad = new HashMap<>();
        EstadoGalaxia estado = EstadoGalaxia.getInstancia();

        // Planetas mineros (capacidad max. 4 cada uno)
        for (PlanetaMinero planeta : estado.getPlanetas()) {
            semaforosCapacidad.put(planeta, new Semaphore(planeta.getCapacidadMaxima(), true));
        }

        // Depositos (capacidad max. 3 cada uno)
        for (DepositoOrbital deposito : estado.getDepositos()) {
            semaforosCapacidad.put(deposito, new Semaphore(deposito.getCapacidadMaxima(), true));
        }
    }

    //Solicita acceso a una zona con limite de capacidad
    //Bloquea al hilo si la zona esta llena hasta que se libere un hueco
    public void entrarZona(Zona zona) throws InterruptedException {
        Semaphore sem = semaforosCapacidad.get(zona);
        if (sem != null) {
            sem.acquire();
        }
    }

    //Libera el hueco ocupado en una zona con límite de capacidad
    public void salirZona(Zona zona) {
        Semaphore sem = semaforosCapacidad.get(zona);
        if (sem != null) {
            sem.release();
        }
    }

    //Devuelve los huecos disponibles en una zona
    //Util para GUI y cliente remoto
    public int getHuecosDisponibles(Zona zona) {
        Semaphore sem = semaforosCapacidad.get(zona);
        if (sem != null) {
            return sem.availablePermits();
        }
        return -1;
    }
}