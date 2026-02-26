package com.picattore.gestion.domain;

public class EpocaTr {
    private int id;
    private int idEpoca;
    private int idIdioma;
    private String nombre;
    private String descripcion;

    public EpocaTr(int id, int idEpoca, int idIdioma, String nombre, String descripcion) {
        this.id = id;
        this.idEpoca = idEpoca;
        this.idIdioma = idIdioma;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public EpocaTr(int idIdioma, String nombre, String descripcion) {
        this.idIdioma = idIdioma;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEpoca() {
        return idEpoca;
    }

    public void setIdEpoca(int idEpoca) {
        this.idEpoca = idEpoca;
    }

    public int getIdIdioma() {
        return idIdioma;
    }

    public void setIdIdioma(int idIdioma) {
        this.idIdioma = idIdioma;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
