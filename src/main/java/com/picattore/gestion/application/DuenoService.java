package com.picattore.gestion.application;

import com.picattore.gestion.domain.Dueno;
import com.picattore.gestion.domain.DuenoRepository;

import java.util.List;
import java.util.Optional;

public class DuenoService {
    private final DuenoRepository duenoRepository;

    public DuenoService(DuenoRepository duenoRepository) {
        this.duenoRepository = duenoRepository;
    }

    public void crearDueno(String nombre) {
        Dueno nuevoDueno = new Dueno(nombre);
        duenoRepository.guardar(nuevoDueno);
    }

    public List<Dueno> obtenerTodosLosDuenos() {
        return duenoRepository.buscarTodos();
    }

    public Optional<Dueno> obtenerDuenoPorId(int id) {
        return duenoRepository.buscarPorId(id);
    }

    public void actualizarDueno(int id, String nombre) {
        Optional<Dueno> duenoExistente = duenoRepository.buscarPorId(id);
        if (duenoExistente.isPresent()) {
            Dueno dueno = duenoExistente.get();
            dueno.setNombre(nombre);
            duenoRepository.actualizar(dueno);
        } else {
            System.err.println("Dueño con ID " + id + " no encontrado.");
        }
    }

    public void eliminarDueno(int id) {
        duenoRepository.eliminar(id);
    }
}
