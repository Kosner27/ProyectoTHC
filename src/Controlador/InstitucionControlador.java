package Controlador;


import Modelo.Conexion;
import Modelo.ConsultasInstitucion;
import Modelo.InstitucionModelo;
import Vistas.RegistrarInstitucion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InstitucionControlador implements ActionListener {
private InstitucionModelo mod;
private ConsultasInstitucion consul;
private RegistrarInstitucion view;

    public InstitucionControlador (InstitucionModelo mod,ConsultasInstitucion consul, RegistrarInstitucion view){
        this.mod=mod;
        this.consul=consul;
        this.view=view;
        this.view.registrarButton.addActionListener(this);
        this.view.inicioButton.addActionListener(this);
        this.view.departamento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.departamento.getItemCount() > 0) {
                    cargarMunicipio();
                }
            }
        });
    }
    public void iniciar(){
        view.setTitle("Registrar Institucion");
        view.setLocationRelativeTo(null);
        CargarDepartamento();
        cargarMunicipio();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.registrarButton) {
            mod.setNit(view.nit.getText());
            mod.setMunicipio(view.municipio.getSelectedItem().toString());
            mod.setNombreInstitucion(view.nombre.getText());
            mod.setHectareas(Integer.parseInt(view.hectareas.getText()));

            String nombreInstitucion = view.nombre.getText();
            String NIT = view.nit.getText();
            String Hectareas = view.hectareas.getText();
            String seleccionado = (String) view.departamento.getSelectedItem();
            String seleccionado2 = (String) view.municipio.getSelectedItem();

            if (!view.nombre.getText().isEmpty() && !view.nit.getText().isEmpty() && !view.hectareas.getText().isEmpty() && !seleccionado2.isEmpty() && !seleccionado.isEmpty()) {
                String[] row = {nombreInstitucion, NIT, Hectareas, seleccionado, seleccionado2};

                // Obtener el modelo de la tabla existente
                DefaultTableModel tableModel = (DefaultTableModel) view.Institucion.getModel();
                tableModel.addRow(row);

                view.Institucion.setModel(tableModel);
                view.Institucion.setVisible(true);
                view.Contenedor.setVisible(true);

                SwingUtilities.invokeLater(() -> {
                    view.Institucion.getColumnModel().getColumn(0).setPreferredWidth(200);
                    view.Institucion.getColumnModel().getColumn(1).setPreferredWidth(150);
                    view.Institucion.getColumnModel().getColumn(2).setPreferredWidth(150);
                    view.Institucion.getColumnModel().getColumn(3).setPreferredWidth(150);
                    view.Institucion.repaint();
                });

                if (consul.registrarInstitucion(mod)) {
                    JOptionPane.showMessageDialog(null, "Registro guardado");
                    Limpiar();
                } else {
                    JOptionPane.showMessageDialog(null, "Error");
                    Limpiar();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos.");
            }
        }
    }


    public void Limpiar(){
        view.departamento.setSelectedIndex(0);
        view.municipio.setSelectedIndex(0);
        view.nit.setText(null);
        view.hectareas.setText(null);
        view.nombre.setText(null);
    }

    public void CargarDepartamento(){
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        String sql = "CALL SeleccionarDepartamento()";

        view.departamento.removeAllItems();
        if(conn!= null){
            try {
                Statement stmt = conn.createStatement();
                ResultSet rst = stmt.executeQuery(sql);
                view.departamento.removeAllItems();
                view.departamento.addItem("");
                while (rst.next()){
                    String nombre = rst.getString("NombreDepartamento");
                    view.departamento.addItem(nombre);

                }
                if (view.departamento.getItemCount() > 0) {
                    view.departamento.setSelectedIndex(0);
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
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        view.municipio.removeAllItems(); // Limpiar los elementos del JComboBox
        view.municipio.addItem("");

        // Obtener el departamento seleccionado directamente del JComboBox
        String departamentoSeleccionado = (String) view.departamento.getSelectedItem();

        if (departamentoSeleccionado != null && !departamentoSeleccionado.isEmpty()) {
            try {
                // Cerrar la conexión existente antes de abrir una nueva
                if (conn != null) {
                    // Crear una nueva conexión para obtener los municipios del departamento seleccionado
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
                                view.municipio.addItem(nombreMunicipio);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(view.PanelMain, "Error al cargar los municipios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Asegúrate de cerrar la conexión
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



}
