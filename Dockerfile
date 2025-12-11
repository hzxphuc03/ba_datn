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
RUN echo "=== Starting Maven build ===" && \
    if ! ./mvnw clean package -DskipTests; then \
        echo "✗ Build failed! Showing error output:" && \
        exit 1; \
    fi && \
    echo "✓ Build completed successfully"

# Verify JAR files được tạo và tìm executable JAR
RUN echo "=== Checking target directory ===" && \
    ls -la /app/target/ 2>/dev/null || (echo "Target directory does not exist!" && exit 1) && \
    echo "=== Listing all JAR files ===" && \
    ls -lh /app/target/*.jar 2>/dev/null || (echo "No JAR files found in target!" && ls -la /app/target/ && exit 1) && \
    echo "=== Finding executable JAR (excluding *-plain.jar) ===" && \
    JAR_FILE=$(find /app/target -name "*.jar" ! -name "*-plain.jar" -type f | head -1) && \
    if [ -z "$JAR_FILE" ]; then \
        echo "ERROR: No executable JAR found!" && \
        echo "=== All JAR files in target ===" && \
        find /app/target -name "*.jar" -type f && \
        echo "=== Contents of /app/target ===" && \
        ls -la /app/target/ && \
        exit 1; \
    fi && \
    echo "Found JAR: $JAR_FILE" && \
    echo "=== JAR file details ===" && \
    ls -lh "$JAR_FILE" && \
    echo "=== Copying to app.jar ===" && \
    cp "$JAR_FILE" /app/target/app.jar && \
    echo "=== Verifying app.jar was created ===" && \
    ls -lh /app/target/app.jar && \
    test -f /app/target/app.jar && echo "✓ app.jar exists and is a file" || (echo "✗ app.jar copy failed!" && exit 1) && \
    JAR_SIZE=$(stat -c%s /app/target/app.jar 2>/dev/null || wc -c < /app/target/app.jar) && \
    test -s /app/target/app.jar && echo "✓ app.jar is not empty (size: $JAR_SIZE bytes)" || (echo "✗ app.jar is empty!" && exit 1) && \
    echo "=== Build stage verification complete ===" && \
    echo "=== Final check: app.jar ready for COPY ===" && \
    ls -lh /app/target/app.jar

# Giai đoạn 2: Tạo hình ảnh Docker cuối cùng
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Tạo user non-root để chạy ứng dụng (bảo mật hơn)
RUN groupadd -r spring && useradd -r -g spring spring

# Copy JAR từ build stage (đã được đổi tên thành app.jar trong build stage)
COPY --from=build /app/target/app.jar app.jar

# Verify JAR file exists và có quyền đọc
RUN echo "=== Checking for app.jar ===" && \
    if [ ! -f app.jar ]; then \
        echo "ERROR: app.jar not found after COPY!" && \
        echo "=== Contents of /app ===" && \
        ls -la /app/ && \
        echo "=== Trying to find any JAR files ===" && \
        find /app -name "*.jar" -type f || echo "No JAR files found" && \
        exit 1; \
    fi && \
    echo "=== JAR file info ===" && \
    ls -lh app.jar && \
    echo "=== JAR file size (bytes) ===" && \
    stat -c%s app.jar 2>/dev/null || wc -c < app.jar && \
    chown spring:spring app.jar && \
    chmod 755 app.jar && \
    echo "=== Verification complete ==="

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