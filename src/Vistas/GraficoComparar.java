package Vistas;

import Controlador.CalcularControlador;
import Controlador.EmisionControlador;
import Controlador.InstitucionControlador;
import Modelo.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GraficoComparar extends JFrame {
    private JMenuItem RegistrarInstitucion;
    private JMenuItem RegistrarEmisión;
    private JMenuItem Calcular;
    private JMenuItem Informes;
    private JMenuItem Graficos;
    private JMenuItem Reducir;
    private JMenuItem MasInformacion;
    private JPanel Subpanel;
    private JPanel PanelGrafico;
    private JPanel PanelMain;
    private JButton descargarButton;
    private JButton atrasButton;
    private JButton inicioButton;
    private JMenuBar bar;

    public GraficoComparar(){
        setTitle("grafico comparar");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000,600);
        setLocationRelativeTo(null);
        setContentPane(PanelMain);
        int n1=303455;
        int n2=200000;
        int n3=300000;
        JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
        JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
        JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");
        Graficos.add(GraficoPrincipal);
        Graficos.add(GraficosCompararInstitucion);
        Graficos.add(GraficoHistorico);
        DefaultCategoryDataset datos= new DefaultCategoryDataset();
        datos.setValue(n1,"Institucion universitaria de envigado","ACPM");
        datos.setValue(n1,"Pascual Bravo","ACPM");
        datos.setValue(n1,"UDEA","ACPM");


        datos.setValue(n1,"Institucion universitaria de envigado","Gasolina Corriente");
        datos.setValue(n1,"Pascual Bravo","Gasolina Corriente");
        datos.setValue(n3,"UDEA","Gasolina Corriente");

        datos.setValue(n1,"Institucion universitaria de envigado","Gasolina Extra");
        datos.setValue(n1,"UDEA","Gasolina Extra");
        datos.setValue(n2,"Pascual Bravo","Gasolina Extra");

        datos.setValue(n2,"Pascual Bravo","Diesel");
        datos.setValue(n1,"Institucion universitaria de envigado","Diesel");
        datos.setValue(n1,"UDEA","Diesel");



        JFreeChart barras =  ChartFactory.createBarChart(
                "Comparativa entre instituciones por alcance especificando sus fuentes de emision",
                "Fuente por institucion",
                "CO2",
                datos,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        ChartPanel panel = new ChartPanel(barras);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(700,500));
        PanelGrafico.setLayout(new BorderLayout());
        PanelGrafico.add(panel,BorderLayout.NORTH);
        descargarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JOptionPane.showMessageDialog(null, "Se descargo satisfactoriamente");
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
        inicioButton.setVisible(true);


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


        MasInformacion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new MasInformacion();
                setVisible(false);
            }
        });
        MasInformacion.setVisible(true);
        Informes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Informe();
                setVisible(false);
            }
        });
        Informes.setVisible(true);



        Reducir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Reducir2();
                setVisible(false);
            }
        });Reducir.setVisible(true);

    }
    public static void  main(String[]args ){

        new GraficoComparar();


    }
}
