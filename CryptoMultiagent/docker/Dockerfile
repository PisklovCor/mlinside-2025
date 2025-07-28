# Multi-stage build для Spring Boot приложения
# Этап 1: Сборка приложения
FROM maven:3.9.5-eclipse-temurin-17 AS builder

# Установка рабочей директории
WORKDIR /app

# Копирование pom.xml для кеширования зависимостей
COPY pom.xml .

# Загрузка зависимостей (кеширование слоя)
RUN mvn dependency:go-offline -B

# Копирование исходного кода
COPY src ./src

# Сборка приложения
RUN mvn clean package -DskipTests

# Этап 2: Финальный образ
FROM openjdk:17-jdk-slim

# Установка рабочей директории
WORKDIR /app

# Создание пользователя для безопасности
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Копирование JAR файла из этапа сборки
COPY --from=builder /app/target/*.jar app.jar

# Изменение владельца файлов
RUN chown -R appuser:appuser /app

# Переключение на непривилегированного пользователя
USER appuser

# Открытие порта
EXPOSE 8080

# Указание точки входа
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Метаданные образа
LABEL maintainer="mlinside-2025"
LABEL version="1.0"
LABEL description="Crypto Multi-Agent Analysis Application" 