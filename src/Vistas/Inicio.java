package Vistas;


import Controlador.EmisionControlador;
import Controlador.InstitucionControlador;
import Modelo.ConsultasEmision;
import Modelo.ConsultasInstitucion;
import Modelo.EmisionModelo;
import Modelo.InstitucionModelo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class Inicio extends JFrame {
    private JPanel panel1;
    private JMenuItem RegistrarInstitucion;
    private JMenuItem RegistrarEmisión;
    private JMenuItem Calcular;
    private JMenuItem Informes;
    private JMenuItem Graficos;
    private JMenuItem Reducir;
    private JMenuItem MasInformacion;
    private JMenuBar bar;
    private JLabel imagen;


    public Inicio() {
        setTitle("Inicio");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null); // Centrar el JFrame en la pantalla

        setContentPane(panel1);
        JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
        JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
        JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");
        Graficos.add(GraficoPrincipal);
        Graficos.add(GraficosCompararInstitucion);
        Graficos.add(GraficoHistorico);

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



        Reducir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Reducir2();
                setVisible(false);
            }
        });Reducir.setVisible(true);
        Graficos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                super.mouseWheelMoved(e);

            }
        });
        Graficos.addMouseListener(new MouseAdapter() {
        });
    }
public static void  main(String[]args ){

        new Inicio();




}

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
