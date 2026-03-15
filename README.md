# Study Session Tracker Backend

API REST para el registro y análisis de sesiones de estudio, desarrollada con Spring Boot y PostgreSQL.

## 🚀 Características

- **Autenticación JWT**: Sistema seguro de registro y login
- **Gestión de sesiones**: CRUD completo para sesiones de estudio
- **Categorización**: Carpetas y etiquetas para organizar estudios
- **Análisis**: Estadísticas semanales y mensuales
- **Seguridad**: CORS configurado, validación de inputs, encriptación BCrypt

## 🏗️ Arquitectura

- **Framework**: Spring Boot 3.3.1
- **Java**: Versión 17
- **Base de datos**: PostgreSQL (con Supabase)
- **Autenticación**: JWT tokens
- **ORM**: JPA/Hibernate
- **Build**: Maven

## Estructura del Proyecto

```
src/main/java/com/studytracker/backend/
├── controller/     # Endpoints REST
├── service/        # Lógica de negocio
├── repository/     # Acceso a datos (JPA)
├── model/          # Entidades de la base de datos
├── dto/            # Data Transfer Objects
├── mapper/         # Conversión entre entidades y DTOs
├── config/         # Configuración de Spring
└── security/       # Configuración de seguridad y JWT
```

## Configuración de la Base de Datos

### 1. Configurar Supabase

1. Crea un nuevo proyecto en [Supabase](https://supabase.com)
2. Ve a Settings > Database y copia la cadena de conexión
3. Crea las siguientes tablas (se generarán automáticamente con Hibernate DDL):

```sql
-- Tablas que se crearán automáticamente:
-- users, folders, sessions, tags, session_tags
```

### 2. Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto o configura las variables de entorno:

```bash
# Database
DB_USERNAME=tu_usuario_supabase
DB_PASSWORD=tu_contraseña_supabase

# JWT (genera una clave segura de 256 bits)
JWT_SECRET=tu_clave_secreta_muy_larga_y_segura_para_jwt_hs256
```

O actualiza directamente en `src/main/resources/application.properties`.

## Instalación y Ejecución

### Prerrequisitos

- Java 17 o superior
- Maven 3.6+
- PostgreSQL (o cuenta de Supabase)

### Pasos

1. **Clonar el repositorio**

   ```bash
   git clone <repository-url>
   cd Backend
   ```

2. **Configurar variables de entorno**

   ```bash
   export DB_USERNAME=tu_usuario
   export DB_PASSWORD=tu_contraseña
   export JWT_SECRET=tu_clave_secreta
   ```

3. **Compilar y ejecutar**

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   O usando el wrapper de Maven:

   ```bash
   ./mvnw spring-boot:run
   ```

4. **Verificar que funciona**

   El servidor se iniciará en `http://localhost:8080`

   Puedes verificar con:

   ```bash
   curl http://localhost:8080/actuator/health
   ```

## API Endpoints

### Autenticación

- `POST /auth/register` - Registrar nuevo usuario
- `POST /auth/login` - Iniciar sesión

### Carpetas

- `GET /folders` - Listar carpetas del usuario
- `POST /folders` - Crear nueva carpeta
- `GET /folders/{id}` - Obtener carpeta por ID
- `PUT /folders/{id}` - Actualizar carpeta
- `DELETE /folders/{id}` - Eliminar carpeta

### Sesiones

- `GET /sessions` - Listar sesiones del usuario
- `POST /sessions` - Crear nueva sesión
- `GET /sessions/{id}` - Obtener sesión por ID
- `PUT /sessions/{id}` - Actualizar sesión
- `DELETE /sessions/{id}` - Eliminar sesión

### Análisis

- `GET /analytics/weekly` - Análisis semanal
- `GET /analytics/monthly` - Análisis mensual
- `GET /analytics/by-folder` - Análisis por carpeta
- `GET /analytics/productivity-hours` - Horas de mayor productividad

## Desarrollo

### Compilar

```bash
mvn clean compile
```

### Ejecutar tests

```bash
mvn test
```

### Generar JAR

```bash
mvn clean package
```

### Ejecutar JAR

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## Configuración Adicional

### CORS

La configuración de CORS permite peticiones desde:

- `http://localhost:3000` (React development server)
- `http://localhost:5173` (Vite development server)

### Logs

Para ver los logs en tiempo real:

```bash
mvn spring-boot:run | grep -E "(ERROR|WARN|INFO|DEBUG)"
```

### Base de Datos H2 (para desarrollo)

Si quieres usar H2 en lugar de PostgreSQL para desarrollo, cambia el `application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
```

## Contribución

1. Fork el proyecto
2. Crear una feature branch (`git checkout -b feature/nueva-funcionalidad`)
3. Commit los cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push al branch (`git push origin feature/nueva-funcionalidad`)
5. Crear un Pull Request

## Licencia

MIT License - ver archivo LICENSE para detalles.
