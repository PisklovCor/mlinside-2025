# CryptoAgents - Multi-Agent Crypto Analysis System

Проект по курсу MLinside: ИИ в разработке

## Описание

CryptoAgents - это система мультиагентного анализа криптовалют, состоящая из трех специализированных агентов:
- **Analyst** - технический анализ и прогнозирование цен
- **Risk Manager** - оценка рисков и управление позициями  
- **Trader** - торговые рекомендации и стратегии

## Технологии

- **Backend**: Spring Boot 3.x, Java 17+
- **Database**: PostgreSQL 15
- **ORM**: Spring Data JPA + Hibernate
- **Migrations**: Flyway
- **Code Generation**: Lombok (геттеры, сеттеры, логгеры)
- **Containerization**: Docker, Docker Compose
- **Testing**: JUnit 5, Spring Boot Test

## Быстрый запуск

### 1. Предварительные требования

- Docker Desktop (для Windows/Mac) или Docker Engine + Docker Compose
- Java 17 или выше
- Maven 3.6+

### 2. Запуск базы данных

**Windows:**
```bash
# Запуск PostgreSQL контейнера
scripts\db-start.bat

# Остановка контейнера
scripts\db-stop.bat

# Просмотр логов
scripts\db-logs.bat
```

**Linux/Mac:**
```bash
# Сделать скрипты исполняемыми (только один раз)
chmod +x scripts/*.sh

# Запуск PostgreSQL контейнера
./scripts/db-start.sh

# Остановка контейнера
./scripts/db-stop.sh

# Просмотр логов
./scripts/db-logs.sh
```

**Альтернативно (Docker Compose напрямую):**
```bash
# Запуск только PostgreSQL
docker-compose up -d postgres

# Запуск PostgreSQL + PgAdmin
docker-compose up -d

# Остановка всех сервисов
docker-compose down
```

### 3. Запуск приложения

```bash
cd CryptoAgents
mvn spring-boot:run
```

### 4. Доступ к сервисам

- **API**: http://localhost:8081
- **PgAdmin** (опционально): http://localhost:8080
  - Email: admin@cryptoagents.com
  - Password: admin
- **PostgreSQL**: localhost:5432
  - Database: cryptoagents
  - Username: postgres
  - Password: password

## Разработка

### Подключение к базе данных

Конфигурация базы данных находится в файле `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cryptoagents
spring.datasource.username=postgres
spring.datasource.password=password
```

### Миграции базы данных

Flyway автоматически применяет миграции при запуске приложения. Файлы миграций находятся в:
```
src/main/resources/db/migration/
```

### Структура проекта

```
CryptoAgents/
├── src/main/java/com/cryptoagents/
│   ├── agent/          # Агенты анализа
│   ├── api/            # REST контроллеры
│   ├── config/         # Конфигурация Spring
│   ├── model/          # JPA сущности
│   ├── repository/     # Spring Data репозитории
│   └── service/        # Бизнес-логика
├── src/main/resources/
│   ├── db/migration/   # Flyway миграции
│   └── application.properties
├── docker/
│   └── init-scripts/   # PostgreSQL инициализация
└── scripts/            # Утилиты управления БД
```

### Логгирование и Lombok

Проект использует **SLF4J API** с **Logback** и **Lombok** для упрощения кода:

**Добавление логгера в класс (Lombok):**
```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MyService {
    
    public void doSomething() {
        log.info("Выполняю операцию...");
        log.debug("Детальная информация: {}", variable);
        log.warn("Предупреждение о потенциальной проблеме");
        log.error("Ошибка при выполнении: {}", errorMessage, exception);
    }
}
```

**Упрощение классов с Lombok:**
```java
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyDto {
    private String name;
    private Integer value;
    
    // Lombok автоматически генерирует:
    // - Геттеры и сеттеры
    // - toString()
    // - equals() и hashCode()
    // - Конструктор без аргументов
}
```

**Конфигурация логгирования:**
- **Файл конфигурации**: `src/main/resources/logback-spring.xml`
- **Профили**: dev (подробное логгирование), prod (минимальное)
- **Файлы логов**: `logs/crypto-agents.log`, `logs/crypto-agents-error.log`

**Уровни логгирования по профилям:**
- **dev**: DEBUG для com.cryptoagents, INFO для остальных
- **prod**: INFO для com.cryptoagents, WARN для остальных

## Тестирование

```bash
# Запуск всех тестов
mvn test

# Запуск конкретного теста
mvn test -Dtest=ClassName

# Запуск с профилем test
mvn test -Dspring.profiles.active=test
```
