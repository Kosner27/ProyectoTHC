package Modelo.Consultas;

import Modelo.Conexion;
import Modelo.modelo.ModeloInforme;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsultaInforme {
    private final Connection conn;

    public ConsultaInforme(Conexion conexion) {
        this.conn = conexion.getConection();
    }

    public List<ModeloInforme> tablaInforme(String nombre, Integer anioBase, String municipio) {
        List<ModeloInforme> tabla = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("call llenarTablaInforme(?,?,?)");
            ps.setString(1, nombre);
            ps.setInt(2, anioBase);
            ps.setString(3, municipio);
            rs = ps.executeQuery();

            int rowCount = 0; // Contador de filas

            while (rs.next()) {
                rowCount++;
                ModeloInforme mod = new ModeloInforme();
                mod.setNombreFuente(rs.getString("NombreFuente"));
                mod.setTipoFuente(rs.getString("Emision"));
                mod.setCantidadConsumidad(rs.getDouble("cargaAmbiental"));
                mod.setAlcance(rs.getString("Alcance"));
                mod.setUnidadMedidad(rs.getString("unidadMedida"));
                mod.setFactorEmision(rs.getDouble("factorEmision"));
                mod.setCargaAmnbiental(rs.getDouble("CO2Aportado"));
                mod.setAnioBase(rs.getInt("anioBase"));
                tabla.add(mod);

            }

            // Verificar si se encontraron filas


        } catch (SQLException e) {
            System.out.println("Error en la consulta SQL: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tabla;
    }
    public List<ModeloInforme> tablaInformeNucleo(String nombre, Integer anioBase, String municipio , String nucleo) {
        List<ModeloInforme> tabla = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("call llenartablaNucleo(?,?,?,?)");
            ps.setString(1, nombre);
            ps.setInt(2, anioBase);
            ps.setString(3, municipio);
            ps.setString(4,nucleo);
            rs = ps.executeQuery();

            int rowCount = 0; // Contador de filas

            while (rs.next()) {
                rowCount++;
                ModeloInforme mod = new ModeloInforme();
                mod.setNombreFuente(rs.getString("NombreFuente"));
                mod.setTipoFuente(rs.getString("Emision"));
                mod.setCantidadConsumidad(rs.getDouble("cargaAmbiental"));
                mod.setAlcance(rs.getString("Alcance"));
                mod.setUnidadMedidad(rs.getString("unidadMedida"));
                mod.setFactorEmision(rs.getDouble("factorEmision"));
                mod.setCargaAmnbiental(rs.getDouble("CO2Aportado"));
                mod.setAnioBase(rs.getInt("anioBase"));
                tabla.add(mod);

            }

            // Verificar si se encontraron filas


        } catch (SQLException e) {
            System.out.println("Error en la consulta SQL: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tabla;
    }

    public List<ModeloInforme> datos(String nombre, String municipio) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ModeloInforme> instituciones = new ArrayList<>();

        try {
            ps = conn.prepareStatement("Select * from institucion i \n" +
                    "inner join municipioinstitiucion mi on i.IdInstitucionAuto = mi.IdInstitucion\n" +
                    "inner join municipio m on mi.idMuncipio = m.idMunicipio \n" +
                    "inner join Departamento d \n" +
                    "on m.idDepartamento = d.idDepartamento where m.NombreMunicipio = ? and i.NombreInstitucion = ?");
            ps.setString(1, municipio);
            ps.setString(2, nombre);
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


