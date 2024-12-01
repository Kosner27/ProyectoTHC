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
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ControladorReducir {
    private final InstitucionModelo ins;
    private final GraficoConsulta consul;
    private final Reducir2 view;
    private final Municipio m;
    private final Usuario modUser;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public ControladorReducir(InstitucionModelo ins, GraficoConsulta consul, Reducir2 view, Municipio m, Usuario modUser) {
        this.ins = ins;
        this.consul = consul;
        this.view = view;
        this.m = m;
        this.modUser = modUser;
        Listeners();

    }

    public void Iniciar() {
        switch (modUser.getTipoUsuario()) {
            case "Invitado":
                view.setTitle("Reducir");
                cargarAnioBase();
                cargarMunicipio();
                llenarComboInstitucion();
                view.comoInstitucion.setVisible(true);
                view.setVisible(true);
                view.Institucio.setText(ins.getNombreInstitucion());
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
                System.out.println(view.Institucio.getText() + " hola");
                System.out.println(modUser.getTipoUsuario() +" estoy en le case");
                view.setVisible(true);
                view.setLocationRelativeTo(null);
                view.Institucio.setText(ins.getNombreInstitucion());
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.verInstitucion.setVisible(false);
                break;
            case "Superadmin":
                view.setTitle("Reducir");
                cargarAnioBase();
                cargarMunicipio();
                Nucleo();
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
                JOptionPane.showMessageDialog(null, "Usuario no definido en el sistema");
                break;

        }


        GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
        GraficosCompararInstitucion.addActionListener(e -> vistaCompararInstituciones());
        GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
    }

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

    public void cargarAnioBase() {
        // Obtener la lista de años base desde el Modelo
        String nombreInstitucion = String.valueOf(view.Institucio.getText());
        String nombreMunicipio = String.valueOf(view.municipio.getSelectedItem());

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

    public void cargarMunicipio() {
        // Obtener la institución seleccionada desde la vista
        if(modUser.getTipoUsuario().equals("Superadmin")){
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
            String nombreInstitucion = ins.getNombreInstitucion();
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

    public void cargarNucleosExistentes() {
        // Limpiamos el combo box antes de cargar los nuevos valores
        view.comboNucleo.removeAllItems();
        view.comboNucleo.addItem(" ");
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
            if(modUser.getTipoUsuario().equals("Superadmin")){
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

    private void descargarSinNucleo() {
        String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        if(modUser.getTipoUsuario().equals("Superadmin")){
            String b = view.comoInstitucion.getSelectedItem() + fecha;
            JFileChooser f = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);
            f.setDialogTitle("Guardar archivo PDF");
            f.setSelectedFile(new File(b + ".pdf"));
            int userSelection = f.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = f.getSelectedFile();
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());
                ExportarPdf(view.introduccionLaHuellaDeTextArea.getText(), file);
            }
        }else{
            String b = view.Institucio.getText() + fecha;
            JFileChooser f = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);
            f.setDialogTitle("Guardar archivo PDF");
            f.setSelectedFile(new File(b + ".pdf"));
            int userSelection = f.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = f.getSelectedFile();
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());
                ExportarPdf(view.introduccionLaHuellaDeTextArea.getText(), file);
            }
        }

    }

    private void descargarConNucleo() {
        String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String nucleo = Objects.requireNonNull(view.comboNucleo.getSelectedItem()).toString();
        if(modUser.getTipoUsuario().equals("Superadmin")){
            String b = String.valueOf(view.comoInstitucion.getSelectedItem()) + fecha + "DelNucleo" + nucleo;
            JFileChooser f = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);
            f.setDialogTitle("Guardar archivo PDF");
            f.setSelectedFile(new File(b + ".pdf"));
            int userSelection = f.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = f.getSelectedFile();
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());
                ExportarPdfConNucleo(view.introduccionLaHuellaDeTextArea.getText(), file);
            }
        }
        else
        {
            String b = view.Institucio.getText() + fecha + "DelNucleo" + nucleo;
            JFileChooser f = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);
            f.setDialogTitle("Guardar archivo PDF");
            f.setSelectedFile(new File(b + ".pdf"));
            int userSelection = f.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = f.getSelectedFile();
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());
                ExportarPdfConNucleo(view.introduccionLaHuellaDeTextArea.getText(), file);
            }
        }


    }

    public void InsertarEncajatexto() {
        if (!Objects.requireNonNull(view.anio.getSelectedItem()).toString().isEmpty() && !Objects.requireNonNull(view.municipio.getSelectedItem()).toString().isEmpty()) {
            String nombreInstitucion = view.Institucio.getText();
            String anioBaseString = String.valueOf(view.anio.getSelectedItem());
            int anioBase = Integer.parseInt(anioBaseString);
            String NombreMuncipio = view.municipio.getSelectedItem().toString();
            List<GraficorModelo> datos = consul.GraficoPorAlcance(nombreInstitucion, anioBase, NombreMuncipio);
            List<CalcularModelo> datos2 = consul.GraficoPorFuente(nombreInstitucion, anioBase, NombreMuncipio);

            String parrafo1 = "Introducción\n" + "La huella de carbono de una institución universitaria incluye las emisiones de gases de \n" + "efecto invernadero (GEI) generadas por sus actividades operativas. Este informe desglosa la huella de carbono de una universidad en Colombia, clasificada por alcance, y propone una estrategia para mitigar estas emisiones mediante la plantación de árboles." + "\n" + "\nPara calcular la huella de carbono, se consideran las siguientes fuentes de emisiones:\n" + "\n" + "Alcance 1: Emisiones Directas\n" + "Emisiones de vehículos universitarios.\n" + "Emisiones de equipos de combustión en el campus.\n" + "\n" + "Alcance 2: Emisiones Indirectas por Consumo de Energía\n" + "Emisiones de electricidad comprada y consumida en el campus.\n" + "\n" + "Alcance 3: Otras Emisiones Indirectas\n" + "Emisiones de viajes de estudiantes y empleados.\n" + "Emisiones de la gestión de residuos.\n" + "Emisiones de proveedores y cadena de suministro.\n" + "\n";
            String[] alcance = new String[datos.size()];
            Double[] total = new Double[datos.size()];
            StringBuilder textoAcumulado = new StringBuilder();
            Double Sumar = 0.0;
            for (int i = 0; i < datos.size(); i++) {
                GraficorModelo a = datos.get(i);
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
                CalcularModelo dato = datos2.get(i);
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

    private void anioBaseNucleo() {
        view.anio.removeAllItems(); // Limpiar el combo de años
        view.anio.addItem(" "); // Agregar un texto indicativo
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);

        if(modUser.getTipoUsuario().equals("Superadmin")){
            String nombre = String.valueOf(view.comoInstitucion.getSelectedItem());
            String municipio = String.valueOf(view.municipio.getSelectedItem());
            String nucleo = String.valueOf(view.comboNucleo.getSelectedItem());
            List<String> anioBaseNucleo = consultaNucleo.obtenerAnosBase(nombre, municipio, nucleo);
            for (String anioBase : anioBaseNucleo) {
                view.anio.addItem(anioBase);
            }
        }  else{
            String nombre = view.Institucio.getText();
            String municipio = String.valueOf(view.municipio.getSelectedItem());
            String nucleo = String.valueOf(view.comboNucleo.getSelectedItem());
            List<String> anioBaseNucleo = consultaNucleo.obtenerAnosBase(nombre, municipio, nucleo);
            for (String anioBase : anioBaseNucleo) {
                view.anio.addItem(anioBase);
        }

        }


    }

    public void InsertarEncajatextoConNucleo() {
        if (!Objects.requireNonNull(view.anio.getSelectedItem()).toString().isEmpty() && !Objects.requireNonNull(view.municipio.getSelectedItem()).toString().isEmpty()) {
            String nombreInstitucion = view.Institucio.getText();
            String anioBaseString = String.valueOf(view.anio.getSelectedItem());
            String nombreN = view.comboNucleo.getSelectedItem().toString();
            int anioBase = Integer.parseInt(anioBaseString);
            String NombreMuncipio = view.municipio.getSelectedItem().toString();
            List<GraficorModelo> datos = consul.GraficoPorAlcanceNucleo(nombreInstitucion, anioBase, NombreMuncipio, nombreN);
            List<CalcularModelo> datos2 = consul.GraficoPorFuenteNucleo(nombreInstitucion, anioBase, NombreMuncipio, nombreN);

            String parrafo1 = "Introducción\n" + "La huella de carbono de una institución universitaria incluye las emisiones de gases de \n" + "efecto invernadero (GEI) generadas por sus actividades operativas. Este informe desglosa la huella de carbono de una universidad en Colombia, clasificada por alcance, y propone una estrategia para mitigar estas emisiones mediante la plantación de árboles." + "\n" + "\nPara calcular la huella de carbono, se consideran las siguientes fuentes de emisiones:\n" + "\n" + "Alcance 1: Emisiones Directas\n" + "Emisiones de vehículos universitarios.\n" + "Emisiones de equipos de combustión en el campus.\n" + "\n" + "Alcance 2: Emisiones Indirectas por Consumo de Energía\n" + "Emisiones de electricidad comprada y consumida en el campus.\n" + "\n" + "Alcance 3: Otras Emisiones Indirectas\n" + "Emisiones de viajes de estudiantes y empleados.\n" + "Emisiones de la gestión de residuos.\n" + "Emisiones de proveedores y cadena de suministro.\n" + "\n";
            String[] alcance = new String[datos.size()];
            Double[] total = new Double[datos.size()];
            StringBuilder textoAcumulado = new StringBuilder();
            Double Sumar = 0.0;
            for (int i = 0; i < datos.size(); i++) {
                GraficorModelo a = datos.get(i);
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
                CalcularModelo dato = datos2.get(i);
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

    public void ExportarPdf(String content, File r) {
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
            if(modUser.getTipoUsuario().equals("Superadmin")){
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

            // Close the document
            document.close();
            JOptionPane.showMessageDialog(null, "PDF se descargó correctamente");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

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
            if(modUser.getTipoUsuario().equals("Superadmin")){
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

    public void vistaActualizarInstitucion() {
        Conexion con = new Conexion();
        ConsultasInstitucion consul = new ConsultasInstitucion();
        VerDatosInstitucion view2 = new VerDatosInstitucion();
        InstitucionModelo mod = new InstitucionModelo();
        InstitucionControlador control = new InstitucionControlador(ins, m, view2, consul, mod, modUser);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();


    }

    public void vistaPerfil() {
        Conexion conn = new Conexion();
        Perfil per = new Perfil();
        ConsultaUsuario cons = new ConsultaUsuario(conn);
        PerfilCOntrolador control = new PerfilCOntrolador(modUser, per, ins, m, cons);
        control.Iniciar();
        per.setVisible(true);
        view.dispose();
    }

    public void vistaCalcular() {
        Conexion con = new Conexion();
        Vistas.Calcular view2 = new Calcular();
        CalcularModelo mod = new CalcularModelo();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        CalcularControlador controlador = new CalcularControlador(mod, consul, view2, ins, consultaUsuario, modUser, m);
        controlador.iniciar();
        view2.setVisible(true);
        view.dispose();
    }

    public void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        EmisionModelo mod = new EmisionModelo();
        ConsultasEmision consul = new ConsultasEmision();
        EmisionControlador controlador = new EmisionControlador(mod, consul, emisionView, ins, m, modUser);
        controlador.iniciar();
        emisionView.setVisible(true);
        view.dispose();
    }

    public void vistaInforme() {
        Conexion con = new Conexion();
        InstitucionModelo mod2 = new InstitucionModelo();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloInforme mod = new ModeloInforme();
        Informe view2 = new Informe();
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorInforme contro = new ControladorInforme(view2, mod, consul, m, ins, modUser);
        contro.iniciar();
        view2.setVisible(true);
        view.dispose();
    }

    public void vistaGraficoPrincipal() {
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos view2 = new Graficos();
        GraficorModelo mod = new GraficorModelo();
        InstitucionModelo modelo = new InstitucionModelo();
        GraficoControlador contro = new GraficoControlador(mod, consul, view2, modelo, m, modUser, ins);
        contro.iniciar();
        view2.Graficos.setVisible(true);
        view.dispose();
    }

    public void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar view2 = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        GraficoCompararModelo mod = new GraficoCompararModelo();
        ComparaInstitucion contro = new ComparaInstitucion(mod, consultas, comIns, view2, ins, m, modUser);
        contro.iniciar();
        view.dispose();
    }

    public void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        TendenciaModelo mod = new TendenciaModelo();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia view2 = new GraficoTendencia();
        TendenciaControlador control = new TendenciaControlador(mod, consult, view2, m, ins, modUser);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();
    }

    public void vistaVerPerfiles() {
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);

        VerPerfilesControlador verControl = new VerPerfilesControlador(modUser, ins, m, verPerfiles, consul);
        verControl.Iniciar();
        view.dispose();
    }

    public void BotonInicio() {
        ControladoInicio control = new ControladoInicio(ins, modUser, m);
        control.inicio();
        view.dispose();
    }

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
                view.municipio.removeAllItems(); // Limpiar el combo de municipio
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

    private void Nucleo() {
        if (!ins.getNombreInstitucion().isEmpty()) {
            cargarMunicipio(); // Carga municipios basados en la institución
            cargarAnioBase(); // Carga años base basados en la institución y municipio
        }
        Conexion conn = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);
        if (consultaNucleo.TieneNucleo(ins.getNombreInstitucion()) >= 1) {
            cargarNucleosExistentes();
            view.comboNucleo.setVisible(true);
            view.Nucleo.setVisible(true);
        }
    }

    private void vistaVerInstitucion() {
        Conexion con = new Conexion();
        VerInstituciones verInstituciones = new VerInstituciones();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(con);
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        InstitucionModelo institucionModelo = new InstitucionModelo();
        ContraladorVerInstituciones contraladorVerInstituciones = new ContraladorVerInstituciones(consultasInstitucion, consultaNucleo,
                institucionModelo, verInstituciones, modUser, m);
        contraladorVerInstituciones.iniciar();
        //view.dispose();
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



}


