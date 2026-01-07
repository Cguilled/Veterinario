package com.veterinario.util;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public final class ApiResponse {

    // Para serializar a JSON
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ApiResponse() {
    }

    /**
     * Construye la respuesta de las funciones Lambda a partir de un objeto DTO.
     *
     * @param statusCode
     * @param dto
     * @return
     */
    public static APIGatewayProxyResponseEvent respuesta(int statusCode, Object dto) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        try {
            String body = OBJECT_MAPPER.writeValueAsString(dto);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(statusCode)
                    .withHeaders(headers)
                    .withBody(body);
        } catch (JsonProcessingException e) {
            // Si hay un error serializando devolvemos error 500
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withHeaders(headers)
                    .withBody("{\"mensaje\": \"Error serializando la respuesta\"}");
        }
    }
}
