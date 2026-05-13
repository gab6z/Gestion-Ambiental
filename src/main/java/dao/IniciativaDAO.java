package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.Iniciativa;
import utilidades.ConexionDB;

/**
 *
 * @author Solis Geovanny
 */
public class IniciativaDAO {

    public int insertar(Iniciativa ini) throws SQLException {
        String sql = "INSERT INTO INICIATIVA (id_sector, id_tarea, id_gestion, titulo_planificacion, "
                + "descripcion_logistica, fecha_ejecucion, hora_inicio, hora_fin, "
                + "meta_cuantitativa_iniciativa, presupuesto, estado_planificacion, fecha_fin) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection cn = null;
        PreparedStatement ps = null;   
        ResultSet rs = null;
        int idGenerado = 0;

        try {
            cn = ConexionDB.getConnection();
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); 

            ps.setInt(1, ini.getIdSector());
            ps.setInt(2, ini.getIdTarea());
            ps.setInt(3, ini.getIdGestion());
            ps.setString(4, ini.getTitulo());
            ps.setString(5, ini.getDescripcion());
            ps.setDate(6, ini.getFechaEjecucion());
            ps.setTime(7, ini.getHoraInicio());
            ps.setTime(8, ini.getHoraFin());
            ps.setInt(9, ini.getMeta());
            ps.setDouble(10, ini.getPresupuesto());
            ps.setString(11, ini.getEstado());
            ps.setDate(12, ini.getFechaFin());

            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
        } finally {
            ConexionDB.cerrar(rs, ps, cn);
        }
        return idGenerado;
    }

    public List<Iniciativa> listarTodas() throws SQLException {
        List<Iniciativa> lista = new ArrayList<>();
        String sql = "SELECT i.*, s.nombre_zona, t.nombre_tarea, g.nombre_entidad, "
                + "(SELECT COUNT(*) FROM PARTICIPACION p WHERE p.id_iniciativa = i.id_iniciativa) as inscritos "
                + "FROM INICIATIVA i "
                + "LEFT JOIN SECTOR s ON i.id_sector = s.id_sector "
                + "LEFT JOIN TAREA t ON i.id_tarea = t.id_tarea "
                + "LEFT JOIN GESTION_AMBIENTAL g ON i.id_gestion = g.id_gestion "
                + "ORDER BY i.fecha_creacion DESC";

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            cn = ConexionDB.getConnection();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Iniciativa ini = new Iniciativa();
                ini.setTotalParticipantes(rs.getInt("inscritos"));
                ini.setNombreSector(rs.getString("nombre_zona"));
                ini.setIdIniciativa(rs.getInt("id_iniciativa"));
                ini.setIdSector(rs.getInt("id_sector"));
                ini.setIdTarea(rs.getInt("id_tarea"));
                ini.setIdGestion(rs.getInt("id_gestion"));
                ini.setTitulo(rs.getString("titulo_planificacion"));
                ini.setDescripcion(rs.getString("descripcion_logistica"));
                ini.setFechaEjecucion(rs.getDate("fecha_ejecucion"));
                ini.setHoraInicio(rs.getTime("hora_inicio"));
                ini.setHoraFin(rs.getTime("hora_fin"));
                ini.setMeta(rs.getInt("meta_cuantitativa_iniciativa"));
                ini.setPresupuesto(rs.getDouble("presupuesto"));
                ini.setEstado(rs.getString("estado_planificacion"));
                ini.setFechaFin(rs.getDate("fecha_fin"));
                ini.setNombreSector(rs.getString("nombre_zona"));   
                ini.setNombreTarea(rs.getString("nombre_tarea"));   
                ini.setNombreGestion(rs.getString("nombre_entidad")); 
                lista.add(ini);
            }
        } finally {
            ConexionDB.cerrar(rs, ps, cn);
        }
        return lista;
    }

    public void actualizar(Iniciativa ini) throws SQLException {
        String sql = "UPDATE INICIATIVA SET id_sector=?, id_tarea=?, id_gestion=?, titulo_planificacion=?, "
                + "descripcion_logistica=?, fecha_ejecucion=?, hora_inicio=?, hora_fin=?, "
                + "meta_cuantitativa_iniciativa=?, presupuesto=?, estado_planificacion=?, fecha_fin=? "
                + "WHERE id_iniciativa=?";

        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = ConexionDB.getConnection();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, ini.getIdSector());
            ps.setInt(2, ini.getIdTarea());
            ps.setInt(3, ini.getIdGestion());
            ps.setString(4, ini.getTitulo());
            ps.setString(5, ini.getDescripcion());
            ps.setDate(6, ini.getFechaEjecucion());
            ps.setTime(7, ini.getHoraInicio());
            ps.setTime(8, ini.getHoraFin());
            ps.setInt(9, ini.getMeta());
            ps.setDouble(10, ini.getPresupuesto());
            ps.setString(11, ini.getEstado());
            ps.setDate(12, ini.getFechaFin());
            ps.setInt(13, ini.getIdIniciativa());
            ps.executeUpdate();
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(cn);
        }
    }

    public boolean eliminar(int idIniciativa) throws SQLException {
        String sql = "DELETE FROM INICIATIVA WHERE id_iniciativa = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = ConexionDB.getConnection();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idIniciativa);
            return ps.executeUpdate() > 0;
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(cn);
        }
    }
    
    // --- MÉTODOS CORREGIDOS (Cambio de id_usuario a id_voluntario) ---

    public void asignarVoluntarios(int idIniciativa, List<Integer> idsVoluntarios) throws SQLException {
        // CORREGIDO: Ahora usa id_voluntario
        String sql = "INSERT INTO PARTICIPACION (id_voluntario, id_iniciativa, estado) VALUES (?, ?, 'pendiente')";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = ConexionDB.getConnection();
            cn.setAutoCommit(false); // Para insertar varios de golpe 
            ps = cn.prepareStatement(sql);
            for (Integer idVol : idsVoluntarios) {
                ps.setInt(1, idVol);
                ps.setInt(2, idIniciativa);
                ps.addBatch();
            }
            ps.executeBatch();
            cn.commit();
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(cn);
        }
    }
    
    public List<Integer> obtenerIdsVoluntarios(int idIniciativa) throws SQLException {
        // CORREGIDO: Ahora selecciona id_voluntario
        String sql = "SELECT id_voluntario FROM PARTICIPACION WHERE id_iniciativa = ?";
        List<Integer> ids = new ArrayList<>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionDB.getConnection();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idIniciativa);
            rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("id_voluntario")); // Extrae la columna correcta
            }
        } finally {
            ConexionDB.cerrar(rs, ps, cn);
        }
        return ids;
    }
    
    public void eliminarParticipaciones(int idIniciativa) throws SQLException {
        String sql = "DELETE FROM PARTICIPACION WHERE id_iniciativa = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = ConexionDB.getConnection();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idIniciativa);
            ps.executeUpdate();
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(cn);
        }
    }

    public List<Integer> obtenerMisIniciativas(int idVoluntario) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_iniciativa FROM PARTICIPACION WHERE id_voluntario = ?";

        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idVoluntario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("id_iniciativa"));
            }
        }
        return ids;
    }
}