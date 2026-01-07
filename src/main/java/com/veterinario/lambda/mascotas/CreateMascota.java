package com.veterinario.lambda.mascotas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinario.dynamodb.DynamoDbService;
import com.veterinario.model.Mascota;
import com.veterinario.util.ApiResponse;
import com.veterinario.util.UtilConstants;

public class CreateMascota implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDbService dynamoDbService = new DynamoDbService();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        return insertarMascota(input);
    }

    private APIGatewayProxyResponseEvent insertarMascota(APIGatewayProxyRequestEvent request) {
        try {
            // Convertir JSON recibido en la request a Mascota
            Mascota mascota = objectMapper.readValue(request.getBody(), Mascota.class);

            dynamoDbService.createMascota(mascota);

            return ApiResponse.respuesta(201, UtilConstants.MASCOTA_REGISTRADA_OK.getValue());
        } catch (Exception e) {
            return ApiResponse.respuesta(500, "Error al registrar mascota: " + e.getMessage());
        }
    }
}
