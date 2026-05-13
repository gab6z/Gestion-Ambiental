/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.util.Objects;

/**
 * Descripción: Clase modelo que representa un sector ambiental específico, 
 * gestionando sus coordenadas, nivel de riesgo y estado de intervención.
 * Proyecto: Sistema de Gestión Ambiental (EcoVida)
 * 
 * @author Gabriela Solange Gonzalez Roman
 * @version 1.0
 * @since 2026-05-05
 */

public class Sector {

    private int idSector;
    private String nombreZona;
    private String latitud;
    private String longitud;
    private String provinciaCiudad;
    private String nivelRiesgo;
    private String descripcionTerreno;
    private String estadoZona;

    public Sector() {}

    public Sector(String nombreZona, String latitud, String longitud, String provinciaCiudad, 
                  String nivelRiesgo, String descripcionTerreno, String estadoZona) {
        setNombreZona(nombreZona);
        setLatitud(latitud);
        setLongitud(longitud);
        setProvinciaCiudad(provinciaCiudad);
        setNivelRiesgo(nivelRiesgo);
        setDescripcionTerreno(descripcionTerreno);
        setEstadoZona(estadoZona);
    }


    public int getIdSector() { return idSector; }
    public String getNombreZona() { return nombreZona; }
    public String getLatitud() { return latitud; }
    public String getLongitud() { return longitud; }
    public String getProvinciaCiudad() { return provinciaCiudad; }
    public String getNivelRiesgo() { return nivelRiesgo; }
    public String getDescripcionTerreno() { return descripcionTerreno; }
    public String getEstadoZona() { return estadoZona; }


    public void setIdSector(int idSector) {
        if (idSector < 0) throw new IllegalArgumentException("El idSector no puede ser negativo");
        this.idSector = idSector;
    }

   public void setNombreZona(String nombreZona) {
        String valor = validarNoVacio(nombreZona, "nombreZona");
        // Solo letras, números y espacios. Máximo 150 caracteres.
        if (!valor.matches("^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ]+$")) {
            throw new IllegalArgumentException("El nombre de la zona solo permite letras, números y espacios.");
        }
        if (valor.length() > 150) throw new IllegalArgumentException("El nombre excede los 150 caracteres.");
        this.nombreZona = valor;
    }

   public void setLatitud(String latitud) {
        String valor = validarNoVacio(latitud, "latitud");
        if (!valor.matches("^-?\\d+(\\.\\d+)?$")) {
            throw new IllegalArgumentException("Latitud inválida. Recibimos: '" + valor + "'. Formato esperado: -2.1962");
        }
        if (valor.length() > 12) throw new IllegalArgumentException("La latitud no puede exceder los 12 caracteres.");
        this.latitud = valor;
    }

    public void setLongitud(String longitud) {
        String valor = validarNoVacio(longitud, "longitud");
        if (!valor.matches("^-?\\d+(\\.\\d+)?$")) {
            throw new IllegalArgumentException("Longitud inválida. Recibimos: '" + valor + "'. Formato esperado: -79.8862");
        }
        if (valor.length() > 12) throw new IllegalArgumentException("La longitud no puede exceder los 12 caracteres.");
        this.longitud = valor;
    }

    public void setProvinciaCiudad(String provinciaCiudad) {
        String valor = validarNoVacio(provinciaCiudad, "provinciaCiudad");
        // Permite letras, espacios y el guion (-)
        if (!valor.matches("^[a-zA-Z áéíóúÁÉÍÓÚñÑ\\-]+$")) {
            throw new IllegalArgumentException("Provincia/Ciudad solo permite letras, espacios y el signo guion (-).");
        }
        if (valor.length() > 100) throw new IllegalArgumentException("Provincia/Ciudad excede los 100 caracteres.");
        this.provinciaCiudad = valor;
    }

    public void setDescripcionTerreno(String descripcionTerreno) {
        if (descripcionTerreno != null && descripcionTerreno.length() > 255) {
            throw new IllegalArgumentException("La descripción no puede exceder los 255 caracteres.");
        }
        this.descripcionTerreno = (descripcionTerreno != null) ? descripcionTerreno.trim() : null;
    }

    public void setNivelRiesgo(String nivelRiesgo) {
        String riesgoLimpio = validarNoVacio(nivelRiesgo, "nivelRiesgo");
        
        if (!riesgoLimpio.equalsIgnoreCase("Bajo") && 
            !riesgoLimpio.equalsIgnoreCase("Medio") && 
            !riesgoLimpio.equalsIgnoreCase("Alto")) {
            throw new IllegalArgumentException("Nivel de riesgo inválido. Debe ser: Bajo, Medio o Alto. Recibido: " + nivelRiesgo);
        }
        
        this.nivelRiesgo = riesgoLimpio.substring(0, 1).toUpperCase() + riesgoLimpio.substring(1).toLowerCase();
    }


    public void setEstadoZona(String estadoZona) {
        String estadoLimpio = validarNoVacio(estadoZona, "estadoZona");
        
        if (!estadoLimpio.equalsIgnoreCase("Requiere intervención") && 
            !estadoLimpio.equalsIgnoreCase("En proceso") && 
            !estadoLimpio.equalsIgnoreCase("Restaurado")) {
            throw new IllegalArgumentException("Estado de zona inválido. Debe ser: Requiere intervención, En proceso o Restaurado. Recibido: " + estadoZona);
        }
        this.estadoZona = estadoLimpio;
    }


    private String validarNoVacio(String valor, String campo) {
        if (valor == null || valor.isBlank() || valor.equals("Seleccionar...")) {
            throw new IllegalArgumentException("El campo '" + campo + "' no puede ser nulo, vacío o sin seleccionar");
        }
        return valor.trim();
    }

    public boolean requiereIntervencionUrgente() {
        return "Alto".equalsIgnoreCase(nivelRiesgo) && "Requiere intervención".equalsIgnoreCase(estadoZona);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sector)) return false;
        Sector sector = (Sector) o;
        return idSector == sector.idSector;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSector);
    }

    /*
    @Override
    public String toString() {
        return "Sector{" +
                "id=" + idSector +
                ", zona='" + nombreZona + '\'' +
                ", riesgo='" + nivelRiesgo + '\'' +
                ", estado='" + estadoZona + '\'' +
                '}';
    }
    */
    
    @Override
    public String toString() {
        return nombreZona;
    }
    
}