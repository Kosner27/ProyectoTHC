package Controlador;

import Modelo.*;
import Modelo.Consultas.GraficoCompararConsultas;
import Modelo.modelo.GraficoCompararModelo;
import Modelo.modelo.InstitucionModelo;
import Vistas.CompararOtrarInstituciones;
import Vistas.GraficoComparar;
import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class ComparaInstitucion {
    public GraficoCompararConsultas consul;
    public GraficoCompararModelo mod;
    public CompararOtrarInstituciones view;
    public GraficoComparar view2;
    public Conexion conn = new Conexion();

    public ComparaInstitucion(GraficoCompararModelo mod, GraficoCompararConsultas consul, CompararOtrarInstituciones view, GraficoComparar view2) {
        this.mod = mod;
        this.consul = consul;
        this.view = view;
        this.view2 = view2;
        this.view.añadirButton.addActionListener(this::actionPerformed);
        this.view.compararButton.addActionListener(this::actionPerformed);
        this.view2.descargarButton.addActionListener(this::actionPerformed);
    }

    public void iniciar() {
        view.setTitle("Seleccionar Instituciones");
        view.setLocationRelativeTo(null);
        cargarInstitucion();
        cargarAnioBase();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.añadirButton) {
            String nombreInstitucion = view.institucion.getSelectedItem().toString();
            if (!nombreInstitucion.isEmpty()) {
                DefaultTableModel tableModel = (DefaultTableModel) view.Instituciones.getModel();
                List<InstitucionModelo> datos = consul.llenarTabla(nombreInstitucion);

                for (InstitucionModelo dato : datos) {
                    Object[] rowData = {
                            dato.getNombreInstitucion(),
                            dato.getNit(),
                            dato.getMunicipio(),
                            dato.getDepartamento()
                    };
                    tableModel.addRow(rowData);
                }

                view.Instituciones.setModel(tableModel);
                view.Instituciones.setVisible(true);
                view.Contenedor.setVisible(true);


            }
        }
        if (e.getSource() == view.compararButton) {

            String anio = view.anio.getSelectedItem().toString();
            String alcance = view.alcance.getSelectedItem().toString();

            if (!anio.isEmpty() && !alcance.isEmpty()) {

                DefaultCategoryDataset datos = new DefaultCategoryDataset();
                DefaultTableModel tableModel = (DefaultTableModel) view.Instituciones.getModel();
                int rowCount = tableModel.getRowCount();
                List<String> nombresInstituciones = new ArrayList<>();
                for (int i = 0; i < rowCount; i++) {
                    String nombreInstitucion = (String) tableModel.getValueAt(i, 0); // Asumiendo que el nombre de la institución está en la primera columna
                    nombresInstituciones.add("'" + nombreInstitucion + "'");
                }
                String instituciones = String.join(",", nombresInstituciones);
                List<GraficoCompararModelo> mod = consul.LlenarGrafico(instituciones, anio, alcance);

                if (mod.isEmpty()) {
                    JOptionPane.showMessageDialog(view,
                            "No se encontraron datos para la institución, año y alcance seleccionados.",
                            "Sin datos",
                            JOptionPane.INFORMATION_MESSAGE);
                    return; // Terminar la ejecución del método si no hay datos
                }

                for (GraficoCompararModelo dato : mod) {
                    String label = dato.getNombrefuente();
                    Double total = dato.getTotal();
                    String label2 = dato.getNombreInstitucion();
                    datos.setValue(total, label2, label);
                    JFreeChart grafico = grafico(datos);
                    mostrarGrafico(grafico);
                }
            } else {
                JOptionPane.showMessageDialog(view,
                        "Por favor seleccione una institución, un año y un alcance antes de proceder.",
                        "Datos faltantes",
                        JOptionPane.WARNING_MESSAGE);
            }

        }

        if (view2.descargarButton == e.getSource()) {
            String anio = view.anio.getSelectedItem().toString();
            String alcance = view.alcance.getSelectedItem().toString();

            if (!anio.isEmpty() && !alcance.isEmpty()) {
                DefaultCategoryDataset datos = new DefaultCategoryDataset();
                DefaultTableModel tableModel = (DefaultTableModel) view.Instituciones.getModel();
                int rowCount = tableModel.getRowCount();
                List<String> nombresInstituciones = new ArrayList<>();
                for (int i = 0; i < rowCount; i++) {
                    String nombreInstitucion = (String) tableModel.getValueAt(i, 0); // Asumiendo que el nombre de la institución está en la primera columna
                    nombresInstituciones.add("'" + nombreInstitucion + "'");
                }
                String instituciones = String.join(",", nombresInstituciones);
                List<GraficoCompararModelo> mod = consul.LlenarGrafico(instituciones, anio, alcance);

                if (mod.isEmpty()) {
                    JOptionPane.showMessageDialog(view,
                            "No se encontraron datos para la institución, año y alcance seleccionados.",
                            "Sin datos",
                            JOptionPane.INFORMATION_MESSAGE);
                    return; // Terminar la ejecución del método si no hay datos
                }

                for (GraficoCompararModelo dato : mod) {
                    String label = dato.getNombrefuente();
                    Double total = dato.getTotal();
                    String label2 = dato.getNombreInstitucion();
                    datos.setValue(total, label2, label);
                    JFreeChart grafico = grafico(datos);
                    mostrarGrafico(grafico);
                }

                String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                String b = view.institucion.getSelectedItem().toString() + fecha;
                JFileChooser f = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
                f.setFileFilter(filter);
                f.setDialogTitle("Guardar archivo ");
                f.setSelectedFile(new File(b + ".pdf"));
                int userSelection = f.showSaveDialog(null);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File c = f.getSelectedFile();
                    ExportarGrafico(datos, c); // Asegúrate de que la variable datos esté en el ámbito adecuado aquí
                }
            } else {
                JOptionPane.showMessageDialog(view,
                        "Por favor seleccione una institución, un año y un alcance antes de proceder.",
                        "Datos faltantes",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void ExportarGrafico(DefaultCategoryDataset dato, File r) {
        Document document = new Document(PageSize.A3);

        try {
            JFreeChart chartDato = grafico(dato);

            ByteArrayOutputStream chartStreamDatos = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamDatos, chartDato.createBufferedImage(600, 300));
            byte[] chartBytesDatos = chartStreamDatos.toByteArray();
            PdfWriter.getInstance(document, new FileOutputStream(r));
            document.open();
            String fechaActual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            Paragraph fecha = new Paragraph("Fecha de creación: " + fechaActual);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            document.add(fecha);
            document.add(new Paragraph(" "));
            Paragraph text = new Paragraph("En la siguiente gráfica, encontrará una tabla que contiene toda la información de las instituciones universitarias con las que se realizó la comparación. La tabla incluye el NIT, el departamento y el municipio al que pertenece cada institución.");
            document.add(text);
            document.add(new Paragraph(" "));
            PdfPTable tabla = new PdfPTable(view.Instituciones.getColumnCount());
            tabla.setWidthPercentage(100);

            for (int i = 0; i < view.Instituciones.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(view.Instituciones.getColumnName(i)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabla.addCell(cell);
            }
            for (int i = 0; i < view.Instituciones.getRowCount(); i++) {
                for (int j = 0; j < view.Instituciones.getColumnCount(); j++) {
                    Object value = view.Instituciones.getValueAt(i, j);
                    String cellText = (value != null) ? value.toString() : "";
                    tabla.addCell(cellText);
                }
            }
            document.add(tabla);
            document.add(new Paragraph(" "));
            Paragraph text2 = new Paragraph("En la siguiente grafica encontraremos en en eje x las fuentes de emisión y en el eje y la cantidad de co2 emitida por las instituciones, y ademas tenemos las leyendas de cual dato pertenece a cada institución");
            document.add(text2);
            document.add(new Paragraph(""));
            Image chartImageDato = Image.getInstance(chartBytesDatos);
            chartImageDato.setAlignment(Element.ALIGN_CENTER);
            chartImageDato.setBorderWidth(23);
            document.add(chartImageDato);


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

private JFreeChart grafico (DefaultCategoryDataset dataset){
    JFreeChart chart = ChartFactory.createBarChart(
            "Comparar instituciones por alcance ",   // Título del gráfico
            "Fuentes emision",       // Etiqueta del eje X
            "Cantidad de co2",       // Etiqueta del eje Y
            dataset   // Conjunto de datos
    );
    return chart;
}

    private void mostrarGrafico(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(300, 700));
        view2.PanelGrafico.setLayout(new BorderLayout());
        view2.PanelGrafico.removeAll();
        view2.PanelGrafico.add(panel,BorderLayout.CENTER);
        view2.PanelGrafico.revalidate();
        view2.PanelGrafico.repaint();
        view2.setVisible(true);

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
