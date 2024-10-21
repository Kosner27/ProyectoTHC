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
                System.out.println("Estoy en docente");
                view.setTitle("Informe");
                view.setLocationRelativeTo(null);
                view.Emisiones.setRowSorter(sorter);
                view.setVisible(true);
                view.VerPerfiles.setVisible(false);
                view.setLocationRelativeTo(null);
                break;
            case "SuperAdmin":
                view.setTitle("Informe");
                view.setLocationRelativeTo(null);
                cargarInstitucion();
                cargarAnioBase();
                cargarMunicipio();
                cargarNucleosExistentes();
                sorter = new TableRowSorter<>(tableModel);
                view.Emisiones.setRowSorter(sorter);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Usuario no definido en el sistema");
                break;

        }
        GraficoPrincipal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vistaGraficoPrincipal();
            }
        });
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

    private void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.buscarButton) {
            if (view.noCheckBox.isSelected()) {
                LlenarTablaSinNucleo();
            }
            if (view.siCheckBox.isSelected()) {
                LlenarTablaConNucleo();
            }
        }
        if (e.getSource() == view.descargarButton) {
            if (view.noCheckBox.isSelected()) {
                LlenarDocumentoSinNucleo();
            }
            if (view.siCheckBox.isSelected()) {
                LlenarDocumentoConNucleo();
            }
        }
        if (e.getSource() == view.inicioButton) {
            BotonInicio();
        }
        if (e.getSource() == view.perfil) {
            vistaPerfil();
        }
        if (e.getSource() == view.RegistrarEmisión) {
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
    }
    private void LlenarDocumentoSinNucleo(){
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

    private void LlenarDocumentoConNucleo(){
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

    private void cargarInstitucion() {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        ResultSet rs = null;
        String sql = "CALL Seleccionarinstitucion()";
        view.institucion.removeAllItems();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                rs = ps.executeQuery();
                view.institucion.removeAllItems();
                view.institucion.addItem("");
                while (rs.next()) {
                    String nombre = rs.getString("NombreInstitucion");
                    view.institucion.addItem(nombre);
                }
                if (view.institucion.getItemCount() > 0) {
                    view.institucion.setSelectedIndex(0);
                } else {
                    // Manejar el caso en el que el JComboBox está vacío
                    System.out.println("El JComboBox institucion está vacío");
                }

                rs.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarAnioBase() {
        view.anio.removeAllItems(); // Limpiar el combo de años
        view.anio.addItem(" "); // Agregar un texto indicativo

        String sql = "SELECT DISTINCT ei.anioBase FROM emisioninstitucion ei " +
                "INNER JOIN institucion i ON ei.idInsititucion = i.idInstitucionAuto " +
                "INNER JOIN emision e ON ei.idEmision = e.idEmision " +
                "INNER JOIN municipioinstitiucion mi ON i.idInstitucionAuto = mi.IdInstitucion " +
                "INNER JOIN municipio m ON mi.IdMuncipio = m.IdMunicipio " +
                "WHERE i.NombreInstitucion = ? AND m.NombreMunicipio = ? " +
                "GROUP BY ei.anioBase";

        String nombre = String.valueOf(view.institucion.getSelectedItem());
        String municipio = String.valueOf(view.municipio.getSelectedItem());

        Conexion conexion = new Conexion();
        try (Connection conn = conexion.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, municipio);

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

    private void cargarMunicipio() {
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
        view.comboNucleo.removeAllItems();
        view.comboNucleo.addItem(" ");
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
                        view.comboNucleo.addItem(nombreNucleo);
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
                "ORDER BY en.anioBase;";

        String nombre = String.valueOf(view.institucion.getSelectedItem());
        String municipio = String.valueOf(view.municipio.getSelectedItem());
        String nucleo = String.valueOf(view.comboNucleo.getSelectedItem());

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

    private void BotonInicio() {
        ControladoInicio control = new ControladoInicio(ins, modUser, m);
        control.inicio();
        view.dispose();
    }

    private void vistaPerfil() {
        Conexion conn = new Conexion();
        Perfil per = new Perfil();
        ConsultaUsuario cons = new ConsultaUsuario(conn);
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
        RegistrarInstitucion view2 = new RegistrarInstitucion();
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

    private void Listeners() {
        this.view.buscarButton.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.descargarButton.addActionListener(this::actionPerformed);
        this.view.perfil.addActionListener(this::actionPerformed);
        this.view.RegistrarEmisión.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.noCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.noCheckBox.isSelected()) {
                    view.nucleo.setVisible(false);
                    view.comboNucleo.setVisible(false);
                    view.siCheckBox.setEnabled(false);
                }
                if (!view.noCheckBox.isSelected()) {
                    view.siCheckBox.setEnabled(true);
                }
            }
        });
        this.view.siCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.siCheckBox.isSelected()) {
                    view.comboNucleo.setVisible(true);
                    view.nucleo.setVisible(true);
                    view.noCheckBox.setEnabled(false);
                    cargarNucleosExistentes();
                }
                if (!view.siCheckBox.isSelected()) {
                    // Si se selecciona "No", ocultar el combo box de núcleos
                    view.nucleo.setVisible(false);
                    view.comboNucleo.setVisible(false);
                    view.noCheckBox.setEnabled(true);
                }


                cargarAnioBase();
            }
        });

// ActionListener para el combo box de institución
        this.view.institucion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.institucion.getItemCount() > 0) {
                    view.municipio.removeAllItems(); // Limpiar el combo de municipio
                    view.comboNucleo.removeAllItems(); // Limpiar el combo de núcleos
                    cargarMunicipio(); // Cargar municipios para la institución seleccionada
                    cargarAnioBase(); // Cargar el año base para la institución
                }
            }
        });

        // ActionListener para el combo box de municipio
        this.view.municipio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.municipio.getItemCount() > 0) {
                    view.comboNucleo.removeAllItems(); // Limpiar el combo de núcleos si está visible
                    if (view.siCheckBox.isSelected()) {
                        cargarNucleosExistentes(); // Cargar núcleos si el checkbox está seleccionado
                    }
                    cargarAnioBase(); // Cargar el año base
                }
            }
        });
        this.view.comboNucleo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.comboNucleo.getItemCount() > 0) {
                    anioBaseNucleo(); // Cargar el año base basado en la selección del núcleo
                }
            }
        });

        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);

    }
}
