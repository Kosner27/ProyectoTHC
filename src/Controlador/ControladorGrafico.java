package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class ControladorGrafico {
    private final GraficorModeloInstitucion graficorModelo;
    private final GraficoConsulta graficoConsulta;
    private final Graficos view;
    private final ModeloInstitucion modeloInstitucion1;
    private final ModeloMunicipio modeloMunicipio;
    private final ModeloUsuario modeloUsuario;
    private final ModeloInstitucion modeloInstitucion;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public ControladorGrafico(GraficorModeloInstitucion graficorModelo,
                              GraficoConsulta graficoConsulta, Graficos view,
                              ModeloInstitucion modeloInstitucion1, ModeloMunicipio modeloMunicipio,
                              ModeloUsuario modeloUsuario, ModeloInstitucion modeloInstitucion) {
        this.graficorModelo = graficorModelo;
        this.modeloInstitucion1 = modeloInstitucion1;
        this.graficoConsulta = graficoConsulta;
        this.view = view;
        this.modeloMunicipio = modeloMunicipio;
        this.modeloInstitucion = modeloInstitucion;
        this.modeloUsuario = modeloUsuario;
        Listeners();
    }

    /**
     * Método para inicializar la vista dependiendo del tipo de modeloUsuario.
     * Se ajustan los elementos visibles y las acciones de acuerdo al tipo de modeloUsuario (Administrador, Superadmin, Invitado).
     */
    public void iniciar() {
        view.Graficos.add(GraficoPrincipal);
        view.Graficos.add(GraficosCompararInstitucion);
        view.Graficos.add(GraficoHistorico);
        String usuario = this.modeloUsuario.getTipoUsuario();
        switch (usuario) {
            case "Administrador":
                cargarInstitucion();
                cargarAnioBase();
                cargarMunicipio();
                System.out.println("Estoy en docente");
                view.setTitle("Grafico principal");
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                view.VerPerfiles.setVisible(true);
                view.verInstitucion.setVisible(false);
                view.setLocationRelativeTo(null);
                break;
            case "Superadmin":
                view.setTitle("Grafico principal");
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                cargarInstitucion();
                cargarAnioBase();
                cargarMunicipio();
                break;
            case "Invitado":
                view.RegistrarInstitucion.setVisible(false);
                view.VerPerfiles.setVisible(false);
                view.RegistrarEmision.setVisible(false);
                view.Calcular.setVisible(false);
                view.verInstitucion.setVisible(false);
                view.perfil.setVisible(true);
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                cargarInstitucion();
                cargarAnioBase();
                cargarMunicipio();
                break;
            default:
                JOptionPane.showMessageDialog(null, "ModeloUsuario no definido en el sistema");
                break;

        }
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

    /**
     * Maneja los eventos de acción generados por los botones y componentes de la vista.
     * Este método se ejecuta cuando un modeloUsuario interactúa con los botones y componentes de la interfaz gráfica.
     * En función del botón que se haya presionado, se ejecuta el método correspondiente para gestionar la acción.
     *
     * @param e El evento de acción que contiene información sobre el componente que generó el evento.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.buscarButton) {
            if(!view.siNucleo.isSelected() && !view.noNucleo.isSelected()){
                graficosSinNucleo();

            }


            if (view.siNucleo.isSelected()) {
                graficosConNucleo();
            }
            if(view.siGeneral.isSelected()){
                graficosConNucleoSumaTodosNucleos();
            }
        }
        if (e.getSource() == view.Descargar) {

            if(view.comboxNucleo.isVisible()){
                DescargarConNucleo();
            }if(!view.comboxNucleo.isVisible() && !view.siGeneral.isSelected()){
                DescargasSinNucleo();
            }if(view.siGeneral.isSelected()){
                DescargaTodosLosNucleos();
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
        if(e.getSource() == view.verInstitucion){
            vistaVerInstitucion();
        }
    }

    /**
     * Método encargado de generar y mostrar gráficos circulares basados en los datos de alcance
     * y fuente para una institución, año base y modeloMunicipio seleccionados.
     * <p>
     * El método valida que los valores seleccionados no estén vacíos, consulta los datos correspondientes,
     * los procesa y genera gráficos utilizando la biblioteca JFreeChart.
     * </p>
     */
    private void graficosSinNucleo() {
        // Obtener las selecciones de la interfaz de modeloUsuario
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String anioBaseString = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = String.valueOf(view.municipio.getSelectedItem());

        // Validar que los valores seleccionados no estén vacíos
        if (!nombreInstitucion.isEmpty() && !anioBaseString.isEmpty() && !NombreMuncipio.isEmpty()) {
            // Crear datasets para los gráficos
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();

            // Convertir el año base a un entero
            int anioBase = Integer.parseInt(anioBaseString);

            // Consultar los datos necesarios para los gráficos
            List<GraficorModeloInstitucion> datos = graficoConsulta.GraficoPorAlcance(nombreInstitucion, anioBase, NombreMuncipio);
            List<ModeloEmisionCalcular> datos2 = graficoConsulta.GraficoPorFuente(nombreInstitucion, anioBase, NombreMuncipio);

            // Procesar los datos para el gráfico de fuentes
            for (ModeloEmisionCalcular dato : datos2) {
                String fuente = dato.getNombreFuente();
                Double total = dato.getTotal1();

                if (fuente != null && total != null) {
                    datosFuente.setValue(fuente, total);
                } else {
                    System.out.println("Datos incorrectos: Fuente = " + fuente + ", Total = " + total);
                }
            }

            // Procesar los datos para el gráfico de alcances
            for (GraficorModeloInstitucion dato : datos) {
                String alcance = dato.getAlcance();
                Double total = dato.getTotal();

                if (alcance != null && total != null) {
                    datosAlcance.setValue(alcance, total);
                } else {
                    System.out.println("Alcance o Total es nulo: Alcance = " + alcance + ", Total = " + total);
                }
            }

            // Crear y mostrar los gráficos
            JFreeChart grafico = crearGraficoCircular(datosAlcance);
            JFreeChart grafico2 = crearGraficoFuente(datosFuente);

            mostrarGrafico(grafico); // Muestra el gráfico de alcance
            mostrarGrafico2(grafico2); // Muestra el gráfico de fuente
        } else {
            // Mostrar mensaje de error si no se seleccionaron valores requeridos
            JOptionPane.showMessageDialog(null, "Debe seleccionar una institución y un año base.");
        }
    }


    /**
     * Método encargado de generar y mostrar gráficos circulares basados en los datos de alcance
     * y fuente para una institución, año base, modeloMunicipio y núcleo seleccionados.
     * <p>
     * Este método valida las entradas proporcionadas por el modeloUsuario, consulta los datos correspondientes
     * a través de los métodos específicos de consulta para el núcleo, procesa los resultados y
     * genera gráficos utilizando la biblioteca JFreeChart.
     * </p>
     */
    private void graficosConNucleo() {
        // Obtener las selecciones de la interfaz de modeloUsuario
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String anioBaseString = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = String.valueOf(view.municipio.getSelectedItem());
        String nucleo = String.valueOf(view.comboxNucleo.getSelectedItem());

        // Validar que todas las selecciones no estén vacías
        if (!nombreInstitucion.isEmpty() && !anioBaseString.isEmpty() &&
                !NombreMuncipio.isEmpty() && !nucleo.isEmpty()) {

            // Crear datasets para los gráficos
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();

            // Convertir el año base a un entero
            int anioBase = Integer.parseInt(anioBaseString);

            // Consultar los datos necesarios para los gráficos con núcleo
            List<GraficorModeloInstitucion> datos = graficoConsulta.GraficoPorAlcanceNucleo(
                    nombreInstitucion, anioBase, NombreMuncipio, nucleo);
            List<ModeloEmisionCalcular> datos2 = graficoConsulta.GraficoPorFuenteNucleo(
                    nombreInstitucion, anioBase, NombreMuncipio, nucleo);

            // Procesar los datos para el gráfico de fuentes
            for (ModeloEmisionCalcular dato : datos2) {
                String fuente = dato.getNombreFuente();
                Double total = dato.getTotal1();

                if (fuente != null && total != null) {
                    datosFuente.setValue(fuente, total);
                } else {
                    System.out.println("Datos incorrectos: Fuente = " + fuente + ", Total = " + total);
                }
            }

            // Procesar los datos para el gráfico de alcances
            for (GraficorModeloInstitucion dato : datos) {
                String alcance = dato.getAlcance();
                Double total = dato.getTotal();

                if (alcance != null && total != null) {
                    datosAlcance.setValue(alcance, total);
                } else {
                    System.out.println("Alcance o Total es nulo: Alcance = " + alcance + ", Total = " + total);
                }
            }

            // Crear y mostrar los gráficos
            JFreeChart grafico = crearGraficoCircular(datosAlcance); // Gráfico de alcance
            JFreeChart grafico2 = crearGraficoFuente(datosFuente);   // Gráfico de fuente

            mostrarGrafico(grafico);   // Mostrar gráfico de alcance
            mostrarGrafico2(grafico2); // Mostrar gráfico de fuente
        }
    }


    /**
     * Método encargado de generar y mostrar gráficos circulares basados en los datos de alcance
     * y fuente para una institución, año base y modeloMunicipio seleccionados, sumando los datos de
     * todos los núcleos asociados.
     * <p>
     * Este método valida las entradas proporcionadas por el modeloUsuario, consulta los datos correspondientes
     * a través de métodos específicos que consideran la suma de todos los núcleos, procesa los resultados
     * y genera gráficos utilizando la biblioteca JFreeChart.
     * </p>
     */
    private void graficosConNucleoSumaTodosNucleos() {
        // Obtener las selecciones de la interfaz de modeloUsuario
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String anioBaseString = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = String.valueOf(view.municipio.getSelectedItem());

        // Validar que las selecciones no estén vacías
        if (!nombreInstitucion.isEmpty() && !anioBaseString.isEmpty() && !NombreMuncipio.isEmpty()) {
            // Convertir el año base a un entero
            int anioBase = Integer.parseInt(anioBaseString);

            // Crear datasets para los gráficos
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();

            // Consultar los datos necesarios para los gráficos (sumando todos los núcleos)
            List<GraficorModeloInstitucion> datos = graficoConsulta.GraficoPorAlcanceNucleoSumaTodosNucleos(
                    nombreInstitucion, anioBase, NombreMuncipio);
            List<ModeloEmisionCalcular> datos2 = graficoConsulta.GraficoPorFuenteNucleoSumaTodosNucleos(
                    nombreInstitucion, anioBase, NombreMuncipio);

            // Procesar los datos para el gráfico de fuentes
            for (ModeloEmisionCalcular dato : datos2) {
                String fuente = dato.getNombreFuente();
                Double total = dato.getTotal1();

                if (fuente != null && total != null) {
                    datosFuente.setValue(fuente, total);
                } else {
                    System.out.println("Datos incorrectos: Fuente = " + fuente + ", Total = " + total);
                }
            }

            // Procesar los datos para el gráfico de alcances
            for (GraficorModeloInstitucion dato : datos) {
                String alcance = dato.getAlcance();
                Double total = dato.getTotal();

                if (alcance != null && total != null) {
                    datosAlcance.setValue(alcance, total);
                } else {
                    System.out.println("Alcance o Total es nulo: Alcance = " + alcance + ", Total = " + total);
                }
            }

            // Crear y mostrar los gráficos
            JFreeChart grafico = crearGraficoCircular(datosAlcance); // Gráfico de alcance
            JFreeChart grafico2 = crearGraficoFuente(datosFuente);   // Gráfico de fuente

            mostrarGrafico(grafico);   // Mostrar gráfico de alcance
            mostrarGrafico2(grafico2); // Mostrar gráfico de fuente
        }
    }


    /**
     * Método encargado de generar datos para gráficos de alcance y fuente basados en la institución,
     * año base y modeloMunicipio seleccionados, y exportar los gráficos generados a un archivo PDF.
     * <p>
     * Este método valida las entradas proporcionadas por el modeloUsuario, consulta los datos correspondientes,
     * procesa los resultados y permite al modeloUsuario guardar los gráficos generados en un archivo PDF
     * utilizando un cuadro de diálogo de selección de archivos.
     * </p>
     */
    private void DescargasSinNucleo() {
        // Obtener las selecciones de la interfaz de modeloUsuario
        String nombreInstitucion = view.institucion.getSelectedItem().toString();
        String anioBaseString = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = view.municipio.getSelectedItem().toString();

        // Validar que la institución y el año base no estén vacíos
        if (!nombreInstitucion.isEmpty() && !anioBaseString.isEmpty()) {
            // Crear datasets para los gráficos
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();

            // Convertir el año base a un entero
            int anioBase = Integer.parseInt(anioBaseString);

            // Consultar los datos necesarios para los gráficos
            List<GraficorModeloInstitucion> datos = graficoConsulta.GraficoPorAlcance(
                    nombreInstitucion, anioBase, NombreMuncipio);
            List<ModeloEmisionCalcular> datos2 = graficoConsulta.GraficoPorFuente(
                    nombreInstitucion, anioBase, NombreMuncipio);

            // Procesar los datos para el gráfico de fuentes
            for (ModeloEmisionCalcular dato : datos2) {
                String fuente = dato.getNombreFuente();
                Double total = dato.getTotal1();

                if (fuente != null && total != null) {
                    datosFuente.setValue(fuente, total);
                } else {
                    System.out.println("Datos incorrectos: Fuente = " + fuente + ", Total = " + total);
                }
            }

            // Procesar los datos para el gráfico de alcances
            for (GraficorModeloInstitucion dato : datos) {
                String alcance = dato.getAlcance();
                Double total = dato.getTotal();

                if (alcance != null && total != null) {
                    datosAlcance.setValue(alcance, total);
                } else {
                    System.out.println("Alcance o Total es nulo: Alcance = " + alcance + ", Total = " + total);
                }
            }

            // Generar un nombre de archivo basado en la fecha actual
            String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String b = "GraficosDeLa" + view.institucion.getSelectedItem().toString() + fecha;

            // Crear un cuadro de diálogo para seleccionar la ubicación del archivo
            JFileChooser f = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);
            f.setDialogTitle("Guardar archivo PDF");
            f.setSelectedFile(new File(b + ".pdf"));

            // Manejar la selección del archivo por el modeloUsuario
            int userSelection = f.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = f.getSelectedFile();
                // Asegurarse de que el nombre del archivo sea único en la carpeta seleccionada
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());
                // Exportar los gráficos a un archivo PDF
                ExportarGraficosSinNucleo(datosAlcance, datosFuente, file);
            }
        }
    }


    /**
     * Método encargado de generar datos para gráficos de alcance y fuente basados en la institución,
     * año base, modeloMunicipio y núcleo seleccionados, y exportar los gráficos generados a un archivo PDF.
     * <p>
     * Este método valida las entradas proporcionadas por el modeloUsuario, consulta los datos correspondientes,
     * procesa los resultados y permite al modeloUsuario guardar los gráficos generados en un archivo PDF
     * mediante un cuadro de diálogo de selección de archivos.
     * </p>
     */
    private void DescargarConNucleo() {
        // Obtener las selecciones de la interfaz de modeloUsuario, asegurando que no sean nulas
        String nombreInstitucion = Objects.requireNonNull(view.institucion.getSelectedItem()).toString();
        String anioBaseString = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = Objects.requireNonNull(view.municipio.getSelectedItem()).toString();
        String nombreN = Objects.requireNonNull(view.comboxNucleo.getSelectedItem()).toString();

        // Validar que los campos clave no estén vacíos
        if (!nombreInstitucion.isEmpty() && !anioBaseString.isEmpty() && !nombreN.isEmpty()) {
            // Convertir el año base a un entero
            int anioBase = Integer.parseInt(anioBaseString);

            // Crear datasets para los gráficos
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();

            // Consultar los datos necesarios para los gráficos con núcleo
            List<GraficorModeloInstitucion> datos = graficoConsulta.GraficoPorAlcanceNucleo(
                    nombreInstitucion, anioBase, NombreMuncipio, nombreN);
            List<ModeloEmisionCalcular> datos2 = graficoConsulta.GraficoPorFuenteNucleo(
                    nombreInstitucion, anioBase, NombreMuncipio, nombreN);

            // Procesar los datos para el gráfico de fuentes
            for (ModeloEmisionCalcular dato : datos2) {
                String fuente = dato.getNombreFuente();
                Double total = dato.getTotal1();

                if (fuente != null && total != null) {
                    datosFuente.setValue(fuente, total);
                } else {
                    System.out.println("Datos incorrectos: Fuente = " + fuente + ", Total = " + total);
                }
            }

            // Procesar los datos para el gráfico de alcances
            for (GraficorModeloInstitucion dato : datos) {
                String alcance = dato.getAlcance();
                Double total = dato.getTotal();

                if (alcance != null && total != null) {
                    datosAlcance.setValue(alcance, total);
                } else {
                    System.out.println("Alcance o Total es nulo: Alcance = " + alcance + ", Total = " + total);
                }
            }

            // Generar un nombre de archivo basado en la fecha actual y el núcleo
            String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String b = "GraficosDeLa" + view.institucion.getSelectedItem().toString()
                    + fecha + "DelNucleo" + nombreN;

            // Crear un cuadro de diálogo para seleccionar la ubicación del archivo
            JFileChooser f = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);
            f.setDialogTitle("Guardar archivo PDF");
            f.setSelectedFile(new File(b + ".pdf"));

            // Manejar la selección del archivo por el modeloUsuario
            int userSelection = f.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = f.getSelectedFile();
                // Asegurarse de que el nombre del archivo sea único en la carpeta seleccionada
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());
                // Exportar los gráficos a un archivo PDF
                ExportarGraficos(datosAlcance, datosFuente, file);
            }
        }
    }

    /**
     * Método encargado de generar gráficos de alcance y fuente basados en la institución,
     * año base y modeloMunicipio seleccionados, considerando la suma de todos los núcleos,
     * y exportarlos a un archivo PDF.
     *
     * Este método consulta los datos necesarios, los procesa y permite al modeloUsuario
     * guardar los gráficos generados en un archivo PDF utilizando un cuadro de diálogo
     * de selección de archivos.
     */
    private void DescargaTodosLosNucleos() {
        // Obtener las selecciones de la interfaz de modeloUsuario
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String anioBaseString = String.valueOf(view.anio.getSelectedItem());
        String NombreMuncipio = String.valueOf(view.municipio.getSelectedItem());

        // Validar que los campos clave no estén vacíos
        if (!nombreInstitucion.isEmpty() && !anioBaseString.isEmpty() && !NombreMuncipio.isEmpty()) {
            // Crear datasets para los gráficos
            DefaultPieDataset datosAlcance = new DefaultPieDataset();
            DefaultPieDataset datosFuente = new DefaultPieDataset();

            // Convertir el año base a un entero
            int anioBase = Integer.parseInt(anioBaseString);

            // Consultar los datos necesarios para los gráficos
            List<GraficorModeloInstitucion> datos = graficoConsulta.GraficoPorAlcanceNucleoSumaTodosNucleos(
                    nombreInstitucion, anioBase, NombreMuncipio);
            List<ModeloEmisionCalcular> datos2 = graficoConsulta.GraficoPorFuenteNucleoSumaTodosNucleos(
                    nombreInstitucion, anioBase, NombreMuncipio);

            // Procesar los datos para el gráfico de fuentes
            for (ModeloEmisionCalcular dato : datos2) {
                String fuente = dato.getNombreFuente();
                Double total = dato.getTotal1();

                if (fuente != null && total != null) {
                    datosFuente.setValue(fuente, total);
                } else {
                    System.out.println("Datos incorrectos: Fuente = " + fuente + ", Total = " + total);
                }
            }

            // Procesar los datos para el gráfico de alcances
            for (GraficorModeloInstitucion dato : datos) {
                String alcance = dato.getAlcance();
                Double total = dato.getTotal();

                if (alcance != null && total != null) {
                    datosAlcance.setValue(alcance, total);
                } else {
                    System.out.println("Alcance o Total es nulo: Alcance = " + alcance + ", Total = " + total);
                }
            }

            // Generar un nombre de archivo basado en la fecha actual
            String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String b = "GraficosDeLa" + view.institucion.getSelectedItem().toString()
                    + fecha + "DeTodosLosNucleos";

            // Crear un cuadro de diálogo para seleccionar la ubicación del archivo
            JFileChooser f = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            f.setFileFilter(filter);
            f.setDialogTitle("Guardar archivo PDF");
            f.setSelectedFile(new File(b + ".pdf"));

            // Manejar la selección del archivo por el modeloUsuario
            int userSelection = f.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = f.getSelectedFile();
                // Asegurarse de que el nombre del archivo sea único en la carpeta seleccionada
                File file = ensureUniqueFilename(c.getParentFile(), c.getName());
                // Exportar los gráficos a un archivo PDF
                ExportaGraficoSumaTodosNucleos(datosAlcance, datosFuente, file);
            }
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
     * Método encargado de exportar gráficos a un archivo PDF.
     *
     * Este método genera gráficos a partir de los datos de alcance y fuente proporcionados,
     * los convierte en imágenes y los añade a un documento PDF junto con información relevante
     * de la institución, modeloMunicipio y núcleo seleccionados. El archivo se guarda en la ruta especificada.
     *
     * @param datosAlcance Dataset para el gráfico circular de alcances.
     * @param datosFuente Dataset para el gráfico circular de fuentes.
     * @param r Archivo destino donde se guardará el PDF.
     */
    private void ExportarGraficos(DefaultPieDataset datosAlcance, DefaultPieDataset datosFuente, File r) {
        try {
            // Crear gráficos basados en los datasets proporcionados
            JFreeChart chartAlcance = crearGraficoCircular(datosAlcance);
            JFreeChart chartFuente = crearGraficoFuente(datosFuente);

            // Convertir gráficos a imágenes en formato PNG
            ByteArrayOutputStream chartStreamAlcance = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamAlcance, chartAlcance.createBufferedImage(400, 200));
            byte[] chartBytesAlcance = chartStreamAlcance.toByteArray();

            ByteArrayOutputStream chartStreamFuente = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamFuente, chartFuente.createBufferedImage(400, 200));
            byte[] chartBytesFuente = chartStreamFuente.toByteArray();

            // Crear un nuevo documento PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(r));
            document.open();

            // Consultar información adicional para incluir en el PDF
            List<ModeloEmisionInforme> a = graficoConsulta.datos(view.institucion.getSelectedItem().toString(), modeloMunicipio.getNombreM());
            String fechaActual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            // Añadir fecha de creación al documento
            Paragraph fecha = new Paragraph("Fecha de creación: " + fechaActual);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            document.add(fecha);
            document.add(new Paragraph(" ")); // Espacio en blanco

            // Añadir información de la institución, modeloMunicipio y núcleo
            String nombre = view.institucion.getSelectedItem().toString();
            Paragraph nombreInstitucion = new Paragraph("Nombre de la Institución: " + nombre);
            nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
            document.add(nombreInstitucion);
            document.add(new Paragraph(" "));

            String m = view.municipio.getSelectedItem().toString();
            Paragraph municipio = new Paragraph("ModeloMunicipio: " + m);
            municipio.setAlignment(Element.ALIGN_LEFT);
            document.add(municipio);
            document.add(new Paragraph(" "));

            String nm = view.comboxNucleo.getSelectedItem().toString();
            Paragraph nucleo = new Paragraph("Núcleo: " + nm);
            nucleo.setAlignment(Element.ALIGN_LEFT);
            document.add(nucleo);
            document.add(new Paragraph(" "));

            // Añadir información adicional desde el modelo de informe
            for (ModeloEmisionInforme info : a) {
                String d = info.getDepartamento();
                Paragraph departamento = new Paragraph("Departamento: " + d);
                departamento.setAlignment(Element.ALIGN_LEFT);
                document.add(departamento);
                document.add(new Paragraph(" "));

                String i = info.getNit();
                Paragraph nit = new Paragraph("NIT de la institución: " + i);
                nit.setAlignment(Element.ALIGN_LEFT);
                document.add(nit);
                document.add(new Paragraph(" "));
            }

            // Añadir gráficos y sus descripciones al PDF
            Image chartImageAlcance = Image.getInstance(chartBytesAlcance);
            chartImageAlcance.setAlignment(Element.ALIGN_CENTER);
            Paragraph textoAlcance = new Paragraph(
                    "En el siguiente gráfico se presenta la suma total de las fuentes de emisión discriminadas por alcance, descritas como:\n" +
                            "Alcance 1: Emisiones directas por consumo de combustibles fósiles y refrigerantes.\n" +
                            "Alcance 2: Emisiones indirectas por consumo de energía eléctrica.\n" +
                            "Alcance 3: Otras emisiones indirectas, incluyendo consumo de materias primas, insumos y viajes de negocios.\n"
            );
            textoAlcance.setAlignment(Element.ALIGN_LEFT);
            document.add(textoAlcance);
            document.add(chartImageAlcance);
            document.add(new Paragraph("\n")); // Espacio entre gráficos

            Image chartImageFuente = Image.getInstance(chartBytesFuente);
            chartImageFuente.setAlignment(Element.ALIGN_CENTER);
            Paragraph textoFuente = new Paragraph(
                    "El gráfico a continuación muestra todas las fuentes de emisión asociadas a la institución seleccionada " +
                            "para el año base indicado, junto con el porcentaje de consumo de cada fuente."
            );
            document.add(textoFuente);
            document.add(chartImageFuente);

            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(null, "PDF guardado correctamente");
            document.close();
        } catch (Exception e) {
            // Manejo de errores
            System.out.println(e.getMessage());
        }
    }

    /**
     * Método para exportar gráficos a un archivo PDF sin incluir datos específicos de núcleos.
     *
     * Este método genera gráficos a partir de los datos de alcance y fuente proporcionados,
     * los convierte a imágenes, y los inserta en un archivo PDF junto con información relevante
     * de la institución y el modeloMunicipio seleccionados.
     *
     * @param datosAlcance Dataset para el gráfico circular de alcances.
     * @param datosFuente Dataset para el gráfico circular de fuentes.
     * @param r Archivo destino donde se guardará el PDF.
     */
    private void ExportarGraficosSinNucleo(DefaultPieDataset datosAlcance, DefaultPieDataset datosFuente, File r) {
        try {
            // Crear gráficos basados en los datasets
            JFreeChart chartAlcance = crearGraficoCircular(datosAlcance);
            JFreeChart chartFuente = crearGraficoFuente(datosFuente);

            // Convertir gráficos a imágenes en formato PNG
            ByteArrayOutputStream chartStreamAlcance = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamAlcance, chartAlcance.createBufferedImage(400, 200));
            byte[] chartBytesAlcance = chartStreamAlcance.toByteArray();

            ByteArrayOutputStream chartStreamFuente = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamFuente, chartFuente.createBufferedImage(400, 200));
            byte[] chartBytesFuente = chartStreamFuente.toByteArray();

            // Crear un nuevo documento PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(r));
            document.open();

            // Consultar información adicional para incluir en el PDF
            List<ModeloEmisionInforme> a = graficoConsulta.datos(view.institucion.getSelectedItem().toString(), modeloMunicipio.getNombreM());
            String fechaActual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            // Añadir fecha de creación al documento
            Paragraph fecha = new Paragraph("Fecha de creación: " + fechaActual);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            document.add(fecha);
            document.add(new Paragraph(" ")); // Espacio en blanco

            // Añadir información de la institución y modeloMunicipio
            String nombre = view.institucion.getSelectedItem().toString();
            Paragraph nombreInstitucion = new Paragraph("Nombre de la Institución: " + nombre);
            nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
            document.add(nombreInstitucion);
            document.add(new Paragraph(" "));

            String m = view.municipio.getSelectedItem().toString();
            Paragraph municipio = new Paragraph("ModeloMunicipio: " + m);
            municipio.setAlignment(Element.ALIGN_LEFT);
            document.add(municipio);
            document.add(new Paragraph(" "));

            // Añadir información adicional desde el modelo de informe
            for (ModeloEmisionInforme info : a) {
                String d = info.getDepartamento();
                Paragraph departamento = new Paragraph("Departamento: " + d);
                departamento.setAlignment(Element.ALIGN_LEFT);
                document.add(departamento);
                document.add(new Paragraph(" "));

                String i = info.getNit();
                Paragraph nit = new Paragraph("NIT de la institución: " + i);
                nit.setAlignment(Element.ALIGN_LEFT);
                document.add(nit);
                document.add(new Paragraph(" "));
            }

            // Añadir gráficos y sus descripciones al PDF
            Image chartImageAlcance = Image.getInstance(chartBytesAlcance);
            chartImageAlcance.setAlignment(Element.ALIGN_CENTER);
            Paragraph textoAlcance = new Paragraph(
                    "El gráfico muestra una suma total de las fuentes de emisión discriminadas por alcance:\n" +
                            "- Alcance 1: Emisiones directas por combustibles fósiles y refrigerantes.\n" +
                            "- Alcance 2: Emisiones indirectas por consumo de energía eléctrica.\n" +
                            "- Alcance 3: Otras emisiones indirectas, incluyendo consumo de materias primas e insumos, y viajes de negocios.\n"
            );
            textoAlcance.setAlignment(Element.ALIGN_LEFT);
            document.add(textoAlcance);
            document.add(chartImageAlcance);
            document.add(new Paragraph("\n")); // Espacio entre gráficos

            Image chartImageFuente = Image.getInstance(chartBytesFuente);
            chartImageFuente.setAlignment(Element.ALIGN_CENTER);
            Paragraph textoFuente = new Paragraph(
                    "El gráfico muestra las fuentes de emisión asociadas a la institución seleccionada " +
                            "para el año base, indicando el porcentaje de consumo de cada fuente."
            );
            textoFuente.setAlignment(Element.ALIGN_LEFT);
            document.add(textoFuente);
            document.add(chartImageFuente);

            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(null, "PDF guardado correctamente");
            document.close();
        } catch (Exception e) {
            // Manejo de errores
            System.out.println(e.getMessage());
        }
    }

    /**
     * Método para exportar gráficos que representan la suma total de todos los núcleos en un archivo PDF.
     *
     * Este método toma dos conjuntos de datos (alcance y fuente), los convierte en gráficos circulares,
     * los renderiza como imágenes y los inserta junto con información relevante en un archivo PDF.
     *
     * @param datosAlcance Dataset con los datos del gráfico circular de alcances.
     * @param datosFuente Dataset con los datos del gráfico circular de fuentes.
     * @param r Archivo donde se guardará el PDF generado.
     */
    private void ExportaGraficoSumaTodosNucleos(DefaultPieDataset datosAlcance, DefaultPieDataset datosFuente, File r) {
        try {
            JFreeChart chartAlcance = crearGraficoCircular(datosAlcance);
            JFreeChart chartFuente = crearGraficoFuente(datosFuente);

            BufferedImage bufferedImageAlcance = chartAlcance.createBufferedImage(600, 300);
            ByteArrayOutputStream chartStreamAlcance = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamAlcance, bufferedImageAlcance);
            byte[] chartBytesAlcance = chartStreamAlcance.toByteArray();

            BufferedImage bufferedImageFuente = chartFuente.createBufferedImage(600, 300);
            ByteArrayOutputStream chartStreamFuente = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartStreamFuente, bufferedImageFuente);
            byte[] chartBytesFuente = chartStreamFuente.toByteArray();

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(r));
            document.open();

            List<ModeloEmisionInforme> a = graficoConsulta.datos(view.institucion.getSelectedItem().toString(), modeloMunicipio.getNombreM());
            String fechaActual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            Paragraph fecha = new Paragraph("Fecha de creación: " + fechaActual);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            document.add(fecha);
            document.add(new Paragraph(" "));

            String nombre = view.institucion.getSelectedItem().toString();
            Paragraph nombreInstitucion = new Paragraph("Nombre de la Institución: " + nombre);
            nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
            document.add(new Paragraph(""));
            document.add(nombreInstitucion);
            document.add(new Paragraph("  "));

            String m = view.municipio.getSelectedItem().toString();
            Paragraph municipio = new Paragraph("ModeloMunicipio: " + m);
            municipio.setAlignment(Element.ALIGN_LEFT);
            document.add(municipio);
            document.add(new Paragraph("  "));

            for (ModeloEmisionInforme info : a) {
                String d = info.getDepartamento();
                Paragraph departamento = new Paragraph("Departamento: " + d);
                departamento.setAlignment(Element.ALIGN_LEFT);
                document.add(departamento);
                document.add(new Paragraph("  "));

                String i = info.getNit();
                Paragraph nit = new Paragraph("NIT de la institución: " + i);
                nit.setAlignment(Element.ALIGN_LEFT);
                document.add(nit);
                document.add(new Paragraph("  "));
            }

            Image chartImageAlcance = Image.getInstance(chartBytesAlcance);
            chartImageAlcance.setAlignment(Element.ALIGN_CENTER);
            chartImageAlcance.scaleToFit(500, 300);

            Paragraph textoALcance = new Paragraph(
                    "En el siguiente vamos apreciar lo siguiente una suma total de las fuentes de emisión discriminada por alcance el cual se describe así.\n" +
                            "Alcance 1: Corresponde al consumo de combustibles fósiles, y de refrigerantes.\n" +
                            "Alcance 2: Corresponde al consumo de la energía eléctrica.\n" +
                            "Alcance 3: Otras emisiones indirectas, consumo de materias primas e insumos, además de los viajes de negocios.\n"
            );
            textoALcance.setAlignment(Element.ALIGN_LEFT);
            document.add(textoALcance);
            document.add(chartImageAlcance);
            document.add(new Paragraph("\n"));

            Image chartImageFuente = Image.getInstance(chartBytesFuente);
            chartImageFuente.setAlignment(Element.ALIGN_CENTER);
            chartImageFuente.scaleToFit(500, 300);

            document.add(new Paragraph(
                    "En el siguiente gráfico encontraremos todas las fuentes de emisión que corresponden " +
                            "a la institución seleccionada con su respectivo año base. Además, se muestra el porcentaje de consumo de cada fuente de emisión."
            ));
            document.add(chartImageFuente);

            JOptionPane.showMessageDialog(null, "PDF guardado correctamente ");
            document.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Crea un gráfico circular para representar las emisiones por alcance.
     *
     * Este método toma un conjunto de datos y genera un gráfico circular que muestra la distribución
     * de emisiones categorizadas por diferentes alcances. Se configura un formato de etiqueta que incluye
     * el nombre de la categoría, el valor absoluto y el porcentaje.
     *
     * @param dataset Conjunto de datos para generar el gráfico.
     * @return Objeto JFreeChart con el gráfico circular generado.
     */
    private JFreeChart crearGraficoCircular(DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Emisiones por Alcance", // Título del gráfico
                dataset,                 // Conjunto de datos
                true,                    // Incluir leyenda
                true,                    // Incluir tooltips
                false                    // Excluir URLs
        );
        PiePlot plot = (PiePlot) chart.getPlot();

        // Formato personalizado para mostrar porcentajes con 6 decimales
        NumberFormat numberFormat = new DecimalFormat("0.000000%");

        // Generador de etiquetas: muestra el nombre, valor absoluto y porcentaje
        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})", // {0}: nombre de la sección, {1}: valor, {2}: porcentaje
                numberFormat,
                numberFormat
        );
        plot.setLabelGenerator(labelGenerator);

        // Configuración para ignorar secciones con valores cero
        plot.setIgnoreZeroValues(true);

        return chart;
    }

    /**
     * Crea un gráfico circular para representar las emisiones por fuente.
     *
     * Este método toma un conjunto de datos y genera un gráfico circular que muestra la distribución
     * de emisiones categorizadas por diferentes fuentes. Se configura un formato de etiqueta que incluye
     * el nombre de la categoría, el valor absoluto y el porcentaje.
     *
     * @param dataset Conjunto de datos para generar el gráfico.
     * @return Objeto JFreeChart con el gráfico circular generado.
     */
    private JFreeChart crearGraficoFuente(DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Emisiones por Fuente", // Título del gráfico
                dataset,                // Conjunto de datos
                true,                   // Incluir leyenda
                true,                   // Incluir tooltips
                false                   // Excluir URLs
        );
        PiePlot plot = (PiePlot) chart.getPlot();

        // Formato personalizado para mostrar porcentajes con 6 decimales
        NumberFormat numberFormat = new DecimalFormat("0.000000%");

        // Generador de etiquetas: muestra el nombre, valor absoluto y porcentaje
        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                "{0}= {1} ({2})", // {0}: nombre de la sección, {1}: valor, {2}: porcentaje
                numberFormat,
                numberFormat
        );
        plot.setLabelGenerator(labelGenerator);

        // Configuración para ignorar secciones con valores cero
        plot.setIgnoreZeroValues(true);

        return chart;
    }


    /**
     * Muestra un gráfico en el panel especificado de la interfaz de modeloUsuario.
     *
     * Este método agrega el gráfico proporcionado a un panel denominado "PanelGrafico2" en la vista,
     * habilitando la interacción con el gráfico mediante el desplazamiento de la rueda del ratón
     * y ajustando el tamaño del panel para que el gráfico se ajuste adecuadamente.
     * Primero elimina cualquier componente previo en el panel antes de agregar el nuevo gráfico.
     *
     * @param chart El gráfico a mostrar, de tipo JFreeChart.
     */
    private void mostrarGrafico(JFreeChart chart) {
        // Crear un panel para el gráfico con la capacidad de usar la rueda del ratón para hacer zoom
        ChartPanel panel2 = new ChartPanel(chart);
        panel2.setMouseWheelEnabled(true); // Permite hacer zoom con la rueda del ratón
        panel2.setPreferredSize(new Dimension(600, 400)); // Ajusta el tamaño preferido del panel

        // Configurar el panel para mostrar el gráfico en el lugar adecuado de la vista
        view.PanelGrafico2.setLayout(new BorderLayout());
        view.PanelGrafico2.removeAll(); // Elimina cualquier componente previo en el panel
        view.PanelGrafico2.add(panel2, BorderLayout.CENTER); // Agrega el gráfico al panel
        view.PanelGrafico2.revalidate(); // Recalcula el layout del panel
        view.PanelGrafico2.repaint();    // Redibuja el panel para reflejar los cambios
    }

    /**
     * Muestra un gráfico en otro panel especificado de la interfaz de modeloUsuario.
     *
     * Similar al método anterior, este agrega un gráfico proporcionado a un panel denominado
     * "PanelGrafico" en la vista, habilitando la interacción con el gráfico mediante el desplazamiento
     * de la rueda del ratón y ajustando el tamaño del panel. Se eliminan componentes previos en el panel
     * antes de agregar el nuevo gráfico.
     *
     * @param chart El gráfico a mostrar, de tipo JFreeChart.
     */
    private void mostrarGrafico2(JFreeChart chart) {
        // Crear un panel para el gráfico con la capacidad de usar la rueda del ratón para hacer zoom
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true); // Permite hacer zoom con la rueda del ratón
        panel.setPreferredSize(new Dimension(600, 400)); // Ajusta el tamaño preferido del panel

        // Configurar el panel para mostrar el gráfico en el lugar adecuado de la vista
        view.PanelGrafico.setLayout(new BorderLayout());
        view.PanelGrafico.removeAll(); // Elimina cualquier componente previo en el panel
        view.PanelGrafico.add(panel, BorderLayout.CENTER); // Agrega el gráfico al panel
        view.PanelGrafico.revalidate(); // Recalcula el layout del panel
        view.PanelGrafico.repaint();    // Redibuja el panel para reflejar los cambios
    }

    private void cargarInstitucion() {
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
    private void cargarAnioBase() {
        // Obtener la lista de años base desde el Modelo
        String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
        String nombreMunicipio = String.valueOf(view.municipio.getSelectedItem());

        // Consultar los años base desde el Modelo
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
    /**
     * Carga los municipios disponibles en el comboBox de la interfaz gráfica,
     * basado en la institución seleccionada por el modeloUsuario.
     * <p>
     * Este método obtiene los municipios asociados a la institución seleccionada
     * desde el modelo de datos, limpia el comboBox de municipios y lo llena con los
     * valores obtenidos. Si no se ha seleccionado una institución, limpia el comboBox.
     */
    private void cargarMunicipio() {
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
    /**
     * Carga los núcleos existentes asociados a una institución en un componente de selección (combo box) de la interfaz gráfica.
     * Pasos:
     * 1. Limpia el contenido del combo box para evitar duplicados o valores previos.
     * 2. Crea una conexión a la base de datos utilizando la clase `Conexion`.
     * 3. Utiliza la clase `ConsultaNucleo` para obtener una lista de núcleos asociados a la institución actual.
     * 4. Agrega los nombres de los núcleos recuperados al combo box para que el modeloUsuario pueda seleccionarlos.
     * Nota: Este método depende de un modelo (`modeloInstitucion`) que contiene el nombre de la institución actual.
     */
    private void cargarNucleosExistentes() {
        // Limpiamos el combo box antes de cargar los nuevos valores
        view.comboxNucleo.removeAllItems();
        view.comboxNucleo.addItem(" ");
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
        // Obtenemos el nombre de la institución desde el modelo
        String nombreInstitucion = view.institucion.getSelectedItem().toString();
        // Llamamos al modelo para cargar los núcleos
        List<String> nucleos = consultaNucleo.cargarNucleos(nombreInstitucion);

        // Agregamos los núcleos al combo box
        for (String nucleo : nucleos) {
            view.comboxNucleo.addItem(nucleo);
        }

        // Manejo de errores si ocurre algún problema en la consulta
        if (nucleos.isEmpty()) {
            JOptionPane.showMessageDialog(view.PanelMain, "No se encontraron núcleos para esta institución.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    /**
     * Carga los años base en el comboBox de la interfaz gráfica, basado en la institución y modeloMunicipio seleccionados.
     * <p>
     * Este método consulta los años base disponibles desde el modelo de datos en función de la institución y
     * el modeloMunicipio seleccionados en la vista. Luego, limpia el comboBox de años y lo llena con los valores obtenidos.
     * Finalmente, selecciona el primer año si el comboBox tiene elementos.
     */
    private void anioBaseNucleo() {
        view.anio.removeAllItems(); // Limpiar el combo de años
        view.anio.addItem(" "); // Agregar un texto indicativo
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
        String nombre = String.valueOf(view.institucion.getSelectedItem());
        String municipio = String.valueOf(view.municipio.getSelectedItem());
        String nucleo = String.valueOf(view.comboxNucleo.getSelectedItem());
        List<String> anioBaseNucleo = consultaNucleo.obtenerAnosBase(nombre, municipio, nucleo);
        for (String anioBase : anioBaseNucleo) {
            view.anio.addItem(anioBase);
        }

    }
    /**
     * Carga los años base en el comboBox de la interfaz gráfica, basado en la institución y modeloMunicipio seleccionados.
     * <p>
     * Este método consulta los años base disponibles desde el modelo de datos en función de la institución y
     * el modeloMunicipio seleccionados en la vista. Luego, limpia el comboBox de años y lo llena con los valores obtenidos.
     * Finalmente, selecciona el primer año si el comboBox tiene elementos.
     */
    private void anioParaSumaTodosLosNucleos() {
        Conexion conexion = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);
        // Obtener la institución y modeloMunicipio seleccionados desde la vista
        String nombreInstitucion = (String) view.institucion.getSelectedItem();
        String nombreMunicipio = (String) view.municipio.getSelectedItem();

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
                view.anio.addItem(anio);
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
     * Navega a la vista inicial de la aplicación.
     * Pasos:
     * 1. Crea el controlador de inicio con las dependencias necesarias.
     * 2. Llama al método que inicia la vista de inicio.
     * 3. Cierra la vista actual.
     */
    private void BotonInicio() {
        ControladorPestaniaPrincipal control = new ControladorPestaniaPrincipal(modeloInstitucion, modeloUsuario, modeloMunicipio);
        System.out.println(modeloInstitucion.getNombreInstitucion());
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
    private void vistaCalcular() {
        Conexion con = new Conexion();
        Vistas.Calcular viewCal = new Calcular();
        ModeloEmisionCalcular mod = new ModeloEmisionCalcular();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorCalcular controlador = new ControladorCalcular(mod, consul, viewCal, modeloInstitucion, consultaUsuario, modeloUsuario, modeloMunicipio);
        controlador.iniciar();
        viewCal.setVisible(true);
        view.dispose();
    }
    /**
     * Inicia la vista de informes.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica de los informes.
     * 3. Muestra la vista de informes y cierra la vista actual.
     */
    private void vistaRegistrarEmision() {
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
    private void vistaInforme() {
        Conexion con = new Conexion();
        ModeloInstitucion mod2 = new ModeloInstitucion();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloEmisionInforme mod = new ModeloEmisionInforme();
        Informe viewInfo = new Informe();
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorInforme contro = new ControladorInforme(viewInfo, mod, consul, modeloMunicipio, modeloInstitucion, modeloUsuario);
        contro.iniciar();
        viewInfo.setVisible(true);
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
        ControladorCompararInstitucion contro = new ControladorCompararInstitucion(mod, consultas, comIns, viewGraf, modeloInstitucion, modeloMunicipio, modeloUsuario);
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
        ControladorTendencia control = new ControladorTendencia(mod, consult, viewGraf, modeloMunicipio, modeloInstitucion, modeloUsuario);
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
        ControladorReducir redu = new ControladorReducir(modeloInstitucion, consul, vista, modeloMunicipio, modeloUsuario);
        redu.Iniciar();
        view.dispose();
    }
    /**
     * Inicia la vista para visualizar instituciones.
     * Pasos:
     * 1. Crea las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica.
     * 3. Opcionalmente, cierra la vista actual (comentado en este caso).
     */
    private void vistaActualizarInstitucion() {
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

        ControladorVerPerfiles verControl = new ControladorVerPerfiles(modeloUsuario, modeloInstitucion, modeloMunicipio, verPerfiles, consul);
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
    private void vistaVerInstitucion(){
        Conexion con = new Conexion();
        VerInstituciones verInstituciones = new VerInstituciones();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(con);
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        ModeloInstitucion modeloInstitucion = new ModeloInstitucion();
        ControladorVerInstituciones contraladorVerInstituciones = new ControladorVerInstituciones(consultasInstitucion, consultaNucleo,
                modeloInstitucion, verInstituciones, modeloUsuario, modeloMunicipio);
        contraladorVerInstituciones.iniciar();
        //view.dispose();
    }

    /**
     * Método que inicializa los listeners para los botones y componentes de la vista.
     * Asocia eventos de acción a los botones y otros componentes interactivos.
     */
    private void Listeners() {
        this.view.buscarButton.addActionListener(this::actionPerformed);
        this.view.Descargar.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.perfil.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.RegistrarEmision.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);
        this.view.verInstitucion.addActionListener(this::actionPerformed);
        this.view.siNucleo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.siNucleo.isSelected()) {
                    view.comboxNucleo.setVisible(true);
                    view.SeleccionarNucleo.setVisible(true);
                    view.noNucleo.setEnabled(false);
                    cargarNucleosExistentes();
                }
                if (!view.siNucleo.isSelected()) {
                    view.SeleccionarNucleo.setVisible(false);
                    view.comboxNucleo.setVisible(false);
                    view.noNucleo.setEnabled(true);
                }


                cargarAnioBase();
            }
        });
        this.view.noNucleo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.noNucleo.isSelected()) {
                    view.SeleccionarNucleo.setVisible(false);
                    view.comboxNucleo.setVisible(false);
                    view.siNucleo.setEnabled(false);
                    view.general.setVisible(true);
                    view.siGeneral.setVisible(true);
                    view.noGeneral.setVisible(true);

                } else {
                    view.siNucleo.setEnabled(true);
                    view.general.setVisible(false);
                    view.siGeneral.setVisible(false);
                    view.noGeneral.setVisible(false);
                }
            }
        });
        this.view.institucion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.institucion.getItemCount() > 0) {
                    view.municipio.removeAllItems(); // Limpiar el combo de modeloMunicipio
                    view.comboxNucleo.removeAllItems();
                    cargarAnioBase();
                    cargarMunicipio();
                }
                Conexion conn = new Conexion();
                ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);
                String nombreInstitucion = String.valueOf(view.institucion.getSelectedItem());
                if(consultaNucleo.TieneNucleo(nombreInstitucion)>=1){
                    view.noNucleo.setVisible(true);
                    view.siNucleo.setVisible(true);
                    view.Nucleo.setVisible(true);
                    view.anio.removeAllItems();
                }else{
                    view.noNucleo.setVisible(false);
                    view.siNucleo.setVisible(false);
                    view.Nucleo.setVisible(false);
                }
            }
        });
        this.view.municipio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.municipio.getItemCount() > 0) {
                    view.comboxNucleo.removeAllItems(); // Limpiar el combo de núcleos si está visible
                    if (view.siNucleo.isSelected()) {
                        cargarNucleosExistentes(); // Cargar núcleos si el checkbox está seleccionado
                    }
                    cargarAnioBase(); // Cargar el año base
                }
            }
        });
        this.view.comboxNucleo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.comboxNucleo.getItemCount() > 0) {
                    anioBaseNucleo(); // Cargar el año base basado en la selección del núcleo
                }
            }
        });
        this.view.siGeneral.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(view.siGeneral.isSelected()){
                    anioParaSumaTodosLosNucleos();
                    view.noGeneral.setEnabled(false);
                }else{
                    view.noGeneral.setEnabled(true);
                }
            }
        });

    }

}