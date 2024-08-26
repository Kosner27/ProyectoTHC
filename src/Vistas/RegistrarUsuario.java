package Vistas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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

    public RegistrarUsuario(){
        setTitle("Registrar Usuario");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setContentPane(Principal);

        inicio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main inicio = new Main();
                inicio.setVisible(true);
                dispose();
            }
        });

    }

}
