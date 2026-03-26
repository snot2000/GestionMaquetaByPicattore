package com.picattore.gestion.application;

import com.picattore.gestion.domain.DecoCV;
import com.picattore.gestion.domain.DecoFuncion;
import com.picattore.gestion.domain.Decoder;
import com.picattore.gestion.domain.DecoderRepository;

import java.util.List;
import java.util.Optional;

public class DecoderService {
    private final DecoderRepository decoderRepository;

    public DecoderService(DecoderRepository decoderRepository) {
        this.decoderRepository = decoderRepository;
    }

    public void crearDecoder(Integer idFabricante, String direccion, boolean compCarga, boolean sonido, String tipoConector, List<DecoCV> cvs, List<DecoFuncion> funciones) {
        Decoder nuevoDecoder = new Decoder(idFabricante, direccion, compCarga, sonido, tipoConector);
        nuevoDecoder.setCvs(cvs);
        nuevoDecoder.setFunciones(funciones);
        decoderRepository.guardar(nuevoDecoder);
    }

    public List<Decoder> obtenerTodosLosDecoders() {
        return decoderRepository.buscarTodos();
    }

    public Optional<Decoder> obtenerDecoderPorId(int id) {
        return decoderRepository.buscarPorId(id);
    }

    public void actualizarDecoder(int id, Integer idFabricante, String direccion, boolean compCarga, boolean sonido, String tipoConector, List<DecoCV> cvs, List<DecoFuncion> funciones) {
        Optional<Decoder> decoderExistente = decoderRepository.buscarPorId(id);
        if (decoderExistente.isPresent()) {
            Decoder decoder = decoderExistente.get();
            decoder.setIdFabricante(idFabricante);
            decoder.setDireccion(direccion);
            decoder.setCompCarga(compCarga);
            decoder.setSonido(sonido);
            decoder.setTipoConector(tipoConector);
            decoder.setCvs(cvs);
            decoder.setFunciones(funciones);
            decoderRepository.actualizar(decoder);
        } else {
            System.err.println("Decoder con ID " + id + " no encontrado.");
        }
    }

    public void eliminarDecoder(int id) {
        decoderRepository.eliminar(id);
    }
}
