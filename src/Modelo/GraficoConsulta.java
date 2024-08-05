package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GraficoConsulta {

    private Connection conn;

    public GraficoConsulta(Conexion conexion) {
        this.conn = conexion.getConection();
    }

    public List<GraficorModelo> GraficoDato(String nombreInstitucion, String anioBase) {
        List<GraficorModelo> datos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("CALL LlenarGrafico(?, ?)");
            ps.setString(1, nombreInstitucion);
            ps.setString(2, anioBase);
            rs = ps.executeQuery();

            while (rs.next()) {
                GraficorModelo dato = new GraficorModelo();
                dato.setAlcance(rs.getString("Alcance"));
                dato.setTotal(rs.getDouble("co2 por alcance")); // Asumiendo que "co2 por alcance" es el nombre correcto de la columna
                datos.add(dato);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return datos;
    }
    public List<CalcularModelo> GraficoDato2(String nombreInstitucion, String anioBase) {
        List<CalcularModelo> datos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("CALL LlenarGrafico2(?, ?)");
            ps.setString(1, nombreInstitucion);
            ps.setString(2, anioBase);
            rs = ps.executeQuery();

            while (rs.next()) {
                CalcularModelo dato = new CalcularModelo();
                dato.setNombreFuente(rs.getString("NombreFuente"));
                dato.setTotal1(rs.getDouble("co2 por fuente"));
                datos.add(dato);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return datos;
    }

    public List<ModeloInforme> datos(String nombre){
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ModeloInforme> instituciones = new ArrayList<>();

        try {
            ps = conn.prepareStatement("CALL institucion(?)");
            ps.setString(1, nombre);
            rs = ps.executeQuery();

            while (rs.next()) {
                ModeloInforme institucion = new ModeloInforme();
                institucion.setMunicipio(rs.getString("NombreMunicipio"));
                institucion.setDepartamento(rs.getString("NombreDepartamento"));
                institucion.setNit(rs.getString("Nit"));


                instituciones.add(institucion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instituciones;
    }
}
