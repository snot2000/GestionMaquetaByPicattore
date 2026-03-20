package com.picattore.gestion.domain;

public class ReferenciaModelo {
    private int id;
    private Integer idFabricante;
    private String referencia;
    private Integer idVehiculoReal;
    private Integer idEscala;
    private String fechaSalida;
    private String fechaDescontinuado;

    public ReferenciaModelo(Integer idFabricante, String referencia, Integer idVehiculoReal, Integer idEscala, String fechaSalida, String fechaDescontinuado) {
        this.idFabricante = idFabricante;
        this.referencia = referencia;
        this.idVehiculoReal = idVehiculoReal;
        this.idEscala = idEscala;
        this.fechaSalida = fechaSalida;
        this.fechaDescontinuado = fechaDescontinuado;
    }

    public ReferenciaModelo(int id, Integer idFabricante, String referencia, Integer idVehiculoReal, Integer idEscala, String fechaSalida, String fechaDescontinuado) {
        this(idFabricante, referencia, idVehiculoReal, idEscala, fechaSalida, fechaDescontinuado);
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getIdFabricante() { return idFabricante; }
    public void setIdFabricante(Integer idFabricante) { this.idFabricante = idFabricante; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public Integer getIdVehiculoReal() { return idVehiculoReal; }
    public void setIdVehiculoReal(Integer idVehiculoReal) { this.idVehiculoReal = idVehiculoReal; }
    public Integer getIdEscala() { return idEscala; }
    public void setIdEscala(Integer idEscala) { this.idEscala = idEscala; }
    public String getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(String fechaSalida) { this.fechaSalida = fechaSalida; }
    public String getFechaDescontinuado() { return fechaDescontinuado; }
    public void setFechaDescontinuado(String fechaDescontinuado) { this.fechaDescontinuado = fechaDescontinuado; }
}
