package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.ConsultaUsuario;
import Modelo.modelo.*;
import Vistas.*;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Controlador para la funcionalidad de inicio de sesión.
 */

public class ControladorLogin {
    private final Usuario user;// Modelo de usuario
    private final LogIn view;//Vista de inicio de sesión
    private final ConsultaUsuario consul;// Consulta de usuarios

    /**
     * Constructor del controlador.
     *
     * @param user   Modelo de usuario
     * @param view   Vista de inicio de sesión
     * @param consul Consultas relacionadas a usuarios
     */
    public ControladorLogin(Usuario user, LogIn view, ConsultaUsuario consul) {
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
        }
        if (event.getSource() == view.inicioButton) {
            inicio();
        }
        if (event.getSource() == view.clickAquiButton) {
            abrirRegistro();
        }
        if (event.getSource() == view.olvidasteTuContrasenaButton) {
            recordarContrasena();
        }

    }
    /**
     * Abre la vista principal.
     */
    public void inicio() {
        Main inicio = new Main();
        Usuario user = new Usuario();
        ControladorMain contro = new ControladorMain(inicio, user);
        contro.Iniciar();
        view.dispose();
    }
    /**
     * Abre la vista de registro.
     */
    public void abrirRegistro() {
        Conexion con = new Conexion();
        Usuario user = new Usuario();
        RegistrarUsuario vista = new RegistrarUsuario();
        ConsultaUsuario consul = new ConsultaUsuario(con);
        ControladoRegistrarUsuario contro = new ControladoRegistrarUsuario(user, vista, consul);
        contro.iniciar();
        view.dispose();
    }
    /**
     * Abre la vista para recordar contraseña.
     */
    public void recordarContrasena() {
        Conexion con = new Conexion();
        Usuario user = new Usuario();
        ConsultaUsuario consul = new ConsultaUsuario(con);
        RecordarContrasena record = new RecordarContrasena();
        RecuperadorControlador control = new RecuperadorControlador(user, consul, record);
        control.inicio();
        view.dispose();
    }
    /**
     * Maneja el proceso de inicio de sesión.
     */
    private void iniciarSesion(){
        InstitucionModelo ins = new InstitucionModelo();
        Municipio m = new Municipio();
        String pass = new String(view.Contrasena.getPassword());
        String usuario = view.Correo.getText();

        if (validarCredenciales(usuario, pass)) {
            user.setCorreo(usuario);
            user.setContrasena(HASH.sha1(pass));

            if (consul.LogIn(user, ins, m)) {
                ControladoInicio controladoInicio = new ControladoInicio(ins, user, m);
                controladoInicio.inicio();
                view.dispose();
                JOptionPane.showMessageDialog(null, "Datos correctos");
            } else {
                JOptionPane.showMessageDialog(null, "Datos incorrectos");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Debe ingresar sus credenciales");
        }
    }
    /**
     * Validar las credenciales ingresadas.
     *
     * @param usuario Nombre de usuario
     * @param contrasena Contraseña
     * @return true si son válidas, false en caso contrario.
     */
    private boolean validarCredenciales(String usuario, String contrasena) {
        return !usuario.isEmpty() && !contrasena.isEmpty();
    }
}
