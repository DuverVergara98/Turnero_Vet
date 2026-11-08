package com.veterinaria.turnero.config;

import com.veterinaria.turnero.model.*;
import com.veterinaria.turnero.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final MedicoRepository medicoRepository;
    private final MascotaRepository mascotaRepository;
    private final TurnoRepository turnoRepository;

    public DataLoader(MedicoRepository medicoRepository, MascotaRepository mascotaRepository, TurnoRepository turnoRepository) {
        this.medicoRepository = medicoRepository;
        this.mascotaRepository = mascotaRepository;
        this.turnoRepository = turnoRepository;
    }

    @Override
    public void run(String... args) {
        try {
            if (medicoRepository.count() == 0) {
                Medico m1 = medicoRepository.save(new Medico(null, "Dra. Diana Mogollon", "Medicina Interna"));
                Medico m2 = medicoRepository.save(new Medico(null, "Dr. Carlos Vergara", "Cirugia General"));

                Mascota p1 = mascotaRepository.save(new Mascota(null, "Firulais", "Canino", "Duver Vergara"));
                Mascota p2 = mascotaRepository.save(new Mascota(null, "Machia", "Felino", "Diana Lara"));

                Turno t1 = new Turno(null, "T-001", EstadoTurno.PENDIENTE, LocalDateTime.now(), p1, m1, "Consulta de control");
                Turno t2 = new Turno(null, "T-002", EstadoTurno.EN_ATENCION, LocalDateTime.now(), p2, m2, "Vacunacion");

                turnoRepository.save(t1);
                turnoRepository.save(t2);
                System.out.println("=== DATOS INICIALES CARGADOS EN LA BD PERSISTENTE ===");
            }
        } catch (Exception e) {
            System.err.println("Nota sobre carga inicial: " + e.getMessage());
        }
    }
}