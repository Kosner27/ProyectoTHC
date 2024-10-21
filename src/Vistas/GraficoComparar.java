package Vistas;



import javax.swing.*;


public class GraficoComparar extends JFrame {
    public JMenuItem Graficos;
    public JPanel Subpanel;
    public JPanel PanelGrafico;
    public JPanel PanelMain;
    public JButton descargarButton;
    public JButton inicioButton;
    public JMenuBar bar;
    public JMenuItem RegistrarInstitucion;
    public JMenuItem RegistrarEmisión;
    public JMenuItem Calcular;
    public JMenuItem Informes;
    public JMenuItem Reducir;
    public JMenuItem MasInformacion;
    public JMenuItem perfil;
    public JMenuItem VerPerfiles;

    public GraficoComparar() {

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


    }
}