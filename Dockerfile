# Multi-stage build for Spring Boot application
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven wrapper và pom.xml trước để cache dependencies
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x ./mvnw

COPY pom.xml .
# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build application và đảm bảo JAR được tạo ra
RUN ./mvnw clean package -DskipTests

# Verify JAR files được tạo và tìm executable JAR
RUN echo "=== Listing all JAR files ===" && \
    ls -lh /app/target/*.jar 2>/dev/null || echo "No JAR files found!" && \
    echo "=== Finding executable JAR (excluding *-plain.jar) ===" && \
    JAR_FILE=$(find /app/target -name "*.jar" ! -name "*-plain.jar" -type f | head -1) && \
    if [ -z "$JAR_FILE" ]; then \
        echo "ERROR: No executable JAR found!" && \
        ls -la /app/target/ && \
        exit 1; \
    fi && \
    echo "Found JAR: $JAR_FILE" && \
    cp "$JAR_FILE" /app/target/app.jar && \
    ls -lh /app/target/app.jar

# Giai đoạn 2: Tạo hình ảnh Docker cuối cùng
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Tạo user non-root để chạy ứng dụng (bảo mật hơn)
RUN groupadd -r spring && useradd -r -g spring spring

# Copy JAR từ build stage (đã được đổi tên thành app.jar)
COPY --from=build /app/target/app.jar app.jar

# Verify JAR file exists và có quyền đọc
RUN if [ ! -f app.jar ]; then \
        echo "ERROR: app.jar not found!" && \
        ls -la /app/ && \
        exit 1; \
    fi && \
    echo "=== JAR file info ===" && \
    ls -lh app.jar && \
    file app.jar && \
    chown spring:spring app.jar && \
    chmod 755 app.jar

# Switch to non-root user
USER spring:spring

# Expose port (Render sẽ inject PORT environment variable)
EXPOSE 8080

# Health check (using wget which is available in JRE image)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run application
# Render sẽ inject PORT environment variable tự động
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dserver.port=${PORT:-8080}", \
  "-Dspring.profiles.active=prod", \
  "-jar", \
  "app.jar"]