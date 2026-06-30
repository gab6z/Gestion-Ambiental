package vista;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.border.EmptyBorder;
import modelo.Iniciativa;
import controlador.DashboardVoluntarioControlador;

public class DashboardVoluntarioPnl extends JPanel {
    
    private JPanel contenedorCards;
    private final Color VERDE_ECO = new Color(23, 93, 62);
    private final Color FONDO_APP = new Color(245, 247, 250);

    public DashboardVoluntarioPnl() {
        setLayout(new BorderLayout());
        setBackground(FONDO_APP);

        JPanel pnlCabecera = new JPanel();
        pnlCabecera.setLayout(new BoxLayout(pnlCabecera, BoxLayout.Y_AXIS));
        pnlCabecera.setBackground(FONDO_APP);
        pnlCabecera.setBorder(new EmptyBorder(30, 40, 15, 40)); 

        JLabel lblTituloSeccion = new JLabel("Iniciativas de Gestión Ambiental");
        lblTituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTituloSeccion.setForeground(VERDE_ECO);
        
        JLabel lblSubtitulo = new JLabel("Explora los proyectos disponibles y revisa el estado de tus postulaciones.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(Color.GRAY);

        pnlCabecera.add(lblTituloSeccion);
        pnlCabecera.add(Box.createVerticalStrut(5));
        pnlCabecera.add(lblSubtitulo);
        
        add(pnlCabecera, BorderLayout.NORTH);
        
        contenedorCards = new JPanel();
        contenedorCards.setLayout(new BoxLayout(contenedorCards, BoxLayout.Y_AXIS));
        contenedorCards.setBackground(FONDO_APP);

        JScrollPane scroll = new JScrollPane(contenedorCards);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16); 
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel pnlScrollWrapper = new JPanel(new BorderLayout());
        pnlScrollWrapper.setBackground(FONDO_APP);
        pnlScrollWrapper.setBorder(new EmptyBorder(0, 40, 0, 40));
        pnlScrollWrapper.add(scroll, BorderLayout.CENTER);

        add(pnlScrollWrapper, BorderLayout.CENTER);
    }
    
    public void cargarCards(List<Iniciativa> listaTotal, Map<Integer, String> estadoParticipaciones, DashboardVoluntarioControlador controlador) {
        contenedorCards.removeAll();
        
        for (Iniciativa ini : listaTotal) {
            String estado = estadoParticipaciones.getOrDefault(ini.getIdIniciativa(), "NO_INSCRITO");
            contenedorCards.add(new IniciativaCard(ini, estado, controlador));
            contenedorCards.add(Box.createVerticalStrut(15)); 
        }
        
        contenedorCards.revalidate();
        contenedorCards.repaint();
    }
}