package com.veterinario.model;

import java.util.List;

public class Enfermedad {
    private String mascotaId;    // PK
    private String enfermedadId;    // SK
    private String enfermedad;
    private List<String> tratamiento;

    public Enfermedad() {
    }

    public String getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(String mascotaId) {
        this.mascotaId = mascotaId;
    }

    public String getEnfermedadId() {
        return enfermedadId;
    }

    public void setEnfermedadId(String enfermedadId) {
        this.enfermedadId = enfermedadId;
    }

    public String getEnfermedad() {
        return enfermedad;
    }

    public void setEnfermedad(String enfermedad) {
        this.enfermedad = enfermedad;
    }

    public List<String> getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(List<String> tratamiento) {
        this.tratamiento = tratamiento;
    }
}
