package com.veterinaria.turnero.service;

import com.veterinaria.turnero.dto.MedicoDTO;
import com.veterinaria.turnero.model.PersonalMedico;
import com.veterinaria.turnero.repository.IPersonalMedicoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonalMedicoServiceImpl implements IPersonalMedicoService {

    private final IPersonalMedicoRepository medicoRepository;

    public PersonalMedicoServiceImpl(IPersonalMedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @Override
    public List<MedicoDTO> obtenerTodos() {
        return medicoRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    public MedicoDTO obtenerPorId(Long id) {
        PersonalMedico med = medicoRepository.findById(id).orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + id));
        return convertirADTO(med);
    }

    @Override
    public MedicoDTO crearMedico(MedicoDTO dto) {
        PersonalMedico med = new PersonalMedico(null, dto.getNombre(), dto.getEspecialidad(), dto.getTarjetaProfesional());
        PersonalMedico guardado = medicoRepository.save(med);
        return convertirADTO(guardado);
    }

    private MedicoDTO convertirADTO(PersonalMedico med) {
        MedicoDTO dto = new MedicoDTO();
        dto.setIdMedico(med.getIdMedico());
        dto.setNombre(med.getNombre());
        dto.setEspecialidad(med.getEspecialidad());
        dto.setTarjetaProfesional(med.getTarjetaProfesional());
        return dto;
    }
}
