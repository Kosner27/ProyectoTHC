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
        public JMenuItem Graficos;
        public JComboBox institucion;
        public JComboBox anio;
        public JComboBox alcance;
        public JTable Instituciones;
        public JPanel PanelMain;
        public JScrollPane Contenedor;
        public JButton añadirButton;
        public JButton compararButton;
        public JButton inicioButton;
        public JLabel titulo;
        public JComboBox Municipio;
        public JMenuItem RegistrarInstitucion;
        public JMenuItem RegistrarEmisión;
        public JMenuItem Calcular;
        public JMenuItem Informes;
        public JMenuItem Reducir;
        public JMenuItem perfil;
        public JMenuBar bar;
        public JMenuItem MasInformacion;
        public JMenuItem VerPerfiles;

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


        }
    }