package vista;

import modelo.Voluntario;
import java.util.List;                
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel de interfaz gráfica para la gestión integral de voluntarios.
 * Permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar),
 * búsqueda filtrada y exportación de datos a PDF.
 * * @author EDUARDO
 * @version 1.0
 * @since 2026-05-07
 */
public class VoluntariosPnl extends JPanel {
    private String estadoActual = "Activo"; 
    private JTextField txtCedula, txtNombres, txtCorreo, txtContrasena, txtTelefono, txtDisponibilidad;
    private JComboBox<String> cbxGenero;
    private JTextArea txtHabilidades; 
    private JButton btnGuardar, btnActualizar, btnEliminar, btnLimpiar;

    private JTextField txtBusqueda;
    private JComboBox<String> cmbFiltroBusqueda;
    private JButton btnBuscar, btnExportarPDF;
    private JTable tablaVoluntarios;
    private DefaultTableModel modeloVoluntarios;

    private int idVoluntarioActual = 0; 

    /**
     * Constructor de la clase. Inicializa los componentes visuales del panel.
     */
    public VoluntariosPnl() {
        iniciarComponentes();
    }

    /**
     * Configura y organiza todos los componentes de la interfaz (Swing).
     * Utiliza BorderLayout y GridBagLayout para la disposición de elementos.
     */
    private void iniciarComponentes() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        JLabel lblTitulo = new JLabel("Gestión de Voluntarios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(23, 93, 62));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelIzq = new JPanel(new BorderLayout());
        panelIzq.setBackground(Color.WHITE);
        panelIzq.setPreferredSize(new Dimension(350, 0));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.weightx = 1.0;

        int fila = 0;
        agregarEtiqueta(formPanel, gbc, "Cédula:", fila++);
        txtCedula = new JTextField(); gbc.gridy = fila++; formPanel.add(txtCedula, gbc);

        agregarEtiqueta(formPanel, gbc, "Nombres Completos:", fila++);
        txtNombres = new JTextField(); gbc.gridy = fila++; formPanel.add(txtNombres, gbc);

        agregarEtiqueta(formPanel, gbc, "Correo:", fila++);
        txtCorreo = new JTextField(); gbc.gridy = fila++; formPanel.add(txtCorreo, gbc);

        agregarEtiqueta(formPanel, gbc, "Contraseña:", fila++);
        txtContrasena = new JTextField(); gbc.gridy = fila++; formPanel.add(txtContrasena, gbc);

        agregarEtiqueta(formPanel, gbc, "Teléfono:", fila++);
        txtTelefono = new JTextField(); gbc.gridy = fila++; formPanel.add(txtTelefono, gbc);

        agregarEtiqueta(formPanel, gbc, "Género:", fila++);
        cbxGenero = new JComboBox<>(new String[]{"Seleccionar...", "Masculino", "Femenino", "Otro"});
        gbc.gridy = fila++; formPanel.add(cbxGenero, gbc);

        agregarEtiqueta(formPanel, gbc, "Disponibilidad (ej: Lunes a Viernes):", fila++);
        txtDisponibilidad = new JTextField(); gbc.gridy = fila++; formPanel.add(txtDisponibilidad, gbc);

        agregarEtiqueta(formPanel, gbc, "Habilidades Especiales:", fila++);
        txtHabilidades = new JTextArea(3, 20);
        txtHabilidades.setLineWrap(true);
        JScrollPane scrollHabil = new JScrollPane(txtHabilidades);
        gbc.gridy = fila++; formPanel.add(scrollHabil, gbc);
        panelIzq.add(formPanel, BorderLayout.NORTH);

        // --- INICIALIZACIÓN CRÍTICA DE BOTONES ---
        // Aquí es donde se arregla tu error:
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones.setBackground(Color.WHITE);

        btnGuardar = crearBoton("Guardar", new Color(34, 166, 89));
        btnActualizar = crearBoton("Actualizar", new Color(0, 102, 204));
        btnEliminar = crearBoton("Dar de Baja", new Color(220, 53, 69));
        btnLimpiar = crearBoton("Limpiar", Color.GRAY);

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        panelIzq.add(panelBotones, BorderLayout.SOUTH);
        add(panelIzq, BorderLayout.WEST);

        JPanel panelDer = new JPanel(new BorderLayout(0, 10));
        panelDer.setBackground(Color.WHITE);

        // Filtros
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFiltros.setBackground(Color.WHITE);
        txtBusqueda = new JTextField(12);
        cmbFiltroBusqueda = new JComboBox<>(new String[]{"Cédula", "Nombre"});
        btnBuscar = crearBoton("Filtrar", new Color(0, 102, 204));
        btnExportarPDF = crearBoton("PDF", new Color(220, 53, 69));
        
        pnlFiltros.add(new JLabel("Buscar:"));
        pnlFiltros.add(txtBusqueda);
        pnlFiltros.add(cmbFiltroBusqueda);
        pnlFiltros.add(btnBuscar);
        pnlFiltros.add(btnExportarPDF);

        // Tabla
        String[] columnas = {"ID", "Nombres", "Cédula", "Género", "Correo", "Disponibilidad"};
        modeloVoluntarios = new DefaultTableModel(columnas, 0);
        tablaVoluntarios = new JTable(modeloVoluntarios);

        panelDer.add(pnlFiltros, BorderLayout.NORTH);
        panelDer.add(new JScrollPane(tablaVoluntarios), BorderLayout.CENTER);
        add(panelDer, BorderLayout.CENTER);
    
    }

