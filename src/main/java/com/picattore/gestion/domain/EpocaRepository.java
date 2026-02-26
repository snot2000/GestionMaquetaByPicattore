package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface EpocaRepository {
    void guardar(Epoca epoca);
    Optional<Epoca> buscarPorId(int id);
    List<Epoca> buscarTodas();
    void actualizar(Epoca epoca);
    void eliminar(int id);
}
