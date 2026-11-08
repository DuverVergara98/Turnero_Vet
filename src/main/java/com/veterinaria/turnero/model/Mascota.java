package com.veterinaria.turnero.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mascotas")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "especie", nullable = false, length = 50)
    private String especie;

    @Column(name = "nombre_propietario", nullable = false, length = 100)
    private String nombrePropietario;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Turno> turnos = new ArrayList<>();

    public Mascota() {}

    public Mascota(Long id, String nombre, String especie, String nombrePropietario) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.nombrePropietario = nombrePropietario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getNombrePropietario() { return nombrePropietario; }
    public void setNombrePropietario(String nombrePropietario) { this.nombrePropietario = nombrePropietario; }

    public List<Turno> getTurnos() { return turnos; }
    public void setTurnos(List<Turno> turnos) { this.turnos = turnos; }
}
