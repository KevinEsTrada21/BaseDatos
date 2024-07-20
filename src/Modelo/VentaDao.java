
package Modelo;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.filechooser.FileSystemView;

public class VentaDao {
    private Connection con;
    private Conexion cn = new Conexion();
    private PreparedStatement ps;
    private ResultSet rs;

    // Obtiene el último ID de venta
    public int obtenerIdVenta() {
        String sql = "SELECT ISNULL(MAX(id), 0) FROM ventas";
        int id = 0;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el ID de venta: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return id;
    }

    // Registra una nueva venta
    public int registrarVenta(Venta v) {
        String sql = "INSERT INTO ventas (cliente, vendedor, total, fecha) VALUES (?,?,?,?)";
        int idGenerado = 0;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, v.getCliente());
            ps.setString(2, v.getVendedor());
            ps.setDouble(3, v.getTotal());
            ps.setString(4, v.getFecha());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar la venta: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return idGenerado;
    }

    // Registra un detalle de la venta
    public boolean registrarDetalle(Detalle d) {
        String sql = "INSERT INTO detalle (id_pro, cantidad, precio, id_venta) VALUES (?,?,?,?)";
        boolean exito = false;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, d.getId_pro());
            ps.setInt(2, d.getCantidad());
            ps.setDouble(3, d.getPrecio());
            ps.setInt(4, d.getId());
            int filasAfectadas = ps.executeUpdate();
            exito = (filasAfectadas > 0);
        } catch (SQLException e) {
            System.out.println("Error al registrar el detalle: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return exito;
    }

    // Actualiza el stock de un producto
    public boolean actualizarStock(int cant, int id) {
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        boolean exito = false;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, cant);
            ps.setInt(2, id);
            int filasAfectadas = ps.executeUpdate();
            exito = (filasAfectadas > 0);
        } catch (SQLException e) {
            System.out.println("Error al actualizar el stock: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return exito;
    }

    // Lista todas las ventas
    public List<Venta> listarVentas() {
        List<Venta> listaVenta = new ArrayList<>();
        String sql = "SELECT c.id AS id_cli, c.nombre, v.* FROM clientes c INNER JOIN ventas v ON c.id = v.cliente";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Venta vent = new Venta();
                vent.setId(rs.getInt("id"));
                vent.setNombre_cli(rs.getString("nombre"));
                vent.setVendedor(rs.getString("vendedor"));
                vent.setTotal(rs.getDouble("total"));
                listaVenta.add(vent);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar las ventas: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return listaVenta;
    }

    // Busca una venta por ID
    public Venta buscarVenta(int id) {
        Venta venta = new Venta();
        String sql = "SELECT * FROM ventas WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                venta.setId(rs.getInt("id"));
                venta.setCliente(rs.getInt("cliente"));
                venta.setTotal(rs.getDouble("total"));
                venta.setVendedor(rs.getString("vendedor"));
                venta.setFecha(rs.getString("fecha"));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar la venta: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return venta;
    }

    // Genera un PDF para la venta
    public void generarPdf(int idVenta, int cliente, double total, String usuario) {
        try {
            Date date = new Date();
            String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
            File archivo = new File(url + "venta.pdf");
            FileOutputStream fos = new FileOutputStream(archivo);
            Document doc = new Document();
            PdfWriter.getInstance(doc, fos);
            doc.open();
            
            Image img = Image.getInstance(getClass().getResource("/Img/factura.png"));
            Font negrita = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLUE);
            
            // Fecha y encabezado
            Paragraph fecha = new Paragraph();
            fecha.add(Chunk.NEWLINE);
            fecha.add("Vendedor: " + usuario + "\nFolio: " + idVenta + "\nFecha: "
                    + new SimpleDateFormat("dd/MM/yyyy").format(date) + "\n\n");
            
            PdfPTable encabezado = new PdfPTable(4);
            encabezado.setWidthPercentage(100);
            encabezado.getDefaultCell().setBorder(0);
            float[] columnWidthsEncabezado = new float[]{20f, 30f, 70f, 40f};
            encabezado.setWidths(columnWidthsEncabezado);
            encabezado.setHorizontalAlignment(Element.ALIGN_LEFT);
            encabezado.addCell(img);
            encabezado.addCell("");
            
            // Información de la empresa
            String configSql = "SELECT * FROM config";
            try {
                con = cn.getConnection();
                ps = con.prepareStatement(configSql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    String mensaje = rs.getString("mensaje");
                    encabezado.addCell("Ruc: " + rs.getString("ruc") + "\nNombre: " + rs.getString("nombre") +
                            "\nTeléfono: " + rs.getString("telefono") + "\nDirección: " + rs.getString("direccion") + "\n\n");
                }
            } catch (SQLException e) {
                System.out.println("Error al obtener la configuración: " + e.getMessage());
            }
            encabezado.addCell(fecha);
            doc.add(encabezado);
            
            // Datos del cliente
            Paragraph clienteParrafo = new Paragraph();
            clienteParrafo.add(Chunk.NEWLINE);
            clienteParrafo.add("DATOS DEL CLIENTE\n\n");
            doc.add(clienteParrafo);

            PdfPTable tablaCliente = new PdfPTable(3);
            tablaCliente.setWidthPercentage(100);
            tablaCliente.getDefaultCell().setBorder(0);
            float[] columnWidthsCliente = new float[]{50f, 25f, 25f};
            tablaCliente.setWidths(columnWidthsCliente);
            tablaCliente.setHorizontalAlignment(Element.ALIGN_LEFT);
            
            PdfPCell cliNom = new PdfPCell(new Phrase("Nombre", negrita));
            PdfPCell cliTel = new PdfPCell(new Phrase("Teléfono", negrita));
            PdfPCell cliDir = new PdfPCell(new Phrase("Dirección", negrita));
            cliNom.setBorder(Rectangle.NO_BORDER);
            cliTel.setBorder(Rectangle.NO_BORDER);
            cliDir.setBorder(Rectangle.NO_BORDER);
            tablaCliente.addCell(cliNom);
            tablaCliente.addCell(cliTel);
            tablaCliente.addCell(cliDir);
            
            String clienteSql = "SELECT * FROM clientes WHERE id = ?";
            try {
                ps = con.prepareStatement(clienteSql);
                ps.setInt(1, cliente);
                rs = ps.executeQuery();
                if (rs.next()) {
                    tablaCliente.addCell(rs.getString("nombre"));
                    tablaCliente.addCell(rs.getString("telefono"));
                    tablaCliente.addCell(rs.getString("direccion") + "\n\n");
                } else {
                    tablaCliente.addCell("Público en General");
                    tablaCliente.addCell("S/N");
                    tablaCliente.addCell("S/N" + "\n\n");
                }
            } catch (SQLException e) {
                System.out.println("Error al obtener los datos del cliente: " + e.getMessage());
            }
            doc.add(tablaCliente);
            
            // Detalles de la venta
            PdfPTable tablaVenta = new PdfPTable(4);
            tablaVenta.setWidthPercentage(100);
            tablaVenta.getDefaultCell().setBorder(0);
            float[] columnWidths = new float[]{10f, 50f, 15f, 15f};
            tablaVenta.setWidths(columnWidths);
            tablaVenta.setHorizontalAlignment(Element.ALIGN_LEFT);
            
            PdfPCell c1 = new PdfPCell(new Phrase("Cant.", negrita));
            PdfPCell c2 = new PdfPCell(new Phrase("Descripción", negrita));
            PdfPCell c3 = new PdfPCell(new Phrase("P. unt.", negrita));
            PdfPCell c4 = new PdfPCell(new Phrase("P. Total", negrita));
            c1.setBorder(Rectangle.NO_BORDER);
            c2.setBorder(Rectangle.NO_BORDER);
            c3.setBorder(Rectangle.NO_BORDER);
            c4.setBorder(Rectangle.NO_BORDER);
            c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tablaVenta.addCell(c1);
            tablaVenta.addCell(c2);
            tablaVenta.addCell(c3);
            tablaVenta.addCell(c4);
            
            String detalleSql = "SELECT d.id, d.id_pro, d.id_venta, d.precio, d.cantidad, p.nombre FROM detalle d INNER JOIN productos p ON d.id_pro = p.id WHERE d.id_venta = ?";
            try {
                ps = con.prepareStatement(detalleSql);
                ps.setInt(1, idVenta);
                rs = ps.executeQuery();
                while (rs.next()) {
                    double subTotal = rs.getInt("cantidad") * rs.getDouble("precio");
                    tablaVenta.addCell(rs.getString("cantidad"));
                    tablaVenta.addCell(rs.getString("nombre"));
                    tablaVenta.addCell(rs.getString("precio"));
                    tablaVenta.addCell(String.valueOf(subTotal));
                }
            } catch (SQLException e) {
                System.out.println("Error al obtener los detalles de la venta: " + e.getMessage());
            }
            doc.add(tablaVenta);
            
            // Información adicional
            Paragraph info = new Paragraph();
            info.add(Chunk.NEWLINE);
            info.add("Total S/: " + total);
            info.setAlignment(Element.ALIGN_RIGHT);
            doc.add(info);
            
            Paragraph firma = new Paragraph();
            firma.add(Chunk.NEWLINE);
            firma.add("Cancelación \n\n");
            firma.add("------------------------------------\n");
            firma.add("Firma \n");
            firma.setAlignment(Element.ALIGN_CENTER);
            doc.add(firma);
            
            // Mensaje de la empresa
            String mensaje = "";
            try {
                con = cn.getConnection();
                ps = con.prepareStatement("SELECT mensaje FROM config");
                rs = ps.executeQuery();
                if (rs.next()) {
                    mensaje = rs.getString("mensaje");
                }
            } catch (SQLException e) {
                System.out.println("Error al obtener el mensaje de la empresa: " + e.getMessage());
            }
            Paragraph mensajeParrafo = new Paragraph();
            mensajeParrafo.add(Chunk.NEWLINE);
            mensajeParrafo.add(mensaje);
            mensajeParrafo.setAlignment(Element.ALIGN_CENTER);
            doc.add(mensajeParrafo);
            
            doc.close();
            fos.close();
            Desktop.getDesktop().open(archivo);
        } catch (DocumentException | IOException e) {
            System.out.println("Error al generar el PDF: " + e.getMessage());
        }
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