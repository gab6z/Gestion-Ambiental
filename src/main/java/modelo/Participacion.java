/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author EDUARDO
 * @since 2026-05-10
 */
public class Participacion {
    private int idParticipacion;
    private int idVoluntario;
    private int idIniciativa;
    private String estado;
    private String nombreIniciativa; 
    private String fechaIniciativa;

    public Participacion() {}

    // Getters y Setters
    public int getIdParticipacion() { return idParticipacion; }
    public void setIdParticipacion(int id) { this.idParticipacion = id; }
    
    public String getNombreIniciativa() { return nombreIniciativa; }
    public void setNombreIniciativa(String nombre) { this.nombreIniciativa = nombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaIniciativa() { return fechaIniciativa; }
    public void setFechaIniciativa(String fecha) { this.fechaIniciativa = fecha; }
    
    public int getIdIniciativa() { return idIniciativa; }
    public void setIdIniciativa(int idIniciativa) { this.idIniciativa = idIniciativa; }
}
