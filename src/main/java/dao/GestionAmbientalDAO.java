package dao;

import modelo.Gestion;
import utilidades.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO encargada de gestionar las operaciones CRUD
 * de la entidad Gestión Ambiental en la base de datos.
 * Implementa el acceso y manipulación de datos utilizando JDBC.
 * Proyecto: Sistema de Gestión de Iniciativas de Preservación Ambiental (SGIPA)
 * @author Dominica Lilibeth Torres Bohorquez
 * @version 1.0
 * @since 2026-05-05
 */
public class GestionAmbientalDAO {

    /**
     * Registra una nueva gestión ambiental en la base de datos.
     *
     * @param ga Objeto Gestión que contiene los datos a registrar.
     * @return true si el registro fue exitoso; false en caso contrario.
     */
    public boolean registrar(Gestion ga) {
        String sql = "INSERT INTO GESTION_AMBIENTAL (ruc_entidad_aliada, nombre_entidad, tipo_autorizacion, categoria_impacto, unidad_medida, meta_anual_global, estado_convenio) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, ga.getRucEntidadAliada());
            ps.setString(2, ga.getNombreEntidad());
            ps.setString(3, ga.getTipoAutorizacion());
            ps.setString(4, ga.getCategoriaImpacto());
            ps.setString(5, ga.getUnidadMedida());
            ps.setInt(6, ga.getMetaAnualGlobal());
            ps.setString(7, ga.getEstadoConvenio());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar: " + e.getMessage());
            return false;
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
    }
 
    /**
     * Obtiene todas las gestiones ambientales almacenadas
     * en la base de datos ordenadas alfabéticamente.
     *
     * @return Lista de objetos Gestión recuperados desde la base de datos.
     */
    public List<Gestion> listar() {
        List<Gestion> lista = new ArrayList<>();
        String sql = "SELECT * FROM GESTION_AMBIENTAL ORDER BY nombre_entidad ASC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Gestion ga = new Gestion();
                ga.setIdGestion(rs.getInt("id_gestion"));
                ga.setRucEntidadAliada(rs.getString("ruc_entidad_aliada"));
                ga.setNombreEntidad(rs.getString("nombre_entidad"));
                ga.setTipoAutorizacion(rs.getString("tipo_autorizacion"));
                ga.setCategoriaImpacto(rs.getString("categoria_impacto"));
                ga.setUnidadMedida(rs.getString("unidad_medida"));
                ga.setMetaAnualGlobal(rs.getInt("meta_anual_global"));
                ga.setEstadoConvenio(rs.getString("estado_convenio"));
                lista.add(ga);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return lista;
    }
    
    /**
     * Actualiza la información de una gestión ambiental existente.
     *
     * @param ga Objeto Gestión con los nuevos datos a modificar.
     * @return true si la actualización fue exitosa; false en caso contrario.
     */
    public boolean actualizar(Gestion ga) {
        String sql = "UPDATE GESTION_AMBIENTAL SET ruc_entidad_aliada=?, nombre_entidad=?, tipo_autorizacion=?, categoria_impacto=?, unidad_medida=?, meta_anual_global=?, estado_convenio=? WHERE id_gestion=?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, ga.getRucEntidadAliada());
            ps.setString(2, ga.getNombreEntidad());
            ps.setString(3, ga.getTipoAutorizacion());
            ps.setString(4, ga.getCategoriaImpacto());
            ps.setString(5, ga.getUnidadMedida());
            ps.setInt(6, ga.getMetaAnualGlobal());
            ps.setString(7, ga.getEstadoConvenio());
            ps.setInt(8, ga.getIdGestion());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
            return false;
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
    }
 
    /**
     * Elimina un registro de gestión ambiental según su identificador.
     *
     * @param id Identificador único del registro a eliminar.
     * @return true si la eliminación fue exitosa; false en caso contrario.
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM GESTION_AMBIENTAL WHERE id_gestion=?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar: " + e.getMessage());
            return false;
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
    }
}