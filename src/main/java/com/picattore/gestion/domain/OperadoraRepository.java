package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface OperadoraRepository {
    void guardar(Operadora operadora);
    Optional<Operadora> buscarPorId(int id);
    List<Operadora> buscarTodas();
    void actualizar(Operadora operadora);
    void eliminar(int id);
}
