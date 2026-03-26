package com.picattore.gestion.domain;

public class Dueno {
    private int id;
    private String nombre;

    public Dueno(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Dueno(String nombre) {
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
