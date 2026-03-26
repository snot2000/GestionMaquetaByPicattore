package com.picattore.gestion.infrastructure;

import com.picattore.gestion.domain.Modelo;
import com.picattore.gestion.domain.ModeloRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteModeloRepository implements ModeloRepository {

    @Override
    public void guardar(Modelo modelo) {
        String sql = "INSERT INTO modelo(id_decoder, id_referencia_modelo, id_dueno) VALUES(?, ?, ?)";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setObject(1, modelo.getIdDecoder());
            pstmt.setObject(2, modelo.getIdReferenciaModelo());
            pstmt.setObject(3, modelo.getIdDueno());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    modelo.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Optional<Modelo> buscarPorId(int id) {
        String sql = "SELECT * FROM modelo WHERE id = ?";
        Modelo modelo = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                modelo = new Modelo(
                        rs.getInt("id"),
                        (Integer) rs.getObject("id_decoder"),
                        (Integer) rs.getObject("id_referencia_modelo"),
                        (Integer) rs.getObject("id_dueno")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(modelo);
    }

    @Override
    public List<Modelo> buscarTodos() {
        String sql = "SELECT * FROM modelo";
        List<Modelo> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Modelo(
                        rs.getInt("id"),
                        (Integer) rs.getObject("id_decoder"),
                        (Integer) rs.getObject("id_referencia_modelo"),
                        (Integer) rs.getObject("id_dueno")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Modelo modelo) {
        String sql = "UPDATE modelo SET id_decoder = ?, id_referencia_modelo = ?, id_dueno = ? WHERE id = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, modelo.getIdDecoder());
            pstmt.setObject(2, modelo.getIdReferenciaModelo());
            pstmt.setObject(3, modelo.getIdDueno());
            pstmt.setInt(4, modelo.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM modelo WHERE id = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
