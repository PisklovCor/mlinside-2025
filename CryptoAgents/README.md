# CryptoAgents

Multi-agent system for cryptocurrency analysis using Spring AI.

## Code Style Validation

Проект использует Google CheckStyle для валидации кода по стандартам Google Java Style Guide.

### Доступные команды CheckStyle

#### Основная проверка
```bash
mvn checkstyle:check
```
Запускает полную проверку кода и выводит результаты в консоль. При обнаружении нарушений сборка завершается с ошибкой.

#### Строгая проверка (профиль)
```bash
mvn validate -P strict-checkstyle
```
Запускает более строгую проверку кода с дополнительными правилами.

#### Генерация отчета
```bash
mvn checkstyle:checkstyle
```
Генерирует HTML отчет с результатами проверки в `target/site/checkstyle.html`.

#### Проверка только основного кода (без тестов)
```bash
mvn checkstyle:check -Dcheckstyle.includeTestSourceDirectory=false
```

### Конфигурация

- **Основные правила**: Файл `checkstyle.xml` в корне проекта
- **Исключения**: Файл `checkstyle-suppressions.xml` 
- **Стандарт**: Google Java Style Guide с отступами в 2 пробела

### Исключения из проверки

CheckStyle автоматически исключает из проверки:
- Сгенерированный код (`target/generated/`)
- Тестовые файлы (частично - без требований к Javadoc)
- Конфигурационные классы (частично)
- DTO и entity классы (частично)

### Интеграция с IDE

Для работы с CheckStyle в IDE рекомендуется:
1. Установить плагин CheckStyle для вашей IDE
2. Импортировать файл `checkstyle.xml` как конфигурацию
3. Настроить автоматическое форматирование по правилам Google Java Style

### Основные правила

- **Отступы**: 2 пробела
- **Максимальная длина строки**: 100 символов  
- **Импорты**: Без использования `.*`, правильный порядок
- **Именование**: camelCase для методов/переменных, PascalCase для классов
- **Javadoc**: Обязательный для публичных классов и методов

### Автоматическое выполнение

CheckStyle автоматически запускается на фазе `validate` при сборке проекта:
```bash
mvn compile
```

Для пропуска CheckStyle используйте:
```bash
mvn compile -Dcheckstyle.skip=true
```

### Полезные команды

Проверка конкретного файла:
```bash
mvn checkstyle:check -Dcheckstyle.includes="**/SpecificFile.java"
```

Проверка с выводом только ошибок:
```bash
mvn checkstyle:check -Dcheckstyle.violationSeverity=error
``` 