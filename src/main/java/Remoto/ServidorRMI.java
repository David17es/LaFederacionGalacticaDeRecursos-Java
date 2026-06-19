package Remoto;

import Concurrencia.ControlSimulacion;
import Concurrencia.SincronizacionRecursos;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

//Arranca el registro RMI y publica el objeto MonitorRemotoImpl para ser consultado
public class ServidorRMI {

    private static final int PUERTO_RMI  = 1099;
    private static final String NOMBRE_RMI  = "MonitorGalaxia";

    private final ControlSimulacion control;
    private final SincronizacionRecursos sincRecursos;
    
    private Registry registry;

    public ServidorRMI(ControlSimulacion control, SincronizacionRecursos sincRecursos) {
        this.control = control;
        this.sincRecursos = sincRecursos;
        this.registry = null;
    }

    //Crea el registro local y publica el objeto remoto, debe llamarse desde MainServidor antes de iniciar la simulacion
    public void iniciar() {
        try {
            //Crear registro local en puerto
            registry = LocateRegistry.createRegistry(PUERTO_RMI);

            //Crear e instanciar el objeto remoto
            MonitorRemotoImpl monitor = new MonitorRemotoImpl(control, sincRecursos);

            //Publicar el objeto en el registro con su nombre
            Naming.rebind("rmi://127.0.0.1/" + NOMBRE_RMI, monitor);

            System.out.println("ServidorRMI: objeto '" + NOMBRE_RMI + "' registrado en puerto " + PUERTO_RMI + ".");

        } catch (Exception e) {
            System.err.println("ServidorRMI: error al iniciar RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}