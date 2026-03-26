package com.picattore.gestion.domain;

public class Modelo {
    private int id;
    private Integer idDecoder;
    private Integer idReferenciaModelo;
    private Integer idDueno;

    public Modelo(Integer idDecoder, Integer idReferenciaModelo, Integer idDueno) {
        this.idDecoder = idDecoder;
        this.idReferenciaModelo = idReferenciaModelo;
        this.idDueno = idDueno;
    }

    public Modelo(int id, Integer idDecoder, Integer idReferenciaModelo, Integer idDueno) {
        this(idDecoder, idReferenciaModelo, idDueno);
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getIdDecoder() { return idDecoder; }
    public void setIdDecoder(Integer idDecoder) { this.idDecoder = idDecoder; }
    public Integer getIdReferenciaModelo() { return idReferenciaModelo; }
    public void setIdReferenciaModelo(Integer idReferenciaModelo) { this.idReferenciaModelo = idReferenciaModelo; }
    public Integer getIdDueno() { return idDueno; }
    public void setIdDueno(Integer idDueno) { this.idDueno = idDueno; }
}
