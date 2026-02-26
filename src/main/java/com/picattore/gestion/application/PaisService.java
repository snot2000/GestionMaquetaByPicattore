package com.picattore.gestion.application;

import com.picattore.gestion.domain.Pais;
import com.picattore.gestion.domain.PaisRepository;
import com.picattore.gestion.domain.PaisTr;

import java.util.List;
import java.util.Optional;

public class PaisService {
    private final PaisRepository paisRepository;

    public PaisService(PaisRepository paisRepository) {
        this.paisRepository = paisRepository;
    }

    public void crearPais(String codigo, List<PaisTr> traducciones) {
        Pais nuevoPais = new Pais(codigo);
        nuevoPais.setTraducciones(traducciones);
        paisRepository.guardar(nuevoPais);
    }

    public List<Pais> obtenerTodosLosPaises() {
        return paisRepository.buscarTodos();
    }

    public Optional<Pais> obtenerPaisPorId(int id) {
        return paisRepository.buscarPorId(id);
    }

    public void actualizarPais(int id, String codigo, List<PaisTr> traducciones) {
        Optional<Pais> paisExistente = paisRepository.buscarPorId(id);
        if (paisExistente.isPresent()) {
            Pais pais = paisExistente.get();
            pais.setCodigo(codigo);
            pais.setTraducciones(traducciones);
            paisRepository.actualizar(pais);
        } else {
            System.err.println("País con ID " + id + " no encontrado.");
        }
    }

    public void eliminarPais(int id) {
        paisRepository.eliminar(id);
    }
}
