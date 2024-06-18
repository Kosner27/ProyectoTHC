package Modelo;
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

    public List<TendenciaModelo> getNombre(String NombreSeleccionado) {
        List<TendenciaModelo> emisiones = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement("CALL GraficoHistorico(?)");
            ps.setString(1, NombreSeleccionado);
            rs = ps.executeQuery();
            while(rs.next()){
                TendenciaModelo dato = new TendenciaModelo();

                dato.setAlcance(rs.getString("Alcance"));
                dato.setCo2(rs.getDouble("co2_por_año")); // Asumiendo que "co2 por alcance" es el nombre correcto de la columna
                dato.setAnioBase(rs.getInt("anioBase"));
                emisiones.add(dato);
            }
        }catch (SQLException ex){
            System.out.printf(ex.getMessage());
        }
        finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return emisiones;

    }



}
