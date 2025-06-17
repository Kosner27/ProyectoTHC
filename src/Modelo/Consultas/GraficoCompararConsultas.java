package Modelo.Consultas;


import Modelo.Conexion;
import Modelo.modelo.ModeloGraficoComparar;
import Modelo.modelo.ModeloInstitucion;
import Modelo.modelo.ModeloNucleo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GraficoCompararConsultas {
    private Connection conn;

    public GraficoCompararConsultas(Conexion conexion) {
        this.conn = conexion.getConection();
    }

    public List<ModeloInstitucion> llenarTabla(String NombreSeleccionado, String nombreMunicipio) {
        List<ModeloInstitucion> Comparar = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("Select * from institucion i inner join  " +
                    "municipioinstitiucion mi on i.idInstitucionAuto = mi.IdInstitucion inner join " +
                    " municipio m on mi.idMuncipio = m.idMunicipio inner join " +
                    "Departamento d on m.idDepartamento = d.idDepartamento " +
                    "where m.NombreMunicipio = ? and i.NombreInstitucion = ? ");
            ps.setString(1, nombreMunicipio);
            ps.setString(2, NombreSeleccionado);
            rs = ps.executeQuery();

            while (rs.next()) {
                ModeloInstitucion dato = new ModeloInstitucion();
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
    public List<ModeloNucleo> llenarTablaConNucleo(String NombreSeleccionado, String municipio , String Nucleo) {
        List<ModeloNucleo> Comparar = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("Select n.NombreNucleo,  i.NombreInstitucion, i.Nit, m.NombreMunicipio, d.NombreDepartamento\n" +
                    " from nucleoinstitucion n\n" +
                    "INNER JOIN institucion i ON i.idInstitucionAuto = n.idInstitucion\n" +
                    "inner join municipioinstitiucion mi on mi.IdInstitucion= i.idInstitucionAuto\n" +
                    "inner join municipio m on m.idMunicipio = mi.idMuncipio\n" +
                    "inner join Departamento d on d.idDepartamento=m.idDepartamento\n" +
                    "where i.NombreInstitucion = ? and n.NombreNucleo = ? and m.NombreMunicipio = ?;");
            ps.setString(1, NombreSeleccionado);
            ps.setString(2, Nucleo);
            ps.setString(3,municipio);
            rs = ps.executeQuery();

            while (rs.next()) {
                ModeloNucleo dato = new ModeloNucleo();
                dato.setNombreNucleo(rs.getString("NombreNucleo"));
                dato.setNombreIns(rs.getString("NombreInstitucion"));
                dato.setIdInstitucion(rs.getString("Nit"));
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

    public List<ModeloGraficoComparar> LlenarGrafico(String instituciones, String anio, String alcance, String campus) {
        List<ModeloGraficoComparar> resultados = new ArrayList<>();
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            // Validar parámetros
            if (instituciones == null || instituciones.trim().isEmpty()) {
                throw new IllegalArgumentException("El parámetro instituciones no puede estar vacío.");
            }
            if (anio == null || anio.trim().isEmpty() || !anio.matches("\\d+")) {
                throw new IllegalArgumentException("El parámetro anio debe ser un número válido.");
            }
            if (alcance == null || alcance.trim().isEmpty()) {
                throw new IllegalArgumentException("El parámetro alcance no puede estar vacío.");
            }

            // Formatear los parámetros (manejo de listas)
            String formattedInstituciones = formatValues(instituciones);

            // Verificar si campus es null, vacío o específico, en cuyo caso no debe ser enviado como NULL
            String formattedCampus = null;
            if (campus != null && !campus.trim().isEmpty()) {
                if ("Sumatoria de todos los nucleos registrados".equalsIgnoreCase(campus)) {
                    formattedCampus = "Sumatoria de todos los nucleos registrados";  // Valor específico
                } else {
                    formattedCampus = formatValues(campus);  // Formato de valores si es una lista
                }
            }

            // Preparar la llamada al procedimiento almacenado
            String sql = "{CALL CompararEmisionesInstituciones(?, ?, ?, ?)}";
            cs = conn.prepareCall(sql);

            // Establecer los parámetros
            cs.setString(1, formattedInstituciones); // Instituciones
            cs.setInt(2, Integer.parseInt(anio));    // Año
            cs.setString(3, alcance);               // Alcance

            // Configurar el parámetro de campus
            if (formattedCampus == null) {
                cs.setNull(4, Types.VARCHAR);       // NULL indica que no se aplica filtro de campus
            } else {
                cs.setString(4, formattedCampus);   // Filtro por campus específico
            }


            rs = cs.executeQuery();

            // Procesar resultados
            while (rs.next()) {
                ModeloGraficoComparar dato = new ModeloGraficoComparar();
                dato.setAlcance(rs.getString("Alcance"));
                dato.setNombrefuente(rs.getString("NombreFuente"));
                dato.setNombreInstitucion(rs.getString("NombreInstitucion"));
                dato.setTotal(rs.getDouble("Co2Aportado"));
                dato.setNucleo(rs.getString("nucleo")); // Lista de núcleos
                resultados.add(dato);
            }

        } catch (SQLException ex) {
            System.err.println("Error al ejecutar procedimiento almacenado: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            System.err.println("Error en los parámetros: " + ex.getMessage());
        } finally {
            // Liberar recursos
            try {
                if (rs != null) rs.close();
                if (cs != null) cs.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        System.out.println(resultados);
        return resultados;
    }

    // Método para formatear valores de cadenas (ejemplo: 'valor1','valor2')
    private String formatValues(String values) {
        return Arrays.stream(values.split(","))
                .map(String::trim) // Eliminar espacios en blanco
                .map(value -> "'" + value.replace("'", "") + "'") // Envolver en comillas simples, eliminando duplicadas
                .collect(Collectors.joining(","));
    }


}


