package controlador;

import modelo.Voluntario;
import vista.VoluntariosPnl;
import service.VoluntarioService;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JOptionPane;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
/**
 * Controlador principal para la gestión de la entidad Voluntario.
 * Actúa como intermediario entre la interfaz gráfica {@link VoluntariosPnl} 
 * y la lógica de negocio {@link VoluntarioService}.
 * * Maneja eventos de usuario, persistencia de datos y generación de reportes PDF.
 * * @author EDUARDO
 * @version 1.1
 * @since 2026-05-07
 */
public class VoluntarioControlador {
    private final VoluntarioService voluntarioService = new VoluntarioService();

    private final VoluntariosPnl panel;
    private List<Voluntario> voluntariosActuales;

    /**
     * Inicializa el controlador vinculándolo a un panel de voluntarios.
     * Al instanciarse, configura los eventos de los botones y carga los datos iniciales.
     * * @param panel Instancia del panel de vista de voluntarios.
     */
    public VoluntarioControlador(VoluntariosPnl panel) {
        this.panel = panel;
        iniciarEventos();
        cargarTabla();
    }
    /**
        * Configura los ActionListeners de los botones y los eventos del ratón para la tabla.
        * Vincula las acciones de guardar, actualizar, eliminar, limpiar y exportar.
     */
    private void iniciarEventos() {
        panel.getBtnGuardar().addActionListener(e -> guardarNuevo());
        panel.getBtnActualizar().addActionListener(e -> actualizarExistente());
        panel.getBtnEliminar().addActionListener(e -> eliminar());
        panel.getBtnLimpiar().addActionListener(e -> panel.limpiarFormulario());
        panel.getBtnBuscar().addActionListener(e -> cargarTabla());
        panel.getBtnExportarPDF().addActionListener(e -> generarPDF()); // <-- Integrado

        panel.getTablaVoluntarios().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = panel.getTablaVoluntarios().getSelectedRow();
                if (fila >= 0) {
                    int filaModelo = panel.getTablaVoluntarios().convertRowIndexToModel(fila);
                    Voluntario seleccionado = voluntariosActuales.get(filaModelo);
                    panel.cargarVoluntarioEnFormulario(seleccionado);
                }
            }
        });
    }

    /**
     * Carga y filtra los datos de los voluntarios en la tabla de la interfaz.
     * Si el campo de búsqueda está vacío, lista todos los registros.
     * Permite filtrar por Cédula o Nombre según la selección del usuario.
     */
    private void cargarTabla() {
        try {
            String texto = panel.getTxtBusqueda().getText().trim();
            String filtro = String.valueOf(panel.getCmbFiltroBusqueda().getSelectedItem());

            if (texto.isEmpty()) {
                voluntariosActuales = voluntarioService.listarVoluntarios();
            } else {
                if (filtro.equals("Cédula")) {
                    voluntariosActuales = new java.util.ArrayList<>();
                    Voluntario v = voluntarioService.buscarPorCedula(texto);
                    if (v != null) voluntariosActuales.add(v);
                } else {
                    voluntariosActuales = voluntarioService.buscarPorNombre(texto);
                }
            }
            panel.cargarDatosTabla(voluntariosActuales);
        } catch (Exception e) {
            panel.mostrarError("Error al cargar: " + e.getMessage());
        }
    }
    /**
     * Obtiene los datos del formulario y registra un nuevo voluntario con estado "Activo".
     */
    private void guardarNuevo() {
        try {
            Voluntario nuevo = panel.getVoluntarioDelFormulario();
            nuevo.setEstado("Activo");
            voluntarioService.guardarVoluntario(nuevo);
            panel.mostrarMensaje("¡Voluntario registrado!");
            actualizarVista();
        } catch (Exception e) {
            panel.mostrarError("Error al guardar: " + e.getMessage());
        }
    }
    /**
     * Procesa la actualización de los datos de un voluntario existente.
     */
    private void actualizarExistente() {
        try {
            Voluntario editado = panel.getVoluntarioDelFormulario();
            voluntarioService.guardarVoluntario(editado);
            panel.mostrarMensaje("Datos actualizados.");
            actualizarVista();
        } catch (Exception e) {
            panel.mostrarError("Error al actualizar: " + e.getMessage());
        }
    }
    /**
     * Solicita confirmación al usuario y procede a dar de baja (eliminar lógicamente) 
     * al voluntario seleccionado en el formulario.
     */
    private void eliminar() {
        int fila = panel.getTablaVoluntarios().getSelectedRow();
    
        if (fila < 0) {
            panel.mostrarError("Por favor, selecciona un voluntario de la tabla primero.");
            return;
        }
        int filaModelo = panel.getTablaVoluntarios().convertRowIndexToModel(fila);
        Voluntario seleccionado = voluntariosActuales.get(filaModelo);

        int confirmacion = JOptionPane.showConfirmDialog(panel, 
            "¿Está seguro de dar de baja a: " + seleccionado.getNombres_completos() + "?", 
            "Confirmar Baja", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                // Usamos el ID que ya sabemos que es correcto
                voluntarioService.eliminarVoluntario(seleccionado.getId_voluntario());
                panel.mostrarMensaje("Voluntario desactivado con éxito.");
                actualizarVista();
            } catch (Exception e) {
                panel.mostrarError("Error al desactivar: " + e.getMessage());
            }
        }
    }

    /**
     * Genera un reporte profesional en PDF permitiendo al usuario elegir la ubicación.
     * Incluye validación de datos, formato de tablas y encabezados estilizados.
     */
    private void generarPDF() {
        if (voluntariosActuales == null || voluntariosActuales.isEmpty()) {
            panel.mostrarError("No hay datos en la tabla para exportar.");
            return;
        }
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Voluntarios");
        fileChooser.setSelectedFile(new java.io.File("Reporte_Voluntarios_EcoVida.pdf"));

        int userSelection = fileChooser.showSaveDialog(panel);

        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File archivoGuardar = fileChooser.getSelectedFile();
            String rutaDestino = archivoGuardar.getAbsolutePath();
            if (!rutaDestino.toLowerCase().endsWith(".pdf")) {
                rutaDestino += ".pdf";
            }

            Document documento = new Document();
            try {
                PdfWriter.getInstance(documento, new FileOutputStream(rutaDestino));
                documento.open();

                com.itextpdf.text.Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                Paragraph titulo = new Paragraph("Reporte General de Voluntarios - EcoVida", fuenteTitulo);
                titulo.setAlignment(Paragraph.ALIGN_CENTER);
                titulo.setSpacingAfter(20);
                documento.add(titulo);

                String busqueda = panel.getTxtBusqueda().getText().trim();
                String infoFiltro = busqueda.isEmpty() ? "Todos los registros" : "Filtro: " + busqueda;
                Paragraph subTitulo = new Paragraph("Listado generado basado en: " + infoFiltro);
                subTitulo.setSpacingAfter(15);
                documento.add(subTitulo);

                PdfPTable tablaPDF = new PdfPTable(7);
                tablaPDF.setWidthPercentage(100);
                tablaPDF.setWidths(new float[]{0.7f, 2f, 1.2f, 1f, 2f, 1.5f, 1f}); 

                com.itextpdf.text.Font fuenteHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
                
                String[] encabezados = {"ID", "Nombres", "Cédula", "Género", "Correo", "Disponibilidad", "Estado"};
                
                for (String h : encabezados) {
                    PdfPCell cell = new PdfPCell(new Paragraph(h, fuenteHeader));
                    cell.setBackgroundColor(new BaseColor(23, 93, 62)); // Verde EcoVida
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    tablaPDF.addCell(cell);
                }
                for (Voluntario v : voluntariosActuales) {
                    tablaPDF.addCell(String.valueOf(v.getId_voluntario()));
                    tablaPDF.addCell(v.getNombres_completos());
                    tablaPDF.addCell(v.getCedula());
                    tablaPDF.addCell(v.getGenero());
                    tablaPDF.addCell(v.getCorreo());
                    tablaPDF.addCell(v.getDisponibilidad_dias());
                    tablaPDF.addCell(v.getEstado());
                }

                documento.add(tablaPDF);
                documento.close();

                panel.mostrarMensaje("¡Reporte generado con éxito en:\n" + rutaDestino);

            } catch (DocumentException | java.io.FileNotFoundException e) {
                panel.mostrarError("Error técnico al generar PDF: " + e.getMessage());
            } catch (Exception e) {
                panel.mostrarError("Error inesperado: " + e.getMessage());
            }
        }
    }
    private void actualizarVista() {
        panel.limpiarFormulario();
        cargarTabla();
    }
}