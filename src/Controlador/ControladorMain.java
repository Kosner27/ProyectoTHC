package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.ConsultaUsuario;
import Modelo.modelo.InstitucionModelo;
import Modelo.modelo.Usuario;
import Modelo.modelo.Municipio;
import Vistas.LogIn;
import Vistas.Main;
import Vistas.RegistrarUsuario;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ControladorMain {
    private Usuario mod2;
    private  Main view;


    public ControladorMain( Main view, Usuario mod) {
        this.view = view;
        this.mod2 = mod;
        this.view.iniciarComoInvitadoButton.addActionListener(this::actionPerformed);
        this.view.iniciarSesionButton.addActionListener(this::actionPerformed);
        this.view.registrarseButton.addActionListener(this::actionPerformed);
    }
    public void Iniciar(){
        view.setTitle("Inicio");
        view.setVisible(true);
        view.setSize(500, 500);
        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==view.iniciarSesionButton){
            LogIn inicio = new LogIn();
            Conexion conn = new Conexion();
            Usuario mod = new Usuario();
            ConsultaUsuario consul = new ConsultaUsuario(conn);
            ControladorLogin login = new ControladorLogin (mod,inicio, consul);
            login.Iniciar();
            view.dispose();
        }if(e.getSource()== view.registrarseButton){
            RegistrarUsuario registro = new RegistrarUsuario();
            Conexion conn = new Conexion();
            Usuario mod = new Usuario();
            ConsultaUsuario consul = new ConsultaUsuario(conn);
            ControladoRegistrarUsuario contro = new ControladoRegistrarUsuario(mod,registro, consul);
            contro.iniciar();
            registro.setVisible(true);
            view.dispose();
        }

    }
}
