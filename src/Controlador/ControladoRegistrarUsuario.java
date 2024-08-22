package Controlador;
import Modelo.Conexion;
import Modelo.modelo.UsuarioModel;
import Vistas.RegistrarUsuario;
import Modelo.Consultas.ConsultaUsuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class ControladoRegistrarUsuario {
    private final UsuarioModel mod;
    private final RegistrarUsuario view;
    private final ConsultaUsuario consul;

    public ControladoRegistrarUsuario(UsuarioModel mod, RegistrarUsuario view, ConsultaUsuario consul) {
        this.mod = mod;
        this.view = view;
        this.consul = consul;
        this.view.departamento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.departamento.getItemCount() > 0) {
                    cargarMunicipio();
                }
            }
        });
        this.view.municipio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(view.municipio.getItemCount()>0){
                    cargarInstitucion();
                }
            }
        });
    }
    public void iniciar(){
        view.setTitle("Registrar Institucion");
        view.setLocationRelativeTo(null);
        CargarDepartamento();
        cargarMunicipio();
        cargarInstitucion();
        CargarRoles();
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
                JOptionPane.showMessageDialog(view.Principal, "Error al cargar los municipios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    public void cargarInstitucion(){
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        view.Institucion.removeAllItems();
        view.Institucion.addItem(" ");
        String municipio = view.municipio.getSelectedItem().toString();
        if(municipio != null && !municipio.isEmpty()){
            try{
                if(conn != null){
                    String procedureCall = "call BuscarInstitucion(?)";
                    try(CallableStatement statement = conn.prepareCall(procedureCall)){
                        statement.setString(1, municipio); // Establecer el valor del parámetro

                        // Ejecutar la consulta
                        ResultSet rs = statement.executeQuery();

                        if(!rs.isBeforeFirst()){
                            JOptionPane.showMessageDialog(null,"No se encontro Universidad y/o institucion para el municipio seleccionado");
                        }else{
                         while (rs.next()){
                             String NombreInstitucion = rs.getString("NombreInstitucion");
                             view.Institucion.addItem(NombreInstitucion);
                         }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(view.Principal, "Error al cargar los municipios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
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

    public void CargarRoles(){
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        view.Rol.addItem(" ");
        if(conn != null ){
            String procedureCall = "call CargarRoles()";
            try(CallableStatement statement = conn.prepareCall(procedureCall)){

                ResultSet rs = statement.executeQuery();
                while(rs.next()){
                    String rol = rs.getString("tipoUsuario");
                    view.Rol.addItem(rol);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                // Asegúrate de cerrar la conexión si es necesario
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
