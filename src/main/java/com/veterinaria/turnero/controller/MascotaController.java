package com.veterinaria.turnero.controller;

import com.veterinaria.turnero.dto.MascotaDTO;
import com.veterinaria.turnero.service.IMascotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@CrossOrigin(origins = "*")
@Tag(name = "Módulo de Mascotas", description = "Operaciones CRUD para la gestión de pacientes animales")
public class MascotaController {

    private final IMascotaService mascotaService;

    public MascotaController(IMascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @GetMapping
    @Operation(summary = "Listar mascotas", description = "Devuelve todos los pacientes registrados en la clínica")
    public ResponseEntity<List<MascotaDTO>> obtenerTodas() {
        return ResponseEntity.ok(mascotaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar mascota por ID", description = "Consulta la información de una mascota en particular")
    public ResponseEntity<MascotaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar mascota", description = "Agrega una nueva mascota a la base de datos de la clínica")
    public ResponseEntity<MascotaDTO> crearMascota(@RequestBody MascotaDTO dto) {
        return new ResponseEntity<>(mascotaService.crearMascota(dto), HttpStatus.CREATED);
    }
}
