package Vistas;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class RegistrarUsuario extends JFrame{
    public JTextField Nombre;
    public JTextField Apellido;
    public JTextField Correo;
    public JPasswordField Contrasena;
    public JComboBox Institucion;
    public JComboBox Rol;
    public JButton Registrar;
    public JPanel Principal;
    public JPanel Titulo;
    public JComboBox departamento;
    public JComboBox municipio;
    public JButton inicio;
    public JPasswordField NewPass;
    public JButton verPassC;
    private JButton verPassN;

    public RegistrarUsuario(){
        setTitle("Registrar Usuario");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setContentPane(Principal);

        verPassC.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                Contrasena.setEchoChar((char) 0);

            }
        });


        verPassC.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                Contrasena.setEchoChar('*');
            }
        });

        verPassN.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                NewPass.setEchoChar((char) 0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                NewPass.setEchoChar('*');
            }
        });
    }

}
