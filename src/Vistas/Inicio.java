package Vistas;


import Controlador.*;
import Modelo.*;
import Modelo.Consultas.*;
import Modelo.modelo.*;

import javax.swing.*;
import java.awt.event.*;


public class Inicio extends JFrame {
    public JPanel panel1;
    public JMenuItem RegistrarInstitucion;
    public JMenuItem RegistrarEmisión;
    public JMenuItem Calcular;
    public JMenuItem Informes;
    public JMenuItem Graficos;
    public JMenuItem Reducir;
    public JMenuItem MasInformacion;
    public JMenuBar bar;
    public JLabel imagen;
    public JMenuItem perfil;
    public JButton CerraSesion;
    public  JMenuItem VerPerfiles;


    public Inicio() {
        setTitle("Inicio");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setContentPane(panel1);

    }



}
