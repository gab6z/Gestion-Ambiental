/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import dao.IniciativaDAO;
import dao.ParticipacionDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import modelo.Iniciativa;
import modelo.Voluntario;

/**
 * Panel para explorar iniciativas con un diseño moderno basado en Cards.
 * @author EDUARDO 
 */
public class FrmExplorarIniciativas extends JPanel {

    private JPanel pnlTarjetasContenedor;
    private Voluntario voluntarioActual;
    
    private final Color VERDE_ECO = new Color(23, 93, 62);
    private final Color FONDO_APP = new Color(245, 247, 250);
    private final Color FONDO_TARJETA = Color.WHITE;

    public FrmExplorarIniciativas(Voluntario v) {
        this.voluntarioActual = v;
        setLayout(new BorderLayout(0, 20)); 
        setBackground(FONDO_APP); 
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Iniciativas Disponibles");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(VERDE_ECO);
        lblTitulo.setHorizontalAlignment(SwingConstants.LEFT);
        add(lblTitulo, BorderLayout.NORTH);

        pnlTarjetasContenedor = new JPanel();
        pnlTarjetasContenedor.setLayout(new BoxLayout(pnlTarjetasContenedor, BoxLayout.Y_AXIS));
        pnlTarjetasContenedor.setBackground(FONDO_APP);

        JScrollPane scrollPane = new JScrollPane(pnlTarjetasContenedor);
        scrollPane.setBorder(null); 
        scrollPane.getViewport().setBackground(FONDO_APP);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    private void cargarDatos() {
        try {
            IniciativaDAO dao = new IniciativaDAO();
            List<Iniciativa> lista = dao.listarTodas(); 
            
            pnlTarjetasContenedor.removeAll(); 
            
            boolean hayIniciativas = false;

            for (Iniciativa ini : lista) {
                if (!"Eliminado".equalsIgnoreCase(ini.getEstado())) {
                    pnlTarjetasContenedor.add(crearTarjeta(ini));
                    pnlTarjetasContenedor.add(Box.createVerticalStrut(15)); 
                    hayIniciativas = true;
                }
            }

            if (!hayIniciativas) {
                JLabel lblVacio = new JLabel("No hay iniciativas disponibles en este momento.");
                lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                lblVacio.setForeground(Color.GRAY);
                lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
                pnlTarjetasContenedor.add(Box.createVerticalStrut(50));
                pnlTarjetasContenedor.add(lblVacio);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        pnlTarjetasContenedor.revalidate();
        pnlTarjetasContenedor.repaint();
    }

    /**
     * Dibuja una tarjeta individual para una iniciativa.
     */
    private JPanel crearTarjeta(Iniciativa ini) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BorderLayout(15, 10));
        tarjeta.setBackground(FONDO_TARJETA);
        
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(20, 25, 20, 25)
        ));
        
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setBackground(FONDO_TARJETA);

        JLabel lblTitulo = new JLabel(ini.getTitulo());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.BLACK);

        JLabel lblDetalles = new JLabel("<html><b>Ubicación:</b> " + ini.getNombreSector() + 
                                        " &nbsp;&nbsp;&nbsp; <b>Fecha:</b> " + ini.getFechaEjecucion() + "</html>");
        lblDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetalles.setForeground(Color.DARK_GRAY);

        JLabel lblTarea = new JLabel("Tarea principal: " + ini.getNombreTarea());
        lblTarea.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblTarea.setForeground(new Color(100, 100, 100));

        pnlInfo.add(lblTitulo);
        pnlInfo.add(Box.createVerticalStrut(8));
        pnlInfo.add(lblDetalles);
        pnlInfo.add(Box.createVerticalStrut(3));
        pnlInfo.add(lblTarea);


        JPanel pnlAccion = new JPanel(new GridBagLayout());
        pnlAccion.setBackground(FONDO_TARJETA);

        JButton btnPostular = new JButton("Postularme");
        btnPostular.setBackground(VERDE_ECO);
        btnPostular.setForeground(Color.WHITE);
        btnPostular.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPostular.setFocusPainted(false);
        btnPostular.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPostular.setPreferredSize(new Dimension(140, 40)); 
        
        btnPostular.addActionListener(e -> inscribirseAccion(ini.getIdIniciativa(), ini.getTitulo()));

        pnlAccion.add(btnPostular);

        tarjeta.add(pnlInfo, BorderLayout.CENTER);
        tarjeta.add(pnlAccion, BorderLayout.EAST);

        return tarjeta;
    }

    /**
     * Lógica de postulación que recibe directamente los datos del botón presionado.
     */
    private void inscribirseAccion(int idIni, String nombreIni) {
        int confirmar = JOptionPane.showConfirmDialog(this, 
            "¿Confirmas tu postulación para la iniciativa:\n'" + nombreIni + "'?", 
            "Confirmar Inscripción", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirmar == JOptionPane.YES_OPTION) {
            try {
                ParticipacionDAO pDao = new ParticipacionDAO();
                boolean exito = pDao.registrarParticipacion(voluntarioActual.getId_voluntario(), idIni);
                
                if (exito) {
                    JOptionPane.showMessageDialog(this, "¡Inscripción registrada con éxito!\nPodrás ver el estado en 'Mi Perfil'.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Ya te encuentras inscrito en esta iniciativa.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error técnico: " + e.getMessage());
            }
        }
    }
}