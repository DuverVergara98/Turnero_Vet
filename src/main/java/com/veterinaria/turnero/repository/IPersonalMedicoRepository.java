package com.veterinaria.turnero.repository;

import com.veterinaria.turnero.model.PersonalMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPersonalMedicoRepository extends JpaRepository<PersonalMedico, Long> {
}
