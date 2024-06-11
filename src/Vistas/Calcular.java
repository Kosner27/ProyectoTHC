package Vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Calcular extends JFrame{
    private JPanel PanelMain;
    private JMenuItem RegistrarInstitucion;
    private JMenuItem RegistrarEmisión;
    private JMenuItem Informes;
    private JMenuItem Graficos;
    private JMenuItem Reducir;
    private JMenuItem MasInformacion;
    private JPanel SubMain;
    private JComboBox institucion;
    private JTextField anio;
    private JButton añadirLeFuenteButton;
    private JTable Emisiones;
    private JTextField total;
    private JButton guardarButton;
    private JButton inicioButton;
    private JScrollPane Contenedor;
    private JComboBox fuente;
    private JMenuBar bar;
    private JMenuItem Calcular;
    private JButton guardarCalculoButton;
    private JLabel titulo;

    public Calcular(){

        setTitle("Calcular");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        Font font = new Font("Arial", Font.PLAIN, 14);
        titulo.setFont(font);
        setContentPane(PanelMain);
        JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
        JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
        JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");
        Graficos.add(GraficoPrincipal);
        Graficos.add(GraficosCompararInstitucion);
        Graficos.add(GraficoHistorico);
        Emisiones.setPreferredScrollableViewportSize(new Dimension(300, 300));
        Contenedor.setSize(200,200);
        String[] columnNames = {"Nombre fuente emision", "Estado", "Alcance","Carga ambiental", "Unidad de medida","FactorEmision"," Co2 Aportado"};
        DefaultTableModel tableModel;
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Hacer editable solo la columna "Carga ambiental"
            }
        };
        inicioButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Inicio();
                setVisible(false);
            }
        });
        Inicio.getWindows();
        guardarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

            }
        });
        añadirLeFuenteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                    if(institucion.getSelectedItem() != null && !anio.getText().isEmpty() && fuente.getSelectedItem() != null){
                        String[] rows = {"ACPM", "liquido", "1", "", "gal", "10.15", "26832.1445"};

                        tableModel.addRow(rows);
                        total.setText("26832.1445");
                        Emisiones.setModel(tableModel);

                        SwingUtilities.invokeLater(() -> {
                            int row = tableModel.getRowCount() - 1;
                            Emisiones.changeSelection(row, 3, false, false);
                            Emisiones.editCellAt(row, 3);
                            Component editor = Emisiones.getEditorComponent();

                            if (editor != null) {
                                editor.requestFocus();
                            }
                        });
                        Emisiones.getColumnModel().getColumn(0).setPreferredWidth(100);
                        Emisiones.getColumnModel().getColumn(3).setPreferredWidth(100);
                        Emisiones.getColumnModel().getColumn(4).setPreferredWidth(100);
                        Emisiones.getColumnModel().getColumn(5).setPreferredWidth(100);
                        Emisiones.repaint();

                    }else {
                        JOptionPane.showMessageDialog(PanelMain, "Llenar Todos los campos");
                    }

            }
        });
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
    }
    public static void  main(String[]args ){

        new Calcular();


    }
}
