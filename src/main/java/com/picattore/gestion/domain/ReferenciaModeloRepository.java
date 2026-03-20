package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface ReferenciaModeloRepository {
    void guardar(ReferenciaModelo referencia);
    Optional<ReferenciaModelo> buscarPorId(int id);
    List<ReferenciaModelo> buscarTodos();
    void actualizar(ReferenciaModelo referencia);
    void eliminar(int id);
}
