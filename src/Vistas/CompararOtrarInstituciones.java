    package Vistas;

    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;

    public class CompararOtrarInstituciones extends JFrame {
        public JMenuItem Graficos;
        public JComboBox institucion;
        public JComboBox anio;
        public JComboBox alcance;
        public JTable Instituciones;
        public JPanel PanelMain;
        public JScrollPane Contenedor;
        public JButton anadirButton;
        public JButton compararButton;
        public JButton inicioButton;
        public JLabel titulo;
        public JComboBox Municipio;
        public JMenuItem RegistrarInstitucion;
        public JMenuItem RegistrarEmision;
        public JMenuItem Calcular;
        public JMenuItem Informes;
        public JMenuItem Reducir;
        public JMenuItem perfil;
        public JMenuBar bar;
        public JMenuItem VerPerfiles;
        public JCheckBox Si;
        public JCheckBox no;
        public JComboBox InstitucionNucleo;
        public JComboBox MunicipioNucleo;
        public JComboBox nucleo;
        public JComboBox anioNucleoCombo;
        public JLabel Institucion;
        public JLabel SeleccionNucleo;
        public JButton eliminarBtn;
        private JMenuItem MasInformacion;
        public JMenuItem verInstitucion;
        public JLabel AnioNucleo;
        public JLabel nucleoSede;

        public CompararOtrarInstituciones() {
            setTitle("Comparar con otras instituciones");
            setVisible(true);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(1300, 500);
            Font font = new Font("Arial", Font.PLAIN, 14);
            titulo.setFont(font);
            setLocationRelativeTo(null);
            setContentPane(PanelMain);
            Instituciones.setPreferredScrollableViewportSize(new Dimension(500, 300));
            Instituciones.setFillsViewportHeight(true);
            String[] columnNames = {"Nombre de la Institución", "NIT", "Departamento", "Municipio", "Campus", "Alcance", "Año Base"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
            Instituciones.setModel(tableModel);


        }
    }