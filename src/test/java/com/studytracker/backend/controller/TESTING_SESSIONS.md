# Documentación de Pruebas: SessionControllerTest

## Descripción General
Esta suite de pruebas valida la capa web (`SessionController`), responsable de gestionar el núcleo de la aplicación: los registros de tiempo de estudio (sesiones). Al tratar con información vinculada a los usuarios y sus carpetas, estos endpoints están estrictamente protegidos. Las pruebas aseguran que las rutas respondan correctamente, validen la integridad de los datos de entrada y respeten las políticas de seguridad.

## Configuración y Entorno
* **Frameworks:** JUnit 5, Spring Boot Test (`@WebMvcTest`), Mockito.
* **Perfil Activo:** `@ActiveProfiles("test")` - Las pruebas se ejecutan de manera aislada usando la base de datos en memoria.
* **Manejo de Seguridad:** * Los filtros nativos de Spring Security se mantienen encendidos.
  * Se utiliza un método `@BeforeEach` para fabricar e inyectar un objeto `User` (entidad propia de la aplicación) en el `SecurityContextHolder`, simulando un usuario logueado sin causar excepciones de "ClassCast".
  * Las peticiones que alteran el estado (`POST`, `DELETE`) incorporan el token `.with(csrf())` para sortear la protección contra falsificación de solicitudes.
  * La capa de servicios (`SessionService`) y la de seguridad (`JwtService`, `UserDetailsService`) están virtualizadas mediante `@MockBean`.

## Casos de Prueba Implementados (7/7 Exitosos)

### 1. Creación de Sesión Exitosa (`shouldCreateSessionSuccessfully`)
* **Propósito:** Validar que un usuario pueda registrar una nueva sesión de estudio válida.
* **Condición:** Petición POST autenticada con un JSON completo (título, descripción, hora de inicio, hora de fin, ID de carpeta y etiquetas).
* **Resultado Esperado:** Código HTTP `201 Created` y un JSON reflejando los datos guardados, incluyendo la duración en minutos calculada y el registro de tiempo de creación.

### 2. Obtener Sesiones por Carpeta (`shouldGetSessionsByFolderIdSuccessfully`)
* **Propósito:** Comprobar la recuperación del historial de estudio de una materia específica.
* **Condición:** Petición GET autenticada hacia `/api/sessions/folder/{folderId}`.
* **Resultado Esperado:** Código HTTP `200 OK` y un array JSON que lista todas las sesiones asociadas a dicha carpeta.

### 3. Eliminar Sesión (`shouldDeleteSessionSuccessfully`)
* **Propósito:** Validar la capacidad de borrar un registro de estudio erróneo o no deseado.
* **Condición:** Petición DELETE autenticada hacia `/api/sessions/{id}`, adjuntando el token CSRF.
* **Resultado Esperado:** Código HTTP `204 No Content`, confirmando el borrado exitoso.

### 4. Validación: Título Vacío (`shouldReturnValidationErrorWhenCreatingSessionWithoutTitle`)
* **Propósito:** Evitar que se creen sesiones anónimas o sin nombre descriptivo.
* **Condición:** Se intenta crear una sesión enviando un JSON donde el campo `title` está en blanco.
* **Resultado Esperado:** Código HTTP `400 Bad Request`.

### 5. Validación: Sin Hora de Inicio (`shouldReturnValidationErrorWhenCreatingSessionWithoutStartTime`)
* **Propósito:** Garantizar la integridad de los datos cronológicos de la sesión.
* **Condición:** Se omite o se envía un valor nulo en el campo `startTime`.
* **Resultado Esperado:** Código HTTP `400 Bad Request`.

### 6. Validación: Sin Hora de Fin (`shouldReturnValidationErrorWhenCreatingSessionWithoutEndTime`)
* **Propósito:** Prevenir la creación de sesiones "abiertas" infinitas (a menos que el modelo de negocio lo permita, en este caso está restringido por `@NotNull`).
* **Condición:** Se omite o se envía un valor nulo en el campo `endTime`.
* **Resultado Esperado:** Código HTTP `400 Bad Request`.

### 7. Obtener Sesión Individual por ID (`shouldGetSessionByIdSuccessfully`)
* **Propósito:** Verificar la consulta detallada de un solo registro de estudio.
* **Condición:** Petición GET autenticada hacia `/api/sessions/{id}`.
* **Resultado Esperado:** Código HTTP `200 OK` y el JSON correspondiente a la sesión exacta solicitada, con sus etiquetas y cálculos de duración.