package Vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Emision extends JFrame {
    private JMenuItem RegistrarInstitucion;
    private JMenuItem RegistrarEmisión;
    private JMenuItem Calcular;
    private JMenuItem Informes;
    private JMenuItem Graficos;
    private JMenuItem Reducir;
    private JMenuItem MasInformacion;
    private JComboBox Fuente;
    private JComboBox Estado;
    private JTextField nombre;
    private JComboBox Unidad;
    private JComboBox Alcance;
    private JTextField Factor;
    private JButton guardarButton;
    private JScrollPane Contenedor;
    private JTable Emisiones;
    private JButton buscarButton;
    private JButton inicioButton;
    private JButton eliminarButton;
    private JButton editarButton;
    private JPanel PanelMain;
    private JButton guardarCambios;
    private JLabel estado;

    public Emision() {
        setTitle("Registrar Emisión");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setContentPane(PanelMain);

        String[] columnNames = {"Fuente emision", "Estado", "Nombre fuente","Unidad medida", "Factor emision","Alcance"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames,0);
        Emisiones.setModel(tableModel);
        Emisiones.getColumnModel().getColumn(0).setPreferredWidth(150);
        Emisiones.getColumnModel().getColumn(2).setPreferredWidth(150);
        Emisiones.getColumnModel().getColumn(3).setPreferredWidth(150);
        Emisiones.getColumnModel().getColumn(4).setPreferredWidth(150);
        Emisiones.setSize(1000, 304);
        Contenedor.setSize(1000, 304);
        inicioButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Inicio();
                setVisible(false);
            }

        });
        Inicio.getWindows();
        RegistrarInstitucion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new RegistrarInstitucion();
                setVisible(false);}
        });
        RegistrarInstitucion.setVisible(true);

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

        guardarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                String fuente =  (String) Fuente.getSelectedItem();
                String estado = (String) Estado.getSelectedItem();
                String unidad = (String )Unidad.getSelectedItem();
                String alcance = (String)Alcance.getSelectedItem();
                String factor = Factor.getText();
                String Nombre= nombre.getText();
                if(!fuente.isEmpty()&& !estado.isEmpty() && !unidad.isEmpty() && !alcance.isEmpty() && !factor.isEmpty() && !Nombre.isEmpty()){
                    String[] rows ={fuente,estado,Nombre, unidad, factor, alcance};

                    tableModel.addRow(rows);

                    setExtendedState(JFrame.MAXIMIZED_BOTH);



                    Emisiones.repaint();
                    Fuente.setSelectedIndex(0);
                    Estado.setSelectedIndex(0);
                    Unidad.setSelectedIndex(0);
                    Alcance.setSelectedIndex(0);
                    Factor.setText("");
                    nombre.setText("");
                }
                else {
                    JOptionPane.showMessageDialog(PanelMain, "Llene todos los campos");
                }

            }
        });
        Fuente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Fuente.getSelectedItem()!="Energia" ){
                    Estado.setVisible(true);
                    estado.setVisible(true);
                }else{
                    Estado.setVisible(false);
                    estado.setVisible(false);
                }
                if(Fuente.getSelectedItem()==" " ){
                    Estado.setVisible(false);
                    estado.setVisible(false);
                }

            }
        });
    }
    public static void  main(String[]args ){

        new Emision();


    }
}
