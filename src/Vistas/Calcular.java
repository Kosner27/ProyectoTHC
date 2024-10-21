package Vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Calcular extends JFrame{
    public JPanel PanelMain;
    public JMenuItem RegistrarInstitucion;
    public JMenuItem RegistrarEmisión;
    public JMenuItem Informes;
    public JMenuItem Graficos;
    public JMenuItem Reducir;
    public JPanel SubMain;
    public JComboBox institucion;
    public JTextField anio;
    public JTable Emisiones;
    public JTextField total;
    public JButton inicioButton;
    public JScrollPane Contenedor;
    public JComboBox fuente;
    public JButton guardarCalculoButton;
    public JLabel titulo;
    public JTextField Institucion;
    public JMenuItem VerPerfiles;
    public JMenuBar bar;
    public JMenuItem Calcular;
    public JMenuItem MasInformacion;
    public JMenuItem perfil;
    public JTextField sede;
    public JCheckBox siCheckBox;
    public JTextField nucleo;
    public JLabel Nucleo;
    public JCheckBox siRegistro;
    public JCheckBox noRegistro;
    public JComboBox comboNucleo;
    public JLabel Registro;
    public JLabel hayNucleo;
    public JButton actualizarButton;


    public Calcular(){

        setTitle("Calcular");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        Font font = new Font("Arial", Font.PLAIN, 14);
        titulo.setFont(font);
        setContentPane(PanelMain);
        Emisiones.setPreferredScrollableViewportSize(new Dimension(300, 300));
        Contenedor.setSize(200,200);
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nombre Fuente");
        model.addColumn("Estado Fuente");
        model.addColumn("Alcance");
        model.addColumn("Carga Ambiental");
        model.addColumn("Unidad Medida");
        model.addColumn("Factor Emisión");
        model.addColumn("Co2 aportado");
        Emisiones.setModel(model);

    }

}
