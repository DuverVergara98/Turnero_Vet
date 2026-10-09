package com.veterinaria.turnero.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TURNO")
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_turno")
    private Long idTurno;

    @Column(name = "codigo_turno", nullable = false, length = 20)
    private String codigoTurno;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_mascota", nullable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_medico")
    private PersonalMedico medico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_prioridad", nullable = false)
    private NivelPrioridad prioridad;

    @Column(length = 20)
    private String estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    public Turno() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "EN_ESPERA";
    }

    public Long getIdTurno() { return idTurno; }
    public void setIdTurno(Long idTurno) { this.idTurno = idTurno; }
    public String getCodigoTurno() { return codigoTurno; }
    public void setCodigoTurno(String codigoTurno) { this.codigoTurno = codigoTurno; }
    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }
    public PersonalMedico getMedico() { return medico; }
    public void setMedico(PersonalMedico medico) { this.medico = medico; }
    public NivelPrioridad getPrioridad() { return prioridad; }
    public void setPrioridad(NivelPrioridad prioridad) { this.prioridad = prioridad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
