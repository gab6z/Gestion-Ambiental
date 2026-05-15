package controlador;

import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.Iniciativa;
import modelo.Voluntario;
import vista.DashboardVoluntarioPnl;

/**
 * Controlador para el Dashboard del Voluntario en el sistema EcoVida. Se
 * encarga de coordinar el flujo de datos entre el modelo de persistencia (DAO)
 * y la interfaz gráfica que renderiza las tarjetas de iniciativas ambientales.
 * * Interconecta la lógica de negocio con la vista según la arquitectura MVC.
 *
 * * @author Solis Caballero Geovanny Andrés
 * @version 1.0
 */

public class DashboardVoluntarioControlador {
    private DashboardVoluntarioPnl vista;
    private Voluntario voluntario;
    private dao.IniciativaDAO dao = new dao.IniciativaDAO();

    public DashboardVoluntarioControlador(DashboardVoluntarioPnl vista, Voluntario voluntario) {
        this.vista = vista;
        this.voluntario = voluntario;
        cargarDatos();
    }
    
    /**
     * Recupera la información de la base de datos y actualiza la interfaz
     * gráfica. Consulta todas las iniciativas disponibles y contrasta cuáles
     * pertenecen al voluntario logueado para enviarle la información
     * consolidada al método de renderizado de la vista. * Muestra una alerta
     * gráfica si ocurre un fallo en la capa de persistencia SQL.
     */
    private void cargarDatos() {
        try {
            List<Iniciativa> todas = dao.listarTodas();
            List<Integer> misIds = dao.obtenerMisIniciativas(voluntario.getId_voluntario());
            vista.cargarCards(todas, misIds);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar dashboard: " + e.getMessage());
        }
    }
}
