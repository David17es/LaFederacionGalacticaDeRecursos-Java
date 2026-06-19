package Concurrencia;

import Modelo.DepositoOrbital;
import Modelo.EstadoGalaxia;
import Modelo.PlanetaMinero;
import Modelo.Zona;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Garantiza maximo un saqueador atacando una zona en un momento
 * Si la zona objetivo del saqueador esta siendo atacada, espera hasta que quede libre
 */
public class SincronizacionSaqueadores {

    // true = zona siendo atacada actualmente
    private final Map<Zona, Boolean> zonasOcupadas;

    private final Lock lock;
    private final Condition zonaLibre;

    public SincronizacionSaqueadores() {
        this.lock = new ReentrantLock();
        this.zonaLibre = lock.newCondition();
        this.zonasOcupadas = new HashMap<>();

        EstadoGalaxia estado = EstadoGalaxia.getInstancia();

        // Registrar zonas atacables
        for (PlanetaMinero planeta : estado.getPlanetas()) {
            zonasOcupadas.put(planeta, false);
        }
        for (DepositoOrbital deposito : estado.getDepositos()) {
            zonasOcupadas.put(deposito, false);
        }
    }

    //Ataca una zona. Si ya hay un saqueador, bloquea al hilo hasta que quede libre
    public void iniciarAtaque(Zona zona) throws InterruptedException {
        lock.lock();
        try {
            // Monitor de Mesa
            while (zonasOcupadas.getOrDefault(zona, false)) {
                zonaLibre.await();
            }
            zonasOcupadas.put(zona, true);
        } finally {
            lock.unlock();
        }
    }

    //Libera la zona al finalizar el ataque
    //Notifica a todos los saqueadores que esperan por esa zona
    public void finalizarAtaque(Zona zona) {
        lock.lock();
        try {
            zonasOcupadas.put(zona, false);
            zonaLibre.signalAll();
        } finally {
            lock.unlock();
        }
    }

    //Consulta si una zona esta siendo atacada, usado por HiloPatrulla
    public boolean estasiendoAtacada(Zona zona) {
        lock.lock();
        try {
            return zonasOcupadas.getOrDefault(zona, false);
        } finally {
            lock.unlock();
        }
    }
}