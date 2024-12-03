package Controlador;
import Modelo.modelo.ModeloUsuario;
import Vistas.Loader;
import Vistas.Main;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase Principal que ejecuta la aplicación.
 * Este programa inicia un cargador (Loader) visual que aparece durante 5 segundos,
 * y luego carga la ventana principal (Main) y configura el controlador (ControladorMain) para la interfaz de modeloUsuario.
 */
public class Prinicipal {

    /**
     * Método principal que se ejecuta al iniciar la aplicación.
     *
     * 1. Muestra una ventana de carga (`Loader`) por 5 segundos.
     * 2. Después de los 5 segundos, cierra la ventana de carga y abre la ventana principal (`Main`).
     * 3. Configura el modelo de modeloUsuario (`ModeloUsuario`) y el controlador (`ControladorMain`),
     *    iniciando el proceso interactivo de la aplicación.
     */
    public static void main(String[] args) {

        // Se crea un hilo para ejecutar el proceso de carga y la ventana principal en segundo plano
        Runnable nRun = () -> {
            // Inicializa la ventana de carga y la hace visible
            Loader load = new Loader();
            load.setVisible(true);
            load.setLocationRelativeTo(null); // Centra la ventana de carga en la pantalla

            try {
                // Simula un tiempo de carga de 5 segundos
                Thread.sleep(5000);
            } catch (Exception ex) {
                // Si ocurre un error, se registra en el log
                Logger.getLogger(Prinicipal.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Cierra la ventana de carga
            load.dispose();

            // Inicializa la ventana principal de la aplicación
            Main inicio = new Main();

            // Crea un modelo de modeloUsuario
            ModeloUsuario mod = new ModeloUsuario();

            // Inicializa el controlador que gestionará la interacción entre la vista y el modelo
            ControladorMain main = new ControladorMain(inicio, mod);

            // Inicia el proceso de interacción de la ventana principal
            main.Iniciar();
        };

        // Crea y comienza un hilo para ejecutar el proceso de carga y la ventana principal en segundo plano
        Thread loader = new Thread(nRun);
        loader.start();
    }
}

