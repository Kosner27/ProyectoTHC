package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ControladorReducir {
    private final ModeloInstitucion modeloInstitucion;
    private final GraficoConsulta graficoConsulta;
    private final Reducir2 view;
    private final ModeloMunicipio modeloMunicipio;
    private final ModeloUsuario modeloUsuario;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public ControladorReducir(ModeloInstitucion modeloInstitucion, GraficoConsulta graficoConsulta, Reducir2 view, ModeloMunicipio modeloMunicipio, ModeloUsuario modeloUsuario) {
        this.modeloInstitucion = modeloInstitucion;
        this.graficoConsulta = graficoConsulta;
        this.view = view;
        this.modeloMunicipio = modeloMunicipio;
        this.modeloUsuario = modeloUsuario;
        Listeners();

    }

    /**
     * Método para inicializar la vista dependiendo del tipo de modeloUsuario.
     * Se ajustan los elementos visibles y las acciones de acuerdo al tipo de modeloUsuario (Administrador, Superadmin, Invitado).
     */
    public void Iniciar() {
        switch (modeloUsuario.getTipoUsuario()) {
            case "Invitado":
                view.setTitle("Reducir");
                cargarAnioBase();
                cargarMunicipio();
                llenarComboInstitucion();
                Nucleo();
                view.comoInstitucion.setVisible(false);
                view.setVisible(true);
                view.Institucio.setText(modeloInstitucion.getNombreInstitucion());
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.RegistrarInstitucion.setVisible(false);
                view.VerPerfiles.setVisible(false);
                view.RegistrarEmision.setVisible(false);
                view.Calcular.setVisible(false);
                view.perfil.setVisible(true);
                view.verInstitucion.setVisible(false);
                view.setLocationRelativeTo(null);
                break;
            case "Administrador":
                view.setTitle("Reducir");
                cargarAnioBase();
                cargarMunicipio();
                Nucleo();
                view.setVisible(true);
                view.setLocationRelativeTo(null);
                view.Institucio.setText(modeloInstitucion.getNombreInstitucion());
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.verInstitucion.setVisible(false);
                break;
            case "Superadmin":
                view.setTitle("Reducir");
                cargarAnioBase();
                cargarMunicipio();
                //Nucleo();
                view.Institucio.setVisible(false);
                view.comoInstitucion.setVisible(true);
                view.setVisible(true);
                llenarComboInstitucion();
                view.comoInstitucion.setVisible(true);
                view.Institucio.setVisible(false);
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.setLocationRelativeTo(null);

                break;
            default:
                JOptionPane.showMessageDialog(null, "ModeloUsuario no definido en el sistema");
                break;

        }


        GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
        GraficosCompararInstitucion.addActionListener(e -> vistaCompararInstituciones());
        GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
    }

    /**
     * Maneja los eventos de acción generados por los botones y componentes de la vista.
     * Este método se ejecuta cuando un modeloUsuario interactúa con los botones y componentes de la interfaz gráfica.
     * En función del botón que se haya presionado, se ejecuta el método correspondiente para gestionar la acción.
     *
     * @param e El evento de acción que contiene información sobre el componente que generó el evento.
     */
    private void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.generarPlanDeAccion) {
            if (view.comboNucleo.isVisible()) {
                InsertarEncajatextoConNucleo();

            } else {
                InsertarEncajatexto();

            }


        }
        if (e.getSource() == view.Descargar) {
            if (view.comboNucleo.isVisible()) {
                descargarConNucleo();
            } else {
                descargarSinNucleo();

            }

        }
        if (e.getSource() == view.RegistrarInstitucion) {
            vistaActualizarInstitucion();
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
        if (e.getSource() == view.VerPerfiles) {
            vistaVerPerfiles();
        }
        if (e.getSource() == view.inicioButton) {
            BotonInicio();
        }
        if (e.getSource() == view.verInstitucion) {
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
     * Carga los años base en el comboBox de la interfaz gráfica, basado en la institución y modeloMunicipio seleccionados.
     * <p>
     * Este método consulta los años base disponibles desde el modelo de datos en función de la institución y
     * el modeloMunicipio seleccionados en la vista. Luego, limpia el comboBox de años y lo llena con los valores obtenidos.
     * Finalmente, selecciona el primer año si el comboBox tiene elementos.
     */
    public void cargarAnioBase() {
        // Obtener la lista de años base desde el Modelo
        String nombreInstitucion = String.valueOf(view.Institucio.getText());
        String nombreMunicipio = String.valueOf(view.municipio.getSelectedItem());
        String nombreIns = String.valueOf(view.comoInstitucion.getSelectedItem());
        if(view.Institucio.isVisible()){
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
        }else{
            ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
            List<String> aniosBase = consultasInstitucion.obtenerAnioBase(nombreIns, nombreMunicipio);

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
        // Consultar los años base desde el Modelo

    }
    /**
     * Navega a la vista inicial de la aplicación.
     * Pasos:
     * 1. Crea el controlador de inicio con las dependencias necesarias.
     * 2. Llama al método que inicia la vista de inicio.
     * 3. Cierra la vista actual.
     */
    private void anioBaseNucleo() {
        view.anio.removeAllItems(); // Limpiar el combo de años
        view.anio.addItem(" "); // Agregar un texto indicativo
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);

        if (modeloUsuario.getTipoUsuario().equals("Superadmin")) {
            String nombre = String.valueOf(view.comoInstitucion.getSelectedItem());
            String municipio = String.valueOf(view.municipio.getSelectedItem());
            String nucleo = String.valueOf(view.comboNucleo.getSelectedItem());
            List<String> anioBaseNucleo = consultaNucleo.obtenerAnosBase(nombre, municipio, nucleo);
            for (String anioBase : anioBaseNucleo) {
                view.anio.addItem(anioBase);
            }
        } else {
            String nombre = view.Institucio.getText();
            String municipio = String.valueOf(view.municipio.getSelectedItem());
            String nucleo = String.valueOf(view.comboNucleo.getSelectedItem());
            List<String> anioBaseNucleo = consultaNucleo.obtenerAnosBase(nombre, municipio, nucleo);
            for (String anioBase : anioBaseNucleo) {
                view.anio.addItem(anioBase);
            }

        }


    }

    /**
     * Carga los municipios disponibles en el comboBox de la interfaz gráfica,
     * basado en la institución seleccionada por el modeloUsuario.
     * <p>
     * Este método obtiene los municipios asociados a la institución seleccionada
     * desde el modelo de datos, limpia el comboBox de municipios y lo llena con los
     * valores obtenidos. Si no se ha seleccionado una institución, limpia el comboBox.
     */
    public void cargarMunicipio() {
        // Obtener la institución seleccionada desde la vista
        if(modeloUsuario.getTipoUsuario().equals("Superadmin")){
            String nombreInstitucion = String.valueOf( view.comoInstitucion.getSelectedItem());
            ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();

            // Verificar que la institución no esté vacía
            if (!nombreInstitucion.isEmpty()) {
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
            }
        }else{
            String nombreInstitucion = modeloInstitucion.getNombreInstitucion();
            ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();

            // Verificar que la institución no esté vacía
            if (!nombreInstitucion.isEmpty()) {
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
            }
        }

    }

    /**
     * Carga los núcleos existentes asociados a una institución en un componente de selección (combo box) de la interfaz gráfica.
     * Pasos:
     * 1. Limpia el contenido del combo box para evitar duplicados o valores previos.
     * 2. Crea una conexión a la base de datos utilizando la clase `Conexion`.
     * 3. Utiliza la clase `ConsultaNucleo` para obtener una lista de núcleos asociados a la institución actual.
     * 4. Agrega los nombres de los núcleos recuperados al combo box para que el modeloUsuario pueda seleccionarlos.
     * Nota: Este método depende de un modelo (`modeloInstitucion`) que contiene el nombre de la institución actual.
     */
    public void cargarNucleosExistentes() {
        // Limpiamos el combo box antes de cargar los nuevos valores
        view.comboNucleo.removeAllItems();
        view.comboNucleo.addItem(" ");
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
            if(modeloUsuario.getTipoUsuario().equals("Superadmin")){
                String nombreInstitucion = String.valueOf(  view.comoInstitucion.getSelectedItem());
                // Llamamos al modelo para cargar los núcleos
                List<String> nucleos = consultaNucleo.cargarNucleos(nombreInstitucion);

                // Agregamos los núcleos al combo box
                for (String nucleo : nucleos) {
                    view.comboNucleo.addItem(nucleo);
                }
            }else{
                String nombreInstitucion = view.Institucio.getText();
                // Llamamos al modelo para cargar los núcleos
                List<String> nucleos = consultaNucleo.cargarNucleos(nombreInstitucion);

                // Agregamos los núcleos al combo box
                for (String nucleo : nucleos) {
                    view.comboNucleo.addItem(nucleo);
                }
            }


        // Manejo de errores si ocurre algún problema en la consulta

    }


    /**
     * Este método permite al modeloUsuario descargar un archivo PDF con el contenido del área de texto `introduccionLaHuellaDeTextArea`.
     * Dependiendo del tipo de modeloUsuario (Superadmin o no), se personaliza el nombre del archivo PDF, agregando la institución y la fecha actual.
     * El modeloUsuario puede seleccionar la ubicación donde guardar el archivo y, si el archivo ya existe, se garantiza que se le dará un nombre único.
     */
    private void descargarSinNucleo() {
        // Obtener la fecha actual en formato dd-MM-yyyy
        String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        // Verificar si el modeloUsuario es "Superadmin"
        if (modeloUsuario.getTipoUsuario().equals("Superadmin")) {
            // Si es "Superadmin", obtener el nombre de la institución desde el combo box y concatenarlo con la fecha
            String b = view.comoInstitucion.getSelectedItem() + fecha;

            // Crear un JFileChooser para permitir al modeloUsuario seleccionar el lugar donde guardar el archivo
            JFileChooser f = new JFileChooser();

            // Establecer un filtro para que solo se muestren archivos PDF
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);

            // Establecer el título del diálogo de guardar archivo
            f.setDialogTitle("Guardar archivo PDF");

            // Establecer el nombre predeterminado del archivo con la institución y la fecha
            f.setSelectedFile(new File(b + ".pdf"));

            // Mostrar el diálogo para que el modeloUsuario seleccione dónde guardar el archivo
            int userSelection = f.showSaveDialog(null);

            // Si el modeloUsuario seleccionó una ubicación para guardar
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Obtener el archivo seleccionado por el modeloUsuario
                File c = f.getSelectedFile();

                // Asegurarse de que el archivo tenga un nombre único (evitar sobrescribir archivos existentes)
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());

                // Exportar el contenido del área de texto a un archivo PDF
                ExportarPdf(view.introduccionLaHuellaDeTextArea.getText(), file);
            }
        } else {
            // Si el modeloUsuario no es "Superadmin", obtener el nombre de la institución desde el campo de texto
            String b = view.Institucio.getText() + fecha;

            // Crear un JFileChooser para permitir al modeloUsuario seleccionar el lugar donde guardar el archivo
            JFileChooser f = new JFileChooser();

            // Establecer un filtro para que solo se muestren archivos PDF
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);

            // Establecer el título del diálogo de guardar archivo
            f.setDialogTitle("Guardar archivo PDF");

            // Establecer el nombre predeterminado del archivo con el nombre de la institución y la fecha
            f.setSelectedFile(new File(b + ".pdf"));

            // Mostrar el diálogo para que el modeloUsuario seleccione dónde guardar el archivo
            int userSelection = f.showSaveDialog(null);

            // Si el modeloUsuario seleccionó una ubicación para guardar
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Obtener el archivo seleccionado por el modeloUsuario
                File c = f.getSelectedFile();

                // Asegurarse de que el archivo tenga un nombre único (evitar sobrescribir archivos existentes)
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());

                // Exportar el contenido del área de texto a un archivo PDF
                ExportarPdf(view.introduccionLaHuellaDeTextArea.getText(), file);
            }
        }
    }


    /**
     * Este método permite descargar un archivo PDF con el contenido del área de texto `introduccionLaHuellaDeTextArea`,
     * pero en este caso incluye información adicional sobre un "núcleo" seleccionado por el modeloUsuario.
     * Dependiendo del tipo de modeloUsuario (Superadmin o no), se personaliza el nombre del archivo PDF con el nombre del núcleo
     * y la fecha actual.
     */
    private void descargarConNucleo() {
        // Obtener la fecha actual en formato dd-MM-yyyy
        String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        // Obtener el nombre del núcleo seleccionado por el modeloUsuario desde el combo box
        String nucleo = Objects.requireNonNull(view.comboNucleo.getSelectedItem()).toString();

        // Verificar si el modeloUsuario es "Superadmin"
        if (modeloUsuario.getTipoUsuario().equals("Superadmin")) {
            // Si es "Superadmin", obtener el nombre de la institución desde el combo box y concatenar la fecha, el nombre del núcleo y otros datos
            String b = String.valueOf(view.comoInstitucion.getSelectedItem()) + fecha + "DelNucleo" + nucleo;

            // Crear un JFileChooser para permitir al modeloUsuario seleccionar el lugar donde guardar el archivo
            JFileChooser f = new JFileChooser();

            // Establecer un filtro para que solo se muestren archivos PDF
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);

            // Establecer el título del diálogo de guardar archivo
            f.setDialogTitle("Guardar archivo PDF");

            // Establecer el nombre predeterminado del archivo con la institución, la fecha y el núcleo
            f.setSelectedFile(new File(b + ".pdf"));

            // Mostrar el diálogo para que el modeloUsuario seleccione dónde guardar el archivo
            int userSelection = f.showSaveDialog(null);

            // Si el modeloUsuario seleccionó una ubicación para guardar
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Obtener el archivo seleccionado por el modeloUsuario
                File c = f.getSelectedFile();

                // Asegurarse de que el archivo tenga un nombre único (evitar sobrescribir archivos existentes)
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());

                // Exportar el contenido del área de texto a un archivo PDF, incluyendo información sobre el núcleo
                ExportarPdfConNucleo(view.introduccionLaHuellaDeTextArea.getText(), file);
            }
        } else {
            // Si el modeloUsuario no es "Superadmin", obtener el nombre de la institución desde el campo de texto
            String b = view.Institucio.getText() + fecha + "DelNucleo" + nucleo;

            // Crear un JFileChooser para permitir al modeloUsuario seleccionar el lugar donde guardar el archivo
            JFileChooser f = new JFileChooser();

            // Establecer un filtro para que solo se muestren archivos PDF
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);

            // Establecer el título del diálogo de guardar archivo
            f.setDialogTitle("Guardar archivo PDF");

            // Establecer el nombre predeterminado del archivo con el nombre de la institución, la fecha y el núcleo
            f.setSelectedFile(new File(b + ".pdf"));

            // Mostrar el diálogo para que el modeloUsuario seleccione dónde guardar el archivo
            int userSelection = f.showSaveDialog(null);

            // Si el modeloUsuario seleccionó una ubicación para guardar
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Obtener el archivo seleccionado por el modeloUsuario
                File c = f.getSelectedFile();

                // Asegurarse de que el archivo tenga un nombre único (evitar sobrescribir archivos existentes)
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());

                // Exportar el contenido del área de texto a un archivo PDF, incluyendo información sobre el núcleo
                ExportarPdfConNucleo(view.introduccionLaHuellaDeTextArea.getText(), file);
            }
        }
    }


    /**
     * Este método es responsable de generar un informe de huella de carbono basado en los datos seleccionados por el modeloUsuario
     * (institución, año y modeloMunicipio) y las fuentes de emisión relacionadas. El informe se construye dinámicamente y se
     * muestra en un área de texto en la vista.
     */
    public void InsertarEncajatexto() {
        if(view.Institucio.isVisible()){
            if (!Objects.requireNonNull(view.anio.getSelectedItem()).toString().isEmpty() &&
                    !Objects.requireNonNull(view.municipio.getSelectedItem()).toString().isEmpty()) {
                // Obtiene los datos necesarios de la vista
                String nombreInstitucion = view.Institucio.getText();  // Nombre de la institución
                String anioBaseString = String.valueOf(view.anio.getSelectedItem());  // Año base seleccionado
                int anioBase = Integer.parseInt(anioBaseString);  // Convierte el año base a entero
                String NombreMuncipio = view.municipio.getSelectedItem().toString();  // ModeloMunicipio seleccionado

                // Consulta los datos de las emisiones para el gráfico por alcance y por fuente
                List<GraficorModeloInstitucion> datos = graficoConsulta.GraficoPorAlcance(nombreInstitucion, anioBase, NombreMuncipio);
                List<ModeloEmisionCalcular> datos2 = graficoConsulta.GraficoPorFuente(nombreInstitucion, anioBase, NombreMuncipio);

                // Construcción del párrafo introductorio sobre la huella de carbono
                String parrafo1 = "Introducción " + "La huella de carbono de una institución universitaria incluye las emisiones de gases de \n" +
                        "efecto invernadero (GEI) generadas por sus actividades operativas. Este informe desglosa la huella de carbono de una universidad en Colombia, " +
                        "clasificada por alcance, y propone una estrategia para mitigar estas emisiones mediante la plantación de árboles." + "\n" + "\n" +
                        "Para calcular la huella de carbono, se consideran las siguientes fuentes de emisiones:\n" + "\n" +
                        "Alcance 1: Emisiones Directas\n" + "Emisiones de vehículos universitarios.\n" +
                        "Emisiones de equipos de combustión en el campus.\n" + "\n" +
                        "Alcance 2: Emisiones Indirectas por Consumo de Energía\n" + "Emisiones de electricidad comprada y consumida en el campus.\n" + "\n" +
                        "Alcance 3: Otras Emisiones Indirectas\n" + "Emisiones de viajes de estudiantes y empleados.\n" +
                        "Emisiones de la gestión de residuos.\n" + "Emisiones de proveedores y cadena de suministro.\n" + "\n";

                // Inicializa los arreglos para almacenar los datos de alcance y los totales
                String[] alcance = new String[datos.size()];
                Double[] total = new Double[datos.size()];
                StringBuilder textoAcumulado = new StringBuilder();  // Acumula el texto de los alcances
                Double Sumar = 0.0;  // Acumulador de la suma total de emisiones

                // Procesa los datos de las emisiones por alcance
                for (int i = 0; i < datos.size(); i++) {
                    GraficorModeloInstitucion a = datos.get(i);
                    alcance[i] = a.Alcance;
                    total[i] = a.Total;
                    Sumar = Sumar + total[i];  // Suma las emisiones totales
                    textoAcumulado.append(alcance[i]).append(" ").append(total[i]).append("\n").append("\n");
                }

                // Calcula la cantidad de árboles a plantar para mitigar la huella de carbono
                double arboles = Sumar / 22;  // 22 kg de CO2 es lo que un árbol puede absorber al año
                String text = "Suma Total: " + Sumar + " kg de CO2 por año\n" + "\n" +
                        "Plantación de Árboles para Mitigar la Huella de Carbono\n" +
                        "Dado que un árbol promedio puede absorber aproximadamente 22 kg de CO2 al año, " +
                        "se puede calcular la cantidad de árboles necesarios para neutralizar las emisiones.\n" +
                        "Cálculo de la cantidad de árboles a plantar." + "\n" +
                        "Número de árboles = Emisiones Totales (kg de CO2) / Absorción de CO2 por árbol (kg)" + "\n" +
                        "Número de árboles recomendados a plantar: " + arboles + "\n";

                // Inicializa los arreglos para los datos de las fuentes de emisión
                String[] nombreFuente = new String[datos2.size()];
                Double[] totalFuente = new Double[datos2.size()];
                StringBuilder textoAcumulado2 = new StringBuilder();  // Acumula el texto de las fuentes

                // Procesa los datos de las emisiones por fuente
                for (int i = 0; i < datos2.size(); i++) {
                    ModeloEmisionCalcular dato = datos2.get(i);
                    nombreFuente[i] = dato.getNombreFuente();
                    totalFuente[i] = dato.getTotal1();
                    textoAcumulado2.append(nombreFuente[i]).append(" ").append(totalFuente[i]).append("\n").append("\n");
                }

                // Texto adicional sobre las fuentes de emisión
                String Fuentes = "\nA continuación verás la cantidad de CO2 emitido por cada una de las fuentes registradas en la institución\n" + "\n";

                // Variable para las conclusiones (aún no utilizada en el texto final)
                String Concluciones = "";

                // Establece el texto completo en el área de texto de la vista
                view.introduccionLaHuellaDeTextArea.setText(parrafo1);
                view.introduccionLaHuellaDeTextArea.append(textoAcumulado.toString());  // Agrega los alcances y totales
                view.introduccionLaHuellaDeTextArea.append(Fuentes);  // Agrega las fuentes de emisión
                view.introduccionLaHuellaDeTextArea.append(textoAcumulado2.toString());  // Agrega los detalles de las fuentes
                view.introduccionLaHuellaDeTextArea.append(text);  // Agrega el cálculo de los árboles a plantar
                view.introduccionLaHuellaDeTextArea.append(Concluciones);  // Agrega las conclusiones (si hay)

                // Configura el área de texto para que sea visible, no editable y con formato adecuado
                view.introduccionLaHuellaDeTextArea.setVisible(true);
                view.introduccionLaHuellaDeTextArea.setLineWrap(true);  // Ajuste de línea
                view.introduccionLaHuellaDeTextArea.setWrapStyleWord(true);  // Ajuste de palabra
                view.introduccionLaHuellaDeTextArea.setEditable(false);  // No editable por el modeloUsuario

                // Establece la fuente del área de texto
                Font font = new Font("Arial", Font.PLAIN, 14);
                view.introduccionLaHuellaDeTextArea.setFont(font);

                // Hace visible el contenedor que contiene el área de texto
                view.Contenedor.setVisible(true);

            } else {
                // Muestra un mensaje de advertencia si los campos obligatorios no están llenos
                JOptionPane.showMessageDialog(view.PanelMain, "Llene todos los campos");
            }
        }else{
            if (!Objects.requireNonNull(view.anio.getSelectedItem()).toString().isEmpty() &&
                    !Objects.requireNonNull(view.municipio.getSelectedItem()).toString().isEmpty()) {
                // Obtiene los datos necesarios de la vista
                String nombreInstitucion = String.valueOf(view.comoInstitucion.getSelectedItem());  // Nombre de la institución
                String anioBaseString = String.valueOf(view.anio.getSelectedItem());  // Año base seleccionado
                int anioBase = Integer.parseInt(anioBaseString);  // Convierte el año base a entero
                String NombreMuncipio = view.municipio.getSelectedItem().toString();  // ModeloMunicipio seleccionado

                // Consulta los datos de las emisiones para el gráfico por alcance y por fuente
                List<GraficorModeloInstitucion> datos = graficoConsulta.GraficoPorAlcance(nombreInstitucion, anioBase, NombreMuncipio);
                List<ModeloEmisionCalcular> datos2 = graficoConsulta.GraficoPorFuente(nombreInstitucion, anioBase, NombreMuncipio);

                // Construcción del párrafo introductorio sobre la huella de carbono
                String parrafo1 = "Introducción " + "La huella de carbono de una institución universitaria incluye las emisiones de gases de \n" +
                        "efecto invernadero (GEI) generadas por sus actividades operativas. Este informe desglosa la huella de carbono de una universidad en Colombia, " +
                        "clasificada por alcance, y propone una estrategia para mitigar estas emisiones mediante la plantación de árboles." + "\n" + "\n" +
                        "Para calcular la huella de carbono, se consideran las siguientes fuentes de emisiones:\n" + "\n" +
                        "Alcance 1: Emisiones Directas\n" + "Emisiones de vehículos universitarios.\n" +
                        "Emisiones de equipos de combustión en el campus.\n" + "\n" +
                        "Alcance 2: Emisiones Indirectas por Consumo de Energía\n" + "Emisiones de electricidad comprada y consumida en el campus.\n" + "\n" +
                        "Alcance 3: Otras Emisiones Indirectas\n" + "Emisiones de viajes de estudiantes y empleados.\n" +
                        "Emisiones de la gestión de residuos.\n" + "Emisiones de proveedores y cadena de suministro.\n" + "\n";

                // Inicializa los arreglos para almacenar los datos de alcance y los totales
                String[] alcance = new String[datos.size()];
                Double[] total = new Double[datos.size()];
                StringBuilder textoAcumulado = new StringBuilder();  // Acumula el texto de los alcances
                Double Sumar = 0.0;  // Acumulador de la suma total de emisiones

                // Procesa los datos de las emisiones por alcance
                for (int i = 0; i < datos.size(); i++) {
                    GraficorModeloInstitucion a = datos.get(i);
                    alcance[i] = a.Alcance;
                    total[i] = a.Total;
                    Sumar = Sumar + total[i];  // Suma las emisiones totales
                    textoAcumulado.append(alcance[i]).append(" ").append(total[i]).append("\n").append("\n");
                }

                // Calcula la cantidad de árboles a plantar para mitigar la huella de carbono
                double arboles = Sumar / 22;  // 22 kg de CO2 es lo que un árbol puede absorber al año
                String text = "Suma Total: " + Sumar + " kg de CO2 por año\n" + "\n" +
                        "Plantación de Árboles para Mitigar la Huella de Carbono\n" +
                        "Dado que un árbol promedio puede absorber aproximadamente 22 kg de CO2 al año, " +
                        "se puede calcular la cantidad de árboles necesarios para neutralizar las emisiones.\n" +
                        "Cálculo de la cantidad de árboles a plantar." + "\n" +
                        "Número de árboles = Emisiones Totales (kg de CO2) / Absorción de CO2 por árbol (kg)" + "\n" +
                        "Número de árboles recomendados a plantar: " + arboles + "\n";

                // Inicializa los arreglos para los datos de las fuentes de emisión
                String[] nombreFuente = new String[datos2.size()];
                Double[] totalFuente = new Double[datos2.size()];
                StringBuilder textoAcumulado2 = new StringBuilder();  // Acumula el texto de las fuentes

                // Procesa los datos de las emisiones por fuente
                for (int i = 0; i < datos2.size(); i++) {
                    ModeloEmisionCalcular dato = datos2.get(i);
                    nombreFuente[i] = dato.getNombreFuente();
                    totalFuente[i] = dato.getTotal1();
                    textoAcumulado2.append(nombreFuente[i]).append(" ").append(totalFuente[i]).append("\n").append("\n");
                }

                // Texto adicional sobre las fuentes de emisión
                String Fuentes = "\nA continuación verás la cantidad de CO2 emitido por cada una de las fuentes registradas en la institución\n" + "\n";

                // Variable para las conclusiones (aún no utilizada en el texto final)
                String Concluciones = "";

                // Establece el texto completo en el área de texto de la vista
                view.introduccionLaHuellaDeTextArea.setText(parrafo1);
                view.introduccionLaHuellaDeTextArea.append(textoAcumulado.toString());  // Agrega los alcances y totales
                view.introduccionLaHuellaDeTextArea.append(Fuentes);  // Agrega las fuentes de emisión
                view.introduccionLaHuellaDeTextArea.append(textoAcumulado2.toString());  // Agrega los detalles de las fuentes
                view.introduccionLaHuellaDeTextArea.append(text);  // Agrega el cálculo de los árboles a plantar
                view.introduccionLaHuellaDeTextArea.append(Concluciones);  // Agrega las conclusiones (si hay)

                // Configura el área de texto para que sea visible, no editable y con formato adecuado
                view.introduccionLaHuellaDeTextArea.setVisible(true);
                view.introduccionLaHuellaDeTextArea.setLineWrap(true);  // Ajuste de línea
                view.introduccionLaHuellaDeTextArea.setWrapStyleWord(true);  // Ajuste de palabra
                view.introduccionLaHuellaDeTextArea.setEditable(false);  // No editable por el modeloUsuario

                // Establece la fuente del área de texto
                Font font = new Font("Arial", Font.PLAIN, 14);
                view.introduccionLaHuellaDeTextArea.setFont(font);

                // Hace visible el contenedor que contiene el área de texto
                view.Contenedor.setVisible(true);

            } else {
                // Muestra un mensaje de advertencia si los campos obligatorios no están llenos
                JOptionPane.showMessageDialog(view.PanelMain, "Llene todos los campos");
            }
        }
        // Verifica si los campos de institución y modeloMunicipio no están vacíos

    }

    /**
     * Este método es responsable de generar un informe de huella de carbono basado en los datos seleccionados por el modeloUsuario
     * (institución, año, modeloMunicipio y/o campus) y las fuentes de emisión relacionadas. El informe se construye dinámicamente y se
     * muestra en un área de texto en la vista.
     */
    public void InsertarEncajatextoConNucleo() {
        if(view.Institucio.isVisible()){
            if (!Objects.requireNonNull(view.anio.getSelectedItem()).toString().isEmpty() && !Objects.requireNonNull(view.municipio.getSelectedItem()).toString().isEmpty()) {
                String nombreInstitucion = view.Institucio.getText();
                String anioBaseString = String.valueOf(view.anio.getSelectedItem());
                String nombreN = view.comboNucleo.getSelectedItem().toString();
                int anioBase = Integer.parseInt(anioBaseString);
                String NombreMuncipio = view.municipio.getSelectedItem().toString();
                List<GraficorModeloInstitucion> datos = graficoConsulta.GraficoPorAlcanceNucleo(nombreInstitucion, anioBase, NombreMuncipio, nombreN);
                List<ModeloEmisionCalcular> datos2 = graficoConsulta.GraficoPorFuenteNucleo(nombreInstitucion, anioBase, NombreMuncipio, nombreN);

                String parrafo1 = "Introducción\n" + "La huella de carbono de una institución universitaria incluye las emisiones de gases de \n" + "efecto invernadero (GEI) generadas por sus actividades operativas. Este informe desglosa la huella de carbono de una universidad en Colombia, clasificada por alcance, y propone una estrategia para mitigar estas emisiones mediante la plantación de árboles." + "\n" + "\nPara calcular la huella de carbono, se consideran las siguientes fuentes de emisiones:\n" + "\n" + "Alcance 1: Emisiones Directas\n" + "Emisiones de vehículos universitarios.\n" + "Emisiones de equipos de combustión en el campus.\n" + "\n" + "Alcance 2: Emisiones Indirectas por Consumo de Energía\n" + "Emisiones de electricidad comprada y consumida en el campus.\n" + "\n" + "Alcance 3: Otras Emisiones Indirectas\n" + "Emisiones de viajes de estudiantes y empleados.\n" + "Emisiones de la gestión de residuos.\n" + "Emisiones de proveedores y cadena de suministro.\n" + "\n";
                String[] alcance = new String[datos.size()];
                Double[] total = new Double[datos.size()];
                StringBuilder textoAcumulado = new StringBuilder();
                Double Sumar = 0.0;
                for (int i = 0; i < datos.size(); i++) {
                    GraficorModeloInstitucion a = datos.get(i);
                    alcance[i] = a.Alcance;
                    total[i] = a.Total;
                    Sumar = Sumar + total[i];
                    textoAcumulado.append(alcance[i]).append(" ").append(total[i]).append("\n").append("\n");

                }
                Double arboles = Sumar / 22;
                String text = "Suma Total: " + Sumar + " kg de CO2 por año\n" + "\n" + "Plantación de Árboles para Mitigar la Huella de Carbono\n" + "Dado que un árbol promedio puede absorber aproximadamente 22 kg de CO2 al año, se puede calcular la cantidad de árboles necesarios para neutralizar las emisiones.\n" + "Cálculo de la cantidad de árboles a plantar." + "\n" + "Número de árboles = Emisiones Totales (kg de CO2) / Absorción de CO2 por árbol (kg)" + "\n" + "Número de árboles recomendados a plantar: " + arboles + "\n";
                String[] nombreFuente = new String[datos2.size()];
                Double[] totalFuente = new Double[datos2.size()];
                StringBuilder textoAcumulado2 = new StringBuilder();

                for (int i = 0; i < datos2.size(); i++) {
                    ModeloEmisionCalcular dato = datos2.get(i);
                    nombreFuente[i] = dato.getNombreFuente();
                    totalFuente[i] = dato.getTotal1();
                    textoAcumulado2.append(nombreFuente[i]).append(" ").append(totalFuente[i]).append("\n").append("\n");
                }
                String Fuentes = "\nAcontinuación veras la cantidad e co2 emitad por cada una de las fuentes que han sido registadas en la institucion\n" + "\n";
                String Concluciones = "";

                view.introduccionLaHuellaDeTextArea.setText(parrafo1);
                view.introduccionLaHuellaDeTextArea.append(textoAcumulado.toString());
                view.introduccionLaHuellaDeTextArea.append(Fuentes);
                view.introduccionLaHuellaDeTextArea.append(textoAcumulado2.toString());
                view.introduccionLaHuellaDeTextArea.append(text);
                view.introduccionLaHuellaDeTextArea.append(Concluciones);
                view.introduccionLaHuellaDeTextArea.setVisible(true);
                view.introduccionLaHuellaDeTextArea.setLineWrap(true);
                view.introduccionLaHuellaDeTextArea.setWrapStyleWord(true);
                view.introduccionLaHuellaDeTextArea.setEditable(false);
                Font font = new Font("Arial", Font.PLAIN, 14);
                view.introduccionLaHuellaDeTextArea.setFont(font);
                view.Contenedor.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(view.PanelMain, "llene todos los campos");
            }
        }else{
            if (!Objects.requireNonNull(view.anio.getSelectedItem()).toString().isEmpty() && !Objects.requireNonNull(view.municipio.getSelectedItem()).toString().isEmpty()) {
                String nombreInstitucion = String.valueOf(view.comoInstitucion.getSelectedItem());
                String anioBaseString = String.valueOf(view.anio.getSelectedItem());
                String nombreN = view.comboNucleo.getSelectedItem().toString();
                int anioBase = Integer.parseInt(anioBaseString);
                String NombreMuncipio = view.municipio.getSelectedItem().toString();
                List<GraficorModeloInstitucion> datos = graficoConsulta.GraficoPorAlcanceNucleo(nombreInstitucion, anioBase, NombreMuncipio, nombreN);
                List<ModeloEmisionCalcular> datos2 = graficoConsulta.GraficoPorFuenteNucleo(nombreInstitucion, anioBase, NombreMuncipio, nombreN);

                String parrafo1 = "Introducción\n" + "La huella de carbono de una institución universitaria incluye las emisiones de gases de \n" + "efecto invernadero (GEI) generadas por sus actividades operativas. Este informe desglosa la huella de carbono de una universidad en Colombia, clasificada por alcance, y propone una estrategia para mitigar estas emisiones mediante la plantación de árboles." + "\n" + "\nPara calcular la huella de carbono, se consideran las siguientes fuentes de emisiones:\n" + "\n" + "Alcance 1: Emisiones Directas\n" + "Emisiones de vehículos universitarios.\n" + "Emisiones de equipos de combustión en el campus.\n" + "\n" + "Alcance 2: Emisiones Indirectas por Consumo de Energía\n" + "Emisiones de electricidad comprada y consumida en el campus.\n" + "\n" + "Alcance 3: Otras Emisiones Indirectas\n" + "Emisiones de viajes de estudiantes y empleados.\n" + "Emisiones de la gestión de residuos.\n" + "Emisiones de proveedores y cadena de suministro.\n" + "\n";
                String[] alcance = new String[datos.size()];
                Double[] total = new Double[datos.size()];
                StringBuilder textoAcumulado = new StringBuilder();
                Double Sumar = 0.0;
                for (int i = 0; i < datos.size(); i++) {
                    GraficorModeloInstitucion a = datos.get(i);
                    alcance[i] = a.Alcance;
                    total[i] = a.Total;
                    Sumar = Sumar + total[i];
                    textoAcumulado.append(alcance[i]).append(" ").append(total[i]).append("\n").append("\n");

                }
                Double arboles = Sumar / 22;
                String text = "Suma Total: " + Sumar + " kg de CO2 por año\n" + "\n" + "Plantación de Árboles para Mitigar la Huella de Carbono\n" + "Dado que un árbol promedio puede absorber aproximadamente 22 kg de CO2 al año, se puede calcular la cantidad de árboles necesarios para neutralizar las emisiones.\n" + "Cálculo de la cantidad de árboles a plantar." + "\n" + "Número de árboles = Emisiones Totales (kg de CO2) / Absorción de CO2 por árbol (kg)" + "\n" + "Número de árboles recomendados a plantar: " + arboles + "\n";
                String[] nombreFuente = new String[datos2.size()];
                Double[] totalFuente = new Double[datos2.size()];
                StringBuilder textoAcumulado2 = new StringBuilder();

                for (int i = 0; i < datos2.size(); i++) {
                    ModeloEmisionCalcular dato = datos2.get(i);
                    nombreFuente[i] = dato.getNombreFuente();
                    totalFuente[i] = dato.getTotal1();
                    textoAcumulado2.append(nombreFuente[i]).append(" ").append(totalFuente[i]).append("\n").append("\n");
                }
                String Fuentes = "\nAcontinuación veras la cantidad e co2 emitad por cada una de las fuentes que han sido registadas en la institucion\n" + "\n";
                String Concluciones = "";

                view.introduccionLaHuellaDeTextArea.setText(parrafo1);
                view.introduccionLaHuellaDeTextArea.append(textoAcumulado.toString());
                view.introduccionLaHuellaDeTextArea.append(Fuentes);
                view.introduccionLaHuellaDeTextArea.append(textoAcumulado2.toString());
                view.introduccionLaHuellaDeTextArea.append(text);
                view.introduccionLaHuellaDeTextArea.append(Concluciones);
                view.introduccionLaHuellaDeTextArea.setVisible(true);
                view.introduccionLaHuellaDeTextArea.setLineWrap(true);
                view.introduccionLaHuellaDeTextArea.setWrapStyleWord(true);
                view.introduccionLaHuellaDeTextArea.setEditable(false);
                Font font = new Font("Arial", Font.PLAIN, 14);
                view.introduccionLaHuellaDeTextArea.setFont(font);
                view.Contenedor.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(view.PanelMain, "llene todos los campos");
            }
        }


    }

    /**
     * Este método permite exportar un contenido a un archivo PDF.
     * Se agrega la fecha actual, el nombre de la institución, el modeloMunicipio y el contenido proporcionado al documento PDF.
     * Al finalizar, se guarda el archivo en la ubicación especificada.
     *
     * @param content El contenido que se desea agregar al PDF.
     * @param r El archivo de salida donde se guardará el PDF.
     */
    public void ExportarPdf(String content, File r) {
        try {
            // Crear un nuevo documento PDF
            Document document = new Document();

            // Especificar la ruta y nombre del archivo de salida para el PDF
            PdfWriter.getInstance(document, new FileOutputStream(r));

            // Abrir el documento para agregarle contenido
            document.open();

            // Obtener la fecha actual en formato dd-MM-yyyy
            String fechaActual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            // Añadir la fecha de creación al documento alineada a la derecha
            Paragraph fecha = new Paragraph("Fecha de creación: " + fechaActual);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            document.add(fecha);  // Agregar la fecha al documento
            document.add(new Paragraph(" "));  // Añadir un espacio vacío para separar

            // Condicional para verificar el tipo de modeloUsuario y agregar el nombre de la institución
            if(modeloUsuario.getTipoUsuario().equals("Superadmin")){
                // Si el modeloUsuario es "Superadmin", obtener el nombre de la institución desde un combo box
                String nombre = String.valueOf(view.comoInstitucion.getSelectedItem());
                Paragraph nombreInstitucion = new Paragraph("Nombre de la Institucion: " + nombre);
                nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
                document.add(new Paragraph(""));  // Agregar un espacio vacío
                document.add(nombreInstitucion);  // Agregar el nombre de la institución
                document.add(new Paragraph("  "));  // Añadir un espacio vacío
            } else {
                // Si el modeloUsuario no es "Superadmin", obtener el nombre de la institución desde un campo de texto
                String nombre = view.Institucio.getText();
                Paragraph nombreInstitucion = new Paragraph("Nombre de la Institucion: " + nombre);
                nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
                document.add(new Paragraph(""));  // Agregar un espacio vacío
                document.add(nombreInstitucion);  // Agregar el nombre de la institución
                document.add(new Paragraph("  "));  // Añadir un espacio vacío
            }

            // Obtener el nombre del modeloMunicipio seleccionado desde un combo box
            String m = view.municipio.getSelectedItem().toString();
            Paragraph municipio = new Paragraph("Municipio: " + m);
            municipio.setAlignment(Element.ALIGN_LEFT);
            document.add(municipio);  // Agregar el modeloMunicipio al documento
            document.add(new Paragraph("  "));  // Añadir un espacio vacío

            // Agregar el contenido principal al documento
            document.add(new Paragraph(content));

            // Cerrar el documento para finalizar la creación del PDF
            document.close();

            // Mostrar un mensaje de éxito indicando que el PDF se descargó correctamente
            JOptionPane.showMessageDialog(null, "PDF se descargó correctamente");
        } catch (Exception e) {
            // En caso de error, imprimir el mensaje de excepción en consola
            System.out.println(e.getMessage());
        }
    }

    /**
     * Este método permite exportar un contenido a un archivo PDF.
     * Se agrega la fecha actual, el nombre de la institución, el modeloMunicipio y el contenido proporcionado al documento PDF.
     * Al finalizar, se guarda el archivo en la ubicación especificada.
     *
     * @param content El contenido que se desea agregar al PDF.
     * @param r El archivo de salida donde se guardará el PDF.
     */
    public void ExportarPdfConNucleo(String content, File r) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(r));
            document.open();
            String fechaActual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            // Añadir la fecha al documento
            Paragraph fecha = new Paragraph("Fecha de creación: " + fechaActual);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            document.add(fecha);
            document.add(new Paragraph(" "));
            if(modeloUsuario.getTipoUsuario().equals("Superadmin")){
                String nombre = String.valueOf(view.comoInstitucion.getSelectedItem());
                Paragraph nombreInstitucion = new Paragraph("Nombre de la Institucion: " + nombre);
                nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
                document.add(new Paragraph(("")));
                document.add(nombreInstitucion);
                document.add(new Paragraph("  "));
            }else{
                String nombre = view.Institucio.getText();
                Paragraph nombreInstitucion = new Paragraph("Nombre de la Institucion: " + nombre);
                nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
                document.add(new Paragraph(("")));
                document.add(nombreInstitucion);
                document.add(new Paragraph("  "));
            }
            String m = view.municipio.getSelectedItem().toString();
            Paragraph municipio = new Paragraph("Municipio: " + m);
            municipio.setAlignment(Element.ALIGN_LEFT);
            document.add(municipio);
            document.add(new Paragraph("  "));
            document.add(new Paragraph(content));

            String nucleo = view.comboNucleo.getSelectedItem().toString();
            Paragraph nombreN = new Paragraph("Municipio: " + nucleo);
            nombreN.setAlignment(Element.ALIGN_LEFT);
            document.add(nombreN);
            document.add(new Paragraph("  "));
            document.add(new Paragraph(content));

            // Close the document
            document.close();
            JOptionPane.showMessageDialog(null, "PDF se descargó correctamente");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    /**
     * Inicia la vista para visualizar instituciones.
     * Pasos:
     * 1. Crea las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica.
     * 3. Opcionalmente, cierra la vista actual (comentado en este caso).
     */
    public void vistaActualizarInstitucion() {
        Conexion con = new Conexion();
        ConsultasInstitucion consul = new ConsultasInstitucion();
        VerDatosInstitucion view2 = new VerDatosInstitucion();
        ModeloInstitucion mod = new ModeloInstitucion();
        ControladorInstitucion control = new ControladorInstitucion(modeloInstitucion, modeloMunicipio, view2, consul, mod, modeloUsuario);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();


    }
    /**
     * Inicia la vista del perfil del modeloUsuario.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado con los datos del modeloUsuario.
     * 3. Muestra la vista del perfil y cierra la vista actual.
     */
    public void vistaPerfil() {
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
    public void vistaCalcular() {
        Conexion con = new Conexion();
        Vistas.Calcular view2 = new Calcular();
        ModeloEmisionCalcular mod = new ModeloEmisionCalcular();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorCalcular controlador = new ControladorCalcular(mod, consul, view2, modeloInstitucion, consultaUsuario, modeloUsuario, modeloMunicipio);
        controlador.iniciar();
        view2.setVisible(true);
        view.dispose();
    }
    /**
     * Inicia la vista para registrar emisiones.
     * Pasos:
     * 1. Configura la vista, modelo y consultas necesarios para registrar emisiones.
     * 2. Inicializa el controlador asociado.
     * 3. Muestra la vista de registro de emisiones y cierra la vista actual.
     */
    public void vistaRegistrarEmision() {
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
    public void vistaInforme() {
        Conexion con = new Conexion();
        ModeloInstitucion mod2 = new ModeloInstitucion();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloEmisionInforme mod = new ModeloEmisionInforme();
        Informe view2 = new Informe();
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorInforme contro = new ControladorInforme(view2, mod, consul, modeloMunicipio, modeloInstitucion, modeloUsuario);
        contro.iniciar();
        view2.setVisible(true);
        view.dispose();
    }
    /**
     * Inicia la vista para mostrar gráficos principales.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para gestionar los gráficos.
     * 3. Muestra la vista de gráficos y cierra la vista actual.
     */
    public void vistaGraficoPrincipal() {
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos view2 = new Graficos();
        GraficorModeloInstitucion mod = new GraficorModeloInstitucion();
        ModeloInstitucion modelo = new ModeloInstitucion();
        ControladorGrafico contro = new ControladorGrafico(mod, consul, view2, modelo, modeloMunicipio, modeloUsuario, modeloInstitucion);
        contro.iniciar();
        view2.Graficos.setVisible(true);
        view.dispose();
    }

    /**
     * Inicia la vista para comparar instituciones mediante gráficos.
     * Pasos:
     * 1. Configura la vista, modelo y consultas necesarios.
     * 2. Inicializa el controlador para manejar la comparación de instituciones.
     * 3. Cierra la vista actual.
     */
    public void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar view2 = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        ModeloGraficoComparar mod = new ModeloGraficoComparar();
        ControladorCompararInstitucion contro = new ControladorCompararInstitucion(mod, consultas, comIns, view2, modeloInstitucion, modeloMunicipio, modeloUsuario);
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
    public void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        ModeloTendencia mod = new ModeloTendencia();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia view2 = new GraficoTendencia();
        ControladorTendencia control = new ControladorTendencia(mod, consult, view2, modeloMunicipio, modeloInstitucion, modeloUsuario);
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
    public void vistaVerPerfiles() {
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);

        ControladorVerPerfiles verControl = new ControladorVerPerfiles(modeloUsuario, modeloInstitucion, modeloMunicipio, verPerfiles, consul);
        verControl.Iniciar();
        view.dispose();
    }
    /**
     * Navega a la vista inicial de la aplicación.
     * Pasos:
     * 1. Crea el controlador de inicio con las dependencias necesarias.
     * 2. Llama al método que inicia la vista de inicio.
     * 3. Cierra la vista actual.
     */
    public void BotonInicio() {
        ControladorPestaniaPrincipal control = new ControladorPestaniaPrincipal(modeloInstitucion, modeloUsuario, modeloMunicipio);
        control.inicio();
        view.dispose();
    }

    private void Nucleo() {
        if (!modeloInstitucion.getNombreInstitucion().isEmpty()) {
            cargarMunicipio(); // Carga municipios basados en la institución
            cargarAnioBase(); // Carga años base basados en la institución y modeloMunicipio
        }
        Conexion conn = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);
        if (consultaNucleo.TieneNucleo(modeloInstitucion.getNombreInstitucion()) >= 1) {
            cargarNucleosExistentes();
            view.comboNucleo.setVisible(true);
            view.Nucleo.setVisible(true);
        }
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
                modeloInstitucion, verInstituciones, modeloUsuario, modeloMunicipio);
        contraladorVerInstituciones.iniciar();
        view.dispose();
    }

    private void llenarComboInstitucion(){
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        // Obtener la lista de instituciones desde el Modelo
        List<String> instituciones = consultasInstitucion.obtenerInstituciones();

        // Limpiar el comboBox y agregar un item vacío
        view.comoInstitucion.removeAllItems();
        view.comoInstitucion.addItem("");  // Añadimos un item vacío como indicativo

        // Llenar el comboBox con las instituciones obtenidas del Modelo
        for (String institucion : instituciones) {
            view.comoInstitucion.addItem(institucion);
        }

        // Si el comboBox tiene elementos, seleccionamos el primero
        if (view.comoInstitucion.getItemCount() > 0) {
            view.comoInstitucion.setSelectedIndex(0); // Seleccionamos el primer elemento
        }
    }

    /**
     * Método que inicializa los listeners para los botones y componentes de la vista.
     * Asocia eventos de acción a los botones y otros componentes interactivos.
     */
    public void Listeners() {
        this.view.perfil.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.RegistrarEmision.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.generarPlanDeAccion.addActionListener(this::actionPerformed);
        this.view.Descargar.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.municipio.addActionListener(e -> {
            if (view.municipio.getItemCount() > 0) {
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
        this.view.verInstitucion.addActionListener(this::actionPerformed);
        this.view.comoInstitucion.addActionListener(e -> {
            if (view.comoInstitucion.getItemCount() > 0) {
                view.municipio.removeAllItems(); // Limpiar el combo de modeloMunicipio
                view.comboNucleo.removeAllItems(); // Limpiar el combo de núcleos
                cargarMunicipio(); // Cargar municipios para la institución seleccionada
                cargarAnioBase(); // Cargar el año base para la institución
            }
            Conexion conn = new Conexion();
            ConsultaNucleo consultaNucleo;
            consultaNucleo = new ConsultaNucleo(conn);
            String nombreInstitucion = String.valueOf(view.comoInstitucion.getSelectedItem());
            if (consultaNucleo.TieneNucleo(nombreInstitucion) >= 1) {
                view.Nucleo.setVisible(true);
                view.comboNucleo.setVisible(true);
                view.anio.removeAllItems();
            } else {
                view.comboNucleo.setVisible(false);
                view.Nucleo.setVisible(false);
                cargarAnioBase();
            }
        });
    }


}


