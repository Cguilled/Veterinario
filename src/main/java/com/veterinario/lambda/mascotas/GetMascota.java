package com.veterinario.lambda.mascotas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.veterinario.dto.MascotaDto;
import com.veterinario.dynamodb.DynamoDbService;
import com.veterinario.model.Mascota;
import com.veterinario.util.ApiResponse;
import com.veterinario.util.UtilConstants;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Objects;

public class GetMascota implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDbService dynamoDbService = new DynamoDbService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        return consultarMascota(input);
    }

    private APIGatewayProxyResponseEvent consultarMascota(APIGatewayProxyRequestEvent request) {
        Map<String, String> pathParameters = request.getPathParameters();
        String mascotaId = "";
        if (pathParameters != null){
            mascotaId = pathParameters.get(UtilConstants.MASCOTA_ID.getValue());
        }

        // Si no hay ningun valor en la ruta /pets/{mascotaId}
        if (Objects.equals(mascotaId, "")){
            return ApiResponse.respuesta(400, UtilConstants.MASCOTA_ID_VACIO.getValue());
        }

        Mascota mascota = dynamoDbService.getMascotaById(mascotaId);

        if (mascota == null) {
            return ApiResponse.respuesta(404, "Mascota no encontrada");
        }

        // Transformar respuesta de DynamoDB a DTO
        MascotaDto mascotaDto = new MascotaDto();
        mascotaDto.setMascotaId(mascotaId);
        mascotaDto.setNombre(mascota.getNombre());
        mascotaDto.setEdad(mascota.getEdad());
        mascotaDto.setRaza(mascota.getRaza());
        mascotaDto.setNumVacunas(mascota.getNumVacunas());
        mascotaDto.setMotivoConsulta(mascota.getMotivoConsulta());

        return ApiResponse.respuesta(200, mascotaDto);
    }
}