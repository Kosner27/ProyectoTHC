package Vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegistrarInstitucion extends JFrame {
    public JPanel PanelMain;
    public JMenuItem RegistrarEmisión;
    public JMenuItem Calcular;
    public JMenuItem Informes;
    public JMenuItem Graficos;
    public JMenuItem Reducir;
    public JMenuItem MasInformacion;
    public JTextField nombre;
    public JTextField nit;
    public JComboBox departamento;
    public JComboBox municipio;
    public JTextField hectareas;
    public JButton registrarButton;
    public JButton inicioButton;
    public JTable Institucion;
    public JScrollPane Contenedor;
    public JButton editarButton;
    public JButton buscarPorNombreButton;
    public JButton eliminarButton;
    private JMenuBar bar;
    private JMenuItem RegistrarInstitucion;
    private JLabel titulo;

    public RegistrarInstitucion() {

        setTitle("Registrar Insititución");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        Font font = new Font("Arial", Font.PLAIN, 14);
        titulo.setFont(font);
        setLocationRelativeTo(null);
        setContentPane(PanelMain);
        JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
        JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
        JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");
        Graficos.add(GraficoPrincipal);
        Graficos.add(GraficosCompararInstitucion);
        Graficos.add(GraficoHistorico);


        Institucion.setPreferredScrollableViewportSize(new Dimension(500, 300));
        Institucion.setFillsViewportHeight(true);
        String[] columnNames = {"Nombre de la Institucion", "NIT", "hectareas de árboles", "Departamento", "Municipio"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        Institucion.setModel(tableModel);
        inicioButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Inicio();
                setVisible(false);
            }
        });
        Inicio.getWindows();


        RegistrarEmisión.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Emision();
                setVisible(false);

            }
        });
        RegistrarEmisión.setVisible(true);
        Calcular.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Calcular();
                setVisible(false);
            }
        });
        Calcular.setVisible(true);
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

        new RegistrarInstitucion();


    }

}