# Configuración de CORS y Despliegue - Study Session Tracker Backend

## Configuración de CORS Implementada

Se ha configurado CORS completamente para soportar tanto desarrollo como producción:

### 1. Configuración Java (`CorsConfig.java`)
- **Orígenes permitidos**: Patrones flexibles para localhost y 127.0.0.1 en cualquier puerto
- **Métodos HTTP**: GET, POST, PUT, DELETE, OPTIONS, PATCH
- **Headers**: Todos los headers incluyendo autorización
- **Credentials**: Habilitado para cookies y headers de autorización
- **Cache**: 3600 segundos para pre-flight requests

### 2. Configuración Spring Security (`SecurityConfig.java`)
- Integración de CORS con la cadena de seguridad
- Configuración aplicada antes de los filtros de seguridad

### 3. Configuración Properties
- **Desarrollo**: `application.properties` con orígenes locales
- **Producción**: `application-prod.properties` con variables de entorno

## Despliegue en Producción

### Opción 1: Usar perfil de producción
```bash
java -jar backend.jar --spring.profiles.active=prod
```

### Opción 2: Variables de entorno (Recomendado)
```bash
export DATABASE_URL="jdbc:postgresql://tu-host:5432/tu-db"
export DATABASE_USERNAME="tu-usuario"
export DATABASE_PASSWORD="tu-password"
export JWT_SECRET="tu-secreto-jwt"
export CORS_ALLOWED_ORIGINS="https://tu-dominio.com,https://www.tu-dominio.com"
export PORT=8080

java -jar backend.jar
```

### Opción 3: Docker
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/backend.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xmx512m"
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Configuración según Plataforma

### Vercel
```env
CORS_ALLOWED_ORIGINS=https://tu-app.vercel.app
DATABASE_URL=tu-database-url
JWT_SECRET=tu-jwt-secret
```

### Railway
```env
CORS_ALLOWED_ORIGINS=https://tu-app.railway.app
DATABASE_URL=tu-database-url
JWT_SECRET=tu-jwt-secret
```

### Heroku
```env
CORS_ALLOWED_ORIGINS=https://tu-app.herokuapp.com
DATABASE_URL=tu-database-url
JWT_SECRET=tu-jwt-secret
```

## Verificación

Para verificar que CORS está funcionando correctamente:

1. **Headers CORS**: Revisa que las respuestas incluyan headers como:
   - `Access-Control-Allow-Origin`
   - `Access-Control-Allow-Methods`
   - `Access-Control-Allow-Headers`

2. **Preflight requests**: Las solicitudes OPTIONS deben devolver 200 OK

3. **Errores comunes**:
   - Si ves "CORS policy error", verifica los orígenes permitidos
   - Si las credenciales no funcionan, asegúrate que `allow-credentials` esté en true

## Notas Importantes

- **Seguridad**: En producción, especifica exactamente los dominios permitidos
- **HTTPS**: Usa siempre HTTPS en producción
- **JWT Secret**: Genera un secret único para producción
- **Database**: Usa credenciales de base de datos específicas para producción

## Comandos Útiles

```bash
# Compilar
mvn clean package

# Ejecutar en desarrollo
mvn spring-boot:run

# Ejecutar con perfil de producción
java -jar target/backend.jar --spring.profiles.active=prod

# Verificar configuración
curl -I -X OPTIONS http://localhost:8080/api/auth/login
```
