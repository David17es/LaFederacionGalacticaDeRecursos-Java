package Main;

import Concurrencia.*;
import GUI.VentanaPrincipalServidor;
import Log.LoggerFederacionImpl;
import Modelo.EstadoGalaxia;
import Remoto.ServidorRMI;

import javax.swing.*;

public class MainServidor {

    public static void main(String[] args) {

        // 1. Logger
        LoggerFederacionImpl logger = new LoggerFederacionImpl();

        // 2. Sincronizacion
        SincronizacionZonas sincZonas = new SincronizacionZonas();
        SincronizacionRecursos sincRecursos = new SincronizacionRecursos();
        SincronizacionSaqueadores sincSaq = new SincronizacionSaqueadores();

        // 3. Control de simulacion
        ControlSimulacion control = new ControlSimulacion();

        // 4. Servidor RMI
        ServidorRMI servidorRMI = new ServidorRMI(control, sincRecursos);
        servidorRMI.iniciar();

        // 5. GUI
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipalServidor ventana = new VentanaPrincipalServidor(control, sincRecursos, logger);
            
            logger.setVentana(ventana); //enlace GUI-logger

            ventana.setVisible(true);
            logger.registrarEvento("[Main] Simulación iniciada.");
        });

        // 6. Generadores de actores
        GeneradorDelegados generadorDel = new GeneradorDelegados(sincZonas, sincRecursos, control, logger);

        GeneradorPatrullas generadorPat = new GeneradorPatrullas(sincZonas, sincSaq, control, logger);

        GeneradorSaqueadores generadorSaq = new GeneradorSaqueadores(sincZonas, sincRecursos, sincSaq, control, logger);

        generadorDel.start();
        generadorPat.start();
        generadorSaq.start();
    }
}