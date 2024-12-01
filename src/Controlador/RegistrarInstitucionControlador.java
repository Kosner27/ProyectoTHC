package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.ConsultasInstitucion;
import Modelo.modelo.InstitucionModelo;
import Modelo.modelo.Municipio;
import Vistas.RegistrarInstitucion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class RegistrarInstitucionControlador {
    private  RegistrarInstitucion registrarInstitucion;
    private  InstitucionModelo institucionModelo;
    private ConsultasInstitucion consultasInstitucion;
    private Municipio municipio1;
    public RegistrarInstitucionControlador(RegistrarInstitucion registrarInstitucion,
                                           InstitucionModelo institucionModelo,
                                           ConsultasInstitucion consultasInstitucion,
                                           Municipio municipio1) {
        this.registrarInstitucion = registrarInstitucion;
        this.institucionModelo = institucionModelo;
        this.consultasInstitucion = consultasInstitucion;
        this.municipio1=municipio1;
        listener();
    }
    public void inicio(){
        registrarInstitucion.setTitle("Registrar Institucion");
        registrarInstitucion.setLocationRelativeTo(null);
        CargarDepartamento();
        cargarMunicipio();


    }
    private void actionPerformed(ActionEvent e){
        if(e.getSource()==registrarInstitucion.guardarButton){
            guardar();
        }
        if(e.getSource()== registrarInstitucion.cancelarButton){
            registrarInstitucion.dispose();
        }
    }
    private void listener(){
        registrarInstitucion.guardarButton.addActionListener(this::actionPerformed);
        this.registrarInstitucion.departamento.addActionListener(e -> cargarMunicipio());
        this.registrarInstitucion.cancelarButton.addActionListener(this::actionPerformed);
    }

    private void guardar(){
        String nombreInst = registrarInstitucion.nombreInstitucion.getText();
        String departamento = String.valueOf(registrarInstitucion.departamento.getSelectedItem());
        String municipio = String.valueOf(registrarInstitucion.municipio.getSelectedItem());
        String nit = registrarInstitucion.nit.getText();
        int hectareas = Integer.parseInt(registrarInstitucion.hectareas.getText());
        institucionModelo.setNombreInstitucion(nombreInst);
        institucionModelo.setNit(nit);
        institucionModelo.setHectareas(hectareas);
        municipio1.setNombreM(municipio);
        if(!nombreInst.isEmpty() && !municipio.isEmpty() && !nit.isEmpty() && !departamento.isEmpty()){
            if(consultasInstitucion.InsertarInstitucion(institucionModelo, municipio1)){
                JOptionPane.showMessageDialog(registrarInstitucion,"La Insitución "+nombreInst + " con NIT "+nit+ " \nUbicada en el departamento de "+departamento+ " En el municipio de "+municipio+" \n ha sido resgistrada de manera exitosa");
            }else {
                JOptionPane.showMessageDialog(registrarInstitucion,"Error en la consulta");
            }
        }else{
            JOptionPane.showMessageDialog(registrarInstitucion, "POR FAVOR LLENAR COMO MINIMO LOS CAMPOS DE NIT, \nNOMBRE INSTITUCIÓN y SELECCIONAR EL DEPARTAMENTO Y EL MUNICIPIO");
        }
    }

    /**
     * Carga los departamentos desde la base de datos en el JComboBox correspondiente.
     */
    private void CargarDepartamento() {
        try (Connection conn = new Conexion().getConection();
             Statement stmt = conn.createStatement()) {

            ResultSet rst = stmt.executeQuery("CALL SeleccionarDepartamento()");
            registrarInstitucion.departamento.removeAllItems();
            registrarInstitucion.departamento.addItem("");
            while (rst.next()) {
                registrarInstitucion.departamento.addItem(rst.getString("NombreDepartamento"));
            }

            if (registrarInstitucion.departamento.getItemCount() > 0) {
                registrarInstitucion.departamento.setSelectedIndex(0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error al cargar departamentos: " + e.getMessage());
        }
    }

    /**
     * Carga los municipios de acuerdo al departamento seleccionado.
     */
    private void cargarMunicipio() {
        String departamentoSeleccionado = (String) registrarInstitucion.departamento.getSelectedItem();
        if (departamentoSeleccionado == null || departamentoSeleccionado.isEmpty()) {
            return;
        }

        try (Connection conn = new Conexion().getConection();
             CallableStatement stmt = conn.prepareCall("CALL BuscarMunicipio(?)")) {

            stmt.setString(1, departamentoSeleccionado);
            ResultSet rs = stmt.executeQuery();
            registrarInstitucion.municipio.removeAllItems();
            registrarInstitucion.municipio.addItem("");

            while (rs.next()) {
                registrarInstitucion.municipio.addItem(rs.getString("NombreMunicipio"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error al cargar municipios: " + e.getMessage());
        }
    }


}
