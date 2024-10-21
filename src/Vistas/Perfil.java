package Vistas;


import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class Perfil extends JFrame {
    public JMenuItem RegistrarInstitucion;
    public JMenuItem RegistrarEmisión;
    public JMenuItem Calcular;
    public JMenuItem Informes;
    public JMenuItem Reducir;
    public JMenu Graficos;
    public JTextField nombre;
    public JTextField apellido;
    public JTextField correo;
    public JPasswordField newPass;
    public JPasswordField confirNewPass;
    public JTextField Uni;
    public JTextField Sede;
    public JButton editarButton;
    public JButton guardarCambiosButton;
    public JButton inicioButton;
    public JTextField rol;
    public JLabel Ro;
    public JPanel PanelMain;
    public JButton verPassN;
    public JButton verPassC;
    public JMenuBar bar;
    public JMenuItem MasInformacion;
    public JMenuItem perfil;
    public JMenuItem VerPerfiles;
    public Perfil (){
        setTitle("Ver perfil");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 500);
        setLocationRelativeTo(null);
        setContentPane(PanelMain);



        verPassC.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                confirNewPass.setEchoChar((char) 0);

            }
        });


        verPassC.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                confirNewPass.setEchoChar('*');
            }
        });

        verPassN.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                newPass.setEchoChar((char) 0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                newPass.setEchoChar('*');
            }
        });
    }

}
