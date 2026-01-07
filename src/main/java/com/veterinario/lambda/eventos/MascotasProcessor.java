package com.veterinario.lambda.eventos;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.StreamsEventResponse;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamRecord;
import com.veterinario.util.UtilConstants;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MascotasProcessor implements RequestHandler<DynamodbEvent, StreamsEventResponse> {

    private static final int EDAD_AVANZADA = 10;
    // Cliente DynamoDB (SDK v2)
    private DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();

    @Override
    public StreamsEventResponse handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        // Para el control de errores por si un elemento falla al insertarse en la tabla MascotaVigilada
        List<StreamsEventResponse.BatchItemFailure> batchItemFailures = new ArrayList<>();
        String curentRecordSequenceNumber = "";

        // Objeto que captura cualquier cambio de un elemento en una tabla de DynamoDB
        List<DynamodbEvent.DynamodbStreamRecord> dynamodbStream = dynamodbEvent.getRecords();

        try {
            for (DynamodbEvent.DynamodbStreamRecord record : dynamodbStream) {
                // Elemento del evento actual
                StreamRecord dynamodbRecord = record.getDynamodb();
                curentRecordSequenceNumber = dynamodbRecord.getSequenceNumber();

                String eventName = record.getEventName();
                if (eventName.equals("INSERT") || eventName.equals("MODIFY")) {
                    // devuelve el elemento de la tabla de DynamoDB despues de que haya cambiado
                    Map<String, com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue> modifiedItem = dynamodbRecord.getNewImage();

                    // si el elemento existe y tiene el atributo "edad"
                    if (modifiedItem != null && modifiedItem.containsKey(UtilConstants.EDAD.getValue())) {
                        int age = Integer.parseInt(modifiedItem.get(UtilConstants.EDAD.getValue()).getN());

                        if (age >= EDAD_AVANZADA) {
                            String mascotaId = modifiedItem.get(UtilConstants.MASCOTA_ID.getValue()).getS();
                            context.getLogger().log("Mascota con edad avanzada detectada: " + mascotaId);

                            insertarMascotaVigilada(mascotaId, age, context);
                        }
                    }
                }

            }
        } catch (Exception e) {
            // Al usar streams el elemento que ha fallado inmediatamente
            // Lambda volvera a intentar procesar desde este elemento fallido
            batchItemFailures.add(new StreamsEventResponse.BatchItemFailure(curentRecordSequenceNumber));
            return new StreamsEventResponse(batchItemFailures);
        }
        return new StreamsEventResponse();
    }

    /**
     * Inserta una mascota en la tabla MascotaVigilada.
     *
     * @param mascotaId
     * @param edad
     * @param context
     */
    private void insertarMascotaVigilada(String mascotaId, int edad, Context context) {

        Map<String, AttributeValue> item = new HashMap<>();
        item.put(UtilConstants.MASCOTA_ID.getValue(), AttributeValue.builder().s(mascotaId).build());
        item.put(UtilConstants.EDAD.getValue(), AttributeValue.builder().n(String.valueOf(edad)).build());
        item.put("motivo", AttributeValue.builder().s("Edad avanzada").build());
        item.put("fechaRegistro", AttributeValue.builder().s(LocalDate.now().toString()).build());

        // conditionExpresion garantiza que el elemento que se esta insertando en la tabla no existe
        PutItemRequest request = PutItemRequest.builder()
                .tableName(UtilConstants.MASCOTA_VIGILADA_TABLE_NAME.getValue())
                .item(item)
                .conditionExpression(
                        "attribute_not_exists(" + UtilConstants.MASCOTA_ID.getValue() + ")"
                )
                .build();

        try {
            dynamoDbClient.putItem(request);
            context.getLogger().log(
                    "Mascota insertada en MascotaVigilada: " + mascotaId
            );
        } catch (ConditionalCheckFailedException e) {
            context.getLogger().log(
                    "Mascota ya registrada como vigilada. Evento ignorado: " + mascotaId
            );
        }
    }
}
