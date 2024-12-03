package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.ConsultaUsuario;
import Modelo.modelo.ModeloUsuario;
import Vistas.LogIn;
import Vistas.Main;
import Vistas.RegistrarUsuario;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Controlador principal para manejar la interacción con la vista Main.
 * Este controlador gestiona las acciones de los botones de la vista Main,
 * como iniciar sesión y registrarse, redirigiendo a las vistas correspondientes.
 */
public class ControladorMain {
    private ModeloUsuario modeloUsuario;  // Objeto que maneja los datos del modeloUsuario
    private Main view;     // Vista principal

    /**
     * Constructor que inicializa el controlador con la vista principal y el modelo de modeloUsuario.
     * Asocia los eventos de los botones a sus respectivas acciones.
     * @param view Vista principal (Main).
     * @param mod Modelo de modeloUsuario.
     */
    public ControladorMain(Main view, ModeloUsuario mod) {
        this.view = view;
        this.modeloUsuario = mod;
        // Asocia los botones con las acciones
        this.view.iniciarSesionButton.addActionListener(this::actionPerformed);
        this.view.registrarseButton.addActionListener(this::actionPerformed);
    }

    /**
     * Método para iniciar la vista principal (Main).
     * Configura la ventana de la vista y la hace visible.
     */
    public void Iniciar() {
        view.setTitle("Inicio");  // Título de la ventana
        view.setVisible(true);    // Hace la ventana visible
        view.setSize(400, 300);   // Establece el tamaño de la ventana
        view.setLocationRelativeTo(null);  // Centra la ventana en la pantalla
        view.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  // Cierra la aplicación al cerrar la ventana
    }

    /**
     * Método que maneja los eventos de acción para los botones de la vista principal.
     * Dependiendo del botón presionado, redirige al modeloUsuario a la vista de login o registro.
     * @param e Evento generado por los botones.
     */
    public void actionPerformed(ActionEvent e) {
        // Si el modeloUsuario hace clic en el botón "Iniciar sesión"
        if (e.getSource() == view.iniciarSesionButton) {
            // Crea una nueva instancia de la vista de login
            LogIn inicio = new LogIn();
            Conexion conn = new Conexion();  // Establece la conexión a la base de datos
            ModeloUsuario mod = new ModeloUsuario();    // Crea un objeto modelo de modeloUsuario
            ConsultaUsuario consul = new ConsultaUsuario(conn);  // Crea un objeto de consultas de modeloUsuario
            // Crea el controlador de login y lo inicializa
            ControladorLogin login = new ControladorLogin(mod, inicio, consul);
            login.Iniciar();  // Inicia la vista de login
            view.dispose();   // Cierra la vista principal
        }

        // Si el modeloUsuario hace clic en el botón "Registrarse"
        if (e.getSource() == view.registrarseButton) {
            // Crea una nueva instancia de la vista de registro
            RegistrarUsuario registro = new RegistrarUsuario();
            Conexion conn = new Conexion();  // Establece la conexión a la base de datos
            ModeloUsuario mod = new ModeloUsuario();    // Crea un objeto modelo de modeloUsuario
            ConsultaUsuario consul = new ConsultaUsuario(conn);  // Crea un objeto de consultas de modeloUsuario
            // Crea el controlador de registro y lo inicializa
            ControladorRegistrarUsuario contro = new ControladorRegistrarUsuario(mod, registro, consul);
            contro.iniciar();  // Inicia la vista de registro
            registro.setVisible(true);  // Hace visible la vista de registro
            view.dispose();   // Cierra la vista principal
        }
    }
}
