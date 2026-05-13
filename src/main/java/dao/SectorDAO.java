/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 * Descripción: Clase de Acceso a Datos (Data Access Object) para la entidad Sector.
 * Contiene toda la lógica de persistencia y se encarga exclusivamente de ejecutar
 * las sentencias SQL (INSERT, UPDATE, DELETE, SELECT) en la base de datos MySQL.
 * Proyecto: Sistema de Gestión Ambiental 
 * 
 * @author Gabriela Solange Gonzalez Roman
 * @version 1.0
 * @since 2026-05-05
 */
import modelo.Sector;
import utilidades.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SectorDAO {

 /**
 * Convierte un registro obtenido desde la base de datos en un objeto {@link Sector}.
 *
 * @param rs objeto {@link ResultSet} que contiene los datos recuperados de la tabla SECTOR.
 * @return un objeto {@link Sector} con los datos de la fila actual del ResultSet.
 * @throws SQLException si ocurre un error al acceder a los datos del ResultSet.
 */
    private Sector mapear(ResultSet rs) throws SQLException {
        Sector s = new Sector();
        s.setIdSector(rs.getInt("id_sector"));
        s.setNombreZona(rs.getString("nombre_zona"));
        s.setLatitud(rs.getString("latitud"));
        s.setLongitud(rs.getString("longitud"));
        s.setProvinciaCiudad(rs.getString("provincia_ciudad"));
        s.setNivelRiesgo(rs.getString("nivel_riesgo"));
        s.setDescripcionTerreno(rs.getString("descripcion_terreno"));
        s.setEstadoZona(rs.getString("estado_zona"));
        return s;
    }

 /**
 * Inserta un nuevo sector en la base de datos.
 *
 * @param s objeto {@link Sector} con la información del sector a registrar.
 * @throws SQLException si ocurre un error durante la inserción en la base de datos.
 */
    public void insertar(Sector s) throws SQLException {
        String sql = "INSERT INTO SECTOR (nombre_zona, latitud, longitud, provincia_ciudad, nivel_riesgo, descripcion_terreno, estado_zona) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getNombreZona());
            ps.setString(2, s.getLatitud());
            ps.setString(3, s.getLongitud());
            ps.setString(4, s.getProvinciaCiudad());
            ps.setString(5, s.getNivelRiesgo());
            ps.setString(6, s.getDescripcionTerreno());
            ps.setString(7, s.getEstadoZona());
            ps.executeUpdate();

            // Recuperar el ID 
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.setIdSector(keys.getInt(1));
            }
        }
    }

 /**
 * Actualiza la información de un sector existente en la base de datos.
 *
 * @param s objeto {@link Sector} con los datos actualizados.
 * @throws SQLException si ocurre un error durante la actualización.
 */
    public void actualizar(Sector s) throws SQLException {
        String sql = "UPDATE SECTOR SET nombre_zona=?, latitud=?, longitud=?, provincia_ciudad=?, " +
                     "nivel_riesgo=?, descripcion_terreno=?, estado_zona=? WHERE id_sector=?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, s.getNombreZona());
            ps.setString(2, s.getLatitud());
            ps.setString(3, s.getLongitud());
            ps.setString(4, s.getProvinciaCiudad());
            ps.setString(5, s.getNivelRiesgo());
            ps.setString(6, s.getDescripcionTerreno());
            ps.setString(7, s.getEstadoZona());
            ps.setInt(8, s.getIdSector());
            ps.executeUpdate();
        }
    }

 /**
 * Elimina un sector de la base de datos según su identificador.
 *
 * @param idSector identificador único del sector a eliminar.
 * @return {@code true} si el sector fue eliminado correctamente;
 *         {@code false} en caso contrario.
 * @throws SQLException si ocurre un error durante la eliminación.
 */
    public boolean eliminar(int idSector) throws SQLException {
        String sql = "DELETE FROM SECTOR WHERE id_sector = ?";
        
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
             
            ps.setInt(1, idSector);
            return ps.executeUpdate() > 0;
        }
    }

/**
 * Obtiene la lista completa de sectores registrados en la base de datos.
 *
 * @return lista de objetos {@link Sector} ordenados por ID descendente.
 * @throws SQLException si ocurre un error durante la consulta.
 */
    public List<Sector> listarTodos() throws SQLException {
        String sql = "SELECT * FROM SECTOR ORDER BY id_sector DESC";
        List<Sector> lista = new ArrayList<>();

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }
    
 /**
 * Obtiene una lista de sectores aplicando filtros de búsqueda.
 *
 * @param busquedaTexto texto utilizado para buscar coincidencias
 *                      en el nombre de la zona.
 * @param riesgo nivel de riesgo del sector a filtrar.
 * @param estado estado de la zona a filtrar.
 * @return lista de sectores que cumplen los criterios de búsqueda.
 * @throws SQLException si ocurre un error durante la consulta.
 */
    public List<Sector> listarFiltrados(String busquedaTexto, String riesgo, String estado) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM SECTOR WHERE 1=1 ");
        List<Object> parametros = new ArrayList<>();

        if (busquedaTexto != null && !busquedaTexto.isBlank()) {
            sql.append("AND nombre_zona LIKE ? ");
            parametros.add("%" + busquedaTexto + "%");
        }
        if (riesgo != null && !riesgo.equals("Todos")) {
            sql.append("AND nivel_riesgo = ? ");
            parametros.add(riesgo);
        }
        if (estado != null && !estado.equals("Todos")) {
            sql.append("AND estado_zona = ? ");
            parametros.add(estado);
        }

        sql.append("ORDER BY id_sector DESC");
        List<Sector> lista = new ArrayList<>();

        try (Connection cn = utilidades.ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
             
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs)); 
            }
        }
        return lista;
    }

/**
 * Busca un sector en la base de datos utilizando su identificador.
 *
 * @param idSector identificador único del sector.
 * @return un {@link Optional} que contiene el sector encontrado,
 *         o vacío si no existe.
 * @throws SQLException si ocurre un error durante la consulta.
 */
    public Optional<Sector> buscarPorId(int idSector) throws SQLException {
        String sql = "SELECT * FROM SECTOR WHERE id_sector = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idSector);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

/**
 * Verifica si un sector tiene iniciativas asociadas.
 *
 * @param idSector identificador único del sector.
 * @return {@code true} si existen iniciativas relacionadas con el sector;
 *         {@code false} en caso contrario.
 * @throws SQLException si ocurre un error durante la consulta.
 */
    public boolean tieneIniciativas(int idSector) throws SQLException {
        String sql = "SELECT COUNT(*) FROM INICIATIVA WHERE id_sector = ?";
        
        try (Connection cn = utilidades.ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
             
            ps.setInt(1, idSector);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int cantidad = rs.getInt(1);
                    return cantidad > 0; 
                }
            }
        }
        return false;
    }
}
