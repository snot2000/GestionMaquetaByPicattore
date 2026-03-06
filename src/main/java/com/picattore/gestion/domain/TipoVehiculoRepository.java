package com.picattore.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface TipoVehiculoRepository {
    void guardar(TipoVehiculo tipoVehiculo);
    Optional<TipoVehiculo> buscarPorId(int id);
    List<TipoVehiculo> buscarTodos();
    void actualizar(TipoVehiculo tipoVehiculo);
    void eliminar(int id);
}
