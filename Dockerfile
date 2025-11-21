# Giai đoạn 1: Xây dựng ứng dụng
FROM eclipse-temurin:17-jdk AS build
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

# Build application
RUN ./mvnw clean package -DskipTests

# Giai đoạn 2: Tạo hình ảnh Docker cuối cùng
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Tạo user non-root để chạy ứng dụng (bảo mật hơn)
RUN groupadd -r spring && useradd -r -g spring spring

# Copy JAR từ build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8080

# Health check (using wget which is available in JRE image)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", \
  "/app.jar"]