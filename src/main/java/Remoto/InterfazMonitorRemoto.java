package Remoto;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaz remota de la Federacion Galactica
 * Define los servicios que el servidor ofrece al cliente remoto:
 *   - Consulta del estado de recursos (depositos)
 *   - Consulta del estado de actores por zona
 *   - Control remoto de la simulacion (pausa/reanudación)
 */
public interface InterfazMonitorRemoto extends Remote {

    //Devuelve el estado actual de los tres depositos
    EstadoRecursosDTO getEstadoRecursos() throws RemoteException;

    //Devuelve los contadores de actores en el sistema
    EstadoZonasDTO getEstadoZonas() throws RemoteException;

    //Pausa la simulacion
    void pausarSimulacion() throws RemoteException;

    //Reanuda la simulacion pausada
    void reanudarSimulacion() throws RemoteException;

    //Indica si la simulacion esta pausada en el momento
    boolean isPausada() throws RemoteException;
}