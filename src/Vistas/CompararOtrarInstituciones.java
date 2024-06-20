    package Vistas;

    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;

    public class CompararOtrarInstituciones extends JFrame {
        public JMenuItem RegistrarInstitucion;
        public JMenuItem RegistrarEmisión;
        public JMenuItem Calcular;
        public JMenuItem Informes;
        public JMenuItem Graficos;
        public JMenuItem Reducir;
        public JMenuItem MasInformacion;
        public JComboBox institucion;
        public JComboBox anio;
        public JComboBox alcance;
        public JTable Instituciones;
        public JPanel PanelMain;
        public JScrollPane Contenedor;
        public JButton añadirButton;
        public JButton compararButton;
        public JButton inicioButton;
        public JMenuBar bar;
        public JLabel titulo;

        public CompararOtrarInstituciones(){
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
            JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
            JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
            JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");
            Graficos.add(GraficoPrincipal);
            Graficos.add(GraficosCompararInstitucion);
            Graficos.add(GraficoHistorico);





            compararButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if(institucion.getSelectedItem()!="" && anio.getSelectedItem()!="" && alcance.getSelectedItem()!="" && Instituciones.getRowCount()!=0){
                        new GraficoComparar();
                        setVisible(false);
                    }else{
                        JOptionPane.showMessageDialog(PanelMain, "Para generar el grafico debe haber al menos dos instituciones registradas");
                    }

                }
            });
            compararButton.setVisible(true);

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

        inicioButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Inicio();
                setVisible(false);
            }
        });
       inicioButton.setVisible(true);
    }
    public static void  main(String[]args ){

        new CompararOtrarInstituciones();
        SwingUtilities.invokeLater(CompararOtrarInstituciones::new);

    }
}
