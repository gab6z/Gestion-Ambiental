/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.Objects;

public class Voluntario extends Persona {
    private int id_voluntario;
    private String disponibilidad_dias;
    private String habilidades;

    public Voluntario() {
        super();
    }

    // Constructor completo
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

    // --- GETTERS Y SETTERS PROPIOS ---
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

    @Override
    public String toString() {
        return getNombres_completos(); 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Voluntario)) return false;
        Voluntario that = (Voluntario) o;
        return Objects.equals(this.getCedula(), that.getCedula());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCedula());
    }
}