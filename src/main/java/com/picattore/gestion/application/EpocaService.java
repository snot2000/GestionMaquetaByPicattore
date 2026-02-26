package com.picattore.gestion.application;

import com.picattore.gestion.domain.Epoca;
import com.picattore.gestion.domain.EpocaRepository;
import com.picattore.gestion.domain.EpocaTr;

import java.util.List;
import java.util.Optional;

public class EpocaService {
    private final EpocaRepository epocaRepository;

    public EpocaService(EpocaRepository epocaRepository) {
        this.epocaRepository = epocaRepository;
    }

    public void crearEpoca(String codigo, int anioInicio, int anioFin, List<EpocaTr> traducciones) {
        Epoca nuevaEpoca = new Epoca(codigo, anioInicio, anioFin);
        nuevaEpoca.setTraducciones(traducciones);
        epocaRepository.guardar(nuevaEpoca);
    }

    public List<Epoca> obtenerTodasLasEpocas() {
        return epocaRepository.buscarTodas();
    }

    public Optional<Epoca> obtenerEpocaPorId(int id) {
        return epocaRepository.buscarPorId(id);
    }

    public void actualizarEpoca(int id, String codigo, int anioInicio, int anioFin, List<EpocaTr> traducciones) {
        Optional<Epoca> epocaExistente = epocaRepository.buscarPorId(id);
        if (epocaExistente.isPresent()) {
            Epoca epoca = epocaExistente.get();
            epoca.setCodigo(codigo);
            epoca.setAnioInicio(anioInicio);
            epoca.setAnioFin(anioFin);
            epoca.setTraducciones(traducciones);
            epocaRepository.actualizar(epoca);
        } else {
            System.err.println("Época con ID " + id + " no encontrada.");
        }
    }

    public void eliminarEpoca(int id) {
        epocaRepository.eliminar(id);
    }
}
