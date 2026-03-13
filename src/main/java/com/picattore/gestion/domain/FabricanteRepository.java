package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface FabricanteRepository {
    void guardar(Fabricante fabricante);
    Optional<Fabricante> buscarPorId(int id);
    List<Fabricante> buscarTodos();
    void actualizar(Fabricante fabricante);
    void eliminar(int id);
}
