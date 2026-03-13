package com.picattore.gestion.domain;

public class Fabricante {
    private int idFabricante;
    private String nombre;
    private String descripcion;
    private Integer idPais;
    private String paginaWeb;
    private String telefono;
    private String email;
    private String fechaAlta;
    private String fechaBaja;

    public Fabricante(String nombre, String descripcion, Integer idPais, String paginaWeb, String telefono, String email, String fechaAlta, String fechaBaja) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idPais = idPais;
        this.paginaWeb = paginaWeb;
        this.telefono = telefono;
        this.email = email;
        this.fechaAlta = fechaAlta;
        this.fechaBaja = fechaBaja;
    }

    public Fabricante(int idFabricante, String nombre, String descripcion, Integer idPais, String paginaWeb, String telefono, String email, String fechaAlta, String fechaBaja) {
        this(nombre, descripcion, idPais, paginaWeb, telefono, email, fechaAlta, fechaBaja);
        this.idFabricante = idFabricante;
    }

    // Getters y Setters
    public int getIdFabricante() { return idFabricante; }
    public void setIdFabricante(int idFabricante) { this.idFabricante = idFabricante; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getIdPais() { return idPais; }
    public void setIdPais(Integer idPais) { this.idPais = idPais; }
    public String getPaginaWeb() { return paginaWeb; }
    public void setPaginaWeb(String paginaWeb) { this.paginaWeb = paginaWeb; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(String fechaAlta) { this.fechaAlta = fechaAlta; }
    public String getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(String fechaBaja) { this.fechaBaja = fechaBaja; }

    @Override
    public String toString() {
        return nombre;
    }
}
