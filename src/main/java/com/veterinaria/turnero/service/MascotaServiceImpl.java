package com.veterinaria.turnero.service;

import com.veterinaria.turnero.dto.MascotaDTO;
import com.veterinaria.turnero.model.Mascota;
import com.veterinaria.turnero.repository.IMascotaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MascotaServiceImpl implements IMascotaService {

    private final IMascotaRepository mascotaRepository;

    public MascotaServiceImpl(IMascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    @Override
    public List<MascotaDTO> obtenerTodas() {
        return mascotaRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    public MascotaDTO obtenerPorId(Long id) {
        Mascota m = mascotaRepository.findById(id).orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + id));
        return convertirADTO(m);
    }

    @Override
    public MascotaDTO crearMascota(MascotaDTO dto) {
        Mascota m = new Mascota(null, dto.getNombre(), dto.getEspecie(), dto.getRaza(), dto.getSexo());
        Mascota guardada = mascotaRepository.save(m);
        return convertirADTO(guardada);
    }

    private MascotaDTO convertirADTO(Mascota m) {
        MascotaDTO dto = new MascotaDTO();
        dto.setIdMascota(m.getIdMascota());
        dto.setNombre(m.getNombre());
        dto.setEspecie(m.getEspecie());
        dto.setRaza(m.getRaza());
        dto.setSexo(m.getSexo());
        return dto;
    }
}
