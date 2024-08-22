package Controlador;

import Modelo.*;

import Modelo.Consultas.GraficoConsulta;
import Modelo.modelo.CalcularModelo;
import Modelo.modelo.GraficorModelo;
import Modelo.modelo.InstitucionModelo;
import Modelo.modelo.ModeloInforme;
import Vistas.Graficos;

import com.itextpdf.text.*;
import com.itextpdf.text.Image;

import com.itextpdf.text.pdf.PdfWriter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;

import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;

import java.awt.event.ActionEvent;


import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
        try {
            //Se creo lo graficos
            JFreeChart chartAlcance = crearGraficoCircular(datosAlcance);
            JFreeChart chartFuente = crearGraficoFuente(datosFuente);

            // se pasason a imagenes
            ByteArrayOutputStream chartStreamAlcance = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamAlcance, chartAlcance.createBufferedImage(400, 200));
            byte[] chartBytesAlcance = chartStreamAlcance.toByteArray();

            ByteArrayOutputStream chartStreamFuente = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamFuente, chartFuente.createBufferedImage(400, 200));
            byte[] chartBytesFuente = chartStreamFuente.toByteArray();

            // Crear el PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(r));
            document.open();
            List<ModeloInforme> a = consul.datos(view.institucion.getSelectedItem().toString());
            String fechaActual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            // Añadir la fecha al documento
            Paragraph fecha = new Paragraph("Fecha de creación: " + fechaActual);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            document.add(fecha);
            document.add(new Paragraph(" ")); // Añadir espacio entre la fecha y la tabla
            String nombre = view.institucion.getSelectedItem().toString();
            Paragraph nombreInstitucion = new Paragraph("Nombre de la Institucion: " + nombre);
            nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
            document.add(new Paragraph(("")));
            document.add(nombreInstitucion);
            document.add(new Paragraph("  "));
            for (ModeloInforme info : a) {
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
            // Añadir los gráficos al PDF
            Image chartImageAlcance = Image.getInstance(chartBytesAlcance);
            chartImageAlcance.setAlignment(Element.ALIGN_CENTER);
            Paragraph textoALcance = new Paragraph( "En el siguiente vamos apreciar lo siguiente una suma total de las fuentes de emisión discriminada por alcance el cual se describe asi.\n" +
                    "Alcance 1: Corresponde al consumo de combustibles fósiles, y de refrigerantes.\n" +
                    "Alcance 2: Corresponde al consumo de la energía eléctrica.\n" +
                    "Alcance 3: Otras emisiones indirectas, consumo de materias primas e insumos, ademas de los viajes de negocios.\n");

            textoALcance.setAlignment(Element.ALIGN_LEFT);
            document.add(textoALcance);
            document.add(chartImageAlcance);
            document.add(new Paragraph("\n")); // Espacio entre gráficos

            Image chartImageFuente = Image.getInstance(chartBytesFuente);
            chartImageFuente.setAlignment(Element.ALIGN_CENTER);
            document.add(new Paragraph("En el siguiente grafico encontraremos todas las fuentes de emision que corresponden a la institución seleccionada con su respectivo año base, ademas se muestra el porcentaje de consumo de la fuente de emisión"));
            document.add(chartImageFuente);

            document.close();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
    private JFreeChart crearGraficoCircular(DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Emisiones por Alcance",  // Título del gráfico
                dataset,
                true,
                true,
                false
        );
        PiePlot plot = (PiePlot) chart.getPlot();



        // Formato personalizado para porcentaje con 3 decimales
        NumberFormat numberFormat = new DecimalFormat("0.000000%");

        // Generador de etiquetas para mostrar nombre de sección, valor absoluto y porcentaje
        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                "{0}=({2})", numberFormat, numberFormat);
        plot.setLabelGenerator(labelGenerator);

        // Configurar para ignorar valores cero
        plot.setIgnoreZeroValues(true);

        return chart;
    }

    private JFreeChart crearGraficoFuente(DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Emisiones por Fuente",  // Título del gráfico
                dataset,
                true,
                true,
                false
        );
        PiePlot plot = (PiePlot) chart.getPlot();

        // Formato personalizado para porcentaje con 3 decimales
        NumberFormat numberFormat = new DecimalFormat("0.000000%");

        // Generador de etiquetas para mostrar nombre de sección, valor absoluto y porcentaje
        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                "{0}=({2})", numberFormat, numberFormat);
        plot.setLabelGenerator(labelGenerator);

        // Configurar para ignorar valores cero
        plot.setIgnoreZeroValues(true);

        return chart;
    }

    private void mostrarGrafico(JFreeChart chart) {
        ChartPanel panel2 = new ChartPanel(chart);
        panel2.setMouseWheelEnabled(true);
        panel2.setPreferredSize(new Dimension(600, 400));

        view.PanelGrafico2.setLayout(new BorderLayout());
        view.PanelGrafico2.removeAll(); // Remove any existing components
        view.PanelGrafico2.add(panel2, BorderLayout.CENTER);
        view.PanelGrafico2.revalidate(); // Actualiza el layout del panel
        view.PanelGrafico2.repaint();


    }

    private void mostrarGrafico2(JFreeChart chart){
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(600,400));
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