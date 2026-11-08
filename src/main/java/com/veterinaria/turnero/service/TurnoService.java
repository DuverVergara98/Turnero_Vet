package com.veterinaria.turnero.service;

import com.veterinaria.turnero.dto.CrearTurnoRequestDTO;
import com.veterinaria.turnero.dto.TurnoDTO;
import com.veterinaria.turnero.model.EstadoTurno;
import com.veterinaria.turnero.model.Mascota;
import com.veterinaria.turnero.model.Medico;
import com.veterinaria.turnero.model.Turno;
import com.veterinaria.turnero.exception.BusinessException;
import com.veterinaria.turnero.exception.ResourceNotFoundException;
import com.veterinaria.turnero.repository.MascotaRepository;
import com.veterinaria.turnero.repository.MedicoRepository;
import com.veterinaria.turnero.repository.TurnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final MascotaRepository mascotaRepository;
    private final MedicoRepository medicoRepository;

    public TurnoService(TurnoRepository turnoRepository,
                        MascotaRepository mascotaRepository,
                        MedicoRepository medicoRepository) {
        this.turnoRepository = turnoRepository;
        this.mascotaRepository = mascotaRepository;
        this.medicoRepository = medicoRepository;
    }

    @Transactional
    public TurnoDTO crearTurno(CrearTurnoRequestDTO dto) {
        Mascota mascota = resolverMascota(dto);
        Medico medico = resolverMedico(dto);

        if (mascota == null || medico == null) {
            throw new BusinessException("Mascota y Medico son obligatorios para registrar un turno.");
        }

        boolean existePendiente = turnoRepository.existsByMascotaIdAndEstado(mascota.getId(), EstadoTurno.PENDIENTE);
        if (existePendiente) {
            throw new BusinessException("La mascota ya tiene un turno pendiente registrado.");
        }

        Turno turno = new Turno();
        turno.setMascota(mascota);
        turno.setMedico(medico);
        turno.setMotivoConsulta(dto.getMotivoConsulta() != null ? dto.getMotivoConsulta() : "Consulta General");
        turno.setFechaCreacion(LocalDateTime.now());
        turno.setEstado(EstadoTurno.PENDIENTE);
        turno.setCodigoTurno(generarCodigoTurno());

        Turno guardado = turnoRepository.save(turno);
        return convertirADTO(guardado);
    }

    private Mascota resolverMascota(CrearTurnoRequestDTO dto) {
        if (dto.getMascotaId() != null) {
            return mascotaRepository.findById(dto.getMascotaId()).orElse(null);
        }
        if (dto.getMascota() != null && dto.getMascota().getNombre() != null) {
            String nombre = dto.getMascota().getNombre();
            return mascotaRepository.findAll().stream()
                    .filter(m -> m.getNombre().equalsIgnoreCase(nombre))
                    .findFirst()
                    .orElseGet(() -> {
                        Mascota nueva = new Mascota();
                        nueva.setNombre(nombre);
                        nueva.setEspecie("Canino");
                        nueva.setNombrePropietario(dto.getMascota().getNombreTutor() != null ? dto.getMascota().getNombreTutor() : "Tutor");
                        return mascotaRepository.save(nueva);
                    });
        }
        return mascotaRepository.findAll().stream().findFirst().orElse(null);
    }

    private Medico resolverMedico(CrearTurnoRequestDTO dto) {
        if (dto.getMedicoId() != null) {
            return medicoRepository.findById(dto.getMedicoId()).orElse(null);
        }
        if (dto.getMedico() != null && dto.getMedico().getNombre() != null) {
            String nombre = dto.getMedico().getNombre();
            return medicoRepository.findAll().stream()
                    .filter(m -> m.getNombre().equalsIgnoreCase(nombre))
                    .findFirst()
                    .orElseGet(() -> {
                        Medico nuevo = new Medico();
                        nuevo.setNombre(nombre);
                        nuevo.setEspecialidad("Medicina General");
                        return medicoRepository.save(nuevo);
                    });
        }
        return medicoRepository.findAll().stream().findFirst().orElse(null);
    }

    @Transactional(readOnly = true)
    public List<TurnoDTO> obtenerTodos() {
        return turnoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TurnoDTO cambiarEstado(Long id, EstadoTurno nuevoEstado) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con ID: " + id));

        if (turno.getEstado() == EstadoTurno.CANCELADO || turno.getEstado() == EstadoTurno.ATENDIDO) {
            throw new BusinessException("No se puede cambiar el estado de un turno finalizado o cancelado.");
        }

        turno.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoTurno.EN_ATENCION) {
            turno.setFechaAtencion(LocalDateTime.now());
        }

        return convertirADTO(turnoRepository.save(turno));
    }

    @Transactional
    public TurnoDTO cambiarEstadoTurno(Long id, EstadoTurno nuevoEstado) {
        return cambiarEstado(id, nuevoEstado);
    }

    @Transactional(readOnly = true)
    public List<TurnoDTO> obtenerPendientesViaJdbcDao() {
        return turnoRepository.findByEstado(EstadoTurno.PENDIENTE).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarTurno(Long id) {
        if (!turnoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Turno no encontrado con ID: " + id);
        }
        turnoRepository.deleteById(id);
    }

    private String generarCodigoTurno() {
        long count = turnoRepository.count() + 1;
        return String.format("T-%03d", count);
    }

    private TurnoDTO convertirADTO(Turno turno) {
        if (turno == null) return null;
        TurnoDTO dto = new TurnoDTO();
        dto.setId(turno.getId());
        dto.setCodigoTurno(turno.getCodigoTurno());

        String estadoStr = "En espera";
        if (turno.getEstado() == EstadoTurno.EN_ATENCION) estadoStr = "Atendiendo";
        else if (turno.getEstado() == EstadoTurno.ATENDIDO) estadoStr = "Atendido";
        else if (turno.getEstado() == EstadoTurno.CANCELADO) estadoStr = "Cancelado";
        dto.setEstado(estadoStr);

        dto.setFechaCreacion(turno.getFechaCreacion());
        dto.setFechaAtencion(turno.getFechaAtencion());
        dto.setMotivoConsulta(turno.getMotivoConsulta());
        dto.setServicio(turno.getMotivoConsulta());

        if (turno.getFechaCreacion() != null) {
            dto.setTimestampRegistro(turno.getFechaCreacion().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }

        if (turno.getMascota() != null) {
            dto.setMascotaId(turno.getMascota().getId());
            dto.setNombreMascota(turno.getMascota().getNombre());
            dto.setNombrePropietario(turno.getMascota().getNombrePropietario());
            dto.setMascota(new TurnoDTO.MascotaDTO(turno.getMascota().getNombre(), turno.getMascota().getNombrePropietario()));
        } else {
            dto.setMascota(new TurnoDTO.MascotaDTO("Mascota", "Tutor"));
        }

        if (turno.getMedico() != null) {
            dto.setMedicoId(turno.getMedico().getId());
            dto.setNombreMedico(turno.getMedico().getNombre());
            dto.setMedico(new TurnoDTO.MedicoDTO(turno.getMedico().getNombre()));
        } else {
            dto.setMedico(new TurnoDTO.MedicoDTO("Médico General"));
        }

        dto.setPrioridad(new TurnoDTO.PrioridadDTO(2, "Media - Consulta"));
        return dto;
    }
}