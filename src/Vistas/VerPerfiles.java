package Vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VerPerfiles extends JFrame {
    public JMenuBar bar;
    public JMenuItem RegistrarInstitucion;
    public JMenuItem RegistrarEmisión;
    public JMenuItem Calcular;
    public JMenuItem Informes;
    public JMenuItem Reducir;
    public JMenuItem MasInformacion;
    public JMenu Graficos;
    public JMenuItem perfil;
    public JMenuItem VerPerfiles;
    public JTable user;
    public JTextField nombreUser;
    public JTextField correoUser;
    public JTextField rolUser;
    public JTextField apellidoUser;
    public JButton inicioButton;
    public JButton editarButton;
    public JButton buscarButton;
    public JButton guardarCambiosButton;
    public JPanel Main;
    public JScrollPane tblUsuario;
    public JButton eliminarButton;

    public VerPerfiles(){
            setTitle("Ver perfiles");
            setVisible(true);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(1000, 600);
            setLocationRelativeTo(null);
            setContentPane(Main);
            String [] columnNames = {"Nombre Usuario","Apellido usuario","Correo Usuario", "Nivel de privilegio","Descripción","Institucion a la que pertenece", "Sede" };
            DefaultTableModel tableModel = new DefaultTableModel(columnNames,0);
            user.setModel(tableModel);
            user.setSize(1000, 304);
            tblUsuario.setSize(1000, 304);
    }
}
