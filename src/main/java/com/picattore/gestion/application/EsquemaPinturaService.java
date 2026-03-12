package com.picattore.gestion.application;

import com.picattore.gestion.domain.EsquemaPintura;
import com.picattore.gestion.domain.EsquemaPinturaRepository;
import com.picattore.gestion.domain.EsquemaPinturaTr;

import java.util.List;
import java.util.Optional;

public class EsquemaPinturaService {
    private final EsquemaPinturaRepository esquemaPinturaRepository;

    public EsquemaPinturaService(EsquemaPinturaRepository esquemaPinturaRepository) {
        this.esquemaPinturaRepository = esquemaPinturaRepository;
    }

    public void crearEsquema(int idPais, int idOperadora, String nombre, Integer anioInicio, Integer anioFin, List<EsquemaPinturaTr> traducciones) {
        EsquemaPintura nuevoEsquema = new EsquemaPintura(idPais, idOperadora, nombre, anioInicio, anioFin);
        nuevoEsquema.setTraducciones(traducciones);
        esquemaPinturaRepository.guardar(nuevoEsquema);
    }

    public List<EsquemaPintura> obtenerTodosLosEsquemas() {
        return esquemaPinturaRepository.buscarTodos();
    }

    public Optional<EsquemaPintura> obtenerEsquemaPorId(int id) {
        return esquemaPinturaRepository.buscarPorId(id);
    }

    public void actualizarEsquema(int id, int idPais, int idOperadora, String nombre, Integer anioInicio, Integer anioFin, List<EsquemaPinturaTr> traducciones) {
        Optional<EsquemaPintura> esquemaExistente = esquemaPinturaRepository.buscarPorId(id);
        if (esquemaExistente.isPresent()) {
            EsquemaPintura esquema = esquemaExistente.get();
            esquema.setIdPais(idPais);
            esquema.setIdOperadora(idOperadora);
            esquema.setNombre(nombre);
            esquema.setAnioInicio(anioInicio);
            esquema.setAnioFin(anioFin);
            esquema.setTraducciones(traducciones);
            esquemaPinturaRepository.actualizar(esquema);
        } else {
            System.err.println("Esquema de pintura con ID " + id + " no encontrado.");
        }
    }

    public void eliminarEsquema(int id) {
        esquemaPinturaRepository.eliminar(id);
    }
}
