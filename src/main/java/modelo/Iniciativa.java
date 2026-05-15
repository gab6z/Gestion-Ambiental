package modelo;

import java.sql.Date;
import java.sql.Time;

/**
 * Clase Modelo que representa una Iniciativa o Planificación Ambiental en el
 * sistema EcoVida. Esta entidad actúa como un POJO (Plain Old Java Object) para
 * el mapeo objeto-relacional (ORM) con la tabla {@code INICIATIVA} de la base
 * de datos, encapsulando tanto las claves foráneas como los atributos
 * descriptivos cargados mediante consultas complejas.
 *
 * * @author Solis Caballero Geovanny Andrés
 * @version 1.2
 */
public class Iniciativa {
    
    private int idIniciativa;
    private int idSector;
    private int idTarea;
    private int idGestion;
    private String titulo;
    private String descripcion;
    private Date fechaEjecucion;
    private Time horaInicio;
    private Time horaFin;
    private int meta;
    private double presupuesto;
    private String estado;
    private Date fechaFin;
    private String nombreSector;
    private String nombreTarea;
    private String nombreGestion;
    private int totalParticipantes;
    
    /**
     * Constructor por defecto de la clase Iniciativa. Requerido para la
     * instanciación dinámica en las estructuras de colecciones y capas DAO.
     */
    public Iniciativa (){}
    
    public String getNombreSector() {
        return nombreSector;
    }

    public void setNombreSector(String nombreSector) {
        this.nombreSector = nombreSector;
    }

    public String getNombreTarea() {
        return nombreTarea;
    }

    public void setNombreTarea(String nombreTarea) {
        this.nombreTarea = nombreTarea;
    }

    public String getNombreGestion() {
        return nombreGestion;
    }

    public void setNombreGestion(String nombreGestion) {
        this.nombreGestion = nombreGestion;
    }
    
    public int getIdIniciativa() {
        return idIniciativa;
    }

    public void setIdIniciativa(int idIniciativa) {
        this.idIniciativa = idIniciativa;
    }

    public int getIdSector() {
        return idSector;
    }

    public void setIdSector(int idSector) {
        this.idSector = idSector;
    }

    public int getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }

    public int getIdGestion() {
        return idGestion;
    }

    public void setIdGestion(int idGestion) {
        this.idGestion = idGestion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(Date fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public Time getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Time horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Time getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Time horaFin) {
        this.horaFin = horaFin;
    }

    public int getMeta() {
        return meta;
    }

    public void setMeta(int meta) {
        this.meta = meta;
    }

    public double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }
    
    public int getTotalParticipantes() {
        return totalParticipantes;
    }

    public void setTotalParticipantes(int total) {
        this.totalParticipantes = total;
    }
   
}
