/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 * Descripción: Panel de interfaz gráfica de usuario (GUI) para la gestión de Sectores.
 * Contiene el formulario de registro, la tabla interactiva de datos y la barra 
 * superior de filtros combinados. Diseñado siguiendo el patrón MVC.
 * Proyecto: Sistema de Gestión Ambiental (EcoVida)
 * 
 * @author Gabriela Solange Gonzalez Roman
 * @version 1.0
 * @since 2026-05-05
 */

import modelo.Sector;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SectoresPnl extends JPanel {

    private JTextField txtNombreZona;
    private JTextField txtLatitud;
    private JTextField txtLongitud;
    private JTextField txtProvinciaCiudad;
    private JComboBox<String> cmbNivelRiesgo;
    private JComboBox<String> cmbEstadoZona;
    private JTextArea txtDescripcion;
    private JTextField txtBusqueda;
    private JComboBox<String> cmbFiltroRiesgo;
    private JComboBox<String> cmbFiltroEstado;
    
    private JButton btnBuscar;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnExportarPDF;

    private JTable tablaSectores;
    private DefaultTableModel modeloSectores;

    private int idSectorActual = 0;

    public SectoresPnl() {
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250)); // Fondo gris clarito

        JLabel lblTitulo = new JLabel("Gestión de Sectores Ambientales");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(23, 93, 62));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelIzq = new JPanel(new BorderLayout());
        panelIzq.setBackground(Color.WHITE);
        panelIzq.setPreferredSize(new Dimension(350, 0));
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

        // Nombre de Zona
        agregarCampoFormulario(formPanel, gbc, "Nombre de la Zona:", fila++);
        txtNombreZona = new JTextField();
        gbc.gridy = fila++; formPanel.add(txtNombreZona, gbc);

        // Provincia / Ciudad
        agregarCampoFormulario(formPanel, gbc, "Provincia y Ciudad:", fila++);
        txtProvinciaCiudad = new JTextField();
        txtProvinciaCiudad.setToolTipText("Ej. Guayas - Samborondón (Centro)");
        gbc.gridy = fila++; formPanel.add(txtProvinciaCiudad, gbc);

        // Coordenadas (Latitud y Longitud en la misma fila)
        JPanel pnlCoordenadas = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlCoordenadas.setBackground(Color.WHITE);
        txtLatitud = new JTextField(); txtLatitud.setBorder(BorderFactory.createTitledBorder("Latitud"));
        txtLongitud = new JTextField(); txtLongitud.setBorder(BorderFactory.createTitledBorder("Longitud"));
        pnlCoordenadas.add(txtLatitud);
        pnlCoordenadas.add(txtLongitud);
        gbc.gridy = fila++; formPanel.add(pnlCoordenadas, gbc);

        // Nivel de Riesgo
        agregarCampoFormulario(formPanel, gbc, "Nivel de Riesgo:", fila++);
        cmbNivelRiesgo = new JComboBox<>(new String[]{"Seleccionar...", "Bajo", "Medio", "Alto"});
        gbc.gridy = fila++; formPanel.add(cmbNivelRiesgo, gbc);

        // Estado de Zona
        agregarCampoFormulario(formPanel, gbc, "Estado de la Zona:", fila++);
        cmbEstadoZona = new JComboBox<>(new String[]{"Seleccionar...", "Requiere intervención", "En proceso", "Restaurado"});
        gbc.gridy = fila++; formPanel.add(cmbEstadoZona, gbc);

        // Descripción
        agregarCampoFormulario(formPanel, gbc, "Descripción del Terreno:", fila++);
        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        gbc.gridy = fila++; formPanel.add(scrollDesc, gbc);

        panelIzq.add(formPanel, BorderLayout.NORTH);

        // Botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        btnGuardar = crearBoton("Guardar", new Color(34, 166, 89));
        btnActualizar = crearBoton("Actualizar", new Color(0, 102, 204));
        btnEliminar = crearBoton("Eliminar", new Color(220, 53, 69));
        btnLimpiar = crearBoton("Limpiar", Color.GRAY);

        btnActualizar.setEnabled(false); 
        btnEliminar.setEnabled(false);

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

        gf.gridy = 0; 
        
        gf.gridx = 0; 
        gf.fill = GridBagConstraints.NONE; 
        gf.weightx = 0.0;
        pnlFiltros.add(new JLabel("Buscar Zona:"), gf);
        
        gf.gridx = 1; 
        gf.fill = GridBagConstraints.HORIZONTAL; 
        gf.weightx = 1.0; 
        txtBusqueda = new JTextField();
        txtBusqueda.setPreferredSize(new Dimension(150, 25)); 
        pnlFiltros.add(txtBusqueda, gf);

        gf.gridx = 2; 
        gf.fill = GridBagConstraints.NONE;
        gf.weightx = 0.0;
        pnlFiltros.add(new JLabel("Riesgo:"), gf);
        
        gf.gridx = 3; 
        gf.fill = GridBagConstraints.HORIZONTAL; 
        gf.weightx = 0.5;
        cmbFiltroRiesgo = new JComboBox<>(new String[]{"Todos", "Bajo", "Medio", "Alto"});
        pnlFiltros.add(cmbFiltroRiesgo, gf);

        gf.gridy = 1; 
        
        gf.gridx = 0; 
        gf.fill = GridBagConstraints.NONE;
        gf.weightx = 0.0;
        pnlFiltros.add(new JLabel("Estado:"), gf);
        
        gf.gridx = 1; 
        gf.gridwidth = 2; 
        gf.fill = GridBagConstraints.HORIZONTAL; 
        gf.weightx = 1.0;
        cmbFiltroEstado = new JComboBox<>(new String[]{"Todos", "Requiere intervención", "En proceso", "Restaurado"});
        pnlFiltros.add(cmbFiltroEstado, gf);

        gf.gridx = 3; 
        gf.gridwidth = 1; 
        gf.fill = GridBagConstraints.NONE; 
        gf.weightx = 0.0;
        
        JPanel pnlBotonesAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlBotonesAccion.setBackground(Color.WHITE);
        
        btnBuscar = crearBoton("Filtrar", new Color(0, 102, 204));
        btnExportarPDF = crearBoton("Exportar a PDF", new Color(220, 53, 69)); 
        
        pnlBotonesAccion.add(btnBuscar);
        pnlBotonesAccion.add(btnExportarPDF);
        
        pnlFiltros.add(pnlBotonesAccion, gf);

        String[] columnas = {"ID", "Zona", "Provincia/Ciudad", "Riesgo", "Estado"};
        modeloSectores = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaSectores = new JTable(modeloSectores);
        tablaSectores.setRowHeight(30);
        tablaSectores.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaSectores.getTableHeader().setBackground(new Color(240, 245, 250));
        
        tablaSectores.getColumnModel().getColumn(0).setMinWidth(0);
        tablaSectores.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaSectores.getColumnModel().getColumn(0).setWidth(0);
        tablaSectores.getColumnModel().getColumn(0).setPreferredWidth(0);

        panelDer.add(pnlFiltros, BorderLayout.NORTH); 
        panelDer.add(new JScrollPane(tablaSectores), BorderLayout.CENTER); 
        
        add(panelDer, BorderLayout.CENTER);
    }

  
    public void cargarDatosTabla(List<Sector> sectores) {
        modeloSectores.setRowCount(0); 
        for (Sector s : sectores) {
            modeloSectores.addRow(new Object[]{
                s.getIdSector(),
                s.getNombreZona(),
                s.getProvinciaCiudad(),
                s.getNivelRiesgo(),
                s.getEstadoZona()
            });
        }
    }

    // Limpiar el formulario
    public void limpiarFormulario() {
        idSectorActual = 0;
        txtNombreZona.setText("");
        txtLatitud.setText("");
        txtLongitud.setText("");
        txtProvinciaCiudad.setText("");
        cmbNivelRiesgo.setSelectedIndex(0);
        cmbEstadoZona.setSelectedIndex(0);
        txtDescripcion.setText("");
        
        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        tablaSectores.clearSelection();
    }

    // Getters 
    public Sector getSectorDelFormulario() {
        Sector s = new Sector();
        s.setIdSector(idSectorActual);
        s.setNombreZona(txtNombreZona.getText().trim());
        s.setLatitud(txtLatitud.getText().trim());
        s.setLongitud(txtLongitud.getText().trim());
        s.setProvinciaCiudad(txtProvinciaCiudad.getText().trim());
        s.setNivelRiesgo(cmbNivelRiesgo.getSelectedItem().toString());
        s.setEstadoZona(cmbEstadoZona.getSelectedItem().toString());
        s.setDescripcionTerreno(txtDescripcion.getText().trim());
        return s;
    }

    // Llenar el formulario al hacer clic 
    public void cargarSectorEnFormulario(Sector s) {
        idSectorActual = s.getIdSector();
        txtNombreZona.setText(s.getNombreZona());
        txtLatitud.setText(s.getLatitud());
        txtLongitud.setText(s.getLongitud());
        txtProvinciaCiudad.setText(s.getProvinciaCiudad());
        cmbNivelRiesgo.setSelectedItem(s.getNivelRiesgo());
        cmbEstadoZona.setSelectedItem(s.getEstadoZona());
        txtDescripcion.setText(s.getDescripcionTerreno());

        btnGuardar.setEnabled(false);
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnActualizar() { return btnActualizar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JTable getTablaSectores() { return tablaSectores; }
    public JTextField getTxtBusqueda() { return txtBusqueda; }
    public JComboBox<String> getCmbFiltroRiesgo() { return cmbFiltroRiesgo; }
    public JComboBox<String> getCmbFiltroEstado() { return cmbFiltroEstado; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnExportarPDF() { return btnExportarPDF; }

    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
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
