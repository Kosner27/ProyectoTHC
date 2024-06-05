package Vistas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class GraficoTendencia extends  JFrame {
    private JMenuItem RegistrarInstitucion;
    private JMenuItem RegistrarEmisión;
    private JMenuItem Calcular;
    private JMenuItem Informes;
    private JMenuItem Graficos;
    private JMenuItem Reducir;
    private JMenuItem MasInformacion;
    private JComboBox institucion;
    private JPanel Grafico;
    private JButton verButton;
    private JPanel PanelMain;
    private JButton inicioButton;
    private JButton atrasButton;

    public  GraficoTendencia(){
        setTitle("Tendencia");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(650, 500);
        setContentPane(PanelMain);

        verButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(institucion.getSelectedItem()!=""){
                    int n1= 2000000;
                    int n2= 3000000;
                    int n3= 4000000;
                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                    dataset.addValue(0, "Huella total de carbono", "2021");
                    dataset.addValue(n1, "Huella total de carbono", "2022");
                    dataset.addValue(n2, "Huella total de carbono", "2023");
                    dataset.addValue(n3, "Huella total de carbono", "2024");
                    dataset.addValue(0, "Alcance 1", "2021");
                    dataset.addValue(1000000, "Alcance 1", "2022");
                    dataset.addValue(1500000, "Alcance 1", "2023");
                    dataset.addValue(2000000, "Alcance 1", "2024");
                    dataset.addValue(0, "Alcance 2", "2021");
                    dataset.addValue(2500000, "Alcance 2", "2022");
                    dataset.addValue(3000000, "Alcance 2", "2023");
                    dataset.addValue(3500000, "Alcance 2", "2024");
                    dataset.addValue(0, "Alcance 3", "2021");
                    dataset.addValue(4000000, "Alcance 3", "2022");
                    dataset.addValue(4500000, "Alcance 3", "2023");
                    dataset.addValue(5000000, "Alcance 3", "2024");
                    JFreeChart chart = ChartFactory.createLineChart(
                            "Tendencia de la huella de carbono",   // Título del gráfico
                            "año",       // Etiqueta del eje X
                            "Cantidad de co2",       // Etiqueta del eje Y
                            dataset   // Conjunto de datos
                    );
                    ChartPanel panel = new ChartPanel(chart);
                    panel.setMouseWheelEnabled(true);
                    panel.setPreferredSize(new Dimension(400,400));
                    Grafico.setLayout(new BorderLayout());
                    Grafico.add(panel,BorderLayout.NORTH);
                    pack();
                    repaint();

                }else{
                    JOptionPane.showMessageDialog(PanelMain, "Seleccione una institución");
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

        inicioButton.setVisible(true);
        atrasButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Graficos();
                setVisible(false);
            }
        });
        atrasButton.setVisible(true);
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
        Informes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Informe();
                setVisible(false);
            }
        });
        Informes.setVisible(true);
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
    }
    public static void  main(String[]args ){

        new GraficoTendencia();


    }
}
