package modelo;

/**
 * Descripción: Clase que representa la entidad Tarea dentro del sistema.
 * Define los atributos y comportamientos básicos de una tarea ambiental, 
 * incluyendo su dificultad, cupo y estado.
 * Proyecto: Sistema de Gestión Ambiental (EcoVida)
 * @author Leandro Palacios
 * @version 1.0
 * @since 2026-05-06
 */
public class Tarea {
    private int idTarea;
    private String nombreTarea;
    private String descripcionInstrucciones;
    private String herramientasRequeridas;
    private String dificultadTecnica;
    private int cupoRecomendado;
    private String estadoTarea;
    
    public Tarea(){
    }
    
    public int getIdTarea() {
        return idTarea; 
    }

    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea; 
    }

    public String getNombreTarea() {
        return nombreTarea; 
    }

    public void setNombreTarea(String nombreTarea) {
        this.nombreTarea = nombreTarea; 
    }

    public String getDescripcionInstrucciones() {
        return descripcionInstrucciones; 
    }

    public void setDescripcionInstrucciones(String descripcionInstrucciones) {
        this.descripcionInstrucciones = descripcionInstrucciones; 
    }

    public String getHerramientasRequeridas() {
        return herramientasRequeridas; 
    }

    public void setHerramientasRequeridas(String herramientasRequeridas) {
        this.herramientasRequeridas = herramientasRequeridas; 
    }

    public String getDificultadTecnica() {
        return dificultadTecnica; 
    }

    public void setDificultadTecnica(String dificultadTecnica) {
        this.dificultadTecnica = dificultadTecnica; 
    }

    public int getCupoRecomendado() {
        return cupoRecomendado; 
    }

    public void setCupoRecomendado(int cupoRecomendado) {
        this.cupoRecomendado = cupoRecomendado; 
    }

    public String getEstadoTarea() {
        return estadoTarea; 
    }

    public void setEstadoTarea(String estadoTarea) {
        this.estadoTarea = estadoTarea; 
    }

    @Override
    public String toString() {
        return nombreTarea;
    }
    
}
