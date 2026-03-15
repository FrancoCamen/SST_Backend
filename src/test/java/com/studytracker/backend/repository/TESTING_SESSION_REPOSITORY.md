# Documentación de Pruebas: SessionRepositoryTest

## Descripción General
Esta suite de pruebas valida la **Capa de Persistencia** (`SessionRepository`). Se trata de **Pruebas de Integración de Datos** que utilizan una base de datos real en memoria para verificar que las consultas SQL (incluyendo agregaciones como SUM y COUNT) y las relaciones entre las entidades `Session`, `User`, `Folder` y `Tag` funcionen correctamente.

## Configuración y Entorno
* **Frameworks**: JUnit 5, Spring Boot Test (`@DataJpaTest`), AssertJ.
* **Perfil Activo**: `@ActiveProfiles("test")`, lo que garantiza el uso de una configuración aislada y segura para el entorno de pruebas.
* **Componentes Clave**:
    * **@DataJpaTest**: Configura un entorno mínimo para probar solo la capa de persistencia.
    * **TestEntityManager**: Proporciona un medio para persistir datos de prueba y sincronizar el estado de la persistencia mediante `flush()`.
    * **Base de Datos**: H2 (en memoria), asegurando rapidez y limpieza entre ejecuciones.
    * **Lógica de Entidad**: La entidad `Session` utiliza `@PrePersist` y `@PreUpdate` para calcular automáticamente `durationMinutes` basándose en la diferencia entre `startTime` y `endTime`.

## Casos de Prueba Implementados (7/7 Exitosos)

### 1. Guardar y Recuperar Sesión (`shouldSaveAndFindSession`)
* **Propósito**: Verificar que el ciclo básico de guardado (`save`) funcione, incluyendo la persistencia de relaciones `@ManyToMany` con etiquetas (`Tags`).
* **Validación**: Comprueba que la sesión recuperada mantenga la integridad de los datos, como la duración calculada y la cantidad de etiquetas asociadas.

### 2. Consultar por Usuario y Carpeta (`shouldFindByUserAndFolder`)
* **Propósito**: Asegurar que el método `findByUserAndFolder` devuelva únicamente las sesiones que pertenecen simultáneamente a un usuario y una carpeta específicos.

### 3. Consulta de Seguridad: ID y Usuario (`shouldFindByIdAndUser`)
* **Propósito**: Validar la lógica de aislamiento: un usuario solo debe poder recuperar una sesión si le pertenece, incluso si conoce el ID de una sesión ajena.
* **Validación**: Verifica que si un usuario intenta buscar una sesión de otro usuario, el resultado sea un `Optional.empty()`.

### 4. Cálculo de Minutos Totales (`shouldCalculateTotalMinutesByUserSince`)
* **Propósito**: Validar la consulta personalizada que utiliza la función `SUM` para calcular el tiempo total de estudio de un usuario desde una fecha determinada.
* **Validación**: Asegura que el cálculo ignore sesiones fuera del rango temporal establecido o pertenecientes a otros usuarios.

### 5. Conteo de Sesiones por Periodo (`shouldCountSessionsByUserSince`)
* **Propósito**: Probar la eficiencia del método de conteo (`COUNT`) para determinar cuántas sesiones ha realizado un usuario desde una fecha específica.

### 6. Búsqueda por Título Parcial (`shouldByUserAndTitleContaining`)
* **Propósito**: Validar el funcionamiento de las consultas personalizadas para búsquedas de texto parcial (`LIKE`) en los títulos de las sesiones.
* **Resultado Esperado**: Recuperar sesiones cuyo título contenga una cadena específica, filtrando estrictamente por el usuario correspondiente.

### 7. Eliminación Segura (`shouldDeleteByIdAndUser`)
* **Propósito**: Confirmar que el borrado físico de una sesión por ID y usuario funcione correctamente y que la base de datos refleje el cambio de inmediato tras un `flush()`.