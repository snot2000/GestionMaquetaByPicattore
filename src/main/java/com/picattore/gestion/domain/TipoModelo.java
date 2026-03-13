package com.picattore.gestion.domain;

import java.util.ArrayList;
import java.util.List;

public class TipoModelo {
    private int idTipoModelo;
    private String codigo;
    private List<TipoModeloTr> traducciones;

    public TipoModelo(int idTipoModelo, String codigo) {
        this.idTipoModelo = idTipoModelo;
        this.codigo = codigo;
        this.traducciones = new ArrayList<>();
    }

    public TipoModelo(String codigo) {
        this.codigo = codigo;
        this.traducciones = new ArrayList<>();
    }

    public int getIdTipoModelo() { return idTipoModelo; }
    public void setIdTipoModelo(int idTipoModelo) { this.idTipoModelo = idTipoModelo; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public List<TipoModeloTr> getTraducciones() { return traducciones; }
    public void setTraducciones(List<TipoModeloTr> traducciones) { this.traducciones = traducciones; }
    public void addTraduccion(TipoModeloTr traduccion) { this.traducciones.add(traduccion); }
}
