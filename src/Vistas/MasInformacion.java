package Vistas;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MasInformacion extends JFrame {
    private JMenuItem RegistrarInstitucion;
    private JMenuItem RegistrarEmisión;
    private JMenuItem Calcular;
    private JMenuItem Informes;
    private JMenuItem Graficos;
    private JMenuItem Reducir;
    private JPanel PanelMain;
    private JLabel Imagen;
    private JButton inicoButton;
    private JMenuBar bar;
    private JMenuItem MasInformacion;


    public MasInformacion() {
        setTitle("Registrar Insititución");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setContentPane(PanelMain);
        JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
        JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
        JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");
        Graficos.add(GraficoPrincipal);
        Graficos.add(GraficosCompararInstitucion);
        Graficos.add(GraficoHistorico);
        // Crear el panel principal
        PanelMain = new JPanel(new BorderLayout());



        RegistrarInstitucion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new RegistrarInstitucion();
                setVisible(false);}
        });
        RegistrarInstitucion.setVisible(true);


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
        });Reducir.setVisible(true);

        inicoButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Inicio();
                setVisible(false);
            }
        });
        inicoButton.setVisible(true);
    }

}
