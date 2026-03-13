package com.picattore.gestion.application;

import com.picattore.gestion.domain.Fabricante;
import com.picattore.gestion.domain.FabricanteRepository;

import java.util.List;
import java.util.Optional;

public class FabricanteService {
    private final FabricanteRepository fabricanteRepository;

    public FabricanteService(FabricanteRepository fabricanteRepository) {
        this.fabricanteRepository = fabricanteRepository;
    }

    public void crearFabricante(String nombre, String descripcion, Integer idPais, String paginaWeb, String telefono, String email, String fechaAlta, String fechaBaja) {
        Fabricante nuevoFabricante = new Fabricante(nombre, descripcion, idPais, paginaWeb, telefono, email, fechaAlta, fechaBaja);
        fabricanteRepository.guardar(nuevoFabricante);
    }

    public List<Fabricante> obtenerTodosLosFabricantes() {
        return fabricanteRepository.buscarTodos();
    }

    public Optional<Fabricante> obtenerFabricantePorId(int id) {
        return fabricanteRepository.buscarPorId(id);
    }

    public void actualizarFabricante(int id, String nombre, String descripcion, Integer idPais, String paginaWeb, String telefono, String email, String fechaAlta, String fechaBaja) {
        Optional<Fabricante> fabricanteExistente = fabricanteRepository.buscarPorId(id);
        if (fabricanteExistente.isPresent()) {
            Fabricante fabricante = fabricanteExistente.get();
            fabricante.setNombre(nombre);
            fabricante.setDescripcion(descripcion);
            fabricante.setIdPais(idPais);
            fabricante.setPaginaWeb(paginaWeb);
            fabricante.setTelefono(telefono);
            fabricante.setEmail(email);
            fabricante.setFechaAlta(fechaAlta);
            fabricante.setFechaBaja(fechaBaja);
            fabricanteRepository.actualizar(fabricante);
        } else {
            System.err.println("Fabricante con ID " + id + " no encontrado.");
        }
    }

    public void eliminarFabricante(int id) {
        fabricanteRepository.eliminar(id);
    }
}
