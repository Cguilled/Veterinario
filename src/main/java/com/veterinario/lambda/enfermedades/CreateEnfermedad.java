package com.veterinario.lambda.enfermedades;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinario.dynamodb.DynamoDbService;
import com.veterinario.model.Enfermedad;
import com.veterinario.util.ApiResponse;
import com.veterinario.util.UtilConstants;

public class CreateEnfermedad implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDbService dynamoDbService = new DynamoDbService();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        return registrarEnfermedad(input);
    }

    private APIGatewayProxyResponseEvent registrarEnfermedad(APIGatewayProxyRequestEvent request) {
        try {
            // Convertir JSON recibido en la request a entidad Enfermedad
            Enfermedad enfermedad = objectMapper.readValue(request.getBody(), Enfermedad.class);

            dynamoDbService.createEnfermedad(enfermedad);

            return ApiResponse.respuesta(201, UtilConstants.ENFERMEDAD_REGISTRADA_OK.getValue());
        } catch (Exception e) {
            return ApiResponse.respuesta(500, "Error al registrar enfermedad: " + e.getMessage());
        }
    }
}
