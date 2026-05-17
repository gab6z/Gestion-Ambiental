/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 * Descripción: Controlador principal del módulo de Sectores.
 * Es el "director de orquesta" que escucha los eventos de la interfaz gráfica (Vista)
 * y delega las operaciones de guardado, eliminación y búsqueda a la capa de Servicio.
 * Además, gestiona la generación de reportes en PDF.
 * Proyecto: Sistema de Gestión Ambiental (EcoVida)
 * 
 * @author Gabriela Solange Gonzalez Roman
 * @version 1.0
 * @since 2026-05-05
 */

import modelo.Sector;
import vista.SectoresPnl;
import service.SectorService;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.JFileChooser;
import java.io.FileOutputStream;

public class SectorControlador {

    private final SectorService sectorService = new SectorService();
    private final SectoresPnl panel;
    private List<Sector> sectoresActuales;

    
/**
 * Crea una nueva instancia del controlador de sectores.
 *
 * @param panel panel de la vista {@link SectoresPnl} asociado al controlador.
 */
    public SectorControlador(SectoresPnl panel) {
        this.panel = panel;
        iniciarEventos();
        cargarTabla();
    }

/**
 * Inicializa y registra todos los eventos de la interfaz gráfica.
 */
    private void iniciarEventos() {
        panel.getBtnGuardar().addActionListener(e -> guardar());
        panel.getBtnActualizar().addActionListener(e -> guardar());
        panel.getBtnLimpiar().addActionListener(e -> panel.limpiarFormulario());
        panel.getBtnEliminar().addActionListener(e -> eliminar());
        panel.getBtnBuscar().addActionListener(e -> buscarConFiltros());
        panel.getBtnExportarPDF().addActionListener(e -> exportarAPDF());

        panel.getTablaSectores().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaVista = panel.getTablaSectores().getSelectedRow();
                if (filaVista >= 0) {
                    int filaModelo = panel.getTablaSectores().convertRowIndexToModel(filaVista);
                    Sector sectorSeleccionado = sectoresActuales.get(filaModelo);
                    panel.cargarSectorEnFormulario(sectorSeleccionado);
                }
            }
        });
    }

/**
 * Carga todos los sectores registrados en la base de datos
 * y los muestra en la tabla de la interfaz.
 *
 * @throws SQLException implícitamente manejada si ocurre un error
 * durante la consulta de datos.
 */
    private void cargarTabla() {
        try {
            sectoresActuales = sectorService.listarSectores();
            panel.cargarDatosTabla(sectoresActuales);
        } catch (SQLException e) {
            panel.mostrarError("Error al cargar los sectores: " + e.getMessage());
        }
    }


/**
 * Valida los datos ingresados en el formulario de sectores.
 *
 * @param nombre nombre de la zona o sector.
 * @param provincia provincia o ciudad del sector.
 * @param riesgo nivel de riesgo seleccionado.
 * @param estado estado actual de la zona.
 * @return una cadena con los mensajes de error encontrados, si no existen errores retorna una cadena vacía.
 */
    public String validarDatosSector(String nombre, String provincia, String riesgo, String estado) {
        String mensajeError = ""; 

        if (nombre == null || nombre.trim().isEmpty()) {
            
            mensajeError += "- El nombre de la zona es obligatorio.\n";
            
        } else if (nombre.length() > 150) {
            
            mensajeError += "- El nombre excede los 150 caracteres permitidos.\n";
        }

        if (provincia == null || provincia.trim().isEmpty()) {
            
            mensajeError += "- La provincia/ciudad es obligatoria.\n";
        }

        if (riesgo == null || riesgo.equals("Seleccionar...")) {
            
            mensajeError += "- Debe seleccionar un nivel de riesgo.\n";
        }

        if (estado == null || estado.equals("Seleccionar...")) {
            
            mensajeError += "- Debe seleccionar el estado de la zona.\n";
        }

        return mensajeError;
    }

/**
 * Valida los filtros ingresados para realizar la búsqueda de sectores.
 *
 * @param textoBusqueda texto de búsqueda ingresado por el usuario.
 * @param riesgo filtro de nivel de riesgo seleccionado.
 * @param estado filtro de estado seleccionado.
 * @return {@code true} si los filtros son válidos;
 *         {@code false} si no se ingresó ningún criterio válido.
 */
public boolean validarFiltrosBusqueda(String textoBusqueda, String riesgo, String estado) {
        
        if (textoBusqueda != null) {
            if (textoBusqueda.length() > 50) {
                return false; 
            }
            if (!textoBusqueda.isBlank()) {
                return true; 
            }
        }

        if (!"Seleccionar...".equals(riesgo)) {
            return true;
        }

        if (!"Seleccionar...".equals(estado)) {
            return true;
        }

        return false;
    }

    
/**
 * Guarda un sector en la base de datos.
 * <p>
 * Si el sector ya existe, actualiza su información;
 * de lo contrario, registra un nuevo sector.
 * También valida los datos antes de realizar la operación.
 * </p>
 */
    private void guardar() {
        try {
            Sector sector = panel.getSectorDelFormulario();
            
            String errores = validarDatosSector(
                sector.getNombreZona(), 
                sector.getProvinciaCiudad(), 
                sector.getNivelRiesgo(), 
                sector.getEstadoZona()
            );

            if (!errores.isEmpty()) {
                panel.mostrarError("Corrija los siguientes errores:\n" + errores);
                return; 
            }

            sectorService.guardarSector(sector);
            panel.mostrarMensaje("Sector guardado correctamente.");
            panel.limpiarFormulario();
            cargarTabla();

        } catch (SQLException e) {
            panel.mostrarError("Error crítico al guardar en la base de datos:\n" + e.getMessage());
        } catch (IllegalArgumentException e) {
            panel.mostrarError(e.getMessage());
        }
    }
    
