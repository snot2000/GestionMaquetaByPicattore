package com.picattore.gestion.domain;

public class PaisTr {
    private int id;
    private int idPais;
    private int idIdioma;
    private String nombre;

    public PaisTr(int id, int idPais, int idIdioma, String nombre) {
        this.id = id;
        this.idPais = idPais;
        this.idIdioma = idIdioma;
        this.nombre = nombre;
    }

    public PaisTr(int idIdioma, String nombre) {
        this.idIdioma = idIdioma;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
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
}
