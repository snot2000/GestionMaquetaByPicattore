package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface DuenoRepository {
    void guardar(Dueno dueno);
    Optional<Dueno> buscarPorId(int id);
    List<Dueno> buscarTodos();
    void actualizar(Dueno dueno);
    void eliminar(int id);
}