/**
 * Realiza una búsqueda de sectores aplicando filtros
 * de texto, riesgo y estado.

 */
    private void buscarConFiltros() {
        try {
            String texto = panel.getTxtBusqueda().getText();
            String riesgo = panel.getCmbFiltroRiesgo().getSelectedItem().toString();
            String estado = panel.getCmbFiltroEstado().getSelectedItem().toString();

            if (!validarFiltrosBusqueda(texto, riesgo, estado)) {
                panel.mostrarError("Debe ingresar al menos un parámetro válido para buscar.");
                return;
            }

            sectoresActuales = sectorService.listarFiltrados(texto, riesgo, estado);
            
            if (sectoresActuales.isEmpty()) {
                panel.cargarDatosTabla(sectoresActuales); 
                panel.mostrarMensaje("No se encontraron sectores bajo estos parámetros.");
            } else {
                panel.cargarDatosTabla(sectoresActuales);
            }
        } catch (SQLException e) {
            panel.mostrarError("Error en la consulta: " + e.getMessage());
        }
    }

    
/**
 * Elimina el sector seleccionado en la tabla.
 * <p>
 * Solicita confirmación al usuario antes de realizar la eliminación.
 * </p>
 */

    private void eliminar() {
        try {
            Sector sector = panel.getSectorDelFormulario();
            if (sector.getIdSector() == 0) {
                panel.mostrarError("Seleccione un sector de la tabla para eliminar.");
                return;
            }

            int confirmacion = javax.swing.JOptionPane.showConfirmDialog(panel, 
                    "¿Está seguro de eliminar el sector '" + sector.getNombreZona() + "'?", 
                    "Confirmar Eliminación", 
                    javax.swing.JOptionPane.YES_NO_OPTION);

            if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
                sectorService.eliminarSector(sector.getIdSector());
                panel.mostrarMensaje("Sector eliminado correctamente.");
                panel.limpiarFormulario();
                cargarTabla();
            }

        } catch (IllegalArgumentException e) {
            panel.mostrarError(e.getMessage());
        } catch (SQLException e) {
            panel.mostrarError("No se puede eliminar: " + e.getMessage());
        }
    }
    
    
  /**
 * Genera un reporte en formato PDF con la información
 * de los sectores mostrados actualmente en la tabla.
 * <p>
 * El reporte incluye los filtros aplicados y los datos
 * principales de cada sector.
 * </p>
 */
    private void exportarAPDF() {
        if (sectoresActuales == null || sectoresActuales.isEmpty()) {
            panel.mostrarError("No hay datos en la tabla para exportar.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        fileChooser.setSelectedFile(new java.io.File("Reporte_Sectores_EcoVida.pdf"));

        int userSelection = fileChooser.showSaveDialog(panel);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File archivoGuardar = fileChooser.getSelectedFile();
            String rutaDestino = archivoGuardar.getAbsolutePath();
            
            if (!rutaDestino.toLowerCase().endsWith(".pdf")) {
                rutaDestino += ".pdf";
            }

            Document documento = new Document() {};
            try {
                PdfWriter.getInstance(documento, new FileOutputStream(rutaDestino));
                documento.open();

                com.itextpdf.text.Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                Paragraph titulo = new Paragraph("Reporte de Sectores - EcoVida", fuenteTitulo);
                titulo.setAlignment(Paragraph.ALIGN_CENTER);
                titulo.setSpacingAfter(20);
                documento.add(titulo);

                String filtroRiesgo = panel.getCmbFiltroRiesgo().getSelectedItem().toString();
                String filtroEstado = panel.getCmbFiltroEstado().getSelectedItem().toString();
                Paragraph filtros = new Paragraph("Filtros aplicados = Riesgo: " + filtroRiesgo + " | Estado: " + filtroEstado);
                filtros.setSpacingAfter(15);
                documento.add(filtros);

                PdfPTable tablaPDF = new PdfPTable(4);
                tablaPDF.setWidthPercentage(100);
                tablaPDF.setWidths(new float[]{1f, 1.5f, 1f, 1f}); 

                tablaPDF.addCell(new Paragraph("Zona", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                tablaPDF.addCell(new Paragraph("Provincia / Ciudad", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                tablaPDF.addCell(new Paragraph("Riesgo", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                tablaPDF.addCell(new Paragraph("Estado", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));

                for (Sector s : sectoresActuales) {
                    tablaPDF.addCell(s.getNombreZona());
                    tablaPDF.addCell(s.getProvinciaCiudad());
                    tablaPDF.addCell(s.getNivelRiesgo());
                    tablaPDF.addCell(s.getEstadoZona());
                }

                documento.add(tablaPDF);
                documento.close();

                panel.mostrarMensaje("¡Reporte PDF generado y guardado con éxito!");

            } catch (DocumentException | java.io.FileNotFoundException e) {
                panel.mostrarError("Error al generar el documento PDF: " + e.getMessage());
            }
        }
    }
}
