package dao;

import utilidades.ConexionDB; 
import modelo.Persona;
import modelo.Administrador;
import modelo.Voluntario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * Clase de Acceso a Datos (DAO) para la entidad Voluntario.
 * Gestiona la persistencia en las tablas PERSONA y VOLUNTARIO de forma sincronizada.
 * Implementa transacciones para asegurar la integridad referencial.
 * * @author EDUARDO
 * @version 1.1
 * @since 2026-05-07
 */
public class VoluntarioDAO {
    /**
     * Convierte una fila del ResultSet en un objeto Voluntario.
     * @param rs El conjunto de resultados de la consulta SQL.
     * @return Un objeto {@link Voluntario} mapeado.
     * @throws SQLException Si hay errores al leer las columnas.
     */
    private Voluntario mapear(ResultSet rs) throws SQLException {
        try {
            Voluntario v = new Voluntario();
            v.setIdPersona(rs.getInt("id_persona")); 
            v.setCedula(rs.getString("cedula"));
            v.setNombres_completos(rs.getString("nombres_completos"));
            v.setCorreo(rs.getString("correo_electronico"));
            v.setTelefono(rs.getString("telefono"));
            v.setGenero(rs.getString("genero"));
            v.setEstado(rs.getString("estado"));
            
            v.setId_voluntario(rs.getInt("id_voluntario"));
            v.setDisponibilidad_dias(rs.getString("disponibilidad"));
            v.setHabilidades(rs.getString("habilidades"));
            
            return v;
        } catch (IllegalArgumentException e) {
            System.err.println("Error al mapear: " + e.getMessage());
            return null;
        }
    }
    /**
     * Inserta un nuevo voluntario realizando una operación atómica en dos tablas.
     * Utiliza RETURN_GENERATED_KEYS para vincular la Persona con el Voluntario.
     * @param v El voluntario a registrar.
     * @throws SQLException Si falla la inserción; realiza rollback automático.
     */
    public void insertar(Voluntario v) throws SQLException {
        String sqlPersona = "INSERT INTO PERSONA (cedula, contrasena, nombres_completos, correo_electronico, telefono, genero, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlVoluntario = "INSERT INTO VOLUNTARIO (id_persona, habilidades, disponibilidad) VALUES (?, ?, ?)";
        
        Connection con = null;
        try {
            con = ConexionDB.getConnection(); 
            con.setAutoCommit(false); 

            PreparedStatement psP = con.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
            psP.setString(1, v.getCedula());
            psP.setString(2, v.getContrasena()); 
            psP.setString(3, v.getNombres_completos());
            psP.setString(4, v.getCorreo());
            psP.setString(5, v.getTelefono());
            psP.setString(6, v.getGenero());
            psP.setString(7, v.getEstado());
            psP.executeUpdate();

            
            ResultSet rs = psP.getGeneratedKeys();
            int idPersonaGenerado = 0;
            if (rs.next()) {
                idPersonaGenerado = rs.getInt(1);
            }
            psP.close();

            PreparedStatement psV = con.prepareStatement(sqlVoluntario);
            psV.setInt(1, idPersonaGenerado);
            psV.setString(2, v.getHabilidades());
            psV.setString(3, v.getDisponibilidad_dias());
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
    /**
     * Obtiene todos los voluntarios activos vinculando las tablas mediante un INNER JOIN.
     * @return List de {@link Voluntario}.
     * @throws SQLException Si ocurre un error en la consulta.
     */
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
    /**
     * Busca un voluntario por su número de cédula.
     * @param cedula Cédula del voluntario.
     * @return {@link Optional} conteniendo al voluntario si se encuentra.
     * @throws SQLException Si hay un error de conexión o SQL.
     */
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
    /**
     * Actualiza la información de un voluntario en ambas tablas dentro de una transacción.
     * @param v Objeto voluntario con los datos actualizados.
     * @throws SQLException Si la actualización falla.
     */
    public void actualizar(Voluntario v) throws SQLException {
        String sqlPersona = "UPDATE PERSONA SET nombres_completos=?, correo_electronico=?, telefono=?, genero=?, estado=? WHERE cedula=?";
        String sqlVoluntario = "UPDATE VOLUNTARIO SET habilidades=?, disponibilidad=? WHERE id_persona=(SELECT id_persona FROM PERSONA WHERE cedula=?)";
        
        Connection con = null;
        try {
            con = ConexionDB.getConnection();
            con.setAutoCommit(false); // Transacción iniciada
            
            PreparedStatement psP = con.prepareStatement(sqlPersona);
            psP.setString(1, v.getNombres_completos());
            psP.setString(2, v.getCorreo());
            psP.setString(3, v.getTelefono());
            psP.setString(4, v.getGenero());
            psP.setString(5, v.getEstado());
            psP.setString(6, v.getCedula());
            psP.executeUpdate();
            psP.close();

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
    /**
     * Realiza un borrado lógico cambiando el estado del voluntario a 'Inactivo'.
     * @param idVoluntario ID del voluntario a desactivar.
     * @return true si la operación afectó a alguna fila.
     * @throws SQLException Si hay un error SQL.
     */
    public boolean eliminarLogico(int idVoluntario) throws SQLException {
        String sql = "UPDATE PERSONA SET estado='Inactivo' " +
                     "WHERE id_persona = (SELECT id_persona FROM VOLUNTARIO WHERE id_voluntario = ?)";

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
    /**
     * Valida las credenciales de acceso y determina el rol del usuario (Administrador o Voluntario).
     * @param correo Credencial de correo.
     * @param contrasena Credencial de contraseña.
     * @return Un objeto {@link Persona} (que puede ser Administrador o Voluntario).
     * @throws SQLException Si falla la consulta de autenticación.
     */
    public Persona validarLogin(String correo, String contrasena) throws SQLException {
        String sql = "SELECT p.*, a.id_admin, v.id_voluntario, v.habilidades, v.disponibilidad " +
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
                // Caso 1: Es Administrador
                if (rs.getObject("id_admin") != null) {
                    Administrador admin = new Administrador();
                    admin.setIdPersona(rs.getInt("id_persona"));
                    admin.setNombres_completos(rs.getString("nombres_completos"));
                    // (Opcional) Llenar correo/cedula si los necesitas en el perfil del admin
                    return admin;
                } 
                // Caso 2: Es Voluntario
                else if (rs.getObject("id_voluntario") != null) {
                    // USAMOS TU MÉTODO mapear(rs) QUE YA ESTÁ BIEN HECHO
                    // Esto llenará cedula, nombres, correo, telefono, habilidades y disponibilidad
                    return mapear(rs); 
                }
            }
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return null;
    }
}