package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.Iniciativa;
import utilidades.ConexionDB;

/**
 * Componente de Acceso a Datos (DAO) para la entidad {@link Iniciativa} en el
 * sistema EcoVida. Proporciona los métodos CRUD (Crear, Leer, Actualizar,
 * Eliminar) y operaciones transaccionales específicas para interactuar con las
 * tablas {@code INICIATIVA} y {@code PARTICIPACION} en MySQL.
 * <p>
 * Implementa optimizaciones como consultas por lotes (Batch) y subconsultas en
 * tiempo real para el cálculo dinámico de métricas de vinculación.
 * </p>
 *
 * * @author Solis Caballero Geovanny Andrés
 * @version 1.3
 */
public class IniciativaDAO {  
    /**
     * Inserta un nuevo registro de iniciativa ambiental en la base de datos.
     * Recupera la clave primaria auto-incrementable generada por el motor
     * relacional.
     *
     * * @param ini El objeto modelo {@link Iniciativa} que contiene la
     * información a persistir.
     * @return El identificador numérico (ID) asignado automáticamente por la
     * base de datos.
     * @throws SQLException Si ocurre una anomalía o fallo en la comunicación
     * con el servidor SQL.
     */
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
    
    /**
     * Recupera el listado completo de iniciativas registradas junto con sus
     * relaciones. Realiza acoplamientos (LEFT JOIN) para obtener los nombres
     * descriptivos de sectores, tareas y entidades, e incluye una subconsulta
     * que computa en tiempo real el total de inscritos.
     *
     * * @return Una colección {@link List} de objetos {@link Iniciativa}
     * ordenados por fecha de creación descensional.
     * @throws SQLException Si el motor de base de datos no puede procesar la
     * consulta estructurada.
     */
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
    
    /**
     * Modifica los datos existentes de una iniciativa específica basándose en
     * su ID único. Actualiza tanto la información de planificación como los
     * estados del módulo.
     *
     * * @param ini El objeto {@link Iniciativa} con los datos actualizados a
     * persistir.
     * @throws SQLException Si los tipos de datos no coinciden o se rompe una
     * restricción de integridad.
     */
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
    
    /**
     * Remueve físicamente una iniciativa de la base de datos a través de su
     * identificador.
     *
     * * @param idIniciativa Identificador único de la iniciativa a suprimir.
     * @return {@code true} si la operación eliminó el registro exitosamente;
     * {@code false} en caso contrario.
     * @throws SQLException Si el registro está protegido por restricciones de
     * clave foránea activas.
     */
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
    
    /**
     * Registra de forma masiva la asignación de múltiples voluntarios a una
     * iniciativa. Desactiva temporalmente el auto-commit para agrupar las
     * inserciones en un único bloque transaccional, garantizando la
     * consistencia de los datos bajo operaciones relacionales seguras.
     *
     * * @param idIniciativa Identificador único de la iniciativa a la cual
     * vincular el personal.
     * @param idsVoluntarios Lista de identificadores (IDs) de los voluntarios
     * seleccionados.
     * @throws SQLException Si falla la inserción masiva o se requiere un
     * rollback automático.
     */
    public void asignarVoluntarios(int idIniciativa, List<Integer> idsVoluntarios) throws SQLException {
        String sql = "INSERT INTO PARTICIPACION (id_voluntario, id_iniciativa, estado) VALUES (?, ?, 'pendiente')";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = ConexionDB.getConnection();
            cn.setAutoCommit(false);
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
    
    /**
     * Consulta y extrae todos los identificadores de los voluntarios
     * actualmente asignados a una iniciativa.
     *
     * * @param idIniciativa Identificador numérico de la iniciativa a auditar.
     * @return Una lista {@link List} con los IDs correspondientes de los
     * voluntarios participantes.
     * @throws SQLException Si falla la ejecución de la lectura relacional.
     */
    public List<Integer> obtenerIdsVoluntarios(int idIniciativa) throws SQLException {
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
                ids.add(rs.getInt("id_voluntario"));
            }
        } finally {
            ConexionDB.cerrar(rs, ps, cn);
        }
        return ids;
    }
    
    /**
     * Elimina todos los registros de participación vinculados a una iniciativa
     * específica. Generalmente utilizado como paso previo a una actualización
     * masiva de asignaciones.
     *
     * * @param idIniciativa Identificador único de la iniciativa cuyas
     * relaciones serán removidas.
     * @throws SQLException Si ocurre un error de truncado o bloqueo en la tabla
     * intermedia.
     */
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
    
    /**
     * Recupera exclusivamente los IDs de las iniciativas a las que se ha
     * inscrito un voluntario específico. Optimiza las consultas mediante el uso
     * de la cláusula estructurada {@code try-with-resources}.
     *
     * @param idVoluntario Identificador único del voluntario logueado.
     * @return Una colección {@link List} que contiene los identificadores de
     * sus iniciativas asignadas.
     * @throws SQLException Si la conexión falla o el query no puede resolverse
     * correctamente.
     */
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
    
    /**
     * Busca una iniciativa específica por su ID único para auditar su estado
     * actual.
     *
     * @param idIniciativa Identificador único de la iniciativa.
     * @return El objeto {@link Iniciativa} encontrado, o null si no existe.
     * @throws SQLException Si ocurre un error en la consulta SQL.
     */
    public Iniciativa buscarPorId(int idIniciativa) throws SQLException {
        String sql = "SELECT * FROM INICIATIVA WHERE id_iniciativa = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Iniciativa ini = null;

        try {
            cn = ConexionDB.getConnection();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idIniciativa);
            rs = ps.executeQuery();

            if (rs.next()) {
                ini = new Iniciativa();
                ini.setIdIniciativa(rs.getInt("id_iniciativa"));
                ini.setIdSector(rs.getInt("id_sector"));
                ini.setEstado(rs.getString("estado_planificacion"));
            }
        } finally {
            ConexionDB.cerrar(rs, ps, cn);
        }
        return ini;
    }
}