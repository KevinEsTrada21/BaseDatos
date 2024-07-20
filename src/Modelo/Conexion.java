package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private Connection con;

    // Método para obtener la conexión
    public Connection getConnection() {
        try {
            // Registrar el driver de SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            // Configurar la URL de la base de datos
            String url = "jdbc:sqlserver://localhost:1433;databaseName=sistemaventa";
            
            // Configurar el usuario y la contraseña
            String user = "KEVINESTRADA"; // Cambia esto a tu usuario de SQL Server
            String password = "your_password_here"; // Cambia esto a tu contraseña de SQL Server
            
            // Establecer la conexión
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa");
            return con;
        } catch (ClassNotFoundException e) {
            System.out.println("Driver no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return null;
    }
}
