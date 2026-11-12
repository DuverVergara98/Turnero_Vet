package com.veterinaria.turnero.model;

import jakarta.persistence.*;

@Entity
@Table(name = "registros_turnero")
public class RegistroTurnero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String clave; // "TURNOS", "MASCOTAS", "MEDICOS"

    @Lob
    @Column(columnDefinition = "TEXT")
    private String jsonData;

    public RegistroTurnero() {}

    public RegistroTurnero(String clave, String jsonData) {
        this.clave = clave;
        this.jsonData = jsonData;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getJsonData() { return jsonData; }
    public void setJsonData(String jsonData) { this.jsonData = jsonData; }
}