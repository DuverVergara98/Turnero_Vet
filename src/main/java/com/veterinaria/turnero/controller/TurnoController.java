package com.veterinaria.turnero.controller;

import com.veterinaria.turnero.model.Turno;
import com.veterinaria.turnero.service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    @GetMapping
    public List<Turno> listarTurnos() {
        return turnoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Turno obtenerTurno(@PathVariable int id) {
        return turnoService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearTurno(@RequestBody Turno turno) {
        try {
            turnoService.registrarTurno(turno);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Turno registrado exitosamente",
                "status", HttpStatus.OK.value()
            ));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public String actualizarTurno(@RequestBody Turno turno, @PathVariable int id) {
        return turnoService.actualizarTurno(turno, id);
    }

    @DeleteMapping("/{id}")
    public String eliminarTurno(@PathVariable int id) {
        return turnoService.eliminarTurno(id);
    }
}
