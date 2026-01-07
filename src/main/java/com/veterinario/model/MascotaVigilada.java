package com.veterinario.model;

public class MascotaVigilada {
    private String mascotaId;         // PK
    private String nombre;
    private Integer edad;
    private String raza;
    private String motivo;
    private String fechaMarcado;

    public MascotaVigilada() {
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getFechaMarcado() {
        return fechaMarcado;
    }

    public void setFechaMarcado(String fechaMarcado) {
        this.fechaMarcado = fechaMarcado;
    }
}
