package Controlador;

import Modelo.*;

import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;

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


import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


public class GraficoControlador {
    private final GraficorModelo mod;
    private final GraficoConsulta consul;
    private final Graficos view;
    private final InstitucionModelo mod2;
    private final Municipio m;
    private final Usuario user;
    private final InstitucionModelo ins;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public GraficoControlador(GraficorModelo mod,
                              GraficoConsulta consul, Graficos view,
                              InstitucionModelo mod2, Municipio m,
                              Usuario user, InstitucionModelo ins) {
        this.mod = mod;
        this.mod2 = mod2;
        this.consul = consul;
        this.view = view;
        this.m = m;
        this.ins = ins;
        this.user = user;
        Listeners();
    }


    public void iniciar() {
        view.Graficos.add(GraficoPrincipal);
        view.Graficos.add(GraficosCompararInstitucion);
        view.Graficos.add(GraficoHistorico);
        String usuario = user.getTipoUsuario();
        switch (usuario) {
            case "Administrador":
                cargarInstitucion();
                cargarAnioBase();
                cargarMunicipio();
                System.out.println("Estoy en docente");
                view.setTitle("Grafico principal");
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                view.VerPerfiles.setVisible(false);
                view.setLocationRelativeTo(null);
                break;
            case "SuperAdmin":
                view.setTitle("Grafico principal");
                view.setLocationRelativeTo(null);
                cargarInstitucion();
                cargarAnioBase();
                cargarMunicipio();
                break;
            default:
                JOptionPane.showMessageDialog(null, "Usuario no definido en el sistema");
                break;

        }
        GraficosCompararInstitucion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vistaCompararInstituciones();
            }
        });
        GraficoHistorico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vistaGraficoHistorico();

            }

        });

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.buscarButton) {
            if (view.noTieneNucleo.isSelected()) {
                graficosSinNucleo();
            }
            if (view.siNucleo.isSelected()) {
                graficosConNucleo();
            }
            if(view.siGeneral.isSelected()){
                graficosConNucleoSumaTodosNucleos();
            }
        }
        if (e.getSource() == view.Descargar) {
            if (view.siNucleo.isSelected()) {
                DescargarConNucleo();
            }
            if (view.noTieneNucleo.isSelected()) {
                DescargasSinNucleo();
            }
        }
        if (e.getSource() == view.inicioButton) {
            BotonInicio();
        }
        if (e.getSource() == view.perfil) {
            vistaPerfil();
        }
        if (e.getSource() == view.Calcular) {
            vistaCalcular();
        }
        if (e.getSource() == view.RegistrarEmisión) {
            vistaRegistrarEmision();
        }
        if (e.getSource() == view.Informes) {
            vistaInforme();
        }
        if (e.getSource() == view.Reducir) {
            vistaReducir();
        }
        if (e.getSource() == view.RegistrarInstitucion) {
            vistaActualizarInstitucion();
        }
        if (e.getSource() == view.VerPerfiles) {
            vistaVerPerfiles();
        }
    }

    private void graficosSinNucleo() {
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String anioBase = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = String.valueOf(view.municipio.getSelectedItem());
        if (!nombreInstitucion.isEmpty() && !anioBase.isEmpty() && !NombreMuncipio.isEmpty()) {
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();
            List<GraficorModelo> datos = consul.GraficoPorAlcance(nombreInstitucion, anioBase, NombreMuncipio);
            List<CalcularModelo> datos2 = consul.GraficoPorFuente(nombreInstitucion, anioBase, NombreMuncipio);
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

    private void graficosConNucleo() {
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String anioBase = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = String.valueOf(view.municipio.getSelectedItem());
        String nucleo = String.valueOf(view.comboxNucleo.getSelectedItem());
        if (!nombreInstitucion.isEmpty() && !anioBase.isEmpty() && !NombreMuncipio.isEmpty() && !nucleo.isEmpty()) {
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();
            List<GraficorModelo> datos = consul.GraficoPorAlcanceNucleo(nombreInstitucion, anioBase, NombreMuncipio, nucleo);
            List<CalcularModelo> datos2 = consul.GraficoPorFuenteNucleo(nombreInstitucion, anioBase, NombreMuncipio, nucleo);
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
        }
    }

    private void graficosConNucleoSumaTodosNucleos() {
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String anioBase = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = String.valueOf(view.municipio.getSelectedItem());
        if (!nombreInstitucion.isEmpty() && !anioBase.isEmpty() && !NombreMuncipio.isEmpty()) {
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();
            List<GraficorModelo> datos = consul.GraficoPorAlcanceNucleoSumaTodosNucleos(nombreInstitucion, anioBase, NombreMuncipio);
            List<CalcularModelo> datos2 = consul.GraficoPorFuenteNucleoSumaTodosNucleos(nombreInstitucion, anioBase, NombreMuncipio);
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
        }
    }

    private void DescargasSinNucleo() {
        String nombreInstitucion = view.institucion.getSelectedItem().toString();
        String anioBase = view.anio.getSelectedItem().toString();
        String NombreMuncipio = view.municipio.getSelectedItem().toString();
        if (!nombreInstitucion.isEmpty() && !anioBase.isEmpty()) {
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();
            List<GraficorModelo> datos = consul.GraficoPorAlcance(nombreInstitucion, anioBase, NombreMuncipio);
            List<CalcularModelo> datos2 = consul.GraficoPorFuente(nombreInstitucion, anioBase, NombreMuncipio);
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
            String b = "GraficosDeLa" + view.institucion.getSelectedItem().toString() + fecha;

            JFileChooser f = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);
            f.setDialogTitle("Guardar archivo PDF");
            f.setSelectedFile(new File(b + ".pdf"));
            int userSelection = f.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = f.getSelectedFile();
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());
                ExportarGraficosSinNucleo(datosAlcance, datosFuente, file);
            }

        }
    }

    private void DescargarConNucleo() {
        String nombreInstitucion = Objects.requireNonNull(view.institucion.getSelectedItem()).toString();
        String anioBase = Objects.requireNonNull(view.anio.getSelectedItem()).toString();
        String NombreMuncipio = Objects.requireNonNull(view.municipio.getSelectedItem()).toString();
        String nombreN = Objects.requireNonNull(view.comboxNucleo.getSelectedItem()).toString();
        if (!nombreInstitucion.isEmpty() && !anioBase.isEmpty() && !nombreN.isEmpty()) {
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();
            List<GraficorModelo> datos = consul.GraficoPorAlcanceNucleo(nombreInstitucion, anioBase, NombreMuncipio, nombreN);
            List<CalcularModelo> datos2 = consul.GraficoPorFuenteNucleo(nombreInstitucion, anioBase, NombreMuncipio, nombreN);
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
            String b = "GraficosDeLa" + view.institucion.getSelectedItem().toString() + fecha + "DelNucleo" + nombreN;

            JFileChooser f = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);
            f.setDialogTitle("Guardar archivo PDF");
            f.setSelectedFile(new File(b + ".pdf"));
            int userSelection = f.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = f.getSelectedFile();
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());
                ExportarGraficos(datosAlcance, datosFuente, file);
            }

        }
    }

    private static File ensureUniqueFilename(File directory, String filename) {
        File file = new File(directory, filename);

        // Si el archivo no existe, simplemente devolvemos el archivo original
        if (!file.exists()) {
            return file;
        }

        // Separamos el nombre del archivo y la extensión
        String baseName = filename;
        String extension = "";
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = filename.substring(0, dotIndex);
            extension = filename.substring(dotIndex);
        }

        // Generamos un nuevo nombre de archivo con un contador
        int counter = 1;
        String newFilename;
        do {
            newFilename = baseName + "(" + counter + ")" + extension;
            file = new File(directory, newFilename);
            counter++;
        } while (file.exists());

        return file;
    }

    private void ExportarGraficos(DefaultPieDataset datosAlcance, DefaultPieDataset datosFuente, File r) {
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
            List<ModeloInforme> a = consul.datos(view.institucion.getSelectedItem().toString(), m.getNombreM());
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
            String m = view.municipio.getSelectedItem().toString();
            Paragraph municipio = new Paragraph("Municipio: " + m);
            municipio.setAlignment(Element.ALIGN_LEFT);
            document.add(municipio);
            document.add(new Paragraph("  "));
            String nm = view.comboxNucleo.getSelectedItem().toString();
            Paragraph nucleo = new Paragraph("Nucleo " + nm);
            nucleo.setAlignment(Element.ALIGN_LEFT);
            document.add(nucleo);
            document.add(new Paragraph("  "));
            for (ModeloInforme info : a) {

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
            Paragraph textoALcance = new Paragraph("En el siguiente vamos apreciar lo siguiente una suma total de las fuentes de emisión discriminada por alcance el cual se describe asi.\n" +
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
            JOptionPane.showMessageDialog(null, "Pdf guardado correctamente ");
            document.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    private void ExportarGraficosSinNucleo(DefaultPieDataset datosAlcance, DefaultPieDataset datosFuente, File r) {
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
            List<ModeloInforme> a = consul.datos(view.institucion.getSelectedItem().toString(), m.getNombreM());
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
            String m = view.municipio.getSelectedItem().toString();
            Paragraph municipio = new Paragraph("Municipio: " + m);
            municipio.setAlignment(Element.ALIGN_LEFT);
            document.add(municipio);
            document.add(new Paragraph("  "));
            for (ModeloInforme info : a) {

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
            Paragraph textoALcance = new Paragraph("En el siguiente vamos apreciar lo siguiente una suma total de las fuentes de emisión discriminada por alcance el cual se describe asi.\n" +
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
            JOptionPane.showMessageDialog(null, "Pdf guardado correctamente ");
            document.close();
        } catch (Exception e) {
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

        // Formato personalizado para porcentaje con 6 decimales
        NumberFormat numberFormat = new DecimalFormat("0.000000%");

        // Generador de etiquetas para mostrar nombre de sección, valor absoluto y porcentaje
        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})", // {0} = nombre, {1} = valor absoluto, {2} = porcentaje
                numberFormat,
                numberFormat
        );
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
                "{0}= {1} ({2})", numberFormat, numberFormat);
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

    private void mostrarGrafico2(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(600, 400));
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
        ResultSet st = null;
        Connection conn = conexion.getConection();
        String sql = " Select  ei.anioBase from emisioninstitucion ei inner join institucion i on ei.idInsititucion=i.idInstitucionAuto inner join emision e on ei.idEmision = e.idEmision where i.NombreInstitucion = ?  group by anioBase";
        String nombre = String.valueOf(view.institucion.getSelectedItem());
        view.anio.addItem(" ");
        view.anio.removeAllItems();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nombre);
                st = ps.executeQuery();
                while (st.next()) {
                    String anioBase = st.getString(1);
                    view.anio.addItem(" ");
                    view.anio.addItem(anioBase);
                }
                if (view.anio.getItemCount() > 0) {
                    view.anio.setSelectedIndex(0);
                } else {
                    // Manejar el caso en el que el JComboBox está vacío
                    System.out.println("El JComboBox año está vacío");
                }

                st.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void anioBaseNucleo() {
        view.anio.removeAllItems(); // Limpiar el combo de años
        view.anio.addItem(" "); // Agregar un texto indicativo

        String sql = "SELECT anioBase " +
                "FROM emisionnucleo en " +
                "INNER JOIN nucleoinstitucion n ON en.IdNucleo = n.IdNucleo " +
                "INNER JOIN emision e ON en.idEmision = e.idEmision " +
                "INNER JOIN institucion i ON n.idInstitucion = i.IdInstitucionAuto " +
                "INNER JOIN municipioinstitiucion mi ON i.idInstitucionAuto = mi.IdInstitucion " +
                "INNER JOIN municipio m ON mi.IdMuncipio = m.IdMunicipio " +
                "WHERE i.NombreInstitucion = ? AND m.NombreMunicipio = ? AND n.NombreNucleo = ? " +
                "group by en.anioBase ORDER BY en.anioBase ;";

        String nombre = String.valueOf(view.institucion.getSelectedItem());
        String municipio = String.valueOf(view.municipio.getSelectedItem());
        String nucleo = String.valueOf(view.comboxNucleo.getSelectedItem());

        Conexion conexion = new Conexion();
        try (Connection conn = conexion.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, municipio);
            ps.setString(3, nucleo);

            try (ResultSet st = ps.executeQuery()) {
                while (st.next()) {
                    String anioBase = st.getString(1);
                    view.anio.addItem(anioBase);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cargarMunicipio() {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        view.municipio.removeAllItems(); // Limpiar los elementos del JComboBox
        view.municipio.addItem("");

        // Obtener el departamento seleccionado directamente del JComboBox
        String Institucion = (String) view.institucion.getSelectedItem();

        if (Institucion != null && !Institucion.isEmpty()) {
            try {
                // Cerrar la conexión existente antes de abrir una nueva
                if (conn != null) {
                    // Crear una nueva conexión para obtener los municipios del departamento seleccionado
                    String procedureCall = "Select NombreMunicipio from municipio m inner join  municipioinstitiucion mi on mi.idMuncipio= m.idMunicipio  inner join institucion i on  i.idInstitucionAuto = mi.IdInstitucion where i.NombreInstitucion = ?";

                    try (CallableStatement statement = conn.prepareCall(procedureCall)) {
                        statement.setString(1, Institucion); // Establecer el valor del parámetro

                        // Ejecutar la consulta
                        ResultSet rs = statement.executeQuery();

                        // Verificar si el ResultSet está vacío
                        if (!rs.isBeforeFirst()) {
                            System.out.println("No se encontraron municipios para el departamento seleccionado.");
                        } else {
                            // Llenar el JComboBox con los municipios obtenidos de la consulta
                            while (rs.next()) {
                                String nombreMunicipio = rs.getString("NombreMunicipio");
                                view.municipio.addItem(nombreMunicipio);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(view.PanelMain, "Error al cargar los municipios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Asegúrate de cerrar la conexión
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void cargarNucleosExistentes() {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        view.comboxNucleo.removeAllItems();
        view.comboxNucleo.addItem(" ");
        String nombreInstitucion = view.institucion.getSelectedItem().toString();
        System.out.println(nombreInstitucion);
        try {
            if (conn != null) {
                String procedureCall = "Select NombreNucleo from emisionnucleo en inner join nucleoinstitucion ni on en.IdNucleo = ni.IdNucleo inner join institucion i on ni.idInstitucion = i.idInstitucionAuto where i.NombreInstitucion = ? group by NombreNucleo";
                try (CallableStatement statement = conn.prepareCall(procedureCall)) {
                    statement.setString(1, nombreInstitucion);
                    ResultSet rs = statement.executeQuery();
                    while (rs.next()) {
                        String nombreNucleo = rs.getString("NombreNucleo");
                        view.comboxNucleo.addItem(nombreNucleo);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view.PanelMain, "Error al cargar los municipios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Asegúrate de cerrar la conexión
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void BotonInicio() {
        ControladoInicio control = new ControladoInicio(ins, user, m);
        System.out.println(ins.getNombreInstitucion());
        control.inicio();
        view.dispose();
    }

    public void vistaPerfil() {
        Conexion conn = new Conexion();
        Perfil per = new Perfil();
        ConsultaUsuario cons = new ConsultaUsuario(conn);
        PerfilCOntrolador control = new PerfilCOntrolador(user, per, ins, m, cons);
        control.Iniciar();
        per.setVisible(true);
        view.dispose();
    }

    public void vistaCalcular() {
        Conexion con = new Conexion();
        Vistas.Calcular viewCal = new Calcular();
        CalcularModelo mod = new CalcularModelo();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        CalcularControlador controlador = new CalcularControlador(mod, consul, viewCal, ins, consultaUsuario, user, m);
        controlador.iniciar();
        viewCal.setVisible(true);
        view.dispose();
    }

    public void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        EmisionModelo mod = new EmisionModelo();
        ConsultasEmision consul = new ConsultasEmision();
        EmisionControlador controlador = new EmisionControlador(mod, consul, emisionView, ins, m, user);
        controlador.iniciar();
        emisionView.setVisible(true);
        view.dispose();
    }

    public void vistaInforme() {
        Conexion con = new Conexion();
        InstitucionModelo mod2 = new InstitucionModelo();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloInforme mod = new ModeloInforme();
        Informe viewInfo = new Informe();
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorInforme contro = new ControladorInforme(viewInfo, mod, consul, m, ins, user);
        contro.iniciar();
        viewInfo.setVisible(true);
        view.dispose();
    }

    public void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar viewGraf = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        GraficoCompararModelo mod = new GraficoCompararModelo();
        ComparaInstitucion contro = new ComparaInstitucion(mod, consultas, comIns, viewGraf, ins, m, user);
        contro.iniciar();
        view.dispose();
    }

    public void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        TendenciaModelo mod = new TendenciaModelo();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia viewGraf = new GraficoTendencia();
        TendenciaControlador control = new TendenciaControlador(mod, consult, viewGraf, m, ins, user);
        viewGraf.setVisible(true);
        control.iniciar();
        view.dispose();
    }

    public void vistaReducir() {
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(ins, consul, vista, m, user);
        redu.Iniciar();
        view.dispose();
    }

    public void vistaActualizarInstitucion() {
        Conexion con = new Conexion();
        ConsultasInstitucion consul = new ConsultasInstitucion();
        RegistrarInstitucion view2 = new RegistrarInstitucion();
        InstitucionModelo mod = new InstitucionModelo();
        InstitucionControlador control = new InstitucionControlador(ins, m, view2, consul, mod, user);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();


    }

    public void vistaVerPerfiles() {
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);

        VerPerfilesControlador verControl = new VerPerfilesControlador(user, ins, m, verPerfiles, consul);
        verControl.Iniciar();
        view.dispose();
    }

    public void Listeners() {
        this.view.buscarButton.addActionListener(this::actionPerformed);
        this.view.Descargar.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.perfil.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.RegistrarEmisión.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);
        this.view.siTieneNucleos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.siTieneNucleos.isSelected()) {
                    view.siNucleo.setVisible(true);
                    view.noNucleo.setVisible(true);
                    view.Nucleo.setVisible(true);
                    view.noTieneNucleo.setEnabled(false);
                } else {
                    view.noTieneNucleo.setEnabled(true);
                    view.siNucleo.setVisible(false);
                    view.noNucleo.setVisible(false);
                    view.Nucleo.setVisible(false);
                }
            }
        });
        this.view.noTieneNucleo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.noTieneNucleo.isSelected()) {
                    view.siNucleo.setVisible(false);
                    view.noNucleo.setVisible(false);
                    view.Nucleo.setVisible(false);
                    view.siTieneNucleos.setEnabled(false);
                } else {
                    view.siTieneNucleos.setEnabled(true);
                }
            }
        });
        this.view.siNucleo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.siNucleo.isSelected()) {
                    view.comboxNucleo.setVisible(true);
                    view.SeleccionarNucleo.setVisible(true);
                    view.noNucleo.setEnabled(false);
                    cargarNucleosExistentes();
                }
                if (!view.siNucleo.isSelected()) {
                    view.SeleccionarNucleo.setVisible(false);
                    view.comboxNucleo.setVisible(false);
                    view.noNucleo.setEnabled(true);
                }


                cargarAnioBase();
            }
        });
        this.view.noNucleo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.noNucleo.isSelected()) {
                    view.SeleccionarNucleo.setVisible(false);
                    view.comboxNucleo.setVisible(false);
                    view.siNucleo.setEnabled(false);
                    view.general.setVisible(true);
                    view.siGeneral.setVisible(true);
                    view.noGeneral.setVisible(true);

                } else {
                    view.siNucleo.setEnabled(true);
                    view.general.setVisible(false);
                    view.siGeneral.setVisible(false);
                    view.noGeneral.setVisible(false);
                }
            }
        });
        this.view.institucion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.institucion.getItemCount() > 0) {
                    view.municipio.removeAllItems(); // Limpiar el combo de municipio
                    view.comboxNucleo.removeAllItems();
                    cargarAnioBase();
                    cargarMunicipio();
                }
            }
        });
        this.view.municipio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.municipio.getItemCount() > 0) {
                    view.comboxNucleo.removeAllItems(); // Limpiar el combo de núcleos si está visible
                    if (view.siNucleo.isSelected()) {
                        cargarNucleosExistentes(); // Cargar núcleos si el checkbox está seleccionado
                    }
                    cargarAnioBase(); // Cargar el año base
                }
            }
        });
        this.view.comboxNucleo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.comboxNucleo.getItemCount() > 0) {
                    anioBaseNucleo(); // Cargar el año base basado en la selección del núcleo
                }
            }
        });

    }


}