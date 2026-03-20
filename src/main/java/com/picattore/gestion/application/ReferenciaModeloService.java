package com.picattore.gestion.application;

import com.picattore.gestion.domain.ReferenciaModelo;
import com.picattore.gestion.domain.ReferenciaModeloRepository;

import java.util.List;
import java.util.Optional;

public class ReferenciaModeloService {
    private final ReferenciaModeloRepository referenciaRepository;

    public ReferenciaModeloService(ReferenciaModeloRepository referenciaRepository) {
        this.referenciaRepository = referenciaRepository;
    }

    public void crearReferencia(Integer idFabricante, String referencia, Integer idVehiculoReal, Integer idEscala, String fechaSalida, String fechaDescontinuado) {
        ReferenciaModelo nuevaReferencia = new ReferenciaModelo(idFabricante, referencia, idVehiculoReal, idEscala, fechaSalida, fechaDescontinuado);
        referenciaRepository.guardar(nuevaReferencia);
    }

    public List<ReferenciaModelo> obtenerTodasLasReferencias() {
        return referenciaRepository.buscarTodos();
    }

    public Optional<ReferenciaModelo> obtenerReferenciaPorId(int id) {
        return referenciaRepository.buscarPorId(id);
    }

    public void actualizarReferencia(int id, Integer idFabricante, String referencia, Integer idVehiculoReal, Integer idEscala, String fechaSalida, String fechaDescontinuado) {
        Optional<ReferenciaModelo> referenciaExistente = referenciaRepository.buscarPorId(id);
        if (referenciaExistente.isPresent()) {
            ReferenciaModelo ref = referenciaExistente.get();
            ref.setIdFabricante(idFabricante);
            ref.setReferencia(referencia);
            ref.setIdVehiculoReal(idVehiculoReal);
            ref.setIdEscala(idEscala);
            ref.setFechaSalida(fechaSalida);
            ref.setFechaDescontinuado(fechaDescontinuado);
            referenciaRepository.actualizar(ref);
        } else {
            System.err.println("Referencia con ID " + id + " no encontrada.");
        }
    }

    public void eliminarReferencia(int id) {
        referenciaRepository.eliminar(id);
    }
}
