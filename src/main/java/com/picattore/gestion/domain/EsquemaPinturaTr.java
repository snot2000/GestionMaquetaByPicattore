package com.picattore.gestion.domain;

public class EsquemaPinturaTr {
    private int id;
    private int idEsquemaPintura;
    private int idIdioma;
    private String descripcion;
    private String codigoColores;
    private String colores;

    public EsquemaPinturaTr(int id, int idEsquemaPintura, int idIdioma, String descripcion, String codigoColores, String colores) {
        this.id = id;
        this.idEsquemaPintura = idEsquemaPintura;
        this.idIdioma = idIdioma;
        this.descripcion = descripcion;
        this.codigoColores = codigoColores;
        this.colores = colores;
    }

    public EsquemaPinturaTr(int idIdioma, String descripcion, String codigoColores, String colores) {
        this.idIdioma = idIdioma;
        this.descripcion = descripcion;
        this.codigoColores = codigoColores;
        this.colores = colores;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdEsquemaPintura() { return idEsquemaPintura; }
    public void setIdEsquemaPintura(int idEsquemaPintura) { this.idEsquemaPintura = idEsquemaPintura; }
    public int getIdIdioma() { return idIdioma; }
    public void setIdIdioma(int idIdioma) { this.idIdioma = idIdioma; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCodigoColores() { return codigoColores; }
    public void setCodigoColores(String codigoColores) { this.codigoColores = codigoColores; }
    public String getColores() { return colores; }
    public void setColores(String colores) { this.colores = colores; }
}
