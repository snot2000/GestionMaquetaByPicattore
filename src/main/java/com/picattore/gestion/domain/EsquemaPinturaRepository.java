package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface EsquemaPinturaRepository {
    void guardar(EsquemaPintura esquema);
    Optional<EsquemaPintura> buscarPorId(int id);
    List<EsquemaPintura> buscarTodos();
    void actualizar(EsquemaPintura esquema);
    void eliminar(int id);
}
