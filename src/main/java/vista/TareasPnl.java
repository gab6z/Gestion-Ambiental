package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Descripción: Clase encargada de la interfaz gráfica del módulo de Tareas.
 * Reestructurada dinámicamente para acoplarse de forma compacta e idéntica
 * al diseño estandarizado del módulo de Sectores.
 * Proyecto: Sistema de Gestión Ambiental (EcoVida)
 * @author Leandro Palacios
 * @version 1.2
 * @since 2026-06-29
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
     * adoptando la distribución compacta de la interfaz hermana.
     */
    public TareasPnl() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250)); // Fondo gris claro uniforme

        JLabel lblTitulo = new JLabel("Gestión de Tareas Ambientales");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(23, 93, 62));
        add(lblTitulo, BorderLayout.NORTH);

        // ================= PANEL IZQUIERDO (FORMULARIO COMPACTO - ANCHO FIXED 350) =================
        JPanel panelIzq = new JPanel(new BorderLayout());
        panelIzq.setBackground(Color.WHITE);
        panelIzq.setPreferredSize(new Dimension(350, 0)); // Evita que se estire al centro
        panelIzq.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.weightx = 1.0;

        // Inicializadores de campos
        txtId = new JTextField(); // Invisible en el Layout
        txtNombre = new JTextField();
        txtHerramientas = new JTextField();
        txtCupo = new JTextField();
        cbxDificultad = new JComboBox<>(new String[]{"Seleccionar...", "Baja", "Media", "Alta"});
        cbxEstado = new JComboBox<>(new String[]{"Seleccionar...", "disponible", "inactiva"});
        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);

        int fila = 0;

        // Mapeo ordenado del Formulario
        agregarCampoFormulario(formPanel, gbc, "Nombre de la Tarea:", fila++);
        gbc.gridy = fila++; formPanel.add(txtNombre, gbc);

        agregarCampoFormulario(formPanel, gbc, "Herramientas Requeridas:", fila++);
        gbc.gridy = fila++; formPanel.add(txtHerramientas, gbc);

        agregarCampoFormulario(formPanel, gbc, "Cupo Recomendado:", fila++);
        gbc.gridy = fila++; formPanel.add(txtCupo, gbc);

        agregarCampoFormulario(formPanel, gbc, "Nivel de Dificultad:", fila++);
        gbc.gridy = fila++; formPanel.add(cbxDificultad, gbc);

        agregarCampoFormulario(formPanel, gbc, "Estado de la Tarea:", fila++);
        gbc.gridy = fila++; formPanel.add(cbxEstado, gbc);

        agregarCampoFormulario(formPanel, gbc, "Descripción de Instrucciones:", fila++);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        gbc.gridy = fila++; formPanel.add(scrollDesc, gbc);

        panelIzq.add(formPanel, BorderLayout.NORTH);

        // Bloque de Botones inferior (Matriz 2x2 idéntica)
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        btnGuardar = crearBoton("Guardar", new Color(34, 166, 89));
        btnActualizar = crearBoton("Actualizar", new Color(0, 102, 204));
        btnEliminar = crearBoton("Eliminar", new Color(220, 53, 69));
        btnLimpiar = crearBoton("Limpiar", Color.GRAY);

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        panelIzq.add(panelBotones, BorderLayout.SOUTH);
        add(panelIzq, BorderLayout.WEST); // Anclado rígidamente a la izquierda

        // ================= PANEL DERECHO (TABLA Y FILTROS EXTENDIDOS) =================
        JPanel panelDer = new JPanel(new BorderLayout(0, 10));
        panelDer.setBackground(Color.WHITE);
        panelDer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel pnlFiltros = new JPanel(new GridBagLayout());
        pnlFiltros.setBackground(Color.WHITE);
        GridBagConstraints gf = new GridBagConstraints();
        gf.insets = new Insets(5, 5, 5, 15);
        gf.anchor = GridBagConstraints.WEST;

        gf.gridy = 0;
        gf.gridx = 0; gf.fill = GridBagConstraints.NONE; gf.weightx = 0.0;
        pnlFiltros.add(new JLabel("Buscar:"), gf);

        gf.gridx = 1; gf.fill = GridBagConstraints.HORIZONTAL; gf.weightx = 1.0;
        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(150, 25));
        pnlFiltros.add(txtBuscar, gf);

        gf.gridx = 2; gf.fill = GridBagConstraints.NONE; gf.weightx = 0.0;
        pnlFiltros.add(new JLabel("Dif:"), gf);

        gf.gridx = 3; gf.fill = GridBagConstraints.HORIZONTAL; gf.weightx = 0.5;
        cbxFiltroDificultad = new JComboBox<>(new String[]{"Todos", "Baja", "Media", "Alta"});
        pnlFiltros.add(cbxFiltroDificultad, gf);

        // Alineación horizontal de botones de control superior
        gf.gridx = 4; gf.fill = GridBagConstraints.NONE; gf.weightx = 0.0;
        JPanel pnlBotonesAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlBotonesAccion.setBackground(Color.WHITE);

        btnFiltrar = crearBoton("Filtrar", new Color(0, 102, 204));
        btnExportarPDF = crearBoton("Exportar PDF", new Color(220, 53, 69));

        pnlBotonesAccion.add(btnFiltrar);
        pnlBotonesAccion.add(btnExportarPDF);
        pnlFiltros.add(pnlBotonesAccion, gf);

        panelDer.add(pnlFiltros, BorderLayout.NORTH);

        // Configuración de la JTable con la Columna de Cupos agregada
        modelo = new DefaultTableModel(new String[]{"ID", "Tarea", "Dificultad", "Cupos", "Estado"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaTareas = new JTable(modelo);
        tablaTareas.setRowHeight(30);
        tablaTareas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaTareas.getTableHeader().setBackground(new Color(240, 245, 250));

        // Dimensionado de las celdas de datos
        tablaTareas.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaTareas.getColumnModel().getColumn(0).setMaxWidth(60);
        tablaTareas.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaTareas.getColumnModel().getColumn(3).setPreferredWidth(60);

        panelDer.add(new JScrollPane(tablaTareas), BorderLayout.CENTER);
        add(panelDer, BorderLayout.CENTER);
    }

    private void agregarCampoFormulario(JPanel panel, GridBagConstraints gbc, String texto, int fila) {
        gbc.gridy = fila;
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.DARK_GRAY);
        panel.add(lbl, gbc);
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}