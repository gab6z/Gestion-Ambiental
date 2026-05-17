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
 * @version 1.0
 * @since 2026-05-06
 */

public class TareaControlador implements ActionListener, KeyListener {
    private Tarea modelo;
    private TareaService service;
    private TareasPnl vista;

    /**
     * Constructor que inicializa los componentes y los escuchadores de eventos.
     * @param modelo Instancia del modelo Tarea.
     * @param service Capa de servicios para persistencia de datos.
     * @param vista Panel de interfaz de usuario.
     */
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

    /**
     * Inicializa la vista cargando la tabla de datos.
     */
    public void iniciar() { listar(); }

    /**
     * Recupera todas las tareas del servicio y actualiza la tabla en la vista.
     */
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
            vista.modelo.addRow(new Object[]{t.getIdTarea(), t.getNombreTarea(), t.getDificultadTecnica(), t.getEstadoTarea()});
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
     * Procesa el guardado/actualización de tareas.
     * Evaluado mediante Complejidad Ciclomática (Análisis Estático).
     * @return Código de estado String para validación en pruebas unitarias.
     * @throws Exception Si ocurre un error de conexión con la base de datos.
     */
    public String Guardado() throws Exception {
        String nombre = vista.txtNombre.getText().trim();
        String herramientas = vista.txtHerramientas.getText().trim();
        String cupoStr = vista.txtCupo.getText().trim();

        if (nombre.isEmpty() || herramientas.isEmpty() || cupoStr.isEmpty()) {
            return "ERROR_CAMPOS";
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


    /**
     * PARA V y V: Determina si una tarea es apta para eliminación.
     * Determina si una tarea es apta para eliminación.
     * Evaluado mediante Análisis DU-Chain (Definición-Uso).
     * @param id ID de la tarea a verificar.
     * @param estadoActual Estado actual de la tarea.
     * @return boolean true si se permite la eliminación, false de lo contrario.
     */
    public boolean validarEstadoEliminacion(int id, String estadoActual) {
        boolean esEliminable = false; 

        if (id > 0) { 
            // Bloquea la eliminación si está "En curso" OR si ya está "Inactiva"
            if (!estadoActual.equals("En curso") && !estadoActual.equals("Inactiva")) { 
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

    /**
     * Genera un reporte PDF con la lista de tareas actualmente visibles en la tabla.
     */
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

    /**
     * Limpia los campos de texto y resetea las selecciones de la interfaz.
     */
    public void limpiarFormulario() {
        vista.txtId.setText(""); vista.txtNombre.setText(""); vista.txtHerramientas.setText("");
        vista.txtCupo.setText(""); vista.txtDescripcion.setText(""); vista.txtBuscar.setText("");
        vista.cbxDificultad.setSelectedIndex(0); vista.cbxEstado.setSelectedIndex(0);
        vista.tablaTareas.clearSelection(); listar();
    }
    
    @Override public void keyReleased(KeyEvent e) { if (e.getSource() == vista.txtBuscar) Filtro(); }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnLimpiar) {
            limpiarFormulario();
        }

        if (e.getSource() == vista.btnGuardar || e.getSource() == vista.btnActualizar) {
            try {
                String resultado = Guardado(); 
                
                switch (resultado) {
                    case "EXITO_REGISTRO":
                        JOptionPane.showMessageDialog(vista, "Tarea registrada exitosamente");
                        listar(); limpiarFormulario(); break;
                    case "EXITO_ACTUALIZACION":
                        JOptionPane.showMessageDialog(vista, "Tarea actualizada exitosamente");
                        listar(); limpiarFormulario(); break;
                    case "ERROR_CAMPOS":
                        JOptionPane.showMessageDialog(vista, "Por favor, llene los campos obligatorios"); break;
                    case "ERROR_FORMATO_TEXTO":
                        JOptionPane.showMessageDialog(vista, "Formato incorrecto: Use solo letras en Nombre/Herramientas"); break;
                    case "ERROR_RANGO_CUPO":
                        JOptionPane.showMessageDialog(vista, "El cupo debe estar entre 1 y 50 voluntarios"); break;
                    case "ERROR_DATO_NUMERICO":
                        JOptionPane.showMessageDialog(vista, "El cupo debe ser un valor numérico"); break;
                    default:
                        JOptionPane.showMessageDialog(vista, "Error en la operación: " + resultado); break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error crítico de base de datos: " + ex.getMessage());
            }
        }

        if (e.getSource() == vista.btnEliminar) {
            int fila = vista.tablaTareas.getSelectedRow(); // Captura la fila seleccionada
            if (fila == -1) {
                JOptionPane.showMessageDialog(vista, "Seleccione una tarea de la tabla");
                return;
            }
            
            // Extrae el ID y el Estado directamente desde las columnas de la tabla de la vista
            int id = Integer.parseInt(vista.tablaTareas.getValueAt(fila, 0).toString());
            String estado = vista.tablaTareas.getValueAt(fila, 3).toString(); // Ajustar el índice de la columna si es necesario
            
            if (validarEstadoEliminacion(id, estado)) {
                if (JOptionPane.showConfirmDialog(vista, "¿Seguro que desea dar de baja esta tarea?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    try {
                        service.darDeBaja(id);
                        JOptionPane.showMessageDialog(vista, "Tarea inactiva exitosamente");
                        listar(); limpiarFormulario();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(vista, "Error al eliminar: " + ex.getMessage());
                    }
                }
            } else {
                // Compara ignorando mayúsculas/minúsculas para evitar fallos de formato en cadenas
                if (estado.equalsIgnoreCase("Inactiva")) {
                    JOptionPane.showMessageDialog(vista, "No se puede eliminar porque esta tarea ya se encuentra 'Inactiva'", "Advertencia", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(vista, "No se puede eliminar una tarea que ya está 'En curso'", "Error", JOptionPane.ERROR_MESSAGE);
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
}
