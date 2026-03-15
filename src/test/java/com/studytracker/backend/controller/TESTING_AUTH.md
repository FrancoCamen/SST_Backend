# Documentación de Pruebas: AuthControllerTest

## Descripción General
Esta suite de pruebas unitarias y de integración parcial valida el comportamiento de la capa web (Controlador) para los endpoints de autenticación y registro de usuarios (`/api/auth`). 

Se utiliza **MockMvc** para simular las peticiones HTTP y **Mockito** para aislar la lógica de negocio, garantizando que los tests sean rápidos, deterministas y no afecten la base de datos real.

## Configuración y Entorno
* **Frameworks:** JUnit 5 (Jupiter), Spring Boot Test, Mockito.
* **Perfil Activo:** `@ActiveProfiles("test")` - Utiliza la configuración de base de datos en memoria (H2) definida en `application-test.properties`.
* **Seguridad:** Los filtros de Spring Security (JWT) están desactivados intencionalmente (`addFilters = false`) porque estos endpoints son de acceso público. Las dependencias de seguridad como `JwtService` han sido "mockeadas" para permitir que el contexto de Spring levante correctamente.
* **Capa de Servicio:** `AuthService` está simulado (`@MockBean`) para controlar exactamente qué datos se devuelven o qué excepciones se lanzan sin ejecutar lógica real.

## Casos de Prueba Implementados (7/7 Exitosos)

### 1. Registro Exitoso (`shouldRegisterNewUserSuccessfully`)
* **Propósito:** Validar que el endpoint `/api/auth/register` procesa correctamente un nuevo usuario.
* **Condición:** Se envía un JSON válido con nombre, email y contraseña.
* **Resultado Esperado:** Código HTTP `201 Created` y un JSON de respuesta que incluye el token JWT y el `userId`.

### 2. Registro con Email Duplicado (`shouldReturnBadRequestWhenRegisteringWithExistingEmail`)
* **Propósito:** Verificar el manejo de errores cuando un usuario intenta registrarse con un correo ya existente.
* **Condición:** El servicio lanza una `IllegalArgumentException`.
* **Resultado Esperado:** Código HTTP `400 Bad Request`.

### 3. Login Exitoso (`shouldLoginUserSuccessfully`)
* **Propósito:** Validar el inicio de sesión con credenciales correctas.
* **Condición:** Se envía un JSON válido con email y contraseña.
* **Resultado Esperado:** Código HTTP `200 OK` y un JSON de respuesta que incluye el token JWT y los datos básicos del usuario.

### 4. Login con Credenciales Inválidas (`shouldReturnUnauthorizedWhenLoginWithInvalidCredentials`)
* **Propósito:** Validar la protección del endpoint contra contraseñas incorrectas o usuarios inexistentes.
* **Condición:** El servicio lanza una `RuntimeException` por credenciales inválidas.
* **Resultado Esperado:** Código HTTP `401 Unauthorized`.

### 5. Validación de Datos en Registro (`shouldReturnValidationErrorForInvalidRegisterRequest`)
* **Propósito:** Asegurar que las validaciones `@Valid` del DTO (Data Transfer Object) funcionen antes de llegar al servicio.
* **Condición:** Se envían campos vacíos, un formato de email inválido y una contraseña demasiado corta (menos de 6 caracteres).
* **Resultado Esperado:** Código HTTP `400 Bad Request`.

### 6. Validación de Datos en Login (`shouldReturnValidationErrorForInvalidLoginRequest`)
* **Propósito:** Evitar procesar peticiones de inicio de sesión incompletas.
* **Condición:** Se envían email y contraseña vacíos.
* **Resultado Esperado:** Código HTTP `400 Bad Request`.

### 7. Endpoint de Prueba (`shouldReturnTestEndpointResponse`)
* **Propósito:** Verificar la disponibilidad de un endpoint básico de testeo (`/api/auth/test`).
* **Condición:** Petición GET simple.
* **Resultado Esperado:** Código HTTP `200 OK` y un mensaje de texto plano confirmando el estado.