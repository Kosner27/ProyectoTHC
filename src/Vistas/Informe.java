package Vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Informe extends JFrame {
    private JMenuItem RegistrarInstitucion;
    private JMenuItem RegistrarEmisión;
    private JMenuItem Calcular;
    private JMenuItem Informes;
    private JMenuItem Graficos;
    private JMenuItem Reducir;
    private JMenuItem MasInformacion;
    private JComboBox institucion;
    private JComboBox anio;
    private JButton buscarButton;
    private JScrollPane Contenedor;
    private JTable Emisiones;
    private JPanel PanelMain;
    private JButton inicioButton;
    private JButton descargarButton;
    private JTextField total;


    public Informe(){
        setTitle("Informes");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 500);
        setContentPane(PanelMain);
        Emisiones.setPreferredScrollableViewportSize(new Dimension(300, 300));
        Contenedor.setSize(1000,200);
        Contenedor.setVisible(true);
        buscarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(institucion.getSelectedItem()!="" && anio.getSelectedItem()!=""){
                    String Anio = (String) anio.getSelectedItem();
                    String[] columnNames = {"Nombre de la fuente de emision", "tipo fuente","Alcance","Cantidad Consumida", "unida de medidda", "Factor emision","Co2 aportado","año base"};
                    String[] rows = {"ACPM", "Combustible fosil","1","2,641.43", "gal", "10.15","26,832.1445",Anio};
                    DefaultTableModel tableModel = new DefaultTableModel(columnNames,0);
                    Emisiones.setModel(tableModel);
                    tableModel.addRow(rows);

                    Emisiones.getColumnModel().getColumn(0).setPreferredWidth(150);
                    Emisiones.getColumnModel().getColumn(3).setPreferredWidth(150);
                    Emisiones.getColumnModel().getColumn(4).setPreferredWidth(150);
                    Emisiones.getColumnModel().getColumn(5).setPreferredWidth(150);
                    total.setText("26,832.1445");
                }else{
                    JOptionPane.showMessageDialog(PanelMain, "Llene todo los campos");
                }

            }

        });
        inicioButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Inicio();
                setVisible(false);
            }
        });
        Inicio.getWindows();



        RegistrarInstitucion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new RegistrarInstitucion();
                setVisible(false);}
        });
        RegistrarInstitucion.setVisible(true);


        RegistrarEmisión.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Emision();
                setVisible(false);

            }
        });
        RegistrarEmisión.setVisible(true);

        Calcular.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Calcular();
                setVisible(false);
            }
        });
        Calcular.setVisible(true);
        MasInformacion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new MasInformacion();
                setVisible(false);
            }
        });
        MasInformacion.setVisible(true);
        Graficos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Graficos();
                setVisible(false);
            }
        });
        Graficos.setVisible(true);


        Reducir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Reducir2();
                setVisible(false);
            }
        });Reducir.setVisible(true);
        descargarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JOptionPane.showMessageDialog(null, "Se descargo satisfactoriamente");
            }
        });
    }
    public static void  main(String[]args ){

        new Informe();


    }
}
