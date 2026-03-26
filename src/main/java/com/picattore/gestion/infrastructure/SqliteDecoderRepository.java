package com.picattore.gestion.infrastructure;

import com.picattore.gestion.domain.DecoCV;
import com.picattore.gestion.domain.DecoFuncion;
import com.picattore.gestion.domain.Decoder;
import com.picattore.gestion.domain.DecoderRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteDecoderRepository implements DecoderRepository {

    @Override
    public void guardar(Decoder decoder) {
        String sql = "INSERT INTO decoder(id_fabricante, direccion, comp_carga, sonido, tipo_conector) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setObject(1, decoder.getIdFabricante());
                pstmt.setString(2, decoder.getDireccion());
                pstmt.setInt(3, decoder.isCompCarga() ? 1 : 0);
                pstmt.setInt(4, decoder.isSonido() ? 1 : 0);
                pstmt.setString(5, decoder.getTipoConector());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        decoder.setId(generatedKeys.getInt(1));
                        guardarListas(decoder, conn);
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

    private void guardarListas(Decoder decoder, Connection conn) throws SQLException {
        // CVs
        String sqlCvs = "INSERT INTO deco_cv(id_decoder, cv, dato) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlCvs)) {
            for (DecoCV cv : decoder.getCvs()) {
                pstmt.setInt(1, decoder.getId());
                pstmt.setString(2, cv.getCv());
                pstmt.setString(3, cv.getDato());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }

        // Funciones
        String sqlFunciones = "INSERT INTO deco_funcion(id_decoder, funcion, tipo_funcion, descripcion) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlFunciones)) {
            for (DecoFuncion funcion : decoder.getFunciones()) {
                pstmt.setInt(1, decoder.getId());
                pstmt.setString(2, funcion.getFuncion());
                pstmt.setString(3, funcion.getTipoFuncion());
                pstmt.setString(4, funcion.getDescripcion());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    @Override
    public Optional<Decoder> buscarPorId(int id) {
        String sql = "SELECT * FROM decoder WHERE id = ?";
        Decoder decoder = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                decoder = new Decoder(
                        rs.getInt("id"),
                        (Integer) rs.getObject("id_fabricante"),
                        rs.getString("direccion"),
                        rs.getInt("comp_carga") == 1,
                        rs.getInt("sonido") == 1,
                        rs.getString("tipo_conector")
                );
                cargarListas(decoder, conn);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(decoder);
    }

    private void cargarListas(Decoder decoder, Connection conn) throws SQLException {
        // CVs
        String sqlCvs = "SELECT id, id_decoder, cv, dato FROM deco_cv WHERE id_decoder = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlCvs)) {
            pstmt.setInt(1, decoder.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                decoder.addCv(new DecoCV(
                        rs.getInt("id"),
                        rs.getInt("id_decoder"),
                        rs.getString("cv"),
                        rs.getString("dato")
                ));
            }
        }

        // Funciones
        String sqlFunciones = "SELECT id, id_decoder, funcion, tipo_funcion, descripcion FROM deco_funcion WHERE id_decoder = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlFunciones)) {
            pstmt.setInt(1, decoder.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                decoder.addFuncion(new DecoFuncion(
                        rs.getInt("id"),
                        rs.getInt("id_decoder"),
                        rs.getString("funcion"),
                        rs.getString("tipo_funcion"),
                        rs.getString("descripcion")
                ));
            }
        }
    }

    @Override
    public List<Decoder> buscarTodos() {
        String sql = "SELECT * FROM decoder";
        List<Decoder> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Decoder decoder = new Decoder(
                        rs.getInt("id"),
                        (Integer) rs.getObject("id_fabricante"),
                        rs.getString("direccion"),
                        rs.getInt("comp_carga") == 1,
                        rs.getInt("sonido") == 1,
                        rs.getString("tipo_conector")
                );
                // cargamos listas para tener toda la info disponible
                cargarListas(decoder, conn);
                lista.add(decoder);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Decoder decoder) {
        String sql = "UPDATE decoder SET id_fabricante = ?, direccion = ?, comp_carga = ?, sonido = ?, tipo_conector = ? WHERE id = ?";

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setObject(1, decoder.getIdFabricante());
                pstmt.setString(2, decoder.getDireccion());
                pstmt.setInt(3, decoder.isCompCarga() ? 1 : 0);
                pstmt.setInt(4, decoder.isSonido() ? 1 : 0);
                pstmt.setString(5, decoder.getTipoConector());
                pstmt.setInt(6, decoder.getId());
                pstmt.executeUpdate();

                eliminarListas(decoder.getId(), conn);
                guardarListas(decoder, conn);
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void eliminarListas(int idDecoder, Connection conn) throws SQLException {
        String sqlCvs = "DELETE FROM deco_cv WHERE id_decoder = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlCvs)) {
            pstmt.setInt(1, idDecoder);
            pstmt.executeUpdate();
        }

        String sqlFunciones = "DELETE FROM deco_funcion WHERE id_decoder = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlFunciones)) {
            pstmt.setInt(1, idDecoder);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM decoder WHERE id = ?";

        try (Connection conn = Database.conectar()) {
             conn.setAutoCommit(false);
             try {
                 eliminarListas(id, conn);
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
