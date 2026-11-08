package com.veterinaria.turnero.service;

import com.veterinaria.turnero.dao.TurnoJdbcDao;
import com.veterinaria.turnero.dto.CrearTurnoRequestDTO;
import com.veterinaria.turnero.dto.TurnoDTO;
import com.veterinaria.turnero.exception.BusinessException;
import com.veterinaria.turnero.model.*;
import com.veterinaria.turnero.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TurnoServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private TurnoJdbcDao turnoJdbcDao;

    @InjectMocks
    private TurnoService turnoService;

    private Mascota mascotaMock;
    private Medico medicoMock;
    private Turno turnoMock;

    @BeforeEach
    void setUp() {
        mascotaMock = new Mascota(1L, "Firulais", "Canino", "Duver Vergara");
        medicoMock = new Medico(1L, "Dra. Diana Mogollon", "Medicina Interna");
        turnoMock = new Turno(1L, "T-001", EstadoTurno.PENDIENTE, LocalDateTime.now(), mascotaMock, medicoMock, "Consulta de control");
    }

    @Test
    void crearTurno_Exito() {
        CrearTurnoRequestDTO request = new CrearTurnoRequestDTO();
        request.setMascotaId(1L);
        request.setMedicoId(1L);
        request.setMotivoConsulta("Consulta de control");

        when(turnoRepository.existsByMascotaIdAndEstado(1L, EstadoTurno.PENDIENTE)).thenReturn(false);
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascotaMock));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medicoMock));
        when(turnoRepository.count()).thenReturn(0L);
        when(turnoRepository.save(any(Turno.class))).thenReturn(turnoMock);

        TurnoDTO resultado = turnoService.crearTurno(request);

        assertNotNull(resultado);
        assertEquals("T-001", resultado.getCodigoTurno());
        assertEquals("Firulais", resultado.getNombreMascota());
        verify(turnoRepository, times(1)).save(any(Turno.class));
    }

    @Test
    void crearTurno_TurnoDuplicado() {
        CrearTurnoRequestDTO request = new CrearTurnoRequestDTO();
        request.setMascotaId(1L);
        request.setMedicoId(1L);

        when(turnoRepository.existsByMascotaIdAndEstado(1L, EstadoTurno.PENDIENTE)).thenReturn(true);

        assertThrows(BusinessException.class, () -> turnoService.crearTurno(request));
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    void cambiarEstadoTurno_Exito() {
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turnoMock));
        when(turnoRepository.save(any(Turno.class))).thenReturn(turnoMock);

        TurnoDTO resultado = turnoService.cambiarEstadoTurno(1L, EstadoTurno.EN_ATENCION);

        assertNotNull(resultado);
        verify(turnoRepository, times(1)).save(any(Turno.class));
    }

    @Test
    void cambiarEstadoTurno_TurnoFinalizado() {
        turnoMock.setEstado(EstadoTurno.ATENDIDO);
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turnoMock));

        assertThrows(BusinessException.class, () -> turnoService.cambiarEstadoTurno(1L, EstadoTurno.EN_ATENCION));
    }

    @Test
    void obtenerPendientesViaJdbcDao() {
        TurnoDTO dtoMock = new TurnoDTO();
        dtoMock.setCodigoTurno("T-001");
        when(turnoJdbcDao.obtenerResumenTurnosPendientes()).thenReturn(Collections.singletonList(dtoMock));

        List<TurnoDTO> pendientes = turnoService.obtenerPendientesViaJdbcDao();

        assertNotNull(pendientes);
        assertEquals(1, pendientes.size());
        verify(turnoJdbcDao, times(1)).obtenerResumenTurnosPendientes();
    }
}