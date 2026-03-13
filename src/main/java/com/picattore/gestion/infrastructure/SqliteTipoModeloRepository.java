package com.picattore.gestion.infrastructure;

import com.picattore.gestion.domain.TipoModelo;
import com.picattore.gestion.domain.TipoModeloRepository;
import com.picattore.gestion.domain.TipoModeloTr;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteTipoModeloRepository implements TipoModeloRepository {

    @Override
    public void guardar(TipoModelo tipoModelo) {
        String sql = "INSERT INTO Tipo_modelo(codigo) VALUES(?)";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, tipoModelo.getCodigo());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tipoModelo.setIdTipoModelo(generatedKeys.getInt(1));
                    guardarTraducciones(tipoModelo, conn);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void guardarTraducciones(TipoModelo tipoModelo, Connection conn) throws SQLException {
        String sql = "INSERT INTO Tipo_modelo_tr(id_tipo_modelo, id_idioma, nombre, descripcion) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (TipoModeloTr tr : tipoModelo.getTraducciones()) {
                pstmt.setInt(1, tipoModelo.getIdTipoModelo());
                pstmt.setInt(2, tr.getIdIdioma());
                pstmt.setString(3, tr.getNombre());
                pstmt.setString(4, tr.getDescripcion());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    @Override
    public Optional<TipoModelo> buscarPorId(int id) {
        String sql = "SELECT id_tipo_modelo, codigo FROM Tipo_modelo WHERE id_tipo_modelo = ?";
        TipoModelo tipoModelo = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                tipoModelo = new TipoModelo(
                        rs.getInt("id_tipo_modelo"),
                        rs.getString("codigo")
                );
                cargarTraducciones(tipoModelo, conn);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(tipoModelo);
    }

    private void cargarTraducciones(TipoModelo tipoModelo, Connection conn) throws SQLException {
        String sql = "SELECT id, id_tipo_modelo, id_idioma, nombre, descripcion FROM Tipo_modelo_tr WHERE id_tipo_modelo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tipoModelo.getIdTipoModelo());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TipoModeloTr tr = new TipoModeloTr(
                        rs.getInt("id"),
                        rs.getInt("id_tipo_modelo"),
                        rs.getInt("id_idioma"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
                tipoModelo.addTraduccion(tr);
            }
        }
    }

    @Override
    public List<TipoModelo> buscarTodos() {
        String sql = "SELECT id_tipo_modelo, codigo FROM Tipo_modelo";
        List<TipoModelo> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TipoModelo tipoModelo = new TipoModelo(
                        rs.getInt("id_tipo_modelo"),
                        rs.getString("codigo")
                );
                cargarTraducciones(tipoModelo, conn);
                lista.add(tipoModelo);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(TipoModelo tipoModelo) {
        String sql = "UPDATE Tipo_modelo SET codigo = ? WHERE id_tipo_modelo = ?";

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, tipoModelo.getCodigo());
                pstmt.setInt(2, tipoModelo.getIdTipoModelo());
                pstmt.executeUpdate();

                eliminarTraducciones(tipoModelo.getIdTipoModelo(), conn);
                guardarTraducciones(tipoModelo, conn);
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void eliminarTraducciones(int idTipoModelo, Connection conn) throws SQLException {
        String sql = "DELETE FROM Tipo_modelo_tr WHERE id_tipo_modelo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTipoModelo);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Tipo_modelo WHERE id_tipo_modelo = ?";

        try (Connection conn = Database.conectar()) {
             conn.setAutoCommit(false);
             try {
                 eliminarTraducciones(id, conn);
                 try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                     pstmt.setInt(1, id);
                     pstmt.executeUpdate();
                 }
                 conn.commit();
             } catch (SQLException e) {
                 conn.rollback();
                 throw e;
             }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
