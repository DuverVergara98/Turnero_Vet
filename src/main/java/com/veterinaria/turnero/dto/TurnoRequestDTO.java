package com.veterinaria.turnero.dto;

public class TurnoRequestDTO {
    private String codigoTurno;
    private Long idMascota;
    private Long idMedico;
    private Long idPrioridad;
    private String estado;

    public TurnoRequestDTO() {}

    public String getCodigoTurno() { return codigoTurno; }
    public void setCodigoTurno(String codigoTurno) { this.codigoTurno = codigoTurno; }
    public Long getIdMascota() { return idMascota; }
    public void setIdMascota(Long idMascota) { this.idMascota = idMascota; }
    public Long getIdMedico() { return idMedico; }
    public void setIdMedico(Long idMedico) { this.idMedico = idMedico; }
    public Long getIdPrioridad() { return idPrioridad; }
    public void setIdPrioridad(Long idPrioridad) { this.idPrioridad = idPrioridad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
