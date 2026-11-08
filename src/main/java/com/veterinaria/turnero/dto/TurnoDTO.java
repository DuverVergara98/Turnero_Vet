package com.veterinaria.turnero.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.veterinaria.turnero.model.EstadoTurno;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TurnoDTO {
    private Long id;
    private Long idTurno;
    private String codigoTurno;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAtencion;
    private String motivoConsulta;
    private String servicio;
    private Long timestampRegistro;

    private Long mascotaId;
    private String nombreMascota;
    private String nombrePropietario;

    private Long medicoId;
    private String nombreMedico;

    private MascotaDTO mascota;
    private MedicoDTO medico;
    private PrioridadDTO prioridad;

    public Long getId() { return id; }
    public void setId(Long id) { 
        this.id = id; 
        this.idTurno = id;
    }

    public Long getIdTurno() { return idTurno; }
    public void setIdTurno(Long idTurno) { this.idTurno = idTurno; }

    public String getCodigoTurno() { return codigoTurno; }
    public void setCodigoTurno(String codigoTurno) { this.codigoTurno = codigoTurno; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setEstado(EstadoTurno estadoTurno) { 
        this.estado = estadoTurno != null ? estadoTurno.name() : null; 
    }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaAtencion() { return fechaAtencion; }
    public void setFechaAtencion(LocalDateTime fechaAtencion) { this.fechaAtencion = fechaAtencion; }

    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) { 
        this.motivoConsulta = motivoConsulta; 
        this.servicio = motivoConsulta;
    }

    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }

    public Long getTimestampRegistro() { return timestampRegistro; }
    public void setTimestampRegistro(Long timestampRegistro) { this.timestampRegistro = timestampRegistro; }

    public Long getMascotaId() { return mascotaId; }
    public void setMascotaId(Long mascotaId) { this.mascotaId = mascotaId; }

    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }

    public String getNombrePropietario() { return nombrePropietario; }
    public void setNombrePropietario(String nombrePropietario) { this.nombrePropietario = nombrePropietario; }

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }

    public String getNombreMedico() { return nombreMedico; }
    public void setNombreMedico(String nombreMedico) { this.nombreMedico = nombreMedico; }

    public MascotaDTO getMascota() { return mascota; }
    public void setMascota(MascotaDTO mascota) { this.mascota = mascota; }

    public MedicoDTO getMedico() { return medico; }
    public void setMedico(MedicoDTO medico) { this.medico = medico; }

    public PrioridadDTO getPrioridad() { return prioridad; }
    public void setPrioridad(PrioridadDTO prioridad) { this.prioridad = prioridad; }

    public static class MascotaDTO {
        private String nombre;
        private String nombreTutor;

        public MascotaDTO() {}
        public MascotaDTO(String nombre, String nombreTutor) {
            this.nombre = nombre;
            this.nombreTutor = nombreTutor;
        }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getNombreTutor() { return nombreTutor; }
        public void setNombreTutor(String nombreTutor) { this.nombreTutor = nombreTutor; }
    }

    public static class MedicoDTO {
        private String nombre;

        public MedicoDTO() {}
        public MedicoDTO(String nombre) { this.nombre = nombre; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }

    public static class PrioridadDTO {
        private Integer idPrioridad;
        private String nombrePrioridad;

        public PrioridadDTO() {}
        public PrioridadDTO(Integer idPrioridad, String nombrePrioridad) {
            this.idPrioridad = idPrioridad;
            this.nombrePrioridad = nombrePrioridad;
        }
        public Integer getIdPrioridad() { return idPrioridad; }
        public void setIdPrioridad(Integer idPrioridad) { this.idPrioridad = idPrioridad; }
        public String getNombrePrioridad() { return nombrePrioridad; }
        public void setNombrePrioridad(String nombrePrioridad) { this.nombrePrioridad = nombrePrioridad; }
    }
}