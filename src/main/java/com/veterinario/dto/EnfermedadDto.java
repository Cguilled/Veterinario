package com.veterinario.dto;

import java.util.List;

public class EnfermedadDto {
    private String enfermedadId;
    private String mascotaId;
    private String enfermedad;
    private List<String> tratamiento;

    public EnfermedadDto() {
    }

    public String getEnfermedadId() {
        return enfermedadId;
    }

    public void setEnfermedadId(String enfermedadId) {
        this.enfermedadId = enfermedadId;
    }

    public String getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(String mascotaId) {
        this.mascotaId = mascotaId;
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

