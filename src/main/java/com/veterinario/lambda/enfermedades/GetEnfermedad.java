package com.veterinario.lambda.enfermedades;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.veterinario.dto.EnfermedadDto;
import com.veterinario.dynamodb.DynamoDbService;
import com.veterinario.model.Enfermedad;
import com.veterinario.util.ApiResponse;
import com.veterinario.util.UtilConstants;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GetEnfermedad implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDbService dynamoDbService = new DynamoDbService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        return consultarEnfermedades(input);
    }

    private APIGatewayProxyResponseEvent consultarEnfermedades(APIGatewayProxyRequestEvent request) {
        Map<String, String> pathParameters = request.getPathParameters();
        String mascotaId = "";
        if (pathParameters != null){
            mascotaId = pathParameters.get(UtilConstants.MASCOTA_ID.getValue());
        }

        if (Objects.equals(mascotaId, "")){
            return ApiResponse.respuesta(400, UtilConstants.MASCOTA_ID_VACIO.getValue());
        }

        List<Enfermedad> enfermedades = dynamoDbService.getEnfermedadesByMascotaId(mascotaId);

        if (enfermedades == null || enfermedades.isEmpty()) {
            return ApiResponse.respuesta(404, "La mascota no tiene enfermedades");
        }

        List<EnfermedadDto> enfermedadesDto = new ArrayList<>();
        for (Enfermedad enfermedad : enfermedades){
            EnfermedadDto enfermedadDto = new EnfermedadDto();
            enfermedadDto.setMascotaId(enfermedad.getMascotaId());
            enfermedadDto.setEnfermedadId(enfermedad.getEnfermedadId());
            enfermedadDto.setEnfermedad(enfermedad.getEnfermedad());
            enfermedadDto.setTratamiento(enfermedad.getTratamiento());
            enfermedadesDto.add(enfermedadDto);
        }

        return ApiResponse.respuesta(200, enfermedadesDto);
    }
}
