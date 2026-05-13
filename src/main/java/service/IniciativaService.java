package service;

import dao.IniciativaDAO;
import modelo.Iniciativa;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Solis Geovanny
 */
public class IniciativaService {

    private final IniciativaDAO iniciativaDAO = new IniciativaDAO();

    public List<Iniciativa> listarIniciativas() throws SQLException {
        return iniciativaDAO.listarTodas();
    }

    public void guardarIniciativa(Iniciativa iniciativa) throws SQLException {
        if (iniciativa.getPresupuesto() < 0) {
            throw new IllegalArgumentException("El presupuesto no puede ser negativo.");
        }

        if (iniciativa.getIdIniciativa() == 0) {
            iniciativaDAO.insertar(iniciativa); 
        } else {
            iniciativaDAO.actualizar(iniciativa); 
        }
    }

    public void eliminarIniciativa(int idIniciativa) throws SQLException {
        if (idIniciativa <= 0) {
            throw new IllegalArgumentException("ID de iniciativa no válido.");
        }

        boolean eliminado = iniciativaDAO.eliminar(idIniciativa); 
        if (!eliminado) {
            throw new SQLException("No se pudo eliminar la iniciativa. Es posible que ya no exista.");
        }
    }
}
