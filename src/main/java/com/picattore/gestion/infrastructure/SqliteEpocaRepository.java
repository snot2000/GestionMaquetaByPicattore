package com.picattore.gestion.infrastructure;

import com.picattore.gestion.domain.Epoca;
import com.picattore.gestion.domain.EpocaRepository;
import com.picattore.gestion.domain.EpocaTr;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteEpocaRepository implements EpocaRepository {

    @Override
    public void guardar(Epoca epoca) {
        String sql = "INSERT INTO Epocas(codigo, anio_inicio, anio_fin) VALUES(?, ?, ?)";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, epoca.getCodigo());
            pstmt.setInt(2, epoca.getAnioInicio());
            pstmt.setInt(3, epoca.getAnioFin());
            pstmt.executeUpdate();

            // Obtener el ID generado
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    epoca.setIdEpoca(generatedKeys.getInt(1));
                    guardarTraducciones(epoca, conn);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void guardarTraducciones(Epoca epoca, Connection conn) throws SQLException {
        String sql = "INSERT INTO EpocasTr(id_epoca, id_idioma, nombre, descripcion) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (EpocaTr tr : epoca.getTraducciones()) {
                pstmt.setInt(1, epoca.getIdEpoca());
                pstmt.setInt(2, tr.getIdIdioma());
                pstmt.setString(3, tr.getNombre());
                pstmt.setString(4, tr.getDescripcion());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    @Override
    public Optional<Epoca> buscarPorId(int id) {
        String sql = "SELECT id_epoca, codigo, anio_inicio, anio_fin FROM Epocas WHERE id_epoca = ?";
        Epoca epoca = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                epoca = new Epoca(
                        rs.getInt("id_epoca"),
                        rs.getString("codigo"),
                        rs.getInt("anio_inicio"),
                        rs.getInt("anio_fin")
                );
                cargarTraducciones(epoca, conn);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(epoca);
    }

    private void cargarTraducciones(Epoca epoca, Connection conn) throws SQLException {
        String sql = "SELECT id, id_epoca, id_idioma, nombre, descripcion FROM EpocasTr WHERE id_epoca = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, epoca.getIdEpoca());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                EpocaTr tr = new EpocaTr(
                        rs.getInt("id"),
                        rs.getInt("id_epoca"),
                        rs.getInt("id_idioma"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
                epoca.addTraduccion(tr);
            }
        }
    }

    @Override
    public List<Epoca> buscarTodas() {
        String sql = "SELECT id_epoca, codigo, anio_inicio, anio_fin FROM Epocas";
        List<Epoca> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Epoca epoca = new Epoca(
                        rs.getInt("id_epoca"),
                        rs.getString("codigo"),
                        rs.getInt("anio_inicio"),
                        rs.getInt("anio_fin")
                );
                // Para optimizar, podríamos cargar las traducciones bajo demanda o en una sola consulta
                // Por simplicidad, las cargamos aquí una por una
                cargarTraducciones(epoca, conn);
                lista.add(epoca);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Epoca epoca) {
        String sql = "UPDATE Epocas SET codigo = ?, anio_inicio = ?, anio_fin = ? WHERE id_epoca = ?";

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false); // Transacción
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, epoca.getCodigo());
                pstmt.setInt(2, epoca.getAnioInicio());
                pstmt.setInt(3, epoca.getAnioFin());
                pstmt.setInt(4, epoca.getIdEpoca());
                pstmt.executeUpdate();

                // Actualizar traducciones (borrar y reinsertar es lo más simple)
                eliminarTraducciones(epoca.getIdEpoca(), conn);
                guardarTraducciones(epoca, conn);
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void eliminarTraducciones(int idEpoca, Connection conn) throws SQLException {
        String sql = "DELETE FROM EpocasTr WHERE id_epoca = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEpoca);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Epocas WHERE id_epoca = ?";

        try (Connection conn = Database.conectar()) {
             conn.setAutoCommit(false);
             try {
                 eliminarTraducciones(id, conn); // Primero las traducciones por FK
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
