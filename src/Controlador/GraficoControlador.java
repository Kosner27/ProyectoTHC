package Controlador;

import Modelo.*;

import Vistas.Graficos;
import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfWriter;
import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.Page;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GraficoControlador {
    private final GraficorModelo mod;
    private final GraficoConsulta consul;
    private final Graficos view;
    private final InstitucionModelo mod2;

    public GraficoControlador(GraficorModelo mod, GraficoConsulta consul, Graficos view, InstitucionModelo mod2) {
        this.mod = mod;
        this.mod2 = mod2;
        this.consul = consul;
        this.view = view;

        this.view.buscarButton.addActionListener(this::actionPerformed);
        this.view.Descargar.addActionListener(this::actionPerformed);
    }

    private File capturarPanelComoImagen(JPanel panel, String nombreArchivo) throws IOException {
        BufferedImage imagen = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imagen.createGraphics();
        panel.paint(g2d);
        g2d.dispose();

        File archivo = new File(nombreArchivo);
        ImageIO.write(imagen, "png", archivo);
        return archivo;
    }


    public void iniciar() {
        view.setTitle("Grafico principal");
        view.setLocationRelativeTo(null);
        cargarInstitucion();
        cargarAnioBase();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.buscarButton) {
            view.imagen.setVisible(false);
            String nombreInstitucion = view.institucion.getSelectedItem().toString();
            String anioBase = view.anio.getSelectedItem().toString();

            if (!nombreInstitucion.isEmpty() && !anioBase.isEmpty()) {
                DefaultPieDataset datosAlcance = new DefaultPieDataset();
                DefaultPieDataset datosFuente = new DefaultPieDataset();
                List<GraficorModelo> datos = consul.GraficoDato(nombreInstitucion, anioBase);
                List<CalcularModelo> datos2 = consul.GraficoDato2(nombreInstitucion, anioBase);
                for (CalcularModelo dato : datos2) {
                    String fuente = dato.getNombreFuente();
                    Double total = dato.getTotal1();

                    if (fuente != null && total != null) {
                        datosFuente.setValue(fuente, total);
                    } else {
                        System.out.println("Datos incorectos " + fuente + total);
                    }
                }
                for (GraficorModelo dato : datos) {
                    String alcance = dato.getAlcance();
                    Double total = dato.getTotal();

                    // Verificar si alguno de los valores es nulo
                    if (alcance != null && total != null) {
                        datosAlcance.setValue(alcance, total);
                    } else {
                        System.out.println("Alcance o Total es nulo: Alcance = " + alcance + ", Total = " + total);
                    }
                }


                // Crear y mostrar el gráfico circular

                JFreeChart grafico = crearGraficoCircular(datosAlcance);
                JFreeChart grafico2 = crearGraficoFuente(datosFuente);

                mostrarGrafico(grafico);
                mostrarGrafico2(grafico2);


            } else {
                JOptionPane.showMessageDialog(null, "Debe seleccionar una institución y un año base.");
            }
        }

        if (e.getSource() == view.Descargar) {
            view.imagen.setVisible(false);
            String nombreInstitucion = view.institucion.getSelectedItem().toString();
            String anioBase = view.anio.getSelectedItem().toString();

            if (!nombreInstitucion.isEmpty() && !anioBase.isEmpty()) {
                DefaultPieDataset datosAlcance = new DefaultPieDataset();
                DefaultPieDataset datosFuente = new DefaultPieDataset();
                List<GraficorModelo> datos = consul.GraficoDato(nombreInstitucion, anioBase);
                List<CalcularModelo> datos2 = consul.GraficoDato2(nombreInstitucion, anioBase);
                for (CalcularModelo dato : datos2) {
                    String fuente = dato.getNombreFuente();
                    Double total = dato.getTotal1();

                    if (fuente != null && total != null) {
                        datosFuente.setValue(fuente, total);
                    } else {
                        System.out.println("Datos incorectos " + fuente + total);
                    }
                }
                for (GraficorModelo dato : datos) {
                    String alcance = dato.getAlcance();
                    Double total = dato.getTotal();

                    // Verificar si alguno de los valores es nulo
                    if (alcance != null && total != null) {
                        datosAlcance.setValue(alcance, total);
                    } else {
                        System.out.println("Alcance o Total es nulo: Alcance = " + alcance + ", Total = " + total);
                    }
                }
                String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                String b = view.institucion.getSelectedItem().toString() + fecha;

                JFileChooser f = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
                f.setFileFilter(filter);
                f.setDialogTitle("Guardar archivo PDF");
                f.setSelectedFile(new File(b + ".pdf"));
                int userSelection = f.showSaveDialog(null);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File c = f.getSelectedFile();
                    ExportarGraficos(datosAlcance, datosFuente, c);
                }

            }

        }
    }

    private void ExportarGraficos(DefaultPieDataset datosAlcance, DefaultPieDataset datosFuente,  File r) {
        JFreeChart chart = crearGraficoCircular(datosAlcance);
        JFreeChart chart2 = crearGraficoCircular(datosFuente);

        PDFDocument pdfDpc = new PDFDocument();
        try {


            // Crear y dibujar la primera página
            Page page = pdfDpc.createPage(new Rectangle(612, 792));
            PDFGraphics2D g2 = page.getGraphics2D();
            chart.draw(g2, new Rectangle(150, 0, 312, 168));

            // Crear y dibujar la segunda página
            Page page1 = pdfDpc.createPage(new Rectangle(612, 468));
            PDFGraphics2D g1 = page1.getGraphics2D();
            chart2.draw(g1, new Rectangle(0, 0, 612, 468));

            // Guardar el PDF en el archivo especificado
            pdfDpc.writeToFile(r);

        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
    private JFreeChart crearGraficoFuente (DefaultPieDataset dataset){
        JFreeChart chart = ChartFactory.createPieChart(
                "Emisiones por fuente",
                dataset,
                true,
                true,
                false
        );
        PiePlot plot2 = (PiePlot)chart.getPlot();
        PieSectionLabelGenerator labelGenerator2 = new StandardPieSectionLabelGenerator(
                "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());
        plot2.setLabelGenerator(labelGenerator2);
        return chart;
    }
    private JFreeChart crearGraficoCircular(DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Emisiones por Alcance",
                dataset,
                true,
                true,
                false
        );
        PiePlot plot2 = (PiePlot)chart.getPlot();
        PieSectionLabelGenerator labelGenerator2 = new StandardPieSectionLabelGenerator(
                "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());
        plot2.setLabelGenerator(labelGenerator2);
        return chart;
    }

    private void mostrarGrafico(JFreeChart chart) {
        ChartPanel panel2 = new ChartPanel(chart);
        panel2.setMouseWheelEnabled(true);
        panel2.setPreferredSize(new Dimension(300, 200));

        view.PanelGrafico2.setLayout(new BorderLayout());
        view.PanelGrafico2.removeAll(); // Remove any existing components
        view.PanelGrafico2.add(panel2, BorderLayout.CENTER);
        view.PanelGrafico2.revalidate(); // Actualiza el layout del panel
        view.PanelGrafico2.repaint();


    }

    private void mostrarGrafico2(JFreeChart chart){
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(300,200));
        view.PanelGrafico.setLayout(new BorderLayout());
        view.PanelGrafico.removeAll();
        view.PanelGrafico.add(panel, BorderLayout.CENTER);
        view.PanelGrafico.revalidate();
        view.PanelGrafico.repaint();

    }

    public void cargarInstitucion() {
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

    public void cargarAnioBase() {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        String sql = "CALL SeleccionarAnioBase()";

        view.anio.removeAllItems();
        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rst = stmt.executeQuery(sql);
                view.anio.removeAllItems();
                view.anio.addItem("");
                while (rst.next()) {
                    String nombre = rst.getString("anioBase");
                    view.anio.addItem(nombre);
                }
                if (view.anio.getItemCount() > 0) {
                    view.anio.setSelectedIndex(0);
                } else {
                    // Manejar el caso en el que el JComboBox está vacío
                    System.out.println("El JComboBox año está vacío");
                }

                rst.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}