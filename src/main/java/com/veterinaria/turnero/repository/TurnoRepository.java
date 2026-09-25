package com.veterinaria.turnero.repository;

import com.veterinaria.turnero.model.Turno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TurnoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Turno> turnoRowMapper = new RowMapper<Turno>() {
        @Override
        public Turno mapRow(ResultSet rs, int rowNum) throws SQLException {
            Turno turno = new Turno();
            turno.setIdTurno(rs.getInt("id_turno"));
            turno.setCodigoTurno(rs.getString("codigo_turno"));
            turno.setIdMascota(rs.getInt("id_mascota"));
            turno.setNombreMascota(rs.getString("nombre_mascota"));
            turno.setIdPrioridad(rs.getInt("id_prioridad"));
            turno.setNombrePrioridad(rs.getString("nombre_prioridad"));
            turno.setIdMedico(rs.getInt("id_medico"));
            turno.setNombreMedico(rs.getString("nombre_medico"));
            turno.setFecha(rs.getDate("fecha"));
            turno.setHora(rs.getTime("hora"));
            turno.setEstado(rs.getString("estado"));
            return turno;
        }
    };

    public List<Turno> findAll() {
        try {
            String sql = "SELECT t.id_turno, t.codigo_turno, t.id_mascota, m.nombre AS nombre_mascota, " +
                         "t.id_prioridad, np.nombre_prioridad, t.id_medico, pm.nombre AS nombre_medico, " +
                         "t.fecha, t.hora, t.estado " +
                         "FROM TURNO t " +
                         "LEFT JOIN MASCOTA m ON t.id_mascota = m.id_mascota " +
                         "LEFT JOIN NIVEL_PRIORIDAD np ON t.id_prioridad = np.id_prioridad " +
                         "LEFT JOIN PERSONAL_MEDICO pm ON t.id_medico = pm.id_medico";
            return jdbcTemplate.query(sql, turnoRowMapper);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Turno findById(int id) {
        String sql = "SELECT t.id_turno, t.codigo_turno, t.id_mascota, m.nombre AS nombre_mascota, " +
                     "t.id_prioridad, np.nombre_prioridad, t.id_medico, pm.nombre AS nombre_medico, " +
                     "t.fecha, t.hora, t.estado " +
                     "FROM TURNO t " +
                     "LEFT JOIN MASCOTA m ON t.id_mascota = m.id_mascota " +
                     "LEFT JOIN NIVEL_PRIORIDAD np ON t.id_prioridad = np.id_prioridad " +
                     "LEFT JOIN PERSONAL_MEDICO pm ON t.id_medico = pm.id_medico " +
                     "WHERE t.id_turno = ?";
        return jdbcTemplate.queryForObject(sql, turnoRowMapper, id);
    }

    public int save(Turno turno) {
        String sql = "INSERT INTO TURNO (codigo_turno, id_mascota, id_prioridad, id_medico, estado) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, 
            turno.getCodigoTurno(), 
            turno.getIdMascota(), 
            turno.getIdPrioridad(), 
            turno.getIdMedico(), 
            turno.getEstado() != null ? turno.getEstado() : "En espera"
        );
    }

    public int update(Turno turno, int id) {
        String sql = "UPDATE TURNO SET codigo_turno = ?, id_mascota = ?, id_prioridad = ?, id_medico = ?, estado = ? WHERE id_turno = ?";
        return jdbcTemplate.update(sql, turno.getCodigoTurno(), turno.getIdMascota(), turno.getIdPrioridad(), turno.getIdMedico(), turno.getEstado(), id);
    }

    public int delete(int id) {
        String sql = "DELETE FROM TURNO WHERE id_turno = ?";
        return jdbcTemplate.update(sql, id);
    }
}
