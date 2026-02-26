package com.picattore.gestion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IdiomaDAO {

    public void insertar(Idioma idioma) {
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

    public List<Idioma> listar() {
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

    public void eliminar(int id) {
        // TODO: Verificar referencias antes de eliminar cuando existan otras tablas
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
