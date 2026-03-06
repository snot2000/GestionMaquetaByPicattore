package com.picattore.gestion.domain;

public class TipoVehiculoTr {
    private int id;
    private int idTipoVehiculo;
    private int idIdioma;
    private String nombre;
    private String descripcion;

    public TipoVehiculoTr(int id, int idTipoVehiculo, int idIdioma, String nombre, String descripcion) {
        this.id = id;
        this.idTipoVehiculo = idTipoVehiculo;
        this.idIdioma = idIdioma;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public TipoVehiculoTr(int idIdioma, String nombre, String descripcion) {
        this.idIdioma = idIdioma;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTipoVehiculo() {
        return idTipoVehiculo;
    }

    public void setIdTipoVehiculo(int idTipoVehiculo) {
        this.idTipoVehiculo = idTipoVehiculo;
    }

    public int getIdIdioma() {
        return idIdioma;
    }

    public void setIdIdioma(int idIdioma) {
        this.idIdioma = idIdioma;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
