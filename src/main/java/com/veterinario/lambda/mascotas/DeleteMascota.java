package com.veterinario.lambda.mascotas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.veterinario.dynamodb.DynamoDbService;
import com.veterinario.util.ApiResponse;
import com.veterinario.util.UtilConstants;

import java.util.Map;
import java.util.Objects;

public class DeleteMascota implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDbService dynamoDbService = new DynamoDbService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        return bajaMascota(input);
    }

    private APIGatewayProxyResponseEvent bajaMascota(APIGatewayProxyRequestEvent request) {
        Map<String, String> pathParameters = request.getPathParameters();
        String mascotaId = "";
        if (pathParameters != null){
            mascotaId = pathParameters.get("mascotaId");
        }

        if (Objects.equals(mascotaId, "")){
            return ApiResponse.respuesta(400, UtilConstants.MASCOTA_ID_VACIO.getValue());
        }

        try {
            dynamoDbService.deleteMascota(mascotaId);
            return ApiResponse.respuesta(200, "Datos de la mascota eliminados correctamente");
        } catch (Exception e) {
            return ApiResponse.respuesta(500, "Error eliminar los datos de la mascota: " + e.getMessage());
        }
    }
}
