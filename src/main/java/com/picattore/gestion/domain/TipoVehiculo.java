package com.picattore.gestion.domain;

import java.util.ArrayList;
import java.util.List;

public class TipoVehiculo {
    private int idTipoVehiculo;
    private String codigo;
    private boolean traccion;
    private List<TipoVehiculoTr> traducciones;

    public TipoVehiculo(int idTipoVehiculo, String codigo, boolean traccion) {
        this.idTipoVehiculo = idTipoVehiculo;
        this.codigo = codigo;
        this.traccion = traccion;
        this.traducciones = new ArrayList<>();
    }

    public TipoVehiculo(String codigo, boolean traccion) {
        this.codigo = codigo;
        this.traccion = traccion;
        this.traducciones = new ArrayList<>();
    }

    // Constructor antiguo para compatibilidad si hay llamadas antiguas (aunque lo ideal es actualizar todo)
    public TipoVehiculo(int idTipoVehiculo, String codigo) {
        this(idTipoVehiculo, codigo, false);
    }
    
    public TipoVehiculo(String codigo) {
        this(codigo, false);
    }

    public int getIdTipoVehiculo() {
        return idTipoVehiculo;
    }

    public void setIdTipoVehiculo(int idTipoVehiculo) {
        this.idTipoVehiculo = idTipoVehiculo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public boolean isTraccion() {
        return traccion;
    }

    public void setTraccion(boolean traccion) {
        this.traccion = traccion;
    }

    public List<TipoVehiculoTr> getTraducciones() {
        return traducciones;
    }

    public void setTraducciones(List<TipoVehiculoTr> traducciones) {
        this.traducciones = traducciones;
    }

    public void addTraduccion(TipoVehiculoTr traduccion) {
        this.traducciones.add(traduccion);
    }
}
