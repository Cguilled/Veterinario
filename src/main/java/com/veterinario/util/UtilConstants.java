package com.veterinario.util;

public enum UtilConstants {

    MASCOTA_REGISTRADA_OK("Mascota registrada correctamente"),
    MASCOTA_ID_VACIO("Id de mascota vacío o nulo"),
    ENFERMEDAD_REGISTRADA_OK("Enfermedad registrada correctamente"),
    EDAD("edad"),
    MASCOTA_ID("mascotaId"),
    ENFERMEDAD_ID("enfermedadId"),
    MASCOTAS_TABLE_NAME("Mascota"),
    MASCOTA_VIGILADA_TABLE_NAME("MascotaVigilada"),
    ENFERMEDADES_TABLE_NAME("Enfermedad");

    private final String value;

    UtilConstants(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
