package dao;

import utilidades.ConexionDB; 
import modelo.Persona;
import modelo.Administrador;
import modelo.Voluntario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoluntarioDAO {

    private Voluntario mapear(ResultSet rs) throws SQLException {
        try {
            Voluntario v = new Voluntario();
            // Datos heredados de la tabla PERSONA
            v.setIdPersona(rs.getInt("id_persona")); // ¡Nuevo setter que debes agregar a tu clase Persona/Voluntario!
            v.setCedula(rs.getString("cedula"));
            v.setNombres_completos(rs.getString("nombres_completos"));
            v.setCorreo(rs.getString("correo_electronico"));
            v.setTelefono(rs.getString("telefono"));
            v.setGenero(rs.getString("genero"));
            v.setEstado(rs.getString("estado"));
            
            // Datos específicos de la tabla VOLUNTARIO
            v.setId_voluntario(rs.getInt("id_voluntario"));
            v.setDisponibilidad_dias(rs.getString("disponibilidad"));
            v.setHabilidades(rs.getString("habilidades"));
            
            return v;
        } catch (IllegalArgumentException e) {
            System.err.println("Error al mapear: " + e.getMessage());
            return null;
        }
    }

    public void insertar(Voluntario v) throws SQLException {
        String sqlPersona = "INSERT INTO PERSONA (cedula, contrasena, nombres_completos, correo_electronico, telefono, genero, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlVoluntario = "INSERT INTO VOLUNTARIO (id_persona, habilidades, disponibilidad) VALUES (?, ?, ?)";
        
        Connection con = null;
        try {
            con = ConexionDB.getConnection(); 
            con.setAutoCommit(false); // Transacción iniciada

            // 1. Guardar Padre (Persona)
            PreparedStatement psP = con.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
            psP.setString(1, v.getCedula());
            psP.setString(2, v.getContrasena()); 
            psP.setString(3, v.getNombres_completos());
            psP.setString(4, v.getCorreo());
            psP.setString(5, v.getTelefono());
            psP.setString(6, v.getGenero());
            psP.setString(7, v.getEstado());
            psP.executeUpdate();

            // Capturar el ID generado
            ResultSet rs = psP.getGeneratedKeys();
            int idPersonaGenerado = 0;
            if (rs.next()) {
                idPersonaGenerado = rs.getInt(1);
            }
            psP.close();

            // 2. Guardar Hijo (Voluntario)
            PreparedStatement psV = con.prepareStatement(sqlVoluntario);
            psV.setInt(1, idPersonaGenerado);
            psV.setString(2, v.getHabilidades());
            psV.setString(3, v.getDisponibilidad_dias());
            psV.executeUpdate();
            psV.close();

            con.commit(); // Confirmar cambios en ambas tablas
        } catch (SQLException e) {
            if (con != null) con.rollback(); // Deshacer todo si hay error
            throw e;
        } finally {
            if (con != null) con.close();
        }
    }

    public List<Voluntario> listar() throws SQLException {
        List<Voluntario> lista = new ArrayList<>();
        String sql = "SELECT p.*, v.id_voluntario, v.habilidades, v.disponibilidad " +
                     "FROM PERSONA p " +
                     "INNER JOIN VOLUNTARIO v ON p.id_persona = v.id_persona " +
                     "ORDER BY p.id_persona ASC";
                     
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Voluntario v = mapear(rs);
                if (v != null) lista.add(v);
            }
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return lista;
    }

    public Optional<Voluntario> buscarPorCedula(String cedula) throws SQLException {
        String sql = "SELECT p.*, v.id_voluntario, v.habilidades, v.disponibilidad " +
                     "FROM PERSONA p " +
                     "INNER JOIN VOLUNTARIO v ON p.id_persona = v.id_persona " +
                     "WHERE p.cedula = ?";
                     
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();
            if (rs.next()) return Optional.ofNullable(mapear(rs));
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return Optional.empty();
    }

    public List<Voluntario> buscarPorNombre(String nombre) throws SQLException {
        List<Voluntario> lista = new ArrayList<>();
        String sql = "SELECT p.*, v.id_voluntario, v.habilidades, v.disponibilidad " +
                     "FROM PERSONA p " +
                     "INNER JOIN VOLUNTARIO v ON p.id_persona = v.id_persona " +
                     "WHERE LOWER(p.nombres_completos) LIKE LOWER(?)";
                     
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + nombre.trim() + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Voluntario v = mapear(rs);
                if (v != null) lista.add(v);
            }
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return lista;
    }

    public void actualizar(Voluntario v) throws SQLException {
        String sqlPersona = "UPDATE PERSONA SET nombres_completos=?, correo_electronico=?, telefono=?, genero=?, estado=? WHERE cedula=?";
        String sqlVoluntario = "UPDATE VOLUNTARIO SET habilidades=?, disponibilidad=? WHERE id_persona=(SELECT id_persona FROM PERSONA WHERE cedula=?)";
        
        Connection con = null;
        try {
            con = ConexionDB.getConnection();
            con.setAutoCommit(false); // Transacción iniciada
            
            // 1. Actualizar Padre
            PreparedStatement psP = con.prepareStatement(sqlPersona);
            psP.setString(1, v.getNombres_completos());
            psP.setString(2, v.getCorreo());
            psP.setString(3, v.getTelefono());
            psP.setString(4, v.getGenero());
            psP.setString(5, v.getEstado());
            psP.setString(6, v.getCedula());
            psP.executeUpdate();
            psP.close();

            // 2. Actualizar Hijo
            PreparedStatement psV = con.prepareStatement(sqlVoluntario);
            psV.setString(1, v.getHabilidades());
            psV.setString(2, v.getDisponibilidad_dias());
            psV.setString(3, v.getCedula());
            psV.executeUpdate();
            psV.close();

            con.commit();
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) con.close();
        }
    }

    public boolean eliminarLogico(int idVoluntario) throws SQLException {
        // En MySQL podemos usar un JOIN dentro del UPDATE para apagar a la persona
        // usando el ID del voluntario que le enviemos.
        String sql = "UPDATE PERSONA p INNER JOIN VOLUNTARIO v ON p.id_persona = v.id_persona " +
                     "SET p.estado='Inactivo' WHERE v.id_voluntario=?";
                     
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idVoluntario);
            return ps.executeUpdate() > 0;
        } finally {
            ConexionDB.cerrar(null, ps, con);
        }
    }

    // ==========================================
    // MÉTODO NUEVO: VALIDACIÓN DEL LOGIN
    // ==========================================
    public Persona validarLogin(String correo, String contrasena) throws SQLException {
        String sql = "SELECT p.*, a.id_admin, v.id_voluntario " +
                     "FROM PERSONA p " +
                     "LEFT JOIN ADMINISTRADOR a ON p.id_persona = a.id_persona " +
                     "LEFT JOIN VOLUNTARIO v ON p.id_persona = v.id_persona " +
                     "WHERE p.correo_electronico = ? AND p.contrasena = ? AND p.estado = 'activo'";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            rs = ps.executeQuery();

            if (rs.next()) {
                // Evaluamos a qué tabla hija pertenece
                if (rs.getObject("id_admin") != null) {
                    Administrador admin = new Administrador();
                    admin.setIdPersona(rs.getInt("id_persona"));
                    admin.setNombres_completos(rs.getString("nombres_completos"));
                    return admin;
                } 
                else if (rs.getObject("id_voluntario") != null) {
                    Voluntario vol = new Voluntario();
                    vol.setIdPersona(rs.getInt("id_persona"));
                    vol.setId_voluntario(rs.getInt("id_voluntario"));
                    vol.setNombres_completos(rs.getString("nombres_completos"));
                    return vol;
                }
            }
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return null; // Si no encontró a nadie
    }
}