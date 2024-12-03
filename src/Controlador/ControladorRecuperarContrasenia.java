package Controlador;

import Modelo.Consultas.ConsultaUsuario;
import Modelo.modelo.HASH;
import Modelo.modelo.ModeloUsuario;
import Vistas.Main;
import Vistas.RecordarContrasena;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Controlador para gestionar el proceso de recuperación de contraseña del modeloUsuario.
 * Este controlador se encarga de la interacción con la vista `RecordarContrasena` y el modelo `ModeloUsuario`.
 * Realiza la verificación del correo electrónico del modeloUsuario, la actualización de la contraseña
 * y la navegación hacia la vista de inicio de sesión.
 */
public class ControladorRecuperarContrasenia {
    private ModeloUsuario modeloUsuario; // Modelo de modeloUsuario
    private ConsultaUsuario consultaUsuario; // Consultas relacionadas con el modeloUsuario
    private RecordarContrasena contrasena; // Vista para recordar la contraseña

    /**
     * Constructor que inicializa las dependencias del controlador.
     *
     * @param modeloUsuario Instancia del modelo `ModeloUsuario`.
     * @param consultaUsuario Instancia de la clase `ConsultaUsuario` para realizar consultas.
     * @param contrasena Instancia de la vista `RecordarContrasena`.
     */
    public ControladorRecuperarContrasenia(ModeloUsuario modeloUsuario, ConsultaUsuario consultaUsuario, RecordarContrasena contrasena) {
        this.modeloUsuario = modeloUsuario;
        this.consultaUsuario = consultaUsuario;
        this.contrasena = contrasena;
        listeners(); // Inicializa los oyentes de eventos
    }

    /**
     * Muestra la vista de recuperación de contraseña.
     */
    public void inicio() {
        contrasena.setVisible(true);
    }

    /**
     * Asocia los eventos de acción a los botones de la vista de recuperación de contraseña.
     * Cada botón tiene un `ActionListener` que responde a las acciones del modeloUsuario.
     */
    public void listeners() {
        this.contrasena.recordarButton.addActionListener(this::actionPerformed);
        this.contrasena.verificarCorreoButton.addActionListener(this::actionPerformed);
        this.contrasena.inicoButton.addActionListener(this::actionPerformed);
    }

    /**
     * Gestiona las acciones de los botones en la vista de recuperación de contraseña.
     * Verifica el correo, muestra los campos para cambiar la contraseña y realiza la actualización.
     *
     * @param e El evento generado por la acción del modeloUsuario.
     */
    public void actionPerformed(ActionEvent e) {
        // Obtiene el correo ingresado en el campo de texto
        String Correo = contrasena.textField1.getText();

        // Si se hace clic en el botón de "Verificar correo"
        if (e.getSource() == contrasena.verificarCorreoButton) {
            if (!Correo.isEmpty()) {
                if (consultaUsuario.esEmail(Correo)) { // Verifica si el correo tiene un formato válido
                    if (consultaUsuario.ExisteUsuario(Correo) == 1) { // Verifica si el modeloUsuario existe
                        // Muestra los campos para ingresar la nueva contraseña
                        contrasena.passC.setVisible(true);
                        contrasena.passN.setVisible(true);
                        contrasena.verPassC.setVisible(true);
                        contrasena.verPassN.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "El modeloUsuario no existe");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "El texto no está en un formato de correo (ejemplo@ejemplo.com)");
                }
            }
        }

        // Si se hace clic en el botón de "Recordar contraseña"
        if (e.getSource() == contrasena.recordarButton) {
            String pass1 = new String(contrasena.passN.getPassword());
            String pass2 = new String(contrasena.passC.getPassword());

            if (pass1.equals(pass2)) { // Verifica si las contraseñas coinciden
                String passCod = HASH.sha1(pass2); // Codifica la nueva contraseña
                // Actualiza la contraseña del modeloUsuario en la base de datos
                if (consultaUsuario.actualizarContrasena(Correo, passCod)) {
                    JOptionPane.showMessageDialog(null, "La actualización de la contraseña ha sido correcta");
                    limpiar(); // Limpia los campos de la vista
                } else {
                    JOptionPane.showMessageDialog(null, "Error en la consulta");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Los campos están vacíos o las contraseñas no coinciden");
            }
        }

        // Si se hace clic en el botón de "Inicio"
        if (e.getSource() == contrasena.inicoButton) {
            Main vista = new Main(); // Crea una nueva instancia de la vista de inicio
            ModeloUsuario user = new ModeloUsuario(); // Crea una nueva instancia del modelo de modeloUsuario
            ControladorMain controladorMain = new ControladorMain(vista, user); // Crea el controlador para la vista de inicio
            controladorMain.Iniciar(); // Inicia la vista de inicio
            contrasena.dispose(); // Cierra la ventana de recuperación de contraseña
        }
    }

    /**
     * Limpia los campos de la vista de recuperación de contraseña.
     * Oculta los campos de nueva contraseña.
     */
    public void limpiar() {
        contrasena.textField1.setText(""); // Limpia el campo de correo
        contrasena.passC.setText(""); // Limpia el campo de confirmación de contraseña
        contrasena.passN.setText(""); // Limpia el campo de nueva contraseña
        contrasena.passC.setVisible(false); // Oculta el campo de confirmación de contraseña
        contrasena.passN.setVisible(false); // Oculta el campo de nueva contraseña
    }
}

