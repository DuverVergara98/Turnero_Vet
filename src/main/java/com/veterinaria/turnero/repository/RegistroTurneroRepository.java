package com.veterinaria.turnero.repository;

import com.veterinaria.turnero.model.RegistroTurnero;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RegistroTurneroRepository extends JpaRepository<RegistroTurnero, Long> {
    Optional<RegistroTurnero> findByClave(String clave);
}