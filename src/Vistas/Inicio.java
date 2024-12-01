package Vistas;


import javax.swing.*;


public class Inicio extends JFrame {
    public JPanel panel1;
    public JMenuItem RegistrarInstitucion;
    public JMenuItem RegistrarEmision;
    public JMenuItem Calcular;
    public JMenuItem Informes;
    public JMenuItem Graficos;
    public JMenuItem Reducir;
    public JMenuItem MasInformacion;
    public JMenuBar bar;
    public JLabel imagen;
    public JMenuItem perfil;
    public JButton CerraSesion;
    public  JMenuItem VerPerfiles;
    public JMenuItem verInstitucion;


    public Inicio() {
        setTitle("Inicio");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setContentPane(panel1);

    }



}
