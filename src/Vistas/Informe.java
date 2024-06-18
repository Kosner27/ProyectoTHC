package Vistas;

import Controlador.CalcularControlador;
import Controlador.EmisionControlador;
import Controlador.InstitucionControlador;
import Modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Informe extends JFrame {
    private JMenuItem RegistrarInstitucion;
    private JMenuItem RegistrarEmisión;
    private JMenuItem Calcular;
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
    private JMenuBar bar;
    private JMenuItem Informes;
    private JLabel titulo;


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
        JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
        JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
        JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");
        Graficos.add(GraficoPrincipal);
        Graficos.add(GraficosCompararInstitucion);
        Graficos.add(GraficoHistorico);
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



        MasInformacion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new MasInformacion();
                setVisible(false);
            }
        });
        MasInformacion.setVisible(true);
        GraficosCompararInstitucion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
                comIns.setVisible(true);
                dispose();
            }
        });

        GraficoHistorico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GraficoTendencia graf = new GraficoTendencia();
                graf.setVisible(true);
                dispose();
            }
        });

        GraficoPrincipal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vistas.Graficos graficos = new Graficos();
                graficos.setVisible(true);
                dispose();
            }
        });


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
