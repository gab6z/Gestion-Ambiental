package vista;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.border.EmptyBorder;
import modelo.Iniciativa;

/**
 *
 * @author Usuario
 */

public class DashboardVoluntarioPnl extends JPanel {

    private JPanel contenedorCards;

    public DashboardVoluntarioPnl() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        // Título de la sección
        JLabel lblTituloSeccion = new JLabel("Iniciativas de Gestión Ambiental");
        lblTituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTituloSeccion.setBorder(new EmptyBorder(20, 25, 10, 25));
        lblTituloSeccion.setForeground(new Color(23, 93, 62));
        add(lblTituloSeccion, BorderLayout.NORTH);

        // Contenedor para las tarjetas con FlowLayout
        contenedorCards = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        contenedorCards.setBackground(new Color(245, 247, 250));

        JScrollPane scroll = new JScrollPane(contenedorCards);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    public void cargarCards(List<Iniciativa> listaTotal, List<Integer> misIds) {
        contenedorCards.removeAll();
        for (Iniciativa ini : listaTotal) {
            // Verificamos si el ID de esta iniciativa está en la lista de asignadas
            boolean esMia = misIds.contains(ini.getIdIniciativa());
            contenedorCards.add(new IniciativaCard(ini, esMia));
        }
        contenedorCards.revalidate();
        contenedorCards.repaint();
    }
}
