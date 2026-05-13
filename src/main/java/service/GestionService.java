package service;

import dao.GestionAmbientalDAO;
import modelo.Gestion;
import java.util.List;

/**
 * Clase de servicio que implementa la comunicación intermedia para el módulo de Gestión Ambiental.
 * Actúa como puente entre el Controlador y la capa de acceso a datos (DAO), procesando
 * decisiones operativas (como delegar si es un INSERT o un UPDATE según el ID).
 * @author Dominica Lilibeth Torres Bohorquez
 * @version 1.0
 * @since 2026-05-11
 */
public class GestionService {

    private final GestionAmbientalDAO gestionDAO = new GestionAmbientalDAO();
 
    /**
     * Obtiene todas las gestiones ambientales registradas.
     *
     * @return Lista de gestiones recuperadas desde la capa DAO.
     */
    public List<Gestion> listarGestiones() {
        return gestionDAO.listar();
    }

    /**
     * Guarda o actualiza una gestión ambiental según el identificador.
     * Si el ID es 0 se registra un nuevo elemento; caso contrario se actualiza.
     *
     * @param gestion Objeto Gestión a persistir.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    public boolean guardarGestion(Gestion gestion) {
        if (gestion.getIdGestion() == 0) {
            return gestionDAO.registrar(gestion);
        } else {
            return gestionDAO.actualizar(gestion);
        }
    }

    /**
     * Elimina una gestión ambiental validando previamente el identificador.
     * 
     * @param idGestion Identificador de la gestión a eliminar.
     * @return true si la eliminación fue exitosa.
     * @throws IllegalArgumentException si el ID es inválido.
    */
    public boolean eliminarGestion(int idGestion) {
        if (idGestion <= 0) {
            throw new IllegalArgumentException("ID de gestión inválido para eliminar.");
        }
        return gestionDAO.eliminar(idGestion);
    }
}