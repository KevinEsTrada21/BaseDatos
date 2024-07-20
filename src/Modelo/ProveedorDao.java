
package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDao {
    private Connection con;
    private Conexion cn = new Conexion();
    private PreparedStatement ps;
    private ResultSet rs;

    // Registrar un proveedor
    public boolean RegistrarProveedor(Proveedor pr) {
        String sql = "INSERT INTO proveedor(ruc, nombre, telefono, direccion) VALUES (?, ?, ?, ?)";
        boolean exito = false;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, pr.getRuc());
            ps.setString(2, pr.getNombre());
            ps.setString(3, pr.getTelefono());
            ps.setString(4, pr.getDireccion());
            int filasAfectadas = ps.executeUpdate();
            exito = (filasAfectadas > 0);
        } catch (SQLException e) {
            System.out.println("Error al registrar proveedor: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return exito;
    }

    // Listar todos los proveedores
    public List<Proveedor> ListarProveedor() {
        List<Proveedor> listaPr = new ArrayList<>();
        String sql = "SELECT * FROM proveedor";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Proveedor pr = new Proveedor();
                pr.setId(rs.getInt("id"));
                pr.setRuc(rs.getString("ruc"));
                pr.setNombre(rs.getString("nombre"));
                pr.setTelefono(rs.getString("telefono"));
                pr.setDireccion(rs.getString("direccion"));
                listaPr.add(pr);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar proveedores: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return listaPr;
    }

    // Eliminar un proveedor por ID
    public boolean EliminarProveedor(int id) {
        String sql = "DELETE FROM proveedor WHERE id = ?";
        boolean exito = false;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            exito = (filasAfectadas > 0);
        } catch (SQLException e) {
            System.out.println("Error al eliminar proveedor: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return exito;
    }

    // Modificar un proveedor
    public boolean ModificarProveedor(Proveedor pr) {
        String sql = "UPDATE proveedor SET ruc=?, nombre=?, telefono=?, direccion=? WHERE id=?";
        boolean exito = false;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, pr.getRuc());
            ps.setString(2, pr.getNombre());
            ps.setString(3, pr.getTelefono());
            ps.setString(4, pr.getDireccion());
            ps.setInt(5, pr.getId());
            int filasAfectadas = ps.executeUpdate();
            exito = (filasAfectadas > 0);
        } catch (SQLException e) {
            System.out.println("Error al modificar proveedor: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return exito;
    }

    // Método para cerrar recursos
    private void cerrarRecursos() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            System.out.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
}