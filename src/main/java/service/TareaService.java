package service;

import dao.TareaDAO;
import java.util.List;
import modelo.Tarea;
import java.sql.SQLException;

/**
 * Clase de servicio que implementa la lógica de negocio para la gestión de tareas.
 * Actúa como capa intermedia entre el Controlador y el DAO, validando reglas de 
 * integridad operativa y coordinando las transacciones de datos.
 * * Proyecto: Sistema de Gestión Ambiental (EcoVida)
 * @author Leandro Palacios
 * @version 1.0
 * @since 2026-05-06
 */
public class TareaService {
    
    private final TareaDAO dao = new TareaDAO();

    public List<Tarea> listar() throws SQLException { 
        return dao.listar(); 
    }
    
    public boolean existeNombreTarea(String nombre) throws Exception {
    return dao.existeNombreTarea(nombre); 
    }

    public boolean agregar(Tarea t) throws SQLException {
        return dao.agregar(t);
    }

    public void guardar(Tarea t) throws SQLException {
        if (t.getIdTarea() == 0) {
            dao.agregar(t);
        } else {
            dao.actualizar(t);
        }
    }
    
    public boolean actualizar(Tarea t) throws SQLException { 
        return dao.actualizar(t); 
    }

    public Tarea listarId(int id) throws SQLException { 
        return dao.listarId(id); 
    }

    public void darDeBaja(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador del sector es inválido para esta operación.");
        }
        dao.darDeBaja(id);
    }
    
    public List<Tarea> filtrar(String texto, String dificultad) throws SQLException {
        return dao.filtrar(texto, dificultad);
    }
}
