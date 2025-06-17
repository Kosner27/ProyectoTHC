package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.*;
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
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class ControladorCompararInstitucion {
    public GraficoCompararConsultas graficoCompararConsultas;
    public ModeloGraficoComparar modeloGraficoComparar;
    public CompararOtrarInstituciones view;
    public GraficoComparar view2;
    public ModeloInstitucion modeloInstitucion;
    public ModeloMunicipio modeloMunicipio;
    private final ModeloUsuario user;
    public Conexion conn = new Conexion();
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public ControladorCompararInstitucion(ModeloGraficoComparar modeloGraficoComparar, GraficoCompararConsultas graficoCompararConsultas,
                                          CompararOtrarInstituciones view, GraficoComparar view2,
                                          ModeloInstitucion modeloInstitucion, ModeloMunicipio modeloMunicipio, ModeloUsuario user) {
        this.modeloGraficoComparar = modeloGraficoComparar;
        this.graficoCompararConsultas = graficoCompararConsultas;
        this.view = view;
        this.view2 = view2;
        this.modeloMunicipio = modeloMunicipio;
        this.modeloInstitucion = modeloInstitucion;
        this.user = user;
        Listeners();
    }

    /**
     * Inicializa la vista principal y configura su comportamiento según el tipo de modeloUsuario.
     * Pasos:
     * 1. Configura la vista con los gráficos disponibles.
     * 2. Determina el tipo de modeloUsuario y ajusta la interfaz gráfica en consecuencia:
     *    - **Administrador:** Muestra elementos básicos y oculta opciones avanzadas.
     *    - **Superadmin:** Muestra todas las opciones y carga datos adicionales.
     *    - **Invitado:** Restringe la interfaz gráfica, ocultando opciones de edición.
     *    - **Por defecto:** Muestra un mensaje de error para usuarios no definidos.
     * 3. Configura los títulos, posición y visibilidad de la ventana principal.
     * 4. Agrega listeners a botones de gráficos para abrir vistas correspondientes.
     */
    public void iniciar() {
        // Agregar botones de gráficos al panel principal
        view.Graficos.add(GraficoPrincipal);
        view.Graficos.add(GraficosCompararInstitucion);
        view.Graficos.add(GraficoHistorico);

        // Obtener el tipo de modeloUsuario
        String usuario = user.getTipoUsuario();

        // Configurar vista según el tipo de modeloUsuario
        switch (usuario) {
            case "Administrador":
                cargarInstitucion();
                cargarMunicipio();
                view.setTitle("Seleccionar Instituciones");
                view.setLocationRelativeTo(null);
                view.setVisible(true);

                // Ocultar botones no necesarios para Administrador
                view.VerPerfiles.setVisible(false);
                view.verInstitucion.setVisible(false);
                break;

            case "Superadmin":
                view.setTitle("Seleccionar Instituciones");
                view.setLocationRelativeTo(null);
                cargarInstitucion();
                cargarMunicipio();
                cargarNucleosExistentes();
                break;

            case "Invitado":
                view.setTitle("Seleccionar Instituciones");
                view.setLocationRelativeTo(null);
                cargarInstitucion();
                cargarMunicipio();
                cargarNucleosExistentes();

                // Ocultar opciones restringidas para Invitado
                view.RegistrarInstitucion.setVisible(false);
                view.VerPerfiles.setVisible(false);
                view.RegistrarEmision.setVisible(false);
                view.Calcular.setVisible(false);
                view.verInstitucion.setVisible(false);
                break;

            default:
                // Manejo de usuarios no reconocidos
                JOptionPane.showMessageDialog(null, "ModeloUsuario no definido en el sistema");
                break;
        }

        // Configuración general de la vista principal
        view.setTitle("Seleccionar Instituciones");
        view.setLocationRelativeTo(null);

        // Carga de elementos iniciales
        cargarInstitucion();
        cargarAnioBase();

        // Configuración de listeners para gráficos
        GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
        GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
    }


    /**
     * Configura los listeners (controladores de eventos) para los elementos interactivos de la vista.
     * Asigna acciones específicas a botones, listas desplegables y otros componentes de la interfaz
     * para que respondan a las interacciones del modeloUsuario.
     */
    public void Listeners() {
        // Asignación de listeners a botones principales
        this.view.anadirButton.addActionListener(this::actionPerformed);
        this.view.compararButton.addActionListener(this::actionPerformed);
        this.view2.descargarButton.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view2.inicioButton.addActionListener(this::actionPerformed);
        this.view.perfil.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.RegistrarEmision.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.eliminarBtn.addActionListener(this::actionPerformed);

        // Listener para la lista desplegable "institución"
        this.view.institucion.addActionListener(e -> {
            if (view.institucion.getItemCount() > 0) {
                cargarMunicipio();
                cargarAnioBase();
                cargarNucleosExistentes();
            }
            Conexion conn = new Conexion();
            ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);
            String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());

            // Configuración de visibilidad basada en la existencia de núcleos
            if (consultaNucleo.TieneNucleo(nombreInstitucion) >= 1) {
                view.nucleo.setVisible(true);
                view.SeleccionNucleo.setVisible(true);
                view.anio.removeAllItems();
            } else {
                view.SeleccionNucleo.setVisible(false);
                view.nucleo.setVisible(false);
                cargarAnioBase();
            }
        });

        // Asignación de listeners a otros elementos interactivos
        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);

        // Listener para la lista desplegable "núcleo"
        this.view.nucleo.addActionListener(e -> {
            if (view.nucleo.getItemCount() > 0) {
                anioBaseNucleo();
                if ("Sumatoria de todos los nucleos registrados".equals(view.nucleo.getSelectedItem())) {
                    anioParaSumaTodosLosNucleos();
                }
            }
        });

        // Listener para el botón "Ver Institución"
        this.view.verInstitucion.addActionListener(this::actionPerformed);

        // Listener para la lista desplegable "ModeloMunicipio"
        this.view.Municipio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.Municipio.getItemCount() > 0) {
                    view.nucleo.removeAllItems(); // Limpiar el combo de núcleos si está visible
                    if (view.nucleo.isVisible()) {
                        cargarNucleosExistentes(); // Cargar núcleos si el checkbox está seleccionado
                    }
                    cargarAnioBase(); // Cargar el año base
                }
            }
        });
    }


    /**
     * Maneja los eventos de acción generados por los componentes interactivos de la interfaz gráfica.
     * Dependiendo del componente que dispara el evento, se ejecuta una acción específica.
     *
     * @param e el evento de acción generado por el modeloUsuario.
     */
    public void actionPerformed(ActionEvent e) {
        // Acción para el botón "Añadir"
        if (e.getSource() == view.anadirButton) {
            aniadirButton();
        }

        // Acción para el botón "Comparar"
        if (e.getSource() == view.compararButton) {
            compararButton();
        }

        // Acción para el botón "Descargar" en la vista secundaria
        if (view2.descargarButton == e.getSource()) {
            verGrafico();
        }

        // Acción para el botón "Inicio" en ambas vistas
        if (e.getSource() == view.inicioButton || e.getSource() == view2.inicioButton) {
            BotonInicio();
        }

        // Acción para el botón "Perfil"
        if (e.getSource() == view.perfil) {
            vistaPerfil();
        }

        // Acción para el botón "Calcular"
        if (e.getSource() == view.Calcular) {
            vistaCalcular();
        }

        // Acción para el botón "Registrar Emisión"
        if (e.getSource() == view.RegistrarEmision) {
            vistaRegistrarEmision();
        }

        // Acción para el botón "Informes"
        if (e.getSource() == view.Informes) {
            vistaInforme();
        }

        // Acción para el botón "Reducir"
        if (e.getSource() == view.Reducir) {
            vistaReducir();
        }

        // Acción para el botón "Registrar Institución"
        if (e.getSource() == view.RegistrarInstitucion) {
            vistaActualizarInstitucion();
        }

        // Acción para el botón "Ver Perfiles"
        if (e.getSource() == view.VerPerfiles) {
            vistaVerPerfiles();
        }

        // Acción para el botón "Eliminar"
        if (e.getSource() == view.eliminarBtn) {
            eliminarFila();
        }

        // Acción para el botón "Ver Institución"
        if (e.getSource() == view.verInstitucion) {
            vistaVerInstitucion();
        }
    }


    /**
     * Maneja la acción del botón "Añadir", cargando datos en la tabla de instituciones.
     * Valida que los campos requeridos estén seleccionados, consulta los datos
     * correspondientes de las instituciones y los agrega a la tabla en la vista.
     * Si se selecciona un núcleo, los datos se filtran por núcleo;
     * de lo contrario, se muestran datos generales.
     */
    private void aniadirButton() {
        // Obtención de valores seleccionados en la vista
        String anioBase = String.valueOf(view.anio.getSelectedItem());
        String alcance = String.valueOf(view.alcance.getSelectedItem());

        // Deshabilitar la edición de las celdas de la tabla
        view.Instituciones.setDefaultEditor(Object.class, null);

        // Validación de campos obligatorios
        if (!anioBase.isEmpty() && !alcance.isEmpty()) {
            String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
            String nombreMunicipio = String.valueOf(view.Municipio.getSelectedItem());

            // Deshabilitar el campo de alcance tras la primera acción
            view.alcance.setEnabled(false);

            // Si el comboBox de núcleo es visible, se obtiene el núcleo seleccionado; de lo contrario, es nulo
            String nucleo = view.nucleo.isVisible() ? String.valueOf(view.nucleo.getSelectedItem()).trim() : null;

            // Obtener el modelo de la tabla para agregar datos
            DefaultTableModel tableModel = (DefaultTableModel) view.Instituciones.getModel();

            if (!nombreInstitucion.isEmpty()) {
                System.out.println("Nombre de la institución: " + nombreInstitucion);
                System.out.println("ModeloMunicipio: " + nombreMunicipio);

                if (nucleo == null || nucleo.isEmpty() || "Sumatoria de todos los nucleos registrados".equals(nucleo)) {
                    // Caso: sin núcleo o seleccionando sumatoria de núcleos
                    List<ModeloInstitucion> datos = graficoCompararConsultas.llenarTabla(nombreInstitucion, nombreMunicipio);

                    for (ModeloInstitucion dato : datos) {
                        Object[] rowData = {
                                dato.getNombreInstitucion(),
                                dato.getNit(),
                                dato.getDepartamento(),
                                dato.getMunicipio(),
                                nucleo != null ? nucleo : "Sin núcleo",
                                view.alcance.getSelectedItem(),
                                view.anio.getSelectedItem()
                        };
                        tableModel.addRow(rowData);
                        System.out.println("Datos obtenidos (sin núcleo): " + dato.getNucleo());
                    }
                } else {
                    // Caso: con núcleo seleccionado
                    System.out.println("Núcleo seleccionado: " + nucleo);
                    List<ModeloNucleo> datos2 = graficoCompararConsultas.llenarTablaConNucleo(nombreInstitucion, nombreMunicipio, nucleo);

                    for (ModeloNucleo dato : datos2) {
                        Object[] rowData = {
                                dato.getNombreIns(),
                                dato.getIdInstitucion(),
                                dato.getDepartamento(),
                                dato.getMunicipio(),
                                dato.getNombreNucleo(),
                                view.alcance.getSelectedItem(),
                                view.anio.getSelectedItem()
                        };
                        tableModel.addRow(rowData);
                        System.out.println("Datos obtenidos (con núcleo): " + dato.getNombreNucleo());
                    }
                }
            }

            // Actualizar y mostrar la tabla en la vista
            view.Instituciones.setModel(tableModel);
            view.Instituciones.setVisible(true);
            view.Contenedor.setVisible(true);
        } else {
            // Mostrar mensaje de error si los campos obligatorios no están seleccionados
            JOptionPane.showMessageDialog(null, "Debes seleccionar un año base y un alcance antes de continuar.");
        }
    }


    /**
     * Genera un gráfico comparativo basado en las instituciones seleccionadas, año base y alcance.
     * Este método:
     * 1. Valida que los campos obligatorios (año y alcance) estén seleccionados.
     * 2. Extrae las instituciones y sus núcleos desde la tabla de la vista.
     * 3. Envía estos datos como parámetros a un procedimiento almacenado para obtener datos relevantes.
     * 4. Crea un gráfico a partir de los datos recuperados y lo muestra en la interfaz.
     */
    private void compararButton() {
        // Obtener valores seleccionados en los combos de año y alcance
        String anio = String.valueOf(view.anio.getSelectedItem());
        String alcance = String.valueOf(view.alcance.getSelectedItem());
        System.out.println(anio);

        // Validación de campos obligatorios
        if (!anio.isEmpty() && !alcance.isEmpty()) {
            // Crear un conjunto de datos para el gráfico
            DefaultCategoryDataset datos = new DefaultCategoryDataset();
            DefaultTableModel tableModel = (DefaultTableModel) view.Instituciones.getModel();
            int rowCount = tableModel.getRowCount();

            // Listas para almacenar los nombres de instituciones y núcleos seleccionados
            List<String> nombresInstituciones = new ArrayList<>();
            List<String> campus = new ArrayList<>();

            // Recorrer la tabla y extraer las instituciones y núcleos
            for (int i = 0; i < rowCount; i++) {
                String nombreInstitucion = (String) tableModel.getValueAt(i, 0); // Nombre de la institución
                nombresInstituciones.add("'" + nombreInstitucion + "'");
                String nombrenucleo = (String) tableModel.getValueAt(i, 4); // Nombre del núcleo
                campus.add("'" + nombrenucleo + "'");
            }

            // Convertir las listas en cadenas delimitadas por comas
            String instituciones = String.join(",", nombresInstituciones);
            String Campus = String.join(",", campus);

            // Imprimir los parámetros para verificación
            System.out.println("Parámetros enviados al procedimiento almacenado:");
            System.out.println("Instituciones: " + instituciones);
            System.out.println("Año: " + anio);
            System.out.println("Alcance: " + alcance);
            System.out.println("Campus: " + Campus);

            // Consultar datos para el gráfico
            List<ModeloGraficoComparar> mod = graficoCompararConsultas.LlenarGrafico(instituciones, anio, alcance, Campus);

            // Verificar si se obtuvieron datos
            if (mod.isEmpty()) {
                JOptionPane.showMessageDialog(view, "No se encontraron datos para la institución, año y alcance seleccionados.", "Sin datos", JOptionPane.INFORMATION_MESSAGE);
                return; // Salir del método si no hay datos
            }

            // Poblar el conjunto de datos para el gráfico
            for (ModeloGraficoComparar dato : mod) {
                String label = dato.getNombrefuente(); // Etiqueta para el eje X
                Double total = dato.getTotal(); // Valor a graficar
                String nucleo = dato.getNucleo() != null ? dato.getNucleo() : "Sin núcleo"; // Validación de núcleo
                String label2 = dato.getNombreInstitucion() + " - Campus: " + nucleo; // Etiqueta para la serie

                datos.setValue(total, label2, label);
            }

            // Crear y mostrar el gráfico
            JFreeChart grafico = grafico(datos);
            mostrarGrafico(grafico);
        } else {
            // Mostrar mensaje de error si no se han seleccionado los campos obligatorios
            JOptionPane.showMessageDialog(null, "Debes seleccionar un año y alcance antes de comparar.");
        }
    }


    /**
     * Genera un gráfico comparativo basado en las instituciones seleccionadas y permite exportarlo como PDF.
     * Funciones principales:
     * 1. Valida los campos obligatorios seleccionados (año y alcance).
     * 2. Obtiene los datos necesarios para el gráfico desde la tabla y una consulta a la base de datos.
     * 3. Construye el gráfico a partir de los datos obtenidos.
     * 4. Ofrece al modeloUsuario guardar el gráfico en un archivo PDF.
     */
    private void verGrafico() {
        // Obtener parámetros seleccionados
        String anio = String.valueOf(view.anio.getSelectedItem());
        String alcance = String.valueOf(view.alcance.getSelectedItem());

        // Validación de campos obligatorios
        if (!anio.isEmpty() && !alcance.isEmpty()) {
            // Crear conjunto de datos para el gráfico
            DefaultCategoryDataset datos = new DefaultCategoryDataset();
            DefaultTableModel tableModel = (DefaultTableModel) view.Instituciones.getModel();
            int rowCount = tableModel.getRowCount();
            List<String> nombresInstituciones = new ArrayList<>();
            List<String> nombreCampus = new ArrayList<>();

            // Extraer datos de la tabla (instituciones y campus)
            for (int i = 0; i < rowCount; i++) {
                String nombreInstitucion = (String) tableModel.getValueAt(i, 0); // Nombre de la institución
                nombresInstituciones.add("'" + nombreInstitucion + "'");
                String nombreNucleo = (String) tableModel.getValueAt(i, 4); // Nombre del núcleo
                nombreCampus.add("'" + nombreNucleo + "'");
            }

            // Convertir listas a cadenas delimitadas por comas
            String campus = String.join(",", nombreCampus);
            String instituciones = String.join(",", nombresInstituciones);

            // Obtener datos del gráfico mediante consulta
            List<ModeloGraficoComparar> mod = graficoCompararConsultas.LlenarGrafico(instituciones, anio, alcance, campus);

            // Validar que se hayan obtenido datos
            if (mod.isEmpty()) {
                JOptionPane.showMessageDialog(view, "No se encontraron datos para la institución, año y alcance seleccionados.", "Sin datos", JOptionPane.INFORMATION_MESSAGE);
                return; // Terminar la ejecución si no hay datos
            }

            // Poblar el conjunto de datos del gráfico
            for (ModeloGraficoComparar dato : mod) {
                String label = dato.getNombrefuente(); // Etiqueta del eje X
                Double total = dato.getTotal(); // Valor a graficar
                String nucleo = dato.getNucleo() != null ? dato.getNucleo() : "Sin núcleo"; // Validación de núcleo
                String label2 = dato.getNombreInstitucion() + " -campus: " + nucleo;

                datos.setValue(total, label2, label);
                JFreeChart grafico = grafico(datos);
                mostrarGrafico(grafico); // Mostrar el gráfico en la interfaz
            }

            // Preparar nombre sugerido para el archivo PDF
            String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String nombreArchivo = "CompararInstitucionesCon" + String.valueOf(view.institucion.getSelectedItem()) + fecha;

            // Mostrar diálogo para guardar el archivo
            JFileChooser f = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);
            f.setDialogTitle("Guardar archivo ");
            f.setSelectedFile(new File(nombreArchivo + ".pdf"));

            int userSelection = f.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Verificar unicidad del archivo
                File c = f.getSelectedFile();
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());

                // Exportar el gráfico al archivo PDF
                ExportarGrafico(datos, file);
            }
        } else {
            // Mostrar mensaje si faltan datos obligatorios
            JOptionPane.showMessageDialog(view, "Por favor seleccione una institución, un año y un alcance antes de proceder.", "Datos faltantes", JOptionPane.WARNING_MESSAGE);
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

        // Si el archivo no existe, devolver directamente el archivo original
        if (!file.exists()) {
            return file;
        }

        // Separar el nombre base y la extensión del archivo
        String baseName = filename;
        String extension = "";
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = filename.substring(0, dotIndex);
            extension = filename.substring(dotIndex); // Incluye el punto (.)
        }

        // Generar nuevos nombres incrementales hasta encontrar uno único
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
     * Exporta un gráfico y una tabla de datos a un archivo PDF.
     * El gráfico se genera a partir de un conjunto de datos (`DefaultCategoryDataset`) y se inserta en un documento PDF.
     * También se incluye una tabla con la información de las instituciones universitarias y una breve descripción.
     *
     * @param dato El conjunto de datos que se utilizará para generar el gráfico.
     * @param r El archivo en el que se guardará el documento PDF.
     */
    private void ExportarGrafico(DefaultCategoryDataset dato, File r) {
        Document document = new Document(PageSize.A3); // Creación del documento con tamaño A3

        try {
            // Generar el gráfico a partir de los datos proporcionados
            JFreeChart chartDato = grafico(dato);

            // Crear una corriente de salida para almacenar la imagen del gráfico en formato PNG
            ByteArrayOutputStream chartStreamDatos = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamDatos, chartDato.createBufferedImage(600, 300));
            byte[] chartBytesDatos = chartStreamDatos.toByteArray();

            // Crear el archivo PDF en el destino especificado
            PdfWriter.getInstance(document, new FileOutputStream(r));
            document.open(); // Abrir el documento PDF

            // Añadir fecha de creación al documento
            String fechaActual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            Paragraph fecha = new Paragraph("Fecha de creación: " + fechaActual);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            document.add(fecha);
            document.add(new Paragraph(" "));

            // Descripción general del gráfico y de los datos que contiene
            Paragraph text = new Paragraph("En la siguiente gráfica, encontrará una tabla que contiene toda la información de las instituciones universitarias con las que se realizó la comparación. La tabla incluye el NIT, el departamento y el modeloMunicipio al que pertenece cada institución.");
            document.add(text);
            document.add(new Paragraph(" "));

            // Crear una tabla en el PDF con los datos de las instituciones
            PdfPTable tabla = new PdfPTable(view.Instituciones.getColumnCount());
            tabla.setWidthPercentage(100); // Establecer el porcentaje de ancho de la tabla (100%)

            // Añadir los nombres de las columnas a la tabla
            for (int i = 0; i < view.Instituciones.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(view.Instituciones.getColumnName(i)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabla.addCell(cell);
            }

            // Añadir los valores de las filas de la tabla
            for (int i = 0; i < view.Instituciones.getRowCount(); i++) {
                for (int j = 0; j < view.Instituciones.getColumnCount(); j++) {
                    Object value = view.Instituciones.getValueAt(i, j);
                    String cellText = (value != null) ? value.toString() : "";
                    tabla.addCell(cellText);
                }
            }

            // Añadir la tabla al documento
            document.add(tabla);
            document.add(new Paragraph(" "));

            // Descripción sobre el gráfico y los ejes
            Paragraph text2 = new Paragraph("En la siguiente gráfica encontraremos en el eje X las fuentes de emisión y en el eje Y la cantidad de CO2 emitida por las instituciones, y además tenemos las leyendas de cuál dato pertenece a cada institución.");
            document.add(text2);
            document.add(new Paragraph(""));

            // Añadir el gráfico al documento como una imagen
            Image chartImageDato = Image.getInstance(chartBytesDatos);
            chartImageDato.setAlignment(Element.ALIGN_CENTER);
            chartImageDato.setBorderWidth(23); // Establecer el borde de la imagen
            document.add(chartImageDato);

            // Mostrar un mensaje de éxito
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
     * Crea un gráfico de barras para comparar instituciones por alcance en relación a la cantidad de CO2 emitido.
     *
     * Este método utiliza la clase `ChartFactory` de JFreeChart para crear un gráfico de barras. El gráfico compara las
     * instituciones según las fuentes de emisión y la cantidad de CO2 emitida, utilizando los datos proporcionados.
     *
     * @param dataset El conjunto de datos que alimentará el gráfico. Este debe ser un objeto `DefaultCategoryDataset`.
     *
     * @return Un objeto `JFreeChart` que representa el gráfico de barras generado.
     */
    private JFreeChart grafico(DefaultCategoryDataset dataset) {
        // Crear el gráfico de barras con el título, las etiquetas de los ejes y el conjunto de datos proporcionado
        return ChartFactory.createBarChart(
                "Comparar instituciones por alcance ",  // Título del gráfico
                "Fuentes emision",                     // Etiqueta del eje X (Fuentes de emisión)
                "Cantidad de co2",                     // Etiqueta del eje Y (Cantidad de CO2)
                dataset                                // Conjunto de datos
        );
    }


    /**
     * Muestra un gráfico en el panel destinado para ello en la interfaz de modeloUsuario.
     *
     * Este método toma un gráfico de tipo `JFreeChart`, lo coloca en un panel (`ChartPanel`) y lo agrega a un contenedor
     * específico en la interfaz gráfica. Además, habilita la capacidad de desplazamiento mediante la rueda del mouse
     * y ajusta el tamaño del panel donde se mostrará el gráfico.
     *
     * @param chart El gráfico a mostrar. Este debe ser un objeto de tipo `JFreeChart` que representa el gráfico que
     *              se desea visualizar.
     */
    private void mostrarGrafico(JFreeChart chart) {
        // Crear un panel que contendrá el gráfico
        ChartPanel panel = new ChartPanel(chart);

        // Habilitar la función de desplazamiento por rueda del mouse
        panel.setMouseWheelEnabled(true);

        // Establecer el tamaño preferido del panel (300x700 píxeles)
        panel.setPreferredSize(new Dimension(300, 700));

        // Configurar el contenedor donde se mostrará el gráfico
        view2.PanelGrafico.setLayout(new BorderLayout());

        // Limpiar cualquier componente previamente agregado al contenedor
        view2.PanelGrafico.removeAll();

        // Agregar el panel con el gráfico en el centro del contenedor
        view2.PanelGrafico.add(panel, BorderLayout.CENTER);

        // Revalidar y repintar el contenedor para asegurar que se actualice la interfaz
        view2.PanelGrafico.revalidate();
        view2.PanelGrafico.repaint();

        // Hacer visible el contenedor que contiene el gráfico
        view2.setVisible(true);
    }


    /**
     * Carga la lista de instituciones en el comboBox de la interfaz gráfica.
     *
     * Este método consulta las instituciones disponibles desde el modelo de datos, limpia el comboBox
     * actual, agrega un item vacío como indicativo y luego llena el comboBox con las instituciones obtenidas.
     * Además, si el comboBox tiene elementos después de ser llenado, selecciona automáticamente el primer elemento.
     */
    private void cargarInstitucion() {
        // Crear una instancia de la clase ConsultasInstitucion para acceder a la base de datos o modelo de datos
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();

        // Obtener la lista de instituciones desde el Modelo
        List<String> instituciones = consultasInstitucion.obtenerInstituciones();

        // Limpiar el comboBox y agregar un item vacío
        view.institucion.removeAllItems();  // Limpiar todos los elementos actuales en el comboBox
        view.institucion.addItem("");  // Añadir un item vacío como indicativo para el modeloUsuario

        // Llenar el comboBox con las instituciones obtenidas del Modelo
        for (String institucion : instituciones) {
            view.institucion.addItem(institucion);  // Agregar cada institución a la lista del comboBox
        }

        // Si el comboBox tiene elementos, seleccionamos el primero
        if (view.institucion.getItemCount() > 0) {
            view.institucion.setSelectedIndex(0);  // Seleccionar el primer elemento del comboBox
        }
    }


    /**
     * Carga los años base en el comboBox de la interfaz gráfica, basado en la institución y modeloMunicipio seleccionados.
     *
     * Este método consulta los años base disponibles desde el modelo de datos en función de la institución y
     * el modeloMunicipio seleccionados en la vista. Luego, limpia el comboBox de años y lo llena con los valores obtenidos.
     * Finalmente, selecciona el primer año si el comboBox tiene elementos.
     */
    private void cargarAnioBase() {
        // Obtener el nombre de la institución y modeloMunicipio seleccionados desde la vista
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String nombreMunicipio = String.valueOf(view.Municipio.getSelectedItem());

        // Crear una instancia de ConsultasInstitucion para obtener los años base desde el modelo de datos
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();

        // Obtener la lista de años base disponibles para la institución y modeloMunicipio seleccionados
        List<String> aniosBase = consultasInstitucion.obtenerAnioBase(nombreInstitucion, nombreMunicipio);

        System.out.println(aniosBase);  // Imprimir la lista de años base para depuración

        // Limpiar el comboBox de años y agregar un item vacío
        view.anio.removeAllItems();  // Limpiar todos los elementos actuales en el comboBox de años
        view.anio.addItem("");  // Añadir un item vacío como indicativo para el modeloUsuario

        // Llenar el comboBox con los años base obtenidos desde el Modelo
        for (String anio : aniosBase) {
            view.anio.addItem(anio);  // Agregar cada año base a la lista del comboBox
        }

        // Si el comboBox tiene elementos, seleccionamos el primer año
        if (view.anio.getItemCount() > 0) {
            view.anio.setSelectedIndex(0);  // Seleccionar el primer elemento del comboBox
        }
    }


    /**
     * Carga los municipios disponibles en el comboBox de la interfaz gráfica,
     * basado en la institución seleccionada por el modeloUsuario.
     *
     * Este método obtiene los municipios asociados a la institución seleccionada
     * desde el modelo de datos, limpia el comboBox de municipios y lo llena con los
     * valores obtenidos. Si no se ha seleccionado una institución, limpia el comboBox.
     */
    private void cargarMunicipio() {
        // Obtener el nombre de la institución seleccionada desde la vista
        String nombreInstitucion = (String) view.institucion.getSelectedItem();

        // Crear una instancia de ConsultasInstitucion para obtener los municipios desde el modelo
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();

        // Verificar que la institución seleccionada no esté vacía
        if (nombreInstitucion != null && !nombreInstitucion.isEmpty()) {
            // Obtener la lista de municipios asociados a la institución seleccionada
            List<String> municipios = consultasInstitucion.obtenerMunicipios(nombreInstitucion);

            // Limpiar el comboBox de municipios y agregar un item vacío como indicativo
            view.Municipio.removeAllItems();  // Limpiar todos los elementos actuales
            view.Municipio.addItem("");  // Añadir un item vacío como indicativo

            // Llenar el comboBox con los municipios obtenidos desde el Modelo
            for (String municipio : municipios) {
                view.Municipio.addItem(municipio);  // Agregar cada modeloMunicipio al comboBox
            }

            // Si el comboBox tiene elementos, seleccionamos el primer modeloMunicipio
            if (view.Municipio.getItemCount() > 0) {
                view.Municipio.setSelectedIndex(0);  // Seleccionar el primer elemento del comboBox
            }
        } else {
            // Si no se ha seleccionado una institución, limpiar el comboBox de municipios
            view.Municipio.removeAllItems();
        }
    }


    /**
     * Carga los núcleos disponibles en el comboBox de la interfaz gráfica,
     * basado en la institución seleccionada por el modeloUsuario.
     *
     * Este método obtiene los núcleos asociados a la institución seleccionada
     * desde el modelo de datos, limpia el comboBox y lo llena con los valores obtenidos.
     */
    private void cargarNucleosExistentes() {
        // Limpiar el comboBox de núcleos antes de cargar los nuevos valores
        view.nucleo.removeAllItems();  // Limpiar todos los elementos actuales
        view.nucleo.addItem(" ");  // Añadir un item vacío como indicativo

        // Crear instancia de la clase Conexion para interactuar con la base de datos
        Conexion conexion = new Conexion();

        // Crear instancia de ConsultaNucleo para obtener los núcleos desde el modelo
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);

        // Obtener el nombre de la institución seleccionada desde la vista
        String nombreInstitucion = Objects.requireNonNull(view.institucion.getSelectedItem()).toString();

        // Llamar al modelo para obtener la lista de núcleos asociados a la institución seleccionada
        List<String> nucleos = consultaNucleo.cargarNucleos(nombreInstitucion);

        // Agregar los núcleos al comboBox
        for (String nucleo : nucleos) {
            view.nucleo.addItem(nucleo);  // Agregar cada núcleo al comboBox
        }
    }


    /**
     * Carga los años base específicos para la institución, modeloMunicipio y núcleo
     * seleccionados en los comboBox de la interfaz gráfica.
     *
     * Este método limpia el comboBox de años y lo llena con los años base
     * obtenidos desde el modelo de datos en función de la institución, modeloMunicipio
     * y núcleo seleccionados.
     */
    private void anioBaseNucleo() {
        // Limpiar el comboBox de años antes de cargar los nuevos valores
        view.anio.removeAllItems();  // Limpiar todos los elementos actuales
        view.anio.addItem(" ");  // Añadir un item vacío como indicativo

        // Crear instancia de la clase Conexion para interactuar con la base de datos
        Conexion conexion = new Conexion();

        // Crear instancia de ConsultaNucleo para obtener los años base desde el modelo
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);

        // Obtener el nombre de la institución, modeloMunicipio y núcleo seleccionados desde la vista
        String nombre = String.valueOf(view.institucion.getSelectedItem());
        String municipio = String.valueOf(view.Municipio.getSelectedItem());
        String nucleo = String.valueOf(view.nucleo.getSelectedItem());

        // Llamar al modelo para obtener la lista de años base asociados a la institución,
        // modeloMunicipio y núcleo seleccionados
        List<String> anioBaseNucleo = consultaNucleo.obtenerAnosBase(nombre, municipio, nucleo);

        // Agregar los años base al comboBox de años
        for (String anioBase : anioBaseNucleo) {
            view.anio.addItem(anioBase);  // Agregar cada año base al comboBox
        }
    }


    /**
     * Carga los años base para la institución y modeloMunicipio seleccionados,
     * y los muestra en el comboBox de años. Si la institución o modeloMunicipio
     * no están seleccionados, se limpia el comboBox de años.
     *
     * Este método obtiene la lista de años base asociados a la institución
     * y modeloMunicipio seleccionados desde el modelo de datos, y actualiza el
     * comboBox correspondiente.
     */
    private void anioParaSumaTodosLosNucleos() {
        // Crear instancia de la clase Conexion para interactuar con la base de datos
        Conexion conexion = new Conexion();

        // Crear instancia de ConsultaNucleo para obtener los años base desde el modelo
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);

        // Obtener el nombre de la institución y modeloMunicipio seleccionados desde la vista
        String nombreInstitucion = (String) view.institucion.getSelectedItem();
        String nombreMunicipio = (String) view.Municipio.getSelectedItem();

        // Verificar que los valores seleccionados no sean nulos o vacíos
        if (nombreInstitucion != null && !nombreInstitucion.isEmpty() &&
                nombreMunicipio != null && !nombreMunicipio.isEmpty()) {

            // Obtener la lista de años base desde el Modelo
            List<String> aniosBase = consultaNucleo.obteneranioParaSumaTodosLosNucleos(nombreInstitucion, nombreMunicipio);

            // Limpiar el JComboBox de años y agregar un item vacío como indicativo
            view.anio.removeAllItems();
            view.anio.addItem(""); // Agregar un espacio vacío para indicar al modeloUsuario que elija un año

            // Llenar el JComboBox con los años base obtenidos del Modelo
            for (String anio : aniosBase) {
                view.anio.addItem(anio); // Agregar cada año base al comboBox
            }

            // Si el JComboBox tiene elementos, seleccionamos el primero
            if (view.anio.getItemCount() > 0) {
                view.anio.setSelectedIndex(0); // Seleccionamos el primer elemento
            }
        } else {
            // Si la institución o modeloMunicipio están vacíos, limpiar el JComboBox de años
            view.anio.removeAllItems();
        }
    }

    /**
     * Elimina las filas seleccionadas en la tabla "Instituciones".
     * Si no se selecciona ninguna fila, muestra un mensaje de advertencia.
     * Si la tabla queda vacía tras la eliminación, habilita el campo de selección "alcance"
     * y lo restablece al primer valor.
     */
    private void eliminarFila() {
        // Obtén el modelo de la tabla "Instituciones" para poder modificar sus filas
        DefaultTableModel tableModel = (DefaultTableModel) view.Instituciones.getModel();

        // Obtén las filas seleccionadas
        int[] selectedRows = view.Instituciones.getSelectedRows();

        // Verifica si hay filas seleccionadas
        if (selectedRows.length > 0) {
            // Eliminamos las filas en orden descendente para evitar errores al modificar el índice
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                tableModel.removeRow(selectedRows[i]);
            }
        } else {
            // Si no se seleccionó ninguna fila, muestra un mensaje de advertencia
            JOptionPane.showMessageDialog(view, "Por favor, selecciona una fila para eliminar.", "No se seleccionó ninguna fila", JOptionPane.WARNING_MESSAGE);
        }

        // Si la tabla está vacía después de eliminar las filas
        if (tableModel.getRowCount() == 0) {
            // Habilita el campo "alcance" y restablece su valor al primer índice
            view.alcance.setEnabled(true);
            view.alcance.setSelectedIndex(0);
        }
    }
    /**
     * Navega a la vista inicial de la aplicación.
     * Pasos:
     * 1. Crea el controlador de inicio con las dependencias necesarias.
     * 2. Llama al método que inicia la vista de inicio.
     * 3. Cierra la vista actual.
     */
    private void BotonInicio() {
        ControladorPestaniaPrincipal control = new ControladorPestaniaPrincipal(modeloInstitucion, user, modeloMunicipio);
        control.inicio();
        view.dispose();
        view2.dispose();
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
        ControladorPerfil control = new ControladorPerfil(user, per, modeloInstitucion, modeloMunicipio, cons);
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
        // Crear la conexión a la base de datos
        Conexion con = new Conexion();

        // Crear la instancia de la vista para el cálculo
        Calcular viewCal = new Calcular();

        // Crear la instancia del modelo para el cálculo
        ModeloEmisionCalcular mod = new ModeloEmisionCalcular();

        // Crear la instancia de las consultas para el cálculo
        CalcularConsultas consul = new CalcularConsultas(con);

        // Crear la instancia para realizar consultas de modeloUsuario
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);

        // Crear el controlador que manejará la lógica entre el modelo, las vistas y las consultas
        ControladorCalcular controlador = new ControladorCalcular(mod, consul, viewCal, modeloInstitucion, consultaUsuario, user, modeloMunicipio);

        // Iniciar el controlador, lo que generalmente configura la vista y otros elementos necesarios
        controlador.iniciar();

        // Hacer visible la vista de cálculo
        viewCal.setVisible(true);

        // Cerrar la vista actual de la aplicación
        view.dispose();
    }

    /**
     * Inicia la vista para registrar emisiones.
     * Pasos:
     * 1. Configura la vista, modelo y consultas necesarios para registrar emisiones.
     * 2. Inicializa el controlador asociado.
     * 3. Muestra la vista de registro de emisiones y cierra la vista actual.
     */
    private void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        ModeloEmision mod = new ModeloEmision();
        ConsultasEmision consul = new ConsultasEmision();
        ControladorEmision controlador = new ControladorEmision(mod, consul, emisionView, modeloInstitucion, modeloMunicipio, user);
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
        ControladorInforme contro = new ControladorInforme(viewInfo, mod, consul, modeloMunicipio, modeloInstitucion, user);
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
        ControladorGrafico contro = new ControladorGrafico(mod, consul, viewGraf, modelo, modeloMunicipio, user, modeloInstitucion);
        contro.iniciar();
        viewGraf.setVisible(true);
        view.dispose();
    }
    /**
     * Inicia la vista para mostrar tendencias históricas de datos mediante gráficos.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador para gestionar las tendencias históricas.
     * 3. Muestra la vista de gráficos y cierra la vista actual.
     */
    private void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        ModeloTendencia mod = new ModeloTendencia();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia viewGraf = new GraficoTendencia();
        ControladorTendencia control = new ControladorTendencia(mod, consult, viewGraf, modeloMunicipio, modeloInstitucion, user);
        viewGraf.setVisible(true);
        control.iniciar();
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
        ControladorReducir redu = new ControladorReducir(modeloInstitucion, consul, vista, modeloMunicipio, user);
        redu.Iniciar();
        view.dispose();
    }
    /**
     * Inicia la vista para actualizar los datos de una institución.
     * Pasos:
     * 1. Configura las dependencias necesarias: vista, modelo y consultas.
     * 2. Inicializa el controlador para gestionar la lógica de actualización.
     * 3. Muestra la vista de actualización y cierra la vista actual.
     */
    private void vistaActualizarInstitucion() {
        Conexion con = new Conexion();
        ConsultasInstitucion consul = new ConsultasInstitucion();
        VerDatosInstitucion view2 = new VerDatosInstitucion();
        ModeloInstitucion mod = new ModeloInstitucion();
        ControladorInstitucion control = new ControladorInstitucion(modeloInstitucion, modeloMunicipio, view2, consul, mod, user);
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

        ControladorVerPerfiles verControl = new ControladorVerPerfiles(user, modeloInstitucion, modeloMunicipio, verPerfiles, consul);
        verControl.Iniciar();
        view.dispose();
    }
    /**
     * Inicia la vista para visualizar instituciones.
     * Pasos:
     * 1. Crea las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica.
     * 3. Opcionalmente, cierra la vista actual (comentado en este caso).
     */
    private void vistaVerInstitucion() {
        Conexion con = new Conexion();
        VerInstituciones verInstituciones = new VerInstituciones();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(con);
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        ModeloInstitucion modeloInstitucion = new ModeloInstitucion();
        ControladorVerInstituciones contraladorVerInstituciones = new ControladorVerInstituciones(consultasInstitucion, consultaNucleo,
                modeloInstitucion, verInstituciones, user, modeloMunicipio);
        contraladorVerInstituciones.iniciar();
        view.dispose();
    }

}