    /**
     * Actualiza la tabla de voluntarios con la lista proporcionada.
     * * @param lista Lista de objetos {@link Voluntario} a mostrar en la tabla.
     */
    public void cargarDatosTabla(List<Voluntario> lista) {
        modeloVoluntarios.setRowCount(0);
        for (Voluntario v : lista) {
            modeloVoluntarios.addRow(new Object[]{
                v.getId_voluntario(),
                v.getNombres_completos(),
                v.getCedula(),
                v.getGenero(),
                v.getCorreo(),
                v.getDisponibilidad_dias(),
                v.getTelefono(),
                v.getEstado()
            });
        }
    }

    /**
     * Limpia todos los campos del formulario y restablece el estado de los botones.
     */
    public void limpiarFormulario() {
        idVoluntarioActual = 0;
        txtCedula.setText("");
        txtCedula.setEditable(true);
        txtNombres.setText("");
        txtCorreo.setText("");
        txtContrasena.setText(""); 
        txtTelefono.setText("");
        txtDisponibilidad.setText("");
        txtHabilidades.setText("");
        cbxGenero.setSelectedIndex(0);
        
        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        tablaVoluntarios.clearSelection();
    }

    /**
     * Llena los campos del formulario con los datos de un voluntario específico.
     * * @param v El objeto {@link Voluntario} cuyos datos se mostrarán.
     */
    public void cargarVoluntarioEnFormulario(Voluntario v) {
        idVoluntarioActual = v.getId_voluntario();
        txtCedula.setText(v.getCedula());
        txtCedula.setEditable(false);
        txtNombres.setText(v.getNombres_completos());
        txtCorreo.setText(v.getCorreo());
        txtContrasena.setText(""); 
        txtTelefono.setText(v.getTelefono());
        cbxGenero.setSelectedItem(v.getGenero());
        txtDisponibilidad.setText(v.getDisponibilidad_dias());
        txtHabilidades.setText(v.getHabilidades());
        estadoActual = v.getEstado();

        btnGuardar.setEnabled(false);
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    /**
     * Extrae la información ingresada en el formulario y la encapsula en un objeto.
     * * @return Un objeto {@link Voluntario} con los datos actuales del formulario.
     */
    public Voluntario getVoluntarioDelFormulario() {
        Voluntario v = new Voluntario();
        v.setId_voluntario(idVoluntarioActual);
        v.setEstado(estadoActual);
        v.setCedula(txtCedula.getText().trim());
        v.setNombres_completos(txtNombres.getText().trim());
        v.setCorreo(txtCorreo.getText().trim());
        v.setContrasena(txtContrasena.getText().trim());
        v.setTelefono(txtTelefono.getText().trim());
        v.setGenero(cbxGenero.getSelectedItem().toString());
        v.setDisponibilidad_dias(txtDisponibilidad.getText().trim());
        v.setHabilidades(txtHabilidades.getText().trim()); 
        return v;
    }

    // --- MÉTODOS DE ACCESO (GETTERS) ---

    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnActualizar() { return btnActualizar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnExportarPDF() { return btnExportarPDF; }
    public JTable getTablaVoluntarios() { return tablaVoluntarios; }
    public JTextField getTxtBusqueda() { return txtBusqueda; }
    public JComboBox<String> getCmbFiltroBusqueda() { return cmbFiltroBusqueda; }

    /**
     * Muestra un cuadro de diálogo informativo al usuario.
     * @param m El mensaje informativo.
     */
    public void mostrarMensaje(String m) { JOptionPane.showMessageDialog(this, m, "Sistema", 1); }
    
    /**
     * Muestra un cuadro de diálogo de error al usuario.
     * @param m El mensaje de error.
     */
    public void mostrarError(String m) { JOptionPane.showMessageDialog(this, m, "Error", 0); }

    /**
     * Agrega de forma estandarizada una etiqueta al panel de formulario.
     */
    private void agregarEtiqueta(JPanel p, GridBagConstraints g, String t, int f) {
        g.gridy = f;
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(l, g);
    }

    /**
     * Crea y estiliza un botón con parámetros específicos de color.
     * @param t Texto del botón.
     * @param bg Color de fondo.
     * @return El objeto JButton configurado.
     */
    private JButton crearBoton(String t, Color bg) {
        JButton b = new JButton(t);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return b;
    }
}