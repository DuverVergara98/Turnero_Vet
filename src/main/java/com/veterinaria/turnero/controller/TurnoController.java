package com.veterinaria.turnero.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/turnos")
@CrossOrigin(origins = "*")
public class TurnoController {

    private final List<Map<String, Object>> turnos = new CopyOnWriteArrayList<>();

    public TurnoController() {
        long ahora = System.currentTimeMillis();
        
        Map<String, Object> t1 = new HashMap<>();
        t1.put("idTurno", 1);
        t1.put("codigoTurno", "T-01");
        t1.put("mascota", Map.of("nombre", "Tobias", "nombreTutor", "Duver Vergara"));
        t1.put("medico", Map.of("nombre", "Dra. Laura Pérez"));
        t1.put("servicio", "Consulta General");
        t1.put("prioridad", Map.of("idPrioridad", 1, "nombrePrioridad", "Alta - Urgencia"));
        t1.put("estado", "En espera");
        t1.put("consultorio", "Consultorio 1");
        t1.put("timestampLlamada", 1L);
        t1.put("timestampRegistro", ahora - (15 * 60 * 1000));
        t1.put("timestampInicioAtencion", null);
        t1.put("timestampFinConsulta", null);
        turnos.add(t1);

        Map<String, Object> t2 = new HashMap<>();
        t2.put("idTurno", 2);
        t2.put("codigoTurno", "T-02");
        t2.put("mascota", Map.of("nombre", "Luna", "nombreTutor", "Diana"));
        t2.put("medico", Map.of("nombre", "Dr. Carlos Gómez"));
        t2.put("servicio", "Vacunación");
        t2.put("prioridad", Map.of("idPrioridad", 2, "nombrePrioridad", "Media - Consulta"));
        t2.put("estado", "Atendiendo");
        t2.put("consultorio", "Consultorio 2");
        t2.put("timestampLlamada", 2L);
        t2.put("timestampRegistro", ahora - (30 * 60 * 1000));
        t2.put("timestampInicioAtencion", ahora - (10 * 60 * 1000));
        t2.put("timestampFinConsulta", null);
        turnos.add(t2);
    }

    @GetMapping
    public List<Map<String, Object>> obtenerTodos() {
        return turnos;
    }

    @PostMapping
    public Map<String, Object> guardarTurno(@RequestBody Map<String, Object> turno) {
        if (!turno.containsKey("idTurno") || turno.get("idTurno") == null) {
            turno.put("idTurno", turnos.size() + 1);
        }
        
        int index = -1;
        for (int i = 0; i < turnos.size(); i++) {
            if (Objects.equals(turnos.get(i).get("codigoTurno"), turno.get("codigoTurno"))) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            turnos.set(index, turno);
        } else {
            turnos.add(turno);
        }
        return turno;
    }

    @PutMapping("/{index}")
    public Map<String, Object> actualizarTurno(@PathVariable int index, @RequestBody Map<String, Object> turno) {
        if (index >= 0 && index < turnos.size()) {
            turnos.set(index, turno);
            return turno;
        }
        return null;
    }

    @DeleteMapping("/{index}")
    public String eliminarTurno(@PathVariable int index) {
        if (index >= 0 && index < turnos.size()) {
            turnos.remove(index);
            return "OK";
        }
        return "ERROR";
    }
}
