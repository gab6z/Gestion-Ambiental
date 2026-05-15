package service;

import dao.IniciativaDAO;
import modelo.Iniciativa;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de la capa de Servicio (Business Logic Layer) para la gestión de
 * Iniciativas en EcoVida. Actúa como un mediador desacoplado entre el
 * controlador de la interfaz gráfica y la capa de acceso a datos
 * ({@link IniciativaDAO}), asegurando que todas las operaciones cumplan con las
 * reglas de negocio institucionales antes de persistir cambios.
 *
 * * @author Solis Caballero Geovanny Andrés
 * @version 1.2
 */
public class IniciativaService {

    /**
     * Instancia única del componente de acceso a datos para la persistencia de
     * iniciativas.
     */
    private final IniciativaDAO iniciativaDAO = new IniciativaDAO();
    
    /**
     * Recupera el listado completo y consolidado de las iniciativas ambientales
     * registradas. Intermedia directamente con la capa DAO para extraer los
     * registros con sus respectivos JOINs.
     *
     * * @return Una colección {@link List} con todas las instancias de
     * {@link Iniciativa} encontradas.
     * @throws SQLException Si ocurre un error de conectividad o de sintaxis
     * estructurada en la base de datos.
     */
    public List<Iniciativa> listarIniciativas() throws SQLException {
        return iniciativaDAO.listarTodas();
    }
    
    /**
     * Procesa las solicitudes de almacenamiento de una iniciativa, sirviendo de
     * compuerta lógica. Evalúa las restricciones del modelo de negocio (como la
     * consistencia presupuestaria) y decide transaccionalmente si se debe
     * ejecutar una inserción de registro nuevo o una actualización basándose en
     * el ID de la entidad.
     *
     * * @param iniciativa El objeto de tipo {@link Iniciativa} cargado con los
     * datos del formulario.
     * @throws IllegalArgumentException Si el presupuesto asignado es un valor
     * negativo menor a 0.
     * @throws SQLException Si las restricciones de integridad de la base de
     * datos rechazan la transacción.
     */
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
    
    /**
     * Ejecuta y valida la remoción física o lógica de una planificación
     * ambiental del sistema. Primero audita la viabilidad del parámetro
     * provisto y posteriormente valida si el motor relacional afectó filas
     * reales, garantizando que el usuario reciba una respuesta fidedigna del
     * estado del registro.
     *
     * * @param idIniciativa El identificador numérico único de la iniciativa
     * que se desea remover.
     * @throws IllegalArgumentException Si el ID enviado es igual o menor a
     * cero, representando un valor inválido.
     * @throws SQLException Si la base de datos rechaza la sentencia o si el
     * registro objetivo no fue encontrado (cero filas afectadas).
     */
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
