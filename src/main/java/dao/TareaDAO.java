package dao;

import utilidades.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.Tarea;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Tarea.
 * Provee los métodos necesarios para realizar operaciones CRUD (Crear, Leer, 
 * Actualizar, Eliminar) y filtrados avanzados sobre la tabla TAREA.
 * Proyecto: Sistema de Gestión Ambiental (EcoVida)
 * @author Leandro Palacios
 * @version 1.0
 * @since 2026-05-06
 */
public class TareaDAO {

    /**
     * Mapea el resultado actual de un ResultSet a un objeto de tipo Tarea.
     * @param rs ResultSet de la consulta SQL.
     * @return Objeto Tarea con los datos extraídos.
     * @throws SQLException Si ocurre un error al acceder a las columnas.
     */
    private Tarea mapear(ResultSet rs) throws SQLException {
        Tarea t = new Tarea();
        t.setIdTarea(rs.getInt("id_tarea"));
        t.setNombreTarea(rs.getString("nombre_tarea"));
        t.setDescripcionInstrucciones(rs.getString("descripcion_instrucciones"));
        t.setHerramientasRequeridas(rs.getString("herramientas_requeridas"));
        t.setDificultadTecnica(rs.getString("dificultad_tecnica"));
        t.setCupoRecomendado(rs.getInt("cupo_recomendado"));
        t.setEstadoTarea(rs.getString("estado_tarea"));
        return t;
    }

    /**
     * Recupera la lista completa de tareas registradas.
     * @return List de objetos Tarea.
     * @throws SQLException Si hay un fallo en la conexión o consulta.
     */
    public List<Tarea> listar() throws SQLException {
        String sql = "SELECT * FROM TAREA ORDER BY id_tarea ASC";
        List<Tarea> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /**
     * Inserta una nueva tarea en la base de datos.
     * @param t Objeto Tarea con la información a registrar.
     * @return true si la inserción fue exitosa, false de lo contrario.
     * @throws SQLException Si hay un fallo en la ejecución de la sentencia.
     */
    public boolean agregar(Tarea t) throws SQLException {
        String sql = "INSERT INTO TAREA (nombre_tarea, descripcion_instrucciones, herramientas_requeridas, dificultad_tecnica, cupo_recomendado, estado_tarea) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getNombreTarea());
            ps.setString(2, t.getDescripcionInstrucciones());
            ps.setString(3, t.getHerramientasRequeridas());
            ps.setString(4, t.getDificultadTecnica());
            ps.setInt(5, t.getCupoRecomendado());
            ps.setString(6, "disponible");
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Busca una tarea específica por su identificador único.
     * @param id ID de la tarea buscada.
     * @return Objeto Tarea encontrado o null si no existe.
     * @throws SQLException Si hay un fallo en la consulta.
     */
    public Tarea listarId(int id) throws SQLException {
        String sql = "SELECT * FROM TAREA WHERE id_tarea = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    /**
     * Actualiza la información de una tarea existente.
     * @param t Objeto Tarea con los datos modificados.
     * @return true si la actualización fue exitosa.
     * @throws SQLException Si hay un fallo en la ejecución.
     */
    public boolean actualizar(Tarea t) throws SQLException {
        String sql = "UPDATE TAREA SET nombre_tarea=?, descripcion_instrucciones=?, herramientas_requeridas=?, dificultad_tecnica=?, cupo_recomendado=?, estado_tarea=? WHERE id_tarea=?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getNombreTarea());
            ps.setString(2, t.getDescripcionInstrucciones());
            ps.setString(3, t.getHerramientasRequeridas());
            ps.setString(4, t.getDificultadTecnica());
            ps.setInt(5, t.getCupoRecomendado());
            ps.setString(6, t.getEstadoTarea());
            ps.setInt(7, t.getIdTarea());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Cambia el estado de una tarea a 'Inactiva'.
     * @param id ID de la tarea a dar de baja.
     * @throws SQLException Si hay un fallo en la ejecución.
     */
    public void darDeBaja(int id) throws SQLException {
        String sql = "UPDATE TAREA SET estado_tarea='Inactiva' WHERE id_tarea = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Realiza una búsqueda filtrada por nombre y nivel de dificultad.
     * @param texto Cadena de búsqueda para el nombre de la tarea.
     * @param dificultad Filtro por dificultad ("Todos" omite el filtro).
     * @return List de tareas que coinciden con los criterios.
     * @throws SQLException Si hay un fallo en la consulta dinámica.
     */
    public List<Tarea> filtrar(String texto, String dificultad) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM TAREA WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (texto != null && !texto.isBlank()) {
            sql.append("AND nombre_tarea LIKE ? ");
            params.add("%" + texto + "%");
        }
        if (!dificultad.equals("Todos")) {
            sql.append("AND dificultad_tecnica = ? ");
            params.add(dificultad);
        }

        List<Tarea> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }
}