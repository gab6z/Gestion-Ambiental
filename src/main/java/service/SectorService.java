package service;

/**
 * Descripción: Clase de servicio que implementa la lógica de negocio para los sectores.
 * Actúa como intermediario entre el Controlador y el DAO, tomando decisiones
 * operativas y procesando los datos antes de que interactúen con la base de datos.
 * Proyecto: Sistema de Gestión Ambiental (EcoVida)
 * * @author Gabriela Solange Gonzalez Roman
 * @version 1.0
 * @since 2026-05-05
 */

import dao.SectorDAO;
import modelo.Sector;
import java.sql.SQLException;
import java.util.List;

public class SectorService {

    private final SectorDAO sectorDAO = new SectorDAO();

    /**
 * Obtiene la lista completa de sectores registrados.
 *
 * @return lista de objetos {@link Sector} disponibles en la base de datos.
 * @throws SQLException si ocurre un error durante la consulta.
 */
    public List<Sector> listarSectores() throws SQLException {
        return sectorDAO.listarTodos();
    }

    /**
 * Guarda la información de un sector.
 *
 * @param sector objeto {@link Sector} con los datos a registrar o actualizar.
 * @throws SQLException si ocurre un error durante la operación en la base de datos.
 */
    public void guardarSector(Sector sector) throws SQLException {

        if (sector.getIdSector() == 0) {
            sectorDAO.insertar(sector);
        } else {
            sectorDAO.actualizar(sector);
        }
    }

    /**
 * Obtiene una lista de sectores aplicando filtros de búsqueda.
 *
 * @param busquedaTexto texto utilizado para buscar coincidencias
 *                      en el nombre de la zona.
 * @param riesgo nivel de riesgo utilizado como filtro.
 * @param estado estado de la zona utilizado como filtro.
 * @return lista de sectores que cumplen los criterios especificados.
 * @throws SQLException si ocurre un error durante la consulta.
 */
    public List<Sector> listarFiltrados(String busquedaTexto, String riesgo, String estado) throws SQLException {
        return sectorDAO.listarFiltrados(busquedaTexto, riesgo, estado);
    }
    
    
 /**
 * Elimina un sector de la base de datos.
 *
 * @param idSector identificador único del sector a eliminar.
 * @throws SQLException si ocurre un error durante la operación.
 * @throws IllegalArgumentException si el identificador es inválido,
 *         el sector tiene iniciativas asociadas o no existe.
 */
    public void eliminarSector(int idSector) throws SQLException {
        if (idSector <= 0) {
            throw new IllegalArgumentException("ID de sector inválido para eliminar.");
        }
        
      
        if (sectorDAO.tieneIniciativas(idSector)) {
            throw new IllegalArgumentException("Restricción de seguridad: No se puede eliminar este sector porque tiene iniciativas ambientales asociadas en curso o registradas.");
        }
        
        boolean eliminado = sectorDAO.eliminar(idSector);
        if (!eliminado) {
            throw new IllegalArgumentException("No se encontró el sector a eliminar en la base de datos.");
        }
    }
}