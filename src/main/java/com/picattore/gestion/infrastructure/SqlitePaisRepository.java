package com.picattore.gestion.infrastructure;

import com.picattore.gestion.domain.Pais;
import com.picattore.gestion.domain.PaisRepository;
import com.picattore.gestion.domain.PaisTr;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlitePaisRepository implements PaisRepository {

    @Override
    public void guardar(Pais pais) {
        String sql = "INSERT INTO Paises(codigo) VALUES(?)";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, pais.getCodigo());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pais.setIdPais(generatedKeys.getInt(1));
                    guardarTraducciones(pais, conn);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void guardarTraducciones(Pais pais, Connection conn) throws SQLException {
        String sql = "INSERT INTO PaisesTr(id_pais, id_idioma, nombre) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (PaisTr tr : pais.getTraducciones()) {
                pstmt.setInt(1, pais.getIdPais());
                pstmt.setInt(2, tr.getIdIdioma());
                pstmt.setString(3, tr.getNombre());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    @Override
    public Optional<Pais> buscarPorId(int id) {
        String sql = "SELECT id_pais, codigo FROM Paises WHERE id_pais = ?";
        Pais pais = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                pais = new Pais(
                        rs.getInt("id_pais"),
                        rs.getString("codigo")
                );
                cargarTraducciones(pais, conn);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(pais);
    }

    private void cargarTraducciones(Pais pais, Connection conn) throws SQLException {
        String sql = "SELECT id, id_pais, id_idioma, nombre FROM PaisesTr WHERE id_pais = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pais.getIdPais());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                PaisTr tr = new PaisTr(
                        rs.getInt("id"),
                        rs.getInt("id_pais"),
                        rs.getInt("id_idioma"),
                        rs.getString("nombre")
                );
                pais.addTraduccion(tr);
            }
        }
    }

    @Override
    public List<Pais> buscarTodos() {
        String sql = "SELECT id_pais, codigo FROM Paises";
        List<Pais> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Pais pais = new Pais(
                        rs.getInt("id_pais"),
                        rs.getString("codigo")
                );
                cargarTraducciones(pais, conn);
                lista.add(pais);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Pais pais) {
        String sql = "UPDATE Paises SET codigo = ? WHERE id_pais = ?";

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, pais.getCodigo());
                pstmt.setInt(2, pais.getIdPais());
                pstmt.executeUpdate();

                eliminarTraducciones(pais.getIdPais(), conn);
                guardarTraducciones(pais, conn);
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void eliminarTraducciones(int idPais, Connection conn) throws SQLException {
        String sql = "DELETE FROM PaisesTr WHERE id_pais = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPais);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Paises WHERE id_pais = ?";

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
