package com.picattore.gestion.domain;

import java.util.ArrayList;
import java.util.List;

public class Epoca {
    private int idEpoca;
    private String codigo;
    private int anioInicio;
    private int anioFin;
    private List<EpocaTr> traducciones;

    public Epoca(int idEpoca, String codigo, int anioInicio, int anioFin) {
        this.idEpoca = idEpoca;
        this.codigo = codigo;
        this.anioInicio = anioInicio;
        this.anioFin = anioFin;
        this.traducciones = new ArrayList<>();
    }

    public Epoca(String codigo, int anioInicio, int anioFin) {
        this.codigo = codigo;
        this.anioInicio = anioInicio;
        this.anioFin = anioFin;
        this.traducciones = new ArrayList<>();
    }

    public int getIdEpoca() {
        return idEpoca;
    }

    public void setIdEpoca(int idEpoca) {
        this.idEpoca = idEpoca;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getAnioInicio() {
        return anioInicio;
    }

    public void setAnioInicio(int anioInicio) {
        this.anioInicio = anioInicio;
    }

    public int getAnioFin() {
        return anioFin;
    }

    public void setAnioFin(int anioFin) {
        this.anioFin = anioFin;
    }

    public List<EpocaTr> getTraducciones() {
        return traducciones;
    }

    public void setTraducciones(List<EpocaTr> traducciones) {
        this.traducciones = traducciones;
    }

    public void addTraduccion(EpocaTr traduccion) {
        this.traducciones.add(traduccion);
    }
}
