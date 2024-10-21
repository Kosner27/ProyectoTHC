package Vistas;



import javax.swing.*;
import java.awt.*;



public class GraficoTendencia extends  JFrame {
    public JMenuItem RegistrarInstitucion;
    public JMenuItem Graficos;
    public JMenuItem Reducir;
    public JComboBox institucion;
    public JPanel Grafico;
    public JButton verButton;
    public JPanel PanelMain;
    public JButton inicioButton;
    public JLabel image;
    public JPanel content;
    public JLabel titulo;
    public JButton descargarButton;
    public JMenuItem RegistrarEmisión;
    public JMenuItem Calcular;
    public JMenuItem Informes;
    public JMenuItem perfil;
    public JComboBox municipio;
    public JMenuBar bar;
    public JMenuItem MasInformacion;
    public JMenuItem VerPerfiles;
    public JCheckBox siCheckBox;
    public JCheckBox noCheckBox;
    public JComboBox comboNucleo;
    public JLabel Nucleo;

    public GraficoTendencia() {
        setTitle("Historico");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);
        Font font = new Font("Arial", Font.PLAIN, 14);
        titulo.setFont(font);
        setContentPane(PanelMain);


    }
}