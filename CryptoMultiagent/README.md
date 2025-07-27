# Crypto Multi-Agent Analysis System

Мультиагентная система для анализа криптовалют, построенная на Spring Boot с использованием Spring AI. Система использует три специализированных AI-агента для комплексного анализа криптовалютных активов.

## 🚀 Особенности

- **Мультиагентная архитектура** с тремя специализированными агентами
- **Технический анализ** - анализ графиков, индикаторов и трендов
- **Фундаментальный анализ** - оценка проекта, команды и технологий
- **Анализ настроений** - мониторинг медиа и социальных сетей
- **Взвешенные рекомендации** на основе уверенности агентов
- **Асинхронная обработка** для улучшенной производительности
- **REST API** с валидацией входных данных
- **Comprehensive testing** с покрытием всех компонентов

## 🏗️ Архитектура

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│  REST Controller │───▶│  Analysis Service │───▶│   AI Agents     │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                 │                       │
                                 ▼                       ▼
                       ┌──────────────────┐    ┌─────────────────┐
                       │ Response Builder │    │   Spring AI     │
                       └──────────────────┘    │   (OpenAI GPT)  │
                                               └─────────────────┘
```

### Агенты системы:

1. **TechnicalAnalysisAgent** 🔍
   - Анализ ценовых трендов и паттернов
   - Технические индикаторы (RSI, MACD, Moving Averages)
   - Уровни поддержки и сопротивления
   - Объемы торгов

2. **FundamentalAnalysisAgent** 📊
   - Анализ технологии и инноваций
   - Оценка команды и партнерств
   - Tokenomics и механизмы стоимости
   - Конкурентные преимущества

3. **SentimentAnalysisAgent** 📈
   - Мониторинг новостей и медиа
   - Анализ социальных сетей
   - Fear & Greed Index
   - Институциональный интерес

## 🛠️ Технологический стек

- **Java 21**
- **Spring Boot 3.3.0**
- **Spring AI 1.0.0-M4**
- **OpenRouter**
- **Maven** для сборки
- **JUnit 5** для тестирования
- **Mockito** для мокирования

## 📦 Установка и запуск

### Предварительные требования

- Java 21+
- Maven 3.8+
- OpenAI API ключ

### 1. Клонирование репозитория

```bash
git clone <repository-url>
cd crypto-multiagent
```

### 2. Настройка конфигурации

Создайте файл `application-local.yml` или установите переменную окружения:

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:your-openai-api-key-here}
```

Или через переменную окружения:
```bash
export OPENAI_API_KEY="your-openai-api-key"
```

### 3. Сборка проекта

```bash
mvn clean install
```

### 4. Запуск приложения

```bash
mvn spring-boot:run
```

Приложение будет доступно по адресу: `http://localhost:8080`

## 🔗 API Endpoints

### Анализ криптовалюты (POST)

```http
POST /api/crypto/analyze
Content-Type: application/json

{
  "cryptocurrency": "Bitcoin",
  "timeframe": "1 месяц"
}
```

### Анализ криптовалюты (GET)

```http
GET /api/crypto/analyze/Bitcoin?timeframe=1%20месяц
```

### Асинхронный анализ

```http
POST /api/crypto/analyze/async
GET /api/crypto/analyze/Bitcoin/async?timeframe=2%20недели
```

### Проверка состояния

```http
GET /api/crypto/agents/status
```

## 📋 Пример ответа

```json
{
  "cryptocurrency": "Bitcoin",
  "agentAnalyses": [
    {
      "agentName": "Технический Аналитик",
      "analysis": "Технический анализ показывает восходящий тренд...",
      "recommendation": "ПОКУПАТЬ",
      "confidence": 0.8
    },
    {
      "agentName": "Фундаментальный Аналитик", 
      "analysis": "Сильные фундаментальные показатели...",
      "recommendation": "ПОКУПАТЬ",
      "confidence": 0.9
    },
    {
      "agentName": "Аналитик Настроений",
      "analysis": "Позитивные настроения в сообществе...",
      "recommendation": "ПОКУПАТЬ", 
      "confidence": 0.7
    }
  ],
  "finalRecommendation": "ПОКУПАТЬ",
  "averageConfidence": 0.8
}
```

