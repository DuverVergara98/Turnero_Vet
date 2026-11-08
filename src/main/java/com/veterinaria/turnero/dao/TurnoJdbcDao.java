package com.veterinaria.turnero.dao;

import com.veterinaria.turnero.dto.TurnoDTO;
import com.veterinaria.turnero.model.EstadoTurno;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TurnoJdbcDao {

    private final JdbcTemplate jdbcTemplate;

    public TurnoJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TurnoDTO> obtenerTurnosPendientes() {
        return obtenerResumenTurnosPendientes();
    }

    public List<TurnoDTO> obtenerResumenTurnosPendientes() {
        String sql = "SELECT t.id, t.codigo_turno, t.estado, t.fecha_creacion, t.fecha_atencion, t.motivo_consulta, " +
                     "p.id AS mascota_id, p.nombre AS nombre_mascota, p.nombre_propietario, " +
                     "m.id AS medico_id, m.nombre AS nombre_medico " +
                     "FROM turnos t " +
                     "LEFT JOIN mascotas p ON t.mascota_id = p.id " +
                     "LEFT JOIN medicos m ON t.medico_id = m.id " +
                     "WHERE t.estado = 'PENDIENTE'";

        return jdbcTemplate.query(sql, new TurnoRowMapper());
    }

    private static class TurnoRowMapper implements RowMapper<TurnoDTO> {
        @Override
        public TurnoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            TurnoDTO dto = new TurnoDTO();
            dto.setId(rs.getLong("id"));
            dto.setCodigoTurno(rs.getString("codigo_turno"));
            
            String estadoStr = rs.getString("estado");
            if (estadoStr != null) {
                dto.setEstado(EstadoTurno.valueOf(estadoStr));
            }

            if (rs.getTimestamp("fecha_creacion") != null) {
                dto.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
            }
            if (rs.getTimestamp("fecha_atencion") != null) {
                dto.setFechaAtencion(rs.getTimestamp("fecha_atencion").toLocalDateTime());
            }

            dto.setMotivoConsulta(rs.getString("motivo_consulta"));
            dto.setMascotaId(rs.getLong("mascota_id"));
            dto.setNombreMascota(rs.getString("nombre_mascota"));
            dto.setNombrePropietario(rs.getString("nombre_propietario"));
            dto.setMedicoId(rs.getLong("medico_id"));
            dto.setNombreMedico(rs.getString("nombre_medico"));
            return dto;
        }
    }
}