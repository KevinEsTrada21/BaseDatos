
package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginDAO {
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;
    private Conexion cn = new Conexion();
    
    // Método para iniciar sesión
    public Login log(String correo, String pass) {
        Login l = null;
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND pass = ?";
        try {
            con = cn.getConnection();
            if (con != null) {
                ps = con.prepareStatement(sql);
                ps.setString(1, correo);
                ps.setString(2, pass);
                rs = ps.executeQuery();
                if (rs.next()) {
                    l = new Login();
                    l.setId(rs.getInt("id"));
                    l.setNombre(rs.getString("nombre"));
                    l.setCorreo(rs.getString("correo"));
                    l.setPass(rs.getString("pass"));
                    l.setRol(rs.getString("rol"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en la consulta de inicio de sesión: " + e.getMessage());
        } finally {
            closeResources();
        }
        return l;
    }
    
    // Método para registrar un nuevo usuario
    public boolean registrar(Login reg) {
        String sql = "INSERT INTO usuarios (nombre, correo, pass, rol) VALUES (?,?,?,?)";
        boolean isRegistered = false;
        try {
            con = cn.getConnection();
            if (con != null) {
                ps = con.prepareStatement(sql);
                ps.setString(1, reg.getNombre());
                ps.setString(2, reg.getCorreo());
                ps.setString(3, reg.getPass());
                ps.setString(4, reg.getRol());
                int rowsAffected = ps.executeUpdate();
                isRegistered = (rowsAffected > 0);
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar el usuario: " + e.getMessage());
        } finally {
            closeResources();
        }
        return isRegistered;
    }
    
    // Método para listar todos los usuarios
    public List<Login> listarUsuarios() {
        List<Login> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try {
            con = cn.getConnection();
            if (con != null) {
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Login lg = new Login();
                    lg.setId(rs.getInt("id"));
                    lg.setNombre(rs.getString("nombre"));
                    lg.setCorreo(rs.getString("correo"));
                    lg.setRol(rs.getString("rol"));
                    lista.add(lg);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar los usuarios: " + e.getMessage());
        } finally {
            closeResources();
        }
        return lista;
    }

    // Método para cerrar recursos
    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            System.out.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
}