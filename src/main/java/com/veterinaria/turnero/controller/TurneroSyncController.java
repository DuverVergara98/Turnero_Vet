package com.veterinaria.turnero.controller;

import com.veterinaria.turnero.model.RegistroTurnero;
import com.veterinaria.turnero.repository.RegistroTurneroRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
public class TurneroSyncController {

    private final RegistroTurneroRepository repository;

    public TurneroSyncController(RegistroTurneroRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "/{clave}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerDatos(@PathVariable String clave) {
        String data = repository.findByClave(clave.toUpperCase())
                .map(RegistroTurnero::getJsonData)
                .filter(json -> json != null && !json.trim().isEmpty())
                .orElse("[]");
        return ResponseEntity.ok(data);
    }

    @PostMapping(value = "/{clave}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> guardarDatos(@PathVariable String clave, @RequestBody String jsonBody) {
        String key = clave.toUpperCase();
        RegistroTurnero registro = repository.findByClave(key)
                .orElse(new RegistroTurnero(key, "[]"));
        registro.setJsonData(jsonBody);
        repository.save(registro);
        return ResponseEntity.ok(jsonBody);
    }
}