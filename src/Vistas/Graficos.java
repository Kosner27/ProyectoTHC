package Vistas;

import javax.swing.*;

import static javax.swing.JOptionPane.showMessageDialog;

public class Graficos extends JFrame {
    public JPanel PanelMain; // Asegúrate de declararlo como privado
    public JComboBox institucion;
    public JComboBox anio;
    public JPanel Submain;
    public JPanel PanelGrafico;
    public JPanel PanelGrafico2;
    public JButton buscarButton;
    public JButton inicioButton;
    public JLabel imagen;
    public JMenu Graficos;
    public JLabel titulo;
    public JButton Descargar;
    public JMenuItem RegistrarInstitucion;
    public JMenuItem RegistrarEmision;
    public JMenuItem Calcular;
    public JMenuItem Informes;
    public JMenuItem Reducir;
    public JMenuItem perfil;
    public JComboBox municipio;
    public JMenuBar bar;
    public JMenuItem MasInformacion;
    public JMenuItem VerPerfiles;
    public JCheckBox siNucleo;
    public JCheckBox noNucleo;
    public JComboBox comboxNucleo;
    public JCheckBox siGeneral;
    public JCheckBox noGeneral;
    public JCheckBox noTieneNucleo;
    public JLabel Nucleo;
    public JLabel general;
    public JLabel SeleccionarNucleo;
    public JMenuItem verInstitucion;


    public Graficos() {
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setContentPane(PanelMain);


    }
}

