package com.picattore.gestion.infrastructure;

import com.picattore.gestion.domain.Fabricante;
import com.picattore.gestion.domain.FabricanteRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteFabricanteRepository implements FabricanteRepository {

    @Override
    public void guardar(Fabricante fabricante) {
        String sql = "INSERT INTO Fabricantes(nombre, descripcion, id_pais, pagina_web, telefono, email, fecha_alta, fecha_baja) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, fabricante.getNombre());
            pstmt.setString(2, fabricante.getDescripcion());
            pstmt.setObject(3, fabricante.getIdPais());
            pstmt.setString(4, fabricante.getPaginaWeb());
            pstmt.setString(5, fabricante.getTelefono());
            pstmt.setString(6, fabricante.getEmail());
            pstmt.setString(7, fabricante.getFechaAlta());
            pstmt.setString(8, fabricante.getFechaBaja());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    fabricante.setIdFabricante(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Optional<Fabricante> buscarPorId(int id) {
        String sql = "SELECT * FROM Fabricantes WHERE id_fabricante = ?";
        Fabricante fabricante = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                fabricante = new Fabricante(
                        rs.getInt("id_fabricante"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        (Integer) rs.getObject("id_pais"),
                        rs.getString("pagina_web"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("fecha_alta"),
                        rs.getString("fecha_baja")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(fabricante);
    }

    @Override
    public List<Fabricante> buscarTodos() {
        String sql = "SELECT * FROM Fabricantes";
        List<Fabricante> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Fabricante fabricante = new Fabricante(
                        rs.getInt("id_fabricante"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        (Integer) rs.getObject("id_pais"),
                        rs.getString("pagina_web"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("fecha_alta"),
                        rs.getString("fecha_baja")
                );
                lista.add(fabricante);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Fabricante fabricante) {
        String sql = "UPDATE Fabricantes SET nombre = ?, descripcion = ?, id_pais = ?, pagina_web = ?, telefono = ?, email = ?, fecha_alta = ?, fecha_baja = ? WHERE id_fabricante = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fabricante.getNombre());
            pstmt.setString(2, fabricante.getDescripcion());
            pstmt.setObject(3, fabricante.getIdPais());
            pstmt.setString(4, fabricante.getPaginaWeb());
            pstmt.setString(5, fabricante.getTelefono());
            pstmt.setString(6, fabricante.getEmail());
            pstmt.setString(7, fabricante.getFechaAlta());
            pstmt.setString(8, fabricante.getFechaBaja());
            pstmt.setInt(9, fabricante.getIdFabricante());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Fabricantes WHERE id_fabricante = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
