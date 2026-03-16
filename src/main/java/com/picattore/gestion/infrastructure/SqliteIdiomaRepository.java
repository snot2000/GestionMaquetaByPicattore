package com.picattore.gestion.infrastructure;

import com.picattore.gestion.domain.Idioma;
import com.picattore.gestion.domain.IdiomaRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteIdiomaRepository implements IdiomaRepository {

    @Override
    public void guardar(Idioma idioma) {
        String sql = "INSERT INTO Idiomas(codigo, nombre, principal) VALUES(?, ?, ?)";

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false);
            try {
                if (idioma.isPrincipal()) {
                    desmarcarOtrosPrincipales(conn, -1); // -1 porque es nuevo
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, idioma.getCodigo());
                    pstmt.setString(2, idioma.getNombre());
                    pstmt.setInt(3, idioma.isPrincipal() ? 1 : 0);
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

    @Override
    public Optional<Idioma> buscarPorId(int id) {
        String sql = "SELECT Id, codigo, nombre, principal FROM Idiomas WHERE Id = ?";
        Idioma idioma = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                idioma = new Idioma(
                        rs.getInt("Id"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getInt("principal") == 1
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(idioma);
    }

    @Override
    public List<Idioma> buscarTodos() {
        String sql = "SELECT Id, codigo, nombre, principal FROM Idiomas";
        List<Idioma> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Idioma idioma = new Idioma(
                        rs.getInt("Id"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getInt("principal") == 1
                );
                lista.add(idioma);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Idioma idioma) {
        String sql = "UPDATE Idiomas SET codigo = ?, nombre = ?, principal = ? WHERE Id = ?";

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false);
            try {
                if (idioma.isPrincipal()) {
                    desmarcarOtrosPrincipales(conn, idioma.getId());
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, idioma.getCodigo());
                    pstmt.setString(2, idioma.getNombre());
                    pstmt.setInt(3, idioma.isPrincipal() ? 1 : 0);
                    pstmt.setInt(4, idioma.getId());
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

    private void desmarcarOtrosPrincipales(Connection conn, int idExcluido) throws SQLException {
        String sql = "UPDATE Idiomas SET principal = 0 WHERE Id != ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idExcluido);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Idiomas WHERE Id = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
