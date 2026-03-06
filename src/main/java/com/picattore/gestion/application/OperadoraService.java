package com.picattore.gestion.application;

import com.picattore.gestion.domain.Operadora;
import com.picattore.gestion.domain.OperadoraRepository;

import java.util.List;
import java.util.Optional;

public class OperadoraService {
    private final OperadoraRepository operadoraRepository;

    public OperadoraService(OperadoraRepository operadoraRepository) {
        this.operadoraRepository = operadoraRepository;
    }

    public void crearOperadora(String codigo, String nombre, String informacion, Integer anioCreacion, Integer anioDisolucion,
                               List<Integer> paisesIds, List<Integer> predecesorasIds, List<Integer> sucesorasIds) {
        Operadora nuevaOperadora = new Operadora(codigo, nombre, informacion, anioCreacion, anioDisolucion);
        nuevaOperadora.setPaisesIds(paisesIds);
        nuevaOperadora.setPredecesorasIds(predecesorasIds);
        nuevaOperadora.setSucesorasIds(sucesorasIds);
        operadoraRepository.guardar(nuevaOperadora);
    }

    public List<Operadora> obtenerTodasLasOperadoras() {
        return operadoraRepository.buscarTodas();
    }

    public Optional<Operadora> obtenerOperadoraPorId(int id) {
        return operadoraRepository.buscarPorId(id);
    }

    public void actualizarOperadora(int id, String codigo, String nombre, String informacion, Integer anioCreacion, Integer anioDisolucion,
                                    List<Integer> paisesIds, List<Integer> predecesorasIds, List<Integer> sucesorasIds) {
        Optional<Operadora> operadoraExistente = operadoraRepository.buscarPorId(id);
        if (operadoraExistente.isPresent()) {
            Operadora operadora = operadoraExistente.get();
            operadora.setCodigo(codigo);
            operadora.setNombre(nombre);
            operadora.setInformacion(informacion);
            operadora.setAnioCreacion(anioCreacion);
            operadora.setAnioDisolucion(anioDisolucion);
            operadora.setPaisesIds(paisesIds);
            operadora.setPredecesorasIds(predecesorasIds);
            operadora.setSucesorasIds(sucesorasIds);
            operadoraRepository.actualizar(operadora);
        } else {
            System.err.println("Operadora con ID " + id + " no encontrada.");
        }
    }

    public void eliminarOperadora(int id) {
        operadoraRepository.eliminar(id);
    }
}
