package Modelo;
import Vistas.GraficoComparar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class GraficoCompararConsultas {
    private Connection conn;

    public GraficoCompararConsultas(Conexion conexion) {
        this.conn = conexion.getConection();
    }

    public List<InstitucionModelo> llenarTabla(String NombreSeleccionado) {
        List<InstitucionModelo> Comparar = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("CALL LlenartablaInstitucion(?)");
            ps.setString(1, NombreSeleccionado);
            rs = ps.executeQuery();

            while (rs.next()) {
                InstitucionModelo dato = new InstitucionModelo();
                dato.setNombreInstitucion(rs.getString("NombreInstitucion"));
                dato.setNit(rs.getString("Nit"));
                dato.setMunicipio(rs.getString("NombreMunicipio"));
                dato.setDepartamento(rs.getString("NombreDepartamento"));
                Comparar.add(dato);

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
        return Comparar;

    }

    public List<GraficoCompararModelo> LlenarGrafico(String Instituciones, String anio, String alcance) {
        List<GraficoCompararModelo> resultados = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "{CALL CompararEmisionesInstituciones(?, ?, ?)}";
            ps = conn.prepareStatement(sql);
            ps.setString(1, Instituciones);
            ps.setInt(2,Integer.parseInt( anio));
            ps.setString(3, alcance);
            rs = ps.executeQuery();

            while (rs.next()){
                GraficoCompararModelo dato = new GraficoCompararModelo();
                dato.setAlcance(rs.getString("Alcance"));
                dato.setNombrefuente(rs.getString("NombreFuente"));
                dato.setNombreInstitucion(rs.getString("NombreInstitucion"));
                dato.setTotal(rs.getDouble("Co2Aportado"));
                resultados.add(dato);
            }

        } catch (SQLException ex) {
            System.out.println("Error al ejecutar procedimiento almacenado: "+ex.getMessage());
        }
        return resultados;
    }
}
