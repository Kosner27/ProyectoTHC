    package Vistas;

    import Controlador.*;
    import Modelo.*;
    import Modelo.Consultas.*;
    import Modelo.modelo.*;

    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;

    public class CompararOtrarInstituciones extends JFrame {
        public JMenuItem RegistrarInstitucion;
        public JMenuItem RegistrarEmisión;
        public JMenuItem Calcular;
        public JMenuItem Informes;
        public JMenuItem Graficos;
        public JMenuItem Reducir;
        public JMenuItem MasInformacion;
        public JComboBox institucion;
        public JComboBox anio;
        public JComboBox alcance;
        public JTable Instituciones;
        public JPanel PanelMain;
        public JScrollPane Contenedor;
        public JButton añadirButton;
        public JButton compararButton;
        public JButton inicioButton;
        public JMenuBar bar;
        public JLabel titulo;
        public JComboBox Municipio;

        public CompararOtrarInstituciones() {
            setTitle("Comparar con otras instituciones");
            setVisible(true);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(1000, 500);
            Font font = new Font("Arial", Font.PLAIN, 14);
            titulo.setFont(font);
            setLocationRelativeTo(null);
            setContentPane(PanelMain);
            Instituciones.setPreferredScrollableViewportSize(new Dimension(500, 300));
            Instituciones.setFillsViewportHeight(true);
            String[] columnNames = {"Nombre de la Institucion", "NIT", "Departamento", "Municipio"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
            Instituciones.setModel(tableModel);
            JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
            JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
            JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");
            Graficos.add(GraficoPrincipal);
            Graficos.add(GraficosCompararInstitucion);
            Graficos.add(GraficoHistorico);


           /* GraficosCompararInstitucion.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
                    GraficoComparar view = new GraficoComparar();
                    Conexion conn = new Conexion();
                    GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
                    GraficoCompararModelo mod = new GraficoCompararModelo();
                    ComparaInstitucion contro = new ComparaInstitucion(mod,consultas,comIns,view);
                    contro.iniciar();
                    dispose();
                }
            });*/

            GraficoHistorico.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Conexion con = new Conexion();
                    TendenciaModelo mod = new TendenciaModelo();
                    ConsultasTendencias consult = new ConsultasTendencias(con);
                    GraficoTendencia view = new GraficoTendencia();
                    TendenciaControlador control = new TendenciaControlador(mod,consult,view);
                    view.setVisible(true);
                    control.iniciar();
                    dispose();

                }
            });


            RegistrarInstitucion.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    RegistrarInstitucion registrarInstitucionView = new RegistrarInstitucion();
                    InstitucionModelo modelo = new InstitucionModelo();
                    ConsultasInstitucion consultas = new ConsultasInstitucion();
                    InstitucionControlador controlador = new InstitucionControlador(modelo, consultas, registrarInstitucionView);
                    controlador.iniciar();
                    registrarInstitucionView.setVisible(true);
                    dispose(); // Cerrar la vista actual
                }
            });

            RegistrarEmisión.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    Emision emisionView = new Emision();
                    EmisionModelo mod = new EmisionModelo();
                    ConsultasEmision consul = new ConsultasEmision();
                    EmisionControlador controlador = new EmisionControlador(mod,consul, emisionView);
                    controlador.iniciar();
                    emisionView.setVisible(true);
                    dispose();
                }
            });
            /*Calcular.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Conexion con = new Conexion();
                    Vistas.Calcular view = new Calcular();
                    CalcularModelo mod = new CalcularModelo();
                    InstitucionModelo modelo = new InstitucionModelo();
                    CalcularConsultas consul = new CalcularConsultas(con);
                    CalcularControlador controlador = new CalcularControlador(mod,consul,view,modelo);
                    controlador.iniciar();
                    view.setVisible(true);
                    dispose();
                }
            });*/

           /* GraficoPrincipal.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Conexion con = new Conexion();
                    GraficoConsulta consul = new GraficoConsulta(con);
                    Vistas.Graficos view = new Graficos();
                    GraficorModelo mod = new GraficorModelo();
                    InstitucionModelo modelo = new InstitucionModelo();
                    GraficoControlador contro = new GraficoControlador(mod,consul, view,modelo);
                    contro.iniciar();
                    view.setVisible(true);
                    dispose();
                }
            });*/



           /* Informes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Conexion con = new Conexion();
                    InstitucionModelo mod2 = new InstitucionModelo();
                    ConsultaInforme consul = new ConsultaInforme(con);
                    ModeloInforme mod = new ModeloInforme();
                    Informe view = new Informe();
                    ControladorInforme contro = new ControladorInforme(view,mod,consul);
                    contro.iniciar();
                    view.setVisible(true);
                    dispose();
                }
            });*/

            MasInformacion.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    new MasInformacion();
                    setVisible(false);
                }
            });
            MasInformacion.setVisible(true);
            Reducir.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    new Reducir2();
                    setVisible(false);
                }
            });Reducir.setVisible(true);

            inicioButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    new Inicio();
                    setVisible(false);
                }
            });

            inicioButton.setVisible(true);
        }
    }
