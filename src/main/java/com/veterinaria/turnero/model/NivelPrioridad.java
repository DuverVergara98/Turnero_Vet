package com.veterinaria.turnero.model;

import jakarta.persistence.*;

@Entity
@Table(name = "NIVEL_PRIORIDAD")
public class NivelPrioridad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prioridad")
    private Long idPrioridad;

    @Column(nullable = false, length = 30)
    private String descripcion;

    @Column(length = 10)
    private String colorHex;

    public NivelPrioridad() {}

    public NivelPrioridad(Long idPrioridad, String descripcion, String colorHex) {
        this.idPrioridad = idPrioridad;
        this.descripcion = descripcion;
        this.colorHex = colorHex;
    }

    public Long getIdPrioridad() { return idPrioridad; }
    public void setIdPrioridad(Long idPrioridad) { this.idPrioridad = idPrioridad; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
}
