package com.picattore.gestion.domain;

import java.util.ArrayList;
import java.util.List;

public class Decoder {
    private int id;
    private Integer idFabricante;
    private String direccion;
    private boolean compCarga;
    private boolean sonido;
    private String tipoConector;

    private List<DecoCV> cvs;
    private List<DecoFuncion> funciones;

    public Decoder(Integer idFabricante, String direccion, boolean compCarga, boolean sonido, String tipoConector) {
        this.idFabricante = idFabricante;
        this.direccion = direccion;
        this.compCarga = compCarga;
        this.sonido = sonido;
        this.tipoConector = tipoConector;
        this.cvs = new ArrayList<>();
        this.funciones = new ArrayList<>();
    }

    public Decoder(int id, Integer idFabricante, String direccion, boolean compCarga, boolean sonido, String tipoConector) {
        this(idFabricante, direccion, compCarga, sonido, tipoConector);
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getIdFabricante() { return idFabricante; }
    public void setIdFabricante(Integer idFabricante) { this.idFabricante = idFabricante; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public boolean isCompCarga() { return compCarga; }
    public void setCompCarga(boolean compCarga) { this.compCarga = compCarga; }
    public boolean isSonido() { return sonido; }
    public void setSonido(boolean sonido) { this.sonido = sonido; }
    public String getTipoConector() { return tipoConector; }
    public void setTipoConector(String tipoConector) { this.tipoConector = tipoConector; }
    public List<DecoCV> getCvs() { return cvs; }
    public void setCvs(List<DecoCV> cvs) { this.cvs = cvs; }
    public List<DecoFuncion> getFunciones() { return funciones; }
    public void setFunciones(List<DecoFuncion> funciones) { this.funciones = funciones; }
    
    public void addCv(DecoCV cv) { this.cvs.add(cv); }
    public void addFuncion(DecoFuncion funcion) { this.funciones.add(funcion); }
}
