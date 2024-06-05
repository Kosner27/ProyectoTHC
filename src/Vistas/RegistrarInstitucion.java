package Vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class RegistrarInstitucion extends JFrame {
    private JPanel PanelMain;
    private JMenuItem RegistrarInstitucion;
    private JMenuItem RegistrarEmisión;
    private JMenuItem Calcular;
    private JMenuItem Informes;
    private JMenuItem Graficos;
    private JMenuItem Reducir;
    private JMenuItem MasInformacion;
    private JTextField nombre;
    private JTextField nit;
    private JComboBox departamento;
    private JComboBox municipio;
    private JTextField hectareas;
    private JButton registrarButton;
    private JButton inicioButton;
    private JTable Institucion;
    private JScrollPane Contenedor;
    private JButton button1;
    private JButton button2;
    private JButton button3;

    public RegistrarInstitucion(){
        setTitle("Registrar Insititución");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setContentPane(PanelMain);
        Institucion.setPreferredScrollableViewportSize(new Dimension(500, 300));
        Institucion.setFillsViewportHeight(true);

        String[] columnNames = {"Nombre de la Institucion", "NIT", "hectareas de árboles","Departamento", "Municipio"};


        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        inicioButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Inicio();
                setVisible(false);}
        });
       Inicio.getWindows();

;
        registrarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String nombreInstitucion = nombre.getText();
                String NIT = nit.getText();
                String Hectareas = hectareas.getText();
                String seleccionado = (String) departamento.getSelectedItem();
                String seleccionado2 = (String) municipio.getSelectedItem();
                if(!nombre.getText().isEmpty()&& !nit.getText().isEmpty() && !hectareas.getText().isEmpty() && !seleccionado2.isEmpty() && !seleccionado.isEmpty()){
                    String[] rows = {nombreInstitucion, NIT, Hectareas , seleccionado,seleccionado2 };
                    tableModel.addRow(rows);

                    Institucion.setModel(tableModel);
                    Institucion.setVisible(true);
                    Contenedor.setVisible(true);
                    // Ajustar el tamaño de las columnas después de que la tabla haya sido actualizada
                    SwingUtilities.invokeLater(() -> {
                        Institucion.getColumnModel().getColumn(0).setPreferredWidth(200);
                        Institucion.getColumnModel().getColumn(1).setPreferredWidth(150);
                        Institucion.getColumnModel().getColumn(2).setPreferredWidth(150);
                        Institucion.getColumnModel().getColumn(3).setPreferredWidth(150);
                        Institucion.repaint();
                    });

                    departamento.setSelectedIndex(0);
                    municipio.setSelectedIndex(0);
                    nit.setText("");
                    hectareas.setText("");
                    nombre.setText("");
                }else{
                    JOptionPane.showMessageDialog(PanelMain, "Llenar todos los campos");
                }


            }
        });

        RegistrarEmisión.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Emision();
                setVisible(false);

            }
        });
        RegistrarEmisión.setVisible(true);
        Calcular.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Calcular();
                setVisible(false);
            }
        });
        Calcular.setVisible(true);
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
        Graficos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Graficos();
                setVisible(false);
            }
        });
        Graficos.setVisible(true);


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

        new RegistrarInstitucion();


    }

}
