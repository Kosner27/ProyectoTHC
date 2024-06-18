    package Vistas;

    import Controlador.CalcularControlador;
    import Controlador.EmisionControlador;
    import Controlador.InstitucionControlador;
    import Modelo.*;

    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;

    public class CompararOtrarInstituciones extends JFrame {
        private JMenuItem RegistrarInstitucion;
        private JMenuItem RegistrarEmisión;
        private JMenuItem Calcular;
        private JMenuItem Informes;
        private JMenuItem Graficos;
        private JMenuItem Reducir;
        private JMenuItem MasInformacion;
        private JComboBox institucion;
        private JComboBox anio;
        private JComboBox alcance;
        private JTable Instituciones;
        private JPanel PanelMain;
        private JScrollPane Contenedor;
        private JButton añadirButton;
        private JButton compararButton;
        private JButton inicioButton;
        private JMenuBar bar;
        private JLabel titulo;

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

            JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
            JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
            JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");
            Graficos.add(GraficoPrincipal);
            Graficos.add(GraficosCompararInstitucion);
            Graficos.add(GraficoHistorico);









            añadirButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if(institucion.getSelectedItem()!="" && anio.getSelectedItem()!="" && alcance.getSelectedItem()!=""){
                        String[] columnNames = {"Nombre de la Institucion", "NIT", "Departamento", "Municipio"};
                        String[] rows = {"Pascual Bravo", "1234", "Antioquia", "Medellin"};
                        String[] rows1 = {"UDEA", "12345667", "Antioquia", "Medellin"};
                        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
                        tableModel.addRow(rows);
                        tableModel.addRow(rows1);
                        Instituciones.setModel(tableModel);

                        // Ajustar el tamaño de las columnas después de que la tabla haya sido actualizada
                        SwingUtilities.invokeLater(() -> {
                            Instituciones.getColumnModel().getColumn(0).setPreferredWidth(200);
                            Instituciones.getColumnModel().getColumn(1).setPreferredWidth(150);
                            Instituciones.getColumnModel().getColumn(2).setPreferredWidth(150);
                            Instituciones.getColumnModel().getColumn(3).setPreferredWidth(150);
                        });
                    }else{
                        JOptionPane.showMessageDialog(PanelMain, "llene todos los campos");
                    }

                }
            });
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
