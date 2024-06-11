package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private final String user = "root";
    private final String password = "willmar";
    private final String url = "jdbc:mysql://localhost:3306/ProyectoTHDC";
    private Connection conn;

    public Connection getConection() {
        try {
            // Intentamos cargar el controlador de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Intentamos establecer la conexión
            this.conn = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el controlador de MySQL");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos");
            e.printStackTrace();
        }
        return conn;
    }

    public Connection getConn() {
        return conn;
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexión cerrada");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // Crear una instancia de la clase Conexion
        Conexion conexion = new Conexion();
        // Obtener la conexión para realizar operaciones con la base de datos
        Connection conn = conexion.getConection();
        // Cerrar la conexión
        conexion.closeConnection();
    }
}
