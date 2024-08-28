package Vistas;

import Controlador.ControladoRegistrarUsuario;
import Controlador.ControladorLogin;
import Modelo.Conexion;
import Modelo.Consultas.ConsultaUsuario;
import Modelo.modelo.Rol;
import Modelo.modelo.UsuarioModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private JPanel Principal;
    private JButton iniciarSesionButton;
    private JButton registrarseButton;
    public Main(){
        setTitle("Inicio");
        setVisible(true);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setContentPane(Principal);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        registrarseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegistrarUsuario registro = new RegistrarUsuario();
                Conexion conn = new Conexion();
                UsuarioModel mod = new UsuarioModel();
                ConsultaUsuario consul = new ConsultaUsuario(conn);
                ControladoRegistrarUsuario contro = new ControladoRegistrarUsuario(mod,registro, consul);
                contro.iniciar();
                registro.setVisible(true);
                dispose();
            }
        });

        iniciarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogIn inicio = new LogIn();
                Conexion conn = new Conexion();
                UsuarioModel mod = new UsuarioModel();
                ConsultaUsuario consul = new ConsultaUsuario(conn);
                ControladorLogin login = new ControladorLogin (mod,inicio, consul);
                login.Iniciar();
                dispose();
            }
        });
    }
}
