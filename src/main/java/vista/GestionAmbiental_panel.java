package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Panel de interfaz gráfica de usuario (GUI) para la gestión de Entidades y Parámetros.
 * Esta clase construye el formulario de registro y la tabla de visualización, 
 * aplicando los estilos visuales del proyecto EcoVida (colores, fuentes y bordes).
 * Representa la "Capa de Vista" en el patrón de arquitectura MVC.
 * Proyecto: Sistema de Gestión de Iniciativas de Preservación Ambiental (SGIPA)
 * @author Dominica Lilibeth Torres Bohorquez
 * @version 1.0
 * @since 2026-05-05
 */
public class GestionAmbiental_panel extends JPanel {

    public JTextField txtRuc, txtNombreEntidad, txtMetaAnual, txtBuscar;
    public JComboBox<String> cbxTipoAutorizacion, cbxUnidadMedida, cbxEstadoConvenio, cbxCategoria;
    public JButton btnGuardar, btnActualizar, btnEliminar, btnLimpiar, btnExportar; 
    public JTable tblGestion;
    public JLabel lblSimbolo;

    public GestionAmbiental_panel() {
        iniciarComponentes();
    }

    /**
     * Inicializa y configura todos los componentes visuales del formulario,
     * incluyendo paneles, botones, tabla y campos de entrada.
     */
    private void iniciarComponentes() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250)); 

        JLabel lblTitulo = new JLabel("Gestión de Entidades y Parámetros");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(23, 93, 62));
        add(lblTitulo, BorderLayout.NORTH);

      
        JPanel panelIzq = new JPanel(new BorderLayout());
        panelIzq.setBackground(Color.WHITE);
        panelIzq.setPreferredSize(new Dimension(360, 0));
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

        int fila = 0;

        agregarCampoFormulario(formPanel, gbc, "RUC Entidad Aliada:", fila++);
        txtRuc = new JTextField();
        gbc.gridy = fila++; formPanel.add(txtRuc, gbc);

        agregarCampoFormulario(formPanel, gbc, "Nombre de Entidad:", fila++);
        txtNombreEntidad = new JTextField();
        gbc.gridy = fila++; formPanel.add(txtNombreEntidad, gbc);

        agregarCampoFormulario(formPanel, gbc, "Tipo de Autorización:", fila++);
        cbxTipoAutorizacion = new JComboBox<>(new String[]{"Gestor de Desechos", "Vivero Forestal", "Centro de Acopio"});
        gbc.gridy = fila++; formPanel.add(cbxTipoAutorizacion, gbc);

        agregarCampoFormulario(formPanel, gbc, "Categoría de Impacto:", fila++);
        cbxCategoria = new JComboBox<>(new String[]{"Alta", "Media", "Baja"});
        gbc.gridy = fila++; formPanel.add( cbxCategoria, gbc);

        agregarCampoFormulario(formPanel, gbc, "Unidad de Medida:", fila++);
        cbxUnidadMedida = new JComboBox<>(new String[]{"Kilogramos", "Toneladas", "Unidades", "Hectáreas"});
        gbc.gridy = fila++; formPanel.add(cbxUnidadMedida, gbc);

        agregarCampoFormulario(formPanel, gbc, "Meta Anual Global:", fila++);
        JPanel panelMeta = new JPanel(new BorderLayout(5, 0));
        panelMeta.setBackground(Color.WHITE);
        lblSimbolo = new JLabel("");
        lblSimbolo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtMetaAnual = new JTextField();
        panelMeta.add(lblSimbolo, BorderLayout.WEST);
        panelMeta.add(txtMetaAnual, BorderLayout.CENTER);
        gbc.gridy = fila++; formPanel.add(panelMeta, gbc);

        agregarCampoFormulario(formPanel, gbc, "Estado del Convenio:", fila++);
        cbxEstadoConvenio = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        gbc.gridy = fila++; formPanel.add(cbxEstadoConvenio, gbc);

        panelIzq.add(formPanel, BorderLayout.NORTH);

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
        add(panelIzq, BorderLayout.WEST);

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

        gf.gridx = 0; gf.gridy = 0; 
        gf.fill = GridBagConstraints.NONE; gf.weightx = 0.0;
        JLabel lblBuscar = new JLabel("Buscar por RUC o Entidad:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlFiltros.add(lblBuscar, gf);
        
        gf.gridx = 1; 
        gf.fill = GridBagConstraints.HORIZONTAL; gf.weightx = 1.0; 
        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(250, 25)); 
        pnlFiltros.add(txtBuscar, gf);

        panelDer.add(pnlFiltros, BorderLayout.NORTH);

        String[] columnas = {"RUC", "Entidad", "Autorización", "Impacto", "Unidad", "Meta", "Estado"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tblGestion = new JTable(modeloTabla);
        tblGestion.setRowHeight(30);
        tblGestion.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblGestion.getTableHeader().setBackground(new Color(240, 245, 250));

        JScrollPane scrollTabla = new JScrollPane(tblGestion);
        scrollTabla.getViewport().setBackground(Color.WHITE);
        panelDer.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelExportar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelExportar.setBackground(Color.WHITE);
        btnExportar = crearBoton("Exportar Catálogo", new Color(220, 53, 69)); 
        panelExportar.add(btnExportar);
        
        panelDer.add(panelExportar, BorderLayout.SOUTH);

        add(panelDer, BorderLayout.CENTER);
    }
 
    /**
     * Agrega una etiqueta descriptiva al formulario usando GridBagLayout.
     *
     * @param panel Panel contenedor del formulario.
     * @param gbc Restricciones de posicionamiento.
     * @param texto Texto de la etiqueta.
     * @param fila Fila donde se agregará el componente.
     */
    private void agregarCampoFormulario(JPanel panel, GridBagConstraints gbc, String texto, int fila) {
        gbc.gridy = fila;
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.DARK_GRAY);
        panel.add(lbl, gbc);
    }
 
    /**
     * Crea un botón personalizado con estilos visuales del sistema.
     *
     * @param texto Texto visible del botón.
     * @param colorFondo Color de fondo aplicado al botón.
     * @return Botón configurado visualmente.
     */
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