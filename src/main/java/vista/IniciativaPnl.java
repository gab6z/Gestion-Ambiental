package vista;

import modelo.Iniciativa;
import modelo.Sector;
import modelo.Tarea;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.Gestion;

/**
 * Panel de Interfaz Gráfica (Vista) que representa el formulario de
 * Planificación de Iniciativas. Proporciona el entorno visual (GUI) necesario
 * para que el Administrador gestione el ciclo de vida de las planificaciones
 * del sistema EcoVida.
 * <p>
 * Incorpora un diseño híbrido utilizando {@link GridBagLayout} para los campos
 * estructurados, contenedores dinámicos basados en {@link BoxLayout} para
 * listas interactivas de voluntarios y un mapeo síncrono bidireccional entre la
 * tabla {@link JTable} y el formulario físico.
 * </p>
 *
 * * @author Solis Caballero Geovanny Andrés
 * @version 1.3
 */
public class IniciativaPnl extends JPanel {

    /**
     * Componentes del Formulario
     */
    private JTextField txtTitulo;
    private JTextArea txtLogistica;
    private JComboBox<Sector> cmbSector;
    private JComboBox<Tarea> cmbTarea;
    private JTextField txtPresupuesto;
    private JTextField txtMeta;
    private JComboBox<String> cmbEstado;
    private com.toedter.calendar.JDateChooser jdFechaEjecucion;
    private JComboBox<Gestion> cmbGestion; 
    private JPanel pnlVoluntarios; 
    private JButton btnPDF;
    private List<JCheckBox> listaChecksVoluntarios = new ArrayList<>(); 
    private JButton btnGuardar, btnActualizar, btnEliminar, btnLimpiar;
    private JTable tablaIniciativas;
    private DefaultTableModel modeloTabla;
    private int idIniciativaActual = 0;
    
    /**
     * Constructor por defecto del panel. Arranca y ensambla todos los
     * componentes gráficos, layouts y paletas cromáticas institucionales.
     */
    public IniciativaPnl() {
        iniciarComponentes();
    }
    
