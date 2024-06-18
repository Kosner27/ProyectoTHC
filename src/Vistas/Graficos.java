package Vistas;

import Controlador.CalcularControlador;
import Controlador.EmisionControlador;
import Controlador.InstitucionControlador;
import Modelo.*;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;

import static javax.swing.JOptionPane.showMessageDialog;

public class Graficos extends JFrame {
    public JMenuItem RegistrarInstitucion;
    public JMenuItem RegistrarEmisión;
    public JMenuItem Calcular;
    public JMenuItem Informes;
    public JMenuItem Reducir;
    public JMenuItem MasInformacion;
    public JPanel PanelMain; // Asegúrate de declararlo como privado
    public JComboBox institucion;
    public JComboBox anio;
    public JPanel Submain;
    public JPanel PanelGrafico;
    public JPanel PanelGrafico2;
    public JButton buscarButton;
    public JButton inicioButton;
    public JLabel imagen;
    public JMenuBar bar;
    public JMenu Graficos;
    public JLabel titulo;


    public Graficos(){
        setTitle("Graficos");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        Font font = new Font("Arial", Font.PLAIN, 14);
        titulo.setFont(font);
        setContentPane(PanelMain);
        JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
        JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
        JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");
        Graficos.add(GraficoPrincipal);
        Graficos.add(GraficosCompararInstitucion);
        Graficos.add(GraficoHistorico);

       /* buscarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                imagen.setVisible(false);
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
        });*/

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

        new Graficos();


    }
}
