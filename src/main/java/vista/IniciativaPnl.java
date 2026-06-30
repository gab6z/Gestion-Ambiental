package vista;

import com.toedter.calendar.JDateChooser;
import modelo.Iniciativa;
import modelo.Sector;
import modelo.Tarea;
import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.Gestion;

/**
 * Panel de Interfaz Gráfica (Vista) que representa el formulario de
 * Planificación de Iniciativas.
 *
 * @author Solis Caballero Geovanny Andrés
 * @version 1.3
 */
public class IniciativaPnl extends JPanel {


    private JTextField txtTitulo;
    private JTextArea txtLogistica;
    private JComboBox<Sector> cmbSector;
    private JComboBox<Tarea> cmbTarea;
    private JTextField txtPresupuesto;
    private JTextField txtMeta;
    private JComboBox<String> cmbEstado;
    private JDateChooser jdFechaEjecucion;
    private JComboBox<Gestion> cmbGestion;
    private JPanel pnlVoluntarios;
    private JButton btnPDF;
    private List<JCheckBox> listaChecksVoluntarios = new ArrayList<>();
    private JButton btnGuardar, btnActualizar, btnEliminar, btnLimpiar;
    private JTable tablaIniciativas;
    private DefaultTableModel modeloTabla;
    private int idIniciativaActual = 0;
    private JDateChooser jdFiltroDesde;
    private JDateChooser jdFiltroHasta;
    private JComboBox<String> cmbFiltroEstado;
    private JButton btnFiltrar;
    private JButton btnLimpiarFiltro;
    private JDateChooser jdFechaFin;
    private JComboBox<Sector> cmbFiltroSector;

    public IniciativaPnl() {
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        JLabel lblTituloPnl = new JLabel("Planificación de Iniciativas");
        lblTituloPnl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTituloPnl.setForeground(new Color(23, 93, 62));
        add(lblTituloPnl, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new BorderLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setPreferredSize(new Dimension(420, -1));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JPanel gblForm = new JPanel(new GridBagLayout());
        gblForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 3, 5, 3);
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
        jdFechaEjecucion = new JDateChooser();
        jdFechaEjecucion.setDateFormatString("yyyy-MM-dd");
        gbc.gridy = f++;
        gblForm.add(jdFechaEjecucion, gbc);

        agregarEtiqueta(gblForm, gbc, "Fecha Fin:", f++);
        jdFechaFin = new JDateChooser();
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

        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnActualizar);
        pnlBotones.add(btnEliminar);
        pnlBotones.add(btnLimpiar);
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
     * Construye el panel de filtros con 3 filas:
     * Fila 1: Desde - Hasta
     * Fila 2: Estado - Sector
     * Fila 3: Botones
     */
    private JPanel construirPanelFiltros() {
        JPanel contenedor = new JPanel(new BorderLayout(0, 8));
        contenedor.setBackground(Color.WHITE);
        contenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel pnlCampos = new JPanel(new GridBagLayout());
        pnlCampos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        JLabel lblDesde = new JLabel("Desde:");
        lblDesde.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlCampos.add(lblDesde, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        jdFiltroDesde = new JDateChooser();
        jdFiltroDesde.setPreferredSize(new Dimension(130, 30));
        pnlCampos.add(jdFiltroDesde, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.1;
        JLabel lblHasta = new JLabel("Hasta:");
        lblHasta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlCampos.add(lblHasta, gbc);


        gbc.gridx = 3;
        gbc.weightx = 0.4;
        jdFiltroHasta = new JDateChooser();
        jdFiltroHasta.setPreferredSize(new Dimension(130, 30));
        pnlCampos.add(jdFiltroHasta, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlCampos.add(lblEstado, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        cmbFiltroEstado = new JComboBox<>(new String[]{
            "Todos",
            "Planificada",
            "En ejecución",
            "Finalizada",
            "Cancelada"
        });
        cmbFiltroEstado.setPreferredSize(new Dimension(150, 30));
        pnlCampos.add(cmbFiltroEstado, gbc);

        // Label "Sector:"
        gbc.gridx = 2;
        gbc.weightx = 0.1;
        JLabel lblSector = new JLabel("Sector:");
        lblSector.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlCampos.add(lblSector, gbc);

        // Combo Sector
        gbc.gridx = 3;
        gbc.weightx = 0.4;
        cmbFiltroSector = new JComboBox<>();
        cmbFiltroSector.setPreferredSize(new Dimension(180, 30));
        // Renderer para mostrar "Todos" cuando el item es null
        cmbFiltroSector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Todos");
                }
                return this;
            }
        });
        pnlCampos.add(cmbFiltroSector, gbc);

        contenedor.add(pnlCampos, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        pnlBotones.setOpaque(false);

        btnFiltrar = crearBoton("Filtrar", new Color(34, 115, 78));
        btnLimpiarFiltro = crearBoton("Limpiar", Color.GRAY);
        btnPDF = crearBoton("PDF", new Color(94, 92, 230));

        pnlBotones.add(btnFiltrar);
        pnlBotones.add(btnLimpiarFiltro);
        pnlBotones.add(btnPDF);

        contenedor.add(pnlBotones, BorderLayout.SOUTH);

        return contenedor;
    }

    public void cargarComboSectores(List<Sector> lista) {
        cmbSector.removeAllItems();
        for (Sector s : lista) {
            cmbSector.addItem(s);
        }
    }

    public void cargarComboTareas(List<Tarea> lista) {
        cmbTarea.removeAllItems();
        for (Tarea t : lista) {
            cmbTarea.addItem(t);
        }
    }

    public void cargarComboGestion(List<Gestion> lista) {
        cmbGestion.removeAllItems();
        for (Gestion g : lista) {
            cmbGestion.addItem(g);
        }
    }

    public void cargarListaFiltroSectores(List<Sector> lista) {
        cmbFiltroSector.removeAllItems();
        cmbFiltroSector.addItem(null); 
        for (Sector s : lista) {
            cmbFiltroSector.addItem(s);
        }
    }

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
            i.setFechaEjecucion(new java.sql.Date(jdFechaEjecucion.getDate().getTime()));
        }

