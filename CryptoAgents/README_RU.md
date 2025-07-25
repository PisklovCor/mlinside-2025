# CryptoAgents - Система мультиагентного анализа криптовалют

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
```

### 3. Запуск приложения

```bash
# Сборка проекта
mvn clean compile

# Запуск в режиме разработки
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Или запуск через IDE с профилем 'dev'
```

### 4. Проверка работоспособности

```bash
# Проверка здоровья приложения
curl http://localhost:8080/actuator/health

# Проверка доступности API
curl http://localhost:8080/api/v1/analysis/health
```

## Архитектура

### Агенты

1. **Analyst Agent** - выполняет технический анализ:
   - Анализ трендов рынка
   - Технические индикаторы
   - Уровни поддержки и сопротивления
   - Прогнозирование цен

2. **Risk Manager Agent** - оценивает риски:
   - Анализ волатильности
   - Расчет Value at Risk (VaR)
   - Оценка ликвидности
   - Рекомендации по размеру позиции

3. **Trader Agent** - генерирует торговые рекомендации:
   - Торговые сигналы (BUY/SELL/HOLD)
   - Целевые цены входа и выхода
   - Stop-loss и take-profit уровни
   - Стратегии исполнения

### База данных

- **analysis_results** - базовая таблица для всех результатов анализа
- **analyst_reports** - отчеты технического анализа
- **risk_manager_reports** - отчеты оценки рисков
- **trader_reports** - торговые рекомендации

### API

REST API предоставляет следующие эндпоинты:
- `POST /api/v1/analysis/{ticker}` - анализ криптовалюты
- `GET /api/v1/analysis/{ticker}/reports` - получение отчетов
- `GET /api/v1/metrics` - метрики производительности

## Разработка

### Структура проекта

```
src/main/java/com/cryptoagents/
├── agent/          # Реализации агентов
├── api/            # REST контроллеры
├── config/         # Конфигурации Spring
├── model/          # Модели данных и DTO
├── repository/     # JPA репозитории
├── service/        # Бизнес-логика
└── util/           # Утилиты
```

### Тестирование

```bash
# Запуск всех тестов
mvn test

# Запуск интеграционных тестов
mvn test -Dtest=*IntegrationTest

# Запуск с покрытием
mvn test jacoco:report
```

### Линтинг

```bash
# Проверка стиля кода
mvn checkstyle:check

# Исправление стиля кода
mvn checkstyle:checkstyle
```

## Конфигурация

### Профили

- **dev** - режим разработки с H2 базой данных
- **test** - режим тестирования
- **prod** - продакшн режим с PostgreSQL

### Настройки

Основные настройки в `application.properties`:
- Параметры подключения к базе данных
- Настройки кэширования
- Лимиты API запросов
- Логирование

## Мониторинг

### Метрики

Приложение предоставляет метрики через Spring Actuator:
- Количество запросов анализа
- Время выполнения агентов
- Частота ошибок
- Состояние кэша

### Логирование

Настроено логирование с различными уровнями:
- ERROR - критические ошибки
- WARN - предупреждения
- INFO - основная информация
- DEBUG - детальная отладочная информация

## Развертывание

### Docker

```bash
# Сборка образа
docker build -t cryptoagents .

# Запуск контейнера
docker run -p 8080:8080 cryptoagents
```

### Docker Compose

```bash
# Запуск полного стека
docker-compose up -d

# Остановка
docker-compose down
```

## Лицензия

MIT License

## Авторы

Команда разработчиков курса MLinside 