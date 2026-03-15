# Documentación de Pruebas: FolderControllerTest

## Descripción General
Esta suite de pruebas valida la capa web (`FolderController`) encargada de la gestión de las carpetas de estudio. A diferencia de los endpoints públicos de autenticación, estas rutas están protegidas, por lo que las pruebas validan tanto la lógica de negocio como la correcta integración con el contexto de seguridad de Spring.

## Configuración y Entorno
* **Frameworks:** JUnit 5, Spring Boot Test, Mockito.
* **Perfil Activo:** `@ActiveProfiles("test")` - Utiliza la base de datos en memoria (H2) para garantizar el aislamiento.
* **Manejo de Seguridad:** * Los filtros de seguridad están activos.
  * Se utiliza un método `@BeforeEach` para inyectar manualmente una instancia de nuestra entidad personalizada `User` directamente en el `SecurityContextHolder`. Esto previene el error `ClassCastException` que ocurre al usar las anotaciones genéricas de Spring Security.
  * Las peticiones que modifican estado (`POST`, `DELETE`) incluyen el token `.with(csrf())` para evitar bloqueos de tipo `403 Forbidden`.
  * `JwtService` y `UserDetailsService` están aislados mediante `@MockBean`.

## Casos de Prueba Implementados (7/7 Exitosos)

### 1. Creación de Carpeta Exitosa (`shouldCreateFolderSuccessfully`)
* **Propósito:** Validar que un usuario autenticado puede crear una nueva carpeta de estudio.
* **Condición:** Se envía un JSON válido con nombre y descripción, acompañado de un token CSRF válido.
* **Resultado Esperado:** Código HTTP `201 Created` y un JSON de respuesta con los datos de la carpeta generada (incluyendo contadores de horas inicializados en 0).

### 2. Obtener Carpetas del Usuario (`shouldGetUserFoldersSuccessfully`)
* **Propósito:** Verificar que un usuario puede recuperar su lista de carpetas.
* **Condición:** Petición GET autenticada.
* **Resultado Esperado:** Código HTTP `200 OK` y un array JSON que contiene la lista de carpetas pertenecientes al usuario.

### 3. Obtener Carpeta por ID (`shouldGetFolderByIdSuccessfully`)
* **Propósito:** Validar la consulta individual de una carpeta específica.
* **Condición:** Petición GET autenticada hacia `/api/folders/{id}`.
* **Resultado Esperado:** Código HTTP `200 OK` y un JSON con los detalles completos de la carpeta solicitada.

### 4. Eliminar Carpeta (`shouldDeleteFolderSuccessfully`)
* **Propósito:** Comprobar que el endpoint de borrado funciona correctamente.
* **Condición:** Petición DELETE autenticada hacia `/api/folders/{id}`, incluyendo token CSRF.
* **Resultado Esperado:** Código HTTP `204 No Content`, confirmando que el recurso fue eliminado sin devolver cuerpo en la respuesta.

### 5. Validación: Nombre Vacío (`shouldReturnValidationErrorWhenCreatingFolderWithEmptyName`)
* **Propósito:** Asegurar que no se puedan crear carpetas sin nombre.
* **Condición:** Se envía un JSON con el campo `name` vacío (`""`).
* **Resultado Esperado:** Código HTTP `400 Bad Request` interceptado por la validación `@NotBlank`.

### 6. Validación: Nombre Excesivamente Largo (`shouldReturnValidationErrorWhenCreatingFolderWithTooLongName`)
* **Propósito:** Prevenir errores de base de datos por desbordamiento en la columna del nombre.
* **Condición:** Se envía un JSON con un `name` de más de 100 caracteres.
* **Resultado Esperado:** Código HTTP `400 Bad Request` interceptado por la validación `@Size`.

### 7. Validación: Descripción Excesivamente Larga (`shouldReturnValidationErrorWhenCreatingFolderWithTooLongDescription`)
* **Propósito:** Prevenir desbordamientos en la columna de descripción.
* **Condición:** Se envía un JSON con un campo `description` de más de 500 caracteres.
* **Resultado Esperado:** Código HTTP `400 Bad Request` interceptado por la validación `@Size`.