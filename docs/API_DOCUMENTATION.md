# Crypto Multi-Agent Analysis API Documentation

## Обзор

API для анализа криптовалют с использованием мульти-агентной системы. Система использует различные агенты для комплексного анализа криптовалют и предоставляет рекомендации.

## Доступ к документации

После запуска приложения документация доступна по следующим URL:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Эндпоинты

### 1. Анализ криптовалюты (синхронный)

**POST** `/api/crypto/analyze`

Выполняет анализ криптовалюты в синхронном режиме.

#### Запрос

```json
{
  "cryptocurrency": "Bitcoin",
  "timeframe": "1 месяц"
}
```

#### Ответ

```json
{
  "cryptocurrency": "Bitcoin",
  "agentAnalyses": [
    {
      "agentName": "Technical Analysis Agent",
      "analysis": "Технический анализ показывает восходящий тренд",
      "recommendation": "ПОКУПАТЬ",
      "confidence": 0.85
    },
    {
      "agentName": "Fundamental Analysis Agent",
      "analysis": "Фундаментальный анализ положительный",
      "recommendation": "ПОКУПАТЬ",
      "confidence": 0.78
    },
    {
      "agentName": "Sentiment Analysis Agent",
      "analysis": "Настроения рынка позитивные",
      "recommendation": "ПОКУПАТЬ",
      "confidence": 0.92
    }
  ],
  "finalRecommendation": "ПОКУПАТЬ",
  "averageConfidence": 0.85
}
```

### 2. Анализ криптовалюты (асинхронный)

**POST** `/api/crypto/analyze/async`

Выполняет анализ криптовалюты в асинхронном режиме.

#### Запрос

```json
{
  "cryptocurrency": "Ethereum",
  "timeframe": "2 недели"
}
```

### 3. Анализ криптовалюты по названию (синхронный)

**GET** `/api/crypto/analyze/{crypto}?timeframe={timeframe}`

Выполняет анализ криптовалюты по названию в синхронном режиме.

#### Параметры

- `crypto` (path) - название криптовалюты (2-50 символов, только буквы, цифры, пробелы и дефисы)
- `timeframe` (query) - временной период (по умолчанию "1 месяц")

#### Пример

```
GET /api/crypto/analyze/Bitcoin?timeframe=1%20месяц
```

### 4. Анализ криптовалюты по названию (асинхронный)

**GET** `/api/crypto/analyze/{crypto}/async?timeframe={timeframe}`

Выполняет анализ криптовалюты по названию в асинхронном режиме.

#### Параметры

- `crypto` (path) - название криптовалюты (2-50 символов)
- `timeframe` (query) - временной период (по умолчанию "1 месяц")

### 5. Статус агентов

**GET** `/api/crypto/agents/status`

Получает текущий статус всех агентов в системе.

#### Ответ

```
Technical Analysis Agent: ACTIVE
Fundamental Analysis Agent: ACTIVE
Sentiment Analysis Agent: ACTIVE
```

## Коды ответов

- **200** - Успешное выполнение
- **400** - Некорректные данные запроса
- **500** - Внутренняя ошибка сервера

## Валидация

### CryptoAnalysisRequest

- `cryptocurrency` - обязательное поле, 2-50 символов
- `timeframe` - обязательное поле

### Path параметры

- Название криптовалюты должно содержать только буквы, цифры, пробелы и дефисы
- Длина названия: 2-50 символов

## Примеры использования

### cURL

```bash
# Синхронный анализ
curl -X POST "http://localhost:8080/api/crypto/analyze" \
  -H "Content-Type: application/json" \
  -d '{"cryptocurrency": "Bitcoin", "timeframe": "1 месяц"}'

# Асинхронный анализ
curl -X POST "http://localhost:8080/api/crypto/analyze/async" \
  -H "Content-Type: application/json" \
  -d '{"cryptocurrency": "Ethereum", "timeframe": "2 недели"}'

# Анализ по названию
curl -X GET "http://localhost:8080/api/crypto/analyze/Bitcoin?timeframe=1%20месяц"

# Статус агентов
curl -X GET "http://localhost:8080/api/crypto/agents/status"
```

### JavaScript

```javascript
// Синхронный анализ
const response = await fetch('http://localhost:8080/api/crypto/analyze', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    cryptocurrency: 'Bitcoin',
    timeframe: '1 месяц'
  })
});

const result = await response.json();
console.log(result);
```

## Агенты системы

1. **Technical Analysis Agent** - выполняет технический анализ
2. **Fundamental Analysis Agent** - выполняет фундаментальный анализ
3. **Sentiment Analysis Agent** - анализирует настроения рынка

Каждый агент предоставляет:
- Результат анализа
- Рекомендацию (ПОКУПАТЬ/ПРОДАВАТЬ/ДЕРЖАТЬ)
- Уровень уверенности (0.0 - 1.0) 