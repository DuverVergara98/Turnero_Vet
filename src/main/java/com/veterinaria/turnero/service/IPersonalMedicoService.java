package com.veterinaria.turnero.service;

import com.veterinaria.turnero.dto.MedicoDTO;
import java.util.List;

public interface IPersonalMedicoService {
    List<MedicoDTO> obtenerTodos();
    MedicoDTO obtenerPorId(Long id);
    MedicoDTO crearMedico(MedicoDTO medicoDTO);
}
