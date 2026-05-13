/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.Iniciativa;
import modelo.Voluntario;
import vista.DashboardVoluntarioPnl;

/**
 *
 * @author Usuario
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

    private void cargarDatos() {
        try {
            // Obtenemos todas las iniciativas con el conteo de inscritos
            List<Iniciativa> todas = dao.listarTodas();
            
            // Obtenemos solo los IDs donde participa este voluntario
            List<Integer> misIds = dao.obtenerMisIniciativas(voluntario.getId_voluntario());
            
            // Enviamos todo a la vista para que dibuje las Cards
            vista.cargarCards(todas, misIds);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar dashboard: " + e.getMessage());
        }
    }
}
