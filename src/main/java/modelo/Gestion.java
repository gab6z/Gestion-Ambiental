package modelo;

/**
 * Representa la entidad de negocio Gestión Ambiental.
 * Contiene los atributos que mapean la tabla 'gestion_ambiental' en la base de datos,
 * encapsulando la información de las entidades aliadas, metas y parámetros de impacto.
 * @author Dominica Lilibeth Torres Bohorquez
 * @version 1.0
 * @since 2026-05-11
 */
public class Gestion {
    private int idGestion;
    private String rucEntidadAliada;
    private String nombreEntidad;
    private String tipoAutorizacion;
    private String categoriaImpacto;
    private String unidadMedida;
    private int metaAnualGlobal;
    private String estadoConvenio;

    public Gestion() {}

    // Getters y Setters

    public int getIdGestion() {
        return idGestion;
    }

    public String getRucEntidadAliada() {
        return rucEntidadAliada;
    }

    public String getNombreEntidad() {
        return nombreEntidad;
    }

    public String getTipoAutorizacion() {
        return tipoAutorizacion;
    }

    public String getCategoriaImpacto() {
        return categoriaImpacto;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public int getMetaAnualGlobal() {
        return metaAnualGlobal;
    }

    public String getEstadoConvenio() {
        return estadoConvenio;
    }

    public void setIdGestion(int idGestion) {
        this.idGestion = idGestion;
    }

    public void setRucEntidadAliada(String rucEntidadAliada) {
        this.rucEntidadAliada = rucEntidadAliada;
    }

    public void setNombreEntidad(String nombreEntidad) {
        this.nombreEntidad = nombreEntidad;
    }

    public void setTipoAutorizacion(String tipoAutorizacion) {
        this.tipoAutorizacion = tipoAutorizacion;
    }

    public void setCategoriaImpacto(String categoriaImpacto) {
        this.categoriaImpacto = categoriaImpacto;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public void setMetaAnualGlobal(int metaAnualGlobal) {
        this.metaAnualGlobal = metaAnualGlobal;
    }

    public void setEstadoConvenio(String estadoConvenio) {
        this.estadoConvenio = estadoConvenio;
    }
    
    @Override
    public String toString() {
        return this.nombreEntidad; 
    }
}