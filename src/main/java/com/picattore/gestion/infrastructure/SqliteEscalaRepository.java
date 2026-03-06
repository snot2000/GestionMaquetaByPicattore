package com.picattore.gestion.infrastructure;

import com.picattore.gestion.domain.Escala;
import com.picattore.gestion.domain.EscalaRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteEscalaRepository implements EscalaRepository {

    @Override
    public void guardar(Escala escala) {
        String sql = "INSERT INTO Escalas(codigo, escala) VALUES(?, ?)";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, escala.getCodigo());
            pstmt.setString(2, escala.getEscala());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    escala.setIdEscala(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Optional<Escala> buscarPorId(int id) {
        String sql = "SELECT id_escala, codigo, escala FROM Escalas WHERE id_escala = ?";
        Escala escala = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                escala = new Escala(
                        rs.getInt("id_escala"),
                        rs.getString("codigo"),
                        rs.getString("escala")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(escala);
    }

    @Override
    public List<Escala> buscarTodas() {
        String sql = "SELECT id_escala, codigo, escala FROM Escalas";
        List<Escala> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Escala escala = new Escala(
                        rs.getInt("id_escala"),
                        rs.getString("codigo"),
                        rs.getString("escala")
                );
                lista.add(escala);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Escala escala) {
        String sql = "UPDATE Escalas SET codigo = ?, escala = ? WHERE id_escala = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, escala.getCodigo());
            pstmt.setString(2, escala.getEscala());
            pstmt.setInt(3, escala.getIdEscala());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Escalas WHERE id_escala = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
