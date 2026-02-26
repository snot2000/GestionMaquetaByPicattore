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
        String sql = "INSERT INTO Idiomas(codigo, nombre) VALUES(?, ?)";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idioma.getCodigo());
            pstmt.setString(2, idioma.getNombre());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Optional<Idioma> buscarPorId(int id) {
        String sql = "SELECT Id, codigo, nombre FROM Idiomas WHERE Id = ?";
        Idioma idioma = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                idioma = new Idioma(
                        rs.getInt("Id"),
                        rs.getString("codigo"),
                        rs.getString("nombre")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(idioma);
    }

    @Override
    public List<Idioma> buscarTodos() {
        String sql = "SELECT Id, codigo, nombre FROM Idiomas";
        List<Idioma> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Idioma idioma = new Idioma(
                        rs.getInt("Id"),
                        rs.getString("codigo"),
                        rs.getString("nombre")
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
        String sql = "UPDATE Idiomas SET codigo = ?, nombre = ? WHERE Id = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idioma.getCodigo());
            pstmt.setString(2, idioma.getNombre());
            pstmt.setInt(3, idioma.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
