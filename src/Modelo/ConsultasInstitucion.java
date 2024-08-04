package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConsultasInstitucion {

    public boolean registrarInstitucion(InstitucionModelo institucion) {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();

        String sql = "CALL insertarInstitucion(?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, institucion.getMunicipio());
            ps.setString(2, institucion.getNit());
            ps.setString(3, institucion.getNombreInstitucion());
            ps.setInt(4, institucion.getHectareas());

            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean ActualizarHectareas(InstitucionModelo institucion) {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        String sql = "CALL ActualizarInstitucion(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, institucion.getNombreInstitucion());
            ps.setInt(2, institucion.getHectareas());
            ps.execute();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;

        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean buscarInstitucion(InstitucionModelo institucion) {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        ResultSet rs = null;
        String sql = "CALL  institucion(?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, institucion.getNombreInstitucion());
                    rs = ps.executeQuery();
                    while(rs.next()){
                        institucion.setNit(rs.getString("Nit"));
                        institucion.setDepartamento(rs.getString("NombreDepartamento"));
                        institucion.setMunicipio(rs.getString("NombreMunicipio"));
                        institucion.setHectareas(rs.getInt("Hectareas"));
                    }

            //dato.setAlcance(rs.getString("Alcance"));

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

public static void main (String []arg){


}

}
