package com.picattore.gestion.application;

import com.picattore.gestion.domain.VehiculoReal;
import com.picattore.gestion.domain.VehiculoRealRepository;

import java.util.List;
import java.util.Optional;

public class VehiculoRealService {
    private final VehiculoRealRepository vehiculoRealRepository;

    public VehiculoRealService(VehiculoRealRepository vehiculoRealRepository) {
        this.vehiculoRealRepository = vehiculoRealRepository;
    }

    public void crearVehiculoReal(String nombre, String apodo, String numeracion, String uid, Integer idTipoVehiculo, Integer idPais, Integer idEpoca, Integer idEsquemaPintura, Integer idOperadora) {
        VehiculoReal nuevoVehiculo = new VehiculoReal(nombre, apodo, numeracion, uid, idTipoVehiculo, idPais, idEpoca, idEsquemaPintura, idOperadora);
        vehiculoRealRepository.guardar(nuevoVehiculo);
    }

    public List<VehiculoReal> obtenerTodosLosVehiculosReales() {
        return vehiculoRealRepository.buscarTodos();
    }

    public Optional<VehiculoReal> obtenerVehiculoRealPorId(int id) {
        return vehiculoRealRepository.buscarPorId(id);
    }

    public void actualizarVehiculoReal(int id, String nombre, String apodo, String numeracion, String uid, Integer idTipoVehiculo, Integer idPais, Integer idEpoca, Integer idEsquemaPintura, Integer idOperadora) {
        Optional<VehiculoReal> vehiculoExistente = vehiculoRealRepository.buscarPorId(id);
        if (vehiculoExistente.isPresent()) {
            VehiculoReal vehiculo = vehiculoExistente.get();
            vehiculo.setNombre(nombre);
            vehiculo.setApodo(apodo);
            vehiculo.setNumeracion(numeracion);
            vehiculo.setUid(uid);
            vehiculo.setIdTipoVehiculo(idTipoVehiculo);
            vehiculo.setIdPais(idPais);
            vehiculo.setIdEpoca(idEpoca);
            vehiculo.setIdEsquemaPintura(idEsquemaPintura);
            vehiculo.setIdOperadora(idOperadora);
            vehiculoRealRepository.actualizar(vehiculo);
        } else {
            System.err.println("Vehículo Real con ID " + id + " no encontrado.");
        }
    }

    public void eliminarVehiculoReal(int id) {
        vehiculoRealRepository.eliminar(id);
    }
}
