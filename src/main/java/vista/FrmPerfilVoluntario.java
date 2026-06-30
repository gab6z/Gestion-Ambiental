package vista;

import dao.ParticipacionDAO;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import modelo.Participacion;
import modelo.Voluntario;
import java.util.List;

/**
 * Panel para visualizar el perfil del voluntario, datos personales e historial de participaciones.
 * @author EDUARDO
 */
public class FrmPerfilVoluntario extends JPanel {
    private final Color COLOR_PRIMARIO = new Color(23, 93, 62);
    private final Color COLOR_FONDO = new Color(245, 247, 250);
    private Voluntario voluntario;
    private JPanel pnlTarjetasContenedor;

    public FrmPerfilVoluntario(Voluntario v) {
        this.voluntario = v;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        inicializarComponentes();
        cargarIniciativas();
    }

    private void inicializarComponentes() {
        JPanel pnlContenedor = new JPanel();
        pnlContenedor.setLayout(new BoxLayout(pnlContenedor, BoxLayout.Y_AXIS));
        pnlContenedor.setBackground(Color.WHITE);
        pnlContenedor.setBorder(new EmptyBorder(40, 30, 40, 30));

        JLabel lblAvatar = new JLabel();
        lblAvatar.setIcon(crearAvatarCircular(voluntario.getNombres_completos(), 80, COLOR_PRIMARIO));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAvatar.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblNombre = new JLabel(voluntario.getNombres_completos().toUpperCase());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlContenedor.add(lblAvatar);
        pnlContenedor.add(lblNombre);
        pnlContenedor.add(Box.createVerticalStrut(20));

        pnlContenedor.add(crearFilaInfo("Cédula:", voluntario.getCedula()));
        pnlContenedor.add(crearFilaInfo("Correo:", voluntario.getCorreo()));
        pnlContenedor.add(crearFilaInfo("Teléfono:", voluntario.getTelefono()));
        pnlContenedor.add(Box.createVerticalStrut(20));

        JPanel pnlEspecial = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlEspecial.setBackground(Color.WHITE);
        pnlEspecial.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        pnlEspecial.add(crearBloqueInfo("Habilidades", voluntario.getHabilidades()));
        pnlEspecial.add(crearBloqueInfo("Disponibilidad", voluntario.getDisponibilidad_dias()));
        pnlContenedor.add(pnlEspecial);

        pnlContenedor.add(Box.createVerticalStrut(30));

        JLabel lblTituloTabla = new JLabel("MIS INICIATIVAS");
        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloTabla.setForeground(COLOR_PRIMARIO);
        lblTituloTabla.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlContenedor.add(lblTituloTabla);
        pnlContenedor.add(Box.createVerticalStrut(15));

        pnlTarjetasContenedor = new JPanel();
        pnlTarjetasContenedor.setLayout(new BoxLayout(pnlTarjetasContenedor, BoxLayout.Y_AXIS));
        pnlTarjetasContenedor.setBackground(Color.WHITE);

        JScrollPane scrollTarjetas = new JScrollPane(pnlTarjetasContenedor);
        scrollTarjetas.setPreferredSize(new Dimension(550, 200));
        scrollTarjetas.setBorder(BorderFactory.createEmptyBorder());
        scrollTarjetas.getVerticalScrollBar().setUnitIncrement(16);

        pnlContenedor.add(scrollTarjetas);
        add(pnlContenedor, BorderLayout.CENTER);
    }

    private JPanel crearFilaInfo(String titulo, String valor) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl.setBackground(Color.WHITE);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblT.setForeground(new Color(80, 80, 80));
        lblT.setPreferredSize(new Dimension(100, 25));

        JLabel lblV = new JLabel(valor);
        lblV.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblV.setForeground(Color.BLACK);

        pnl.add(lblT);
        pnl.add(lblV);
        return pnl;
    }

    private JPanel crearBloqueInfo(String titulo, String valor) {
        String texto = (valor == null || valor.isEmpty()) ? "No registrado" : valor;
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(new Color(242, 248, 245));
        pnl.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel lblT = new JLabel(titulo.toUpperCase());
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblT.setForeground(COLOR_PRIMARIO);

        JLabel lblV = new JLabel("<html><body style='width: 180px'>" + texto + "</body></html>");
        lblV.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        pnl.add(lblT);
        pnl.add(Box.createVerticalStrut(5));
        pnl.add(lblV);
        return pnl;
    }

    public void cargarIniciativas() {
        if (pnlTarjetasContenedor == null) return;
        pnlTarjetasContenedor.removeAll();

        try {
            ParticipacionDAO dao = new ParticipacionDAO();
            List<Participacion> lista = dao.listarPorVoluntario(voluntario.getId_voluntario());

            if (lista.isEmpty()) {
                JLabel lblVacio = new JLabel("Aún no tienes iniciativas registradas.");
                lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
                lblVacio.setForeground(Color.GRAY);
                pnlTarjetasContenedor.add(lblVacio);
            } else {
                for (Participacion p : lista) {
                    pnlTarjetasContenedor.add(crearTarjetaIniciativa(
                        p.getNombreIniciativa(), p.getFechaIniciativa().toString(), p.getEstado()));
                    pnlTarjetasContenedor.add(Box.createVerticalStrut(10));
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        pnlTarjetasContenedor.revalidate();
        pnlTarjetasContenedor.repaint();
    }

    private JPanel crearTarjetaIniciativa(String nombre, String fecha, String estado) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BorderLayout(10, 5));
        tarjeta.setBackground(new Color(250, 252, 250));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 225, 220), 1, true),
                new EmptyBorder(12, 18, 12, 18)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setBackground(tarjeta.getBackground());

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNombre.setForeground(COLOR_PRIMARIO);

        JLabel lblFecha = new JLabel("Fecha programada: " + fecha);
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFecha.setForeground(Color.GRAY);

        pnlInfo.add(lblNombre);
        pnlInfo.add(Box.createVerticalStrut(4));
        pnlInfo.add(lblFecha);

        JLabel lblEstado = new JLabel("  " + estado.toUpperCase() + "  ");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        if (estado.equalsIgnoreCase("PENDIENTE") || estado.equalsIgnoreCase("EN PROCESO")) {
            lblEstado.setForeground(new Color(210, 130, 0));
        } else {
            lblEstado.setForeground(COLOR_PRIMARIO);
        }
        lblEstado.setBorder(new LineBorder(lblEstado.getForeground(), 1, true));

        tarjeta.add(pnlInfo, BorderLayout.CENTER);
        tarjeta.add(lblEstado, BorderLayout.EAST);
        return tarjeta;
    }

    private Icon crearAvatarCircular(String nombreCompleto, int tamaño, Color colorFondo) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorFondo);
                g2.fillOval(x, y, tamaño, tamaño);
                String inicial = (nombreCompleto != null && !nombreCompleto.isEmpty()) ? nombreCompleto.substring(0, 1).toUpperCase() : "?";
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, tamaño / 2));
                FontMetrics fm = g2.getFontMetrics();
                int txtX = x + (tamaño - fm.stringWidth(inicial)) / 2;
                int txtY = y + ((tamaño - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(inicial, txtX, txtY);
                g2.dispose();
            }
            @Override public int getIconWidth() { return tamaño; }
            @Override public int getIconHeight() { return tamaño; }
        };
    }
}