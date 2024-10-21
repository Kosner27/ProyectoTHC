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
        switch (modUser.getTipoUsuario()){
            case "Invitado" :
                view.setTitle("Reducir");
                cargarAnioBase();
                cargarMunicipio();
                view.setVisible(true);
                view.Institucio.setText(ins.getNombreInstitucion());
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.RegistrarInstitucion.setVisible(false);
                view.VerPerfiles.setVisible(false);
                view.RegistrarEmisión.setVisible(false);
                view.Calcular.setVisible(false);
                view.perfil.setVisible(true);
                view.setLocationRelativeTo(null);
                break;
            case "Administrador" :
                view.setTitle("Reducir");
                cargarAnioBase();
                cargarMunicipio();
                System.out.println(modUser.getTipoUsuario());
                view.setVisible(true);
                view.Institucio.setText(ins.getNombreInstitucion());
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.setSize(3000,600);
                break;
            case "SuperAdmin":
                view.setTitle("Reducir");
                cargarAnioBase();
                cargarMunicipio();
                view.setVisible(true);
                view.Institucio.setText(ins.getNombreInstitucion());
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.setSize(3000,600);
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
        if (e.getSource() == view.generarPlanDeAccion) {
            if(view.NOCheckBox.isSelected()){
                InsertarEncajatexto();
            }if(view.SICheckBox.isSelected()){
                InsertarEncajatextoConNucleo();
            }

        }
        if (e.getSource() == view.Descargar) {
            if(view.NOCheckBox.isSelected()){
               descargarSinNucleo();
            }

            if(view.SICheckBox.isSelected()){
                descargarConNucleo();
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
        if (e.getSource() == view.RegistrarEmisión) {
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
        Conexion conexion = new Conexion();
        ResultSet st = null;
        Connection conn = conexion.getConection();
        String sql = " Select  ei.anioBase from emisioninstitucion ei inner join institucion i on ei.idInsititucion=i.idInstitucionAuto inner join emision e on ei.idEmision = e.idEmision where i.NombreInstitucion = ?  group by anioBase";
        String nombre = view.Institucio.getText();
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
        view.municipio.removeAllItems(); // Limpiar los elementos del JComboBox
        view.municipio.addItem("");

        // Obtener el departamento seleccionado directamente del JComboBox
        String Institucion = view.Institucio.getText();

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
        String nombreInstitucion = view.Institucio.getText();
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

    private void descargarSinNucleo(){
        String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
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

    private void descargarConNucleo(){
        String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String nucleo = Objects.requireNonNull(view.comboNucleo.getSelectedItem()).toString();
        String b = view.Institucio.getText() + fecha+ "DelNucleo"+nucleo;
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

    public void InsertarEncajatexto() {
        if (!Objects.requireNonNull(view.anio.getSelectedItem()).toString().isEmpty() && !Objects.requireNonNull(view.municipio.getSelectedItem()).toString().isEmpty()) {
            String nombreInstitucion = view.Institucio.getText();
            String anioBase = view.anio.getSelectedItem().toString();
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

    public void InsertarEncajatextoConNucleo() {
        if (!Objects.requireNonNull(view.anio.getSelectedItem()).toString().isEmpty() && !Objects.requireNonNull(view.municipio.getSelectedItem()).toString().isEmpty()) {
            String nombreInstitucion = view.Institucio.getText();
            String anioBase = view.anio.getSelectedItem().toString();
            String nombreN = view.comboNucleo.getSelectedItem().toString();

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
            String nombre = view.Institucio.getText();
            Paragraph nombreInstitucion = new Paragraph("Nombre de la Institucion: " + nombre);
            nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
            document.add(new Paragraph(("")));
            document.add(nombreInstitucion);
            document.add(new Paragraph("  "));
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
            String nombre = view.Institucio.getText();
            Paragraph nombreInstitucion = new Paragraph("Nombre de la Institucion: " + nombre);
            nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
            document.add(new Paragraph(("")));
            document.add(nombreInstitucion);
            document.add(new Paragraph("  "));
            String m = view.municipio.getSelectedItem().toString();
            Paragraph municipio = new Paragraph("Municipio: " + m);
            municipio.setAlignment(Element.ALIGN_LEFT);
            document.add(municipio);
            document.add(new Paragraph("  "));
            document.add(new Paragraph(content));

            String nucleo= view.comboNucleo.getSelectedItem().toString();
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
        RegistrarInstitucion view2 = new RegistrarInstitucion();
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
        this.view.RegistrarEmisión.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.Institucio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!view.Institucio.getText().isEmpty()) {
                    cargarAnioBase();
                    cargarMunicipio();
                    cargarNucleosExistentes();
                }
            }
        });
        this.view.SICheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.SICheckBox.isSelected()) {
                    view.comboNucleo.setVisible(true);
                    view.Nucleo.setVisible(true);
                    view.NOCheckBox.setEnabled(false);
                    cargarNucleosExistentes();
                }
                if (!view.SICheckBox.isSelected()) {
                    // Si se selecciona "No", ocultar el combo box de núcleos
                    view.Nucleo.setVisible(false);
                    view.comboNucleo.setVisible(false);
                    view.NOCheckBox.setEnabled(true);
                }


                cargarAnioBase();
            }
        });
        this.view.NOCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.NOCheckBox.isSelected()) {
                    view.Nucleo.setVisible(false);
                    view.comboNucleo.setVisible(false);
                    view.SICheckBox.setEnabled(false);
                    cargarAnioBase();
                }
                if (!view.NOCheckBox.isSelected()) {
                    view.SICheckBox.setEnabled(true);
                }
            }
        });
        this.view.generarPlanDeAccion.addActionListener(this::actionPerformed);
        this.view.Descargar.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
    }
}


