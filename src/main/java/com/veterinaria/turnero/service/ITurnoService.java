package com.veterinaria.turnero.service;

import com.veterinaria.turnero.dto.TurnoRequestDTO;
import com.veterinaria.turnero.dto.TurnoResponseDTO;
import java.util.List;

public interface ITurnoService {
    List<TurnoResponseDTO> obtenerTodos();
    TurnoResponseDTO obtenerPorId(Long id);
    TurnoResponseDTO crearTurno(TurnoRequestDTO turnoDTO);
    TurnoResponseDTO actualizarTurno(Long id, TurnoRequestDTO turnoDTO);
    void eliminarTurno(Long id);
}
