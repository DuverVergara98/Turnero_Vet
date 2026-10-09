package com.veterinaria.turnero.service;

import com.veterinaria.turnero.dto.MascotaDTO;
import java.util.List;

public interface IMascotaService {
    List<MascotaDTO> obtenerTodas();
    MascotaDTO obtenerPorId(Long id);
    MascotaDTO crearMascota(MascotaDTO mascotaDTO);
}
