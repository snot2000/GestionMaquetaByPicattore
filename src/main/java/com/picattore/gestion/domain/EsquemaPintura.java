package com.picattore.gestion.domain;

import java.util.ArrayList;
import java.util.List;

public class EsquemaPintura {
    private int idEsquemaPintura;
    private int idPais;
    private int idOperadora;
    private String nombre;
    private Integer anioInicio;
    private Integer anioFin;
    private List<EsquemaPinturaTr> traducciones;

    public EsquemaPintura(int idPais, int idOperadora, String nombre, Integer anioInicio, Integer anioFin) {
        this.idPais = idPais;
        this.idOperadora = idOperadora;
        this.nombre = nombre;
        this.anioInicio = anioInicio;
        this.anioFin = anioFin;
        this.traducciones = new ArrayList<>();
    }

    public EsquemaPintura(int idEsquemaPintura, int idPais, int idOperadora, String nombre, Integer anioInicio, Integer anioFin) {
        this(idPais, idOperadora, nombre, anioInicio, anioFin);
        this.idEsquemaPintura = idEsquemaPintura;
    }

    // Getters y Setters
    public int getIdEsquemaPintura() { return idEsquemaPintura; }
    public void setIdEsquemaPintura(int idEsquemaPintura) { this.idEsquemaPintura = idEsquemaPintura; }
    public int getIdPais() { return idPais; }
    public void setIdPais(int idPais) { this.idPais = idPais; }
    public int getIdOperadora() { return idOperadora; }
    public void setIdOperadora(int idOperadora) { this.idOperadora = idOperadora; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getAnioInicio() { return anioInicio; }
    public void setAnioInicio(Integer anioInicio) { this.anioInicio = anioInicio; }
    public Integer getAnioFin() { return anioFin; }
    public void setAnioFin(Integer anioFin) { this.anioFin = anioFin; }
    public List<EsquemaPinturaTr> getTraducciones() { return traducciones; }
    public void setTraducciones(List<EsquemaPinturaTr> traducciones) { this.traducciones = traducciones; }
    
    public void addTraduccion(EsquemaPinturaTr traduccion) {
        this.traducciones.add(traduccion);
    }
}
