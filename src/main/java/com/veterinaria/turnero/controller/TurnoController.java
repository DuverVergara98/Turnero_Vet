package com.veterinaria.turnero.controller;

import com.veterinaria.turnero.dto.TurnoRequestDTO;
import com.veterinaria.turnero.dto.TurnoResponseDTO;
import com.veterinaria.turnero.service.ITurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@CrossOrigin(origins = "*")
@Tag(name = "Módulo de Turnos", description = "Operaciones CRUD para la gestión de turnos veterinarios en atención")
public class TurnoController {

    private final ITurnoService turnoService;

    public TurnoController(ITurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los turnos", description = "Retorna el listado completo de turnos con nombres de paciente, médico y prioridad")
    public ResponseEntity<List<TurnoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(turnoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar turno por ID", description = "Retorna el detalle del turno solicitado por su identificador primario")
    public ResponseEntity<TurnoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo turno", description = "Registra un turno asociándolo a una mascota y nivel de prioridad preexistentes")
    public ResponseEntity<TurnoResponseDTO> crearTurno(@RequestBody TurnoRequestDTO request) {
        return new ResponseEntity<>(turnoService.crearTurno(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar turno", description = "Modifica el estado, prioridad o asignación médica de un turno existente")
    public ResponseEntity<TurnoResponseDTO> actualizarTurno(@PathVariable Long id, @RequestBody TurnoRequestDTO request) {
        return ResponseEntity.ok(turnoService.actualizarTurno(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar turno", description = "Remueve permanentemente un turno del sistema")
    public ResponseEntity<Void> eliminarTurno(@PathVariable Long id) {
        turnoService.eliminarTurno(id);
        return ResponseEntity.noContent().build();
    }
}
