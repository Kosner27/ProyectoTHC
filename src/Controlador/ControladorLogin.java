package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.ConsultaUsuario;
import Modelo.modelo.*;
import Vistas.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ControladorLogin {
    private final ModeloUsuario user; // Modelo de modeloUsuario
    private final LogIn view; // Vista de inicio de sesión
    private final ConsultaUsuario consul; // Consulta de usuarios
    private int intentosFallidos = 0; // Contador de intentos fallidos
    private static final int MAX_INTENTOS = 3; // Número máximo de intentos

    /**
     * Constructor del controlador.
     *
     * @param user   Modelo de modeloUsuario
     * @param view   Vista de inicio de sesión
     * @param consul Consultas relacionadas a usuarios
     */
    public ControladorLogin(ModeloUsuario user, LogIn view, ConsultaUsuario consul) {
        this.user = user;
        this.view = view;
        this.consul = consul;

        // Agregar listeners para eventos
        this.view.iniciarSesionButton.addActionListener(this::actionPerformed);
        this.view.addEnterKeyListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.clickAquiButton.addActionListener(this::actionPerformed);
        this.view.olvidasteTuContrasenaButton.addActionListener(this::actionPerformed);


    }

    /**
     * Inicializa la vista de inicio de sesión.
     */
    public void Iniciar() {
        view.setTitle("Login");
        view.setLocationRelativeTo(null);
    }

    /**
     * Maneja los eventos de acción.
     *
     * @param event Evento de acción
     */
    private void actionPerformed(ActionEvent event) {
        if (event.getSource() == view.iniciarSesionButton ||
                event.getSource() == view.Contrasena ||
                event.getSource() == view.Correo) {
            iniciarSesion();
            //monitoreo.detener();
        }
        if (event.getSource() == view.inicioButton) {
            inicio();
            //monitoreo.detener();
        }
        if (event.getSource() == view.clickAquiButton) {
            abrirRegistro();
           // monitoreo.detener();
        }
        if (event.getSource() == view.olvidasteTuContrasenaButton) {
            recordarContrasena();
           // monitoreo.reiniciar();
        }
    }

    /**
     * Abre la vista principal.
     */
    public void inicio() {
        Main inicio = new Main();
        ModeloUsuario user = new ModeloUsuario();
        ControladorMain contro = new ControladorMain(inicio, user);
        contro.Iniciar();
        view.dispose();
    }

    /**
     * Abre la vista de registro.
     */
    public void abrirRegistro() {
        Conexion con = new Conexion();
        ModeloUsuario user = new ModeloUsuario();
        RegistrarUsuario vista = new RegistrarUsuario();
        ConsultaUsuario consul = new ConsultaUsuario(con);
        ControladorRegistrarUsuario contro = new ControladorRegistrarUsuario(user, vista, consul);
        contro.iniciar();
        view.dispose();
    }

    /**
     * Abre la vista para recordar contraseña.
     */
    public void recordarContrasena() {
        Conexion con = new Conexion();
        ModeloUsuario user = new ModeloUsuario();
        ConsultaUsuario consul = new ConsultaUsuario(con);
        RecordarContrasena record = new RecordarContrasena();
        ControladorRecuperarContrasenia control = new ControladorRecuperarContrasenia(user, consul, record);
        control.inicio();
        view.dispose();
    }

    /**
     * Maneja el proceso de inicio de sesión.
     */
    private void iniciarSesion() {
        ModeloInstitucion ins = new ModeloInstitucion();
        ModeloMunicipio m = new ModeloMunicipio();
        String pass = new String(view.Contrasena.getPassword());
        String usuario = view.Correo.getText();

        // Verificar si los intentos fallidos superan el máximo permitido
        if (intentosFallidos == MAX_INTENTOS) {
            JOptionPane.showMessageDialog(null, "Demasiados intentos fallidos. La aplicación se cerrará.");
            System.exit(0); // Cierra la aplicación
        }

        if (validarCredenciales(usuario, pass)) {
            user.setCorreo(usuario);
            user.setContrasena(HASH.sha1(pass));

            if (consul.LogIn(user, ins, m)) {
                ControladorPestaniaPrincipal controladorPestaniaPrincipal = new ControladorPestaniaPrincipal(ins, user, m);
                controladorPestaniaPrincipal.inicio();
                view.dispose();
                JOptionPane.showMessageDialog(null, "Datos correctos");
                intentosFallidos = 0; // Restablecer los intentos fallidos
            } else {
                intentosFallidos++; // Incrementar los intentos fallidos
                JOptionPane.showMessageDialog(null, "Datos incorrectos. Intento " + intentosFallidos + " de " + MAX_INTENTOS);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Debe ingresar sus credenciales");
        }
    }

    /**
     * Validar las credenciales ingresadas.
     *
     * @param usuario   Nombre de modeloUsuario
     * @param contrasena Contraseña
     * @return true si son válidas, false en caso contrario.
     */
    private boolean validarCredenciales(String usuario, String contrasena) {
        return !usuario.isEmpty() && !contrasena.isEmpty();
    }
}
