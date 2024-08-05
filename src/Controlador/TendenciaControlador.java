package Controlador;
import Modelo.Conexion;
import Modelo.ConsultasTendencias;
import Modelo.ModeloInforme;
import Modelo.TendenciaModelo;
import Vistas.GraficoTendencia;
import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.orsonpdf.Page;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.CategoryPlot;

import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TendenciaControlador {
    private TendenciaModelo mod;
    private ConsultasTendencias consul;
    private GraficoTendencia view;

    public TendenciaControlador(TendenciaModelo mod, ConsultasTendencias consul, GraficoTendencia view) {
        this.mod = mod;
        this.consul = consul;
        this.view = view;
        this.view.verButton.addActionListener(this::actionPerformed);
        this.view.descargarButton.addActionListener(this::actionPerformed);

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
        if(e.getSource()==view.descargarButton){
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
                String Nombre = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                String b = view.institucion.getSelectedItem().toString()+" "+Nombre;
                //codgio para eligir el lugar en donde se descarga el archivo
                JFileChooser fileChooser = new JFileChooser();

                FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
                fileChooser.setFileFilter(filter);

                fileChooser.setDialogTitle("Guardar archivo PDF");
                fileChooser.setSelectedFile(new File(b+ "GraficoHistórico" + ".pdf"));
                int userSelection = fileChooser.showSaveDialog(null);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File c = fileChooser.getSelectedFile();
                    ExportarGraficos(dataset, c);
                }
            }
        }
    }
private void ExportarGraficos(DefaultCategoryDataset a , File c ){
    Document document = new Document(PageSize.A3);
   try {

       JFreeChart chartDataset = crearGraficoTendencia(a);
       ByteArrayOutputStream chartStreamDataset = new ByteArrayOutputStream();
       ChartUtils.writeBufferedImageAsPNG(chartStreamDataset, chartDataset.createBufferedImage(600, 450));
       byte[] chartBytesDataset = chartStreamDataset.toByteArray();
       PdfWriter.getInstance(document, new FileOutputStream(c));
       document.open();
       List<ModeloInforme> z = consul.datos(view.institucion.getSelectedItem().toString());
       String fechaActual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
       Paragraph fecha = new Paragraph("Fecha de creación: " + fechaActual);
       fecha.setAlignment(Element.ALIGN_RIGHT);
       document.add(fecha);
       document.add(new Paragraph(" "));
       String nombre = view.institucion.getSelectedItem().toString();
       Paragraph nombreInstitucion = new Paragraph("Nombre de la Institucion: " + nombre);
       nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
       document.add(new Paragraph(("")));
       document.add(nombreInstitucion);
       document.add(new Paragraph("  "));
       for (ModeloInforme info : z) {
           String m = info.getMunicipio();
           Paragraph municipio = new Paragraph("Municipio: " + m);
           municipio.setAlignment(Element.ALIGN_LEFT);
           document.add(municipio);
           document.add(new Paragraph("  "));
           //////////////////////////////
           String d = info.getDepartamento();
           Paragraph departamento = new Paragraph("Departamento: " + d);
           departamento.setAlignment(Element.ALIGN_LEFT);
           document.add(departamento);
           document.add(new Paragraph("  "));
           ///////////////////////////////7
           String i = info.getNit();
           Paragraph nit = new Paragraph("Nit de la institución: " + i);
           nit.setAlignment(Element.ALIGN_LEFT);
           document.add(nit);
           document.add(new Paragraph("  "));
       }
       Paragraph text = new Paragraph("En el siguiente grafico se comporta de la siguiente manera, tiene un eje x que es el factor tiempo en años, y tiene un eje y que tiene la cantidad de co2 consumida de la institución. Además la grafica muestra cuatro lineas las cuales son: co2 emitido por las fuentes de alcance 1, alcance 2, alcance 3 y por último el total de los tres alcances juntos.");
       document.add(text);
       document.add(new Paragraph(" "));
       Image chartImageDataset = Image.getInstance(chartBytesDataset);
       chartImageDataset.setAlignment(Element.ALIGN_CENTER);
       document.add(chartImageDataset);
       JOptionPane.showMessageDialog(null, "PDF generado correctamente.");
   } catch (DocumentException ex) {
       throw new RuntimeException(ex);
   } catch (FileNotFoundException ex) {
       throw new RuntimeException(ex);
   } catch (IOException e) {
       throw new RuntimeException(e);
   } finally {
       document.close();
       System.out.println("Documento cerrado.");
   }
}
    private JFreeChart crearGraficoTendencia(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(
                "Tendencia de la huella de carbono",   // Título del gráfico
                "año",       // Etiqueta del eje X
                "Cantidad de co2",       // Etiqueta del eje Y
                dataset  // Conjunto de datos
        );
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, Color.RED);
        plot.getRenderer().setSeriesPaint(1, Color.BLUE);
        plot.getRenderer().setSeriesPaint(2, Color.BLACK);
        plot.getRenderer().setSeriesPaint(3, Color.MAGENTA);
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

