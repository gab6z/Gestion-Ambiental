package controlador;

import dao.GestionAmbientalDAO;
import modelo.Iniciativa;
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
import java.sql.Date;
import javax.swing.JFileChooser;
import modelo.Sector;

/**
 * Controlador para el módulo de Iniciativa (Planificación Ambiental) del
 * sistema EcoVida. Conecta e interconecta los servicios de Sectores, Tareas y
 * Gestión Institucional para permitir una planificación integral, gobernada por
 * las reglas de negocio del sistema. * Gestiona el ciclo de vida completo de
 * una iniciativa (CRUD), la asignación múltiple de voluntarios participantes y
 * la auditoría visual mediante reportes PDF exportables.
 *
 * @author Solis Caballero Geovanny Andrés
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
     * Constructor original del sistema, inicializa UI y BD.
     *
     * @param panel El panel de la interfaz gráfica.
     */
    public IniciativaControlador(IniciativaPnl panel) {
        this.panel = panel;
        iniciarEventos();
        cargarCombos();
        cargarTabla();
    }

    /**
     * Constructor alternativo para pruebas unitarias. Omite la inicialización
     * de UI y BD cuando modoTest es true.
     *
     * @param panel El panel de la UI, puede ser null en contexto de testing.
     * @param modoTest Si es true, omite la inicialización de UI y BD.
     */
    public IniciativaControlador(IniciativaPnl panel, boolean modoTest) {
        this.panel = panel;
        if (!modoTest) {
            iniciarEventos();
            cargarCombos();
            cargarTabla();
        }
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
        panel.getBtnFiltrar().addActionListener(e -> aplicarFiltros());
        panel.getBtnLimpiarFiltro().addActionListener(e -> {
            panel.limpiarFiltros();
            panel.cargarDatosTabla(iniciativasActuales);
        });
        
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
            
            panel.cargarListaFiltroSectores(sectorService.listarSectores());

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
            validarCamposObligatorios(ini); 
            validarFechasYTiempos(ini);
            validarPresupuestoYLogistica(ini);
            validarEstadoYSector(ini); 
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
     * @param ini El objeto iniciativa con las fechas y tiempos provistos por
     * el formulario.
     * @throws IllegalArgumentException Si la fecha de fin es anterior a la de
     * ejecución, o si las horas se solapan de manera ilógica.
     */
    public void validarFechasYTiempos(Iniciativa ini) throws IllegalArgumentException {
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
     * @param ini El objeto iniciativa a ser auditado.
     * @throws IllegalArgumentException Si el presupuesto o la meta cuantitativa
     * son menores o iguales a cero, o si la descripción excede los 500
     * caracteres de persistencia.
     */
    public void validarPresupuestoYLogistica(Iniciativa ini) throws IllegalArgumentException {
        if (ini.getPresupuesto() <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser una cantidad mayor a 0.");
        }

        if (ini.getMeta() <= 0) {
            throw new IllegalArgumentException("Debe asignar una cantidad de participantes mayor a 0.");
        }
        
        if (ini.getMeta() >= 5000){
            throw new IllegalArgumentException("Debe asignar una cantidad de participantes menor a 5000.");
        }
        
        if (ini.getDescripcion() != null && ini.getDescripcion().length() > 500) {
            throw new IllegalArgumentException("La descripción logística excede el límite de 500 caracteres.");
        }
    }
    
    /**
     * Valida que los campos obligatorios del formulario no estén vacíos y que
     * los datos numéricos tengan el formato correcto.
     *
     * @param ini El objeto iniciativa mapeado desde el formulario.
     * @throws IllegalArgumentException Si algún campo obligatorio está vacío o
     * contiene un valor inválido.
     */
    public void validarCamposObligatorios(Iniciativa ini) throws IllegalArgumentException {

        if (ini.getTitulo() == null || ini.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la iniciativa es obligatorio.");
        }
        
        if (ini.getTitulo().trim().length() < 3) {
            throw new IllegalArgumentException("El título debe tener al menos 3 caracteres.");
        }
        
        if (ini.getTitulo().trim().length() > 50) {
            throw new IllegalArgumentException("El título no puede superar los 50 caracteres.");
        }

        if (ini.getIdSector() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un sector.");
        }

        if (ini.getIdTarea() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una tarea ambiental.");
        }

        if (ini.getIdGestion() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una entidad de gestión.");
        }

        if (ini.getFechaEjecucion() == null) {
            throw new IllegalArgumentException("La fecha de ejecución es obligatoria.");
        }

        if (ini.getIdSector() > 0) {
            List<Voluntario> seleccionados = panel.getVoluntariosSeleccionados();
            if (seleccionados == null || seleccionados.isEmpty()) {
                throw new IllegalArgumentException("Debe asignar al menos un voluntario.");
            }
        }
    }
    
    /**
     * Valida que no se altere el sector de una iniciativa si esta ya se
     * encuentra en estado "En ejecución".
     *
     * @param ini El objeto iniciativa con los datos del formulario.
     * @throws IllegalArgumentException Si se intenta modificar el sector en un
     * estado prohibido.
     */
    public void validarEstadoYSector(Iniciativa ini) throws IllegalArgumentException {
        if (ini.getIdIniciativa() > 0) {
            try {
                Iniciativa iniOriginal = iniciativaDAO.buscarPorId(ini.getIdIniciativa());

                if (iniOriginal != null) {
                    String estadoAct = iniOriginal.getEstado();

                    if ("En ejecución".equalsIgnoreCase(estadoAct) || "En Curso".equalsIgnoreCase(estadoAct)) {
                        if (iniOriginal.getIdSector() != ini.getIdSector()) {
                            throw new IllegalArgumentException("No se puede modificar el sector de una iniciativa que ya se encuentra en ejecución.");
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException("Error de sistema al validar el estado: " + e.getMessage());
            }
        }
    }
    
    /**
     * Aplica los filtros seleccionados (rango de fechas, sectores y estado)
     * sobre la lista local de iniciativas y actualiza la tabla con los
     * resultados. El filtrado es local (sin nueva consulta a BD) para
     * garantizar rapidez de respuesta.
     */
    private void aplicarFiltros() {
        if (iniciativasActuales == null || iniciativasActuales.isEmpty()) {
            panel.mostrarError("No hay datos para filtrar.");
            return;
        }

        Date desde = panel.getFiltroDesde();
        Date hasta = panel.getFiltroHasta();
        List<Sector> sectoresSel = panel.getSectoresFiltroSeleccionados();
        String estado = panel.getFiltroEstado();

        if (desde != null && hasta != null && desde.after(hasta)) {
            panel.mostrarError("La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.");
            return;
        }

        List<Iniciativa> resultado = iniciativasActuales.stream()
                .filter(ini -> filtrarPorFecha(ini, desde, hasta))
                .filter(ini -> filtrarPorSector(ini, sectoresSel))
                .filter(ini -> filtrarPorEstado(ini, estado))
                .toList();

        if (resultado.isEmpty()) {
            panel.mostrarError("No se encontraron iniciativas con los filtros aplicados.");
            panel.cargarDatosTabla(resultado);
        } else {
            panel.cargarDatosTabla(resultado);
        }
    }

    /**
     * Evalúa si una iniciativa cae dentro del rango de fechas indicado. Si
     * ambas fechas son nulas, no aplica restricción.
     *
     * @param ini Iniciativa a evaluar.
     * @param desde Fecha mínima del rango (inclusive), puede ser null.
     * @param hasta Fecha máxima del rango (inclusive), puede ser null.
     * @return true si la iniciativa pasa el filtro de fecha.
     */
    private boolean filtrarPorFecha(Iniciativa ini, Date desde, Date hasta) {
        Date fechaEj = ini.getFechaEjecucion();

        if (fechaEj == null) {
            return false;
        }
        
        return (desde == null || !fechaEj.before(desde)) && (hasta == null || !fechaEj.after(hasta));
    }

    /**
     * Evalúa si una iniciativa pertenece a alguno de los sectores
     * seleccionados. Si la lista está vacía, no aplica restricción.
     *
     * @param ini Iniciativa a evaluar.
     * @param sectoresSel Lista de sectores seleccionados en el filtro.
     * @return true si la iniciativa pasa el filtro de sector.
     */
    private boolean filtrarPorSector(Iniciativa ini, List<Sector> sectoresSel) {
        if (sectoresSel == null || sectoresSel.isEmpty()) {
            return true;
        }
        return sectoresSel.stream()
                .anyMatch(s -> s.getIdSector() == ini.getIdSector());
    }

    /**
     * Evalúa si una iniciativa coincide con el estado seleccionado en el
     * filtro. Si el estado es "Todos", no aplica restricción.
     *
     * @param ini Iniciativa a evaluar.
     * @param estado Estado seleccionado en el combo de filtros.
     * @return true si la iniciativa pasa el filtro de estado.
     */
    private boolean filtrarPorEstado(Iniciativa ini, String estado) {
        if (estado == null || "Todos".equals(estado)) {
            return true;
        }
        return estado.equalsIgnoreCase(ini.getEstado());
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
