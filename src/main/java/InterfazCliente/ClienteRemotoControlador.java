package InterfazCliente;

import Remoto.EstadoRecursosDTO;
import Remoto.EstadoZonasDTO;
import Remoto.InterfazMonitorRemoto;

import java.rmi.Naming;

/**
 * Hilo controlador del cliente remoto
 * Se conecta al servidor RMI y consulta el estado de la simulacion cada 2 segundos, actualizando la GUI con esos nuevos datos
 * Si la conexion falla, reintenta en el siguiente ciclo sin detener la aplicacion
 */
public class ClienteRemotoControlador extends Thread {

    private static final int INTERVALO_MS = 2000;
    private static final String URL_RMI = "rmi://127.0.0.1/MonitorGalaxia";

    private InterfazMonitorRemoto monitor;
    private VentanaClienteRemoto ventana;
    private boolean activo;

    public ClienteRemotoControlador(VentanaClienteRemoto ventana) {
        this.ventana = ventana;
        this.monitor = null;
        this.activo  = true;
        setName("ClienteRemotoController");
        setDaemon(true);
    }

    @Override
    public void run() {
        //Intentar conexion inicial
        conectar();

        while (activo) {
            try {
                Thread.sleep(INTERVALO_MS);

                if (monitor == null) {
                    //Reintentar conexion si aun no hay monitor
                    conectar();
                    continue;
                }

                //Consultar estado al servidor
                EstadoRecursosDTO recursos = monitor.getEstadoRecursos();
                EstadoZonasDTO zonas = monitor.getEstadoZonas();

                // Actualizar GUI en el hilo de eventos
                javax.swing.SwingUtilities.invokeLater(() -> ventana.actualizarEstado(recursos, zonas)
                );

            } catch (java.rmi.RemoteException e) {
                //Conexion perdida: resetear monitor y reintentar
                monitor = null;
                javax.swing.SwingUtilities.invokeLater(() -> ventana.mostrarErrorConexion("Conexión perdida. Reintentando...")
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    //Intenta localizar el objeto remoto en el registro RMI. Si falla, deja monitor a null para reintentar en el siguiente ciclo
    private void conectar() {
        try {
            monitor = (InterfazMonitorRemoto) Naming.lookup(URL_RMI);
            javax.swing.SwingUtilities.invokeLater(() -> ventana.mostrarMensajeConexion("Conectado al servidor en " + URL_RMI)
            );
        } catch (Exception e) {
            monitor = null;
            javax.swing.SwingUtilities.invokeLater(() -> ventana.mostrarErrorConexion("No se pudo conectar: " + e.getMessage())
            );
        }
    }

    //Envia la orden de pausar al servidor remoto
    public void pausarRemoto() {
        if (monitor == null) return;
        try {
            monitor.pausarSimulacion();
        } catch (java.rmi.RemoteException e) {
            ventana.mostrarErrorConexion("Error al pausar: " + e.getMessage());
        }
    }

    //Envia la orden de reanudar
    public void reanudarRemoto() {
        if (monitor == null) return;
        try {
            monitor.reanudarSimulacion();
        } catch (java.rmi.RemoteException e) {
            ventana.mostrarErrorConexion("Error al reanudar: " + e.getMessage());
        }
    }

    //Detiene el hilo controlador limpiamente
    public void detener() {
        activo = false;
        interrupt();
    }
}