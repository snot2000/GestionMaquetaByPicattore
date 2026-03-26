package com.picattore.gestion.application;

import com.picattore.gestion.domain.Modelo;
import com.picattore.gestion.domain.ModeloRepository;

import java.util.List;
import java.util.Optional;

public class ModeloService {
    private final ModeloRepository modeloRepository;

    public ModeloService(ModeloRepository modeloRepository) {
        this.modeloRepository = modeloRepository;
    }

    public void crearModelo(Integer idDecoder, Integer idReferenciaModelo, Integer idDueno) {
        Modelo nuevoModelo = new Modelo(idDecoder, idReferenciaModelo, idDueno);
        modeloRepository.guardar(nuevoModelo);
    }

    public List<Modelo> obtenerTodosLosModelos() {
        return modeloRepository.buscarTodos();
    }

    public Optional<Modelo> obtenerModeloPorId(int id) {
        return modeloRepository.buscarPorId(id);
    }

    public void actualizarModelo(int id, Integer idDecoder, Integer idReferenciaModelo, Integer idDueno) {
        Optional<Modelo> modeloExistente = modeloRepository.buscarPorId(id);
        if (modeloExistente.isPresent()) {
            Modelo modelo = modeloExistente.get();
            modelo.setIdDecoder(idDecoder);
            modelo.setIdReferenciaModelo(idReferenciaModelo);
            modelo.setIdDueno(idDueno);
            modeloRepository.actualizar(modelo);
        } else {
            System.err.println("Modelo con ID " + id + " no encontrado.");
        }
    }

    public void eliminarModelo(int id) {
        modeloRepository.eliminar(id);
    }
}
