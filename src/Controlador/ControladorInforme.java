package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ControladorInforme {
    private final Informe view;
    private final ModeloEmisionInforme mod;
    private final ConsultaInforme consul;
    private final ModeloMunicipio m;
    private final ModeloInstitucion ins;
    private final ModeloUsuario modUser;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public ControladorInforme(Informe view, ModeloEmisionInforme mod, ConsultaInforme consul,
                              ModeloMunicipio m, ModeloInstitucion ins, ModeloUsuario modUse) {
        this.view = view;
        this.consul = consul;
        this.mod = mod;
        this.m = m;
        this.ins = ins;
        this.modUser = modUse;
        Listeners();
    }


    public void iniciar() {
        // Agrega opciones de gráficos al menú de la vista
        view.Graficos.add(GraficoPrincipal);
        view.Graficos.add(GraficosCompararInstitucion);
        view.Graficos.add(GraficoHistorico);

        // Obtiene el tipo de modeloUsuario para personalizar la vista
        String usuario = modUser.getTipoUsuario();

        // Obtiene el modelo de la tabla de emisiones y configura el ordenamiento de las filas
        DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);

        // Personaliza la vista según el tipo de modeloUsuario
        switch (usuario) {
            case "Administrador":
                cargarInstitucion();
                cargarAnioBase();
                cargarMunicipio();
                view.setTitle("Informe");
                view.setLocationRelativeTo(null);
                view.Emisiones.setRowSorter(sorter);
                view.setVisible(true);
                view.VerPerfiles.setVisible(false);
                view.verInstitucion.setVisible(false);
                break;

            case "Superadmin":
                view.setTitle("Informe");
                view.setLocationRelativeTo(null);
                cargarInstitucion();
                cargarAnioBase();
                cargarMunicipio();
                cargarNucleosExistentes();
                sorter = new TableRowSorter<>(tableModel);
                view.Emisiones.setRowSorter(sorter);
                break;

            case "Invitado":
                view.setTitle("Informe");
                view.verInstitucion.setVisible(false);
                view.RegistrarInstitucion.setVisible(false);
                view.VerPerfiles.setVisible(false);
                view.RegistrarEmision.setVisible(false);
                view.Calcular.setVisible(false);
                view.perfil.setVisible(true);
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                cargarInstitucion();
                cargarAnioBase();
                cargarMunicipio();
                cargarNucleosExistentes();
                sorter = new TableRowSorter<>(tableModel);
                view.Emisiones.setRowSorter(sorter);
                break;

            default:
                JOptionPane.showMessageDialog(null, "ModeloUsuario no definido en el sistema");
                break;
        }

        // Asigna los oyentes de acción a los botones de los gráficos
        GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
        GraficosCompararInstitucion.addActionListener(e -> vistaCompararInstituciones());
        GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
    }

    private void actionPerformed(ActionEvent e) {
        // Acciones de los botones según el evento

        // Acción para el botón "Buscar"
        if (e.getSource() == view.buscarButton) {
            if (!view.comboNucleo.isVisible()) {
                LlenarTablaSinNucleo();
            }
            if (view.comboNucleo.isVisible()) {
                LlenarTablaConNucleo();
            }
        }

        // Acción para el botón "Descargar"
        if (e.getSource() == view.descargarButton) {
            if (!view.comboNucleo.isVisible()) {
                LlenarDocumentoSinNucleo();
            }
            if (view.comboNucleo.isVisible()) {
                LlenarDocumentoConNucleo();
            }
        }

        // Acción para el botón "Inicio"
        if (e.getSource() == view.inicioButton) {
            BotonInicio();
        }

        // Acción para el botón "Perfil"
        if (e.getSource() == view.perfil) {
            vistaPerfil();
        }

        // Acción para el botón "Registrar Emisión"
        if (e.getSource() == view.RegistrarEmision) {
            vistaRegistrarEmision();
        }

        // Acción para el botón "Calcular"
        if (e.getSource() == view.Calcular) {
            vistaCalcular();
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

        // Acción para el botón "Ver Institución"
        if (e.getSource() == view.verInstitucion) {
            vistaVerInstitucion();
        }
    }

    /**
     * Método que genera un documento PDF sin núcleo seleccionado, con la información de emisiones.
     */
    private void LlenarDocumentoSinNucleo() {
        // Se obtiene la fecha actual y el nombre de la institución seleccionada
        String Nombre = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String Institucion = String.valueOf(view.institucion.getSelectedItem());
        Document document = new Document();
        String b = "InformeDeLa" + Institucion + Nombre;

        // Se obtiene la lista de datos de la institución y modeloMunicipio seleccionados
        List<ModeloEmisionInforme> a = consul.datos(view.institucion.getSelectedItem().toString(), m.getNombreM());

        // Configuración del JFileChooser para guardar el archivo PDF
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Guardar archivo PDF");
        fileChooser.setSelectedFile(new File(b + ".pdf"));
        int userSelection = fileChooser.showSaveDialog(null);

        // Si el modeloUsuario selecciona un archivo para guardar
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File c = fileChooser.getSelectedFile();
            File file = ensureUniqueFilename(c.getParentFile(), c.getName());  // Asegura que el archivo no se sobrescriba

            try {
                // Crea un escritor de PDF para generar el archivo
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Crea la tabla de datos de emisiones
                PdfPTable table = new PdfPTable(view.Emisiones.getColumnCount());

                // Añadir la fecha de creación al documento
                String fechaActual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                Paragraph fecha = new Paragraph("Fecha de creación: " + fechaActual);
                fecha.setAlignment(Element.ALIGN_RIGHT);
                document.add(fecha);
                document.add(new Paragraph(" "));  // Espacio entre la fecha y la tabla

                // Añadir el nombre de la institución
                Paragraph nombreInstitucion = new Paragraph("Nombre de la Institucion: " + Institucion);
                nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
                document.add(nombreInstitucion);
                document.add(new Paragraph(" "));

                // Añadir los datos de la institución y el modeloMunicipio
                for (ModeloEmisionInforme info : a) {
                    String nombreMunicipi = info.getMunicipio();
                    Paragraph municipio1 = new Paragraph("ModeloMunicipio: " + nombreMunicipi);
                    municipio1.setAlignment(Element.ALIGN_LEFT);
                    document.add(municipio1);
                    document.add(new Paragraph(" "));

                    String d = info.getDepartamento();
                    Paragraph departamento = new Paragraph("Departamento: " + d);
                    departamento.setAlignment(Element.ALIGN_LEFT);
                    document.add(departamento);
                    document.add(new Paragraph(" "));

                    String i = info.getNit();
                    Paragraph nit = new Paragraph("Nit de la institución: " + i);
                    nit.setAlignment(Element.ALIGN_LEFT);
                    document.add(nit);
                    document.add(new Paragraph(" "));
                }

                // Descripción de las columnas en la tabla
                Paragraph text = new Paragraph("En la siguiente tabla vamos a encontrar las siguiente columnas \n" +
                        "Nombre de la fuente de emisión: Aparece el nombre de cada una de las fuentes de emisión asociadas a la institución en el año base seleccionado.\n" +
                        "Alcance: Corresponde al alcance de cada una de las fuentes de emisión.\n" +
                        "Cantidad consumida: Indica el consumo que tuvo la institución durante el año base de dicha fuente de emisión.\n" +
                        "Unidad de medida: Es la unidad en la que se mide cada fuente de emisión.\n" +
                        "Factor de emisión: Factor estandarizado por Colombia, utilizado para calcular el CO2 aportado por cada fuente de emisión.\n" +
                        "Año base: Año en que fueron recolectados los datos de la fuente de emisión.\n");
                document.add(text);
                document.add(new Paragraph(" "));

                // Añadir la tabla con los datos de las emisiones
                table.setWidthPercentage(100);
                for (int i = 0; i < view.Emisiones.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(view.Emisiones.getColumnName(i)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                // Añadir los datos de las filas de la tabla
                for (int i = 0; i < view.Emisiones.getRowCount(); i++) {
                    for (int j = 0; j < view.Emisiones.getColumnCount(); j++) {
                        Object value = view.Emisiones.getValueAt(i, j);
                        String cellText = (value != null) ? value.toString() : "";
                        table.addCell(cellText);
                    }
                }

                document.add(table);
                document.add(new Paragraph(" "));

                // Añadir el total de CO2 emitido
                String t = view.total.getText();
                Paragraph tot = new Paragraph("Total de CO2 emitido para el año base seleccionado: " + t);
                document.add(tot);
                document.add(new Paragraph(" "));

                // Mensaje de éxito
                JOptionPane.showMessageDialog(null, "PDF generado correctamente.");

            } catch (DocumentException | FileNotFoundException ex) {
                throw new RuntimeException(ex);
            } finally {
                document.close();
                System.out.println("Documento cerrado.");
            }
        }
    }


    /**
     * Método que genera un documento PDF con la información de emisiones,
     * incluyendo los datos relacionados con un núcleo específico.
     */
    private void LlenarDocumentoConNucleo() {
        // Obtiene la fecha actual en formato "dd-MM-yyyy"
        String Nombre = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        // Obtiene el nombre de la institución seleccionada desde la vista
        String Institucion = String.valueOf(view.institucion.getSelectedItem());

        // Obtiene el nombre del núcleo seleccionado desde la vista
        String nucleo = String.valueOf(view.comboNucleo.getSelectedItem());

        // Crea un nuevo documento PDF
        Document document = new Document();

        // Crea un nombre de archivo único combinando la institución, la fecha y el núcleo
        String b = "InformeDeLa" + Institucion + Nombre + "DelNucleo" + nucleo;

        // Obtiene los datos de la institución y modeloMunicipio utilizando el modelo de datos
        List<ModeloEmisionInforme> a = consul.datos(view.institucion.getSelectedItem().toString(), m.getNombreM());

        // Configura el JFileChooser para seleccionar el lugar y nombre de guardado del archivo PDF
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Guardar archivo PDF");
        fileChooser.setSelectedFile(new File(b + ".pdf"));

        // Muestra el cuadro de diálogo para guardar el archivo
        int userSelection = fileChooser.showSaveDialog(null);

        // Si el modeloUsuario selecciona un archivo, continua con la generación del PDF
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File c = fileChooser.getSelectedFile();

            // Asegura que el nombre del archivo no se sobrescriba
            File file = ensureUniqueFilename(c.getParentFile(), c.getName());

            try {
                // Inicializa el escritor del documento PDF
                PdfWriter.getInstance(document, new FileOutputStream(file));

                // Abre el documento para agregar contenido
                document.open();

                // Crea una tabla de PDF con la misma cantidad de columnas que la tabla de emisiones
                PdfPTable table = new PdfPTable(view.Emisiones.getColumnCount());

                // Obtiene la fecha actual para agregarla al documento
                String fechaActual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                // Añade la fecha de creación del documento al PDF
                Paragraph fecha = new Paragraph("Fecha de creación: " + fechaActual);
                fecha.setAlignment(Element.ALIGN_RIGHT);
                document.add(fecha);
                document.add(new Paragraph(" ")); // Espacio entre la fecha y la tabla

                // Añade el nombre de la institución al documento
                String nombre = view.institucion.getSelectedItem().toString();
                Paragraph nombreInstitucion = new Paragraph("Nombre de la Institucion: " + nombre);
                nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
                document.add(new Paragraph((""))); // Añadir espacio
                document.add(nombreInstitucion);
                document.add(new Paragraph("  "));

                // Añade el nombre del núcleo al documento
                String nombreNucleo = view.comboNucleo.getSelectedItem().toString();
                Paragraph nNucleo = new Paragraph("Nombre del modeloNucleo: " + nombreNucleo);
                nNucleo.setAlignment(Element.ALIGN_LEFT);
                document.add(new Paragraph((""))); // Añadir espacio
                document.add(nNucleo);
                document.add(new Paragraph("  "));

                // Añade información adicional sobre la institución (modeloMunicipio, departamento, NIT)
                for (ModeloEmisionInforme info : a) {
                    String nombreMunicipi = info.getMunicipio();
                    Paragraph municipio1 = new Paragraph("ModeloMunicipio: " + nombreMunicipi);
                    municipio1.setAlignment(Element.ALIGN_LEFT);
                    document.add(municipio1);
                    document.add(new Paragraph("  "));

                    // Información adicional sobre el departamento
                    String d = info.getDepartamento();
                    Paragraph departamento = new Paragraph("Departamento: " + d);
                    departamento.setAlignment(Element.ALIGN_LEFT);
                    document.add(departamento);
                    document.add(new Paragraph("  "));

                    // Información adicional sobre el NIT de la institución
                    String i = info.getNit();
                    Paragraph nit = new Paragraph("Nit de la institución: " + i);
                    nit.setAlignment(Element.ALIGN_LEFT);
                    document.add(nit);
                    document.add(new Paragraph("  "));
                }

                // Descripción de las columnas que se muestran en la tabla
                Paragraph text = new Paragraph("En la siguiente tabla vamos a encontrar las siguiente columnas \n" +
                        "Nombre de la fuente de emisión: Aparece el nombre de cada una de las fuentes de emisión asociadas a la institución en el año base seleccionado.\n" +
                        "Alcance: Corresponde al alcance de cada una de las fuentes de emisión.\n" +
                        "Cantidad consumida: Indica el consumo que tuvo la institución durante el año base de dicha fuente de emisión.\n" +
                        "Unidad de medida: Es la unidad en la que se mide cada fuente de emisión.\n" +
                        "Factor de emisión: Factor estandarizado por Colombia, utilizado para calcular el CO2 aportado por cada fuente de emisión.\n" +
                        "Año base: Año en que fueron recolectados los datos de la fuente de emisión.\n");
                document.add(text);
                document.add(new Paragraph(" ")); // Espacio entre la descripción y la tabla

                // Añade los encabezados de la tabla (nombres de las columnas)
                table.setWidthPercentage(100);
                for (int i = 0; i < view.Emisiones.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(view.Emisiones.getColumnName(i)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                // Añade los datos de la tabla de emisiones
                for (int i = 0; i < view.Emisiones.getRowCount(); i++) {
                    for (int j = 0; j < view.Emisiones.getColumnCount(); j++) {
                        Object value = view.Emisiones.getValueAt(i, j);
                        String cellText = (value != null) ? value.toString() : "";
                        table.addCell(cellText);
                    }
                }

                // Añade la tabla al documento PDF
                document.add(table);
                document.add(new Paragraph(" ")); // Espacio adicional

                // Añade el total de CO2 emitido al final del documento
                String t = view.total.getText();
                Paragraph tot = new Paragraph("Total de CO2 emitido para el año base seleccionado: " + t);
                document.add(tot);
                document.add(new Paragraph(" ")); // Espacio final

                // Muestra un mensaje de éxito cuando se genera el PDF
                JOptionPane.showMessageDialog(null, "PDF generado correctamente.");

            } catch (DocumentException ex) {
                // Captura excepciones de documentos y muestra el error
                throw new RuntimeException(ex);
            } catch (FileNotFoundException ex) {
                // Captura excepciones si el archivo no puede ser encontrado y muestra el error
                throw new RuntimeException(ex);
            } finally {
                // Cierra el documento una vez que ha terminado de generarse
                document.close();
                System.out.println("Documento cerrado.");
            }
        }
    }


    /**
     * Método que llena una tabla con los datos de emisiones, excluyendo la selección de núcleo,
     * y calcula el total de la carga ambiental.
     */
    private void LlenarTablaSinNucleo() {
        // Obtiene los objetos seleccionados en los JComboBox de la vista
        Object anioObj = view.anio.getSelectedItem();
        Object InstitucionObj = view.institucion.getSelectedItem();
        Object municipioObj = view.modeloMunicipio.getSelectedItem();
        Object nucleoObje = view.comboNucleo.getSelectedItem();


        // Crea un modelo de tabla para la vista y define una variable para el total
        DefaultTableModel tableModel2 = (DefaultTableModel) view.Emisiones.getModel();
        double total = 0.0;

        // Verifica si los campos de institución y modeloMunicipio están vacíos
        if (InstitucionObj == null && municipioObj == null && nucleoObje == null) {
            // Muestra un mensaje si no se han seleccionado institución ni modeloMunicipio
            JOptionPane.showMessageDialog(null, "LLene los datos de institucion y modeloMunicipio");
        } else {
            // Verifica si el año está vacío
            if (anioObj == null) {
                // Muestra un mensaje si no hay datos para la sede seleccionada
                JOptionPane.showMessageDialog(null, "No hay datos para la sede seleccionada");
                return; // Sale del método si el año es nulo
            }
        }

        // Convierte los objetos a cadenas de texto
        String anio = String.valueOf(anioObj);
        String Institucion = String.valueOf(InstitucionObj);
        String municipio = String.valueOf(municipioObj);

        // Obtiene los informes de la base de datos utilizando la consulta
        List<ModeloEmisionInforme> informes = consul.tablaInforme(Institucion, Integer.parseInt(anio), municipio);

        // Limpia las filas anteriores de la tabla
        tableModel2.setRowCount(0);

        // Verifica si la institución y el modeloMunicipio no están vacíos
        if (!Institucion.isEmpty() && !municipio.isEmpty()) {
            // Añade las filas con los datos de los informes a la tabla
            for (ModeloEmisionInforme informe : informes) {
                Object[] row = new Object[]{
                        informe.getNombreFuente(),
                        informe.getTipoFuente(),
                        informe.getAlcance(),
                        informe.getCantidadConsumidad(),
                        informe.getUnidadMedidad(),
                        informe.getFactorEmision(),
                        informe.getCargaAmnbiental(),
                        informe.getAnioBase()
                };
                tableModel2.addRow(row);
            }

            // Calcular el total de la carga ambiental sumando las columnas correspondientes
            for (int row = 0; row < tableModel2.getRowCount(); row++) {
                String valor = tableModel2.getValueAt(row, 6).toString(); // Carga ambiental está en la columna 6
                if (!valor.isEmpty()) {
                    try {
                        double valor1 = Double.parseDouble(valor);
                        total += valor1; // Suma el valor al total
                    } catch (NumberFormatException ex) {
                        // Muestra un mensaje de error si el valor no se puede convertir a número
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
            }

            // Actualiza el campo de total en la vista con el valor calculado
            view.total.setText(String.valueOf(total));
        } else {
            // Muestra un mensaje si no hay datos para el modeloMunicipio seleccionado
            JOptionPane.showMessageDialog(null, "No hay datos para el modeloMunicipio seleccionado.");
            // Limpia las filas de la tabla si no hay datos
            tableModel2.setRowCount(0);
        }
    }


    /**
     * Método que llena una tabla con los datos de emisiones, incluyendo la selección de núcleo,
     * y calcula el total de la carga ambiental.
     */
    private void LlenarTablaConNucleo() {
        // Obtiene los objetos seleccionados en los JComboBox de la vista
        Object anioObj = view.anio.getSelectedItem();
        Object InstitucionObj = view.institucion.getSelectedItem();
        Object municipioObj = view.modeloMunicipio.getSelectedItem();
        Object nucleoObje = view.comboNucleo.getSelectedItem();

        // Crea un modelo de tabla para la vista y define una variable para el total
        DefaultTableModel tableModel2 = (DefaultTableModel) view.Emisiones.getModel();
        double total = 0.0;

        // Verifica si los campos de institución y modeloMunicipio están vacíos
        if (InstitucionObj == null && municipioObj == null) {
            // Muestra un mensaje si no se han seleccionado institución ni modeloMunicipio
            JOptionPane.showMessageDialog(null, "LLene los datos de institucion y modeloMunicipio");
        } else {
            // Verifica si el año está vacío
            if (anioObj == null) {
                // Muestra un mensaje si no hay datos para la sede seleccionada
                JOptionPane.showMessageDialog(null, "No hay datos para la sede seleccionada");
                return; // Sale del método si el año es nulo
            }
        }

        // Convierte los objetos a cadenas de texto
        String anio = String.valueOf(anioObj);
        String Institucion = String.valueOf(InstitucionObj);
        String municipio = String.valueOf(municipioObj);
        String nucleo = String.valueOf(nucleoObje);

        // Obtiene los informes de la base de datos utilizando la consulta, incluyendo el núcleo
        List<ModeloEmisionInforme> informes = consul.tablaInformeNucleo(Institucion, Integer.parseInt(anio), municipio, nucleo);

        // Limpia las filas anteriores de la tabla
        tableModel2.setRowCount(0);

        // Verifica si la institución y el modeloMunicipio no están vacíos
        if (!Institucion.isEmpty() && !municipio.isEmpty()) {
            // Añade las filas con los datos de los informes a la tabla
            for (ModeloEmisionInforme informe : informes) {
                Object[] row = new Object[]{
                        informe.getNombreFuente(),
                        informe.getTipoFuente(),
                        informe.getAlcance(),
                        informe.getCantidadConsumidad(),
                        informe.getUnidadMedidad(),
                        informe.getFactorEmision(),
                        informe.getCargaAmnbiental(),
                        informe.getAnioBase()
                };
                tableModel2.addRow(row);
            }

            // Calcular el total de la carga ambiental sumando las columnas correspondientes
            for (int row = 0; row < tableModel2.getRowCount(); row++) {
                String valor = tableModel2.getValueAt(row, 6).toString(); // Carga ambiental está en la columna 6
                if (!valor.isEmpty()) {
                    try {
                        double valor1 = Double.parseDouble(valor);
                        total += valor1; // Suma el valor al total
                    } catch (NumberFormatException ex) {
                        // Muestra un mensaje de error si el valor no se puede convertir a número
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
            }

            // Actualiza el campo de total en la vista con el valor calculado
            view.total.setText(String.valueOf(total));
        } else {
            // Muestra un mensaje si no hay datos para el modeloMunicipio seleccionado
            JOptionPane.showMessageDialog(null, "No hay datos para el modeloMunicipio seleccionado.");
            // Limpia las filas de la tabla si no hay datos
            tableModel2.setRowCount(0);
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
     * Carga el nombre de la institución en el componente correspondiente de la vista.
     * Obtiene el nombre de la institución y el modeloMunicipio desde los modelos correspondientes
     * y realiza una consulta para obtener el nombre de la institución en la base de datos.
     * Luego, muestra el nombre de la institución en el campo de texto de la vista y desactiva la edición.
     */
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

    /**
     * Carga los años base en el comboBox de la interfaz gráfica, basado en la institución y modeloMunicipio seleccionados.
     * <p>
     * Este método consulta los años base disponibles desde el modelo de datos en función de la institución y
     * el modeloMunicipio seleccionados en la vista. Luego, limpia el comboBox de años y lo llena con los valores obtenidos.
     * Finalmente, selecciona el primer año si el comboBox tiene elementos.
     */
    public void cargarAnioBase() {
        // Obtener la lista de años base desde el Modelo
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String nombreMunicipio = String.valueOf(view.modeloMunicipio.getSelectedItem());

        // Consultar los años base desde el Modelo
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        List<String> aniosBase = consultasInstitucion.obtenerAnioBase(nombreInstitucion, nombreMunicipio);

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

    /**
     * Carga los municipios disponibles en el comboBox de la interfaz gráfica,
     * basado en la institución seleccionada por el modeloUsuario.
     *
     * Este método obtiene los municipios asociados a la institución seleccionada
     * desde el modelo de datos, limpia el comboBox de municipios y lo llena con los
     * valores obtenidos. Si no se ha seleccionado una institución, limpia el comboBox.
     */
    public void cargarMunicipio() {
        // Obtener la institución seleccionada desde la vista
        String nombreInstitucion = (String) view.institucion.getSelectedItem();
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();

        // Verificar que la institución no esté vacía
        if (nombreInstitucion != null && !nombreInstitucion.isEmpty()) {
            // Obtener la lista de municipios desde el Modelo
            List<String> municipios = consultasInstitucion.obtenerMunicipios(nombreInstitucion);

            // Limpiar el JComboBox de municipios y agregar un item vacío
            view.modeloMunicipio.removeAllItems();
            view.modeloMunicipio.addItem("");  // Añadir un item vacío como indicativo

            // Llenar el JComboBox con los municipios obtenidos del Modelo
            for (String municipio : municipios) {
                view.modeloMunicipio.addItem(municipio);
            }

            // Si el JComboBox tiene elementos, seleccionamos el primero
            if (view.modeloMunicipio.getItemCount() > 0) {
                view.modeloMunicipio.setSelectedIndex(0); // Seleccionamos el primer elemento
            }
        } else {
            // Si la institución está vacía, limpiar el JComboBox de municipios
            view.modeloMunicipio.removeAllItems();
        }
    }

    /**
     * Carga los núcleos disponibles en el comboBox de la interfaz gráfica,
     * basado en la institución seleccionada por el modeloUsuario.
     *
     * Este método obtiene los núcleos asociados a la institución seleccionada
     * desde el modelo de datos, limpia el comboBox y lo llena con los valores obtenidos.
     */
    public void cargarNucleosExistentes() {
        // Limpiamos el combo box antes de cargar los nuevos valores
        view.comboNucleo.removeAllItems();
        view.comboNucleo.addItem(" ");
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
        // Obtenemos el nombre de la institución desde el modelo
        String nombreInstitucion = view.institucion.getSelectedItem().toString();
        // Llamamos al modelo para cargar los núcleos
        List<String> nucleos = consultaNucleo.cargarNucleos(nombreInstitucion);

        // Agregamos los núcleos al combo box
        for (String nucleo : nucleos) {
            view.comboNucleo.addItem(nucleo);
        }

        // Manejo de errores si ocurre algún problema en la consulta

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
        view.anio.removeAllItems(); // Limpiar el combo de años
        view.anio.addItem(" "); // Agregar un texto indicativo
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
        String nombre = String.valueOf(view.institucion.getSelectedItem());
        String municipio = String.valueOf(view.modeloMunicipio.getSelectedItem());
        String nucleo = String.valueOf(view.comboNucleo.getSelectedItem());
        List<String> anioBaseNucleo = consultaNucleo.obtenerAnosBase(nombre, municipio, nucleo);
        for (String anioBase : anioBaseNucleo) {
            view.anio.addItem(anioBase);
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
        ControladorPestaniaPrincipal control = new ControladorPestaniaPrincipal(ins, modUser, m);
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
        System.out.println(ins.getNombreInstitucion());
        ControladorPerfil control = new ControladorPerfil(modUser, per, ins, m, cons);

        control.Iniciar();
        per.setVisible(true);
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
        ControladorEmision controlador = new ControladorEmision(mod, consul, emisionView, ins, m, modUser);
        controlador.iniciar();
        emisionView.setVisible(true);
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
        Calcular viewCal = new Calcular();
        ModeloEmisionCalcular mod = new ModeloEmisionCalcular();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorCalcular controlador = new ControladorCalcular(mod, consul, viewCal, ins, consultaUsuario, modUser, m);
        controlador.iniciar();
        viewCal.setVisible(true);
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
        ControladorGrafico contro = new ControladorGrafico(mod, consul, viewGraf, modelo, m, modUser, ins);
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
        ControladorCompararInstitucion contro = new ControladorCompararInstitucion(mod, consultas, comIns, viewGraf, ins, m, modUser);
        contro.iniciar();
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
        ControladorTendencia control = new ControladorTendencia(mod, consult, viewGraf, m, ins, modUser);
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
        ControladorReducir redu = new ControladorReducir(ins, consul, vista, m, modUser);
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
        ConsultasInstitucion consul = new ConsultasInstitucion();
        VerDatosInstitucion view2 = new VerDatosInstitucion();
        ModeloInstitucion mod = new ModeloInstitucion();
        ControladorInstitucion control = new ControladorInstitucion(ins, m, view2, consul, mod, modUser);
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

        ControladorVerPerfiles verControl = new ControladorVerPerfiles(modUser, ins, m, verPerfiles, consul);
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
                modeloInstitucion, verInstituciones, modUser, m);
        contraladorVerInstituciones.iniciar();
        view.dispose();
    }
    /**
     * Método que inicializa los listeners para los botones y componentes de la vista.
     * Asocia eventos de acción a los botones y otros componentes interactivos.
     */
    private void Listeners() {
        this.view.buscarButton.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.descargarButton.addActionListener(this::actionPerformed);
        this.view.perfil.addActionListener(this::actionPerformed);
        this.view.RegistrarEmision.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);


// ActionListener para el combo box de institución
        this.view.institucion.addActionListener(e -> {
            if (view.institucion.getItemCount() > 0) {
                view.modeloMunicipio.removeAllItems(); // Limpiar el combo de modeloMunicipio
                view.comboNucleo.removeAllItems(); // Limpiar el combo de núcleos
                cargarMunicipio(); // Cargar municipios para la institución seleccionada
                cargarAnioBase(); // Cargar el año base para la institución
            }
            Conexion conn = new Conexion();
            ConsultaNucleo consultaNucleo;
            consultaNucleo = new ConsultaNucleo(conn);
            String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
            if (consultaNucleo.TieneNucleo(nombreInstitucion) >= 1) {
                view.modeloNucleo.setVisible(true);
                view.comboNucleo.setVisible(true);
                view.anio.removeAllItems();
            } else {
                view.comboNucleo.setVisible(false);
                view.modeloNucleo.setVisible(false);
                cargarAnioBase();
            }
        });

        // ActionListener para el combo box de modeloMunicipio
        this.view.modeloMunicipio.addActionListener(e -> {
            if (view.modeloMunicipio.getItemCount() > 0) {
                view.comboNucleo.removeAllItems(); // Limpiar el combo de núcleos si está visible
                cargarNucleosExistentes(); // Cargar núcleos si el checkbox está seleccionado

                cargarAnioBase(); // Cargar el año base
            }
        });
        this.view.comboNucleo.addActionListener(e -> {
            if (view.comboNucleo.getItemCount() > 0) {
                anioBaseNucleo(); // Cargar el año base basado en la selección del núcleo
            }
        });

        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);
        this.view.verInstitucion.addActionListener(this::actionPerformed);

    }

}
