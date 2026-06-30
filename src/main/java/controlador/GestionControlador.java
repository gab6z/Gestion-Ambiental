package controlador;

import modelo.Gestion;
import service.GestionService;
import vista.GestionAmbiental_panel;

import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador del módulo de Gestión Ambiental.
 * Proyecto: Sistema de Gestión de Iniciativas de Preservación Ambiental (SGIPA) - EcoVida
 * Permite el flujo de datos entre la interfaz gráfica y los servicios de persistencia,
 * aplicando las reglas de negocio establecidas para el catálogo de convenios ecológicos.
 * * @author Dominica Lilibeth Torres Bohorquez
 * @version 1.2
 */
public class GestionControlador implements ActionListener {

    private final Gestion modelo;
    private final GestionService gestionService = new GestionService(); 
    private final GestionAmbiental_panel vista;
    private List<Gestion> listaGlobal;
    private int idFilaSeleccionada = -1;

    /**
     * Inicializa el controlador del módulo de gestión ambiental.
     * * @param vista Panel de la interfaz gráfica correspondiente a la gestión ambiental.
     */
    public GestionControlador(GestionAmbiental_panel vista) {
        this.vista = vista;
        this.modelo = new Gestion();

        iniciarEventos();
        listarEnTabla();
    }

    /**
     * Configura y registra los listeners de eventos para los componentes de la vista,
     * incluyendo listeners de teclado para validación en tiempo real y selección de tablas.
     */
    private void iniciarEventos() {
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnActualizar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnLimpiar.addActionListener(this);
        this.vista.btnExportar.addActionListener(this);

        this.vista.txtRuc.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                if (!Character.isDigit(evt.getKeyChar()) || vista.txtRuc.getText().length() >= 13) {
                    evt.consume(); 
                }
            }
        });

        this.vista.txtMetaAnual.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                if (!Character.isDigit(evt.getKeyChar())) evt.consume();
            }
        });

        this.vista.txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarTabla(vista.txtBuscar.getText().trim());
            }
        });

        this.vista.tblGestion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = vista.tblGestion.getSelectedRow();
                if (fila != -1) {
                    String rucSeleccionado = vista.tblGestion.getValueAt(fila, 0).toString();
                    
                    for (Gestion ga : listaGlobal) {
                        if (ga.getRucEntidadAliada().equals(rucSeleccionado)) {
                            idFilaSeleccionada = ga.getIdGestion(); 
                            
                            vista.txtRuc.setText(ga.getRucEntidadAliada());
                            vista.txtNombreEntidad.setText(ga.getNombreEntidad());
                            vista.cbxTipoAutorizacion.setSelectedItem(ga.getTipoAutorizacion());
                            vista.cbxCategoria.setSelectedItem(ga.getCategoriaImpacto());
                            vista.cbxUnidadMedida.setSelectedItem(ga.getUnidadMedida());
                            vista.txtMetaAnual.setText(String.valueOf(ga.getMetaAnualGlobal()));
                            vista.cbxEstadoConvenio.setSelectedItem(ga.getEstadoConvenio());
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnGuardar) guardar();
        if (e.getSource() == vista.btnActualizar) actualizar();
        if (e.getSource() == vista.btnEliminar) eliminar();
        if (e.getSource() == vista.btnLimpiar) limpiarCampos();
        if (e.getSource() == vista.btnExportar) generarReportePDF();
    }
    
    /**
     * Valida mediante reglas de negocio los datos de la entidad aliada antes de su persistencia.
     * Incluye control estricto de campos incompletos y unicidad de RUC y Nombre.
     * * @param ruc Cadena de texto correspondiente al RUC evaluado.
     * @param nombre Cadena de texto del nombre de la entidad.
     * @param metaTexto Formato String del número entero de la meta global.
     * @param idActual ID del registro actual (útil para omitir la validación de unicidad al actualizar).
     * @return String con el consolidado de errores encontrados; cadena vacía si los datos son válidos.
     */
    public String validarDatosGestion(String ruc, String nombre, String metaTexto, int idActual) {
        String mensajeError = "";
        if (ruc == null || ruc.trim().isEmpty()) {
            mensajeError += "- El RUC de la entidad es obligatorio.\n";
        } else if (!ruc.matches("\\d+")) {
            mensajeError += "- El RUC solo debe contener números.\n";
        } else if (ruc.length() != 13) {
            mensajeError += "- El RUC debe tener exactamente 13 dígitos numéricos.\n";
        } else if (listaGlobal != null) {
            for (Gestion ga : listaGlobal) {
                if (ga.getRucEntidadAliada().equals(ruc) && ga.getIdGestion() != idActual) {
                    mensajeError += "- El RUC ingresado ya existe en el sistema, ingrese otro.\n";
                    break;
                }
            }
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            mensajeError += "- El nombre de la entidad es obligatorio.\n";
        } else if (listaGlobal != null) {
            for (Gestion ga : listaGlobal) {
                if (ga.getNombreEntidad().trim().equalsIgnoreCase(nombre.trim()) && ga.getIdGestion() != idActual) {
                    mensajeError += "- El nombre de la entidad ya se encuentra registrado, ingrese otro.\n";
                    break;
                }
            }
        }

        if (metaTexto == null || metaTexto.trim().isEmpty()) {
            mensajeError += "- La meta anual es obligatoria.\n";
        } else {
            try {
                int valorMeta = Integer.parseInt(metaTexto.trim());
                if (valorMeta <= 0) {
                    mensajeError += "- La Meta Anual Global debe ser mayor a cero.\n";
                }
            } catch (NumberFormatException ex) {
                mensajeError += "- La Meta Anual debe ser un número entero válido.\n";
            }
        }

        return mensajeError;
    }
    
    /**
     * Procesa el almacenamiento de un nuevo registro de gestión ambiental tras superar validaciones.
     */
    private void guardar() {
        String ruc = vista.txtRuc.getText().trim();
        String nombre = vista.txtNombreEntidad.getText().trim();
        String meta = vista.txtMetaAnual.getText().trim();
        String errores = validarDatosGestion(ruc, nombre, meta, 0);

        if (!errores.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Corrija los siguientes errores:\n" + errores, "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        llenarModelo();
        modelo.setIdGestion(0); 
        modelo.setEstadoConvenio("Activo"); 

        if (gestionService.guardarGestion(modelo)) { 
            JOptionPane.showMessageDialog(vista, "Registro guardado correctamente."); 
            limpiarCampos();
            listarEnTabla();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al registrar en la base de datos.");
        }
    }
    
    /**
     * Procesa la modificación de un registro seleccionado basándose en su ID único de gestión.
     */
    private void actualizar() {
        if (idFilaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un registro de la tabla para actualizar.");
            return;
        }

        String ruc = vista.txtRuc.getText().trim();
        String nombre = vista.txtNombreEntidad.getText().trim();
        String meta = vista.txtMetaAnual.getText().trim();

        String errores = validarDatosGestion(ruc, nombre, meta, idFilaSeleccionada);
        if (!errores.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Corrija los siguientes errores:\n" + errores, "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        llenarModelo();
        modelo.setIdGestion(idFilaSeleccionada); 
        modelo.setEstadoConvenio(vista.cbxEstadoConvenio.getSelectedItem().toString());

        if (gestionService.guardarGestion(modelo)) { 
            JOptionPane.showMessageDialog(vista, "Registro actualizado correctamente."); 
            limpiarCampos();
            listarEnTabla();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al actualizar en la base de datos.");
        }
    }
    
    /**
     * Ejecuta la eliminación física definitiva de un registro de la base de datos (RF-E05).
     * Solicita una confirmación explícita de doble paso debido al impacto crítico del borrado
     * y captura excepciones asociadas a restricciones de integridad referencial.
     */
    private void eliminar() {
        if (idFilaSeleccionada != -1) {
            int respuesta = JOptionPane.showConfirmDialog(
                    vista, 
                    "¿Está seguro de que desea eliminar este registro?", 
                    "Confirmación", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE
            );
            
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    if (gestionService.eliminarGestion(idFilaSeleccionada)) { 
                        JOptionPane.showMessageDialog(vista, "Registro eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE); 
                        limpiarCampos();
                        listarEnTabla();
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Seleccione una fila para eliminar.");
        }
    }
    
    /**
     * Transfiere el contenido actual de los campos del formulario de la vista hacia la instancia del modelo.
     */
    private void llenarModelo() {
        modelo.setRucEntidadAliada(vista.txtRuc.getText().trim());
        modelo.setNombreEntidad(vista.txtNombreEntidad.getText().trim());
        modelo.setTipoAutorizacion(vista.cbxTipoAutorizacion.getSelectedItem().toString());
        modelo.setCategoriaImpacto(vista.cbxCategoria.getSelectedItem().toString());
        modelo.setUnidadMedida(vista.cbxUnidadMedida.getSelectedItem().toString());
        modelo.setMetaAnualGlobal(Integer.parseInt(vista.txtMetaAnual.getText().trim()));
    }
    
    /**
     * Recupera la totalidad de los registros de gestión ambiental desde la base de datos
     * y refresca la cuadrícula visual de datos (JTable).
     */
    private void listarEnTabla() {
        listaGlobal = gestionService.listarGestiones(); 
        cargarDatosTabla(listaGlobal);
    }
    
    /**
     * Filtra una colección de datos basándose en coincidencias del RUC o Nombre de la entidad.
     * * @param textoFiltro Patrón de búsqueda ingresado por el usuario.
     * @param listaOriginal Lista completa de los datos en memoria.
     * @return Lista filtrada que contiene únicamente las coincidencias encontradas.
     */
    public List<Gestion> filtrarDatosBuscador(String textoFiltro, List<Gestion> listaOriginal) {
        if (textoFiltro == null || textoFiltro.trim().isEmpty()) {
            return listaOriginal; 
        }
        
        var texto = textoFiltro.toLowerCase().trim();
        return listaOriginal.stream()
                .filter(ga -> ga.getRucEntidadAliada().toLowerCase().contains(texto) || 
                              ga.getNombreEntidad().toLowerCase().contains(texto))
                .collect(Collectors.toList());
    }

    /**
     * Invoca el filtro de datos y actualiza inmediatamente los elementos renderizados en el JTable.
     * * @param textoFiltro Patrón de búsqueda.
     */
    private void filtrarTabla(String textoFiltro) {
        List<Gestion> resultados = filtrarDatosBuscador(textoFiltro, listaGlobal);
        cargarDatosTabla(resultados); 
    }

    /**
     * Reestructura el DefaultTableModel del JTable de la vista para desplegar la lista especificada.
     * * @param lista Colección de objetos de tipo Gestion a renderizar.
     */
    private void cargarDatosTabla(List<Gestion> lista) {
        DefaultTableModel model = (DefaultTableModel) vista.tblGestion.getModel();
        model.setRowCount(0); 
        for (Gestion ga : lista) {
            model.addRow(new Object[]{
                ga.getRucEntidadAliada(), 
                ga.getNombreEntidad(), 
                ga.getTipoAutorizacion(),
                ga.getCategoriaImpacto(),
                ga.getUnidadMedida(),
                ga.getMetaAnualGlobal(),
                ga.getEstadoConvenio()
            });
        }
    }
    
    private void limpiarCampos() {
        vista.txtRuc.setText("");
        vista.txtNombreEntidad.setText("");
        vista.cbxCategoria.setSelectedIndex(0);
        vista.txtMetaAnual.setText("");
        vista.cbxTipoAutorizacion.setSelectedIndex(0);
        vista.cbxUnidadMedida.setSelectedIndex(0);
        vista.cbxEstadoConvenio.setSelectedIndex(0);
        vista.txtBuscar.setText(""); 
        idFilaSeleccionada = -1;
    }
    
    /**
     * Exporta el catálogo consolidado de registros a un documento externo en formato PDF en orientación horizontal.
     */
    private void generarReportePDF() {
        var fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        fileChooser.setSelectedFile(new java.io.File("Reporte_Gestion_Ambiental.pdf"));
        
        var seleccion = fileChooser.showSaveDialog(vista);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            var archivo = fileChooser.getSelectedFile();
            try {
                var doc = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4.rotate());
                com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new java.io.FileOutputStream(archivo));
                doc.open();
                
                var fontTitulo = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18);
                var titulo = new com.itextpdf.text.Paragraph("REPORTE DE GESTIÓN AMBIENTAL - ECOVIDA", fontTitulo);
                titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                doc.add(titulo);
                doc.add(new com.itextpdf.text.Paragraph(" "));
                
                var tabla = new com.itextpdf.text.pdf.PdfPTable(7);
                tabla.setWidthPercentage(100);
                
                tabla.addCell("RUC"); tabla.addCell("Entidad"); tabla.addCell("Autorización"); 
                tabla.addCell("Impacto"); tabla.addCell("Unidad"); tabla.addCell("Meta"); tabla.addCell("Estado");
                
                for (var ga : listaGlobal) {
                    tabla.addCell(ga.getRucEntidadAliada());
                    tabla.addCell(ga.getNombreEntidad());
                    tabla.addCell(ga.getTipoAutorizacion());
                    tabla.addCell(ga.getCategoriaImpacto());
                    tabla.addCell(ga.getUnidadMedida());
                    tabla.addCell(String.valueOf(ga.getMetaAnualGlobal()));
                    tabla.addCell(ga.getEstadoConvenio());
                }
                doc.add(tabla);
                doc.close();
                
                JOptionPane.showMessageDialog(vista, "PDF guardado en: " + archivo.getAbsolutePath());
            } catch (com.itextpdf.text.DocumentException | java.io.FileNotFoundException e) {
                JOptionPane.showMessageDialog(vista, "Error al generar PDF. Asegúrate de no tener el archivo abierto.\n" + e.getMessage());
            }
        }
    }
}