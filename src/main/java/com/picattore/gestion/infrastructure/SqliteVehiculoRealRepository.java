package com.picattore.gestion.infrastructure;

import com.picattore.gestion.domain.VehiculoReal;
import com.picattore.gestion.domain.VehiculoRealRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteVehiculoRealRepository implements VehiculoRealRepository {

    @Override
    public void guardar(VehiculoReal vehiculoReal) {
        String sql = "INSERT INTO vehiculo_real(nombre, apodo, numeracion, uid, id_tipo_vehiculo, id_pais, id_epoca, id_esquema_pintura, id_operadora, fecha_fabricacion, fecha_baja, fecha_inicio_pintura, fecha_final_pintura, descripcion_tecnica, velocidad_maxima) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, vehiculoReal.getNombre());
            pstmt.setString(2, vehiculoReal.getApodo());
            pstmt.setString(3, vehiculoReal.getNumeracion());
            pstmt.setString(4, vehiculoReal.getUid());
            pstmt.setObject(5, vehiculoReal.getIdTipoVehiculo());
            pstmt.setObject(6, vehiculoReal.getIdPais());
            pstmt.setObject(7, vehiculoReal.getIdEpoca());
            pstmt.setObject(8, vehiculoReal.getIdEsquemaPintura());
            pstmt.setObject(9, vehiculoReal.getIdOperadora());
            pstmt.setString(10, vehiculoReal.getFechaFabricacion());
            pstmt.setString(11, vehiculoReal.getFechaBaja());
            pstmt.setString(12, vehiculoReal.getFechaInicioPintura());
            pstmt.setString(13, vehiculoReal.getFechaFinalPintura());
            pstmt.setString(14, vehiculoReal.getDescripcionTecnica());
            pstmt.setObject(15, vehiculoReal.getVelocidadMaxima());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vehiculoReal.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Optional<VehiculoReal> buscarPorId(int id) {
        String sql = "SELECT * FROM vehiculo_real WHERE id = ?";
        VehiculoReal vehiculoReal = null;

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                vehiculoReal = new VehiculoReal(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apodo"),
                        rs.getString("numeracion"),
                        rs.getString("uid"),
                        (Integer) rs.getObject("id_tipo_vehiculo"),
                        (Integer) rs.getObject("id_pais"),
                        (Integer) rs.getObject("id_epoca"),
                        (Integer) rs.getObject("id_esquema_pintura"),
                        (Integer) rs.getObject("id_operadora"),
                        rs.getString("fecha_fabricacion"),
                        rs.getString("fecha_baja"),
                        rs.getString("fecha_inicio_pintura"),
                        rs.getString("fecha_final_pintura"),
                        rs.getString("descripcion_tecnica"),
                        (Integer) rs.getObject("velocidad_maxima")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(vehiculoReal);
    }

    @Override
    public List<VehiculoReal> buscarTodos() {
        String sql = "SELECT * FROM vehiculo_real";
        List<VehiculoReal> lista = new ArrayList<>();

        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                VehiculoReal vehiculoReal = new VehiculoReal(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apodo"),
                        rs.getString("numeracion"),
                        rs.getString("uid"),
                        (Integer) rs.getObject("id_tipo_vehiculo"),
                        (Integer) rs.getObject("id_pais"),
                        (Integer) rs.getObject("id_epoca"),
                        (Integer) rs.getObject("id_esquema_pintura"),
                        (Integer) rs.getObject("id_operadora"),
                        rs.getString("fecha_fabricacion"),
                        rs.getString("fecha_baja"),
                        rs.getString("fecha_inicio_pintura"),
                        rs.getString("fecha_final_pintura"),
                        rs.getString("descripcion_tecnica"),
                        (Integer) rs.getObject("velocidad_maxima")
                );
                lista.add(vehiculoReal);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(VehiculoReal vehiculoReal) {
        String sql = "UPDATE vehiculo_real SET nombre = ?, apodo = ?, numeracion = ?, uid = ?, id_tipo_vehiculo = ?, id_pais = ?, id_epoca = ?, id_esquema_pintura = ?, id_operadora = ?, fecha_fabricacion = ?, fecha_baja = ?, fecha_inicio_pintura = ?, fecha_final_pintura = ?, descripcion_tecnica = ?, velocidad_maxima = ? WHERE id = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehiculoReal.getNombre());
            pstmt.setString(2, vehiculoReal.getApodo());
            pstmt.setString(3, vehiculoReal.getNumeracion());
            pstmt.setString(4, vehiculoReal.getUid());
            pstmt.setObject(5, vehiculoReal.getIdTipoVehiculo());
            pstmt.setObject(6, vehiculoReal.getIdPais());
            pstmt.setObject(7, vehiculoReal.getIdEpoca());
            pstmt.setObject(8, vehiculoReal.getIdEsquemaPintura());
            pstmt.setObject(9, vehiculoReal.getIdOperadora());
            pstmt.setString(10, vehiculoReal.getFechaFabricacion());
            pstmt.setString(11, vehiculoReal.getFechaBaja());
            pstmt.setString(12, vehiculoReal.getFechaInicioPintura());
            pstmt.setString(13, vehiculoReal.getFechaFinalPintura());
            pstmt.setString(14, vehiculoReal.getDescripcionTecnica());
            pstmt.setObject(15, vehiculoReal.getVelocidadMaxima());
            pstmt.setInt(16, vehiculoReal.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM vehiculo_real WHERE id = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
