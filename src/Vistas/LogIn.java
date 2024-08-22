package Vistas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogIn extends JFrame {
    private JPanel Titulo;
    private JTextField Correo;
    private JPasswordField Contrasena;
    private JButton iniciarSesionButton;
    private JButton olvidasteTúContraseñaButton;
    private JPanel Principal;
    private JButton clickAquíButton;
    private JButton inicioButton;

    public  LogIn(){

    setTitle("Registrar Usuario");
    setVisible(true);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setSize(500, 600);
    setLocationRelativeTo(null);
    setContentPane(Principal);

iniciarSesionButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Inicio app = new Inicio();
            app.setVisible(true);
            dispose();
        }
    });

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
