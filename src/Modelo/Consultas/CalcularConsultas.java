package Modelo.Consultas;

import Modelo.modelo.CalcularModelo;
import Modelo.Conexion;
import Modelo.modelo.EmisionModelo;
import Modelo.modelo.InstitucionModelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CalcularConsultas {
    private Connection conn;

    public CalcularConsultas(Conexion conexion) {
        this.conn = conexion.getConection();
    }

    public List<EmisionModelo> getEmisiones(String fuenteSeleccionada) {
        List<EmisionModelo> emisiones = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("CALL LlenarTablaCalcular(?)");
            ps.setString(1, fuenteSeleccionada);
            rs = ps.executeQuery();
            while (rs.next()) {
                EmisionModelo mod = new EmisionModelo();
                mod.setNombreFuente(rs.getString("NombreFuente"));
                mod.setEstadoFuente(rs.getString("EstadoFuente"));
                mod.setUnidadMedidad(rs.getString("UnidadMedida"));
                mod.setAlcance(rs.getString("Alcance"));
                mod.setFactorEmision(rs.getDouble("FactorEmision"));
                emisiones.add(mod);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return emisiones;
    }

    public boolean registrarCargaAmbienta(CalcularModelo mod, InstitucionModelo mod2) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("CALL insertarEmisionInstitucion(?,?,?,?,?)");
            ps.setInt(1, mod.getAnioBase());
            ps.setDouble(2, mod.getCantidadConsumidad());
            ps.setString(3, mod2.getNombreInstitucion());
            ps.setString(4, mod.getNombreFuente());
            ps.setDouble(5,mod.getTotal1());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
