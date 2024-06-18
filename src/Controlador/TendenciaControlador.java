package Controlador;
import Modelo.Conexion;
import Modelo.ConsultasTendencias;
import Modelo.TendenciaModelo;
import Vistas.GraficoTendencia;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.*;
public class TendenciaControlador {
    private TendenciaModelo mod;
    private ConsultasTendencias consul;
    private GraficoTendencia view;

    public TendenciaControlador(TendenciaModelo mod, ConsultasTendencias consul, GraficoTendencia view) {
        this.mod = mod;
        this.consul = consul;
        this.view = view;
        this.view.verButton.addActionListener(this::actionPerformed);


    }

    public void iniciar() {
        view.setTitle("Grafico Tendencia");
        view.setLocationRelativeTo(null);
        cargarInstitucion();

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.verButton) {
            view.image.setVisible(false);
            String nombreInstitucion = view.institucion.getSelectedItem().toString();
            if (!nombreInstitucion.isEmpty()) {
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                List<TendenciaModelo> mod = consul.getNombre(nombreInstitucion);
                for (TendenciaModelo dato : mod) {
                    String label = dato.getAlcance();
                    Integer anioBase = dato.getAnioBase();
                    Double Total = dato.getCo2();

                    if (anioBase != null && Total != null) {
                        dataset.setValue(Total,label,  anioBase.toString());

                    } else {
                        System.out.println("ERROR");
                    }
                    JFreeChart grafico =crearGraficoTendencia(dataset);
                    mostrarGrafico(grafico);
                }

            }
        }
    }

    private JFreeChart crearGraficoTendencia(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(
                "Tendencia de la huella de carbono",   // Título del gráfico
                "año",       // Etiqueta del eje X
                "Cantidad de co2",       // Etiqueta del eje Y
                dataset   // Conjunto de datos
        );
             return chart;
    }

    private void mostrarGrafico(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(300, 700));

        view.Grafico.setLayout(new BorderLayout());
        view.Grafico.removeAll();
        view.Grafico.add(panel,BorderLayout.CENTER);
        view.Grafico.revalidate();
        view.Grafico.repaint();

    }
        public void cargarInstitucion () {
            Conexion conexion = new Conexion();
            Connection conn = conexion.getConection();
            String sql = "CALL Seleccionarinstitucion()";

            view.institucion.removeAllItems();
            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rst = stmt.executeQuery(sql);
                    view.institucion.removeAllItems();
                    view.institucion.addItem("");
                    while (rst.next()) {
                        String nombre = rst.getString("NombreInstitucion");
                        view.institucion.addItem(nombre);
                    }
                    if (view.institucion.getItemCount() > 0) {
                        view.institucion.setSelectedIndex(0);
                    } else {
                        // Manejar el caso en el que el JComboBox está vacío
                        System.out.println("El JComboBox institucion está vacío");
                    }

                    rst.close();
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

