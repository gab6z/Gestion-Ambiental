package vista;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.border.EmptyBorder;
import modelo.Iniciativa;

/**
 * Panel de Interfaz Gráfica (Vista) que representa el Dashboard principal para
 * el Voluntario. Hereda de {@link JPanel} y se encarga de estructurar una
 * cuadrícula dinámica y deslizable donde se renderizan las tarjetas
 * contenedoras de información (Cards) sobre iniciativas ambientales.
 * <p>
 * Sigue los principios de la arquitectura MVC, abstrayéndose por completo de la
 * lógica de datos y limitándose al refresco, pintado y revalidación de
 * componentes gráficos en el hilo de Swing.
 * </p>
 *
 * * @author Solis Caballero Geovanny Andrés
 * @version 1.2
 */
public class DashboardVoluntarioPnl extends JPanel {
    /**
     * Contenedor secundario encargado de agrupar y organizar horizontalmente
     * los componentes de tipo Card.
     */
    private JPanel contenedorCards;

    /**
     * Constructor por defecto del panel. Configura la disposición espacial de
     * la sección, define la paleta cromática institucional basada en tonos
     * verdes ecológicos y monta el contenedor con soporte para scroll vertical
     * adaptativo.
     */
    public DashboardVoluntarioPnl() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JLabel lblTituloSeccion = new JLabel("Iniciativas de Gestión Ambiental");
        lblTituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTituloSeccion.setBorder(new EmptyBorder(20, 25, 10, 25));
        lblTituloSeccion.setForeground(new Color(23, 93, 62));
        add(lblTituloSeccion, BorderLayout.NORTH);
        
        contenedorCards = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        contenedorCards.setBackground(new Color(245, 247, 250));

        JScrollPane scroll = new JScrollPane(contenedorCards);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }
    
    /**
     * Reconstruye dinámicamente el catálogo de tarjetas visuales en el panel.
     * Remueve los componentes previos del contenedor, itera el listado total de
     * planificaciones contrastando el estado de pertenencia del voluntario para
     * instanciar nuevos objetos {@code IniciativaCard}, y fuerza el repintado
     * estructural de la interfaz de usuario.
     *
     * * @param listaTotal Colección {@link List} que contiene el universo de
     * todas las instancias de {@link Iniciativa} vigentes.
     * @param misIds Colección {@link List} de enteros conteniendo únicamente
     * los IDs de las iniciativas a las que pertenece el voluntario autenticado.
     */
    public void cargarCards(List<Iniciativa> listaTotal, List<Integer> misIds) {
        contenedorCards.removeAll();
        for (Iniciativa ini : listaTotal) {
            boolean esMia = misIds.contains(ini.getIdIniciativa());
            contenedorCards.add(new IniciativaCard(ini, esMia));
        }
        contenedorCards.revalidate();
        contenedorCards.repaint();
    }
}
