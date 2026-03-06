package com.picattore.gestion.infrastructure;

import com.picattore.gestion.domain.Operadora;
import com.picattore.gestion.domain.OperadoraRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteOperadoraRepository implements OperadoraRepository {

    @Override
    public void guardar(Operadora operadora) {
        String sql = "INSERT INTO Operadoras(codigo, nombre, informacion, anio_creacion, anio_disolucion) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, operadora.getCodigo());
                pstmt.setString(2, operadora.getNombre());
                pstmt.setString(3, operadora.getInformacion());
                pstmt.setObject(4, operadora.getAnioCreacion());
                pstmt.setObject(5, operadora.getAnioDisolucion());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        operadora.setIdOperadora(generatedKeys.getInt(1));
                        guardarRelaciones(operadora, conn);
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

    private void guardarRelaciones(Operadora operadora, Connection conn) throws SQLException {
        // Guardar Países
        String sqlPaises = "INSERT INTO Operadoras_Paises(id_operadora, id_pais) VALUES(?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlPaises)) {
            for (Integer idPais : operadora.getPaisesIds()) {
                pstmt.setInt(1, operadora.getIdOperadora());
                pstmt.setInt(2, idPais);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }

        // Guardar Predecesoras (id_predecesora = id_lista, id_sucesora = id_actual)
        String sqlPredecesoras = "INSERT INTO Operadoras_Relacion(id_predecesora, id_sucesora) VALUES(?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlPredecesoras)) {
            for (Integer idPredecesora : operadora.getPredecesorasIds()) {
                pstmt.setInt(1, idPredecesora);
                pstmt.setInt(2, operadora.getIdOperadora());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }

        // Guardar Sucesoras (id_predecesora = id_actual, id_sucesora = id_lista)
        String sqlSucesoras = "INSERT INTO Operadoras_Relacion(id_predecesora, id_sucesora) VALUES(?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSucesoras)) {
            for (Integer idSucesora : operadora.getSucesorasIds()) {
                pstmt.setInt(1, operadora.getIdOperadora());
                pstmt.setInt(2, idSucesora);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    @Override
    public Optional<Operadora> buscarPorId(int id) {
        String sql = "SELECT id_operadora, codigo, nombre, informacion, anio_creacion, anio_disolucion FROM Operadoras WHERE id_operadora = ?";
        Operadora operadora = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                operadora = new Operadora(
                        rs.getInt("id_operadora"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("informacion"),
                        (Integer) rs.getObject("anio_creacion"),
                        (Integer) rs.getObject("anio_disolucion")
                );
                cargarRelaciones(operadora, conn);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(operadora);
    }

    private void cargarRelaciones(Operadora operadora, Connection conn) throws SQLException {
        // Cargar Países
        String sqlPaises = "SELECT id_pais FROM Operadoras_Paises WHERE id_operadora = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlPaises)) {
            pstmt.setInt(1, operadora.getIdOperadora());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                operadora.getPaisesIds().add(rs.getInt("id_pais"));
            }
        }

        // Cargar Predecesoras (donde id_sucesora es la actual)
        String sqlPredecesoras = "SELECT id_predecesora FROM Operadoras_Relacion WHERE id_sucesora = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlPredecesoras)) {
            pstmt.setInt(1, operadora.getIdOperadora());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                operadora.getPredecesorasIds().add(rs.getInt("id_predecesora"));
            }
        }

        // Cargar Sucesoras (donde id_predecesora es la actual)
        String sqlSucesoras = "SELECT id_sucesora FROM Operadoras_Relacion WHERE id_predecesora = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSucesoras)) {
            pstmt.setInt(1, operadora.getIdOperadora());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                operadora.getSucesorasIds().add(rs.getInt("id_sucesora"));
            }
        }
    }

    @Override
    public List<Operadora> buscarTodas() {
        String sql = "SELECT id_operadora, codigo, nombre, informacion, anio_creacion, anio_disolucion FROM Operadoras";
        List<Operadora> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Operadora operadora = new Operadora(
                        rs.getInt("id_operadora"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("informacion"),
                        (Integer) rs.getObject("anio_creacion"),
                        (Integer) rs.getObject("anio_disolucion")
                );
                cargarRelaciones(operadora, conn);
                lista.add(operadora);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Operadora operadora) {
        String sql = "UPDATE Operadoras SET codigo = ?, nombre = ?, informacion = ?, anio_creacion = ?, anio_disolucion = ? WHERE id_operadora = ?";

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, operadora.getCodigo());
                pstmt.setString(2, operadora.getNombre());
                pstmt.setString(3, operadora.getInformacion());
                pstmt.setObject(4, operadora.getAnioCreacion());
                pstmt.setObject(5, operadora.getAnioDisolucion());
                pstmt.setInt(6, operadora.getIdOperadora());
                pstmt.executeUpdate();

                eliminarRelaciones(operadora.getIdOperadora(), conn);
                guardarRelaciones(operadora, conn);
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void eliminarRelaciones(int idOperadora, Connection conn) throws SQLException {
        String sqlPaises = "DELETE FROM Operadoras_Paises WHERE id_operadora = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlPaises)) {
            pstmt.setInt(1, idOperadora);
            pstmt.executeUpdate();
        }

        // Eliminar donde es sucesora (borrar predecesoras)
        String sqlPredecesoras = "DELETE FROM Operadoras_Relacion WHERE id_sucesora = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlPredecesoras)) {
            pstmt.setInt(1, idOperadora);
            pstmt.executeUpdate();
        }

        // Eliminar donde es predecesora (borrar sucesoras)
        String sqlSucesoras = "DELETE FROM Operadoras_Relacion WHERE id_predecesora = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSucesoras)) {
            pstmt.setInt(1, idOperadora);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Operadoras WHERE id_operadora = ?";

        try (Connection conn = Database.conectar()) {
             conn.setAutoCommit(false);
             try {
                 eliminarRelaciones(id, conn);
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
