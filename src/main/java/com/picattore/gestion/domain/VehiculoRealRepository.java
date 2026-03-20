package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface VehiculoRealRepository {
    void guardar(VehiculoReal vehiculoReal);
    Optional<VehiculoReal> buscarPorId(int id);
    List<VehiculoReal> buscarTodos();
    void actualizar(VehiculoReal vehiculoReal);
    void eliminar(int id);
}
