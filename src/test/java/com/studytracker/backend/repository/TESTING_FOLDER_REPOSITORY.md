# Documentación de Pruebas: FolderRepositoryTest

## Descripción General
Esta suite de pruebas valida la **Capa de Persistencia** (`FolderRepository`). A diferencia de los tests unitarios previos, se trata de **Pruebas de Integración de Datos** que utilizan una base de datos real en memoria para verificar que las consultas SQL generadas por Spring Data JPA y las relaciones entre entidades funcionen correctamente.

## Configuración y Entorno
* **Frameworks**: JUnit 5, Spring Boot Test (`@DataJpaTest`), AssertJ.
* **Perfil Activo**: `@ActiveProfiles("test")`, lo que garantiza el uso de una configuración aislada y segura para el entorno de pruebas.
* **Componentes Clave**:
    * **@DataJpaTest**: Configura un entorno mínimo para probar solo la capa de persistencia.
    * **TestEntityManager**: Proporciona un medio para persistir y limpiar datos de prueba de forma manual antes de ejecutar los métodos del repositorio.
    * **Base de Datos**: H2 (en memoria), lo que asegura que las pruebas sean rápidas y no dejen residuos en bases de datos de desarrollo o producción.

## Casos de Prueba Implementados (7/7 Exitosos)

### 1. Guardar y Recuperar Carpeta (`shouldSaveAndFindFolder`)
* **Propósito**: Verificar que el ciclo básico de guardado (`save`) funcione y que los campos automáticos (como `id` y `createdAt`) se generen correctamente.
* **Validación**: Comprueba que la carpeta recuperada mantenga la integridad de los datos originales y la relación con el usuario.

### 2. Consultar por Usuario (`shouldFindFoldersByUser`)
* **Propósito**: Asegurar que el método `findByUser` devuelva únicamente las carpetas pertenecientes a un usuario específico, filtrando correctamente las de otros usuarios.

### 3. Consulta de Seguridad: ID y Usuario (`shouldFindByIdAndUser`)
* **Propósito**: Validar la lógica de aislamiento: un usuario solo debe poder recuperar una carpeta si le pertenece.
* **Validación**: Verifica que si un usuario intenta buscar el ID de una carpeta de otro usuario, el resultado sea un `Optional.empty()`.

### 4. Búsqueda por Nombre Parcial (`shouldByUserAndNameContaining`)
* **Propósito**: Validar el funcionamiento de las consultas derivadas para búsquedas parciales (tipo `LIKE`).
* **Resultado Esperado**: Recuperar carpetas cuyo nombre contenga una cadena específica ignorando el resto.

### 5. Verificación de Existencia (`shouldExistsByIdAndUser`)
* **Propósito**: Probar la eficiencia del método `exists` para comprobaciones rápidas de propiedad antes de realizar operaciones críticas como borrado o edición.

### 6. Eliminación Segura (`shouldDeleteByIdAndUser`)
* **Propósito**: Confirmar que el borrado físico de una carpeta funcione correctamente y que la base de datos refleje el cambio de inmediato mediante el uso de `entityManager.flush()`.

### 7. Consulta por ID Numérico (`shouldFindByUserId`)
* **Propósito**: Verificar el filtrado utilizando directamente el ID numérico del usuario en lugar de la entidad completa.