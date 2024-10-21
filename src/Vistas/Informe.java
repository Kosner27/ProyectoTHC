package Vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Informe extends JFrame {
    public JMenuItem RegistrarInstitucion;
    public JMenuItem Graficos;
    public JComboBox institucion;
    public JComboBox anio;
    public JButton buscarButton;
    public JScrollPane Contenedor;
    public JTable Emisiones;
    public JPanel PanelMain;
    public JButton inicioButton;
    public JButton descargarButton;
    public JTextField total;
    public JLabel titulo;
    public JMenuItem RegistrarEmisión;
    public JMenuItem Calcular;
    public JMenuItem Reducir;
    public JMenuItem perfil;
   public JComboBox municipio;
    public JMenuBar bar;
    public JMenuItem Informes;
    public JMenuItem MasInformacion;
    public JMenuItem VerPerfiles;
    public JCheckBox noCheckBox;
    public JCheckBox siCheckBox;
    public JComboBox comboNucleo;
    public JLabel nucleo;


    public Informe(){
        setTitle("Informes");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        Font font = new Font("Arial", Font.PLAIN, 14);
        titulo.setFont(font);
        setContentPane(PanelMain);
        Emisiones.setPreferredScrollableViewportSize(new Dimension(400, 400));
        Contenedor.setSize(1000,200);
        Contenedor.setVisible(true);
        String[] columnNames = {"Nombre de la fuente de emision", "tipo fuente","Alcance","Cantidad Consumida", "unida de medidda", "Factor emision","Co2 aportado","año base"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames,0);
        Emisiones.setModel(tableModel);



    }

}
