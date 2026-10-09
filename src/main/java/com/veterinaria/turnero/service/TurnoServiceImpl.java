package com.veterinaria.turnero.service;

import com.veterinaria.turnero.dto.TurnoRequestDTO;
import com.veterinaria.turnero.dto.TurnoResponseDTO;
import com.veterinaria.turnero.model.*;
import com.veterinaria.turnero.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurnoServiceImpl implements ITurnoService {

    private final ITurnoRepository turnoRepository;
    private final IMascotaRepository mascotaRepository;
    private final IPersonalMedicoRepository medicoRepository;
    private final INivelPrioridadRepository prioridadRepository;

    public TurnoServiceImpl(ITurnoRepository turnoRepository,
                            IMascotaRepository mascotaRepository,
                            IPersonalMedicoRepository medicoRepository,
                            INivelPrioridadRepository prioridadRepository) {
        this.turnoRepository = turnoRepository;
        this.mascotaRepository = mascotaRepository;
        this.medicoRepository = medicoRepository;
        this.prioridadRepository = prioridadRepository;
    }

    @Override
    public List<TurnoResponseDTO> obtenerTodos() {
        return turnoRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TurnoResponseDTO obtenerPorId(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con el ID: " + id));
        return convertirAResponseDTO(turno);
    }

    @Override
    public TurnoResponseDTO crearTurno(TurnoRequestDTO request) {
        Mascota mascota = mascotaRepository.findById(request.getIdMascota())
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + request.getIdMascota()));

        NivelPrioridad prioridad = prioridadRepository.findById(request.getIdPrioridad())
                .orElseThrow(() -> new RuntimeException("Prioridad no encontrada con ID: " + request.getIdPrioridad()));

        PersonalMedico medico = null;
        if (request.getIdMedico() != null) {
            medico = medicoRepository.findById(request.getIdMedico()).orElse(null);
        }

        Turno turno = new Turno();
        turno.setCodigoTurno(request.getCodigoTurno());
        turno.setMascota(mascota);
        turno.setMedico(medico);
        turno.setPrioridad(prioridad);
        if (request.getEstado() != null) {
            turno.setEstado(request.getEstado());
        }

        Turno guardado = turnoRepository.save(turno);
        return convertirAResponseDTO(guardado);
    }

    @Override
    public TurnoResponseDTO actualizarTurno(Long id, TurnoRequestDTO request) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + id));

        if (request.getIdMascota() != null) {
            Mascota mascota = mascotaRepository.findById(request.getIdMascota())
                    .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
            turno.setMascota(mascota);
        }

        if (request.getIdPrioridad() != null) {
            NivelPrioridad prioridad = prioridadRepository.findById(request.getIdPrioridad())
                    .orElseThrow(() -> new RuntimeException("Prioridad no encontrada"));
            turno.setPrioridad(prioridad);
        }

        if (request.getIdMedico() != null) {
            PersonalMedico medico = medicoRepository.findById(request.getIdMedico()).orElse(null);
            turno.setMedico(medico);
        }

        if (request.getCodigoTurno() != null) turno.setCodigoTurno(request.getCodigoTurno());
        if (request.getEstado() != null) turno.setEstado(request.getEstado());

        Turno actualizado = turnoRepository.save(turno);
        return convertirAResponseDTO(actualizado);
    }

    @Override
    public void eliminarTurno(Long id) {
        if (!turnoRepository.existsById(id)) {
            throw new RuntimeException("No existe el turno con ID: " + id);
        }
        turnoRepository.deleteById(id);
    }

    private TurnoResponseDTO convertirAResponseDTO(Turno turno) {
        TurnoResponseDTO dto = new TurnoResponseDTO();
        dto.setIdTurno(turno.getIdTurno());
        dto.setCodigoTurno(turno.getCodigoTurno());
        dto.setNombreMascota(turno.getMascota() != null ? turno.getMascota().getNombre() : "N/A");
        dto.setNombreMedico(turno.getMedico() != null ? turno.getMedico().getNombre() : "Sin Asignar");
        dto.setNivelPrioridad(turno.getPrioridad() != null ? turno.getPrioridad().getDescripcion() : "Normal");
        dto.setEstado(turno.getEstado());
        dto.setFechaCreacion(turno.getFechaCreacion());
        return dto;
    }
}
