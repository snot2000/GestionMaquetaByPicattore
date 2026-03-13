package com.picattore.gestion.domain;

public class TipoModeloTr {
    private int id;
    private int idTipoModelo;
    private int idIdioma;
    private String nombre;
    private String descripcion;

    public TipoModeloTr(int id, int idTipoModelo, int idIdioma, String nombre, String descripcion) {
        this.id = id;
        this.idTipoModelo = idTipoModelo;
        this.idIdioma = idIdioma;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public TipoModeloTr(int idIdioma, String nombre, String descripcion) {
        this.idIdioma = idIdioma;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdTipoModelo() { return idTipoModelo; }
    public void setIdTipoModelo(int idTipoModelo) { this.idTipoModelo = idTipoModelo; }
    public int getIdIdioma() { return idIdioma; }
    public void setIdIdioma(int idIdioma) { this.idIdioma = idIdioma; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
