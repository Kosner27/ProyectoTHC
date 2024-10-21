package Vistas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegistrarInstitucion extends JFrame {
    public JPanel PanelMain;
    public JMenuItem RegistrarEmisión;
    public JMenuItem Calcular;
    public JMenuItem Informes;
    public JMenuItem Graficos;
    public JMenuItem Reducir;
    public JTextField nombre;
    public JTextField nit;
    public JTextField hectareas;
    public JButton inicioButton;
    public JScrollPane Contenedor;
    public JButton editarButton;
    public JLabel titulo;
    public JTextField departamento;
    public JTextField municipio;
    public JButton guardarCambios;
    public JMenuBar bar;
    public JMenuItem RegistrarInstitucion;
    public JMenuItem MasInformacion;
    public JMenuItem perfil;
    public JMenuItem VerPerfiles;

    public RegistrarInstitucion() {

        setTitle("Registrar Insititución");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        Font font = new Font("Arial", Font.PLAIN, 14);
        titulo.setFont(font);
        setLocationRelativeTo(null);
        setContentPane(PanelMain);

    }




}