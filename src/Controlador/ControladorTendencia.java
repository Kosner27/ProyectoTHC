package Controlador;

import Modelo.Conexion;
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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ControladorTendencia {
    private final ModeloTendencia modeloTendencia;
    private final ConsultasTendencias consultasTendencias;
    private final GraficoTendencia view;
    private final ModeloMunicipio modeloMunicipio;
    private final ModeloInstitucion modeloInstitucion;
    private final ModeloUsuario modeloUsuario;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    //Constructor explicito de la clase
    public ControladorTendencia(ModeloTendencia modeloTendencia, ConsultasTendencias consultasTendencias, GraficoTendencia view,
                                ModeloMunicipio modeloMunicipio, ModeloInstitucion modeloInstitucion, ModeloUsuario modUse) {
        this.modeloTendencia = modeloTendencia;
        this.consultasTendencias = consultasTendencias;
        this.view = view;
        this.modeloMunicipio = modeloMunicipio;
        this.modeloInstitucion = modeloInstitucion;
        this.modeloUsuario = modUse;
        Listeners();

    }
    /**
     * Inicializa la vista de perfil dependiendo del tipo de modeloUsuario.
     * Configura las opciones disponibles para cada tipo de modeloUsuario y agrega los listeners correspondientes.
     */

    public void iniciar() {
        view.Graficos.add(GraficoPrincipal);
        view.Graficos.add(GraficosCompararInstitucion);
        view.Graficos.add(GraficoHistorico);
        String usuario = this.modeloUsuario.getTipoUsuario();
        switch (usuario) {
            case "Administrador":
                cargarNucleosExistentes();
                cargarInstitucion();
                cargarMunicipio();
                System.out.println("Estoy en docente");
                view.setTitle("Grafico Historico");
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                view.VerPerfiles.setVisible(true);
                view.verInstitucion.setVisible(false);
                view.setLocationRelativeTo(null);
                break;
            case "Superadmin":
                view.setTitle("Grafico Tendencia");
                view.setLocationRelativeTo(null);
                cargarInstitucion();
                cargarMunicipio();
                cargarNucleosExistentes();

                break;
            case "Invitado":
                view.setTitle("Grafico Tendencia");
                view.setLocationRelativeTo(null);
                cargarInstitucion();
                cargarMunicipio();
                cargarNucleosExistentes();
                view.RegistrarInstitucion.setVisible(false);
                view.VerPerfiles.setVisible(false);
                view.RegistrarEmision.setVisible(false);
                view.Calcular.setVisible(false);
                view.verInstitucion.setVisible(false);
                break;
            default:
                JOptionPane.showMessageDialog(null, "ModeloUsuario no definido en el sistema");
                break;

        }
        GraficoPrincipal.addActionListener(_ -> vistaGraficoPrincipal());
        GraficosCompararInstitucion.addActionListener(_ -> vistaCompararInstituciones());

    }
    /**
     * Método que maneja las acciones de los botones y otros componentes en la vista de perfil.
     *
     * @param e Evento generado por los componentes de la vista.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.verButton) {
            if(view.comboNucleo.isVisible()){
                GraficoConNucleo();

            }else {
                GraficoSinNucleo();
            }

        }
        if (e.getSource() == view.descargarButton) {
            if (view.comboNucleo.isVisible()) {
                DocumentoConNucleo();
            }
            else {
                DocumentoSinNucleo();
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

    /**
     * Garantiza un nombre único para un archivo dentro de un directorio.
     * Si el archivo con el nombre original ya existe, genera un nuevo nombre añadiendo un contador al final del nombre base.
     *
     * @param directory Directorio donde se buscará o creará el archivo.
     * @param filename  Nombre del archivo propuesto.
     * @return Archivo (`File`) con un nombre único que no existe en el directorio.
     */
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
    /**
     * Método encargado de exportar gráficos a un archivo PDF.
     * Este método genera gráficos a partir de los datos de alcance y fuente proporcionados,
     * los convierte en imágenes y los añade a un documento PDF junto con información relevante
     * de la institución, modeloMunicipio y núcleo seleccionados. El archivo se guarda en la ruta especificada.
     * @param a Dataset para la creacion del grafico en el archivo.
     * @param c file es el archivo que se va a crear.

     */
    private void ExportarGraficos(DefaultCategoryDataset a, File c) {
        Document document = new Document(PageSize.A3);
        try {

            JFreeChart chartDataset = crearGraficoTendencia(a);
            ByteArrayOutputStream chartStreamDataset = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamDataset, chartDataset.createBufferedImage(600, 450));
            byte[] chartBytesDataset = chartStreamDataset.toByteArray();
            PdfWriter.getInstance(document, new FileOutputStream(c));
            document.open();
            List<ModeloEmisionInforme> z = consultasTendencias.datos(String.valueOf(view.institucion.getSelectedItem()), String.valueOf(view.municipio.getSelectedItem()));
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
            for (ModeloEmisionInforme info : z) {
                String m = info.getMunicipio();
                Paragraph municipio = new Paragraph("ModeloMunicipio: " + m);
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
    /**
     * Método encargado de exportar gráficos a un archivo PDF.
     * Este método genera gráficos a partir de los datos de alcance y fuente proporcionados,
     * los convierte en imágenes y los añade a un documento PDF junto con información relevante
     * de la institución, modeloMunicipio y núcleo seleccionados. El archivo se guarda en la ruta especificada.
     * @param a Dataset para la creacion del grafico en el archivo.
     * @param c file es el archivo que se va a crear.

     */
    private void ExportarGraficosDeNucleo(DefaultCategoryDataset a, File c) {
        Document document = new Document(PageSize.A3);
        try {

            JFreeChart chartDataset = crearGraficoTendencia(a);
            ByteArrayOutputStream chartStreamDataset = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamDataset, chartDataset.createBufferedImage(600, 450));
            byte[] chartBytesDataset = chartStreamDataset.toByteArray();
            PdfWriter.getInstance(document, new FileOutputStream(c));
            document.open();
            List<ModeloEmisionInforme> z = consultasTendencias.datos(String.valueOf(view.institucion.getSelectedItem()), String.valueOf(view.municipio.getSelectedItem()));
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
            for (ModeloEmisionInforme info : z) {
                String m = info.getMunicipio();
                Paragraph municipio = new Paragraph("ModeloMunicipio: " + m);
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
            String nombreN = view.comboNucleo.getSelectedItem().toString();
            Paragraph nucleo = new Paragraph("ModeloNucleo: " + nombreN);
            nucleo.setAlignment(Element.ALIGN_LEFT);
            document.add(nucleo);
            document.add(new Paragraph("  "));
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
    /**
     * Crea un gráfico circular para representar las emisiones por alcance.
     * Este método toma un conjunto de datos y genera un gráfico circular que muestra la distribución
     * de emisiones categorizadas por diferentes alcances. Se configura un formato de etiqueta que incluye
     * el nombre de la categoría, el valor absoluto y el porcentaje.
     *
     * @param dataset Conjunto de datos para generar el gráfico.
     * @return Objeto JFreeChart con el gráfico circular generado.
     */
    private JFreeChart crearGraficoTendencia(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(
                "Histórico de la huella de carbono",   // Título del gráfico
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
    /**
     * Muestra un gráfico en el panel especificado de la interfaz de modeloUsuario.
     * Este método agrega el gráfico proporcionado a un panel denominado "PanelGrafico2" en la vista,
     * habilitando la interacción con el gráfico mediante el desplazamiento de la rueda del ratón
     * y ajustando el tamaño del panel para que el gráfico se ajuste adecuadamente.
     * Primero elimina cualquier componente previo en el panel antes de agregar el nuevo gráfico.
     *
     * @param chart El gráfico a mostrar, de tipo JFreeChart.
     */
    private void mostrarGrafico(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(300, 700));

        view.Grafico.setLayout(new BorderLayout());
        view.Grafico.removeAll();
        view.Grafico.add(panel, BorderLayout.CENTER);
        view.Grafico.revalidate();
        view.Grafico.repaint();

    }
    /**
     * Método encargado de generar y mostrar el grafico historico basados en los datos de alcance
     * y fuente para una institución y modeloMunicipio seleccionados.
     * <p>
     * El método valida que los valores seleccionados no estén vacíos, consulta los datos correspondientes,
     * los procesa y genera gráficos utilizando la biblioteca JFreeChart.
     * </p>
     */
    private void GraficoSinNucleo() {
        view.image.setVisible(false);
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String nombreMunicipio = String.valueOf(view.municipio.getSelectedItem());
        if (!nombreInstitucion.isEmpty() && !nombreMunicipio.isEmpty()) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            List<ModeloTendencia> mod = consultasTendencias.getNombre(nombreInstitucion, nombreMunicipio);
            for (ModeloTendencia dato : mod) {
                String label = dato.getAlcance();
                Integer anioBase = dato.getAnioBase();
                Double Total = dato.getCo2();

                if (anioBase != null && Total != null) {
                    dataset.setValue(Total, label, anioBase.toString());

                } else {
                    System.out.println("ERROR");
                }
                JFreeChart grafico = crearGraficoTendencia(dataset);
                mostrarGrafico(grafico);
            }

        }
    }
    /**
     * Método encargado de generar y mostrar el grafico historico basados en los datos de alcance
     * y fuente para una institución, nucleo y modeloMunicipio seleccionados.
     * <p>
     * El método valida que los valores seleccionados no estén vacíos, consulta los datos correspondientes,
     * los procesa y genera gráficos utilizando la biblioteca JFreeChart.
     * </p>
     */

    private void GraficoConNucleo() {
        view.image.setVisible(false);
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String nombreMunicipio = String.valueOf(view.municipio.getSelectedItem());
        String nombreN = String.valueOf(view.comboNucleo.getSelectedItem());
        if (!nombreInstitucion.isEmpty() && !nombreMunicipio.isEmpty() && !nombreN.isEmpty()) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            List<ModeloTendencia> mod = consultasTendencias.getNombrePorNucleo(nombreInstitucion, nombreMunicipio, nombreN);
            for (ModeloTendencia dato : mod) {
                String label = dato.getAlcance();
                Integer anioBase = dato.getAnioBase();
                Double Total = dato.getCo2();

                if (anioBase != null && Total != null) {
                    dataset.setValue(Total, label, anioBase.toString());

                } else {
                    System.out.println("ERROR");
                }
                JFreeChart grafico = crearGraficoTendencia(dataset);
                mostrarGrafico(grafico);
            }

        }
    }

    /**
     * Genera un gráfico de tendencias de CO2 para una institución y modeloMunicipio seleccionados
     * y permite al modeloUsuario guardar el gráfico como un archivo PDF.
     * Este método realiza los siguientes pasos:
     * 1. Obtiene los datos de la institución y modeloMunicipio seleccionados en la vista.
     * 2. Carga los datos históricos de tendencias de CO2 para la institución y modeloMunicipio.
     * 3. Crea un gráfico basado en los datos obtenidos.
     * 4. Muestra el gráfico en la interfaz de modeloUsuario.
     * 5. Permite al modeloUsuario seleccionar la ubicación y nombre del archivo para guardar el gráfico en formato PDF.
     * El gráfico muestra la tendencia de CO2 en función del año base y el alcance.
     */
    private void DocumentoSinNucleo() {
        // Oculta la imagen en la vista (si es visible).
        view.image.setVisible(false);

        // Obtiene los valores seleccionados en los comboboxes de institución y modeloMunicipio.
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String nombreMunicipio = String.valueOf(view.municipio.getSelectedItem());

        // Verifica que los campos no estén vacíos antes de proceder.
        if (!nombreInstitucion.isEmpty() && !nombreMunicipio.isEmpty()) {
            // Crea un dataset para almacenar los datos del gráfico.
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // Obtiene los datos de tendencias de CO2 desde la base de datos.
            List<ModeloTendencia> mod = consultasTendencias.getNombre(nombreInstitucion, nombreMunicipio);

            // Itera sobre los datos obtenidos.
            for (ModeloTendencia dato : mod) {
                String label = dato.getAlcance();  // Obtiene el alcance de la tendencia (e.g., "Alcance 1")
                Integer anioBase = dato.getAnioBase();  // Obtiene el año base
                Double Total = dato.getCo2();  // Obtiene el valor total de CO2

                // Verifica que los datos sean válidos antes de agregarlos al dataset.
                if (anioBase != null && Total != null) {
                    dataset.setValue(Total, label, anioBase.toString());  // Agrega los datos al dataset
                } else {
                    System.out.println("ERROR");  // En caso de que los datos sean nulos, imprime un mensaje de error.
                }

                // Crea un gráfico con los datos del dataset.
                JFreeChart grafico = crearGraficoTendencia(dataset);

                // Muestra el gráfico en la vista.
                mostrarGrafico(grafico);
            }

            // Crea un nombre para el archivo PDF con la fecha actual.
            String Nombre = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String b = "GraficoHistoricoDeLa" + view.institucion.getSelectedItem().toString() + " " + Nombre;

            // Configura el JFileChooser para seleccionar la ubicación y nombre del archivo a guardar.
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");  // Filtro para archivos PDF
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Guardar archivo PDF");  // Título del cuadro de diálogo
            fileChooser.setSelectedFile(new File(b + "GraficoHistórico" + ".pdf"));  // Establece un nombre por defecto para el archivo

            // Muestra el cuadro de diálogo y verifica si el modeloUsuario selecciona una ubicación.
            int userSelection = fileChooser.showSaveDialog(null);

            // Si el modeloUsuario elige una ubicación, guarda el archivo.
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = fileChooser.getSelectedFile();  // Obtiene el archivo seleccionado
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());  // Asegura que el archivo no exista previamente

                // Exporta el gráfico a un archivo PDF.
                ExportarGraficos(dataset, file);
            }
        }
    }


    /**
     * Genera un gráfico de tendencias de CO2 para una institución, modeloMunicipio y núcleo seleccionados
     * y permite al modeloUsuario guardar el gráfico como un archivo PDF.
     * Este método realiza los siguientes pasos:
     * 1. Obtiene los datos de la institución, modeloMunicipio y núcleo seleccionados en la vista.
     * 2. Carga los datos históricos de tendencias de CO2 para la institución, modeloMunicipio y núcleo.
     * 3. Crea un gráfico basado en los datos obtenidos.
     * 4. Muestra el gráfico en la interfaz de modeloUsuario.
     * 5. Permite al modeloUsuario seleccionar la ubicación y nombre del archivo para guardar el gráfico en formato PDF.
     * El gráfico muestra la tendencia de CO2 en función del año base, el alcance y el núcleo seleccionado.
     */
    private void DocumentoConNucleo() {
        // Oculta la imagen en la vista (si está visible).
        view.image.setVisible(false);

        // Obtiene los valores seleccionados en los comboboxes de institución, modeloMunicipio y núcleo.
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String nombreMunicipio = String.valueOf(view.municipio.getSelectedItem());
        String nombreN = String.valueOf(view.comboNucleo.getSelectedItem());

        // Verifica que los campos no estén vacíos antes de proceder.
        if (!nombreInstitucion.isEmpty() && !nombreMunicipio.isEmpty() && !nombreN.isEmpty()) {
            // Crea un dataset para almacenar los datos del gráfico.
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // Obtiene los datos de tendencias de CO2 desde la base de datos para la institución, modeloMunicipio y núcleo seleccionados.
            List<ModeloTendencia> mod = consultasTendencias.getNombrePorNucleo(nombreInstitucion, nombreMunicipio, nombreN);

            // Itera sobre los datos obtenidos.
            for (ModeloTendencia dato : mod) {
                String label = dato.getAlcance();  // Obtiene el alcance de la tendencia (e.g., "Alcance 1")
                Integer anioBase = dato.getAnioBase();  // Obtiene el año base
                Double Total = dato.getCo2();  // Obtiene el valor total de CO2

                // Verifica que los datos sean válidos antes de agregarlos al dataset.
                if (anioBase != null && Total != null) {
                    dataset.setValue(Total, label, anioBase.toString());  // Agrega los datos al dataset
                } else {
                    System.out.println("ERROR");  // En caso de que los datos sean nulos, imprime un mensaje de error.
                }

                // Crea un gráfico con los datos del dataset.
                JFreeChart grafico = crearGraficoTendencia(dataset);

                // Muestra el gráfico en la vista.
                mostrarGrafico(grafico);
            }

            // Crea un nombre para el archivo PDF con la fecha actual y el nombre del núcleo.
            String Nombre = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String b = "GraficoHistoricoDeLa" + view.institucion.getSelectedItem().toString() + " " + Nombre + " " + "delNucleo" + nombreN;

            // Configura el JFileChooser para seleccionar la ubicación y nombre del archivo a guardar.
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");  // Filtro para archivos PDF
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Guardar archivo PDF");  // Título del cuadro de diálogo
            fileChooser.setSelectedFile(new File(b + "GraficoHistórico" + ".pdf"));  // Establece un nombre por defecto para el archivo

            // Muestra el cuadro de diálogo y verifica si el modeloUsuario selecciona una ubicación.
            int userSelection = fileChooser.showSaveDialog(null);

            // Si el modeloUsuario elige una ubicación, guarda el archivo.
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = fileChooser.getSelectedFile();  // Obtiene el archivo seleccionado
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());  // Asegura que el archivo no exista previamente

                // Exporta el gráfico a un archivo PDF.
                ExportarGraficosDeNucleo(dataset, file);
            }
        }
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
        view.comboNucleo.removeAllItems();
        view.comboNucleo.addItem(" ");
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
        // Obtenemos el nombre de la institución desde el modelo
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        // Llamamos al modelo para cargar los núcleos
        List<String> nucleos = consultaNucleo.cargarNucleos(nombreInstitucion);

        // Agregamos los núcleos al combo box
        for (String nucleo : nucleos) {
            view.comboNucleo.addItem(nucleo);
        }

        // Manejo de errores si ocurre algún problema en la consulta

    }
    /**
     * Navega a la vista inicial de la aplicación.
     * Pasos:
     * 1. Crea el controlador de inicio con las dependencias necesarias.
     * 2. Llama al método que inicia la vista de inicio.
     * 3. Cierra la vista actual.
     */
    private void BotonInicio() {
        ControladorPestaniaPrincipal control = new ControladorPestaniaPrincipal(modeloInstitucion, modeloUsuario, modeloMunicipio);
        control.inicio();
        view.dispose();
    }
    /**
     * Inicia la vista del perfil del modeloUsuario.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado con los datos del modeloUsuario.
     * 3. Muestra la vista del perfil y cierra la vista actual.
     */
    private void vistaPerfil() {
        Conexion conn = new Conexion();
        Perfil per = new Perfil();
        ConsultaUsuario cons = new ConsultaUsuario(conn);
        ControladorPerfil control = new ControladorPerfil(modeloUsuario, per, modeloInstitucion, modeloMunicipio, cons);
        control.Iniciar();
        per.setVisible(true);
        view.dispose();
    }
    /**
     * Este método se encarga de inicializar y mostrar la vista de cálculo de la aplicación.
     * Crea las instancias necesarias de las clases para manejar el modelo, las consultas y el controlador
     * que gestionará la lógica del cálculo. Posteriormente, muestra la interfaz de modeloUsuario para realizar el cálculo
     * y cierra la vista actual de la aplicación.
     */
    private void vistaCalcular() {
        Conexion con = new Conexion();
        Vistas.Calcular viewCal = new Calcular();
        ModeloEmisionCalcular mod = new ModeloEmisionCalcular();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorCalcular controlador = new ControladorCalcular(mod, consul, viewCal, modeloInstitucion, consultaUsuario, modeloUsuario, modeloMunicipio);
        controlador.iniciar();
        viewCal.setVisible(true);
        view.dispose();
    }
    /**
     * Inicia la vista de informes.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica de los informes.
     * 3. Muestra la vista de informes y cierra la vista actual.
     */
    private void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        ModeloEmision mod = new ModeloEmision();
        ConsultasEmision consul = new ConsultasEmision();
        ControladorEmision controlador = new ControladorEmision(mod, consul, emisionView, modeloInstitucion, modeloMunicipio, modeloUsuario);
        controlador.iniciar();
        emisionView.setVisible(true);
        view.dispose();
    }
    /**
     * Inicia la vista de informes.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica de los informes.
     * 3. Muestra la vista de informes y cierra la vista actual.
     */
    private void vistaInforme() {
        Conexion con = new Conexion();
        ModeloInstitucion mod2 = new ModeloInstitucion();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloEmisionInforme mod = new ModeloEmisionInforme();
        Informe viewInfo = new Informe();
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorInforme contro = new ControladorInforme(viewInfo, mod, consul, modeloMunicipio, modeloInstitucion, modeloUsuario);
        contro.iniciar();
        viewInfo.setVisible(true);
        view.dispose();
    }
    /**
     * Inicia la vista para mostrar gráficos principales.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para gestionar los gráficos.
     * 3. Muestra la vista de gráficos y cierra la vista actual.
     */
    private void vistaGraficoPrincipal() {
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos viewGraf = new Graficos();
        GraficorModeloInstitucion mod = new GraficorModeloInstitucion();
        ModeloInstitucion modelo = new ModeloInstitucion();
        ControladorGrafico contro = new ControladorGrafico(mod, consul, viewGraf, modelo, modeloMunicipio, modeloUsuario, modeloInstitucion);
        contro.iniciar();
        viewGraf.setVisible(true);
        view.dispose();
    }
    /**
     * Inicia la vista para comparar instituciones mediante gráficos.
     * Pasos:
     * 1. Configura la vista, modelo y consultas necesarios.
     * 2. Inicializa el controlador para manejar la comparación de instituciones.
     * 3. Cierra la vista actual.
     */
    private void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar viewGraf = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        ModeloGraficoComparar mod = new ModeloGraficoComparar();
        ControladorCompararInstitucion contro = new ControladorCompararInstitucion(mod, consultas, comIns, viewGraf, modeloInstitucion, modeloMunicipio, modeloUsuario);
        contro.iniciar();
        view.dispose();
    }
    /**
     * Inicia la vista para aplicar estrategias de reducción de emisiones.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador para manejar las estrategias de reducción.
     * 3. Cierra la vista actual.
     */
    private void vistaReducir() {
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(modeloInstitucion, consul, vista, modeloMunicipio, modeloUsuario);
        redu.Iniciar();
        view.dispose();
    }
    /**
     * Inicia la vista para visualizar instituciones.
     * Pasos:
     * 1. Crea las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica.
     * 3. Opcionalmente, cierra la vista actual (comentado en este caso).
     */
    private void vistaActualizarInstitucion() {
        Conexion con = new Conexion();
        ConsultasInstitucion consul = new ConsultasInstitucion();
        VerDatosInstitucion view2 = new VerDatosInstitucion();
        ModeloInstitucion mod2 = new ModeloInstitucion();
        ControladorInstitucion control = new ControladorInstitucion(modeloInstitucion, modeloMunicipio, view2, consul, mod2, modeloUsuario);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();


    }
    /**
     * Inicia la vista para visualizar perfiles de usuarios.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista y consultas.
     * 2. Inicializa el controlador para manejar la lógica de visualización de perfiles.
     * 3. Cierra la vista actual.
     */
    private void vistaVerPerfiles() {
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);

        ControladorVerPerfiles verControl = new ControladorVerPerfiles(modeloUsuario, modeloInstitucion, modeloMunicipio, verPerfiles, consul);
        verControl.Iniciar();
        view.dispose();
    }
    /**
     * Método que inicializa los listeners para los botones y componentes de la vista.
     * Asocia eventos de acción a los botones y otros componentes interactivos.
     */
    private void Listeners() {
        this.view.verButton.addActionListener(this::actionPerformed);
        this.view.descargarButton.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.perfil.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.RegistrarEmision.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.verInstitucion.addActionListener(this::actionPerformed);
        this.view.institucion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.institucion.getItemCount() > 0) {
                    view.municipio.removeAllItems(); // Limpiar el combo de modeloMunicipio
                    view.comboNucleo.removeAllItems();
                    cargarMunicipio();
                    cargarNucleosExistentes();

                }
                Conexion conn = new Conexion();
                ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);
                String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
                if(consultaNucleo.TieneNucleo(nombreInstitucion)>=1){
                    view.Nucleo.setVisible(true);
                    view.comboNucleo.setVisible(true);
                    cargarNucleosExistentes(); // Cargar núcleos si el checkbox está seleccionado
                }else{
                    view.comboNucleo.setVisible(false);

                }
            }
        });
        this.view.municipio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.municipio.getItemCount() > 0) {
                    view.comboNucleo.removeAllItems(); // Limpiar el combo de núcleos si está visible
                        cargarNucleosExistentes(); // Cargar núcleos si el checkbox está seleccionado


                }
            }
        });
        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);

    }
    /**
     * Inicia la vista para visualizar instituciones.
     * Pasos:
     * 1. Crea las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica.
     * 3. Opcionalmente, cierra la vista actual (comentado en este caso).
     */
    private void vistaVerInstitucion(){
        Conexion con = new Conexion();
        VerInstituciones verInstituciones = new VerInstituciones();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(con);
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        ModeloInstitucion modeloInstitucion = new ModeloInstitucion();
        ControladorVerInstituciones contraladorVerInstituciones = new ControladorVerInstituciones(consultasInstitucion,consultaNucleo,
                modeloInstitucion,verInstituciones, modeloUsuario, modeloMunicipio);
        contraladorVerInstituciones.iniciar();
        view.dispose();
    }

}

