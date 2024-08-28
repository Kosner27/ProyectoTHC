package Vistas;

import Controlador.ControladorLogin;
import Modelo.Conexion;
import Modelo.Consultas.ConsultaUsuario;
import Modelo.modelo.Rol;
import Modelo.modelo.UsuarioModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogIn extends JFrame {
    public JPanel Titulo;
    public JTextField Correo;
    public JPasswordField Contrasena;
    public JButton iniciarSesionButton;
    public JButton olvidasteTúContraseñaButton;
    public JPanel Principal;
    public JButton clickAquíButton;
    public JButton inicioButton;

    public  LogIn(){

    setTitle("Registrar Usuario");
    setVisible(true);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setSize(500, 600);
    setLocationRelativeTo(null);
    setContentPane(Principal);



inicioButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        Main inicio = new Main();
        inicio.setVisible(true);
        dispose();
    }
});
}

}
