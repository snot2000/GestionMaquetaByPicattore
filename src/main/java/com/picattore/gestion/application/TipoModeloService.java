package com.picattore.gestion.application;

import com.picattore.gestion.domain.TipoModelo;
import com.picattore.gestion.domain.TipoModeloRepository;
import com.picattore.gestion.domain.TipoModeloTr;

import java.util.List;
import java.util.Optional;

public class TipoModeloService {
    private final TipoModeloRepository tipoModeloRepository;

    public TipoModeloService(TipoModeloRepository tipoModeloRepository) {
        this.tipoModeloRepository = tipoModeloRepository;
    }

    public void crearTipoModelo(String codigo, List<TipoModeloTr> traducciones) {
        TipoModelo nuevoTipo = new TipoModelo(codigo);
        nuevoTipo.setTraducciones(traducciones);
        tipoModeloRepository.guardar(nuevoTipo);
    }

    public List<TipoModelo> obtenerTodosLosTiposModelo() {
        return tipoModeloRepository.buscarTodos();
    }

    public Optional<TipoModelo> obtenerTipoModeloPorId(int id) {
        return tipoModeloRepository.buscarPorId(id);
    }

    public void actualizarTipoModelo(int id, String codigo, List<TipoModeloTr> traducciones) {
        Optional<TipoModelo> tipoExistente = tipoModeloRepository.buscarPorId(id);
        if (tipoExistente.isPresent()) {
            TipoModelo tipo = tipoExistente.get();
            tipo.setCodigo(codigo);
            tipo.setTraducciones(traducciones);
            tipoModeloRepository.actualizar(tipo);
        } else {
            System.err.println("Tipo de modelo con ID " + id + " no encontrado.");
        }
    }

    public void eliminarTipoModelo(int id) {
        tipoModeloRepository.eliminar(id);
    }
}
