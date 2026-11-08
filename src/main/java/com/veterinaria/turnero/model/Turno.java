package com.veterinaria.turnero.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_turno", nullable = false, unique = true)
    private String codigoTurno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTurno estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_atencion")
    private LocalDateTime fechaAtencion;

    @Column(name = "fecha_fin_consulta")
    private LocalDateTime fechaFinConsulta;

    @Column(name = "motivo_consulta")
    private String motivoConsulta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    public Turno() {}

    public Turno(Long id, String codigoTurno, EstadoTurno estado, LocalDateTime fechaCreacion, Mascota mascota, Medico medico, String motivoConsulta) {
        this.id = id;
        this.codigoTurno = codigoTurno;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.mascota = mascota;
        this.medico = medico;
        this.motivoConsulta = motivoConsulta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoTurno() { return codigoTurno; }
    public void setCodigoTurno(String codigoTurno) { this.codigoTurno = codigoTurno; }

    public EstadoTurno getEstado() { return estado; }
    public void setEstado(EstadoTurno estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaAtencion() { return fechaAtencion; }
    public void setFechaAtencion(LocalDateTime fechaAtencion) { this.fechaAtencion = fechaAtencion; }

    public LocalDateTime getFechaFinConsulta() { return fechaFinConsulta; }
    public void setFechaFinConsulta(LocalDateTime fechaFinConsulta) { this.fechaFinConsulta = fechaFinConsulta; }

    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }

    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }
}