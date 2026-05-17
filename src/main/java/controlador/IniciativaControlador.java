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
 * Controlador para el módulo de Iniciativa (Planificación Ambiental) del
 * sistema EcoVida. Conecta e interconecta los servicios de Sectores, Tareas y
 * Gestión Institucional para permitir una planificación integral, gobernada por
 * las reglas de negocio del sistema. * Gestiona el ciclo de vida completo de
 * una iniciativa (CRUD), la asignación múltiple de voluntarios participantes y
 * la auditoría visual mediante reportes PDF exportables.
 *
 * * @author Solis Caballero Geovanny Andrés
 * @version 1.2
 */
public class IniciativaControlador {

    private final IniciativaService iniciativaService = new IniciativaService();
    private final SectorService sectorService = new SectorService();
    private final TareaService tareaService = new TareaService();
    private final IniciativaPnl panel;
    private final dao.IniciativaDAO iniciativaDAO = new dao.IniciativaDAO();
    
    private List<Iniciativa> iniciativasActuales;
    
    /**
     * Constructor del controlador que asocia la vista y arranca los componentes
     * de datos. Enlaza los escuchadores de eventos y rellena los componentes
     * interactivos de la UI.
     *
     * * @param panel El panel de la interfaz gráfica (UI) que se va a
     * controlar.
     */
    public IniciativaControlador(IniciativaPnl panel) {
        this.panel = panel;
        iniciarEventos();
        cargarCombos(); 
        cargarTabla();
    }
    
    /**
     * Inicializa y enlaza los escuchadores de eventos (ActionListeners y
     * MouseListeners) para los botones del formulario, cambios de visibilidad
     * del panel y clics en la tabla.
     */
    private void iniciarEventos() {
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

    /**
     * Consulta los catálogos en la base de datos a través de los servicios
     * correspondientes y los inyecta dentro de los ComboBoxes y listas de
     * selección de la interfaz gráfica.
     */
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
    
    /**
     * Sincroniza la tabla visual de la interfaz recuperando el listado completo
     * actualizado de iniciativas registrado en el servidor de base de datos.
     */
    private void cargarTabla() {
        try {
            iniciativasActuales = iniciativaService.listarIniciativas();
            panel.cargarDatosTabla(iniciativasActuales);
        } catch (SQLException e) {
            panel.mostrarError("Error al cargar planificaciones: " + e.getMessage());
        }
    }
    
    /**
     * Procesa la inserción o actualización de una iniciativa ambiental. Mapea
     * los datos del formulario, aplica las reglas estrictas de validación y, de
     * ser exitoso, procesa transaccionalmente la asignación de voluntarios
     * vinculados.
     */
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
    
    /**
     * Elimina de forma lógica o física una planificación de iniciativa del
     * sistema. Solicita una confirmación explícita al usuario advirtiendo la
     * eliminación en cascada de las participaciones de voluntarios que dependen
     * de ella.
     */
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
    
    /**
     * Aplica reglas de negocio temporales de la aplicación para evitar
     * inconsistencias cronológicas.
     *
     * * @param ini El objeto iniciativa con las fechas y tiempos provistos por
     * el formulario.
     * @throws IllegalArgumentException Si la fecha de fin es anterior a la de
     * ejecución, o si las horas se solapan de manera ilógica.
     */
    private void validarFechasYTiempos(Iniciativa ini) throws IllegalArgumentException {
        if (ini.getFechaFin() != null && ini.getFechaEjecucion() != null) {
            if (ini.getFechaFin().before(ini.getFechaEjecucion())) {
                throw new IllegalArgumentException("La fecha de finalización no puede ser anterior a la de ejecución.");
            }
        }

        if (ini.getHoraInicio() != null && ini.getHoraFin() != null) {
            if (!ini.getHoraInicio().before(ini.getHoraFin())) {
                throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
            }
        }
    }
    
    /**
     * Valida la consistencia financiera y los límites de almacenamiento de
     * datos logísticos. Protege al sistema contra desbordamientos de datos e
     * inconsistencias monetarias.
     *
     * * @param ini El objeto iniciativa a ser auditado.
     * @throws IllegalArgumentException Si el presupuesto o la meta cuantitativa
     * son menores o iguales a cero, o si la descripción excede los 500
     * caracteres de persistencia.
     */
    private void validarPresupuestoYLogistica(Iniciativa ini) throws IllegalArgumentException {
        if (ini.getPresupuesto() <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser una cantidad mayor a 0.");
        }

        if (ini.getMeta() <= 0) {
            throw new IllegalArgumentException("Debe asignar una meta cuantitativa válida.");
        }
        if (ini.getDescripcion() != null && ini.getDescripcion().length() > 500) {
            throw new IllegalArgumentException("La descripción logística excede el límite de 500 caracteres.");
        }
    }
    
    /**
     * Genera y exporta dinámicamente un documento formal en formato PDF (A4 en
     * orientación apaisada) conteniendo los registros completos almacenados en
     * la tabla de iniciativas ambientales. Utiliza un JFileChooser para
     * permitir el almacenamiento interactivo local del reporte.
     */
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

            Document documento = new Document(PageSize.A4.rotate());
            try {
                PdfWriter.getInstance(documento, new FileOutputStream(ruta));
                documento.open();

                Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(23, 93, 62));
                Paragraph titulo = new Paragraph("Reporte de Iniciativas Ambientales - EcoVida\n\n", fuenteTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);
                documento.add(titulo);

                PdfPTable tablaPDF = new PdfPTable(6);
                tablaPDF.setWidthPercentage(100);

                String[] encabezados = {"Título", "Sector", "Tarea", "Entidad", "Fecha", "Estado"};
                for (String h : encabezados) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE)));
                    cell.setBackgroundColor(new BaseColor(34, 115, 78));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    tablaPDF.addCell(cell);
                }

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
