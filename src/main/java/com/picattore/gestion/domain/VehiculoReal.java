package com.picattore.gestion.domain;

public class VehiculoReal {
    private int id;
    private String nombre;
    private String apodo;
    private String numeracion;
    private String uid;
    private Integer idTipoVehiculo;
    private Integer idPais;
    private Integer idEpoca;
    private Integer idEsquemaPintura;
    private Integer idOperadora;

    public VehiculoReal(String nombre, String apodo, String numeracion, String uid, Integer idTipoVehiculo, Integer idPais, Integer idEpoca, Integer idEsquemaPintura, Integer idOperadora) {
        this.nombre = nombre;
        this.apodo = apodo;
        this.numeracion = numeracion;
        this.uid = uid;
        this.idTipoVehiculo = idTipoVehiculo;
        this.idPais = idPais;
        this.idEpoca = idEpoca;
        this.idEsquemaPintura = idEsquemaPintura;
        this.idOperadora = idOperadora;
    }

    public VehiculoReal(int id, String nombre, String apodo, String numeracion, String uid, Integer idTipoVehiculo, Integer idPais, Integer idEpoca, Integer idEsquemaPintura, Integer idOperadora) {
        this(nombre, apodo, numeracion, uid, idTipoVehiculo, idPais, idEpoca, idEsquemaPintura, idOperadora);
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApodo() { return apodo; }
    public void setApodo(String apodo) { this.apodo = apodo; }
    public String getNumeracion() { return numeracion; }
    public void setNumeracion(String numeracion) { this.numeracion = numeracion; }
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public Integer getIdTipoVehiculo() { return idTipoVehiculo; }
    public void setIdTipoVehiculo(Integer idTipoVehiculo) { this.idTipoVehiculo = idTipoVehiculo; }
    public Integer getIdPais() { return idPais; }
    public void setIdPais(Integer idPais) { this.idPais = idPais; }
    public Integer getIdEpoca() { return idEpoca; }
    public void setIdEpoca(Integer idEpoca) { this.idEpoca = idEpoca; }
    public Integer getIdEsquemaPintura() { return idEsquemaPintura; }
    public void setIdEsquemaPintura(Integer idEsquemaPintura) { this.idEsquemaPintura = idEsquemaPintura; }
    public Integer getIdOperadora() { return idOperadora; }
    public void setIdOperadora(Integer idOperadora) { this.idOperadora = idOperadora; }
}
