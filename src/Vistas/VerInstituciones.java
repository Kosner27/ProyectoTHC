package Vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VerInstituciones extends JFrame{
    public JPanel Main;
    public JPanel SubMain;
    public JMenuBar bar;
    public JMenuItem RegistrarInstitucion;
    public JMenuItem RegistrarEmision;
    public JMenuItem Calcular;
    public JMenuItem Informes;
    public JMenuItem Reducir;
    public JMenuItem MasInformacion;
    public JMenu Graficos;
    public JMenuItem perfil;
    public JMenuItem VerPerfiles;
    public JTable instituciontbl;
    public JButton buscarPorNombreButton;
    public JButton insertarButton;
    public JButton limpiarCamposButton;
    public JButton eliminarButton;
    public JButton guardarButton;
    public JButton editarButton;
    public JButton inicioButton;
    public JScrollPane Institucion;
    public JMenuItem verInstitucion;
    public JTextField nit;
    public JTextField nombreInstitucion;
    public JTextField departamento;
    public JTextField municipio;
    public JTextField campus;
    public JTextField hectareas;
    public JButton registrarNucleoButton;
    public JButton actualizarTablaButton;

    public VerInstituciones(){
        setTitle("Ver perfiles");
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setContentPane(Main);
        instituciontbl.setPreferredScrollableViewportSize(new Dimension(600, 300));
        instituciontbl.setFillsViewportHeight(true);
        String[] columnNames = {"Nombre de la Institución", "NIT", "Departamento", "Municipio", "Campus","Hectareas", "Hectareas Del Nucleo"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        instituciontbl.setModel(tableModel);


    }
}
