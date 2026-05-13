package controlador;

import modelo.Gestion;
import service.GestionService;
import vista.GestionAmbiental_panel;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


/**
 * Controlador del módulo de Gestión Ambiental.
 * Inicializa la vista, el modelo y los eventos del sistema.
 * Se encarga de capturar los eventos de la interfaz gráfica (Vista) y delegar 
 * las operaciones de validación, registro y búsqueda a la capa de Servicio.
 * Proyecto: Sistema de Gestión de Iniciativas de Preservación Ambiental (SGIPA) - EcoVida
 * @author Dominica Lilibeth Torres Bohorquez
 * @version 1.0
 * @since 2026-05-05
 */
public class GestionControlador implements ActionListener {

    private final Gestion modelo;
    private final GestionService gestionService = new GestionService(); 
    private final GestionAmbiental_panel vista;
    private List<Gestion> listaGlobal;
    private int idFilaSeleccionada = -1;

    public GestionControlador(GestionAmbiental_panel vista) {
        this.vista = vista;
        this.modelo = new Gestion();

        iniciarEventos();
        listarEnTabla();
    }

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
                            vista.txtCategoriaImpacto.setText(ga.getCategoriaImpacto());
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
     * Verifica que el RUC contenga exactamente 13 dígitos numéricos y que la meta sea un valor válido.
     * Este método es testeado unitariamente mediante JUnit.
     * @param ruc El número de RUC de la entidad aliada ingresado en el formulario.
     * @param nombre El nombre completo o razón social de la entidad.
     * @param impacto La descripción de la categoría de impacto ambiental.
     * @param metaTexto La meta anual global capturada como texto desde la interfaz.
     * @return Una cadena de texto (String) que acumula los mensajes de error encontrados. Si retorna vacío (""), los datos son válidos.
     */
    public String validarDatosGestion(String ruc, String nombre, String impacto, String metaTexto) {
        String mensajeError = "";

        if (ruc == null || ruc.trim().isEmpty()) {
            mensajeError += "- El RUC de la entidad es obligatorio.\n";
        } else if (!ruc.matches("\\d+")) {
            mensajeError += "- El RUC solo debe contener números.\n";
        } else if (ruc.length() != 13) {
            mensajeError += "- El RUC debe tener exactamente 13 dígitos numéricos.\n";
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            mensajeError += "- El nombre de la entidad es obligatorio.\n";
        }

        if (impacto == null || impacto.trim().isEmpty()) {
            mensajeError += "- La categoría de impacto es obligatoria.\n";
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
     * Guarda un nuevo registro de gestión ambiental.
     * Valida los datos ingresados y registra la información
     * en la base de datos mediante la capa de servicio.
     */
    private void guardar() {
        String ruc = vista.txtRuc.getText().trim();
        String nombre = vista.txtNombreEntidad.getText().trim();
        String impacto = vista.txtCategoriaImpacto.getText().trim();
        String meta = vista.txtMetaAnual.getText().trim();

        String errores = validarDatosGestion(ruc, nombre, impacto, meta);

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
     * Actualiza un registro existente seleccionado en la tabla.
     * Verifica la selección y valida los datos antes de persistir los cambios.
     */
    private void actualizar() {
        if (idFilaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un registro de la tabla para actualizar.");
            return;
        }

        String ruc = vista.txtRuc.getText().trim();
        String nombre = vista.txtNombreEntidad.getText().trim();
        String impacto = vista.txtCategoriaImpacto.getText().trim();
        String meta = vista.txtMetaAnual.getText().trim();

        String errores = validarDatosGestion(ruc, nombre, impacto, meta);

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
     * Elimina un registro seleccionado de la tabla.
     * Muestra mensajes de confirmación o error según el resultado.
     */
    private void eliminar() {
        if (idFilaSeleccionada != -1) {
            try {
                if (gestionService.eliminarGestion(idFilaSeleccionada)) { 
                    JOptionPane.showMessageDialog(vista, "Registro eliminado correctamente."); 
                    limpiarCampos();
                    listarEnTabla();
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Seleccione una fila para eliminar.");
        }
    }
    
    
    /**
     * Transfiere los datos ingresados en la interfaz gráfica
     * hacia el objeto modelo de Gestión.
     */
    private void llenarModelo() {
        modelo.setRucEntidadAliada(vista.txtRuc.getText().trim());
        modelo.setNombreEntidad(vista.txtNombreEntidad.getText().trim());
        modelo.setTipoAutorizacion(vista.cbxTipoAutorizacion.getSelectedItem().toString());
        modelo.setCategoriaImpacto(vista.txtCategoriaImpacto.getText().trim());
        modelo.setUnidadMedida(vista.cbxUnidadMedida.getSelectedItem().toString());
        modelo.setMetaAnualGlobal(Integer.parseInt(vista.txtMetaAnual.getText().trim()));
    }
    
    /**
     * Obtiene todas las gestiones registradas y las carga en la tabla.
     */
    private void listarEnTabla() {
        listaGlobal = gestionService.listarGestiones(); 
        cargarDatosTabla(listaGlobal);
    }
    
    
    /**
     * Filtra la lista global de gestiones ambientales utilizando un término de búsqueda.
     * La coincidencia se evalúa tanto en el RUC como en el Nombre de la entidad, ignorando mayúsculas y minúsculas.
     * * @param textoFiltro El texto o fragmento introducido por el usuario en el buscador.
     * @param listaOriginal La lista completa de entidades obtenida desde la base de datos.
     * @return Una nueva lista (List) que contiene únicamente los objetos que coinciden con el filtro de búsqueda.
     */
    public List<Gestion> filtrarDatosBuscador(String textoFiltro, List<Gestion> listaOriginal) {
        List<Gestion> listaFiltrada = new ArrayList<>();
        
        if (textoFiltro == null || textoFiltro.trim().isEmpty()) {
            return listaOriginal; 
        }
        
        String texto = textoFiltro.toLowerCase().trim();
        for (Gestion ga : listaOriginal) {
            if (ga.getRucEntidadAliada().toLowerCase().contains(texto) || 
                ga.getNombreEntidad().toLowerCase().contains(texto)) {
                listaFiltrada.add(ga);
            }
        }
        return listaFiltrada;
    }

    private void filtrarTabla(String textoFiltro) {
        List<Gestion> resultados = filtrarDatosBuscador(textoFiltro, listaGlobal);
        cargarDatosTabla(resultados); 
    }

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
    
    /**
     * Limpia todos los campos del formulario y reinicia la selección actual.
     */
    private void limpiarCampos() {
        vista.txtRuc.setText("");
        vista.txtNombreEntidad.setText("");
        vista.txtCategoriaImpacto.setText("");
        vista.txtMetaAnual.setText("");
        vista.cbxTipoAutorizacion.setSelectedIndex(0);
        vista.cbxUnidadMedida.setSelectedIndex(0);
        vista.cbxEstadoConvenio.setSelectedIndex(0);
        vista.txtBuscar.setText(""); 
        idFilaSeleccionada = -1;
    }
    
    /**
     * Genera un reporte PDF con la información de las gestiones ambientales.
     * Permite seleccionar la ubicación de guardado mediante un JFileChooser.
     */
    private void generarReportePDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        fileChooser.setSelectedFile(new java.io.File("Reporte_Gestion_Ambiental.pdf"));
        
        int seleccion = fileChooser.showSaveDialog(vista);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            java.io.File archivo = fileChooser.getSelectedFile();
            try {
                com.itextpdf.text.Document doc = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4.rotate());
                com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new java.io.FileOutputStream(archivo));
                doc.open();
                
                com.itextpdf.text.Font fontTitulo = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18);
                com.itextpdf.text.Paragraph titulo = new com.itextpdf.text.Paragraph("REPORTE DE GESTIÓN AMBIENTAL - ECOVIDA", fontTitulo);
                titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                doc.add(titulo);
                doc.add(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.pdf.PdfPTable tabla = new com.itextpdf.text.pdf.PdfPTable(7);
                tabla.setWidthPercentage(100);
                
                tabla.addCell("RUC"); tabla.addCell("Entidad"); tabla.addCell("Autorización"); 
                tabla.addCell("Impacto"); tabla.addCell("Unidad"); tabla.addCell("Meta"); tabla.addCell("Estado");
                
                for (Gestion ga : listaGlobal) {
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
            } catch (Exception e) {
                JOptionPane.showMessageDialog(vista, "Error al generar PDF. Asegúrate de no tener el archivo abierto.\n" + e.getMessage());
            }
        }
    }
}