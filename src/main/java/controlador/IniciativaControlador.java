package controlador;

import dao.GestionAmbientalDAO;
import modelo.Iniciativa;
import modelo.Sector;
import modelo.Tarea;
import vista.IniciativaPnl;
import service.IniciativaService;
import service.SectorService;
import service.TareaService;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.Voluntario;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;

/**
 * Descripción: Controlador para el módulo de Iniciativa (Planificación).
 * Conecta los servicios de Sectores y Tareas para permitir la planificación integral.
 * 
 * @author Solis Geovanny
 * @version 1.1
 */
public class IniciativaControlador {

    private final IniciativaService iniciativaService = new IniciativaService();
    private final SectorService sectorService = new SectorService();
    private final TareaService tareaService = new TareaService();
    private final IniciativaPnl panel;
    private final dao.IniciativaDAO iniciativaDAO = new dao.IniciativaDAO();
    
    private List<Iniciativa> iniciativasActuales;

    public IniciativaControlador(IniciativaPnl panel) {
        this.panel = panel;
        iniciarEventos();
        cargarCombos(); 
        cargarTabla();
    }
    
    private void iniciarEventos() {
        // CRUD: Create / Update
        panel.getBtnGuardar().addActionListener(e -> guardar());
        panel.getBtnActualizar().addActionListener(e -> guardar());
        panel.getBtnLimpiar().addActionListener(e -> panel.limpiarFormulario());
        panel.getBtnEliminar().addActionListener(e -> eliminar());
        panel.getBtnPDF().addActionListener(e -> generarReportePDF());
        
        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                cargarCombos(); 
                cargarTabla(); 
            }
        });

        panel.getTablaIniciativas().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = panel.getTablaIniciativas().getSelectedRow();
                if (fila >= 0) {
                    int filaModelo = panel.getTablaIniciativas()
                            .convertRowIndexToModel(fila);
                    Iniciativa ini = iniciativasActuales.get(filaModelo);
                    panel.cargarIniciativaEnFormulario(ini);

                    try {
                        List<Integer> idsVol = iniciativaDAO.obtenerIdsVoluntarios(ini.getIdIniciativa());
                        panel.seleccionarVoluntariosPorIds(idsVol);
                    } catch (SQLException ex) {
                        panel.mostrarError("No se pudieron cargar los voluntarios: " + ex.getMessage());
                    }
                }
            }
        });
    }

    private void cargarCombos() {
        try {
            panel.cargarComboSectores(sectorService.listarSectores());
            panel.cargarComboTareas(tareaService.listar());

            GestionAmbientalDAO gestionDAO = new GestionAmbientalDAO();
            panel.cargarComboGestion(gestionDAO.listar());

            dao.VoluntarioDAO voluntarioDAO = new dao.VoluntarioDAO();
            panel.cargarListaVoluntarios(voluntarioDAO.listar());

        } catch (SQLException e) {
            panel.mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }

    private void cargarTabla() {
        try {
            iniciativasActuales = iniciativaService.listarIniciativas();
            panel.cargarDatosTabla(iniciativasActuales);
        } catch (SQLException e) {
            panel.mostrarError("Error al cargar planificaciones: " + e.getMessage());
        }
    }

    private void guardar() {
        try {
            Iniciativa ini = panel.getIniciativaDelFormulario();
            validarFechasYTiempos(ini);
            validarPresupuestoYLogistica(ini);
            int idReal;
            if (ini.getIdIniciativa() == 0) {
                idReal = iniciativaDAO.insertar(ini);
            } else {
                iniciativaDAO.actualizar(ini);
                idReal = ini.getIdIniciativa();
                iniciativaDAO.eliminarParticipaciones(idReal);
            }

            List<Voluntario> seleccionados = panel.getVoluntariosSeleccionados();
            if (!seleccionados.isEmpty() && idReal > 0) {
                List<Integer> ids = seleccionados.stream()
                        .map(Voluntario::getId_voluntario)
                        .toList();
                iniciativaDAO.asignarVoluntarios(idReal, ids);
            }

            panel.mostrarMensaje("Registrado con éxito.");
            panel.limpiarFormulario();
            cargarTabla();
        } catch (IllegalArgumentException e) {

            panel.mostrarError(e.getMessage());

        } catch (Exception e) {
            panel.mostrarError("Error: " + e.getMessage());
        }
    }

    private void eliminar() {
        try {
            Iniciativa ini = panel.getIniciativaDelFormulario();
            
            if (ini.getIdIniciativa() == 0) {
                panel.mostrarError("Debe seleccionar una iniciativa de la tabla para eliminar.");
                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(panel, 
                    "¿Está seguro de eliminar la planificación: '" + ini.getTitulo() + "'?\n" +
                    "Esto borrará también las participaciones asociadas.", 
                    "Confirmar Eliminación", 
                    JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                iniciativaService.eliminarIniciativa(ini.getIdIniciativa());
                panel.mostrarMensaje("Iniciativa eliminada correctamente.");
                panel.limpiarFormulario();
                cargarTabla();
            }

        } catch (IllegalArgumentException | SQLException e) {
            panel.mostrarError("No se pudo eliminar:\n" + e.getMessage());
        } 
       
    }

    // Implementación basada en image_54aaa3.png
    private void validarFechasYTiempos(Iniciativa ini) throws IllegalArgumentException {
        // Validar que fechaFin no sea menor a fechaEjecucion
        if (ini.getFechaFin() != null && ini.getFechaEjecucion() != null) {
            if (ini.getFechaFin().before(ini.getFechaEjecucion())) {
                throw new IllegalArgumentException("La fecha de finalización no puede ser anterior a la de ejecución.");
            }
        }

        // Validar que horaInicio sea lógica respecto a horaFin
        if (ini.getHoraInicio() != null && ini.getHoraFin() != null) {
            if (!ini.getHoraInicio().before(ini.getHoraFin())) {
                throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
            }
        }
    }

    private void validarPresupuestoYLogistica(Iniciativa ini) throws IllegalArgumentException {
        // Presupuesto mayor a 0
        if (ini.getPresupuesto() <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser una cantidad mayor a 0.");
        }

        // Meta cuantitativa asignada (asumiendo que debe ser mayor a 0)
        if (ini.getMeta() <= 0) {
            throw new IllegalArgumentException("Debe asignar una meta cuantitativa válida.");
        }

        // Límite de caracteres en descripción (ejemplo: 500 según tu DB)
        if (ini.getDescripcion() != null && ini.getDescripcion().length() > 500) {
            throw new IllegalArgumentException("La descripción logística excede el límite de 500 caracteres.");
        }
    }

    private void generarReportePDF() {
        if (iniciativasActuales == null || iniciativasActuales.isEmpty()) {
            panel.mostrarError("No hay datos para exportar.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Reporte PDF");
        if (chooser.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().getAbsolutePath();
            if (!ruta.endsWith(".pdf")) {
                ruta += ".pdf";
            }

            Document documento = new Document(PageSize.A4.rotate()); // Horizontal para que quepa la tabla
            try {
                PdfWriter.getInstance(documento, new FileOutputStream(ruta));
                documento.open();

                // Título del Reporte
                Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(23, 93, 62));
                Paragraph titulo = new Paragraph("Reporte de Iniciativas Ambientales - EcoVida\n\n", fuenteTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);
                documento.add(titulo);

                // Crear Tabla en el PDF (6 columnas para que coincida con tu vista)
                PdfPTable tablaPDF = new PdfPTable(6);
                tablaPDF.setWidthPercentage(100);

                // Encabezados
                String[] encabezados = {"Título", "Sector", "Tarea", "Entidad", "Fecha", "Estado"};
                for (String h : encabezados) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE)));
                    cell.setBackgroundColor(new BaseColor(34, 115, 78));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    tablaPDF.addCell(cell);
                }

                // Llenar datos desde la lista del controlador
                for (Iniciativa ini : iniciativasActuales) {
                    tablaPDF.addCell(ini.getTitulo());
                    tablaPDF.addCell(ini.getNombreSector());
                    tablaPDF.addCell(ini.getNombreTarea());
                    tablaPDF.addCell(ini.getNombreGestion());
                    tablaPDF.addCell(ini.getFechaEjecucion() != null ? ini.getFechaEjecucion().toString() : "N/A");
                    tablaPDF.addCell(ini.getEstado());
                }

                documento.add(tablaPDF);
                panel.mostrarMensaje("Reporte generado exitosamente en: " + ruta);

            } catch (Exception ex) {
                panel.mostrarError("Error al generar PDF: " + ex.getMessage());
            } finally {
                documento.close();
            }
        }
    }
}
