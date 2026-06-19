package Main;

import InterfazCliente.VentanaClienteRemoto;

import javax.swing.*;

//Arranca la ventana del cliente
public class MainClienteRemoto {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaClienteRemoto ventana =
                new VentanaClienteRemoto();
            ventana.setVisible(true);
        });
    }
}