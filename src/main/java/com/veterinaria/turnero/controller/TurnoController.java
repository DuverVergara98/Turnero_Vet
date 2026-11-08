package com.veterinaria.turnero.controller;

import com.veterinaria.turnero.dto.CrearTurnoRequestDTO;
import com.veterinaria.turnero.dto.TurnoDTO;
import com.veterinaria.turnero.model.EstadoTurno;
import com.veterinaria.turnero.service.TurnoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @PostMapping
    public ResponseEntity<TurnoDTO> crearTurno(@RequestBody CrearTurnoRequestDTO dto) {
        TurnoDTO creado = turnoService.crearTurno(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<TurnoDTO>> obtenerTodos() {
        return ResponseEntity.ok(turnoService.obtenerTodos());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<TurnoDTO> cambiarEstado(@PathVariable Long id, @RequestParam EstadoTurno estado) {
        return ResponseEntity.ok(turnoService.cambiarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTurno(@PathVariable Long id) {
        turnoService.eliminarTurno(id);
        return ResponseEntity.noContent().build();
    }
}