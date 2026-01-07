package com.veterinario.dynamodb;

import com.veterinario.model.Enfermedad;
import com.veterinario.model.Mascota;
import com.veterinario.util.UtilConstants;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DynamoDbService {

    private DynamoDbClient dynamoDbClient;

    public DynamoDbService() {
        this.dynamoDbClient = DynamoDbClient.builder().build();
    }

    /**
     * Consultar mascota por su id.
     *
     * @param mascotaId
     * @return
     */
    public Mascota getMascotaById(String mascotaId) {
        // Clave primaria
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(UtilConstants.MASCOTA_ID.getValue(), AttributeValue.builder().s(mascotaId).build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(UtilConstants.MASCOTAS_TABLE_NAME.getValue())
                .key(key)
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(getItemRequest).item();

        if (item == null || item.isEmpty()) {
            return null;
        }

        Mascota mascota = new Mascota();
        mascota.setMascotaId(item.get(UtilConstants.MASCOTA_ID.getValue()).s());
        mascota.setNombre(item.get("nombre").s());
        mascota.setEdad(Integer.valueOf(item.get("edad").n()));
        mascota.setRaza(item.get("raza").s());
        mascota.setNumVacunas(Integer.valueOf(item.get("numVacunas").n()));
        mascota.setMotivoConsulta(item.get("motivoConsulta").s());

        return mascota;
    }

    /**
     * Devuelve una lista de enfermedades de una mascota
     *
     * @param mascotaId
     * @return
     */
    public List<Enfermedad> getEnfermedadesByMascotaId(String mascotaId) {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":mascotaId", AttributeValue.builder().s(mascotaId).build());

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(UtilConstants.ENFERMEDADES_TABLE_NAME.getValue())
                .keyConditionExpression("mascotaId = :mascotaId")
                .expressionAttributeValues(expressionValues)
                .build();

        QueryResponse response = dynamoDbClient.query(queryRequest);

        List<Enfermedad> enfermedades = new ArrayList<>();

        if (response.items() == null || response.items().isEmpty()) {
            return enfermedades;
        }

        for (Map<String, AttributeValue> item : response.items()) {
            enfermedades.add(mapToEnfermedad(item));
        }

        return enfermedades;
    }

    private Enfermedad mapToEnfermedad(Map<String, AttributeValue> item) {

        Enfermedad enfermedad = new Enfermedad();

        enfermedad.setEnfermedadId(item.get(UtilConstants.ENFERMEDAD_ID.getValue()).s());
        enfermedad.setMascotaId(item.get(UtilConstants.MASCOTA_ID.getValue()).s());
        enfermedad.setEnfermedad(item.get("enfermedad").s());
        List<String> tratamiento = item.get("tratamiento").l()
                .stream()
                .map(AttributeValue::s)
                .collect(Collectors.toList());
        enfermedad.setTratamiento(tratamiento);

        return enfermedad;
    }

    /**
     * Inserta una mascota en la tabla Mascotas
     *
     * @param mascota
     */
    public void createMascota(Mascota mascota) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(UtilConstants.MASCOTA_ID.getValue(), AttributeValue.builder().s(mascota.getMascotaId()).build());
        item.put("nombre", AttributeValue.builder().s(mascota.getNombre()).build());
        item.put("edad", AttributeValue.builder().n(String.valueOf(mascota.getEdad())).build());
        item.put("raza", AttributeValue.builder().s(mascota.getRaza()).build());
        item.put("numVacunas", AttributeValue.builder().n(String.valueOf(mascota.getNumVacunas())).build());
        item.put("motivoConsulta", AttributeValue.builder().s(mascota.getMotivoConsulta()).build());

        dynamoDbClient.putItem(builder -> builder.tableName(UtilConstants.MASCOTAS_TABLE_NAME.getValue()).item(item));
    }

    /**
     * Inserta una enfermedad en la tabla Enfermedades
     *
     * @param enfermedad
     */
    public void createEnfermedad(Enfermedad enfermedad) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(UtilConstants.MASCOTA_ID.getValue(), AttributeValue.builder().s(enfermedad.getMascotaId()).build());
        item.put("enfermedadId", AttributeValue.builder().s(enfermedad.getEnfermedadId()).build());
        item.put("enfermedad", AttributeValue.builder().s(enfermedad.getEnfermedad()).build());

        if (enfermedad.getTratamiento() != null && !enfermedad.getTratamiento().isEmpty()) {
            List<AttributeValue> tratamiento = enfermedad.getTratamiento().stream()
                    .map(s -> AttributeValue.builder().s(s).build())
                    .collect(Collectors.toList());
            item.put("tratamiento", AttributeValue.builder().l(tratamiento).build());
        }

        dynamoDbClient.putItem(builder -> builder.tableName(UtilConstants.ENFERMEDADES_TABLE_NAME.getValue()).item(item));
    }

    /**
     * Elimina una mascota de la tabla Mascotas y sus enfermedades asociadas. Si los datos de esa mascota estan en la
     * tabla MascotaVigilada tambien se eliminan
     *
     * @param mascotaId
     */
    public void deleteMascota(String mascotaId) {

        // Eliminar de tabla Mascotas
        dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(UtilConstants.MASCOTAS_TABLE_NAME.getValue())
                .key(Map.of(UtilConstants.MASCOTA_ID.getValue(), AttributeValue.builder().s(mascotaId).build()))
                .build());

        // Eliminar todas las enfermedades de esa mascota
        // Obtener la lista de enfermedades de la mascota
        Map<String, AttributeValue> expressionValues = Map.of(
                ":mascotaId", AttributeValue.builder().s(mascotaId).build()
        );

        List<Map<String, AttributeValue>> enfermedades = dynamoDbClient.query(
                QueryRequest.builder()
                        .tableName(UtilConstants.ENFERMEDADES_TABLE_NAME.getValue())
                        .keyConditionExpression("mascotaId = :mascotaId")
                        .expressionAttributeValues(expressionValues)
                        .build()
        ).items();

        if (!enfermedades.isEmpty()) {
            // Convertimos cada enfermedad en un objeto WriteRequest para crear una peticion de borrado en DynamoDB
            // Se usa la clave de particion mascotaId y la clave de ordenacion enfermedadId para eliminar cada elemento
            // en concreto
            List<WriteRequest> deleteRequests = enfermedades.stream()
                    .map(item -> WriteRequest.builder()
                            .deleteRequest(d -> d.key(Map.of(
                                    UtilConstants.MASCOTA_ID.getValue(), item.get(UtilConstants.MASCOTA_ID.getValue()),
                                    "enfermedadId", item.get("enfermedadId")
                            )))
                            .build())
                    .collect(Collectors.toList());

            // Construimos BatchWriteItem con cada WriteRequest
            // DynamoDB permite máximo 25 elementos por cada llamada a BatchWriteItem
            for (int i = 0; i < deleteRequests.size(); i += 25) {
                List<WriteRequest> batch = deleteRequests.subList(i, Math.min(i + 25, deleteRequests.size()));
                dynamoDbClient.batchWriteItem(BatchWriteItemRequest.builder()
                        .requestItems(Map.of(UtilConstants.ENFERMEDADES_TABLE_NAME.getValue(), batch))
                        .build());
            }
        }

        // Eliminar de MascotaVigilada si existe
        dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(UtilConstants.MASCOTA_VIGILADA_TABLE_NAME.getValue())
                .key(Map.of(UtilConstants.MASCOTA_ID.getValue(), AttributeValue.builder().s(mascotaId).build()))
                .build());
    }
}
