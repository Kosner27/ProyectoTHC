package Modelo.Consultas;

import Modelo.modelo.CalcularModelo;
import Modelo.Conexion;
import Modelo.modelo.GraficorModelo;
import Modelo.modelo.ModeloInforme;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GraficoConsulta {

    private Connection conn;

    public GraficoConsulta(Conexion conexion) {
        this.conn = conexion.getConection();
    }

    public List<GraficorModelo> GraficoPorAlcance(String nombreInstitucion, String anioBase, String NombreMunicipio) {
        List<GraficorModelo> datos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("CALL LlenarGraficoAlcance(?, ?,?)");
            ps.setString(1, nombreInstitucion);
            ps.setString(2, anioBase);
            ps.setString(3, NombreMunicipio);
            rs = ps.executeQuery();

            while (rs.next()) {
                GraficorModelo dato = new GraficorModelo();
                dato.setAlcance(rs.getString("Alcance"));
                dato.setTotal(rs.getDouble("co2 por alcance")); // Asumiendo que "co2 por alcance" es el nombre correcto de la columna
                datos.add(dato);
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
        return datos;
    }

    public List<GraficorModelo> GraficoPorAlcanceNucleo(String nombreInstitucion, String anioBase, String NombreMunicipio,String nucleo ) {
        List<GraficorModelo> datos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("CALL LlenarGraficoAlcanceNucleo(?, ?, ?, ?)");
            ps.setString(1, nombreInstitucion);
            ps.setString(2, anioBase);
            ps.setString(3, NombreMunicipio);
            ps.setString(4,nucleo);
            rs = ps.executeQuery();

            while (rs.next()) {
                GraficorModelo dato = new GraficorModelo();
                dato.setAlcance(rs.getString("Alcance"));
                dato.setTotal(rs.getDouble("co2 por alcance")); // Asumiendo que "co2 por alcance" es el nombre correcto de la columna
                datos.add(dato);
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
        return datos;
    }


    public List<CalcularModelo> GraficoPorFuente(String nombreInstitucion, String anioBase, String NombreMuncicipio) {
        List<CalcularModelo> datos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("CALL LlenarGraficoFuente(?, ?,?)");
            ps.setString(1, nombreInstitucion);
            ps.setString(2, anioBase);
            ps.setString(3, NombreMuncicipio);
            rs = ps.executeQuery();

            while (rs.next()) {
                CalcularModelo dato = new CalcularModelo();
                dato.setNombreFuente(rs.getString("NombreFuente"));
                dato.setTotal1(rs.getDouble("co2 por fuente"));
                datos.add(dato);
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
        return datos;
    }

    public List<CalcularModelo> GraficoPorFuenteNucleo(String nombreInstitucion, String anioBase, String NombreMuncicipio, String nucleo) {
        List<CalcularModelo> datos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("CALL LlenarGraficoFuenteNucleo(?, ?, ?, ?)");
            ps.setString(1, nombreInstitucion);
            ps.setString(2, anioBase);
            ps.setString(3, NombreMuncicipio);
            ps.setString(4, nucleo);
            rs = ps.executeQuery();

            while (rs.next()) {
                CalcularModelo dato = new CalcularModelo();
                dato.setNombreFuente(rs.getString("NombreFuente"));
                dato.setTotal1(rs.getDouble("co2 por fuente"));
                datos.add(dato);
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
        return datos;
    }

    public List<GraficorModelo> GraficoPorAlcanceNucleoSumaTodosNucleos(String nombreInstitucion, String anioBase, String NombreMunicipio) {
        List<GraficorModelo> datos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("CALL LlenarGraficoAlcanceNucleoSumaTodosNucleos(?, ?, ?)");
            ps.setString(1, nombreInstitucion);
            ps.setString(2, anioBase);
            ps.setString(3, NombreMunicipio);
            rs = ps.executeQuery();

            while (rs.next()) {
                GraficorModelo dato = new GraficorModelo();
                dato.setAlcance(rs.getString("Alcance"));
                dato.setTotal(rs.getDouble("co2 por alcance")); // Asumiendo que "co2 por alcance" es el nombre correcto de la columna
                datos.add(dato);
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
        return datos;
    }

    public List<CalcularModelo> GraficoPorFuenteNucleoSumaTodosNucleos(String nombreInstitucion, String anioBase, String NombreMuncicipio) {
        List<CalcularModelo> datos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("CALL LlenarGraficoFuenteNucleoSumaTodosNucleo(?, ?, ?)");
            ps.setString(1, nombreInstitucion);
            ps.setString(2, anioBase);
            ps.setString(3, NombreMuncicipio);
            rs = ps.executeQuery();

            while (rs.next()) {
                CalcularModelo dato = new CalcularModelo();
                dato.setNombreFuente(rs.getString("NombreFuente"));
                dato.setTotal1(rs.getDouble("co2 por fuente"));
                datos.add(dato);
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
        return datos;
    }

    public List<ModeloInforme> datos(String nombre, String nomMunicipio) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ModeloInforme> instituciones = new ArrayList<>();

        try {
            ps = conn.prepareStatement("Select * from institucion i inner join  municipioinstitiucion mi on i.IdInstitucionAuto = mi.IdInstitucion inner join   municipio m on mi.idMuncipio = m.idMunicipio inner join Departamento d on m.idDepartamento = d.idDepartamento where m.NombreMunicipio = ? and i.NombreInstitucion = ?");
            ps.setString(1, nomMunicipio);
            ps.setString(2, nombre);
            rs = ps.executeQuery();

            while (rs.next()) {
                ModeloInforme institucion = new ModeloInforme();
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
