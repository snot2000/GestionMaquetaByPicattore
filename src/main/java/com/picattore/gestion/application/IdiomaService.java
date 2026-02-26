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

    public void crearIdioma(String codigo, String nombre) {
        Idioma nuevoIdioma = new Idioma(codigo, nombre);
        idiomaRepository.guardar(nuevoIdioma);
    }

    public List<Idioma> obtenerTodosLosIdiomas() {
        return idiomaRepository.buscarTodos();
    }

    public Optional<Idioma> obtenerIdiomaPorId(int id) {
        return idiomaRepository.buscarPorId(id);
    }

    public void actualizarIdioma(int id, String codigo, String nombre) {
        Optional<Idioma> idiomaExistente = idiomaRepository.buscarPorId(id);
        if (idiomaExistente.isPresent()) {
            Idioma idioma = idiomaExistente.get();
            idioma.setCodigo(codigo);
            idioma.setNombre(nombre);
            idiomaRepository.actualizar(idioma);
        } else {
            // Manejar el caso de que no exista, lanzar excepción, etc.
            System.err.println("Idioma con ID " + id + " no encontrado.");
        }
    }

    public void eliminarIdioma(int id) {
        // Aquí podrías añadir la lógica de verificación de referencias antes de eliminar
        idiomaRepository.eliminar(id);
    }
}