        if (jdFechaFin.getDate() != null) {
            i.setFechaFin(new java.sql.Date(jdFechaFin.getDate().getTime()));
        }
        return i;
    }

    public void cargarIniciativaEnFormulario(Iniciativa ini) {
        this.idIniciativaActual = ini.getIdIniciativa();
        txtTitulo.setText(ini.getTitulo());
        txtLogistica.setText(ini.getDescripcion());
        txtPresupuesto.setText(String.valueOf(ini.getPresupuesto()));
        txtMeta.setText(String.valueOf(ini.getMeta()));
        jdFechaEjecucion.setDate(ini.getFechaEjecucion());
        jdFechaFin.setDate(ini.getFechaFin());

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

    public void seleccionarVoluntariosPorIds(List<Integer> ids) {
        for (JCheckBox chk : listaChecksVoluntarios) {
            chk.setSelected(false);
            modelo.Voluntario v = (modelo.Voluntario) chk.getClientProperty("datosVoluntario");
            if (ids.contains(v.getId_voluntario())) {
                chk.setSelected(true);
            }
        }
    }

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

    public Date getFiltroDesde() {
        return jdFiltroDesde.getDate() != null
                ? new java.sql.Date(jdFiltroDesde.getDate().getTime()) : null;
    }

    public Date getFiltroHasta() {
        return jdFiltroHasta.getDate() != null
                ? new java.sql.Date(jdFiltroHasta.getDate().getTime()) : null;
    }

    public String getFiltroEstado() {
        return cmbFiltroEstado.getSelectedItem().toString();
    }

    public Sector getSectorFiltroSeleccionado() {
        return (Sector) cmbFiltroSector.getSelectedItem();
    }

    public void limpiarFiltros() {
        jdFiltroDesde.setDate(null);
        jdFiltroHasta.setDate(null);
        if (cmbFiltroSector.getItemCount() > 0) {
            cmbFiltroSector.setSelectedIndex(0);
        }
        cmbFiltroEstado.setSelectedIndex(0);
    }

    // ===== GETTERS PARA BOTONES =====
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnActualizar() { return btnActualizar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnFiltrar() { return btnFiltrar; }
    public JButton getBtnLimpiarFiltro() { return btnLimpiarFiltro; }
    public JButton getBtnPDF() { return btnPDF; }
    public JTable getTablaIniciativas() { return tablaIniciativas; }

    private void agregarEtiqueta(JPanel p, GridBagConstraints g, String t, int r) {
        g.gridy = r;
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(l, g);
    }

    private JButton crearBoton(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        return b;
    }

    public void mostrarMensaje(String m) {
        JOptionPane.showMessageDialog(this, m);
    }

    public void mostrarError(String m) {
        JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE);
    }
}