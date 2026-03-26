package com.picattore.gestion.domain;

public class DecoCV {
    private int id;
    private int idDecoder;
    private String cv;
    private String dato;

    public DecoCV(int id, int idDecoder, String cv, String dato) {
        this.id = id;
        this.idDecoder = idDecoder;
        this.cv = cv;
        this.dato = dato;
    }

    public DecoCV(String cv, String dato) {
        this.cv = cv;
        this.dato = dato;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdDecoder() { return idDecoder; }
    public void setIdDecoder(int idDecoder) { this.idDecoder = idDecoder; }
    public String getCv() { return cv; }
    public void setCv(String cv) { this.cv = cv; }
    public String getDato() { return dato; }
    public void setDato(String dato) { this.dato = dato; }
}
