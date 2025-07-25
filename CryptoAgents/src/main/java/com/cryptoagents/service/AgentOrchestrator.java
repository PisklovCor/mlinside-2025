package com.cryptoagents.service;

import com.cryptoagents.agent.*;
import com.cryptoagents.model.AnalysisReport;
import com.cryptoagents.model.AnalysisResult;
import com.cryptoagents.model.dto.MarketData;
import com.cryptoagents.model.dto.HistoricalData;
import com.cryptoagents.model.enums.TimePeriod;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Сервис, координирующий выполнение агентов анализа криптовалют.
 * 
 * Этот компонент координирует последовательное выполнение агентов в правильном порядке:
 * 1. Аналитик - Выполняет технический анализ
 * 2. Риск-менеджер - Оценивает риски на основе результатов аналитика
 * 3. Трейдер - Принимает торговые рекомендации на основе предыдущих результатов
 * 
 * Поддерживает как последовательный анализ одного токена, так и параллельный анализ нескольких токенов.
 */
@Service
@RequiredArgsConstructor
public class AgentOrchestrator {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentOrchestrator.class);
    
    // Зависимости, внедряемые через конструктор
    private final AgentFactory agentFactory;
    private final CryptoDataService cryptoDataService;
    
    // Порядок выполнения агентов
    private static final List<Agent.AgentType> AGENT_EXECUTION_ORDER = Arrays.asList(
            Agent.AgentType.ANALYST,
            Agent.AgentType.RISK_MANAGER,
            Agent.AgentType.TRADER
    );
    
    // Пул потоков для параллельного выполнения
    private final Executor executorService = Executors.newCachedThreadPool();
    
    // Метрики для мониторинга производительности оркестратора
    private final OrchestratorMetrics metrics = new OrchestratorMetrics();
    
    /**
     * Анализирует один тикер криптовалюты, используя всех агентов последовательно.
     * 
     * Это основная точка входа для анализа одного токена.
     * Агенты выполняются в предопределенном порядке с передачей результатов между ними.
     * 
     * @param ticker тикер криптовалюты для анализа (например, "BTC", "ETH")
     * @return полный отчет об анализе, содержащий результаты от всех агентов
     * @throws IllegalArgumentException если тикер равен null или пустой
     */
    public AnalysisReport analyze(String ticker) throws OrchestrationException {
        // Настройка контекста логирования для этой операции
        String operationId = java.util.UUID.randomUUID().toString().substring(0, 8);
        MDC.put("operationId", operationId);
        MDC.put("ticker", ticker);
        
        logger.info("Начало анализа для тикера: {} [operationId: {}]", ticker, operationId);
        metrics.recordAnalysisStart(ticker);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Валидация
            if (!StringUtils.hasText(ticker)) {
                throw new OrchestrationException(ticker, "VALIDATION", "Тикер не может быть null или пустым");
            }
            
            // Инициализация отчета
            AnalysisReport report = AnalysisReport.builder()
                    .ticker(ticker.toUpperCase())
                    .analysisStartTime(LocalDateTime.now())
                    .successful(true)
                    .build();
            
            // Проверка поддержки тикера
            if (!cryptoDataService.isTickerSupported(ticker)) {
                String error = "Тикер не поддерживается: " + ticker;
                logger.warn(error);
                report.addError(error);
                metrics.recordAnalysisFailure(ticker, error);
                return report;
            }
            
            // Подготовка контекста анализа с рыночными данными
            AnalysisContext context = prepareAnalysisContext(ticker);
            if (context == null) {
                String error = "Не удалось получить рыночные данные для тикера: " + ticker;
                logger.error(error);
                report.addError(error);
                metrics.recordAnalysisFailure(ticker, error);
                throw new OrchestrationException(ticker, "DATA_RETRIEVAL", operationId, error, null);
            }
            
            // Выполнение агентов последовательно
            executeAgentsSequentially(context, report);
            
            // Запись успешной завершения
            long totalTime = System.currentTimeMillis() - startTime;
            metrics.recordAnalysisSuccess(ticker, totalTime);
            
            // Финальная обработка отчета
            report.setAnalysisEndTime(LocalDateTime.now());
            report.calculateExecutionTime();
            
            logger.info("Анализ завершен для тикера: {} за {}мс. Успех: {} [operationId: {}]", 
                    ticker, report.getExecutionTimeMs(), report.isSuccessful(), operationId);
            
            return report;
            
        } catch (OrchestrationException oe) {
            long executionTime = System.currentTimeMillis() - startTime;
            metrics.recordAnalysisFailure(ticker, oe.getMessage());
            logger.error("Ошибка оркестрации для тикера: {} после {}мс [operationId: {}]", ticker, executionTime, operationId, oe);
            throw oe;
        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            String error = "Непредвиденная ошибка во время анализа: " + ex.getMessage();
            metrics.recordAnalysisFailure(ticker, error);
            logger.error("Непредвиденная ошибка во время анализа для тикера: {} после {}мс [operationId: {}]", ticker, executionTime, operationId, ex);
            throw new OrchestrationException(ticker, "UNEXPECTED_ERROR", operationId, error, ex);
        } finally {
            // Очистка MDC
            MDC.clear();
        }
    }
    
    /**
     * Анализирует несколько тикеров криптовалют параллельно.
     * 
     * Каждый тикер анализируется независимо, используя полную конвейерную обработку агентов.
     * Этот метод оптимизирует пропускную способность, выполняя несколько анализов параллельно.
     * 
     * @param tickers коллекция тикеров криптовалют для анализа
     * @return карта тикера к отчету об анализе
     * @throws IllegalArgumentException если tickers равен null или пустой
     */
    public Map<String, AnalysisReport> analyzeMultiple(Collection<String> tickers) {
        logger.info("Начало параллельного анализа для {} тикеров", tickers != null ? tickers.size() : 0);
        
        if (tickers == null || tickers.isEmpty()) {
            throw new IllegalArgumentException("Коллекция тикеров не может быть null или пустой");
        }
        
        // Создание будущих для параллельного выполнения
        Map<String, CompletableFuture<AnalysisReport>> futures = tickers.stream()
                .collect(Collectors.toMap(
                        ticker -> ticker,
                        ticker -> CompletableFuture.supplyAsync(() -> {
                            try {
                                return analyze(ticker);
                            } catch (OrchestrationException e) {
                                logger.error("Ошибка оркестрации для тикера: {} в параллельном выполнении", ticker, e);
                                AnalysisReport errorReport = AnalysisReport.builder()
                                        .ticker(ticker)
                                        .analysisStartTime(LocalDateTime.now())
                                        .analysisEndTime(LocalDateTime.now())
                                        .successful(false)
                                        .build();
                                errorReport.addError("Ошибка оркестрации: " + e.getMessage());
                                return errorReport;
                            }
                        }, executorService)
                ));
        
        // Ожидание завершения всех анализов и сбор результатов
        Map<String, AnalysisReport> results = new HashMap<>();
        futures.forEach((ticker, future) -> {
            try {
                results.put(ticker, future.get());
            } catch (Exception e) {
                logger.error("Не удалось завершить анализ для тикера: {}", ticker, e);
                AnalysisReport errorReport = AnalysisReport.builder()
                        .ticker(ticker)
                        .analysisStartTime(LocalDateTime.now())
                        .analysisEndTime(LocalDateTime.now())
                        .successful(false)
                        .build();
                errorReport.addError("Параллельное выполнение завершено с ошибкой: " + e.getMessage());
                results.put(ticker, errorReport);
            }
        });
        
        logger.info("Параллельный анализ завершен для {} тикеров", results.size());
        return results;
    }
    
    /**
     * Получает список доступных агентов в порядке выполнения.
     * 
     * @return список доступных агентов, отсортированных по приоритету/порядку выполнения
     */
    public List<Agent> getAvailableAgents() {
        return AGENT_EXECUTION_ORDER.stream()
                .map(agentFactory::createAgent)
                .collect(Collectors.toList());
    }
    
    /**
     * Проверяет, готов ли оркестратор к выполнению анализа.
     * 
     * @return true, если доступны все необходимые сервисы
     */
    public boolean isReady() {
        try {
            // Проверка доступности сервиса данных
            boolean dataServiceReady = cryptoDataService.isServiceAvailable();
            
            // Проверка создания агентов
            boolean agentsReady = AGENT_EXECUTION_ORDER.stream()
                    .allMatch(agentType -> {
                        try {
                            Agent agent = agentFactory.createAgent(agentType);
                            return agent != null;
                        } catch (Exception e) {
                            logger.warn("Не удалось создать агента: {}", agentType, e);
                            return false;
                        }
                    });
            
            boolean ready = dataServiceReady && agentsReady;
            logger.debug("Статус готовности оркестратора: dataService={}, agents={}, overall={}", 
                    dataServiceReady, agentsReady, ready);
            
            return ready;
        } catch (Exception e) {
            logger.error("Ошибка проверки готовности оркестратора", e);
            return false;
        }
    }
    
    /**
     * Подготавливает контекст анализа с необходимыми рыночными данными.
     * 
     * @param ticker тикер криптовалюты
     * @return подготовленный контекст анализа или null, если данные не могут быть получены
     */
    private AnalysisContext prepareAnalysisContext(String ticker) {
        logger.debug("Подготовка контекста анализа для тикера: {}", ticker);
        
        try {
            AnalysisContext context = new AnalysisContext();
            context.setTicker(ticker.toUpperCase());
            
            // Получение текущих рыночных данных
            Optional<MarketData> marketDataOpt = cryptoDataService.getMarketData(ticker);
            if (marketDataOpt.isPresent()) {
                context.setMarketData(marketDataOpt.get());
                logger.debug("Рыночные данные получены для тикера: {}", ticker);
            } else {
                logger.warn("Рыночные данные недоступны для тикера: {}", ticker);
                return null;
            }
            
            // Получение исторических данных (по умолчанию 30 дней)
            Optional<HistoricalData> historicalDataOpt = cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH);
            if (historicalDataOpt.isPresent()) {
                context.setHistoricalData(Arrays.asList(historicalDataOpt.get()));
                logger.debug("Исторические данные получены для тикера: {}", ticker);
            } else {
                logger.warn("Исторические данные недоступны для тикера: {}", ticker);
                // Исторические данные являются необязательными, поэтому продолжаем без них
            }
            
            context.setStartTime(System.currentTimeMillis());
            return context;
            
        } catch (Exception e) {
            logger.error("Не удалось подготовить контекст анализа для тикера: {}", ticker, e);
            return null;
        }
    }
    
    /**
     * Выполняет всех агентов в предопределенной последовательности.
     * 
     * @param context контекст анализа с рыночными данными
     * @param report отчет для заполнения результатами
     */
    private void executeAgentsSequentially(AnalysisContext context, AnalysisReport report) throws OrchestrationException {
        String ticker = context.getTicker();
        logger.debug("Выполнение агентов последовательно для тикера: {}", ticker);
        
        for (Agent.AgentType agentType : AGENT_EXECUTION_ORDER) {
            long agentStartTime = System.currentTimeMillis();
            String agentName = agentType.name();
            
            try {
                // Создание экземпляра агента
                Agent agent = agentFactory.createAgent(agentType);
                agentName = agent.getName(); // Использование фактического имени агента
                
                logger.debug("Выполнение агента: {} для тикера: {}", agentName, ticker);
                
                // Проверка, может ли агент анализировать этот контекст
                if (!agent.canAnalyze(context)) {
                    String warning = String.format("Агент %s не может анализировать тикер: %s", agentName, ticker);
                    logger.warn(warning);
                    report.addError(warning);
                    
                    // Запись неудачного выполнения в метрики
                    long executionTime = System.currentTimeMillis() - agentStartTime;
                    metrics.recordAgentExecution(agentName, executionTime, false);
                    continue;
                }
                
                // Выполнение анализа агента
                AnalysisResult result = agent.analyze(context);
                long executionTime = System.currentTimeMillis() - agentStartTime;
                
                if (result != null) {
                    // Добавление результата в отчет
                    report.getAgentResults().add(result);
                    
                    // Добавление результата в контекст для следующего агента
                    context.addAgentResult(agentName, result);
                    
                    // Запись времени выполнения
                    report.addAgentExecutionTime(agentName, executionTime);
                    
                    // Запись успешного выполнения в метрики
                    metrics.recordAgentExecution(agentName, executionTime, true);
                    
                    logger.info("Агент {} завершил анализ для тикера: {} за {}мс", 
                            agentName, ticker, executionTime);
                } else {
                    String error = String.format("Агент %s вернул null результат для тикера: %s", agentName, ticker);
                    logger.error(error);
                    report.addError(error);
                    
                    // Запись неудачного выполнения в метрики
                    metrics.recordAgentExecution(agentName, executionTime, false);
                    
                    // Это критическая ошибка - мы не должны продолжать оркестрацию
                    throw new OrchestrationException(ticker, "AGENT_NULL_RESULT", 
                            String.format("Агент %s вернул null результат", agentName));
                }
                
            } catch (AgentAnalysisException aae) {
                long executionTime = System.currentTimeMillis() - agentStartTime;
                String error = String.format("Агент %s не удалось проанализировать: %s", agentName, aae.getMessage());
                
                logger.error(error, aae);
                report.addError(error);
                report.addAgentExecutionTime(agentName, executionTime);
                
                // Запись неудачного выполнения в метрики
                metrics.recordAgentExecution(agentName, executionTime, false);
                
                // Продолжение с следующим агентом, а не отказ всей оркестрации
                logger.warn("Продолжение с следующим агентом после {} неудачи", agentName);
                
            } catch (Exception ex) {
                long executionTime = System.currentTimeMillis() - agentStartTime;
                String error = String.format("Непредвиденная ошибка в агенте %s: %s", agentName, ex.getMessage());
                
                logger.error(error, ex);
                report.addError(error);
                report.addAgentExecutionTime(agentName, executionTime);
                
                // Запись неудачного выполнения в метрики
                metrics.recordAgentExecution(agentName, executionTime, false);
                
                // Для неожиданных ошибок мы можем завершить всю оркестрацию
                throw new OrchestrationException(ticker, "AGENT_UNEXPECTED_ERROR", 
                        String.format("Непредвиденная ошибка в агенте %s", agentName), ex);
            }
        }
        
        logger.debug("Последовательное выполнение агентов завершено для тикера: {}", ticker);
        
                 // Проверка, производил ли хоть один агент результаты
         if (report.getAgentResults().isEmpty()) {
             throw new OrchestrationException(ticker, "NO_AGENT_RESULTS", 
                     "Ни один агент не произвел успешные результаты");
         }
    }
    
    /**
     * Получает метрики производительности оркестратора.
     * 
     * @return текущий экземпляр метрик
     */
    public OrchestratorMetrics getMetrics() {
        return metrics;
    }
    
    /**
     * Логирует сводку текущих метрик.
     * Полезно для мониторинга и отладки производительности оркестратора.
     */
    public void logMetricsSummary() {
        metrics.logMetricsSummary();
    }
} 