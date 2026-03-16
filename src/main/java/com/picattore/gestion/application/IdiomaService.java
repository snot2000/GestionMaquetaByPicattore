package com.picattore.gestion.application;

import com.picattore.gestion.domain.Idioma;
import com.picattore.gestion.domain.IdiomaRepository;

import java.util.List;
import java.util.Optional;

public class IdiomaService {
    private final IdiomaRepository idiomaRepository;

    public IdiomaService(IdiomaRepository idiomaRepository) {
        this.idiomaRepository = idiomaRepository;
    }

    public void crearIdioma(String codigo, String nombre, boolean principal) {
        Idioma nuevoIdioma = new Idioma(codigo, nombre, principal);
        idiomaRepository.guardar(nuevoIdioma);
    }

    public List<Idioma> obtenerTodosLosIdiomas() {
        return idiomaRepository.buscarTodos();
    }

    public Optional<Idioma> obtenerIdiomaPorId(int id) {
        return idiomaRepository.buscarPorId(id);
    }

    public Optional<Idioma> obtenerIdiomaPrincipal() {
        return idiomaRepository.buscarTodos().stream()
                .filter(Idioma::isPrincipal)
                .findFirst();
    }

    public void actualizarIdioma(int id, String codigo, String nombre, boolean principal) {
        Optional<Idioma> idiomaExistente = idiomaRepository.buscarPorId(id);
        if (idiomaExistente.isPresent()) {
            Idioma idioma = idiomaExistente.get();
            idioma.setCodigo(codigo);
            idioma.setNombre(nombre);
            idioma.setPrincipal(principal);
            idiomaRepository.actualizar(idioma);
        } else {
            System.err.println("Idioma con ID " + id + " no encontrado.");
        }
    }

    public void eliminarIdioma(int id) {
        idiomaRepository.eliminar(id);
    }
}
