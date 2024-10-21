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
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TendenciaControlador {
    private final TendenciaModelo mod;
    private final ConsultasTendencias consul;
    private final GraficoTendencia view;
    private final Municipio m;
    private final InstitucionModelo ins;
    private final Usuario modUser;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public TendenciaControlador(TendenciaModelo mod, ConsultasTendencias consul, GraficoTendencia view,
                                Municipio m, InstitucionModelo ins, Usuario modUse) {
        this.mod = mod;
        this.consul = consul;
        this.view = view;
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
        switch (usuario) {
            case "Administrador":
                cargarInstitucion();
                cargarMunicipio();
                System.out.println("Estoy en docente");
                view.setTitle("Grafico Historico");
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                view.VerPerfiles.setVisible(false);
                view.setLocationRelativeTo(null);
                break;
            case "SuperAdmin":
                view.setTitle("Grafico Tendencia");
                view.setLocationRelativeTo(null);
                cargarInstitucion();
                cargarMunicipio();
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

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.verButton) {
            if (view.noCheckBox.isSelected()) {
                GraficoSinNucleo();
            }
            if (view.siCheckBox.isSelected()) {
                GraficoConNucleo();
            }
        }
        if (e.getSource() == view.descargarButton) {
            if (view.noCheckBox.isSelected()) {
                DocumentoSinNucleo();
            }
            if (view.siCheckBox.isSelected()) {
                DocumentoConNucleo();
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
        if (e.getSource() == view.RegistrarEmisión) {
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

    private void ExportarGraficos(DefaultCategoryDataset a, File c) {
        Document document = new Document(PageSize.A3);
        try {

            JFreeChart chartDataset = crearGraficoTendencia(a);
            ByteArrayOutputStream chartStreamDataset = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamDataset, chartDataset.createBufferedImage(600, 450));
            byte[] chartBytesDataset = chartStreamDataset.toByteArray();
            PdfWriter.getInstance(document, new FileOutputStream(c));
            document.open();
            List<ModeloInforme> z = consul.datos(String.valueOf(view.institucion.getSelectedItem()), String.valueOf(view.municipio.getSelectedItem()));
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
            for (ModeloInforme info : z) {
                String m = info.getMunicipio();
                Paragraph municipio = new Paragraph("Municipio: " + m);
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

    private void ExportarGraficosDeNucleo(DefaultCategoryDataset a, File c) {
        Document document = new Document(PageSize.A3);
        try {

            JFreeChart chartDataset = crearGraficoTendencia(a);
            ByteArrayOutputStream chartStreamDataset = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamDataset, chartDataset.createBufferedImage(600, 450));
            byte[] chartBytesDataset = chartStreamDataset.toByteArray();
            PdfWriter.getInstance(document, new FileOutputStream(c));
            document.open();
            List<ModeloInforme> z = consul.datos(String.valueOf(view.institucion.getSelectedItem()), String.valueOf(view.municipio.getSelectedItem()));
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
            for (ModeloInforme info : z) {
                String m = info.getMunicipio();
                Paragraph municipio = new Paragraph("Municipio: " + m);
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
            Paragraph nucleo = new Paragraph("Nucleo: " + nombreN);
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

    private void GraficoSinNucleo() {
        view.image.setVisible(false);
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String nombreMunicipio = String.valueOf(view.municipio.getSelectedItem());
        if (!nombreInstitucion.isEmpty() && !nombreMunicipio.isEmpty()) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            List<TendenciaModelo> mod = consul.getNombre(nombreInstitucion, nombreMunicipio);
            for (TendenciaModelo dato : mod) {
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

    private void GraficoConNucleo() {
        view.image.setVisible(false);
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String nombreMunicipio = String.valueOf(view.municipio.getSelectedItem());
        String nombreN = String.valueOf(view.comboNucleo.getSelectedItem());
        if (!nombreInstitucion.isEmpty() && !nombreMunicipio.isEmpty() && !nombreN.isEmpty()) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            List<TendenciaModelo> mod = consul.getNombrePorNucleo(nombreInstitucion, nombreMunicipio, nombreN);
            for (TendenciaModelo dato : mod) {
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

    private void DocumentoSinNucleo() {
        view.image.setVisible(false);
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String nombreMunicipio = String.valueOf(view.municipio.getSelectedItem());
        if (!nombreInstitucion.isEmpty() && !nombreMunicipio.isEmpty()) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            List<TendenciaModelo> mod = consul.getNombre(nombreInstitucion, nombreMunicipio);
            for (TendenciaModelo dato : mod) {
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
            String Nombre = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String b = "GraficoHistoricoDeLa" + view.institucion.getSelectedItem().toString() + " " + Nombre;
            //codgio para eligir el lugar en donde se descarga el archivo
            JFileChooser fileChooser = new JFileChooser();

            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            fileChooser.setFileFilter(filter);

            fileChooser.setDialogTitle("Guardar archivo PDF");
            fileChooser.setSelectedFile(new File(b + "GraficoHistórico" + ".pdf"));
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = fileChooser.getSelectedFile();
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());
                ExportarGraficos(dataset, file);
            }
        }
    }

    private void DocumentoConNucleo() {
        view.image.setVisible(false);
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String nombreMunicipio = String.valueOf(view.municipio.getSelectedItem());
        String nombreN = String.valueOf(view.comboNucleo.getSelectedItem());
        if (!nombreInstitucion.isEmpty() && !nombreMunicipio.isEmpty() && !nombreN.isEmpty()) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            List<TendenciaModelo> mod = consul.getNombrePorNucleo(nombreInstitucion, nombreMunicipio, nombreN);
            for (TendenciaModelo dato : mod) {
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
            String Nombre = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String b = "GraficoHistoricoDeLa" + view.institucion.getSelectedItem().toString() + " " + Nombre + " " + "delNucelo" + nombreN;
            //codgio para eligir el lugar en donde se descarga el archivo
            JFileChooser fileChooser = new JFileChooser();

            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            fileChooser.setFileFilter(filter);

            fileChooser.setDialogTitle("Guardar archivo PDF");
            fileChooser.setSelectedFile(new File(b + "GraficoHistórico" + ".pdf"));
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = fileChooser.getSelectedFile();
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());
                ExportarGraficosDeNucleo(dataset, file);
            }
        }
    }

    private void cargarInstitucion() {
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

    private void vistaCalcular() {
        Conexion con = new Conexion();
        Vistas.Calcular viewCal = new Calcular();
        CalcularModelo mod = new CalcularModelo();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        CalcularControlador controlador = new CalcularControlador(mod, consul, viewCal, ins, consultaUsuario, modUser, m);
        controlador.iniciar();
        viewCal.setVisible(true);
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

    private void vistaInforme() {
        Conexion con = new Conexion();
        InstitucionModelo mod2 = new InstitucionModelo();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloInforme mod = new ModeloInforme();
        Informe viewInfo = new Informe();
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorInforme contro = new ControladorInforme(viewInfo, mod, consul, m, ins, modUser);
        contro.iniciar();
        viewInfo.setVisible(true);
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

    private void vistaReducir() {
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(ins, consul, vista, m, modUser);
        redu.Iniciar();
        view.dispose();
    }

    private void vistaActualizarInstitucion() {
        Conexion con = new Conexion();
        ConsultasInstitucion consul = new ConsultasInstitucion();
        RegistrarInstitucion view2 = new RegistrarInstitucion();
        InstitucionModelo mod2 = new InstitucionModelo();
        InstitucionControlador control = new InstitucionControlador(ins, m, view2, consul, mod2, modUser);
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
        this.view.verButton.addActionListener(this::actionPerformed);
        this.view.descargarButton.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.perfil.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.RegistrarEmisión.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.institucion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.institucion.getItemCount() > 0) {
                    cargarMunicipio();

                }
            }
        });
        this.view.municipio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.municipio.getItemCount() > 0) {
                    view.comboNucleo.removeAllItems(); // Limpiar el combo de núcleos si está visible
                    if (view.siCheckBox.isSelected()) {
                        cargarNucleosExistentes(); // Cargar núcleos si el checkbox está seleccionado
                    }

                }
            }
        });
        this.view.siCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.siCheckBox.isSelected()) {
                    view.comboNucleo.setVisible(true);
                    view.Nucleo.setVisible(true);
                    view.noCheckBox.setEnabled(false);
                    cargarNucleosExistentes();
                }
                if (!view.siCheckBox.isSelected()) {
                    view.Nucleo.setVisible(false);
                    view.comboNucleo.setVisible(false);
                    view.noCheckBox.setEnabled(true);
                }
            }
        });
        this.view.noCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.noCheckBox.isSelected()) {
                    view.Nucleo.setVisible(false);
                    view.comboNucleo.setVisible(false);
                    view.siCheckBox.setEnabled(false);


                } else {
                    view.siCheckBox.setEnabled(true);

                }
            }
        });
        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);

    }

}

