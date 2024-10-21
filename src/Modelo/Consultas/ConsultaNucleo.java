package Modelo.Consultas;

import Modelo.Conexion;
import Modelo.modelo.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultaNucleo {

    private final Connection conn;

    public ConsultaNucleo (Conexion conexion) {
        this.conn = conexion.getConection();
    }

    public boolean RegistrarNucleo(Nucleo nucleo, InstitucionModelo ins, Municipio municipio){
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("Call EmisionNucleo(?,?,?)");
            ps.setString(1, ins.getNombreInstitucion());
            ps.setString(2, nucleo.getNombreNucleo());
            ps.setString(3, municipio.getNombreM());
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
    public int ExisteNucleo(Nucleo nucleo){
      ResultSet rs = null;
      String sql = " Select count(*) from nucleoinstitucion where NombreNucleo =?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nucleo.getNombreNucleo());
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            {
                return 1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
    }
    public boolean InsertarCalculoConNucleo (CalcularModelo calcularModelo, Nucleo nucleo){
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("CALL InsersarCalculoConNucleo(?,?,?,?,?)");
            ps.setString(1,nucleo.getNombreNucleo());
            ps.setInt(2, calcularModelo.getAnioBase());
            ps.setDouble(3, calcularModelo.getCantidadConsumidad());
            ps.setString(4, calcularModelo.getNombreFuente());
            ps.setDouble(5, calcularModelo.getTotal1());
            System.out.println(nucleo.getNombreNucleo());
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

    public String UltimoRegistro(){
        PreparedStatement ps = null;
        String nombre = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement("SELECT * FROM nucleoinstitucion ORDER BY IdNucleo DESC LIMIT 1");
            rs = ps.executeQuery();
            while(rs.next()){
                nombre = rs.getString("NombreNucleo");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
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
