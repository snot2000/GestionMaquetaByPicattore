package com.picattore.gestion.domain;

import java.util.ArrayList;
import java.util.List;

public class Pais {
    private int idPais;
    private String codigo;
    private List<PaisTr> traducciones;

    public Pais(int idPais, String codigo) {
        this.idPais = idPais;
        this.codigo = codigo;
        this.traducciones = new ArrayList<>();
    }

    public Pais(String codigo) {
        this.codigo = codigo;
        this.traducciones = new ArrayList<>();
    }

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<PaisTr> getTraducciones() {
        return traducciones;
    }

    public void setTraducciones(List<PaisTr> traducciones) {
        this.traducciones = traducciones;
    }

    public void addTraduccion(PaisTr traduccion) {
        this.traducciones.add(traduccion);
    }

    @Override
    public String toString() {
        // El renderer de la UI se encargará de mostrar el nombre traducido.
        // Esto es un fallback y útil para depuración.
        return codigo != null ? codigo : "País sin código";
    }
}
