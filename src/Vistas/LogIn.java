package Vistas;

import javax.swing.*;
import java.awt.event.ActionListener;

public class LogIn extends JFrame {
    public JPanel Titulo;
    public JTextField Correo;
    public JPasswordField Contrasena;
    public JButton iniciarSesionButton;
    public JButton olvidasteTuContrasenaButton;
    public JPanel Principal;
    public JButton clickAquiButton;
    public JButton inicioButton;

    public  LogIn(){

    setTitle("Registrar Usuario");
    setVisible(true);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setSize(500, 600);
    setLocationRelativeTo(null);
    setContentPane(Principal);





    }
    public void addEnterKeyListener(ActionListener listener) {
        Contrasena.addActionListener(listener);
        Correo.addActionListener(listener);
    }
}
