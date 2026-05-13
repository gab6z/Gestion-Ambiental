/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Usuario
 */

public class Administrador extends Persona {
    
    private int idAdmin;
    private String cargoDepartamento; // Lo que pusimos en la base de datos

    public Administrador() {
        super(); // Llama al constructor del padre (Persona)
    }

    // --- GETTERS Y SETTERS (Solo de sus variables propias) ---
    
    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getCargoDepartamento() {
        return cargoDepartamento;
    }

    public void setCargoDepartamento(String cargoDepartamento) {
        this.cargoDepartamento = cargoDepartamento;
    }
    
    // No hace falta poner getCedula() o getNombre(), 
    // ¡ya los tiene escondidos gracias a la herencia!
}
