package com.picattore.gestion.domain;

public class Escala {
    private int idEscala;
    private String codigo;
    private String escala;

    public Escala(int idEscala, String codigo, String escala) {
        this.idEscala = idEscala;
        this.codigo = codigo;
        this.escala = escala;
    }

    public Escala(String codigo, String escala) {
        this.codigo = codigo;
        this.escala = escala;
    }

    public int getIdEscala() {
        return idEscala;
    }

    public void setIdEscala(int idEscala) {
        this.idEscala = idEscala;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEscala() {
        return escala;
    }

    public void setEscala(String escala) {
        this.escala = escala;
    }

    @Override
    public String toString() {
        return codigo + " (" + escala + ")";
    }
}
