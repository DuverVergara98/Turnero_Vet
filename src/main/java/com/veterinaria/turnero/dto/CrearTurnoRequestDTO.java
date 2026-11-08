package com.veterinaria.turnero.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CrearTurnoRequestDTO {
    private Long idTurno;
    private Long id;
    private String codigoTurno;
    private String estado;
    private Long mascotaId;
    private Long medicoId;
    private String motivoConsulta;
    private String servicio;
    
    private MascotaSubDTO mascota;
    private MedicoSubDTO medico;

    public Long getIdTurno() { return idTurno != null ? idTurno : id; }
    public void setIdTurno(Long idTurno) { this.idTurno = idTurno; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoTurno() { return codigoTurno; }
    public void setCodigoTurno(String codigoTurno) { this.codigoTurno = codigoTurno; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Long getMascotaId() { return mascotaId; }
    public void setMascotaId(Long mascotaId) { this.mascotaId = mascotaId; }

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }

    public String getMotivoConsulta() { 
        return motivoConsulta != null ? motivoConsulta : servicio; 
    }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }

    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }

    public MascotaSubDTO getMascota() { return mascota; }
    public void setMascota(MascotaSubDTO mascota) { this.mascota = mascota; }

    public MedicoSubDTO getMedico() { return medico; }
    public void setMedico(MedicoSubDTO medico) { this.medico = medico; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MascotaSubDTO {
        private String nombre;
        private String nombreTutor;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getNombreTutor() { return nombreTutor; }
        public void setNombreTutor(String nombreTutor) { this.nombreTutor = nombreTutor; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MedicoSubDTO {
        private String nombre;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }
}