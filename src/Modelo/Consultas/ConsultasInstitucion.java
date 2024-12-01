package Modelo.Consultas;

import Modelo.Conexion;
import Modelo.modelo.InstitucionModelo;
import Modelo.modelo.Municipio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsultasInstitucion {

    public boolean ActualizarInstitucion(String nombreIns, String Municipio, Integer Hectareas, String nit) {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();

        String sql = "Call ActualizarInstitucion(?, ?, ?, ? )";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nit);
            ps.setString(2, nombreIns);
            ps.setString(3, Municipio);
            ps.setInt(4, Hectareas);

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
            ps = conn.prepareStatement("Select NombreInstitucion, Nit, NombreMunicipio, NombreDepartamento from " +
                    "Institucion i inner join  municipioinstitiucion mi on i.idInstitucionAuto = mi.IdInstitucion " +
                    " inner join Municipio m  on m.IdMunicipio = mi.idMuncipio " +
                    "inner join Departamento d on m.idDepartamento = d.IdDepartamento " +
                    "where NombreInstitucion = ? and NombreMunicipio = ? ");
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

    public List<InstitucionModelo>LlenarTablas(){
        Conexion conexion = new Conexion();
        List<InstitucionModelo> datos = new ArrayList<>();
        Connection conn = conexion.getConection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement("SELECT i.NombreInstitucion, i.Nit, " +
                    " CASE  WHEN n.NombreNucleo IS NOT NULL THEN 0  ELSE i.hectareas " +
                    "END AS hectareas, d.NombreDepartamento,m.NombreMunicipio, " +
                    " COALESCE(n.NombreNucleo, '') AS NombreNucleo,  " +
                    "COALESCE(n.Hectareas, '') AS HectareasNucleo  " +
                    "FROM departamento d " +
                    "INNER JOIN municipio m ON d.idDepartamento = m.idDepartamento " +
                    "INNER JOIN municipioinstitiucion mi ON m.idMunicipio = mi.idMuncipio " +
                    "INNER JOIN institucion i ON i.IdInstitucionAuto = mi.IdInstitucion " +
                    "LEFT JOIN nucleoinstitucion n ON i.IdInstitucionAuto = n.IdInstitucion " +
                    "WHERE i.Activo = 1  AND (n.Activo = 1 OR n.NombreNucleo IS NULL) ");

            rs = ps.executeQuery();

            while(rs.next()){
                InstitucionModelo mod = new InstitucionModelo();
                mod.setNombreInstitucion(rs.getString("NombreInstitucion"));
                mod.setNucleo(rs.getString("NombreNucleo"));
                mod.setHectareas(rs.getInt("hectareas"));
                mod.setDepartamento(rs.getString("NombreDepartamento"));
                mod.setMunicipio(rs.getString("NombreMunicipio"));
                mod.setNit(rs.getString("Nit"));
                mod.setHectareasNucleo(rs.getInt("HectareasNucleo"));
                datos.add(mod);
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
        return datos;
    }

    public List<InstitucionModelo>BuscarPorNIT(String nombreInstitucion){
        Conexion conexion = new Conexion();
        List<InstitucionModelo> datos = new ArrayList<>();
        Connection conn = conexion.getConection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement("SELECT i.NombreInstitucion, i.Nit, " +
                    "CASE WHEN n.NombreNucleo IS NOT NULL THEN 0 " +
                    "ELSE i.hectareas " +
                    "END AS hectareas, d.NombreDepartamento, m.NombreMunicipio, " +
                    "COALESCE(n.NombreNucleo, '') AS NombreNucleo,  " +
                    "COALESCE(n.Hectareas, '') AS HectareasNucleo  " +
                    "FROM departamento d INNER JOIN municipio m ON d.idDepartamento = m.idDepartamento " +
                    "INNER JOIN municipioinstitiucion mi ON m.idMunicipio = mi.idMuncipio " +
                    "INNER JOIN institucion i ON i.IdInstitucionAuto = mi.IdInstitucion " +
                    "LEFT JOIN nucleoinstitucion n ON i.IdInstitucionAuto = n.IdInstitucion where i.NombreInstitucion = ?");
            ps.setString(1, nombreInstitucion);
            rs = ps.executeQuery();

            while(rs.next()){
                InstitucionModelo institucionModelo = new InstitucionModelo();
                institucionModelo.setNombreInstitucion(rs.getString("NombreInstitucion"));
                institucionModelo.setNit(rs.getString("Nit"));
                institucionModelo.setHectareas(rs.getInt("hectareas"));
                institucionModelo.setDepartamento(rs.getString("NombreDepartamento"));
                institucionModelo.setMunicipio(rs.getString("NombreMunicipio"));
                institucionModelo.setNucleo(rs.getString("NombreNucleo"));
                institucionModelo.setHectareasNucleo(rs.getInt("HectareasNucleo"));
                datos.add(institucionModelo);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();

        }finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
        return datos;
    }

    public boolean ActualizarInstitucionConNucleo(String nombreIns, String Municipio, Integer Hectareas, String nucleo)
    {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();

        String sql = "Call ActualizarInstitucionDeHectareasNucleo(?, ?, ?, ? )";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nucleo);
            ps.setString(2, nombreIns);
            ps.setString(3, Municipio);
            ps.setInt(4, Hectareas);

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

    public boolean ActualizarInstitucionNitNucleo(String nombreIns, String Municipio, String nit) {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();

        String sql = "Call ActualizarInstitucionConNucleoNit(?, ?, ? )";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nit);
            ps.setString(2, nombreIns);
            ps.setString(3, Municipio);

            ps.execute();
            System.out.println(nombreIns + " " + Municipio + " " + nit);
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

    public boolean InsertarInstitucion(InstitucionModelo institucionModelo, Municipio municipio) {
        String sql = "call insertarInstitucion(?,?,?,?)";
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, municipio.getNombreM());
            ps.setString(2, institucionModelo.getNit());
            ps.setString(3, institucionModelo.getNombreInstitucion());
            ps.setInt(4, institucionModelo.getHectareasNucleo());
            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> obtenerInstituciones() {
        List<String> instituciones = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        ResultSet rs = null;
        String sql =
                "SELECT NombreInstitucion AS NombreInstitucion " +
                        "FROM institucion i order by NombreInstitucion "
                        ;

        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                rs = ps.executeQuery();
                while (rs.next()) {
                    instituciones.add(rs.getString("NombreInstitucion"));
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return instituciones;
    }

    public List<String> obtenerAnioBase(String nombreInstitucion, String nombreMunicipio) {
        List<String> aniosBase = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        ResultSet rs = null;
        String sql = "SELECT DISTINCT en.anioBase FROM emisionnucleo en " +
                "INNER JOIN institucion i ON en.idInstitucion = i.idInstitucionAuto " +
                "INNER JOIN emision e ON en.idEmision = e.idEmision " +
                "INNER JOIN municipioinstitiucion mi ON i.idInstitucionAuto = mi.IdInstitucion " +
                "INNER JOIN municipio m ON mi.IdMuncipio = m.IdMunicipio " +
                "WHERE i.NombreInstitucion = ? AND m.NombreMunicipio = ? " +
                "GROUP BY en.anioBase";

        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nombreInstitucion);
                ps.setString(2, nombreMunicipio);

                rs = ps.executeQuery();
                while (rs.next()) {
                    aniosBase.add(rs.getString("anioBase"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return aniosBase;
    }

    public List<String> obtenerMunicipios(String nombreInstitucion) {
        List<String> municipios = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        ResultSet rs = null;
        String sql = "SELECT NombreMunicipio FROM municipio m " +
                "INNER JOIN municipioinstitiucion mi ON mi.idMuncipio = m.idMunicipio " +
                "INNER JOIN institucion i ON i.idInstitucionAuto = mi.IdInstitucion " +
                "WHERE i.NombreInstitucion = ?";

        try {
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, nombreInstitucion); // Establecer el valor del parámetro
                    rs = ps.executeQuery();

                    // Verificar si se obtuvieron resultados
                    while (rs.next()) {
                        String nombreMunicipio = rs.getString("NombreMunicipio");
                        municipios.add(nombreMunicipio); // Agregar municipio a la lista
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

        return municipios;
    }

    public boolean eliminarInstiucion(String nombreInstitucion, String municipio){
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        String sql = "Call eliminarInstitucion(?,?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, nombreInstitucion);
            ps.setString(2,municipio);
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

    public boolean eliminarNucleo(String campus){
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        String sql = "Call EliminarCampus(?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, campus);
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