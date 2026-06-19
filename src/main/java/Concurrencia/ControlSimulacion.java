package Concurrencia;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Controla el ciclo de vida global de la simulacion
 * 
 * Todos los hilos activos (delegados, patrullas, saqueadores y generadores) llaman a comprobarPausa()
 * Tambien controla la señal de parada total (simulacionActiva) para que los hilos terminen limpiamente cuando se termina el programa
 */
public class ControlSimulacion {

    private final Lock lock;
    private final Condition reanudada;

    private boolean pausada;
    private boolean simulacionActiva;

    public ControlSimulacion() {
        this.lock = new ReentrantLock();
        this.reanudada = lock.newCondition();
        this.pausada = false;
        this.simulacionActiva = false;
    }

    // =========================================================
    // ARRANQUE Y PARADA
    // =========================================================

    //Inicia la simulacion
    public void iniciar() {
        lock.lock();
        try {
            simulacionActiva = true;
            pausada = false;
            reanudada.signalAll();
        } finally {
            lock.unlock();
        }
    }

    //Detiene la simulacion
    public void detener() {
        lock.lock();
        try {
            simulacionActiva = false;
            pausada = false;
            // Desbloquea todos los hilos para que puedan terminar
            reanudada.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // =========================================================
    // PAUSA Y REANUDACION
    // =========================================================

    //Pausa la simulacion
    public void pausar() {
        lock.lock();
        try {
            pausada = true;
        } finally {
            lock.unlock();
        }
    }

    //Reanuda la simulacion pausada
    public void reanudar() {
        lock.lock();
        try {
            pausada = false;
            reanudada.signalAll();
        } finally {
            lock.unlock();
        }
    }

    //Punto de control que cada hilo llama en los momentos seguros de su ciclo (entre fases, antes de dormir o al cambiar de zona)
    //Si la simulacion esta pausada, el hilo se bloquea hasta que se reanude
    public void comprobarPausa() throws InterruptedException {
        lock.lock();
        try {
            while (pausada) {
                reanudada.await();
            }
        } finally {
            lock.unlock();
        }
    }

    // =========================================================
    // CONSULTAS DE ESTADO
    // =========================================================

    //Indica si la simulacion esta activa (no detenida)
    public boolean isSimulacionActiva() {
        lock.lock();
        try {
            return simulacionActiva;
        } finally {
            lock.unlock();
        }
    }

    //Indica si la simulacion esta pausada en el momento
    public boolean isPausada() {
        lock.lock();
        try {
            return pausada;
        } finally {
            lock.unlock();
        }
    }
}