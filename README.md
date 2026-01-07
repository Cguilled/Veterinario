# Clínica Veterinaria – Backend Serverless en AWS

Backend serverless para una aplicación de gestión de una clínica veterinaria, desarrollado en **Java 11** sobre **AWS**, utilizando una arquitectura desacoplada, escalable y orientada a eventos.

El sistema permite gestionar **mascotas**, sus **enfermedades** y un mecanismo automático de **vigilancia de mascotas** mediante **DynamoDB Streams**.

---

## Arquitectura

La aplicación está construida íntegramente con servicios serverless de AWS:

- AWS Lambda
- Amazon API Gateway
- Amazon DynamoDB
- DynamoDB Streams
- AWS IAM
- AWS Lambda Layers

### Flujo general

API Gateway  
→ AWS Lambda (lógica de negocio)  
→ DynamoDB  
→ DynamoDB Streams  
→ Lambda de procesamiento  
→ Tabla MascotaVigilada

---

## Modelo de datos

### Tabla `Mascota`

| Campo          | Tipo   | Descripción                 |
|---------------|--------|-----------------------------|
| mascotaId     | String | Partition Key               |
| nombre        | String | Nombre de la mascota        |
| edad          | Number | Edad en años                |
| raza          | String | Raza                        |
| numVacunas    | Number | Número de vacunas           |
| motivoConsulta| String | Motivo de la consulta       |

---

### Tabla `Enfermedad`

| Campo         | Tipo   | Descripción                 |
|--------------|--------|-----------------------------|
| mascotaId    | String | Partition Key               |
| enfermedadId | String | Sort Key                    |
| enfermedad   | String | Nombre de la enfermedad     |
| tratamiento  | List   | Lista de tratamientos       |

---

### Tabla `MascotaVigilada`

Tabla rellenada automáticamente mediante DynamoDB Streams.

| Campo         | Tipo   | Descripción                 |
|--------------|--------|-----------------------------|
| mascotaId    | String | Partition Key               |
| edad         | Number | Edad detectada              |
| motivo       | String | Motivo de vigilancia        |
| fechaRegistro| String | Fecha de registro           |

---

## Endpoints REST

| Método | Endpoint                                      | Descripción                         |
|------|-----------------------------------------------|-------------------------------------|
| POST | /mascotas                                     | Crear una mascota                   |
| GET  | /mascotas/{mascotaId}                         | Obtener una mascota                 |
| DELETE | /mascotas/{mascotaId}                       | Eliminar una mascota                |
| POST | /mascotas/{mascotaId}/enfermedades            | Crear una enfermedad                |
| GET  | /mascotas/{mascotaId}/enfermedades            | Obtener enfermedades de una mascota |

---

## Lambdas implementadas

- CreateMascota
- GetMascota
- DeleteMascota
- CreateEnfermedad
- GetEnfermedad
- MascotasProcessor (trigger de DynamoDB Streams)

---

## DynamoDB Streams

Cuando se inserta o modifica una mascota:

- Si la edad es mayor o igual a 10
- Se registra automáticamente en la tabla `MascotaVigilada`
- Se evita duplicidad mediante `ConditionExpression`

El proceso es asíncrono y desacoplado del flujo principal.

---

## Seguridad

Cada Lambda utiliza un rol IAM específico con permisos mínimos:

- Acceso limitado a tablas DynamoDB concretas
- Permisos `BatchWriteItem` para eliminaciones en cascada

---

## Tecnologías

- Java 11
