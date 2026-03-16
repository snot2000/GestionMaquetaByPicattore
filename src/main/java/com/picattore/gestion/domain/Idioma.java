package com.picattore.gestion.domain;

public class Idioma {
    private int id;
    private String codigo;
    private String nombre;
    private boolean principal;

    public Idioma(int id, String codigo, String nombre, boolean principal) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.principal = principal;
    }

    public Idioma(String codigo, String nombre, boolean principal) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.principal = principal;
    }

    // Constructor antiguo para compatibilidad (por defecto no principal)
    public Idioma(int id, String codigo, String nombre) {
        this(id, codigo, nombre, false);
    }

    public Idioma(String codigo, String nombre) {
        this(codigo, nombre, false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    @Override
    public String toString() {
        return nombre + " (" + codigo + ")" + (principal ? " *" : "");
    }
}
