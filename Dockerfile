# ----------------------
# 1. Стадия сборки
# ----------------------
FROM gradle:8.3-jdk17 AS builder
WORKDIR /app

# Копируем файлы сборки и wrapper
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle/wrapper/gradle-wrapper.properties gradle/wrapper/gradle-wrapper.properties
COPY src ./src

# Собираем проект (без тестов, чтобы быстрее)
RUN gradle build -x test --no-daemon

# ----------------------
# 2. Финальный образ
# ----------------------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
