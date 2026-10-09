package com.veterinaria.turnero.repository;

import com.veterinaria.turnero.model.NivelPrioridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface INivelPrioridadRepository extends JpaRepository<NivelPrioridad, Long> {
}
