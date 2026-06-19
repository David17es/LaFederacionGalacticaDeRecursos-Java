package Remoto;

import Concurrencia.ControlSimulacion;
import Concurrencia.SincronizacionRecursos;
import Modelo.DepositoOrbital;
import Modelo.EstadoGalaxia;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementacion del monitor remoto
 * Delega todas las consultas en EstadoGalaxia y ControlSimulacion, que ya son thread-safe, por lo que no necesita sincronizacion extra
 */
public class MonitorRemotoImpl extends UnicastRemoteObject implements InterfazMonitorRemoto {

    private static final long serialVersionUID = 1L;

    private final EstadoGalaxia estado;
    private final ControlSimulacion control;
    private final SincronizacionRecursos sincRecursos;

    public MonitorRemotoImpl(ControlSimulacion control, SincronizacionRecursos sincRecursos) throws RemoteException {
        super(); // Registra el objeto como exportable
        this.estado = EstadoGalaxia.getInstancia();
        this.control = control;
        this.sincRecursos = sincRecursos;
    }

    @Override
    public EstadoRecursosDTO getEstadoRecursos() throws RemoteException {
        DepositoOrbital cristal = estado.getDepositoCristal();
        DepositoOrbital mineral = estado.getDepositoMineral();
        DepositoOrbital plasma = estado.getDepositoPlasma();

        return new EstadoRecursosDTO(
            sincRecursos.getUnidadesActuales(Modelo.TipoRecurso.CRISTAL),
            cristal.getCapacidadMaximaUnidades(),
            sincRecursos.getUnidadesActuales(Modelo.TipoRecurso.MINERAL),
            mineral.getCapacidadMaximaUnidades(),
            sincRecursos.getUnidadesActuales(Modelo.TipoRecurso.PLASMA),
            plasma.getCapacidadMaximaUnidades()
        );
    }

    @Override
    public EstadoZonasDTO getEstadoZonas() throws RemoteException {
        return new EstadoZonasDTO(
            estado.getNumeroDelegados(),
            estado.getNumeroDelegadosActivos(),
            estado.getNumeroPatrullas(),
            estado.getNumeroSaqueadores(),
            control.isPausada()
        );
    }

    @Override
    public void pausarSimulacion() throws RemoteException {
        control.pausar();
    }

    @Override
    public void reanudarSimulacion() throws RemoteException {
        control.reanudar();
    }

    @Override
    public boolean isPausada() throws RemoteException {
        return control.isPausada();
    }
}