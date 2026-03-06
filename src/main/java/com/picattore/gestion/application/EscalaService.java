package com.picattore.gestion.application;

import com.picattore.gestion.domain.Escala;
import com.picattore.gestion.domain.EscalaRepository;

import java.util.List;
import java.util.Optional;

public class EscalaService {
    private final EscalaRepository escalaRepository;

    public EscalaService(EscalaRepository escalaRepository) {
        this.escalaRepository = escalaRepository;
    }

    public void crearEscala(String codigo, String escala) {
        Escala nuevaEscala = new Escala(codigo, escala);
        escalaRepository.guardar(nuevaEscala);
    }

    public List<Escala> obtenerTodasLasEscalas() {
        return escalaRepository.buscarTodas();
    }

    public Optional<Escala> obtenerEscalaPorId(int id) {
        return escalaRepository.buscarPorId(id);
    }

    public void actualizarEscala(int id, String codigo, String escala) {
        Optional<Escala> escalaExistente = escalaRepository.buscarPorId(id);
        if (escalaExistente.isPresent()) {
            Escala esc = escalaExistente.get();
            esc.setCodigo(codigo);
            esc.setEscala(escala);
            escalaRepository.actualizar(esc);
        } else {
            System.err.println("Escala con ID " + id + " no encontrada.");
        }
    }

    public void eliminarEscala(int id) {
        escalaRepository.eliminar(id);
    }
}
