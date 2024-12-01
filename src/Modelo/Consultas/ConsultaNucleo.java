package Modelo.Consultas;

import Modelo.Conexion;
import Modelo.modelo.*;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultaNucleo {

    private final Connection conn;

    public ConsultaNucleo(Conexion conexion) {
        this.conn = conexion.getConection();
    }

    public boolean RegistrarNucleo(Nucleo nucleo, Municipio municipio, InstitucionModelo ins) {
        String sql = "CALL EmisionNucleo(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nucleo.getNombreNucleo());
            ps.setString(2, municipio.getNombreM());
            ps.setString(3, ins.getNombreInstitucion());
            ps.setInt(4, nucleo.getHectareas());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(ConsultaNucleo.class.getName()).log(Level.SEVERE, "Error al registrar el núcleo", e);
            return false;
        }
    }

    public int ExisteNucleo(Nucleo nucleo) {
        String sql = "SELECT COUNT(*) FROM nucleoinstitucion WHERE NombreNucleo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nucleo.getNombreNucleo());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            Logger.getLogger(ConsultaNucleo.class.getName()).log(Level.SEVERE, "Error al verificar existencia de núcleo", e);
            return 0;
        }
    }

    public boolean InsertarCalculoConNucleo(CalcularModelo calcularModelo, Nucleo nucleo) {
        String sql = "CALL InsertarCalculoConNucleo(?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nucleo.getNombreNucleo());
            ps.setInt(2, calcularModelo.getAnioBase());
            ps.setDouble(3, calcularModelo.getCantidadConsumidad());
            ps.setString(4, calcularModelo.getNombreFuente());
            ps.setDouble(5, calcularModelo.getTotal1());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(ConsultaNucleo.class.getName()).log(Level.SEVERE, "Error al insertar cálculo con núcleo", e);
            return false;
        }
    }

    public String UltimoRegistro() {
        String sql = "SELECT NombreNucleo FROM nucleoinstitucion ORDER BY IdNucleo DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("NombreNucleo");
            }
        } catch (SQLException e) {
            Logger.getLogger(ConsultaNucleo.class.getName()).log(Level.SEVERE, "Error al obtener último registro de núcleo", e);
        }
        return null;
    }

    public int TieneNucleo(String ins) {
        String sql = "SELECT COUNT(nu.IdNucleo) FROM nucleoinstitucion nu " +
                "INNER JOIN municipio m ON nu.idMunicipio = m.idMunicipio " +
                "INNER JOIN municipioinstitiucion mi ON m.idMunicipio = mi.idMuncipio " +
                "INNER JOIN institucion i ON i.IdInstitucionAuto = mi.IdInstitucion " +
                "WHERE i.NombreInstitucion = ? GROUP BY nu.NombreNucleo";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ins);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            Logger.getLogger(ConsultaNucleo.class.getName()).log(Level.SEVERE, "Error al verificar si una institución tiene núcleo", e);
            return 0;
        }
    }

    // Nuevo método para cargar los núcleos en un combo box
    public List<String> cargarNucleos(String nombreInstitucion) {
        List<String> nucleos = new ArrayList<>();
        String sql = "SELECT nu.NombreNucleo FROM nucleoinstitucion nu " +
                "INNER JOIN municipio m ON nu.idMunicipio = m.idMunicipio " +
                "INNER JOIN municipioinstitiucion mi ON m.idMunicipio = mi.idMuncipio " +
                "INNER JOIN institucion i ON i.IdInstitucionAuto = mi.IdInstitucion " +
                "WHERE nu.Activo = 1 AND i.NombreInstitucion = ? GROUP BY nu.NombreNucleo";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreInstitucion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    nucleos.add(rs.getString("NombreNucleo"));
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(ConsultaNucleo.class.getName()).log(Level.SEVERE, "Error al cargar núcleos", e);
        }
        return nucleos;
    }

    public List<String> obtenerAnosBase(String nombreInstitucion, String nombreMunicipio, String nombreNucleo) {
        List<String> anosBase = new ArrayList<>();
        String sql = "SELECT anioBase FROM emisionnucleo en " +
                "INNER JOIN nucleoinstitucion n ON n.IdNucleo = en.idNucleo " +
                "INNER JOIN municipio m ON m.IdMunicipio = n.IdMunicipio " +
                "INNER JOIN municipioinstitiucion mi ON m.IdMunicipio = mi.idMuncipio " +
                "INNER JOIN institucion i ON i.idInstitucionAuto = mi.IdInstitucion " +
                "WHERE i.NombreInstitucion = ? AND m.NombreMunicipio = ? AND n.NombreNucleo = ? " +
                " group by en.anioBase ORDER BY en.anioBase";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreInstitucion);
            ps.setString(2, nombreMunicipio);
            ps.setString(3, nombreNucleo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    anosBase.add(rs.getString("anioBase"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return anosBase;
    }

    // Método que consulta los años base de las emisiones según la institución y municipio
    public List<String> obteneranioParaSumaTodosLosNucleos(String nombreInstitucion, String nombreMunicipio) {
        List<String> aniosBase = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        ResultSet rs = null;

        String sql = "SELECT en.anioBase " +
                "FROM emisionnucleo en " +
                "INNER JOIN nucleoinstitucion n ON n.IdNucleo = en.idNucleo " +
                "INNER JOIN municipio m ON m.IdMunicipio = n.IdMunicipio " +
                "INNER JOIN municipioinstitiucion mi ON m.IdMunicipio = mi.idMuncipio " +
                "INNER JOIN institucion i ON i.idInstitucionAuto = mi.IdInstitucion " +
                "WHERE i.NombreInstitucion = ? " +
                "AND m.NombreMunicipio = ? " +
                "GROUP BY en.anioBase " +
                "HAVING COUNT(en.anioBase) >= 1 " +
                "ORDER BY en.anioBase";

        try {
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, nombreInstitucion); // Establecer el valor de la institución
                    ps.setString(2, nombreMunicipio); // Establecer el valor del municipio

                    rs = ps.executeQuery(); // Ejecutar la consulta

                    // Obtener los resultados y agregarlos a la lista
                    while (rs.next()) {
                        String anioBase = rs.getString("anioBase");
                        aniosBase.add(anioBase); // Agregar el año a la lista
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return aniosBase;
    }
}
