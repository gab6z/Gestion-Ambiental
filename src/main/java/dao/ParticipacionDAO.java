/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import modelo.Participacion;
import utilidades.ConexionDB;
/**
 * Clase de Acceso a Datos (DAO) para la entidad Participacion.
 * Gestiona las operaciones CRUD y vinculación entre voluntarios e iniciativas.
 * * @author EDUARDO
 * @version 1.1
 * @since 2026-05-10
 */
public class ParticipacionDAO {
    /**
     * Registra una nueva postulación de un voluntario a una iniciativa.
     * * @param idVoluntario ID del voluntario que se postula.
     * @param idIniciativa ID de la iniciativa seleccionada.
     * @return true si el registro fue exitoso, false si ya existe la inscripción.
     * @throws SQLException Si ocurre un error en la consulta SQL.
     */
    public boolean registrarParticipacion(int idVoluntario, int idIniciativa) throws SQLException {
        String sqlCheck = "SELECT COUNT(*) FROM participacion WHERE id_voluntario = ? AND id_iniciativa = ?";
        String sqlInsert = "INSERT INTO participacion (id_voluntario, id_iniciativa, estado) VALUES (?, ?, 'Pendiente')";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement psCheck = con.prepareStatement(sqlCheck)) {
            
            psCheck.setInt(1, idVoluntario);
            psCheck.setInt(2, idIniciativa);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // Ya registrado
                }
            }

            try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, idVoluntario);
                psInsert.setInt(2, idIniciativa);
                return psInsert.executeUpdate() > 0;
            }
        }
    }
    
    public java.util.Map<Integer, String> obtenerEstadosPorIniciativa(int idIniciativa) throws SQLException {
    java.util.Map<Integer, String> estados = new java.util.HashMap<>();
    String sql = "SELECT id_voluntario, estado FROM PARTICIPACION WHERE id_iniciativa = ?";
    
    try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setInt(1, idIniciativa);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            estados.put(rs.getInt("id_voluntario"), rs.getString("estado"));
        }
    }
    return estados;
}

    public List<Participacion> listarPorVoluntario(int idVoluntario) throws SQLException {
        List<Participacion> lista = new ArrayList<>();
        String sql = "SELECT p.id_participacion, p.estado, i.titulo_planificacion, i.fecha_ejecucion, i.id_iniciativa " +
                     "FROM participacion p " +
                     "INNER JOIN iniciativa i ON p.id_iniciativa = i.id_iniciativa " +
                     "WHERE p.id_voluntario = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idVoluntario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Participacion part = new Participacion();
                    part.setIdParticipacion(rs.getInt("id_participacion"));
                    part.setEstado(rs.getString("estado"));
                    part.setNombreIniciativa(rs.getString("titulo_planificacion"));
                    part.setFechaIniciativa(rs.getString("fecha_ejecucion"));
                    part.setIdIniciativa(rs.getInt("id_iniciativa"));
                    lista.add(part);
                }
            }
        }
        return lista;
    }
    
    /**
     * Consulta la tabla intermedia PARTICIPACION y devuelve un diccionario
     * con los IDs de las iniciativas y el estado actual del voluntario.
     */
    public Map<Integer, String> obtenerEstadosPorVoluntario(int idVoluntario) throws SQLException {
        Map<Integer, String> estados = new java.util.HashMap<>();
        
        String sql = "SELECT id_iniciativa, estado FROM participacion WHERE id_voluntario = ?";
        
        try (java.sql.Connection cn = utilidades.ConexionDB.getConnection();
             java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {
             
            ps.setInt(1, idVoluntario);
            
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    estados.put(rs.getInt("id_iniciativa"), rs.getString("estado"));
                }
            }
        }
        return estados;
    }
    
    public void actualizarEstado(int idIniciativa, int idVoluntario, String nuevoEstado) throws SQLException {
        String sql = "UPDATE PARTICIPACION SET estado = ? WHERE id_iniciativa = ? AND id_voluntario = ?";

        try (Connection cn = ConexionDB.getConnection(); 
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idIniciativa);
            ps.setInt(3, idVoluntario);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                System.out.println("ADVERTENCIA: No se encontró ningún registro para actualizar con Iniciativa: " + idIniciativa + " y Voluntario: " + idVoluntario);
            } else {
                System.out.println("ÉXITO: Se actualizó el estado a " + nuevoEstado);
            }
        }
    }
}