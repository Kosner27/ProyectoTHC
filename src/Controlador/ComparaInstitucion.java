package Controlador;

import Modelo.*;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;

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
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ComparaInstitucion {
    public GraficoCompararConsultas consul;
    public GraficoCompararModelo mod;
    public CompararOtrarInstituciones view;
    public GraficoComparar view2;
    public InstitucionModelo ins;
    public Municipio m;
    private final Usuario user;
    public Conexion conn = new Conexion();
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public ComparaInstitucion(GraficoCompararModelo mod, GraficoCompararConsultas consul,
                              CompararOtrarInstituciones view, GraficoComparar view2,
                              InstitucionModelo ins, Municipio m, Usuario user) {
        this.mod = mod;
        this.consul = consul;
        this.view = view;
        this.view2 = view2;
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
                //cargarAnioBase();
                cargarMunicipio();
                //cargarNucleosExistentes();
                //anioBaseNucleo();
                view.setTitle("Seleccionar Instituciones");
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                view.VerPerfiles.setVisible(false);
                view.verInstitucion.setVisible(false);
                view.setLocationRelativeTo(null);
                break;
            case "Superadmin":
                view.setTitle("Seleccionar Instituciones");
                view.setLocationRelativeTo(null);
                cargarInstitucion();
                //cargarAnioBase();
                //anioBaseNucleo();
                cargarMunicipio();
                cargarNucleosExistentes();
                break;
            case "Invitado":
                view.setTitle("Seleccionar Instituciones");
                view.setLocationRelativeTo(null);
                cargarInstitucion();
               // cargarAnioBase();
               // anioBaseNucleo();
                cargarMunicipio();
                cargarNucleosExistentes();
                view.RegistrarInstitucion.setVisible(false);
                view.VerPerfiles.setVisible(false);
                view.RegistrarEmision.setVisible(false);
                view.Calcular.setVisible(false);
                view.verInstitucion.setVisible(false);

                break;
            default:
                JOptionPane.showMessageDialog(null, "Usuario no definido en el sistema");
                break;

        }
        view.setTitle("Seleccionar Instituciones");
        view.setLocationRelativeTo(null);
        cargarInstitucion();
        cargarAnioBase();
        GraficoPrincipal.addActionListener(e -> {
            vistaGraficoPrincipal();
        });
        GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.anadirButton) {
            String a = String.valueOf(view.anio.getSelectedItem());
            String b = String.valueOf(view.alcance.getSelectedItem());
            view.Instituciones.setDefaultEditor(Object.class, null); // Deshabilita la edición de las celdas

            if (!a.isEmpty() && !b.isEmpty()) {
                String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
                String nombreMunicipio = String.valueOf(view.Municipio.getSelectedItem());
                view.alcance.setEnabled(false);
                String nucleo = view.nucleo.isVisible() ? String.valueOf(view.nucleo.getSelectedItem()).trim() : null; // Si no está visible, es opcional

                DefaultTableModel tableModel = (DefaultTableModel) view.Instituciones.getModel();

                if (!nombreInstitucion.isEmpty()) {
                    System.out.println("Nombre de la institución: " + nombreInstitucion);
                    System.out.println("Municipio: " + nombreMunicipio);

                    if (nucleo == null || nucleo.isEmpty() || "Sumatoria de todos los nucleos registrados".equals(nucleo)) {
                        // Caso: sumatoria de núcleos o sin núcleo
                        List<InstitucionModelo> datos = consul.llenarTabla(nombreInstitucion, nombreMunicipio);


                        for (InstitucionModelo dato : datos) {
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
                        List<Nucleo> datos2 = consul.llenarTablaConNucleo(nombreInstitucion, nombreMunicipio, nucleo);


                        for (Nucleo dato : datos2) {
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

                view.Instituciones.setModel(tableModel);
                view.Instituciones.setVisible(true);
                view.Contenedor.setVisible(true);


            } else {
                JOptionPane.showMessageDialog(null, "Debes seleccionar un año base y un alcance antes de continuar.");
            }
        }
        if (e.getSource() == view.compararButton) {
            String anio = String.valueOf(view.anio.getSelectedItem());
            String alcance = String.valueOf(view.alcance.getSelectedItem());
            System.out.println(anio);
            if (!anio.isEmpty() && !alcance.isEmpty()) {
                DefaultCategoryDataset datos = new DefaultCategoryDataset();
                DefaultTableModel tableModel = (DefaultTableModel) view.Instituciones.getModel();
                int rowCount = tableModel.getRowCount();
                List<String> nombresInstituciones = new ArrayList<>();
                List<String> campus = new ArrayList<>();

                for (int i = 0; i < rowCount; i++) {
                    String nombreInstitucion = (String) tableModel.getValueAt(i, 0);
                    nombresInstituciones.add("'" + nombreInstitucion + "'");
                    String nombrenucleo = (String) tableModel.getValueAt(i, 4);
                    campus.add("'" + nombrenucleo + "'");
                }

                String instituciones = String.join(",", nombresInstituciones);
                String Campus = String.join(",", campus);

                System.out.println("Parámetros enviados al procedimiento almacenado:");
                System.out.println("Instituciones: " + instituciones);
                System.out.println("Año: " + anio);
                System.out.println("Alcance: " + alcance);
                System.out.println("Campus: " + Campus);

                // Obtener datos para el gráfico
                List<GraficoCompararModelo> mod = consul.LlenarGrafico(instituciones, anio, alcance, Campus);

                if (mod.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "No se encontraron datos para la institución, año y alcance seleccionados.", "Sin datos", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                for (GraficoCompararModelo dato : mod) {
                    String label = dato.getNombrefuente();
                    Double total = dato.getTotal();
                    String nucleo = dato.getNucleo() != null ? dato.getNucleo() : "Sin núcleo";
                    String label2 = dato.getNombreInstitucion() + " - Campus: " + nucleo;

                    datos.setValue(total, label2, label);
                }

                JFreeChart grafico = grafico(datos);
                mostrarGrafico(grafico);
            } else {
                JOptionPane.showMessageDialog(null, "Debes seleccionar un año y alcance antes de comparar.");
            }
        }

        if (view2.descargarButton == e.getSource()) {
            String anio = String.valueOf(view.anio.getSelectedItem());
            String alcance = String.valueOf(view.alcance.getSelectedItem());
            if (!anio.isEmpty() && !alcance.isEmpty()) {
                DefaultCategoryDataset datos = new DefaultCategoryDataset();
                DefaultTableModel tableModel = (DefaultTableModel) view.Instituciones.getModel();
                int rowCount = tableModel.getRowCount();
                List<String> nombresInstituciones = new ArrayList<>();
                List<String> nombreCampus = new ArrayList<>();
                for (int i = 0; i < rowCount; i++) {
                    String nombreInstitucion = (String) tableModel.getValueAt(i, 0); // Asumiendo que el nombre de la institución está en la primera columna
                    nombresInstituciones.add("'" + nombreInstitucion + "'");
                    String nombreNucleo = (String) tableModel.getValueAt(i, 4);
                    nombreCampus.add("'" + nombreNucleo + "'");
                }
                String campus = String.join(",", nombreCampus);
                String instituciones = String.join(",", nombresInstituciones);
                List<GraficoCompararModelo> mod = consul.LlenarGrafico(instituciones, anio, alcance, campus);

                if (mod.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "No se encontraron datos para la institución, año y alcance seleccionados.", "Sin datos", JOptionPane.INFORMATION_MESSAGE);
                    return; // Terminar la ejecución del método si no hay datos
                }

                for (GraficoCompararModelo dato : mod) {
                    String label = dato.getNombrefuente();
                    Double total = dato.getTotal();
                    String nucleo = dato.getNucleo() != null ? dato.getNucleo() : "Sin núcleo";
                    String label2 = dato.getNombreInstitucion() + " -campus: " + nucleo;
                    datos.setValue(total, label2, label);
                    JFreeChart grafico = grafico(datos);
                    mostrarGrafico(grafico);
                }

                String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                String b = "CompararInstitucionesCon" + String.valueOf(view.institucion.getSelectedItem()) + fecha;
                JFileChooser f = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
                f.setFileFilter(filter);
                f.setDialogTitle("Guardar archivo ");
                f.setSelectedFile(new File(b + ".pdf"));
                int userSelection = f.showSaveDialog(null);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File c = f.getSelectedFile();
                    File file = ensureUniqueFilename(c.getParentFile(), c.getName());
                    ExportarGrafico(datos, file); // Asegúrate de que la variable datos esté en el ámbito adecuado aquí
                }
            } else {
                JOptionPane.showMessageDialog(view, "Por favor seleccione una institución, un año y un alcance antes de proceder.", "Datos faltantes", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.getSource() == view.inicioButton) {
            BotonInicio();
        }
        if (e.getSource() == view2.inicioButton) {
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
        if (e.getSource() == view.eliminarBtn) {
            eliminarFila();
        }

        if(e.getSource() == view.verInstitucion){
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

    private JFreeChart grafico(DefaultCategoryDataset dataset) {
        return ChartFactory.createBarChart("Comparar instituciones por alcance ",   // Título del gráfico
                "Fuentes emision",       // Etiqueta del eje X
                "Cantidad de co2",       // Etiqueta del eje Y
                dataset   // Conjunto de datos
        );
    }

    private void mostrarGrafico(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(300, 700));
        view2.PanelGrafico.setLayout(new BorderLayout());
        view2.PanelGrafico.removeAll();
        view2.PanelGrafico.add(panel, BorderLayout.CENTER);
        view2.PanelGrafico.revalidate();
        view2.PanelGrafico.repaint();
        view2.setVisible(true);

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
        String nombreMunicipio = String.valueOf(view.Municipio.getSelectedItem());

        // Consultar los años base desde el Modelo
        System.out.println(nombreInstitucion + " hola");

        System.out.println(nombreMunicipio + "que mas");

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
            view.Municipio.removeAllItems();
            view.Municipio.addItem("");  // Añadir un item vacío como indicativo

            // Llenar el JComboBox con los municipios obtenidos del Modelo
            for (String municipio : municipios) {
                view.Municipio.addItem(municipio);
            }

            // Si el JComboBox tiene elementos, seleccionamos el primero
            if (view.Municipio.getItemCount() > 0) {
                view.Municipio.setSelectedIndex(0); // Seleccionamos el primer elemento
            }
        } else {
            // Si la institución está vacía, limpiar el JComboBox de municipios
            view.Municipio.removeAllItems();
        }
    }

    public void cargarNucleosExistentes() {
        // Limpiamos el combo box antes de cargar los nuevos valores
        view.nucleo.removeAllItems();
        view.nucleo.addItem(" ");
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
        // Obtenemos el nombre de la institución desde el modelo
        String nombreInstitucion = view.institucion.getSelectedItem().toString();
        // Llamamos al modelo para cargar los núcleos
        List<String> nucleos = consultaNucleo.cargarNucleos(nombreInstitucion);

        // Agregamos los núcleos al combo box
        for (String nucleo : nucleos) {
            view.nucleo.addItem(nucleo);
        }


    }

    private void anioBaseNucleo() {
        view.anio.removeAllItems(); // Limpiar el combo de años
        view.anio.addItem(" "); // Agregar un texto indicativo
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
        String nombre = String.valueOf(view.institucion.getSelectedItem());
        String municipio = String.valueOf(view.Municipio.getSelectedItem());
        String nucleo = String.valueOf(view.nucleo.getSelectedItem());
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
        String nombreMunicipio = (String) view.Municipio.getSelectedItem();

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
        control.inicio();
        view.dispose();
        view2.dispose();
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
        Calcular viewCal = new Calcular();
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

    public void vistaGraficoPrincipal() {
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos viewGraf = new Graficos();
        GraficorModelo mod = new GraficorModelo();
        InstitucionModelo modelo = new InstitucionModelo();
        GraficoControlador contro = new GraficoControlador(mod, consul, viewGraf, modelo, m, user, ins);
        contro.iniciar();
        viewGraf.setVisible(true);
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

    public void eliminarFila() {
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
            // Si no se seleccionó ninguna fila, muestra un mensaje
            JOptionPane.showMessageDialog(view, "Por favor, selecciona una fila para eliminar.", "No se seleccionó ninguna fila", JOptionPane.WARNING_MESSAGE);
        }
        if (tableModel.getRowCount() == 0) {
            // Si la tabla está vacía, desbloquea el campo
            view.alcance.setEnabled(true);
            view.alcance.setSelectedIndex(0);
        }

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
        this.view.institucion.addActionListener(e -> {
            if (view.institucion.getItemCount() > 0) {
                cargarMunicipio();
                cargarAnioBase();
                cargarNucleosExistentes();
            }
            Conexion conn = new Conexion();
            ConsultaNucleo consultaNucleo;
            consultaNucleo = new ConsultaNucleo(conn);
            String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
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
        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);
        this.view.nucleo.addActionListener(e -> {
            if (view.nucleo.getItemCount() > 0) {
                anioBaseNucleo();
                if (view.nucleo.getSelectedItem() == "Sumatoria de todos los nucleos registrados") {
                    anioParaSumaTodosLosNucleos();
                }// Cargar el año base basado en la selección del núcleo
            }
        });
        this.view.verInstitucion.addActionListener(this::actionPerformed);
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

}
