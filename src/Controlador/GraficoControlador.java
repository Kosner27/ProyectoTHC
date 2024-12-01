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
import java.awt.image.BufferedImage;
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
                view.VerPerfiles.setVisible(true);
                view.verInstitucion.setVisible(false);
                view.setLocationRelativeTo(null);
                break;
            case "Superadmin":
                view.setTitle("Grafico principal");
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                cargarInstitucion();
                cargarAnioBase();
                cargarMunicipio();
                break;
            case "Invitado":
                view.RegistrarInstitucion.setVisible(false);
                view.VerPerfiles.setVisible(false);
                view.RegistrarEmision.setVisible(false);
                view.Calcular.setVisible(false);
                view.verInstitucion.setVisible(false);
                view.perfil.setVisible(true);
                view.setLocationRelativeTo(null);
                view.setVisible(true);
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
            if(!view.siNucleo.isSelected() && !view.noNucleo.isSelected()){
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

            if(view.comboxNucleo.isVisible()){
                DescargarConNucleo();
            }if(!view.comboxNucleo.isVisible() && !view.siGeneral.isSelected()){
                DescargasSinNucleo();
            }if(view.siGeneral.isSelected()){
                DescargaTodosLosNucleos();
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
        if (e.getSource() == view.RegistrarEmision) {
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
        if(e.getSource() == view.verInstitucion){
            vistaVerInstitucion();
        }
    }

    private void graficosSinNucleo() {
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String anioBaseString = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = String.valueOf(view.municipio.getSelectedItem());
        if (!nombreInstitucion.isEmpty() && !anioBaseString.isEmpty() && !NombreMuncipio.isEmpty()) {
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();
            int anioBase = Integer.parseInt(anioBaseString);
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
        String anioBaseString = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = String.valueOf(view.municipio.getSelectedItem());
        String nucleo = String.valueOf(view.comboxNucleo.getSelectedItem());
        if (!nombreInstitucion.isEmpty() && !anioBaseString.isEmpty() && !NombreMuncipio.isEmpty() && !nucleo.isEmpty()) {
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();
            int anioBase = Integer.parseInt(anioBaseString);
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
        String anioBaseString = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = String.valueOf(view.municipio.getSelectedItem());
        if (!nombreInstitucion.isEmpty() && !anioBaseString.isEmpty() && !NombreMuncipio.isEmpty()) {
            int anioBase = Integer.parseInt(anioBaseString);
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
        String anioBaseString = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = view.municipio.getSelectedItem().toString();
        if (!nombreInstitucion.isEmpty() && !anioBaseString.isEmpty()) {
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();
            int anioBase = Integer.parseInt(anioBaseString);
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
        String anioBaseString = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = Objects.requireNonNull(view.municipio.getSelectedItem()).toString();
        String nombreN = Objects.requireNonNull(view.comboxNucleo.getSelectedItem()).toString();
        if (!nombreInstitucion.isEmpty() && !anioBaseString.isEmpty() && !nombreN.isEmpty()) {
            int anioBase = Integer.parseInt(anioBaseString);
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

    private void DescargaTodosLosNucleos(){
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String anioBaseString = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = String.valueOf(view.municipio.getSelectedItem());
        if (!nombreInstitucion.isEmpty() && !anioBaseString.isEmpty() && !NombreMuncipio.isEmpty()) {
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();
            int anioBase = Integer.parseInt(anioBaseString);
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


            String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String b = "GraficosDeLa" + view.institucion.getSelectedItem().toString() + fecha + "DeTodosLosNucleos";

            JFileChooser f = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);
            f.setDialogTitle("Guardar archivo PDF");
            f.setSelectedFile(new File(b + ".pdf"));
            int userSelection = f.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = f.getSelectedFile();
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());
                ExportaGraficoSumaTodosNucleos(datosAlcance, datosFuente, file);
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

    private void ExportaGraficoSumaTodosNucleos(DefaultPieDataset datosAlcance, DefaultPieDataset datosFuente, File r){
        try {
            //Se creo lo graficos
            JFreeChart chartAlcance = crearGraficoCircular(datosAlcance);
            JFreeChart chartFuente = crearGraficoFuente(datosFuente);

            // se pasason a imagenes
            BufferedImage bufferedImageAlcance = chartAlcance.createBufferedImage(600, 300); // Ajuste de tamaño
            ByteArrayOutputStream chartStreamAlcance = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamAlcance, bufferedImageAlcance);
            byte[] chartBytesAlcance = chartStreamAlcance.toByteArray();

            BufferedImage bufferedImageFuente = chartFuente.createBufferedImage(600, 300); // Ajuste de tamaño
            ByteArrayOutputStream chartStreamFuente = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamFuente, bufferedImageFuente);
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
            chartImageAlcance.scaleToFit(500, 300);
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
            chartImageFuente.scaleToFit(500, 300);
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
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        // Obtener la lista de instituciones desde el Modelo
        List<String> instituciones = consultasInstitucion.obtenerInstituciones();

        // Limpiar el comboBox y agregar un item vacío
        view.institucion.removeAllItems();
        view.institucion.addItem("");  // Añadimos un item vacío como indicativo

        // Llenar el comboBox con las instituciones obtenidas del Modelo
        for (String institucion : instituciones) {
            view.institucion.addItem(institucion);
        }

        // Si el comboBox tiene elementos, seleccionamos el primero
        if (view.institucion.getItemCount() > 0) {
            view.institucion.setSelectedIndex(0); // Seleccionamos el primer elemento
        }
    }

    // Método para cargar los años base en el comboBox de la Vista
    public void cargarAnioBase() {
        // Obtener la lista de años base desde el Modelo
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String nombreMunicipio = String.valueOf(view.municipio.getSelectedItem());

        // Consultar los años base desde el Modelo
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        List<String> aniosBase = consultasInstitucion.obtenerAnioBase(nombreInstitucion, nombreMunicipio);

        System.out.println(aniosBase);
        // Limpiar el comboBox de años y agregar un item vacío
        view.anio.removeAllItems();
        view.anio.addItem("");  // Añadir un item vacío como indicativo

        // Llenar el comboBox con los años base obtenidos del Modelo
        for (String anio : aniosBase) {
            view.anio.addItem(anio);
        }

        // Si el comboBox tiene elementos, seleccionamos el primero
        if (view.anio.getItemCount() > 0) {
            view.anio.setSelectedIndex(0); // Seleccionamos el primer elemento
        }
    }

    public void cargarMunicipio() {
        // Obtener la institución seleccionada desde la vista
        String nombreInstitucion = (String) view.institucion.getSelectedItem();
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();

        // Verificar que la institución no esté vacía
        if (nombreInstitucion != null && !nombreInstitucion.isEmpty()) {
            // Obtener la lista de municipios desde el Modelo
            List<String> municipios = consultasInstitucion.obtenerMunicipios(nombreInstitucion);

            // Limpiar el JComboBox de municipios y agregar un item vacío
            view.municipio.removeAllItems();
            view.municipio.addItem("");  // Añadir un item vacío como indicativo

            // Llenar el JComboBox con los municipios obtenidos del Modelo
            for (String municipio : municipios) {
                view.municipio.addItem(municipio);
            }

            // Si el JComboBox tiene elementos, seleccionamos el primero
            if (view.municipio.getItemCount() > 0) {
                view.municipio.setSelectedIndex(0); // Seleccionamos el primer elemento
            }
        } else {
            // Si la institución está vacía, limpiar el JComboBox de municipios
            view.municipio.removeAllItems();
        }
    }

    public void cargarNucleosExistentes() {
        // Limpiamos el combo box antes de cargar los nuevos valores
        view.comboxNucleo.removeAllItems();
        view.comboxNucleo.addItem(" ");
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
        // Obtenemos el nombre de la institución desde el modelo
        String nombreInstitucion = view.institucion.getSelectedItem().toString();
        // Llamamos al modelo para cargar los núcleos
        List<String> nucleos = consultaNucleo.cargarNucleos(nombreInstitucion);

        // Agregamos los núcleos al combo box
        for (String nucleo : nucleos) {
            view.comboxNucleo.addItem(nucleo);
        }

        // Manejo de errores si ocurre algún problema en la consulta
        if (nucleos.isEmpty()) {
            JOptionPane.showMessageDialog(view.PanelMain, "No se encontraron núcleos para esta institución.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void anioBaseNucleo() {
        view.anio.removeAllItems(); // Limpiar el combo de años
        view.anio.addItem(" "); // Agregar un texto indicativo
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
        String nombre = String.valueOf(view.institucion.getSelectedItem());
        String municipio = String.valueOf(view.municipio.getSelectedItem());
        String nucleo = String.valueOf(view.comboxNucleo.getSelectedItem());
        List<String> anioBaseNucleo = consultaNucleo.obtenerAnosBase(nombre, municipio, nucleo);
        for (String anioBase : anioBaseNucleo) {
            view.anio.addItem(anioBase);
        }

    }

    public void anioParaSumaTodosLosNucleos() {
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
        // Obtener la institución y municipio seleccionados desde la vista
        String nombreInstitucion = (String) view.institucion.getSelectedItem();
        String nombreMunicipio = (String) view.municipio.getSelectedItem();

        // Verificar que los valores seleccionados no sean nulos o vacíos
        if (nombreInstitucion != null && !nombreInstitucion.isEmpty() &&
                nombreMunicipio != null && !nombreMunicipio.isEmpty()) {

            // Obtener la lista de años base desde el Modelo
            List<String> aniosBase = consultaNucleo.obteneranioParaSumaTodosLosNucleos(nombreInstitucion, nombreMunicipio);

            // Limpiar el JComboBox de años y agregar un item vacío como indicativo
            view.anio.removeAllItems();
            view.anio.addItem(""); // Agregar un espacio vacío para indicar al usuario que elija un año

            // Llenar el JComboBox con los años base obtenidos del Modelo
            for (String anio : aniosBase) {
                view.anio.addItem(anio);
            }

            // Si el JComboBox tiene elementos, seleccionamos el primero
            if (view.anio.getItemCount() > 0) {
                view.anio.setSelectedIndex(0); // Seleccionamos el primer elemento
            }
        } else {
            // Si la institución o municipio están vacíos, limpiar el JComboBox de años
            view.anio.removeAllItems();
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
        VerDatosInstitucion view2 = new VerDatosInstitucion();
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

    private void vistaVerInstitucion(){
        Conexion con = new Conexion();
        VerInstituciones verInstituciones = new VerInstituciones();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(con);
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        InstitucionModelo institucionModelo = new InstitucionModelo();
        ContraladorVerInstituciones contraladorVerInstituciones = new ContraladorVerInstituciones(consultasInstitucion,consultaNucleo,
                institucionModelo,verInstituciones,user,m);
        contraladorVerInstituciones.iniciar();
        //view.dispose();
    }


    public void Listeners() {
        this.view.buscarButton.addActionListener(this::actionPerformed);
        this.view.Descargar.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.perfil.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.RegistrarEmision.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);
        this.view.verInstitucion.addActionListener(this::actionPerformed);
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
                Conexion conn = new Conexion();
                ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);
                String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
                if(consultaNucleo.TieneNucleo(nombreInstitucion)>=1){
                    view.noNucleo.setVisible(true);
                    view.siNucleo.setVisible(true);
                    view.Nucleo.setVisible(true);
                    view.anio.removeAllItems();
                }else{
                    view.noNucleo.setVisible(false);
                    view.siNucleo.setVisible(false);
                    view.Nucleo.setVisible(false);
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
        this.view.siGeneral.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(view.siGeneral.isSelected()){
                    anioParaSumaTodosLosNucleos();
                    view.noGeneral.setEnabled(false);
                }else{
                    view.noGeneral.setEnabled(true);
                }
            }
        });

    }

}