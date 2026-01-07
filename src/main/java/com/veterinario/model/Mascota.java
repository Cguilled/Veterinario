package com.veterinario.model;

import java.util.UUID;

public class Mascota {
    private String mascotaId;    // PK
    private String nombre;
    private Integer edad;
    private String raza;
    private Integer numVacunas;
    private String motivoConsulta;

    public Mascota() {
    }

    public String getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(String mascotaId) {
        this.mascotaId = mascotaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public Integer getNumVacunas() {
        return numVacunas;
    }

    public void setNumVacunas(Integer numVacunas) {
        this.numVacunas = numVacunas;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }
}
