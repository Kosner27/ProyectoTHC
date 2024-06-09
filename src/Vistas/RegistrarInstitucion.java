package Vistas;

import Conexiones.Conexion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

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
    public static Conexion conexion = new Conexion();
    public RegistrarInstitucion(){
        CargarDepartamento();
        cargarMunicipio();
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

        registrarButton.addMouseListener(new MouseAdapter() {
            @Override

            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Connection conn = conexion.getConnection();
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

                    try {
                        if (conn != null) {
                            // Consulta SQL para obtener los municipios del departamento seleccionado
                            String procedureCall = "CALL insertarInstitucion(?,?,?,?)";

                            CallableStatement statement = conn.prepareCall(procedureCall);
                            statement.setString(1,seleccionado2);
                            statement.setString(2,NIT);
                            statement.setString(3,nombreInstitucion);
                            statement.setString(4,Hectareas);
                            // Ejecutar la consulta
                           statement.executeQuery();

                            // Cerrar los recursos

                            statement.close();

                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(PanelMain, "Error al insertar la institución: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
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
                    JOptionPane.showMessageDialog(PanelMain,"institucion alamcenada correctamente");
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

        departamento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (departamento.getItemCount() > 0) {
                    cargarMunicipio();
                }
            }
        });
    }

    public void CargarDepartamento(){
        Connection conn = conexion.getConnection();
        String sql = "CALL SeleccionarDepartamento()";

        departamento.removeAllItems();
        if(conn!= null){
            try {
                Statement stmt = conn.createStatement();
                ResultSet rst = stmt.executeQuery(sql);
                departamento.removeAllItems();
                departamento.addItem("");
                while (rst.next()){
                    String nombre = rst.getString("NombreDepartamento");
                    departamento.addItem(nombre);

                }
                if (departamento.getItemCount() > 0) {
                    departamento.setSelectedIndex(0);
                }else {
                    // Manejar el caso en el que el JComboBox está vacío
                    System.out.println("El JComboBox departamento está vacío");
                }

                rst.close();
                stmt.close();

            }catch (SQLException e ){
                e.printStackTrace();
            }
        }
    }

    public void cargarMunicipio() {
        municipio.removeAllItems(); // Limpiar los elementos del JComboBox
        municipio.addItem("");

        // Obtener el departamento seleccionado directamente del JComboBox
        String departamentoSeleccionado = (String) departamento.getSelectedItem();

        if (departamentoSeleccionado != null && !departamentoSeleccionado.isEmpty()) {
            try {
                // Cerrar la conexión existente antes de abrir una nueva
                if (conexion.getConnection() == null) {
                    conexion.getConnection().close();
                }

                // Crear una nueva conexión para obtener los municipios del departamento seleccionado
                Connection conn = conexion.getConnection();
                if (conn != null) {
                    // Consulta SQL para obtener los municipios del departamento seleccionado
                    String procedureCall = "CALL BuscarMunicipio(?)";

                    try (CallableStatement statement = conn.prepareCall(procedureCall)) {
                        statement.setString(1, departamentoSeleccionado); // Establecer el valor del parámetro

                        // Ejecutar la consulta
                        ResultSet rs = statement.executeQuery();

                        // Verificar si el ResultSet está vacío
                        if (!rs.isBeforeFirst()) {
                            System.out.println("No se encontraron municipios para el departamento seleccionado.");
                        } else {
                            // Llenar el JComboBox con los municipios obtenidos de la consulta
                            while (rs.next()) {
                                String nombreMunicipio = rs.getString("NombreMunicipio");
                                municipio.addItem(nombreMunicipio);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(PanelMain, "Error al cargar los municipios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public static void  main (String[]args ){

        new RegistrarInstitucion();


    }

}
