package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface DecoderRepository {
    void guardar(Decoder decoder);
    Optional<Decoder> buscarPorId(int id);
    List<Decoder> buscarTodos();
    void actualizar(Decoder decoder);
    void eliminar(int id);
}
