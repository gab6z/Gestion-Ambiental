package vista;

import modelo.Iniciativa;
import modelo.Sector;
import modelo.Tarea;
import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
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
    private com.toedter.calendar.JDateChooser jdFiltroDesde;
    private com.toedter.calendar.JDateChooser jdFiltroHasta;
    private JList<Sector> listFiltroSectores;
    private DefaultListModel<Sector> modeloListaSectores;
    private JComboBox<String> cmbFiltroEstado;
    private JButton btnFiltrar;
    private JButton btnLimpiarFiltro;
    private com.toedter.calendar.JDateChooser jdFechaFin;
    
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
        construirPanelFiltros();    

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
        
        agregarEtiqueta(gblForm, gbc, "Fecha Fin:", f++);
        jdFechaFin = new com.toedter.calendar.JDateChooser();
        jdFechaFin.setDateFormatString("yyyy-MM-dd");
        gbc.gridy = f++;
        gblForm.add(jdFechaFin, gbc);

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
        
        JPanel pnlDerecho = new JPanel(new BorderLayout(0, 8));
        pnlDerecho.setBackground(new Color(245, 247, 250));
        pnlDerecho.add(construirPanelFiltros(), BorderLayout.NORTH);
        pnlDerecho.add(new JScrollPane(tablaIniciativas), BorderLayout.CENTER);
        add(pnlDerecho, BorderLayout.CENTER);
    }
    
    /**
     * Construye el panel superior de filtros con rango de fechas,
     * multiselección de sectores y estado de iniciativa.
     *
     * @return JPanel configurado con todos los controles de filtrado.
     */
    private JPanel construirPanelFiltros() {
        JPanel pnlFiltros = new JPanel(new GridBagLayout());
        pnlFiltros.setBackground(new Color(235, 240, 235));
        pnlFiltros.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(34, 115, 78)),
                "Filtros de Búsqueda",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 11),
                new Color(23, 93, 62)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        gbc.weightx = 0;

        gbc.gridx = 0;
        pnlFiltros.add(new JLabel("Desde:"), gbc);

        gbc.gridx = 1;
        pnlFiltros.add(new JLabel("Hasta:"), gbc);

        gbc.gridx = 2;
        pnlFiltros.add(new JLabel("Sectores:"), gbc);

        gbc.gridx = 3;
        pnlFiltros.add(new JLabel("Estado:"), gbc);

        gbc.gridy = 1;

        gbc.gridx = 0;
        gbc.weightx = 0;
        jdFiltroDesde = new com.toedter.calendar.JDateChooser();
        jdFiltroDesde.setDateFormatString("yyyy-MM-dd");
        jdFiltroDesde.setPreferredSize(new Dimension(120, 26));
        pnlFiltros.add(jdFiltroDesde, gbc);

        gbc.gridx = 1;
        jdFiltroHasta = new com.toedter.calendar.JDateChooser();
        jdFiltroHasta.setDateFormatString("yyyy-MM-dd");
        jdFiltroHasta.setPreferredSize(new Dimension(120, 26));
        pnlFiltros.add(jdFiltroHasta, gbc);

        gbc.gridx = 2;
        gbc.gridheight = 1;
        modeloListaSectores = new DefaultListModel<>();
        listFiltroSectores = new JList<>(modeloListaSectores);
        listFiltroSectores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listFiltroSectores.setVisibleRowCount(3);
        JScrollPane scrollSectores = new JScrollPane(listFiltroSectores);
        scrollSectores.setPreferredSize(new Dimension(150, 60)); // fijo siempre
        pnlFiltros.add(scrollSectores, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0;
        cmbFiltroEstado = new JComboBox<>(new String[]{
            "Todos", "Planificada", "En ejecución", "Finalizada", "Cancelada"
        });
        cmbFiltroEstado.setPreferredSize(new Dimension(130, 26));
        pnlFiltros.add(cmbFiltroEstado, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;

        gbc.gridy = 0;
        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setBackground(new Color(34, 115, 78));
        btnFiltrar.setForeground(Color.WHITE);
        btnFiltrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFiltrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFiltrar.setPreferredSize(new Dimension(110, 26));
        pnlFiltros.add(btnFiltrar, gbc);

        gbc.gridy = 1;
        btnLimpiarFiltro = new JButton("Limpiar Filtros");
        btnLimpiarFiltro.setBackground(Color.GRAY);
        btnLimpiarFiltro.setForeground(Color.WHITE);
        btnLimpiarFiltro.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLimpiarFiltro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiarFiltro.setPreferredSize(new Dimension(110, 26));
        pnlFiltros.add(btnLimpiarFiltro, gbc);

        return pnlFiltros;
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
        
        try {
            i.setPresupuesto(Double.parseDouble(txtPresupuesto.getText().trim()));
        } catch (NumberFormatException e) {
            i.setPresupuesto(0);
        }

        try {
            i.setMeta(Integer.parseInt(txtMeta.getText().trim()));
        } catch (NumberFormatException e) {
            i.setMeta(0);
        }

        // Combos seguros
        if (cmbSector.getSelectedItem() != null) {
            i.setIdSector(((Sector) cmbSector.getSelectedItem()).getIdSector());
        }

        if (cmbTarea.getSelectedItem() != null) {
            i.setIdTarea(((Tarea) cmbTarea.getSelectedItem()).getIdTarea());
        }

        if (cmbGestion.getSelectedItem() != null) {
            i.setIdGestion(((Gestion) cmbGestion.getSelectedItem()).getIdGestion());
        }

        i.setEstado(cmbEstado.getSelectedItem().toString());

        if (jdFechaEjecucion.getDate() != null) {
            i.setFechaEjecucion(new java.sql.Date(
                    jdFechaEjecucion.getDate().getTime()));
        }
        
        if (jdFechaFin.getDate() != null) {
            i.setFechaFin(new java.sql.Date(
                    jdFechaFin.getDate().getTime()));       
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
        jdFechaFin.setDate(ini.getFechaFin());
 
        if (ini.getEstado() != null) {
            cmbEstado.setSelectedItem(ini.getEstado());
        }
        
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
        jdFechaFin.setDate(null);
        
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
    
    /**
     * Carga los sectores disponibles en la lista multiselect de filtros.
     * Reutiliza el mismo catálogo ya cargado en el combo del formulario.
     *
     * @param lista Lista de sectores a mostrar.
     */
    public void cargarListaFiltroSectores(List<Sector> lista) {
        modeloListaSectores.clear();
        for (Sector s : lista) {
            modeloListaSectores.addElement(s);
        }
    }

    /**
     * @return Sectores seleccionados en el filtro multiselect.
     */
    public List<Sector> getSectoresFiltroSeleccionados() {
        return listFiltroSectores.getSelectedValuesList();
    }

    /**
     * @return Fecha de inicio del rango de filtro, o null si no se seleccionó.
     */
    public Date getFiltroDesde() {
        return jdFiltroDesde.getDate() != null
                ? new java.sql.Date(jdFiltroDesde.getDate().getTime()) : null;
    }

    /**
     * @return Fecha fin del rango de filtro, o null si no se seleccionó.
     */
    public Date getFiltroHasta() {
        return jdFiltroHasta.getDate() != null
                ? new java.sql.Date(jdFiltroHasta.getDate().getTime()) : null;
    }

    /**
     * @return Estado seleccionado en el filtro, o "Todos" si no se filtró.
     */
    public String getFiltroEstado() {
        return cmbFiltroEstado.getSelectedItem().toString();
    }

    /**
     * Resetea todos los controles del panel de filtros a su estado inicial.
     */
    public void limpiarFiltros() {
        jdFiltroDesde.setDate(null);
        jdFiltroHasta.setDate(null);
        listFiltroSectores.clearSelection();
        cmbFiltroEstado.setSelectedIndex(0);
    }

    /**
     * @return Botón Filtrar para enlazar en el controlador.
     */
    public JButton getBtnFiltrar() {
        return btnFiltrar;
    }

    /**
     * @return Botón Limpiar Filtros para enlazar en el controlador.
     */
    public JButton getBtnLimpiarFiltro() {
        return btnLimpiarFiltro;
    }

}
