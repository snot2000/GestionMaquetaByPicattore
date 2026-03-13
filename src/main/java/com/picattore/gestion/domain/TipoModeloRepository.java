package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface TipoModeloRepository {
    void guardar(TipoModelo tipoModelo);
    Optional<TipoModelo> buscarPorId(int id);
    List<TipoModelo> buscarTodos();
    void actualizar(TipoModelo tipoModelo);
    void eliminar(int id);
}
