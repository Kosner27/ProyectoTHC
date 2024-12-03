package Modelo.Consultas;

import Modelo.Conexion;
import Modelo.modelo.ModeloEmisionInforme;
import Modelo.modelo.ModeloTendencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsultasTendencias {
    private Connection conn;

    public ConsultasTendencias(Conexion conexion) {
        this.conn = conexion.getConection();
    }

    public List<ModeloTendencia> getNombre(String NombreSeleccionado, String NombreMunicipio) {
        List<ModeloTendencia> emisiones = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("CALL GraficoHistorico(?,?)");
            ps.setString(2, NombreMunicipio);
            ps.setString(1, NombreSeleccionado);
            rs = ps.executeQuery();
            while (rs.next()) {
                ModeloTendencia dato = new ModeloTendencia();

                dato.setAlcance(rs.getString("Alcance"));
                dato.setCo2(rs.getDouble("co2_por_año")); // Asumiendo que "co2 por alcance" es el nombre correcto de la columna
                dato.setAnioBase(rs.getInt("anioBase"));
                emisiones.add(dato);
            }
        } catch (SQLException ex) {
            System.out.printf(ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return emisiones;

    }
    public List<ModeloTendencia> getNombrePorNucleo(String NombreSeleccionado, String NombreMunicipio, String Nucleo) {
        List<ModeloTendencia> emisiones = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("CALL GraficoHistoricoPorNucleo(?,?,?)");
            ps.setString(2, NombreMunicipio);
            ps.setString(1, NombreSeleccionado);
            ps.setString(3,Nucleo);
            rs = ps.executeQuery();
            while (rs.next()) {
                ModeloTendencia dato = new ModeloTendencia();

                dato.setAlcance(rs.getString("Alcance"));
                dato.setCo2(rs.getDouble("co2_por_año")); // Asumiendo que "co2 por alcance" es el nombre correcto de la columna
                dato.setAnioBase(rs.getInt("anioBase"));
                emisiones.add(dato);
            }
        } catch (SQLException ex) {
            System.out.printf(ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return emisiones;

    }

    public List<ModeloEmisionInforme> datos(String nombre, String nombreM) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ModeloEmisionInforme> instituciones = new ArrayList<>();

        try {
            ps = conn.prepareStatement("Select * from institucion i inner join municipioinstitiucion mi on i.idInstitucionAuto = mi.IdInstitucion inner join modeloMunicipio m on mi.idMuncipio = m.idMunicipio inner join Departamento d on m.idDepartamento = d.idDepartamento where m.NombreMunicipio = ? and i.NombreInstitucion = ?");
            ps.setString(1, nombreM);
            ps.setString(2, nombre);
            rs = ps.executeQuery();

            while (rs.next()) {
                ModeloEmisionInforme institucion = new ModeloEmisionInforme();
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
