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
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ControladorInforme {
    private final Informe view;
    private final ModeloInforme mod;
    private final ConsultaInforme consul;
    private final Municipio m;
    private final InstitucionModelo ins;
    private final Usuario modUser;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public ControladorInforme(Informe view, ModeloInforme mod, ConsultaInforme consul,
                              Municipio m, InstitucionModelo ins, Usuario modUse) {
        this.view = view;
        this.consul = consul;
        this.mod = mod;
        this.m = m;
        this.ins = ins;
        this.modUser = modUse;
        Listeners();
    }


    public void iniciar() {
        view.Graficos.add(GraficoPrincipal);
        view.Graficos.add(GraficosCompararInstitucion);
        view.Graficos.add(GraficoHistorico);
        String usuario = modUser.getTipoUsuario();
        DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
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
                view.setLocationRelativeTo(null);
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
                JOptionPane.showMessageDialog(null, "Usuario no definido en el sistema");


        }
        GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
        GraficosCompararInstitucion.addActionListener(e -> vistaCompararInstituciones());
        GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());

    }

    private void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.buscarButton) {

            if (!view.comboNucleo.isVisible()) {
                LlenarTablaSinNucleo();
            }
            if (view.comboNucleo.isVisible()) {
                LlenarTablaConNucleo();
            }
        }
        if (e.getSource() == view.descargarButton) {
            if (!view.comboNucleo.isVisible()) {
                LlenarDocumentoSinNucleo();
            }
            if (view.comboNucleo.isVisible()) {
                LlenarDocumentoConNucleo();
            }
        }
        if (e.getSource() == view.inicioButton) {
            BotonInicio();
        }
        if (e.getSource() == view.perfil) {
            vistaPerfil();
        }
        if (e.getSource() == view.RegistrarEmision) {
            vistaRegistrarEmision();
        }
        if (e.getSource() == view.Calcular) {
            vistaCalcular();
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
        if (e.getSource() == view.verInstitucion) {
            vistaVerInstitucion();
        }
    }

    private void LlenarDocumentoSinNucleo() {
        String Nombre = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String Institucion = String.valueOf(view.institucion.getSelectedItem());
        Document document = new Document();
        String b = "InformeDeLa" + Institucion + Nombre;
        List<ModeloInforme> a = consul.datos(view.institucion.getSelectedItem().toString(), m.getNombreM());
        //codgio para eligir el lugar en donde se descarga el archivo
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
        fileChooser.setFileFilter(filter);

        fileChooser.setDialogTitle("Guardar archivo PDF");
        fileChooser.setSelectedFile(new File(b + ".pdf"));
        int userSelection = fileChooser.showSaveDialog(null);
        ////

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File c = fileChooser.getSelectedFile();
            File file = ensureUniqueFilename(c.getParentFile(), c.getName());

            try {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                PdfPTable table = new PdfPTable(view.Emisiones.getColumnCount());
                // Obtener la fecha actual
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
                    String nombreMunicipi = info.getMunicipio();
                    Paragraph municipio1 = new Paragraph("Municipio: " + nombreMunicipi);
                    municipio1.setAlignment(Element.ALIGN_LEFT);
                    document.add(municipio1);
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
                Paragraph text = new Paragraph("En la siguiente tabla vamos a encontrar las siguiente columnas \n" +
                        "Nombre de la fuente de emisión: Aparece el nombre de cada una de las fuentes de emisión asociadas a la institución en el año base seleccionado.\n" +
                        "Alcance: Corresponde al alcance de cada una de las fuentes de emisión.\n" +
                        "Cantidad consumida: Indica el consumo que tuvo la institución durante el año base de dicha fuente de emisión.\n" +
                        "Unidad de medida: Es la unidad en la que se mide cada fuente de emisión.\n" +
                        "Factor de emisión: Factor estandarizado por Colombia, utilizado para calcular el CO2 aportado por cada fuente de emisión.\n" +
                        "Año base: Año en que fueron recolectados los datos de la fuente de emisión.\n");
                document.add(text);
                document.add(new Paragraph(" "));

                table.setWidthPercentage(100);


                for (int i = 0; i < view.Emisiones.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(view.Emisiones.getColumnName(i)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                for (int i = 0; i < view.Emisiones.getRowCount(); i++) {
                    for (int j = 0; j < view.Emisiones.getColumnCount(); j++) {
                        Object value = view.Emisiones.getValueAt(i, j);
                        String cellText = (value != null) ? value.toString() : "";
                        table.addCell(cellText);
                    }
                }

                document.add(table);
                document.add(new Paragraph(" "));
                String t = view.total.getText();
                Paragraph tot = new Paragraph("Total de CO2 emitido para el año base seleccionado: " + t);
                document.add(tot);
                document.add(new Paragraph(" "));
                JOptionPane.showMessageDialog(null, "PDF generado correctamente.");

            } catch (DocumentException ex) {
                throw new RuntimeException(ex);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            } finally {
                document.close();
                System.out.println("Documento cerrado.");
            }

        }
    }

    private void LlenarDocumentoConNucleo() {
        String Nombre = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String Institucion = String.valueOf(view.institucion.getSelectedItem());
        String nucleo = String.valueOf(view.comboNucleo.getSelectedItem());
        Document document = new Document();
        String b = "InformeDeLa" + Institucion + Nombre + "DelNucleo" + nucleo;
        List<ModeloInforme> a = consul.datos(view.institucion.getSelectedItem().toString(), m.getNombreM());
        //codgio para eligir el lugar en donde se descarga el archivo
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
        fileChooser.setFileFilter(filter);

        fileChooser.setDialogTitle("Guardar archivo PDF");
        fileChooser.setSelectedFile(new File(b + ".pdf"));
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File c = fileChooser.getSelectedFile();
            File file = ensureUniqueFilename(c.getParentFile(), c.getName());

            try {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                PdfPTable table = new PdfPTable(view.Emisiones.getColumnCount());
                // Obtener la fecha actual
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
                String nombreNucleo = view.comboNucleo.getSelectedItem().toString();
                Paragraph nNucleo = new Paragraph("Nombre del nucleo: " + nombreNucleo);
                nNucleo.setAlignment(Element.ALIGN_LEFT);
                document.add(new Paragraph(("")));
                document.add(nNucleo);
                document.add(new Paragraph("  "));


                for (ModeloInforme info : a) {
                    String nombreMunicipi = info.getMunicipio();
                    Paragraph municipio1 = new Paragraph("Municipio: " + nombreMunicipi);
                    municipio1.setAlignment(Element.ALIGN_LEFT);
                    document.add(municipio1);
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
                Paragraph text = new Paragraph("En la siguiente tabla vamos a encontrar las siguiente columnas \n" +
                        "Nombre de la fuente de emisión: Aparece el nombre de cada una de las fuentes de emisión asociadas a la institución en el año base seleccionado.\n" +
                        "Alcance: Corresponde al alcance de cada una de las fuentes de emisión.\n" +
                        "Cantidad consumida: Indica el consumo que tuvo la institución durante el año base de dicha fuente de emisión.\n" +
                        "Unidad de medida: Es la unidad en la que se mide cada fuente de emisión.\n" +
                        "Factor de emisión: Factor estandarizado por Colombia, utilizado para calcular el CO2 aportado por cada fuente de emisión.\n" +
                        "Año base: Año en que fueron recolectados los datos de la fuente de emisión.\n");
                document.add(text);
                document.add(new Paragraph(" "));

                table.setWidthPercentage(100);


                for (int i = 0; i < view.Emisiones.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(view.Emisiones.getColumnName(i)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                for (int i = 0; i < view.Emisiones.getRowCount(); i++) {
                    for (int j = 0; j < view.Emisiones.getColumnCount(); j++) {
                        Object value = view.Emisiones.getValueAt(i, j);
                        String cellText = (value != null) ? value.toString() : "";
                        table.addCell(cellText);
                    }
                }

                document.add(table);
                document.add(new Paragraph(" "));
                String t = view.total.getText();
                Paragraph tot = new Paragraph("Total de CO2 emitido para el año base seleccionado: " + t);
                document.add(tot);
                document.add(new Paragraph(" "));
                JOptionPane.showMessageDialog(null, "PDF generado correctamente.");

            } catch (DocumentException ex) {
                throw new RuntimeException(ex);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            } finally {
                document.close();
                System.out.println("Documento cerrado.");
            }

        }
    }

    private void LlenarTablaSinNucleo() {
        Object anioObj = view.anio.getSelectedItem();
        Object InstitucionObj = view.institucion.getSelectedItem();
        Object municipioObj = view.municipio.getSelectedItem();
        Object nucleoObje = view.comboNucleo.getSelectedItem();
        DefaultTableModel tableModel2 = (DefaultTableModel) view.Emisiones.getModel();
        double total = 0.0;
        if (InstitucionObj == null && municipioObj == null && nucleoObje == null) {
            JOptionPane.showMessageDialog(null, " LLene los datos de institucion y municipio");
        } else {
            if (anioObj == null) {
                JOptionPane.showMessageDialog(null, "No hay datos para la sede seleccionada");
                return; // Salir del método si algún JComboBox es null
            }
        }
        String anio = String.valueOf(anioObj);
        String Institucion = String.valueOf(InstitucionObj);
        String municipio = String.valueOf(municipioObj);

        List<ModeloInforme> informes = consul.tablaInforme(Institucion, Integer.parseInt(anio), municipio);


        tableModel2.setRowCount(0);
        if (!Institucion.isEmpty() && !municipio.isEmpty()) {
            for (ModeloInforme informe : informes) {
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

            // Calcular el total de la carga ambiental
            for (int row = 0; row < tableModel2.getRowCount(); row++) {
                String valor = tableModel2.getValueAt(row, 6).toString();
                if (!valor.isEmpty()) {
                    try {
                        double valor1 = Double.parseDouble(valor);
                        total += valor1;
                    } catch (NumberFormatException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
            }
            view.total.setText(String.valueOf(total));
        } else {
            JOptionPane.showMessageDialog(null, "No hay datos para el municipio seleccionado.");
            tableModel2.setRowCount(0);
        }
    }

    private void LlenarTablaConNucleo() {
        Object anioObj = view.anio.getSelectedItem();
        Object InstitucionObj = view.institucion.getSelectedItem();
        Object municipioObj = view.municipio.getSelectedItem();
        Object nucleoObje = view.comboNucleo.getSelectedItem();
        DefaultTableModel tableModel2 = (DefaultTableModel) view.Emisiones.getModel();
        double total = 0.0;
        if (InstitucionObj == null && municipioObj == null) {
            JOptionPane.showMessageDialog(null, " LLene los datos de institucion y municipio");
        } else {
            if (anioObj == null) {
                JOptionPane.showMessageDialog(null, "No hay datos para la sede seleccionada");
                return; // Salir del método si algún JComboBox es null
            }
        }
        String anio = String.valueOf(anioObj);
        String Institucion = String.valueOf(InstitucionObj);
        String municipio = String.valueOf(municipioObj);
        String nucleo = String.valueOf(nucleoObje);
        List<ModeloInforme> informes = consul.tablaInformeNucleo(Institucion, Integer.parseInt(anio), municipio, nucleo);


        tableModel2.setRowCount(0);
        if (!Institucion.isEmpty() && !municipio.isEmpty()) {
            for (ModeloInforme informe : informes) {
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

            // Calcular el total de la carga ambiental
            for (int row = 0; row < tableModel2.getRowCount(); row++) {
                String valor = tableModel2.getValueAt(row, 6).toString();
                if (!valor.isEmpty()) {
                    try {
                        double valor1 = Double.parseDouble(valor);
                        total += valor1;
                    } catch (NumberFormatException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
            }
            view.total.setText(String.valueOf(total));
        } else {
            JOptionPane.showMessageDialog(null, "No hay datos para el municipio seleccionado.");
            tableModel2.setRowCount(0);
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

    private void anioBaseNucleo() {
        view.anio.removeAllItems(); // Limpiar el combo de años
        view.anio.addItem(" "); // Agregar un texto indicativo
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
        String nombre = String.valueOf(view.institucion.getSelectedItem());
        String municipio = String.valueOf(view.municipio.getSelectedItem());
        String nucleo = String.valueOf(view.comboNucleo.getSelectedItem());
        List<String> anioBaseNucleo = consultaNucleo.obtenerAnosBase(nombre, municipio, nucleo);
        for (String anioBase : anioBaseNucleo) {
            view.anio.addItem(anioBase);
        }

    }

    private void BotonInicio() {
        ControladoInicio control = new ControladoInicio(ins, modUser, m);
        control.inicio();
        view.dispose();
    }

    private void vistaPerfil() {
        Conexion conn = new Conexion();
        Perfil per = new Perfil();
        ConsultaUsuario cons = new ConsultaUsuario(conn);
        System.out.println(ins.getNombreInstitucion());
        PerfilCOntrolador control = new PerfilCOntrolador(modUser, per, ins, m, cons);

        control.Iniciar();
        per.setVisible(true);
        view.dispose();
    }

    private void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        EmisionModelo mod = new EmisionModelo();
        ConsultasEmision consul = new ConsultasEmision();
        EmisionControlador controlador = new EmisionControlador(mod, consul, emisionView, ins, m, modUser);
        controlador.iniciar();
        emisionView.setVisible(true);
        view.dispose();
    }

    private void vistaCalcular() {
        Conexion con = new Conexion();
        Calcular viewCal = new Calcular();
        CalcularModelo mod = new CalcularModelo();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        CalcularControlador controlador = new CalcularControlador(mod, consul, viewCal, ins, consultaUsuario, modUser, m);
        controlador.iniciar();
        viewCal.setVisible(true);
        view.dispose();
    }

    private void vistaGraficoPrincipal() {
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos viewGraf = new Graficos();
        GraficorModelo mod = new GraficorModelo();
        InstitucionModelo modelo = new InstitucionModelo();
        GraficoControlador contro = new GraficoControlador(mod, consul, viewGraf, modelo, m, modUser, ins);
        contro.iniciar();
        viewGraf.setVisible(true);
        view.dispose();
    }

    private void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar viewGraf = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        GraficoCompararModelo mod = new GraficoCompararModelo();
        ComparaInstitucion contro = new ComparaInstitucion(mod, consultas, comIns, viewGraf, ins, m, modUser);
        contro.iniciar();
        view.dispose();
    }

    private void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        TendenciaModelo mod = new TendenciaModelo();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia viewGraf = new GraficoTendencia();
        TendenciaControlador control = new TendenciaControlador(mod, consult, viewGraf, m, ins, modUser);
        viewGraf.setVisible(true);
        control.iniciar();
        view.dispose();
    }

    private void vistaReducir() {
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(ins, consul, vista, m, modUser);
        redu.Iniciar();
        view.dispose();
    }

    private void vistaActualizarInstitucion() {
        ConsultasInstitucion consul = new ConsultasInstitucion();
        VerDatosInstitucion view2 = new VerDatosInstitucion();
        InstitucionModelo mod = new InstitucionModelo();
        InstitucionControlador control = new InstitucionControlador(ins, m, view2, consul, mod, modUser);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();


    }

    private void vistaVerPerfiles() {
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);

        VerPerfilesControlador verControl = new VerPerfilesControlador(modUser, ins, m, verPerfiles, consul);
        verControl.Iniciar();
        view.dispose();
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
                view.municipio.removeAllItems(); // Limpiar el combo de municipio
                view.comboNucleo.removeAllItems(); // Limpiar el combo de núcleos
                cargarMunicipio(); // Cargar municipios para la institución seleccionada
                cargarAnioBase(); // Cargar el año base para la institución
            }
            Conexion conn = new Conexion();
            ConsultaNucleo consultaNucleo;
            consultaNucleo = new ConsultaNucleo(conn);
            String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
            if (consultaNucleo.TieneNucleo(nombreInstitucion) >= 1) {
                view.nucleo.setVisible(true);
                view.comboNucleo.setVisible(true);
                view.anio.removeAllItems();
            } else {
                view.comboNucleo.setVisible(false);
                view.nucleo.setVisible(false);
                cargarAnioBase();
            }
        });

        // ActionListener para el combo box de municipio
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

        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);
        this.view.verInstitucion.addActionListener(this::actionPerformed);

    }

}
