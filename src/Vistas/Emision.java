package Vistas;




import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class Emision extends JFrame {
    public JMenuItem RegistrarInstitucion;
    public JMenuItem Informes;
    public JMenuItem Graficos;
    public JMenuItem Reducir;
    public JComboBox Fuente;
    public JComboBox Estado;
    public JTextField nombre;
    public JComboBox Unidad;
    public JComboBox Alcance;
    public JTextField Factor;
    public JButton guardarButton;
    public JScrollPane Contenedor;
    public JTable Emisiones;
    public JButton buscarButton;
    public JButton inicioButton;
    public JButton guardarCambiosButton;
    public JButton eliminarButton1;
    public JPanel PanelMain;
    public JButton editar;
    public JLabel estado;
    public JLabel Titulo;
    public JMenuItem Calcular;
    public JMenuItem perfil;
    public JMenuBar bar;
    public JMenuItem RegistrarEmisión;
    public JMenuItem MasInformacion;
    public JMenuItem VerPerfiles;


    public Emision() {
        setTitle("Registrar Emisión");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(PanelMain);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        String[] columnNames = {"Fuente emision", "Estado", "Nombre fuente","Unidad medida", "Factor emision","Alcance"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames,0);
        Emisiones.setModel(tableModel);
        Emisiones.setSize(1000, 304);
        Contenedor.setSize(1000, 304);
        Font font = new Font("Arial", Font.PLAIN, 14);
        Titulo.setFont(font);

    }


}



