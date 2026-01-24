# Dockerfile
# Многоэтапная сборка для модульного Gradle проекта
FROM gradle:8.10-jdk25 AS cache

# Настройка Gradle кэша для ускорения сборок
WORKDIR /app

# Копируем файлы конфигурации (для кэширования)
COPY gradle gradle/
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .
COPY gradle.properties .
COPY build.gradle.kts .

# Создаем кэш зависимостей (пустая структура проекта)
RUN mkdir -p security/src/main/java && \
    echo "// Temp" > security/src/main/java/Temp.java && \
    mkdir -p src/main/java && \
    echo "// Temp" > src/main/java/Temp.java

# Загружаем зависимости в кэш
RUN ./gradlew dependencies --no-daemon || true

# Этап сборки
FROM gradle:8.10-jdk25 AS builder

WORKDIR /app

# Копируем кэш Gradle
COPY --from=cache /root/.gradle /root/.gradle
COPY --from=cache /app /app

# Теперь копируем реальный исходный код
COPY . .

# Собираем проект
RUN ./gradlew clean bootJar --no-daemon --stacktrace

# Этап рантайма
FROM openjdk:25-jdk-slim

WORKDIR /app

# Метаданные образа
LABEL maintainer="skripov"
LABEL version="1.0.0"
LABEL description="Resume Backend Application"

# Копируем собранный JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Создаем пользователя для безопасности
RUN groupadd -r spring && useradd -r -g spring spring \
    && chown -R spring:spring /app \
    && mkdir -p /app/logs \
    && chown -R spring:spring /app/logs

USER spring:spring

# Открываем порты
EXPOSE 8080

# Здоровье приложения
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Оптимизированный запуск для контейнеров
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-XX:+UseStringDeduplication", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=docker", \
    "-Dlogging.file.name=/app/logs/application.log", \
    "-jar", \
    "app.jar"]