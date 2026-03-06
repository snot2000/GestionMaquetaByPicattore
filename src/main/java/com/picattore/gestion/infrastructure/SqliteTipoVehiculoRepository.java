package com.picattore.gestion.infrastructure;

import com.picattore.gestion.domain.TipoVehiculo;
import com.picattore.gestion.domain.TipoVehiculoRepository;
import com.picattore.gestion.domain.TipoVehiculoTr;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteTipoVehiculoRepository implements TipoVehiculoRepository {

    @Override
    public void guardar(TipoVehiculo tipoVehiculo) {
        String sql = "INSERT INTO Tipo_vehiculo(codigo) VALUES(?)";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, tipoVehiculo.getCodigo());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tipoVehiculo.setIdTipoVehiculo(generatedKeys.getInt(1));
                    guardarTraducciones(tipoVehiculo, conn);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void guardarTraducciones(TipoVehiculo tipoVehiculo, Connection conn) throws SQLException {
        String sql = "INSERT INTO Tipo_vehiculo_tr(id_tipo_vehiculo, id_idioma, nombre, descripcion) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (TipoVehiculoTr tr : tipoVehiculo.getTraducciones()) {
                pstmt.setInt(1, tipoVehiculo.getIdTipoVehiculo());
                pstmt.setInt(2, tr.getIdIdioma());
                pstmt.setString(3, tr.getNombre());
                pstmt.setString(4, tr.getDescripcion());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    @Override
    public Optional<TipoVehiculo> buscarPorId(int id) {
        String sql = "SELECT id_tipo_vehiculo, codigo FROM Tipo_vehiculo WHERE id_tipo_vehiculo = ?";
        TipoVehiculo tipoVehiculo = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                tipoVehiculo = new TipoVehiculo(
                        rs.getInt("id_tipo_vehiculo"),
                        rs.getString("codigo")
                );
                cargarTraducciones(tipoVehiculo, conn);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(tipoVehiculo);
    }

    private void cargarTraducciones(TipoVehiculo tipoVehiculo, Connection conn) throws SQLException {
        String sql = "SELECT id, id_tipo_vehiculo, id_idioma, nombre, descripcion FROM Tipo_vehiculo_tr WHERE id_tipo_vehiculo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tipoVehiculo.getIdTipoVehiculo());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TipoVehiculoTr tr = new TipoVehiculoTr(
                        rs.getInt("id"),
                        rs.getInt("id_tipo_vehiculo"),
                        rs.getInt("id_idioma"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
                tipoVehiculo.addTraduccion(tr);
            }
        }
    }

    @Override
    public List<TipoVehiculo> buscarTodos() {
        String sql = "SELECT id_tipo_vehiculo, codigo FROM Tipo_vehiculo";
        List<TipoVehiculo> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TipoVehiculo tipoVehiculo = new TipoVehiculo(
                        rs.getInt("id_tipo_vehiculo"),
                        rs.getString("codigo")
                );
                cargarTraducciones(tipoVehiculo, conn);
                lista.add(tipoVehiculo);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(TipoVehiculo tipoVehiculo) {
        String sql = "UPDATE Tipo_vehiculo SET codigo = ? WHERE id_tipo_vehiculo = ?";

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, tipoVehiculo.getCodigo());
                pstmt.setInt(2, tipoVehiculo.getIdTipoVehiculo());
                pstmt.executeUpdate();

                eliminarTraducciones(tipoVehiculo.getIdTipoVehiculo(), conn);
                guardarTraducciones(tipoVehiculo, conn);
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void eliminarTraducciones(int idTipoVehiculo, Connection conn) throws SQLException {
        String sql = "DELETE FROM Tipo_vehiculo_tr WHERE id_tipo_vehiculo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTipoVehiculo);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Tipo_vehiculo WHERE id_tipo_vehiculo = ?";

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
