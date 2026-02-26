package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface IdiomaRepository {
    void guardar(Idioma idioma);
    Optional<Idioma> buscarPorId(int id);
    List<Idioma> buscarTodos();
    void actualizar(Idioma idioma);
    void eliminar(int id);
}
