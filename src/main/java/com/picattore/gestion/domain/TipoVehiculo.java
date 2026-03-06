package com.picattore.gestion.domain;

import java.util.ArrayList;
import java.util.List;

public class TipoVehiculo {
    private int idTipoVehiculo;
    private String codigo;
    private List<TipoVehiculoTr> traducciones;

    public TipoVehiculo(int idTipoVehiculo, String codigo) {
        this.idTipoVehiculo = idTipoVehiculo;
        this.codigo = codigo;
        this.traducciones = new ArrayList<>();
    }

    public TipoVehiculo(String codigo) {
        this.codigo = codigo;
        this.traducciones = new ArrayList<>();
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
