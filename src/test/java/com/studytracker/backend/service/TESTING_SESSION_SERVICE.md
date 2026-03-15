# Documentación de Pruebas Unitarias: SessionServiceTest

## Contexto Técnico
- **Clase Probada:** `com.studytracker.backend.service.SessionService`
- **Tecnologías:** JUnit 5, Mockito, AssertJ.
- **Configuración Especial:** Se implementó `@MockitoSettings(strictness = Strictness.LENIENT)` para permitir flexibilidad en flujos de procesamiento de colecciones (como etiquetas), priorizando la validación de resultados finales sobre la estrictez de las invocaciones intermedias.

## Objetivos de la Suite
Validar la integridad de las sesiones de estudio, asegurando que:
1. El cálculo de tiempos y duraciones sea exacto.
2. La gestión de etiquetas (creación vs. reutilización) funcione sin errores.
3. Se respete la propiedad de los datos (aislamiento por usuario).

## Casos de Prueba Clave

### A. Gestión de Sesiones y Tiempos
- **`shouldCreateSessionSuccessfully`**: Verifica que al crear una sesión se calculen los minutos de duración y se mapeen correctamente las etiquetas existentes.
- **`shouldGetSessionByIdSuccessfully`**: Asegura que los metadatos de la sesión (como la fecha de creación) se recuperen íntegros.
- **`shouldDeleteSessionSuccessfully`**: Confirma que el borrado solo ocurra si la sesión existe y pertenece al usuario.

### B. Lógica de Etiquetas (Tags)
- **`shouldCreateSessionWithNewTagsSuccessfully`**: Valida el flujo donde el servicio encuentra una etiqueta nueva, la persiste mediante el repositorio y luego la vincula a la sesión.

### C. Seguridad y Validación de Negocio
- **`shouldThrowExceptionWhenFolderNotFound`**: Garantiza que no se puedan crear sesiones en carpetas que no pertenecen al usuario o que no existen.
- **`shouldThrowExceptionWhenGettingSessionByIdNotFound`**: Valida el manejo de errores ante intentos de acceso no autorizado o IDs inexistentes.

## Notas de Mantenimiento
- La suite utiliza `List.of()` para simular la persistencia en base de datos, reflejando el comportamiento real de las entidades JPA.
- Si se modifica la lógica de `processTags` en el servicio, revisar los stubs de `tagRepository` en esta suite.