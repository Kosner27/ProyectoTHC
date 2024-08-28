package Modelo.Consultas;

import Modelo.Conexion;
import Modelo.modelo.EmisionModelo;
import Modelo.modelo.UsuarioModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public List<EmisionModelo>dato() {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        String sql = "Select Emision, NombreFuente, EstadoFuente, unidadMedida, alcance, factorEmision from emision order by NombreFuente";
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<EmisionModelo> emisionModeloList = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            emisionModeloList = new ArrayList<>();
            while (rs.next()) {
                EmisionModelo mod = new EmisionModelo();
                mod.setTipoFuente(rs.getString(1));
                mod.setNombreFuente(rs.getString(2));
                mod.setEstadoFuente(rs.getString(3));
                mod.setUnidadMedidad(rs.getString(4));
                mod.setAlcance(rs.getString(5));
                mod.setFactorEmision(rs.getDouble(6));
                emisionModeloList.add(mod);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return emisionModeloList;
    }

    public int ExisteEmision(String nombreEmision){
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        ResultSet st = null;
        String sql = "Select count(idEmision) from emision where NombreFuente = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,nombreEmision);
            st=ps.executeQuery();
            if(st.next()){
                return st.getInt(1);
            }{
                return 1;
            }

        }catch (SQLException ex ){
            ex.printStackTrace();
            return 1;
        }
    }


    }

