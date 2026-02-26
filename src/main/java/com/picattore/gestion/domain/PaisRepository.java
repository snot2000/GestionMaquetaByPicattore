package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface PaisRepository {
    void guardar(Pais pais);
    Optional<Pais> buscarPorId(int id);
    List<Pais> buscarTodos();
    void actualizar(Pais pais);
    void eliminar(int id);
}
