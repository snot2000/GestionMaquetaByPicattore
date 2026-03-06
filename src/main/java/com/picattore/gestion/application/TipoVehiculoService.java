package com.picattore.gestion.application;

import com.picattore.gestion.domain.TipoVehiculo;
import com.picattore.gestion.domain.TipoVehiculoRepository;
import com.picattore.gestion.domain.TipoVehiculoTr;

import java.util.List;
import java.util.Optional;

public class TipoVehiculoService {
    private final TipoVehiculoRepository tipoVehiculoRepository;

    public TipoVehiculoService(TipoVehiculoRepository tipoVehiculoRepository) {
        this.tipoVehiculoRepository = tipoVehiculoRepository;
    }

    public void crearTipoVehiculo(String codigo, List<TipoVehiculoTr> traducciones) {
        TipoVehiculo nuevoTipo = new TipoVehiculo(codigo);
        nuevoTipo.setTraducciones(traducciones);
        tipoVehiculoRepository.guardar(nuevoTipo);
    }

    public List<TipoVehiculo> obtenerTodosLosTiposVehiculo() {
        return tipoVehiculoRepository.buscarTodos();
    }

    public Optional<TipoVehiculo> obtenerTipoVehiculoPorId(int id) {
        return tipoVehiculoRepository.buscarPorId(id);
    }

    public void actualizarTipoVehiculo(int id, String codigo, List<TipoVehiculoTr> traducciones) {
        Optional<TipoVehiculo> tipoExistente = tipoVehiculoRepository.buscarPorId(id);
        if (tipoExistente.isPresent()) {
            TipoVehiculo tipo = tipoExistente.get();
            tipo.setCodigo(codigo);
            tipo.setTraducciones(traducciones);
            tipoVehiculoRepository.actualizar(tipo);
        } else {
            System.err.println("Tipo de vehículo con ID " + id + " no encontrado.");
        }
    }

    public void eliminarTipoVehiculo(int id) {
        tipoVehiculoRepository.eliminar(id);
    }
}
