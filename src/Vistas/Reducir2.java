package Vistas;

import javax.swing.*;
import java.awt.*;

public class Reducir2 extends JFrame {
    public JMenuItem RegistrarInstitucion;
    public JMenuItem Graficos;
    public JComboBox anio;
    public JButton generarPlanDeAccion;
    public JTextArea introduccionLaHuellaDeTextArea;
    public JLabel Imagen;
    public JLabel Texto1;
    public JPanel PanelMain;
    public JScrollPane Contenedor;
    public JLabel titulo;
    public JButton inicioButton;
    public JTextField Institucio;
    public JComboBox municipio;
    public JButton Descargar;
    public JMenuBar bar;
    public JMenuItem RegistrarEmisión;
    public JMenuItem Calcular;
    public JMenuItem Informes;
    public JMenuItem Reducir;
    public JMenuItem MasInformacion;
    public JMenuItem perfil;
    public JMenuItem VerPerfiles;
    public JCheckBox SICheckBox;
    public JCheckBox NOCheckBox;
    public JComboBox comboNucleo;
    public JLabel Nucleo;

    public Reducir2() {
        setTitle("Reducir");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        Font font = new Font("Arial", Font.PLAIN, 14);
        titulo.setFont(font);
        setContentPane(PanelMain);


    }

}
