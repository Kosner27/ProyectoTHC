package Modelo.Consultas;

import Modelo.modelo.*;
import Modelo.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CalcularConsultas {
    private final Connection conn;

    public CalcularConsultas(Conexion conexion) {
        this.conn = conexion.getConection();
    }

    public List<ModeloEmision> getEmisiones(String fuenteSeleccionada) {
        List<ModeloEmision> emisiones = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("CALL LlenarTablaCalcular(?)");
            ps.setString(1, fuenteSeleccionada);
            rs = ps.executeQuery();
            while (rs.next()) {
                ModeloEmision mod = new ModeloEmision();
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

    public boolean registrarCargaAmbientaInstitucionSinNucleo(ModeloEmisionCalcular mod, ModeloInstitucion nombreInstitucion, ModeloMunicipio nombreModeloMunicipio) {
        PreparedStatement ps = null;
        try {
            System.out.println("Año Base: " + mod.getAnioBase());
            System.out.println("Cantidad Consumida: " + mod.getCantidadConsumidad());
            System.out.println("Nombre Institución: " + nombreInstitucion.getNombreInstitucion());
            System.out.println("Nombre Fuente: " + mod.getNombreFuente());
            System.out.println("Total 1: " + mod.getTotal1());
            System.out.println("Nombre ModeloMunicipio: " + nombreModeloMunicipio.getNombreM());
            ps = conn.prepareStatement("CALL insertarCalculoSinNucleo(?,?,?,?,?,?)");
            ps.setInt(1, mod.getAnioBase());
            ps.setDouble(2, mod.getCantidadConsumidad());
            ps.setString(3, nombreInstitucion.getNombreInstitucion());
            ps.setString(4, mod.getNombreFuente());
            ps.setDouble(5, mod.getTotal1());
            ps.setString(6, nombreModeloMunicipio.getNombreM());
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



    public String Institucion(String ins, String m) {
        PreparedStatement ps = null;
        String nombre = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("Select i.NombreInstitucion, m.NombreMunicipio from institucion i  inner join  municipioinstitiucion mi \n" +
                    "on i.idInstitucionAuto = mi.IdInstitucion inner join modeloMunicipio m on mi.idMuncipio= m.idMunicipio where i.NombreInstitucion=? and m.NombreMunicipio =?");
            ps.setString(1, ins);
            ps.setString(2, m);
            rs = ps.executeQuery();
            while (rs.next()) {

                nombre = rs.getString("NombreInstitucion");

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return nombre;
    }
}
