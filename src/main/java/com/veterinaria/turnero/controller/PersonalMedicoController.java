package com.veterinaria.turnero.controller;

import com.veterinaria.turnero.dto.MedicoDTO;
import com.veterinaria.turnero.service.IPersonalMedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/medicos")
@CrossOrigin(origins = "*")
@Tag(name = "Módulo de Personal Médico", description = "Operaciones CRUD para la gestión de los veterinarios de la clínica")
public class PersonalMedicoController {

    private final IPersonalMedicoService medicoService;

    public PersonalMedicoController(IPersonalMedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @GetMapping
    @Operation(summary = "Listar médicos", description = "Obtiene el equipo médico registrado")
    public ResponseEntity<List<MedicoDTO>> obtenerTodos() {
        return ResponseEntity.ok(medicoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar médico por ID", description = "Obtiene los detalles de un médico por su id")
    public ResponseEntity<MedicoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicoService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar médico", description = "Agrega un nuevo profesional médico al equipo")
    public ResponseEntity<MedicoDTO> crearMedico(@RequestBody MedicoDTO dto) {
        return new ResponseEntity<>(medicoService.crearMedico(dto), HttpStatus.CREATED);
    }
}
