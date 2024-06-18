package Vistas;
import Controlador.CalcularControlador;
import Controlador.EmisionControlador;
import Controlador.InstitucionControlador;
import Modelo.*;
import Diccionario.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Emision extends JFrame {
    public JMenuItem RegistrarInstitucion;
    public JMenuItem Calcular;
    public JMenuItem Informes;
    public JMenuItem Graficos;
    public JMenuItem Reducir;
    public JMenuItem MasInformacion;
    public JComboBox Fuente;
    public JComboBox Estado;
    public JTextField nombre;
    public JComboBox Unidad;
    public JComboBox Alcance;
    public JTextField Factor;
    public JButton guardarButton;
    public JScrollPane Contenedor;
    public JTable Emisiones;
    public JButton buscarButton;
    public JButton inicioButton;
    public JButton guardarCambiosButton;
    public JButton eliminarButton1;
    public JPanel PanelMain;
    public JButton guardarCambios;
    public JLabel estado;
    private JLabel Titulo;
    private JMenuBar bar;
    private JMenuItem RegistrarEmisión;
    public static Conexion conexion = new Conexion();
    public static Diccionario factorEmision = new Diccionario();

    public Emision() {
        setTitle("Registrar Emisión");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(PanelMain);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
        JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
        JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");
        Graficos.add(GraficoPrincipal);
        Graficos.add(GraficosCompararInstitucion);
        Graficos.add(GraficoHistorico);
        String[] columnNames = {"Fuente emision", "Estado", "Nombre fuente","Unidad medida", "Factor emision","Alcance"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames,0);
        Emisiones.setModel(tableModel);
        Emisiones.setSize(1000, 304);
        Contenedor.setSize(1000, 304);
        Font font = new Font("Arial", Font.PLAIN, 14);
        Titulo.setFont(font);
        inicioButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Inicio();
                setVisible(false);
            }
        });
        Inicio.getWindows();

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
        Calcular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        MasInformacion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new MasInformacion();
                setVisible(false);
            }
        });
        MasInformacion.setVisible(true);
        Informes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Informe();
                setVisible(false);
            }
        });
        Informes.setVisible(true);

        GraficosCompararInstitucion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
                comIns.setVisible(true);
                dispose();
            }
        });

        GraficoHistorico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GraficoTendencia graf = new GraficoTendencia();
                graf.setVisible(true);
                dispose();
            }
        });

        GraficoPrincipal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vistas.Graficos graficos = new Graficos();
                graficos.setVisible(true);
                dispose();
            }
        });
        Reducir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Reducir2();
                setVisible(false);
            }
        });
        Reducir.setVisible(true);

                }
    public static void  main (String[]args ){

        new Emision();


    }

            }



