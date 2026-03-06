package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface EscalaRepository {
    void guardar(Escala escala);
    Optional<Escala> buscarPorId(int id);
    List<Escala> buscarTodas();
    void actualizar(Escala escala);
    void eliminar(int id);
}
