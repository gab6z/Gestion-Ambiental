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

public class VoluntarioControlador {
    private final VoluntarioService voluntarioService = new VoluntarioService();
    private final VoluntariosPnl panel;
    private List<Voluntario> voluntariosActuales;

    public VoluntarioControlador(VoluntariosPnl panel) {
        this.panel = panel;
        iniciarEventos();
        cargarTabla();
    }

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

    // --- LÓGICA DE PDF ---
    private void generarPDF() {
        try {
            Document doc = new Document();
            String archivo = "Reporte_Voluntarios.pdf";
            PdfWriter.getInstance(doc, new FileOutputStream(archivo));
            doc.open();
            doc.add(new Paragraph("LISTADO DE VOLUNTARIOS - ECOVIDA"));
            doc.add(new Paragraph(" "));
            
            PdfPTable tabla = new PdfPTable(7);
            tabla.addCell("ID"); tabla.addCell("Nombres"); tabla.addCell("Cédula");
            tabla.addCell("Género"); tabla.addCell("Correo"); tabla.addCell("Disponibilidad"); tabla.addCell("Estado");

            for (Voluntario v : voluntariosActuales) {
                tabla.addCell(String.valueOf(v.getId_voluntario()));
                tabla.addCell(v.getNombres_completos());
                tabla.addCell(v.getCedula());
                tabla.addCell(v.getGenero());
                tabla.addCell(v.getCorreo());
                tabla.addCell(v.getDisponibilidad_dias());
                tabla.addCell(v.getEstado());
            }
            doc.add(tabla);
            doc.close();
            panel.mostrarMensaje("PDF generado: " + archivo);
        } catch (Exception e) {
            panel.mostrarError("Error al generar PDF: " + e.getMessage());
        }
    }

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

    private void eliminar() {
        Voluntario seleccionado = panel.getVoluntarioDelFormulario();
        if (seleccionado.getId_voluntario() == 0) {
            panel.mostrarError("Selecciona un voluntario.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(panel, "¿Dar de baja?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                voluntarioService.eliminarVoluntario(seleccionado.getId_voluntario());
                panel.mostrarMensaje("Voluntario desactivado.");
                actualizarVista();
            } catch (Exception e) {
                panel.mostrarError("Error: " + e.getMessage());
            }
        }
    }

    private void actualizarVista() {
        panel.limpiarFormulario();
        cargarTabla();
    }
}