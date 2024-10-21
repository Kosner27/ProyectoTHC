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
        switch (usuario){
            case "Administrador" :
                cargarInstitucion();
                cargarAnioBase();
                cargarMunicipio();
                view.setTitle("Seleccionar Instituciones");
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                view.VerPerfiles.setVisible(false);
                view.setLocationRelativeTo(null);
                break;
            case "SuperAdmin" :
                view.setTitle("Seleccionar Instituciones");
                view.setLocationRelativeTo(null);
                cargarInstitucion();
                cargarAnioBase();
                cargarMunicipio();
                break;
            default:
                JOptionPane.showMessageDialog(null, "Usuario no definido en el sistema");
                break;

        }
        view.setTitle("Seleccionar Instituciones");
        view.setLocationRelativeTo(null);
        cargarInstitucion();
        cargarAnioBase();
        GraficoPrincipal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vistaGraficoPrincipal();
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
        if (e.getSource() == view.añadirButton) {
            String nombreInstitucion =String.valueOf( view.institucion.getSelectedItem());
            String nombreMunicipio = String.valueOf(view.Municipio.getSelectedItem());
            if (!nombreInstitucion.isEmpty()) {
                DefaultTableModel tableModel = (DefaultTableModel) view.Instituciones.getModel();

                System.out.println("Nombre de la institución: " + nombreInstitucion);
                System.out.println("Municipio del usuario: " + view.Municipio.getSelectedItem().toString());
                // Obtener datos filtrados basados en el usuario y la institución
                List<InstitucionModelo> datos = consul.llenarTabla(nombreInstitucion, nombreMunicipio);

                // Verificar los datos obtenidos
                System.out.println("Datos obtenidos para la institución " + nombreInstitucion + ": " + datos);

                for (InstitucionModelo dato : datos) {
                    Object[] rowData = {
                            dato.getNombreInstitucion(),
                            dato.getNit(),
                            dato.getDepartamento(),
                            dato.getMunicipio()
                    };
                    tableModel.addRow(rowData);
                }

                view.Instituciones.setModel(tableModel);
                view.Instituciones.setVisible(true);
                view.Contenedor.setVisible(true);


            }
        }
        if (e.getSource() == view.compararButton) {

            String anio = String.valueOf(view.anio.getSelectedItem());
            String alcance = String.valueOf(view.alcance.getSelectedItem());

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
                String b = "CompararInstitucionesCon"+view.institucion.getSelectedItem().toString() + fecha;
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
                JOptionPane.showMessageDialog(view,
                        "Por favor seleccione una institución, un año y un alcance antes de proceder.",
                        "Datos faltantes",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
        if (e.getSource() == view.inicioButton) {
            BotonInicio();
        }
        if (e.getSource() == view2.inicioButton) {
            BotonInicio();
        }if(e.getSource() == view.perfil){
            vistaPerfil();
        }if(e.getSource()==view.Calcular){
            vistaCalcular();
        }if(e.getSource()==view.RegistrarEmisión){
            vistaRegistrarEmision();
        }if(e.getSource()== view.Informes){
            vistaInforme();
        }if(e.getSource()==view.Reducir){
            vistaReducir();
        }if(e.getSource()==view.RegistrarInstitucion){
            vistaActualizarInstitucion();
        }if(e.getSource()==view.VerPerfiles){
            vistaVerPerfiles();
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
        view2.PanelGrafico.add(panel, BorderLayout.CENTER);
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
        ResultSet st = null;
        Connection conn = conexion.getConection();
        String sql = " Select  ei.anioBase from emisioninstitucion ei inner join institucion i on ei.idInsititucion=i.idInstitucionAuto inner join emision e on ei.idEmision = e.idEmision where i.NombreInstitucion = ?  group by anioBase";
        String nombre = view.institucion.getSelectedItem().toString();
        view.anio.addItem(" ");
        view.anio.removeAllItems();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nombre);
                st = ps.executeQuery();
                while (st.next()) {
                    String anioBase = st.getString(1);
                    view.anio.addItem(" ");
                    view.anio.addItem(anioBase);
                }
                if (view.anio.getItemCount() > 0) {
                    view.anio.setSelectedIndex(0);
                } else {
                    // Manejar el caso en el que el JComboBox está vacío
                    System.out.println("El JComboBox año está vacío");
                }

                st.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void cargarMunicipio() {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        view.Municipio.removeAllItems(); // Limpiar los elementos del JComboBox
        view.Municipio.addItem("");

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
                                view.Municipio.addItem(nombreMunicipio);
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
    public void BotonInicio (){
        ControladoInicio control = new ControladoInicio(ins,user, m);
        control.inicio();
        view.dispose();
        view2.dispose();
    }
    public void vistaPerfil(){
        Conexion conn = new Conexion();
        Perfil per = new Perfil();
        ConsultaUsuario cons = new ConsultaUsuario(conn);
        PerfilCOntrolador control = new PerfilCOntrolador(user, per, ins, m, cons);
        control.Iniciar();
        per.setVisible(true);
        view.dispose();
    }
    public void vistaCalcular(){
        Conexion con = new Conexion();
        Calcular viewCal = new Calcular();
        CalcularModelo mod = new CalcularModelo();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario= new ConsultaUsuario(con);
        CalcularControlador controlador = new CalcularControlador(mod,consul,viewCal,ins,consultaUsuario,user,m);
        controlador.iniciar();
        viewCal.setVisible(true);
        view.dispose();
    }
    public void vistaRegistrarEmision(){
        Emision emisionView = new Emision();
        EmisionModelo mod = new EmisionModelo();
        ConsultasEmision consul = new ConsultasEmision();
        EmisionControlador controlador = new EmisionControlador(mod,consul,emisionView,ins,m,user);
        controlador.iniciar();
        emisionView.setVisible(true);
        view.dispose();
    }
    public void vistaInforme(){
        Conexion con = new Conexion();
        InstitucionModelo mod2 = new InstitucionModelo();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloInforme mod = new ModeloInforme();
        Informe viewInfo = new Informe();
        ConsultaUsuario consultaUsuario= new ConsultaUsuario(con);
        ControladorInforme contro = new ControladorInforme(viewInfo,mod,consul,m,ins,user);
        contro.iniciar();
        viewInfo.setVisible(true);
        view.dispose();
    }
    public void vistaGraficoPrincipal(){
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos viewGraf = new Graficos();
        GraficorModelo mod = new GraficorModelo();
        InstitucionModelo modelo = new InstitucionModelo();
        GraficoControlador contro = new GraficoControlador(mod,consul, viewGraf,modelo,m,user,ins);
        contro.iniciar();
        viewGraf.setVisible(true);
        view.dispose();
    }
    public void vistaGraficoHistorico(){
        Conexion con = new Conexion();
        TendenciaModelo mod = new TendenciaModelo();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia viewGraf = new GraficoTendencia();
        TendenciaControlador control = new TendenciaControlador(mod,consult,viewGraf,m,ins,user);
        viewGraf.setVisible(true);
        control.iniciar();
        view.dispose();
    }
    public void vistaReducir(){
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(ins,consul, vista,m,user);
        redu.Iniciar();
        view.dispose();
    }
    public void vistaActualizarInstitucion(){
        Conexion con = new Conexion();
        ConsultasInstitucion consul = new ConsultasInstitucion();
        RegistrarInstitucion view2 = new RegistrarInstitucion();
        InstitucionModelo mod = new InstitucionModelo();
        InstitucionControlador control = new InstitucionControlador(ins, m,view2, consul,mod,user);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();



    }
    public void vistaVerPerfiles(){
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);

        VerPerfilesControlador verControl = new VerPerfilesControlador(user,ins, m,verPerfiles,consul);
        verControl.Iniciar();
        view.dispose();
    }
    public void Listeners(){
        this.view.añadirButton.addActionListener(this::actionPerformed);
        this.view.compararButton.addActionListener(this::actionPerformed);
        this.view2.descargarButton.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view2.inicioButton.addActionListener(this::actionPerformed);
        this.view.perfil.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.RegistrarEmisión.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.institucion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.institucion.getItemCount() > 0) {
                    cargarAnioBase();
                    cargarMunicipio();
                }
            }
        });
        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);

    }

}
