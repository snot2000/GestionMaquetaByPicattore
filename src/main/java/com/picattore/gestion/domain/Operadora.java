package com.picattore.gestion.domain;

import java.util.ArrayList;
import java.util.List;

public class Operadora {
    private int idOperadora;
    private String codigo;
    private String nombre;
    private String informacion;
    private Integer anioCreacion;
    private Integer anioDisolucion;

    private List<Integer> paisesIds;
    private List<Integer> predecesorasIds;
    private List<Integer> sucesorasIds;

    public Operadora(String codigo, String nombre, String informacion, Integer anioCreacion, Integer anioDisolucion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.informacion = informacion;
        this.anioCreacion = anioCreacion;
        this.anioDisolucion = anioDisolucion;
        this.paisesIds = new ArrayList<>();
        this.predecesorasIds = new ArrayList<>();
        this.sucesorasIds = new ArrayList<>();
    }

    public Operadora(int idOperadora, String codigo, String nombre, String informacion, Integer anioCreacion, Integer anioDisolucion) {
        this(codigo, nombre, informacion, anioCreacion, anioDisolucion);
        this.idOperadora = idOperadora;
    }

    // Getters y Setters
    public int getIdOperadora() { return idOperadora; }
    public void setIdOperadora(int idOperadora) { this.idOperadora = idOperadora; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getInformacion() { return informacion; }
    public void setInformacion(String informacion) { this.informacion = informacion; }
    public Integer getAnioCreacion() { return anioCreacion; }
    public void setAnioCreacion(Integer anioCreacion) { this.anioCreacion = anioCreacion; }
    public Integer getAnioDisolucion() { return anioDisolucion; }
    public void setAnioDisolucion(Integer anioDisolucion) { this.anioDisolucion = anioDisolucion; }
    public List<Integer> getPaisesIds() { return paisesIds; }
    public void setPaisesIds(List<Integer> paisesIds) { this.paisesIds = paisesIds; }
    public List<Integer> getPredecesorasIds() { return predecesorasIds; }
    public void setPredecesorasIds(List<Integer> predecesorasIds) { this.predecesorasIds = predecesorasIds; }
    public List<Integer> getSucesorasIds() { return sucesorasIds; }
    public void setSucesorasIds(List<Integer> sucesorasIds) { this.sucesorasIds = sucesorasIds; }

    @Override
    public String toString() {
        return nombre + " (" + codigo + ")";
    }
}
