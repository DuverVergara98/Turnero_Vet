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

import java.time.Duration;
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
    public TurnoDTO crearOActualizarTurno(CrearTurnoRequestDTO dto) {
        Mascota mascota = resolverMascota(dto);
        Medico medico = resolverMedico(dto);

        if (mascota == null || medico == null) {
            throw new BusinessException("Mascota y Medico son obligatorios para registrar un turno.");
        }

        Turno turno;
        if (dto.getIdTurno() != null && turnoRepository.existsById(dto.getIdTurno())) {
            turno = turnoRepository.findById(dto.getIdTurno()).get();
        } else {
            turno = new Turno();
            turno.setFechaCreacion(LocalDateTime.now());
            turno.setCodigoTurno(dto.getCodigoTurno() != null ? dto.getCodigoTurno() : generarCodigoTurno());
        }

        turno.setMascota(mascota);
        turno.setMedico(medico);
        turno.setMotivoConsulta(dto.getMotivoConsulta() != null ? dto.getMotivoConsulta() : "Consulta General");
        
        EstadoTurno nuevoEstado = MapearEstado(dto.getEstado());

        if (nuevoEstado == EstadoTurno.PENDIENTE) {
            turno.setFechaAtencion(null);
            turno.setFechaFinConsulta(null);
        } else if (nuevoEstado == EstadoTurno.EN_ATENCION) {
            if (turno.getFechaAtencion() == null) {
                turno.setFechaAtencion(LocalDateTime.now());
            }
            turno.setFechaFinConsulta(null);
        } else if (nuevoEstado == EstadoTurno.ATENDIDO) {
            if (turno.getFechaAtencion() == null) {
                turno.setFechaAtencion(LocalDateTime.now());
            }
            if (turno.getFechaFinConsulta() == null) {
                turno.setFechaFinConsulta(LocalDateTime.now());
            }
        }

        turno.setEstado(nuevoEstado);

        Turno guardado = turnoRepository.save(turno);
        return convertirADTO(guardado, dto);
    }

    @Transactional
    public TurnoDTO crearTurno(CrearTurnoRequestDTO dto) {
        return crearOActualizarTurno(dto);
    }

    private EstadoTurno MapearEstado(String estadoStr) {
        if (estadoStr == null) return EstadoTurno.PENDIENTE;
        String e = estadoStr.trim().toLowerCase();
        if (e.contains("llamando")) return EstadoTurno.LLAMANDO;
        if (e.contains("atendiendo") || e.contains("en_atencion")) return EstadoTurno.EN_ATENCION;
        if (e.contains("atendido")) return EstadoTurno.ATENDIDO;
        if (e.contains("cancelado")) return EstadoTurno.CANCELADO;
        return EstadoTurno.PENDIENTE;
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

        if (nuevoEstado == EstadoTurno.PENDIENTE) {
            turno.setFechaAtencion(null);
            turno.setFechaFinConsulta(null);
        } else if (nuevoEstado == EstadoTurno.EN_ATENCION) {
            if (turno.getFechaAtencion() == null) turno.setFechaAtencion(LocalDateTime.now());
            turno.setFechaFinConsulta(null);
        } else if (nuevoEstado == EstadoTurno.ATENDIDO) {
            if (turno.getFechaAtencion() == null) turno.setFechaAtencion(LocalDateTime.now());
            if (turno.getFechaFinConsulta() == null) turno.setFechaFinConsulta(LocalDateTime.now());
        }

        turno.setEstado(nuevoEstado);
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
        if (turnoRepository.existsById(id)) {
            turnoRepository.deleteById(id);
            return;
        }

        List<Turno> todos = turnoRepository.findAll();
        int index = id.intValue();
        if (index >= 0 && index < todos.size()) {
            turnoRepository.deleteById(todos.get(index).getId());
        } else if (!todos.isEmpty()) {
            turnoRepository.deleteById(todos.get(todos.size() - 1).getId());
        }
    }

    private String generarCodigoTurno() {
        long count = turnoRepository.count() + 1;
        return String.format("T-%03d", count);
    }

    private TurnoDTO convertirADTO(Turno turno) {
        return convertirADTO(turno, null);
    }

    private TurnoDTO convertirADTO(Turno turno, CrearTurnoRequestDTO dtoRequest) {
        if (turno == null) return null;
        TurnoDTO dto = new TurnoDTO();
        dto.setId(turno.getId());
        dto.setIdTurno(turno.getId());
        dto.setCodigoTurno(turno.getCodigoTurno());

        String estadoStr = (dtoRequest != null && dtoRequest.getEstado() != null) ? dtoRequest.getEstado() : null;
        if (estadoStr == null) {
            if (turno.getEstado() == EstadoTurno.LLAMANDO) estadoStr = "Llamando";
            else if (turno.getEstado() == EstadoTurno.EN_ATENCION) estadoStr = "Atendiendo";
            else if (turno.getEstado() == EstadoTurno.ATENDIDO) estadoStr = "Atendido";
            else if (turno.getEstado() == EstadoTurno.CANCELADO) estadoStr = "Cancelado";
            else estadoStr = "En espera";
        }
        dto.setEstado(estadoStr);

        dto.setFechaCreacion(turno.getFechaCreacion());
        dto.setFechaAtencion(turno.getFechaAtencion());
        dto.setMotivoConsulta(turno.getMotivoConsulta());
        dto.setServicio(turno.getMotivoConsulta());

        long regTs = turno.getFechaCreacion() != null ? 
            turno.getFechaCreacion().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : System.currentTimeMillis();
        dto.setTimestampRegistro(regTs);

        long atencionTs = turno.getFechaAtencion() != null ? 
            turno.getFechaAtencion().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : regTs;
        dto.setTimestampInicioAtencion(atencionTs);

        long finTs = turno.getFechaFinConsulta() != null ?
            turno.getFechaFinConsulta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : System.currentTimeMillis();
        dto.setTimestampFinConsulta(finTs);

        // 1. Tiempo de Espera
        LocalDateTime finEspera = turno.getFechaAtencion() != null ? turno.getFechaAtencion() : LocalDateTime.now();
        long minutosEspera = Math.max(0, Duration.between(turno.getFechaCreacion(), finEspera).toMinutes());
        dto.setTiempoEsperaTexto(minutosEspera + " min");

        // 2. Duración Consulta (Estricto: Oculto en curso, sólo visible al finalizar)
        if (turno.getEstado() == EstadoTurno.ATENDIDO || (estadoStr != null && estadoStr.equalsIgnoreCase("Atendido"))) {
            if (turno.getFechaAtencion() != null && turno.getFechaFinConsulta() != null) {
                long minutosConsulta = Math.max(1, Duration.between(turno.getFechaAtencion(), turno.getFechaFinConsulta()).toMinutes());
                dto.setDuracionConsultaTexto(minutosConsulta + " min");
            } else {
                dto.setDuracionConsultaTexto("1 min");
            }
        } else if (turno.getEstado() == EstadoTurno.EN_ATENCION || (estadoStr != null && estadoStr.equalsIgnoreCase("Atendiendo"))) {
            dto.setDuracionConsultaTexto("En curso");
        } else {
            dto.setDuracionConsultaTexto("-");
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