    /**
     * Construye, alinea y posiciona las regiones visuales de la interfaz.
     * Divide la pantalla en una sección de formulario de controles (Región
     * Oeste) empotrada en scrolls adaptativos y una sección de visualización
     * tabular (Región Centro).
     */
    private void iniciarComponentes() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));
        

        JLabel lblTituloPnl = new JLabel("Planificación de Iniciativas (EcoVida)");
        lblTituloPnl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTituloPnl.setForeground(new Color(23, 93, 62));
        add(lblTituloPnl, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new BorderLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setPreferredSize(new Dimension(380, -1));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220)),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JPanel gblForm = new JPanel(new GridBagLayout());
        gblForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.weightx = 1.0;

        int f = 0;
        agregarEtiqueta(gblForm, gbc, "Título de la Iniciativa:", f++);
        txtTitulo = new JTextField();
        gbc.gridy = f++;
        gblForm.add(txtTitulo, gbc);

        agregarEtiqueta(gblForm, gbc, "Sector Seleccionado:", f++);
        cmbSector = new JComboBox<>();
        gbc.gridy = f++;
        gblForm.add(cmbSector, gbc);

        agregarEtiqueta(gblForm, gbc, "Tarea Ambiental:", f++);
        cmbTarea = new JComboBox<>();
        gbc.gridy = f++;
        gblForm.add(cmbTarea, gbc);
        
        agregarEtiqueta(gblForm, gbc, "Entidad de Gestión:", f++);
        cmbGestion = new JComboBox<>();
        gbc.gridy = f++;
        gblForm.add(cmbGestion, gbc);

        JPanel pnlDividido = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlDividido.setBackground(Color.WHITE);
        txtPresupuesto = new JTextField();
        txtPresupuesto.setBorder(BorderFactory.createTitledBorder("Presupuesto ($)"));
        txtMeta = new JTextField();
        txtMeta.setBorder(BorderFactory.createTitledBorder("Cant. Participantes"));
        pnlDividido.add(txtPresupuesto);
        pnlDividido.add(txtMeta);
        gbc.gridy = f++;
        gblForm.add(pnlDividido, gbc);
        
        agregarEtiqueta(gblForm, gbc, "Estado de la Iniciativa:", f++);
        cmbEstado = new JComboBox<>(new String[]{
            "Planificada",
            "En ejecución",
            "Finalizada",
            "Cancelada"
        });
        gbc.gridy = f++;
        gblForm.add(cmbEstado, gbc);
        
        agregarEtiqueta(gblForm, gbc, "Fecha Ejecución:", f++);
        jdFechaEjecucion = new com.toedter.calendar.JDateChooser();
        jdFechaEjecucion.setDateFormatString("yyyy-MM-dd"); // Para que visualmente se vea así
        gbc.gridy = f++;
        gblForm.add(jdFechaEjecucion, gbc);

        agregarEtiqueta(gblForm, gbc, "Descripción Logística:", f++);
        txtLogistica = new JTextArea(3, 20);
        txtLogistica.setLineWrap(true);
        gbc.gridy = f++;
        gblForm.add(new JScrollPane(txtLogistica), gbc);
        
       agregarEtiqueta(gblForm, gbc, "Asignar Voluntarios:", f++);
        
        pnlVoluntarios = new JPanel();
        pnlVoluntarios.setLayout(new BoxLayout(pnlVoluntarios, BoxLayout.Y_AXIS));
        pnlVoluntarios.setBackground(Color.WHITE);
        
        JScrollPane scrollVol = new JScrollPane(pnlVoluntarios);
        scrollVol.setPreferredSize(new Dimension(0, 80)); 
        scrollVol.getVerticalScrollBar().setUnitIncrement(16); 
        
        gbc.gridy = f++;
        gblForm.add(scrollVol, gbc);

        JScrollPane scrollFormulario = new JScrollPane(gblForm);
        scrollFormulario.setBorder(null);
        scrollFormulario.getVerticalScrollBar().setUnitIncrement(16); 
        
        panelForm.add(scrollFormulario, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new GridLayout(2, 2, 8, 8));
        pnlBotones.setBackground(Color.WHITE);
        btnGuardar = crearBoton("Registrar", new Color(34, 166, 89));
        btnActualizar = crearBoton("Modificar", new Color(0, 102, 204));
        btnEliminar = crearBoton("Borrar", new Color(220, 53, 69));
        btnLimpiar = crearBoton("Limpiar", Color.GRAY);
        btnPDF = crearBoton("Desc. PDF", new Color(100, 100, 255));

        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnActualizar);
        pnlBotones.add(btnEliminar);
        pnlBotones.add(btnLimpiar);
        pnlBotones.add(btnPDF);
        panelForm.add(pnlBotones, BorderLayout.SOUTH);

        add(panelForm, BorderLayout.WEST);

        String[] col = {"ID", "Título", "Sector", "Tarea", "Entidad", "Fecha de Ejecución", "Estado"};
        modeloTabla = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tablaIniciativas = new JTable(modeloTabla);
        tablaIniciativas.getColumnModel().getColumn(0).setMinWidth(0);
        tablaIniciativas.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaIniciativas.getColumnModel().getColumn(0).setPreferredWidth(0);
        tablaIniciativas.setRowHeight(25);
        add(new JScrollPane(tablaIniciativas), BorderLayout.CENTER);
    }
    
    /**
     * Carga de forma limpia los registros del catálogo de sectores en el
     * componente interactivo.
     *
     * @param lista Colección {@link List} que contiene las instancias del
     * modelo {@link Sector}.
     */
    public void cargarComboSectores(List<Sector> lista) {
        cmbSector.removeAllItems();
        for (Sector s : lista) {
            cmbSector.addItem(s);
        }
    }
    
    /**
     * Carga de forma limpia los registros del catálogo de tareas en el
     * componente interactivo.
     *
     * @param lista Colección {@link List} que contiene las instancias del
     * modelo {@link Tarea}.
     */
    public void cargarComboTareas(List<Tarea> lista) {
        cmbTarea.removeAllItems();
        for (Tarea t : lista) {
            cmbTarea.addItem(t);
        }
    }
    
    /**
     * Vacía e inyecta un listado nuevo de planificaciones estructuradas dentro
     * de la grilla tabular.
     *
     * @param lista Colección {@link List} conteniendo los objetos
     * {@link Iniciativa} recuperados por el controlador.
     */
    public void cargarDatosTabla(List<Iniciativa> lista) {
        modeloTabla.setRowCount(0);
        for (Iniciativa i : lista) {
            modeloTabla.addRow(new Object[]{
                i.getIdIniciativa(),
                i.getTitulo(),
                i.getNombreSector(),
                i.getNombreTarea(),
                i.getNombreGestion(),               
                i.getFechaEjecucion(),
                i.getEstado()
            });
        }
    }
    
    /**
     * Mapea, extrae y consolida los valores ingresados en el formulario en un
     * objeto de negocio estructurado. Realiza conversiones explícitas de tipos
     * primitivos y parsea fechas desde componentes utilitarios a SQL.
     *
     * @return Una instancia modelo de tipo {@link Iniciativa} lista para
     * procesamiento o almacenamiento.
     */
    public Iniciativa getIniciativaDelFormulario() {
        Iniciativa i = new Iniciativa();
        i.setIdIniciativa(idIniciativaActual);
        i.setTitulo(txtTitulo.getText());
        i.setDescripcion(txtLogistica.getText());
        i.setPresupuesto(Double.parseDouble(txtPresupuesto.getText()));
        i.setMeta(Integer.parseInt(txtMeta.getText()));
        i.setIdSector(((Sector) cmbSector.getSelectedItem()).getIdSector());
        i.setIdTarea(((Tarea) cmbTarea.getSelectedItem()).getIdTarea());
        i.setIdGestion(((Gestion) cmbGestion.getSelectedItem()).getIdGestion());
        i.setEstado(cmbEstado.getSelectedItem().toString());

        if (jdFechaEjecucion.getDate() != null) {
            long tiempoMilis = jdFechaEjecucion.getDate().getTime();
            i.setFechaEjecucion(new java.sql.Date(tiempoMilis));
        }

        return i;
    }

    /**
     * Inyecta los atributos de una iniciativa seleccionada de regreso a los
     * controles del formulario físico. Gestiona la habilitación mutua de
     * botones operacionales de actualización para evitar inserciones cruzadas.
     *
     * @param ini El objeto {@link Iniciativa} cuyos datos poblarán la UI.
     */
    public void cargarIniciativaEnFormulario(Iniciativa ini) {
        this.idIniciativaActual = ini.getIdIniciativa();
        txtTitulo.setText(ini.getTitulo());
        txtLogistica.setText(ini.getDescripcion());
        txtPresupuesto.setText(String.valueOf(ini.getPresupuesto()));
        txtMeta.setText(String.valueOf(ini.getMeta()));
        jdFechaEjecucion.setDate(ini.getFechaEjecucion());


        for (int i = 0; i < cmbSector.getItemCount(); i++) {
            if (cmbSector.getItemAt(i).getIdSector() == ini.getIdSector()) {
                cmbSector.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < cmbTarea.getItemCount(); i++) {
            if (cmbTarea.getItemAt(i).getIdTarea() == ini.getIdTarea()) {
                cmbTarea.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < cmbGestion.getItemCount(); i++) {
            if (cmbGestion.getItemAt(i).getIdGestion() == ini.getIdGestion()) {
                cmbGestion.setSelectedIndex(i);
                break;
            }
        }

        btnGuardar.setEnabled(false);
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }
    
    /**
     * Construye de manera dinámica la lista de selección con estructura
     * CheckBox en el contenedor secundario. Vincula metadatos lógicos a los
     * componentes interactivos mediante el uso de propiedades de cliente.
     *
     * @param lista Colección {@link List} que contiene el catálogo total de
     * voluntarios vigentes.
     */
    public void cargarListaVoluntarios(List<modelo.Voluntario> lista) {
        pnlVoluntarios.removeAll();
        listaChecksVoluntarios.clear();

        for (modelo.Voluntario v : lista) {

            JCheckBox chk = new JCheckBox(v.toString());
            chk.setBackground(Color.WHITE);
            chk.setCursor(new Cursor(Cursor.HAND_CURSOR));

            chk.putClientProperty("datosVoluntario", v);

            listaChecksVoluntarios.add(chk);
            pnlVoluntarios.add(chk);
        }
        pnlVoluntarios.revalidate();
        pnlVoluntarios.repaint();
    }
    
    /**
     * Intercepta la lista de CheckBoxes y extrae únicamente las instancias de
     * los voluntarios marcados.
     *
     * @return Una colección {@link List} conteniendo los objetos
     * {@link modelo.Voluntario} seleccionados por el usuario.
     */
    public List<modelo.Voluntario> getVoluntariosSeleccionados() {
        List<modelo.Voluntario> seleccionados = new ArrayList<>();
        for (JCheckBox chk : listaChecksVoluntarios) {
            if (chk.isSelected()) {
                modelo.Voluntario v = (modelo.Voluntario) chk.getClientProperty("datosVoluntario");
                seleccionados.add(v);
            }
        }
        return seleccionados;
    }
    
    /**
     * Sincroniza y activa visualmente los CheckBoxes correspondientes a una
     * lista de IDs relacionales.
     *
     * @param ids Lista {@link List} de enteros representando los IDs de los
     * voluntarios ya asignados previamente.
     */
    public void seleccionarVoluntariosPorIds(List<Integer> ids) {
        for (JCheckBox chk : listaChecksVoluntarios) {
            chk.setSelected(false);
            
            modelo.Voluntario v = (modelo.Voluntario) chk.getClientProperty("datosVoluntario");
            
            if (ids.contains(v.getId_voluntario())) {
                chk.setSelected(true);
            }
        }
    }

    /**
     * Restablece los campos de captura informativa y los CheckBoxes del panel a
     * sus valores nulos de fábrica. Devuelve el estado de alternancia por
     * defecto de los disparadores del módulo CRUD.
     */
    public void limpiarFormulario() {
        idIniciativaActual = 0;
        txtTitulo.setText("");
        txtLogistica.setText("");
        txtPresupuesto.setText("");
        jdFechaEjecucion.setDate(null);
        txtMeta.setText("");
        
        for (JCheckBox chk : listaChecksVoluntarios) {
            chk.setSelected(false);
        }
        
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(true);
    }

    /**
     * Carga los registros del catálogo institucional en el componente ComboBox
     * de Gestión Ambiental.
     *
     * @param lista Colección {@link List} conteniendo instancias del modelo
     * {@link modelo.Gestion}.
     */
    public void cargarComboGestion(List<modelo.Gestion> lista) {
        cmbGestion.removeAllItems();
        for (modelo.Gestion g : lista) {
            cmbGestion.addItem(g);
        }
    }

    /**
     * Métodos GETTERS para eventos del controlador.
     *  
     */
    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }
    
    public JButton getBtnPDF() {
        return btnPDF;
    }
    
    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public JButton getBtnLimpiar() {
        return btnLimpiar;
    }

    public JTable getTablaIniciativas() {
        return tablaIniciativas;
    }
    
    /**
     * Método de abstracción gráfica interna para inyectar etiquetas
     * estandarizadas en el GridBagLayout.
     */
    private void agregarEtiqueta(JPanel p, GridBagConstraints g, String t, int r) {
        g.gridy = r;
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(l, g);
    }
    
    /**
     * Factoría de botones interna para mantener la consistencia del diseño de
     * la interfaz de usuario.
     */
    private JButton crearBoton(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
    
    /**
     * Muestra un cuadro modal de alerta informativa estándar al usuario.
     *
     * @param m Texto con la descripción del mensaje.
     */
    public void mostrarMensaje(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
    
    /**
     * Muestra un cuadro modal crítico configurado con ícono de excepción de
     * error.
     *
     * @param m Texto descriptivo de la falla interna encontrada.
     */
    public void mostrarError(String m) {
        JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
}
