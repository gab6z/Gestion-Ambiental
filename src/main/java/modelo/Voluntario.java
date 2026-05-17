/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
/**
 * Representa la entidad Voluntario en el sistema, extendiendo de la clase Persona.
 * Esta clase encapsula la información específica de un voluntario, como sus 
 * habilidades y disponibilidad de tiempo, además de sus credenciales.
 * * @author EDUARDO
 * @version 1.0
 * @since 2026-05-07
 */
import java.util.Objects;

public class Voluntario extends Persona {
    private int id_voluntario;
    private String disponibilidad_dias;
    private String habilidades;

    /**
     * Constructor por defecto. Crea una instancia vacía de Voluntario.
     */
    public Voluntario() {
        super();
    }
    /**
        * Constructor con parámetros para inicializar un voluntario con sus datos básicos.
        * * @param id_voluntario Identificador único del registro.
        * @param cedula Número de identificación del voluntario.
        * @param nombres Nombres y apellidos completos.
        * @param correo Dirección de correo electrónico institucional o personal.
        * @param habilidades Descripción de las capacidades del voluntario.
        * @param disponibilidad Días u horarios disponibles.
        * @param estado Estado actual en el sistema (Activo, Inactivo, etc.).
     */
    public Voluntario(int id_voluntario, String cedula, String nombres, String correo, 
                     String habilidades, String disponibilidad, String estado) {
        super();
        this.id_voluntario = id_voluntario;
        this.setCedula(cedula);
        this.setNombres_completos(nombres);
        this.setCorreo(correo);
        this.setHabilidades(habilidades);
        this.setDisponibilidad_dias(disponibilidad);
        this.setEstado(estado);
    }
  
    public int getId_voluntario() { return id_voluntario; }
    public void setId_voluntario(int id_voluntario) { this.id_voluntario = id_voluntario; }

    public void setHabilidades(String habilidades) {
        this.habilidades = validarNoVacio(habilidades, "Habilidades");
    }

    public String getHabilidades() { return habilidades; }

    public void setDisponibilidad_dias(String disp) {
        this.disponibilidad_dias = validarNoVacio(disp, "Disponibilidad");
    }

    public String getDisponibilidad_dias() { return disponibilidad_dias; }

    /**
     * Retorna una representación en texto del voluntario, generalmente su nombre.
     * @return Nombres completos del voluntario.
     */
    @Override
    public String toString() {
        return getNombres_completos(); 
    }
    /**
     * Compara si este voluntario es igual a otro objeto basándose en la cédula.
     * @param o Objeto a comparar.
     * @return true si las cédulas coinciden, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Voluntario)) return false;
        Voluntario that = (Voluntario) o;
        return Objects.equals(this.getCedula(), that.getCedula());
    }

    /**
     * Genera un valor hash basado en la cédula del voluntario.
     * @return valor hash del objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getCedula());
    }
}