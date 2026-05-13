/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author EDUARDO
 */

public abstract class Persona {
    protected int idPersona;
    protected String cedula;
    protected String contrasena;
    protected String nombres_completos;
    protected String correo;
    protected String telefono;
    protected String genero;
    protected String estado;

    public Persona() {}

    // --- MÉTODOS DE VALIDACIÓN REUTILIZABLES ---
    protected String validarNoVacio(String valor, String campo) {
        if (valor == null || valor.isBlank() || valor.equals("Seleccionar...")) {
            throw new IllegalArgumentException("El campo '" + campo + "' no puede estar vacío.");
        }
        return valor.trim();
    }

    // --- SETTERS CON TUS VALIDACIONES ---
    public void setCedula(String cedula) {
        String valor = validarNoVacio(cedula, "cédula");
        if (!valor.matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("La cédula debe contener exactamente 10 dígitos.");
        }
        this.cedula = valor;
    }

    public void setNombres_completos(String nombres) {
        String valor = validarNoVacio(nombres, "nombres");
        if (!valor.matches("^[a-zA-Z áéíóúÁÉÍÓÚñÑ]+$")) {
            throw new IllegalArgumentException("Los nombres solo permiten letras.");
        }
        this.nombres_completos = valor;
    }

    public void setCorreo(String correo) {
        String valor = validarNoVacio(correo, "correo");
        if (!valor.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Formato de correo electrónico inválido.");
        }
        this.correo = valor;
    }

    // Getters y Setters restantes (telefono, genero, estado, contrasena...)
    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }
    public String getCedula() { return cedula; }
    public String getNombres_completos() { return nombres_completos; }
    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}