/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import dao.ParticipacionDAO;
import javax.swing.Box;
import javax.swing.JPanel;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import modelo.Participacion;
import modelo.Voluntario;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author EDUARDO
 */
public class FrmPerfilVoluntario extends JPanel{
    private final Color COLOR_PRIMARIO = new Color(23, 93, 62);
    private final Color COLOR_FONDO = new Color(245, 247, 250);
    private Voluntario voluntario;
    private DefaultTableModel modeloTabla;

    public FrmPerfilVoluntario(Voluntario v) {
        this.voluntario = v;
        setLayout(new GridBagLayout());
        setBackground(COLOR_FONDO);
        inicializarComponentes();
        cargarIniciativas();
    }

    private void inicializarComponentes() {
        JPanel pnlContenedor = new JPanel();
        pnlContenedor.setLayout(new BoxLayout(pnlContenedor, BoxLayout.Y_AXIS));
        pnlContenedor.setBackground(Color.WHITE);
        pnlContenedor.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(25, 40, 25, 40)
        ));

        JLabel lblAvatar = new JLabel("👤");
        lblAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAvatar.setForeground(COLOR_PRIMARIO);

        JLabel lblNombre = new JLabel(voluntario.getNombres_completos().toUpperCase());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlContenedor.add(lblAvatar);
        pnlContenedor.add(lblNombre);
        pnlContenedor.add(Box.createVerticalStrut(15));

        pnlContenedor.add(crearFilaInfo("Cédula:", voluntario.getCedula()));
        pnlContenedor.add(crearFilaInfo("Correo:", voluntario.getCorreo()));
        pnlContenedor.add(crearFilaInfo("Teléfono:", voluntario.getTelefono()));
        pnlContenedor.add(Box.createVerticalStrut(15));

        JPanel pnlEspecial = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlEspecial.setBackground(Color.WHITE);
        pnlEspecial.setMaximumSize(new Dimension(550, 70));
        pnlEspecial.add(crearBloqueInfo("Habilidades", voluntario.getHabilidades()));
        pnlEspecial.add(crearBloqueInfo("Disponibilidad", voluntario.getDisponibilidad_dias()));
        pnlContenedor.add(pnlEspecial);
        
        pnlContenedor.add(Box.createVerticalStrut(25));

        JLabel lblTituloTabla = new JLabel("MIS INICIATIVAS");
        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloTabla.setForeground(COLOR_PRIMARIO);
        lblTituloTabla.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlContenedor.add(lblTituloTabla);
        pnlContenedor.add(Box.createVerticalStrut(10));

        String[] columnas = {"Iniciativa", "Fecha", "Estado"};
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tabla = new JTable(modeloTabla);
        configurarEstiloYAnchoTabla(tabla);

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(550, 140));
        scrollTabla.setBorder(new LineBorder(new Color(230, 230, 230)));
        
        pnlContenedor.add(scrollTabla);
        add(pnlContenedor);
    }

    private JPanel crearFilaInfo(String titulo, String valor) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl.setBackground(Color.WHITE);
        pnl.setMaximumSize(new Dimension(600, 30));
        JLabel lblT = new JLabel("<html><b>" + titulo + "</b></html>");
        lblT.setPreferredSize(new Dimension(90, 25));
        JLabel lblV = new JLabel(valor);
        pnl.add(lblT);
        pnl.add(lblV);
        return pnl;
    }

    private JPanel crearBloqueInfo(String titulo, String valor) {
        String texto = (valor == null || valor.isEmpty()) ? "No registrado" : valor;
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(new Color(242, 248, 245)); // Verde muy claro
        pnl.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel lblT = new JLabel(titulo.toUpperCase());
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblT.setForeground(COLOR_PRIMARIO);

        JLabel lblV = new JLabel("<html><body style='width: 150px'>" + texto + "</body></html>");
        lblV.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        pnl.add(lblT);
        pnl.add(Box.createVerticalStrut(3));
        pnl.add(lblV);
        return pnl;
    }
    
    public void cargarIniciativas() {
        if (modeloTabla == null) return;
        
        modeloTabla.setRowCount(0); 
        
        try {
            ParticipacionDAO dao = new ParticipacionDAO();
            List<Participacion> lista = dao.listarPorVoluntario(voluntario.getId_voluntario());
            for (Participacion p : lista) {
                modeloTabla.addRow(new Object[]{
                    p.getNombreIniciativa(), 
                    p.getFechaIniciativa(), 
                    p.getEstado().toUpperCase()
                });
            }
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
        }
    }
    
    private void configurarEstiloYAnchoTabla(JTable tabla) {
        tabla.setRowHeight(30);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setSelectionBackground(new Color(230, 245, 235));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setGridColor(new Color(240, 240, 240));
        tabla.setShowVerticalLines(false);

        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(COLOR_PRIMARIO);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false); // Para que no muevan las columnas

        TableColumnModel columnModel = tabla.getColumnModel();
        
        columnModel.getColumn(0).setPreferredWidth(300); 
        
        columnModel.getColumn(1).setPreferredWidth(100);
        
        columnModel.getColumn(2).setPreferredWidth(100);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        columnModel.getColumn(1).setCellRenderer(centerRenderer);
        columnModel.getColumn(2).setCellRenderer(centerRenderer);
    }
}
