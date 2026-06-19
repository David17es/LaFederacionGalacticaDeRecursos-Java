package Concurrencia;

import Modelo.DepositoOrbital;
import Modelo.EstadoGalaxia;
import Modelo.TipoRecurso;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Gestiona el acceso thread-safe a las unidades almacenadas en los depositos
 *
 * Protege dos operaciones criticas:
 *   depositar(): el delegado añade unidades al deposito
 *   vaciarPorSaqueo(): el saqueador retira unidades
 */
public class SincronizacionRecursos {

    private final Lock lock;
    private final Condition hayEspacio;
    private final Condition hayRecursos;

    public SincronizacionRecursos() {
        this.lock = new ReentrantLock();
        this.hayEspacio = lock.newCondition();
        this.hayRecursos = lock.newCondition();
    }

    //Deposita unidades en el deposito correspondiente al tipo de recurso indicado
    //Si no hay espacio suficiente, el hilo espera
    public void depositar(TipoRecurso tipoRecurso, int cantidad)
            throws InterruptedException {
        DepositoOrbital deposito = getDeposito(tipoRecurso);
        lock.lock();
        try {
            //Monitor de Mesa
            while (!deposito.tieneEspacio(cantidad)) {
                hayEspacio.await();
            }
            deposito.setUnidadesActuales(deposito.getUnidadesActuales() + cantidad);
            //Notificar a posibles saqueadores esperando recursos
            hayRecursos.signalAll();
        } finally {
            lock.unlock();
        }
    }

    //Retira hasta un 30% de las unidades actuales de un deposito durante un saqueo (min. 1)
    //Si el deposito esta vacio, el saqueador espera
    public int vaciarPorSaqueo(TipoRecurso tipoRecurso) throws InterruptedException {
        DepositoOrbital deposito = getDeposito(tipoRecurso);
        lock.lock();
        try {
            while (deposito.getUnidadesActuales() == 0) {
                hayRecursos.await();
            }
            int unidadesActuales = deposito.getUnidadesActuales();
            int robadas = Math.max(1, (int)(unidadesActuales * 0.30));
            deposito.setUnidadesActuales(unidadesActuales - robadas);
            // Notificar a delegados esperando espacio
            hayEspacio.signalAll();
            return robadas;
        } finally {
            lock.unlock();
        }
    }

    
    //Consulta de unidades actuales de un deposito
    //Usado por GUI, cliente remoto y generadores de actores
    public int getUnidadesActuales(TipoRecurso tipoRecurso) {
        DepositoOrbital deposito = getDeposito(tipoRecurso);
        lock.lock();
        try {
            return deposito.getUnidadesActuales();
        } finally {
            lock.unlock();
        }
    }

    //Comprueba si un deposito esta por debajo del umbral de alerta (10% de capacidad max). Usado por GeneradorDelegados
    public boolean hayDepositoBajoUmbral() {
        lock.lock();
        try {
            EstadoGalaxia estado = EstadoGalaxia.getInstancia();
            for (DepositoOrbital d : estado.getDepositos()) {
                if (d.estaPorDebajoUmbral()) {
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    // --- Auxiliar ---

    private DepositoOrbital getDeposito(TipoRecurso tipoRecurso) {
        EstadoGalaxia estado = EstadoGalaxia.getInstancia();
        switch (tipoRecurso) {
            case CRISTAL: return estado.getDepositoCristal();
            case MINERAL: return estado.getDepositoMineral();
            case PLASMA:  return estado.getDepositoPlasma();
            default: throw new IllegalArgumentException(
                "Tipo de recurso desconocido: " + tipoRecurso);
        }
    }
}