package com.picattore.gestion.infrastructure;

import com.picattore.gestion.domain.EsquemaPintura;
import com.picattore.gestion.domain.EsquemaPinturaRepository;
import com.picattore.gestion.domain.EsquemaPinturaTr;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteEsquemaPinturaRepository implements EsquemaPinturaRepository {

    @Override
    public void guardar(EsquemaPintura esquema) {
        String sql = "INSERT INTO Esquema_pintura(id_pais, id_operadora, nombre, anio_inicio, anio_fin) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, esquema.getIdPais());
                pstmt.setInt(2, esquema.getIdOperadora());
                pstmt.setString(3, esquema.getNombre());
                pstmt.setObject(4, esquema.getAnioInicio());
                pstmt.setObject(5, esquema.getAnioFin());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        esquema.setIdEsquemaPintura(generatedKeys.getInt(1));
                        guardarTraducciones(esquema, conn);
                    }
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

    private void guardarTraducciones(EsquemaPintura esquema, Connection conn) throws SQLException {
        String sql = "INSERT INTO Esquema_pintura_tr(id_esquema_pintura, id_idioma, descripcion, codigo_colores, colores) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (EsquemaPinturaTr tr : esquema.getTraducciones()) {
                pstmt.setInt(1, esquema.getIdEsquemaPintura());
                pstmt.setInt(2, tr.getIdIdioma());
                pstmt.setString(3, tr.getDescripcion());
                pstmt.setString(4, tr.getCodigoColores());
                pstmt.setString(5, tr.getColores());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    @Override
    public Optional<EsquemaPintura> buscarPorId(int id) {
        String sql = "SELECT id_esquema_pintura, id_pais, id_operadora, nombre, anio_inicio, anio_fin FROM Esquema_pintura WHERE id_esquema_pintura = ?";
        EsquemaPintura esquema = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                esquema = new EsquemaPintura(
                        rs.getInt("id_esquema_pintura"),
                        rs.getInt("id_pais"),
                        rs.getInt("id_operadora"),
                        rs.getString("nombre"),
                        (Integer) rs.getObject("anio_inicio"),
                        (Integer) rs.getObject("anio_fin")
                );
                cargarTraducciones(esquema, conn);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(esquema);
    }

    private void cargarTraducciones(EsquemaPintura esquema, Connection conn) throws SQLException {
        String sql = "SELECT id, id_esquema_pintura, id_idioma, descripcion, codigo_colores, colores FROM Esquema_pintura_tr WHERE id_esquema_pintura = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, esquema.getIdEsquemaPintura());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                EsquemaPinturaTr tr = new EsquemaPinturaTr(
                        rs.getInt("id"),
                        rs.getInt("id_esquema_pintura"),
                        rs.getInt("id_idioma"),
                        rs.getString("descripcion"),
                        rs.getString("codigo_colores"),
                        rs.getString("colores")
                );
                esquema.addTraduccion(tr);
            }
        }
    }

    @Override
    public List<EsquemaPintura> buscarTodos() {
        String sql = "SELECT id_esquema_pintura, id_pais, id_operadora, nombre, anio_inicio, anio_fin FROM Esquema_pintura";
        List<EsquemaPintura> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                EsquemaPintura esquema = new EsquemaPintura(
                        rs.getInt("id_esquema_pintura"),
                        rs.getInt("id_pais"),
                        rs.getInt("id_operadora"),
                        rs.getString("nombre"),
                        (Integer) rs.getObject("anio_inicio"),
                        (Integer) rs.getObject("anio_fin")
                );
                cargarTraducciones(esquema, conn);
                lista.add(esquema);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(EsquemaPintura esquema) {
        String sql = "UPDATE Esquema_pintura SET id_pais = ?, id_operadora = ?, nombre = ?, anio_inicio = ?, anio_fin = ? WHERE id_esquema_pintura = ?";

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, esquema.getIdPais());
                pstmt.setInt(2, esquema.getIdOperadora());
                pstmt.setString(3, esquema.getNombre());
                pstmt.setObject(4, esquema.getAnioInicio());
                pstmt.setObject(5, esquema.getAnioFin());
                pstmt.setInt(6, esquema.getIdEsquemaPintura());
                pstmt.executeUpdate();

                eliminarTraducciones(esquema.getIdEsquemaPintura(), conn);
                guardarTraducciones(esquema, conn);
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void eliminarTraducciones(int idEsquema, Connection conn) throws SQLException {
        String sql = "DELETE FROM Esquema_pintura_tr WHERE id_esquema_pintura = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEsquema);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Esquema_pintura WHERE id_esquema_pintura = ?";

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
