package vista;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import modelo.Iniciativa;
import controlador.DashboardVoluntarioControlador;

public class IniciativaCard extends JPanel {

    private Iniciativa iniciativa;
    private String estadoParticipacion;
    private DashboardVoluntarioControlador controlador; 
    private final Color VERDE_ECO = new Color(23, 93, 62);

    public IniciativaCard(Iniciativa iniciativa, String estadoParticipacion, DashboardVoluntarioControlador controlador) {
        this.iniciativa = iniciativa;
        this.estadoParticipacion = estadoParticipacion;
        this.controlador = controlador; 
        
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(15, 10));
        setBackground(Color.WHITE);
        
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(20, 25, 20, 25)
        ));
        
        setPreferredSize(new Dimension(550, 110));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel(iniciativa.getTitulo());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.BLACK);

        JLabel lblDetalles = new JLabel("<html><b>Ubicación:</b> " + iniciativa.getNombreSector() + 
                                        " &nbsp;&nbsp;&nbsp; <b>Fecha:</b> " + iniciativa.getFechaEjecucion() + "</html>");
        lblDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetalles.setForeground(Color.DARK_GRAY);

        JLabel lblTarea = new JLabel("Tarea: " + iniciativa.getNombreTarea());
        lblTarea.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblTarea.setForeground(new Color(100, 100, 100));

        pnlInfo.add(lblTitulo);
        pnlInfo.add(Box.createVerticalStrut(8));
        pnlInfo.add(lblDetalles);
        pnlInfo.add(Box.createVerticalStrut(3));
        pnlInfo.add(lblTarea);

        JPanel pnlAccion = new JPanel(new GridBagLayout()); 
        pnlAccion.setBackground(Color.WHITE);

        JButton btnAccion = new JButton();
        btnAccion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAccion.setPreferredSize(new Dimension(140, 40));
        btnAccion.setFocusPainted(false);

        if (estadoParticipacion.equalsIgnoreCase("Pendiente")) {
            btnAccion.setText("En Espera");
            btnAccion.setBackground(new Color(230, 140, 0)); 
            btnAccion.setForeground(Color.WHITE);
        } else if (estadoParticipacion.equalsIgnoreCase("Aceptado")) {
            btnAccion.setText("¡Aprobado!");
            btnAccion.setBackground(new Color(180, 200, 180)); 
            btnAccion.setForeground(Color.DARK_GRAY);
            btnAccion.setEnabled(false); 
        } else {
            btnAccion.setText("Postularme");
            btnAccion.setBackground(VERDE_ECO);
            btnAccion.setForeground(Color.WHITE);
            btnAccion.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            btnAccion.addActionListener(e -> {
                controlador.postularAIniciativa(iniciativa.getIdIniciativa(), iniciativa.getTitulo());
            });
        }

        pnlAccion.add(btnAccion);

        add(pnlInfo, BorderLayout.CENTER);
        add(pnlAccion, BorderLayout.EAST);
    }
}