package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface ModeloRepository {
    void guardar(Modelo modelo);
    Optional<Modelo> buscarPorId(int id);
    List<Modelo> buscarTodos();
    void actualizar(Modelo modelo);
    void eliminar(int id);
}
