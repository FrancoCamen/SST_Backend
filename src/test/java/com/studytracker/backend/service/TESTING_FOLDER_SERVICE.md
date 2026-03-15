# Documentación de Pruebas: FolderServiceTest

## Descripción General
Esta suite de pruebas valida la **Capa de Servicios** (`FolderService`), que es donde reside la lógica de negocio real de la aplicación. A diferencia de las pruebas de los controladores, estas son **Pruebas Unitarias Puras**. No se levanta el contexto de Spring Boot, no hay base de datos H2 ni intervienen los filtros de Spring Security. Esto garantiza una ejecución extremadamente rápida (en milisegundos) y un aislamiento total del código.

## Configuración y Entorno
* **Frameworks:** JUnit 5, Mockito.
* **Anotaciones Principales:** * `@ExtendWith(MockitoExtension.class)`: Habilita el uso de Mockito sin necesidad de Spring.
  * `@InjectMocks`: Crea una instancia real de `FolderService` inyectando automáticamente los mocks que necesita.
  * `@Mock`: Crea simulaciones vacías de los repositorios (`FolderRepository`, `SessionRepository`).
* **Manejo de Verificaciones (Verify):** Se utilizó `verify(..., atLeastOnce())` al comprobar interacciones con métodos auxiliares del repositorio (como los cálculos de totales de horas). Esto evita que los tests sean excesivamente frágiles ("TooManyActualInvocations") ante cambios internos en la optimización del servicio, enfocándose en que la comunicación con la base de datos ocurra, independientemente de si el servicio necesita consultarla una o varias veces durante su mapeo.

## Casos de Prueba Implementados (8/8 Exitosos)

### 1. Creación Exitosa (`shouldCreateFolderSuccessfully`)
* **Propósito:** Validar que el servicio guarde correctamente una entidad nueva y la mapee a un DTO de respuesta.
* **Comportamiento:** Simula el guardado en `FolderRepository` e inicializa los cálculos de horas/sesiones en 0.

### 2. Obtener Lista de Carpetas (`shouldGetUserFoldersSuccessfully`)
* **Propósito:** Comprobar que el servicio puede recuperar y transformar múltiples carpetas del usuario.
* **Comportamiento:** Simula una lista devuelta por la base de datos y verifica que la transformación a DTO calcule correctamente las horas totales (ej. convirtiendo 120 minutos en 2 horas).

### 3. Obtener Carpeta por ID - Éxito (`shouldGetFolderByIdSuccessfully`)
* **Propósito:** Validar la consulta de una carpeta individual.
* **Comportamiento:** Retorna un `Optional` presente con la carpeta y verifica el correcto mapeo al `FolderResponse`.

### 4. Obtener Carpeta por ID - Error (`shouldThrowExceptionWhenFolderNotFoundById`)
* **Propósito:** Garantizar que el servicio no falle silenciosamente ni retorne nulos si el recurso no existe o no pertenece al usuario.
* **Comportamiento:** Simula un `Optional.empty()` en el repositorio y verifica que se lance una `RuntimeException` con el mensaje adecuado.

### 5. Actualización Exitosa (`shouldUpdateFolderSuccessfully`)
* **Propósito:** Verificar la modificación de propiedades (nombre, descripción) de una carpeta existente.
* **Comportamiento:** Busca la entidad previa, aplica los cambios del DTO de entrada, simula el guardado y comprueba la integridad de la respuesta.

### 6. Actualización - Error (`shouldThrowExceptionWhenUpdatingFolderNotFound`)
* **Propósito:** Prevenir modificaciones en registros inexistentes.
* **Comportamiento:** Al no encontrar la carpeta previa, verifica que se lance la excepción correcta y que **nunca** se llame al método `save()` del repositorio.

### 7. Eliminación Exitosa (`shouldDeleteFolderSuccessfully`)
* **Propósito:** Validar el borrado de una carpeta.
* **Comportamiento:** Simula que la carpeta existe y verifica que el servicio invoque exactamente el método `deleteByIdAndUser` del repositorio.

### 8. Eliminación - Error (`shouldThrowExceptionWhenDeletingFolderNotFound`)
* **Propósito:** Evitar intentos de borrado sobre IDs inválidos.
* **Comportamiento:** Simula que la carpeta no existe, lanza la excepción y verifica que **nunca** se ejecute la instrucción de borrado en el repositorio.