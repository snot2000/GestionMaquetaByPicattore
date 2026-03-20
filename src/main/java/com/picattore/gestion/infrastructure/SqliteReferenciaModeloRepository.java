package com.picattore.gestion.infrastructure;

import com.picattore.gestion.domain.ReferenciaModelo;
import com.picattore.gestion.domain.ReferenciaModeloRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteReferenciaModeloRepository implements ReferenciaModeloRepository {

    @Override
    public void guardar(ReferenciaModelo referencia) {
        String sql = "INSERT INTO referencia_modelo(id_fabricante, referencia, id_vehiculo_real, id_escala, fecha_salida, fecha_descontinuado) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setObject(1, referencia.getIdFabricante());
            pstmt.setString(2, referencia.getReferencia());
            pstmt.setObject(3, referencia.getIdVehiculoReal());
            pstmt.setObject(4, referencia.getIdEscala());
            pstmt.setString(5, referencia.getFechaSalida());
            pstmt.setString(6, referencia.getFechaDescontinuado());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    referencia.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Optional<ReferenciaModelo> buscarPorId(int id) {
        String sql = "SELECT * FROM referencia_modelo WHERE id = ?";
        ReferenciaModelo referencia = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                referencia = new ReferenciaModelo(
                        rs.getInt("id"),
                        (Integer) rs.getObject("id_fabricante"),
                        rs.getString("referencia"),
                        (Integer) rs.getObject("id_vehiculo_real"),
                        (Integer) rs.getObject("id_escala"),
                        rs.getString("fecha_salida"),
                        rs.getString("fecha_descontinuado")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(referencia);
    }

    @Override
    public List<ReferenciaModelo> buscarTodos() {
        String sql = "SELECT * FROM referencia_modelo";
        List<ReferenciaModelo> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ReferenciaModelo referencia = new ReferenciaModelo(
                        rs.getInt("id"),
                        (Integer) rs.getObject("id_fabricante"),
                        rs.getString("referencia"),
                        (Integer) rs.getObject("id_vehiculo_real"),
                        (Integer) rs.getObject("id_escala"),
                        rs.getString("fecha_salida"),
                        rs.getString("fecha_descontinuado")
                );
                lista.add(referencia);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(ReferenciaModelo referencia) {
        String sql = "UPDATE referencia_modelo SET id_fabricante = ?, referencia = ?, id_vehiculo_real = ?, id_escala = ?, fecha_salida = ?, fecha_descontinuado = ? WHERE id = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, referencia.getIdFabricante());
            pstmt.setString(2, referencia.getReferencia());
            pstmt.setObject(3, referencia.getIdVehiculoReal());
            pstmt.setObject(4, referencia.getIdEscala());
            pstmt.setString(5, referencia.getFechaSalida());
            pstmt.setString(6, referencia.getFechaDescontinuado());
            pstmt.setInt(7, referencia.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM referencia_modelo WHERE id = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
