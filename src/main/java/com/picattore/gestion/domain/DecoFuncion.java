package com.picattore.gestion.domain;

public class DecoFuncion {
    private int id;
    private int idDecoder;
    private String funcion;
    private String tipoFuncion; // on/off, switch
    private String descripcion;

    public DecoFuncion(int id, int idDecoder, String funcion, String tipoFuncion, String descripcion) {
        this.id = id;
        this.idDecoder = idDecoder;
        this.funcion = funcion;
        this.tipoFuncion = tipoFuncion;
        this.descripcion = descripcion;
    }

    public DecoFuncion(String funcion, String tipoFuncion, String descripcion) {
        this.funcion = funcion;
        this.tipoFuncion = tipoFuncion;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdDecoder() { return idDecoder; }
    public void setIdDecoder(int idDecoder) { this.idDecoder = idDecoder; }
    public String getFuncion() { return funcion; }
    public void setFuncion(String funcion) { this.funcion = funcion; }
    public String getTipoFuncion() { return tipoFuncion; }
    public void setTipoFuncion(String tipoFuncion) { this.tipoFuncion = tipoFuncion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
