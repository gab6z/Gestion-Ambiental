package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Descripción: Clase encargada de la interfaz gráfica del módulo de Tareas.
 * Define la estructura visual mediante el uso de Layout Managers (BorderLayout, 
 * GridBagLayout, GridLayout) para organizar el formulario de registro, 
 * los controles de búsqueda y la visualización de datos en tablas.
 * Proyecto: Sistema de Gestión Ambiental (EcoVida)
 * @author Leandro Palacios
 * @version 1.0
 * @since 2026-05-06
 */
public class TareasPnl extends JPanel {

    public JTextField txtId, txtNombre, txtHerramientas, txtCupo;
    public JTextArea txtDescripcion;
    public JComboBox<String> cbxDificultad, cbxEstado;
    public JButton btnGuardar, btnActualizar, btnEliminar, btnLimpiar;

    public JTextField txtBuscar;
    public JComboBox<String> cbxFiltroDificultad;
    public JButton btnFiltrar, btnExportarPDF;
    public JTable tablaTareas;
    public DefaultTableModel modelo;

    /**
     * Constructor que inicializa y configura todos los componentes visuales 
     * del panel de gestión de tareas.
     */
    public TareasPnl() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        JLabel lblTitulo = new JLabel("Gestión de Tareas Ambientales");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(23, 93, 62));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 20, 0));
        panelCentral.setOpaque(false);

        // ================= PANEL IZQUIERDO (FORMULARIO) =================
        JPanel panelIzq = new JPanel(new GridBagLayout());
        panelIzq.setBackground(Color.WHITE);
        panelIzq.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.weightx = 1.0;

        // El txtId se inicializa pero NO se agrega al panelIzq
        txtId = new JTextField(); 

        txtNombre = new JTextField();
        txtHerramientas = new JTextField();
        txtCupo = new JTextField();
        cbxDificultad = new JComboBox<>(new String[]{"Seleccionar...", "Baja", "Media", "Alta"});
        cbxEstado = new JComboBox<>(new String[]{"Seleccionar...", "disponible", "inactiva"});
        txtDescripcion = new JTextArea(3, 20);

        int f = 0;
        
        f = agregarCampoConLabel(panelIzq, gbc, "Nombre de la Tarea:", txtNombre, f);
        f = agregarCampoConLabel(panelIzq, gbc, "Herramientas Requeridas:", txtHerramientas, f);
        f = agregarCampoConLabel(panelIzq, gbc, "Cupo Recomendado:", txtCupo, f);
        f = agregarCampoConLabel(panelIzq, gbc, "Nivel de Dificultad:", cbxDificultad, f);
        f = agregarCampoConLabel(panelIzq, gbc, "Estado de la Tarea:", cbxEstado, f);
        
        gbc.gridx = 0; gbc.gridy = f++;
        JLabel lblDesc = new JLabel("Descripción de Instrucciones:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelIzq.add(lblDesc, gbc);
        gbc.gridy = f++;
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.5;
        panelIzq.add(new JScrollPane(txtDescripcion), gbc);

        gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridy = f++;
        JPanel pnlBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlBotones.setOpaque(false);
        btnGuardar = crearBoton("Guardar", new Color(34, 166, 89));
        btnActualizar = crearBoton("Actualizar", new Color(0, 102, 204));
        btnEliminar = crearBoton("Eliminar", new Color(220, 53, 69));
        btnLimpiar = crearBoton("Limpiar", Color.GRAY);
        pnlBotones.add(btnGuardar); pnlBotones.add(btnActualizar);
        pnlBotones.add(btnEliminar); pnlBotones.add(btnLimpiar);
        panelIzq.add(pnlBotones, gbc);
        panelCentral.add(panelIzq);

        // ================= PANEL DERECHO (TABLA Y FILTROS) =================
        JPanel panelDer = new JPanel(new BorderLayout(5, 5));
        panelDer.setOpaque(false);

        JPanel pnlFiltros = new JPanel(new GridBagLayout());
        pnlFiltros.setOpaque(false);
        GridBagConstraints gf = new GridBagConstraints();
        gf.insets = new Insets(5, 5, 5, 5);

        txtBuscar = new JTextField(10);
        cbxFiltroDificultad = new JComboBox<>(new String[]{"Todos", "Baja", "Media", "Alta"});
        btnFiltrar = crearBoton("Filtrar", new Color(0, 102, 204));
        btnExportarPDF = crearBoton("Exportar PDF", new Color(220, 53, 69));

        gf.gridx = 0; gf.gridy = 0; pnlFiltros.add(new JLabel("Buscar:"), gf);
        gf.gridx = 1; pnlFiltros.add(txtBuscar, gf);
        gf.gridx = 2; pnlFiltros.add(new JLabel("Dif:"), gf);
        gf.gridx = 3; pnlFiltros.add(cbxFiltroDificultad, gf);
        gf.gridx = 4; pnlFiltros.add(btnFiltrar, gf);
        
        gf.gridx = 0; gf.gridy = 1; gf.gridwidth = 5; gf.fill = GridBagConstraints.HORIZONTAL;
        pnlFiltros.add(btnExportarPDF, gf);

        panelDer.add(pnlFiltros, BorderLayout.NORTH);
        
        modelo = new DefaultTableModel(new String[]{"ID", "Tarea", "Dificultad", "Estado"}, 0);
        tablaTareas = new JTable(modelo);
        tablaTareas.setRowHeight(30);
        
        tablaTareas.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaTareas.getColumnModel().getColumn(0).setMinWidth(30);
        tablaTareas.getColumnModel().getColumn(0).setMaxWidth(70);
        
        panelDer.add(new JScrollPane(tablaTareas), BorderLayout.CENTER);
        panelCentral.add(panelDer);
        
        add(panelCentral, BorderLayout.CENTER);
    }

    /**
     * Método auxiliar para agrupar una etiqueta y su componente en el layout.
     */
    private int agregarCampoConLabel(JPanel p, GridBagConstraints gbc, String labelTexto, JComponent comp, int fila) {
        gbc.gridx = 0; gbc.gridy = fila;
        JLabel lbl = new JLabel(labelTexto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(lbl, gbc);
        gbc.gridy = fila + 1;
        p.add(comp, gbc);
        return fila + 2;
    }

    /**
     * Helper para la creación de botones personalizados.
     */
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}