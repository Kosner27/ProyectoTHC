package Controlador;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase para monitorear la inactividad de la aplicación.
 */
public class MonitoreoInactividad {
    private static final int TIEMPO_INACTIVIDAD = 2 * 60 * 1000; // 5 minutos en milisegundos
    private Timer timer;
    private Runnable onInactividad;

    public MonitoreoInactividad(Runnable onInactividad) {
        this.onInactividad = onInactividad;
        this.timer = new Timer(TIEMPO_INACTIVIDAD, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Si se alcanza el tiempo de inactividad, ejecutar la acción
                onInactividad.run();
            }
        });
        this.timer.setRepeats(false); // Solo ejecuta una vez después de la inactividad
    }

    /**
     * Inicia el monitoreo de inactividad.
     */
    public void iniciar() {
        timer.start();
    }

    /**
     * Reinicia el timer de inactividad cada vez que haya actividad del modeloUsuario.
     */
    public void reiniciar() {
        timer.restart();
    }

    /**
     * Detiene el monitoreo de inactividad.
     */
    public void detener() {
        timer.stop();
    }
}
