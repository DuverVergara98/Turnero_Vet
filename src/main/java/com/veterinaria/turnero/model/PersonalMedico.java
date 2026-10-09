package com.veterinaria.turnero.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PERSONAL_MEDICO")
public class PersonalMedico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medico")
    private Long idMedico;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 50)
    private String especialidad;

    @Column(length = 20)
    private String tarjetaProfesional;

    public PersonalMedico() {}

    public PersonalMedico(Long idMedico, String nombre, String especialidad, String tarjetaProfesional) {
        this.idMedico = idMedico;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.tarjetaProfesional = tarjetaProfesional;
    }

    public Long getIdMedico() { return idMedico; }
    public void setIdMedico(Long idMedico) { this.idMedico = idMedico; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEspecie() { return especialidad; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public String getTarjetaProfesional() { return tarjetaProfesional; }
    public void setTarjetaProfesional(String tarjetaProfesional) { this.tarjetaProfesional = tarjetaProfesional; }
}
