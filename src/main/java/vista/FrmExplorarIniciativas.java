/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;
import dao.IniciativaDAO;
import dao.ParticipacionDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import modelo.Iniciativa;
import modelo.Voluntario;
import modelo.Participacion;
/**
 *
 * @author EDUARDO
 */
public class FrmExplorarIniciativas extends JPanel {
private JTable tabla;
    private DefaultTableModel modelo;
    private Voluntario voluntarioActual;
    
    // Paleta de colores consistente
    private final Color VERDE_ECO = new Color(23, 93, 62);
    private final Color GRIS_CLARO = new Color(245, 245, 245);

    public FrmExplorarIniciativas(Voluntario v) {
        this.voluntarioActual = v;
        setLayout(new BorderLayout(0, 20)); 
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Iniciativas Disponibles");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(VERDE_ECO);
        lblTitulo.setHorizontalAlignment(SwingConstants.LEFT);
        add(lblTitulo, BorderLayout.NORTH);

        String[] columnas = {"ID", "Título de la Iniciativa", "Sector / Zona", "Tarea Principal", "Fecha de Ejecución"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tabla = new JTable(modelo);
        tabla.setRowHeight(35); 
        tabla.setSelectionBackground(new Color(200, 230, 201));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setShowVerticalLines(false); 
        tabla.setGridColor(new Color(230, 230, 230));

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(VERDE_ECO);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnInscribirse = new JButton("Postularme ahora");
        btnInscribirse.setBackground(VERDE_ECO);
        btnInscribirse.setForeground(Color.WHITE);
        btnInscribirse.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnInscribirse.setFocusPainted(false);
        btnInscribirse.setPreferredSize(new Dimension(200, 45));
        btnInscribirse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnInscribirse.addActionListener(e -> inscribirseAccion());
        
        JPanel pnlSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSur.setBackground(Color.WHITE);
        pnlSur.add(btnInscribirse);
        add(pnlSur, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        try {
            IniciativaDAO dao = new IniciativaDAO();
            List<Iniciativa> lista = dao.listarTodas(); 
            
            modelo.setRowCount(0);
            for (Iniciativa ini : lista) {
                // Filtrar para no mostrar eliminadas
                if (!"Eliminado".equalsIgnoreCase(ini.getEstado())) {
                    modelo.addRow(new Object[]{
                        ini.getIdIniciativa(),
                        ini.getTitulo(),
                        ini.getNombreSector(),
                        ini.getNombreTarea(),
                        ini.getFechaEjecucion()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inscribirseAccion() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una iniciativa de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idIni = (int) modelo.getValueAt(fila, 0);
        String nombreIni = (String) modelo.getValueAt(fila, 1);

        int confirmar = JOptionPane.showConfirmDialog(this, 
            "¿Confirmas tu postulación para:\n" + nombreIni + "?", 
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