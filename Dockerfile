# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy pom.xml first for better Docker layer caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage (Usamos JRE para que sea más liviana)
FROM eclipse-temurin:17-jre-alpine

# Install curl for health checks (comando apk para Alpine)
RUN apk add --no-cache curl

# Create app directory
WORKDIR /app

# Copy the built JAR from the build stage
# Se usa el comodín * para capturar la versión (ej: backend-0.0.1-SNAPSHOT.jar)
COPY --from=build /app/target/backend-*.jar app.jar

# Create non-root user for security (sintaxis para Alpine)
RUN addgroup -S appuser && adduser -S appuser -G appuser
RUN chown -R appuser:appuser /app
USER appuser

# Expose port
EXPOSE 8080

# Add JVM options for production
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
