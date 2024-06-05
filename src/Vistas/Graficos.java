package Vistas;

import org.jfree.chart.ChartPanel;

import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;

import static javax.swing.JOptionPane.showMessageDialog;

public class Graficos extends JFrame {
    private JMenuItem RegistrarInstitucion;
    private JMenuItem RegistrarEmisión;
    private JMenuItem Calcular;
    private JMenuItem Informes;
    private JMenuItem Graficos;
    private JMenuItem Reducir;
    private JMenuItem MasInformacion;
    private JPanel PanelMain; // Asegúrate de declararlo como privado
    private JComboBox institucion;
    private JComboBox anio;
    private JPanel Submain;
    private JPanel PanelGrafico;
    private JPanel PanelGrafico2;
    private JButton buscarButton;
    private JButton inicioButton;
    private JLabel Comparar;
    private JLabel Tendencia;


    public Graficos(){
        setTitle("Graficos");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(650, 300);

        setContentPane(PanelMain);


        buscarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int alc1 = 500000;
                int alc2 = 303455;
                int n1 = 303455;
                int n2 = 200000;
                int n3 = 300000;
                if(institucion.getSelectedItem()=="" || anio.getSelectedItem()==""){
                    JOptionPane.showMessageDialog(PanelMain, "llenar campos");
                }else{
                    // Crear dataset para "Alcance"
                    DefaultPieDataset datosAlcance = new DefaultPieDataset();
                    datosAlcance.setValue("Alcance 1", alc1);
                    datosAlcance.setValue("Alcance 2", alc2);

                    // Crear gráfico de pastel para "Alcance"
                    JFreeChart grafico2 = ChartFactory.createPieChart(
                            "Alcance",
                            datosAlcance,
                            true,
                            true,
                            false
                    );

                    // Configurar etiquetas para mostrar porcentajes en "Alcance"
                    PiePlot plot2 = (PiePlot) grafico2.getPlot();
                    PieSectionLabelGenerator labelGenerator2 = new StandardPieSectionLabelGenerator(
                            "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());
                    plot2.setLabelGenerator(labelGenerator2);

                    ChartPanel panel2 = new ChartPanel(grafico2);
                    panel2.setMouseWheelEnabled(true);
                    panel2.setPreferredSize(new Dimension(300, 200));
                    PanelGrafico2.setLayout(new BorderLayout());
                    PanelGrafico2.removeAll(); // Remove any existing components
                    PanelGrafico2.add(panel2, BorderLayout.CENTER);

                    // Crear dataset para "Fuente de emision"
                    DefaultPieDataset datos = new DefaultPieDataset();
                    datos.setValue("electricidad", n1);
                    datos.setValue("Acpm", n2);
                    datos.setValue("Refrigerante", n3);

                    // Crear gráfico de pastel para "Fuente de emision"
                    JFreeChart grafico = ChartFactory.createPieChart(
                            "Fuente de emision",
                            datos,
                            true,
                            true,
                            false
                    );

                    // Configurar etiquetas para mostrar porcentajes en "Fuente de emision"
                    PiePlot plot = (PiePlot) grafico.getPlot();
                    PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                            "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());
                    plot.setLabelGenerator(labelGenerator);

                    ChartPanel panel = new ChartPanel(grafico); // Crear un ChartPanel para el gráfico
                    panel.setMouseWheelEnabled(true);
                    panel.setPreferredSize(new Dimension(300, 200));
                    PanelGrafico.setLayout(new BorderLayout()); // Establecer un BorderLayout en el PanelGrafico
                    PanelGrafico.removeAll(); // Remove any existing components
                    PanelGrafico.add(panel, BorderLayout.CENTER); // Agregar el panel al PanelGrafico en la posición norte

                    pack();
                    repaint();
                }



            }
        });
        Comparar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
               new CompararOtrarInstituciones();
               setVisible(false);
            }
        });
        Comparar.setVisible(true);

        Tendencia.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new GraficoTendencia();
                setVisible(false);
            }

        });
        Tendencia.setVisible(true);
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

        new Graficos();


    }
}
