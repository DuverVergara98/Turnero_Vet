package com.veterinaria.turnero.model;

import java.sql.Date;
import java.sql.Time;

public class Turno {
    private int idTurno;
    private String codigoTurno;
    private int idMascota;
    private String nombreMascota;
    private int idPrioridad;
    private String nombrePrioridad;
    private int idMedico;
    private String nombreMedico;
    private Date fecha;
    private Time hora;
    private String estado;

    public Turno() {}

    public int getIdTurno() { return idTurno; }
    public void setIdTurno(int idTurno) { this.idTurno = idTurno; }

    public String getCodigoTurno() { return codigoTurno; }
    public void setCodigoTurno(String codigoTurno) { this.codigoTurno = codigoTurno; }

    public int getIdMascota() { return idMascota; }
    public void setIdMascota(int idMascota) { this.idMascota = idMascota; }

    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }

    public int getIdPrioridad() { return idPrioridad; }
    public void setIdPrioridad(int idPrioridad) { this.idPrioridad = idPrioridad; }

    public String getNombrePrioridad() { return nombrePrioridad; }
    public void setNombrePrioridad(String nombrePrioridad) { this.nombrePrioridad = nombrePrioridad; }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getNombreMedico() { return nombreMedico; }
    public void setNombreMedico(String nombreMedico) { this.nombreMedico = nombreMedico; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Time getHora() { return hora; }
    public void setHora(Time hora) { this.hora = hora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
