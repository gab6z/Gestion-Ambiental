package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import modelo.Iniciativa;

/**
 *
 * @author Usuario
 */


public class IniciativaCard extends JPanel {

    public IniciativaCard(Iniciativa ini, boolean asignado) {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(300, 200));

        Color colorFondo = asignado ? new Color(232, 245, 233) : Color.WHITE;
        Color colorBorde = asignado ? new Color(76, 175, 80) : new Color(200, 200, 200);

        setBackground(colorFondo);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBorde, asignado ? 2 : 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // --- TÍTULO (JTextArea para multilínea sin HTML) ---
        JTextArea txtTitulo = new JTextArea(ini.getTitulo());
        txtTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtTitulo.setWrapStyleWord(true);
        txtTitulo.setLineWrap(true);
        txtTitulo.setEditable(false);
        txtTitulo.setFocusable(false);
        txtTitulo.setOpaque(false);
        add(txtTitulo, BorderLayout.NORTH);

        
        JPanel pnlDetalles = new JPanel();
        pnlDetalles.setLayout(new BoxLayout(pnlDetalles, BoxLayout.Y_AXIS));
        pnlDetalles.setOpaque(false);

        pnlDetalles.add(crearDatoLabel("Sector: ", ini.getNombreSector()));
        pnlDetalles.add(crearDatoLabel("Tarea: ", ini.getNombreTarea()));
        pnlDetalles.add(Box.createVerticalStrut(5));

        String cupos = "Participantes: " + ini.getTotalParticipantes() + " / " + ini.getMeta();
        JLabel lblCupos = new JLabel(cupos);
        lblCupos.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pnlDetalles.add(lblCupos);

        add(pnlDetalles, BorderLayout.CENTER);

        if (asignado) {
            JLabel lblAsignado = new JLabel("Has sido ASIGNADO a este proyecto.");
            lblAsignado.setForeground(new Color(46, 125, 50));
            lblAsignado.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblAsignado.setHorizontalAlignment(SwingConstants.RIGHT);
            add(lblAsignado, BorderLayout.SOUTH);
        }
    }

    private JLabel crearDatoLabel(String titulo, String valor) {
        JLabel lbl = new JLabel(titulo + valor);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}
