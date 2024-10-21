package Modelo.Consultas;

import Modelo.Conexion;
import Modelo.modelo.InstitucionModelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsultasInstitucion {

    public boolean ActualizarInstitucion(String nombreIns, String Municipio, Integer Hectareas) {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();

        String sql = "Call ActualizarInstitucion(?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreIns);
            ps.setInt(2, Hectareas);
            ps.setString(3, Municipio);

            ps.execute();
            System.out.println(nombreIns + " " + Municipio + " " + Hectareas);
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

    public List<InstitucionModelo> CargarDatos(String nombreInst, String nombreMunicipo) {
        Conexion conexion = new Conexion();
        List<InstitucionModelo> datos = new ArrayList<>();
        Connection conn = conexion.getConection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("Select NombreInstitucion, Nit, NombreMunicipio, NombreDepartamento from Institucion i inner join  municipioinstitiucion mi on i.idInstitucionAuto = mi.IdInstitucion inner join Municipio m  on m.IdMunicipio = mi.idMuncipio inner join Departamento d on m.idDepartamento = d.IdDepartamento where NombreInstitucion = ? and NombreMunicipio = ? ");
            ps.setString(1, nombreInst);
            ps.setString(2, nombreMunicipo);
            rs = ps.executeQuery();

            while (rs.next()) {
                InstitucionModelo mod = new InstitucionModelo();
                mod.setNombreInstitucion(rs.getString("NombreInstitucion"));
                mod.setNit(rs.getString("Nit"));
                mod.setMunicipio(rs.getString("NombreMunicipio"));
                mod.setDepartamento(rs.getString("NombreDepartamento"));
                datos.add(mod);
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
        return datos;
    }


}