## 🧪 Тестирование

### Запуск всех тестов

```bash
mvn test
```

### Запуск конкретного класса тестов

```bash
mvn test -Dtest=CryptoAnalysisServiceTest
mvn test -Dtest=CryptoAnalysisControllerTest
mvn test -Dtest=TechnicalAnalysisAgentTest
```

### Покрытие тестами

- **Сервисный слой**: синхронный/асинхронный анализ, логика рекомендаций
- **REST API**: валидация входных данных, обработка ошибок
- **Агенты**: распознавание сигналов, обработка ошибок AI
- **Интеграционные тесты**: полный цикл анализа

## 🔧 Конфигурация

### application.yml

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      base-url: https://openrouter.ai/api
      chat:
        options:
          model: anthropic/claude-3-sonnet
          temperature: 0.7
          maxTokens: 1000

server:
  port: 8080

logging:
  level:
    com.example.crypto: DEBUG
```

### Доступные модели

- `gpt-4` (рекомендуется для продакшена)
- `gpt-3.5-turbo` (для разработки и тестирования)

## 📊 Мониторинг и метрики

Приложение включает Spring Boot Actuator для мониторинга:

```http
GET /actuator/health
GET /actuator/metrics
GET /actuator/info
```

## 🚦 Обработка ошибок

Система включает несколько уровней обработки ошибок:

- **Retry механизм** для AI запросов (3 попытки)
- **Fallback значения** при сбоях агентов
- **Валидация входных данных** с информативными сообщениями
- **Graceful degradation** при недоступности отдельных агентов

## 🔒 Безопасность

- API ключи хранятся в переменных окружения
- Валидация всех входящих данных
- Rate limiting через Spring AI Retry
- Отсутствие чувствительной информации в логах

## 📈 Производительность

### Синхронный режим
- Последовательное выполнение анализов
- Время ответа: ~15-30 секунд

### Асинхронный режим
- Параллельное выполнение анализов
- Время ответа: ~8-15 секунд
- Лучшее использование ресурсов

## 🤝 Вклад в проект

1. Fork репозитория
2. Создайте feature branch (`git checkout -b feature/amazing-feature`)
3. Commit изменения (`git commit -m 'Add amazing feature'`)
4. Push в branch (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

## 📝 Примеры использования

### Curl команды

```bash
# Анализ Bitcoin
curl -X POST http://localhost:8080/api/crypto/analyze \
  -H "Content-Type: application/json" \
  -d '{"cryptocurrency": "Bitcoin", "timeframe": "1 месяц"}'

# Анализ Ethereum через GET
curl "http://localhost:8080/api/crypto/analyze/Ethereum?timeframe=2%20недели"
```

### JavaScript пример

```javascript
const analyzeResponse = await fetch('http://localhost:8080/api/crypto/analyze', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    cryptocurrency: 'Cardano',
    timeframe: '3 месяца'
  })
});

const analysis = await analyzeResponse.json();
console.log(`Рекомендация: ${analysis.finalRecommendation}`);
console.log(`Уверенность: ${analysis.averageConfidence}`);
```

## 🐛 Известные проблемы

- API ключ OpenAI требует активной подписки
- Время ответа зависит от загрузки OpenAI API
- Некоторые криптовалюты могут быть неизвестны модели

## 📚 Дополнительные ресурсы

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API Documentation](https://platform.openai.com/docs)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)

## 📄 Лицензия

MIT License - см. файл [LICENSE](LICENSE) для деталей.

## 🙏 Благодарности

- Spring AI Team за отличный фреймворк
- OpenAI за мощные языковые модели
- Криптовалютное сообщество за вдохновение