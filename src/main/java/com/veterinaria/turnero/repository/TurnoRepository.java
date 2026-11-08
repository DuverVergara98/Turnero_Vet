package com.veterinaria.turnero.repository;

import com.veterinaria.turnero.model.EstadoTurno;
import com.veterinaria.turnero.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    List<Turno> findByEstado(EstadoTurno estado);
    List<Turno> findByMedicoId(Long medicoId);
    List<Turno> findByMedicoIdAndEstado(Long medicoId, EstadoTurno estado);
    boolean existsByMascotaIdAndEstado(Long mascotaId, EstadoTurno estado);
}