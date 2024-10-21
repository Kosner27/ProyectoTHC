package Vistas;



import Controlador.EmisionControlador;
import Controlador.InstitucionControlador;
import Modelo.Consultas.ConsultasEmision;
import Modelo.Consultas.ConsultasInstitucion;
import Modelo.modelo.EmisionModelo;
import Modelo.modelo.InstitucionModelo;

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
    }

}
