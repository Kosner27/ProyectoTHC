package Modelo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConsultasEmision {
    public boolean registrarEmision(EmisionModelo mod) {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        String sql = "CALL insertarEmision(?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn = conexion.getConection();
            if (conn == null) {
                System.out.println("No se pudo establecer la conexión con la base de datos.");
                return false;
            }

            ps.setString(1, mod.getTipoFuente());
            ps.setString(2, mod.getNombreFuente());
            ps.setString(3, mod.getEstadoFuente());
            ps.setString(4, mod.getUnidadMedidad());
            ps.setString(5, mod.getAlcance());
            ps.setDouble(6, mod.getFactorEmision());
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

