package controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.*;
import modelo.Tarea;
import service.TareaService;
import vista.TareasPnl;

/**
 * Descripción: Controlador principal para el módulo de Tareas.
 * Gestiona el flujo de datos entre la vista y el servicio, encargándose de 
 * los filtros de búsqueda, la validación de registros, la baja de tareas
 * y la exportación de reportes detallados en formato PDF.
 * Proyecto: Sistema de Gestión Ambiental (EcoVida)
 * @author Leandro Palacios
 * @version 1.2
 * @since 2026-06-28
 */
public class TareaControlador implements ActionListener, KeyListener {
    private final Tarea modelo;
    private final TareaService service;
    private final TareasPnl vista;

    public TareaControlador(Tarea modelo, TareaService service, TareasPnl vista) {
        this.modelo = modelo;
        this.service = service;
        this.vista = vista;

        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnActualizar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnLimpiar.addActionListener(this);
        this.vista.btnFiltrar.addActionListener(this);
        this.vista.btnExportarPDF.addActionListener(this);
        
        this.vista.txtBuscar.addKeyListener(this);
        this.vista.cbxFiltroDificultad.addActionListener(this);

        this.vista.tablaTareas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDatosFormulario();
            }
        });
    }

    public void iniciar() { listar(); }

    public void listar() {
        vista.modelo.setRowCount(0);
        try {
            llenarTabla(service.listar());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al listar: " + e.getMessage());
        }
    }

    private void llenarTabla(List<Tarea> lista) {
        for (Tarea t : lista) {
            vista.modelo.addRow(new Object[]{
                t.getIdTarea(), 
                t.getNombreTarea(), 
                t.getDificultadTecnica(),
                t.getCupoRecomendado(),
                t.getEstadoTarea()});
        }
    }

    private void cargarDatosFormulario() {
        int fila = vista.tablaTareas.getSelectedRow();
        if (fila != -1) {
            int id = Integer.parseInt(vista.tablaTareas.getValueAt(fila, 0).toString());
            try {
                Tarea t = service.listarId(id);
                vista.txtId.setText(String.valueOf(t.getIdTarea()));
                vista.txtNombre.setText(t.getNombreTarea());
                vista.txtHerramientas.setText(t.getHerramientasRequeridas());
                vista.txtCupo.setText(String.valueOf(t.getCupoRecomendado()));
                vista.cbxDificultad.setSelectedItem(t.getDificultadTecnica());
                vista.cbxEstado.setSelectedItem(t.getEstadoTarea());
                vista.txtDescripcion.setText(t.getDescripcionInstrucciones());
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * METODO 1 PARA V Y V: Procesa el guardado/actualización de tareas con validaciones robustas.
     */
    public String guardado() throws Exception {
        String nombre = vista.txtNombre.getText().trim();
        String herramientas = vista.txtHerramientas.getText().trim();
        String cupoStr = vista.txtCupo.getText().trim();
        String descripcion = vista.txtDescripcion.getText().trim();
        String dificultad = vista.cbxDificultad.getSelectedItem().toString();
        String estado = vista.cbxEstado.getSelectedItem().toString();

        if (nombre.isEmpty() || herramientas.isEmpty() || cupoStr.isEmpty() || descripcion.isEmpty()) {
            return "ERROR_CAMPOS_VACIOS";
        }
        
        if (dificultad.equals("Seleccionar...")) {
            return "ERROR_SELECCION_DIFICULTAD";
        }
        
        if (estado.equals("Seleccionar...")) {
            return "ERROR_SELECCION_ESTADO";
        }

        if (nombre.length() > 50 || herramientas.length() > 150 || descripcion.length() > 250) {
            return "ERROR_LONGITUD_EXCEDIDA";
        }

        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$") || !herramientas.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ, ]+$")) {
            return "ERROR_FORMATO_TEXTO";
        }

        try {
            int cupo = Integer.parseInt(cupoStr);
            if (cupo < 1 || cupo > 50) {
                return "ERROR_RANGO_CUPO";
            }
        } catch (NumberFormatException e) {
            return "ERROR_DATO_NUMERICO";
        }

        if (vista.txtId.getText().isEmpty()) {
            if (service.existeNombreTarea(nombre)) {
                return "ERROR_NOMBRE_DUPLICADO";
            }
        }

        asignarModelo();
        if (vista.txtId.getText().isEmpty()) {
            service.agregar(modelo);
            return "EXITO_REGISTRO";
        } else {
            modelo.setIdTarea(Integer.parseInt(vista.txtId.getText()));
            service.actualizar(modelo);
            return "EXITO_ACTUALIZACION";
        }
    }

    public boolean validarEstadoEliminacion(int id, String estadoActual) {
        boolean esEliminable = false;
        if (id > 0) {
            if (!estadoActual.equals("En curso")) {
                esEliminable = true;
            }
        }
        return esEliminable;
    }
    
    private void Filtro() {
        vista.modelo.setRowCount(0);
        try {
            llenarTabla(service.filtrar(vista.txtBuscar.getText().trim(), vista.cbxFiltroDificultad.getSelectedItem().toString()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void exportarAPDF() {
        if (vista.tablaTareas.getRowCount() == 0) {
            JOptionPane.showMessageDialog(vista, "No hay datos para exportar.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("Reporte_Tareas_EcoVida.pdf"));
        if (fc.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                Document doc = new Document();
                PdfWriter.getInstance(doc, new FileOutputStream(fc.getSelectedFile()));
                doc.open();
                doc.add(new Paragraph("Reporte de Tareas - EcoVida"));
                doc.add(new Paragraph(" "));
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.addCell("ID"); table.addCell("Nombre"); table.addCell("Dificultad"); table.addCell("Estado");
                for (int i = 0; i < vista.tablaTareas.getRowCount(); i++) {
                    for (int j = 0; j < 4; j++) table.addCell(vista.tablaTareas.getValueAt(i, j).toString());
                }
                doc.add(table);
                doc.close();
                JOptionPane.showMessageDialog(vista, "PDF Generado con éxito");
            } catch (Exception ex) { JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage()); }
        }
    }

    private void asignarModelo() {
        modelo.setNombreTarea(vista.txtNombre.getText());
        modelo.setDescripcionInstrucciones(vista.txtDescripcion.getText());
        modelo.setHerramientasRequeridas(vista.txtHerramientas.getText());
        modelo.setDificultadTecnica(vista.cbxDificultad.getSelectedItem().toString());
        modelo.setEstadoTarea(vista.cbxEstado.getSelectedItem().toString());
        try { 
            modelo.setCupoRecomendado(Integer.parseInt(vista.txtCupo.getText().trim())); 
        } catch (Exception ex) { 
            modelo.setCupoRecomendado(0); 
        }
    }

    public void limpiarFormulario() {
        vista.txtId.setText(""); vista.txtNombre.setText(""); vista.txtHerramientas.setText("");
        vista.txtCupo.setText(""); vista.txtDescripcion.setText(""); vista.txtBuscar.setText("");
        vista.cbxDificultad.setSelectedIndex(0); vista.cbxEstado.setSelectedIndex(0);
        vista.tablaTareas.clearSelection(); listar();
    }
    
    @Override 
    public void keyReleased(KeyEvent e) { if (e.getSource() == vista.txtBuscar) { Filtro(); } }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnLimpiar) {
            limpiarFormulario();
        }

        if (e.getSource() == vista.btnGuardar) {
            ejecutarProcesoGuardado();
        }

        if (e.getSource() == vista.btnActualizar) {
            int filaSeleccionada = vista.tablaTareas.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(vista, "Por favor, seleccione una tarea de la tabla para actualizar.");
                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(vista, 
                    "¿Desea guardar los cambios en esta tarea?", 
                    "Confirmación de Guardado", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                ejecutarProcesoGuardado();
            } else {
                cargarDatosFormulario();
                JOptionPane.showMessageDialog(vista, "Actualización cancelada. Se han restaurado los valores originales en la interfaz.");
            }
        }

        if (e.getSource() == vista.btnEliminar) {
            if (vista.txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Seleccione una tarea de la tabla");
                return;
            }

            int id = Integer.parseInt(vista.txtId.getText());
            String estadoActual = vista.cbxEstado.getSelectedItem().toString();

            int confirmacion = JOptionPane.showConfirmDialog(vista, 
                    "Está a punto de dar de baja esta tarea. ¿Desea continuar?", 
                    "Confirmar Baja Lógica", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    service.darDeBaja(id);
                    JOptionPane.showMessageDialog(vista, "Tarea dada de baja (Inactiva) exitosamente.");

                    listar(); 
                    limpiarFormulario();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(vista, "Error al procesar la baja lógica: " + ex.getMessage());
                }
            }
        }

        if (e.getSource() == vista.btnFiltrar || e.getSource() == vista.cbxFiltroDificultad) {
            Filtro();
        }

        if (e.getSource() == vista.btnExportarPDF) {
            exportarAPDF();
        }
    }

    private void ejecutarProcesoGuardado() {
        try {
            String resultado = guardado(); 
            
            switch (resultado) {
                case "EXITO_REGISTRO":
                    JOptionPane.showMessageDialog(vista, "Tarea registrada exitosamente");
                    listar(); limpiarFormulario(); break;
                case "EXITO_ACTUALIZACION":
                    JOptionPane.showMessageDialog(vista, "Tarea actualizada exitosamente");
                    listar(); limpiarFormulario(); break;
                case "ERROR_CAMPOS_VACIOS":
                    JOptionPane.showMessageDialog(vista, "Por favor, llene todos los campos obligatorios del formulario"); break;
                case "ERROR_SELECCION_DIFICULTAD":
                    JOptionPane.showMessageDialog(vista, "Por favor, elija un nivel de dificultad válido de la lista"); break;
                case "ERROR_SELECCION_ESTADO":
                    JOptionPane.showMessageDialog(vista, "Por favor, elija un estado válido para la tarea de la lista"); break;
                case "ERROR_LONGITUD_EXCEDIDA":
                    JOptionPane.showMessageDialog(vista, "Error de longitud: Nombre máx 50, Herramientas máx 150, Descripción máx 250 caracteres"); break;
                case "ERROR_FORMATO_TEXTO":
                    JOptionPane.showMessageDialog(vista, "Formato incorrecto: Use solo letras en Nombre/Herramientas"); break;
                case "ERROR_RANGO_CUPO":
                    JOptionPane.showMessageDialog(vista, "El cupo debe estar entre 1 y 50 voluntarios"); break;
                case "ERROR_DATO_NUMERICO":
                    JOptionPane.showMessageDialog(vista, "El cupo debe ser un valor numérico"); break;
                case "ERROR_NOMBRE_DUPLICADO":
                    JOptionPane.showMessageDialog(vista, "Error: El nombre de la tarea ya se encuentra registrado. Por favor, sea más específico (Ej: 'Limpieza de Parque Guayaquil')"); break;
                default:
                    JOptionPane.showMessageDialog(vista, "Error en la operación: " + resultado); break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error crítico de base de datos: " + ex.getMessage());
        }
    }
}