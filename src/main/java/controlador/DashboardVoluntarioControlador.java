package controlador;

import dao.IniciativaDAO;
import dao.ParticipacionDAO;
import modelo.Iniciativa;
import modelo.Voluntario;
import vista.DashboardVoluntarioPnl;

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DashboardVoluntarioControlador {

    private DashboardVoluntarioPnl panel;
    private Voluntario voluntarioActual;
    private IniciativaDAO iniDao;
    private ParticipacionDAO partDao;

    public DashboardVoluntarioControlador(DashboardVoluntarioPnl panel, Voluntario voluntario) {
        this.panel = panel;
        this.voluntarioActual = voluntario;
        this.iniDao = new IniciativaDAO();
        this.partDao = new ParticipacionDAO();
        
        cargarDashboard();
    }

    public void cargarDashboard() {
        try {
            List<Iniciativa> listaIniciativas = iniDao.listarTodas();
            Map<Integer, String> misEstados = partDao.obtenerEstadosPorVoluntario(voluntarioActual.getId_voluntario());
            
            panel.cargarCards(listaIniciativas, misEstados, this);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panel, "Error al cargar la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void postularAIniciativa(int idIniciativa, String tituloIniciativa) {
        int confirmar = JOptionPane.showConfirmDialog(panel, 
            "¿Confirmas tu postulación para la iniciativa:\n'" + tituloIniciativa + "'?", 
            "Confirmar Inscripción", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirmar == JOptionPane.YES_OPTION) {
            try {
                boolean exito = partDao.registrarParticipacion(voluntarioActual.getId_voluntario(), idIniciativa);
                
                if (exito) {
                    JOptionPane.showMessageDialog(panel, "¡Inscripción registrada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDashboard(); 
                } else {
                    JOptionPane.showMessageDialog(panel, "Ya te encuentras inscrito en esta iniciativa.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(panel, "Error técnico: " + e.getMessage());
            }
        }
    }
    
    
}