package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
}
