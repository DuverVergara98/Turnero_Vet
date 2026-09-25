package com.veterinaria.turnero.service;

import com.veterinaria.turnero.model.Turno;
import com.veterinaria.turnero.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    public List<Turno> obtenerTodos() {
        return turnoRepository.findAll();
    }

    public Turno obtenerPorId(int id) {
        return turnoRepository.findById(id);
    }

    public String registrarTurno(Turno turno) {
        if (turno.getEstado() == null) {
            turno.setEstado("En espera");
        }
        int resultado = turnoRepository.save(turno);
        return resultado > 0 ? "Turno registrado exitosamente." : "Error al registrar el turno.";
    }

    public String actualizarTurno(Turno turno, int id) {
        int resultado = turnoRepository.update(turno, id);
        return resultado > 0 ? "Turno actualizado correctamente." : "Error al actualizar el turno.";
    }

    public String eliminarTurno(int id) {
        int resultado = turnoRepository.delete(id);
        return resultado > 0 ? "Turno eliminado correctamente." : "Error al eliminar el turno.";
    }
}
