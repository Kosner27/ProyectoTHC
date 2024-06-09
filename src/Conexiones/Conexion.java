package Conexiones;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Conexion {

    public static final String URL = "jdbc:mysql://localhost:3306/ProyectoTHDC"; // Cambia esto a tu URL de conexión
    public static final String USER = "root"; // Cambia esto a tu usuario de la base de datos
    public static final String PASSWORD = "willmar"; // Cambia esto a tu contraseña de la base de datos
   public Connection connection;

    public Conexion() {
        try {
            // Intentamos cargar el controlador de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Intentamos establecer la conexión
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el controlador de MySQL");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
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
        Connection conn = conexion.getConnection();
        // Cerrar la conexión
        conexion.closeConnection();
    }
}
