package com.cryptoagents.old.service;

import com.cryptoagents.old.agent.old.Agent;
import com.cryptoagents.old.agent.old.AgentAnalysisException;
import com.cryptoagents.old.agent.old.AgentFactory;
import com.cryptoagents.old.agent.old.AnalysisContext;
import com.cryptoagents.old.model.AnalysisReport;
import com.cryptoagents.old.model.AnalysisResult;
import com.cryptoagents.old.model.dto.MarketData;
import com.cryptoagents.old.model.enums.TimePeriod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.cryptoagents.old.agent.old.Agent.AgentType.ANALYST;
import static com.cryptoagents.old.agent.old.Agent.AgentType.RISK_MANAGER;
import static com.cryptoagents.old.agent.old.Agent.AgentType.TRADER;

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
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentOrchestrator {

    private final AgentFactory agentFactory;
    //private final CryptoDataService cryptoDataService;

    // Порядок выполнения агентов
    private static final List<Agent.AgentType> AGENT_EXECUTION_ORDER = Arrays.asList(
            ANALYST,
            RISK_MANAGER,
            TRADER
    );

    // Пул потоков для параллельного выполнения
    //private final Executor executorService = Executors.newCachedThreadPool();

    // Метрики для мониторинга производительности оркестратора
    //private final OrchestratorMetrics metrics = new OrchestratorMetrics();

    /**
     * Анализирует один тикер криптовалюты, используя всех агентов последовательно.
     * <p>
     * Это основная точка входа для анализа одного токена.
     * Агенты выполняются в предопределенном порядке с передачей результатов между ними.
     *
     * @param ticker тикер криптовалюты для анализа (например, "BTC", "ETH")
     * @return полный отчет об анализе, содержащий результаты от всех агентов
     * @throws IllegalArgumentException если тикер равен null или пустой
     */
    public AnalysisReport analyze(String ticker) throws OrchestrationException {
        log.info("Начало анализа для тикера: {}", ticker);

        try {
            // Валидация
            if (!StringUtils.hasText(ticker)) {
                throw new OrchestrationException(ticker);
            }

            // Инициализация отчета
            AnalysisReport report = AnalysisReport.builder()
                    .ticker(ticker.toUpperCase()).build();

            // Проверка поддержки тикера
//            if (!cryptoDataService.isTickerSupported(ticker)) {
//                String error = "Тикер не поддерживается: " + ticker;
//                log.warn(error);
//                report.setError(error);
//               // metrics.recordAnalysisFailure(ticker, error);
//                return report;
//            }

            // Подготовка контекста анализа с рыночными данными
            AnalysisContext context = prepareAnalysisContext(ticker);
            if (context == null) {
                String error = "Не удалось получить рыночные данные для тикера: " + ticker;
                log.error(error);
                report.setError(error);
                //metrics.recordAnalysisFailure(ticker, error);
                //throw new OrchestrationException(ticker, "DATA_RETRIEVAL", operationId, error, null);
                throw new OrchestrationException(ticker);
            }

            // Выполнение агентов последовательно
            AnalysisReport fullReport = executeAgentsSequentially(context, report);

            // Запись успешной завершения
            //long totalTime = System.currentTimeMillis() - startTime;
            //metrics.recordAnalysisSuccess(ticker, totalTime);

//            log.info("Анализ завершен для тикера: {} за {}мс. Успех: {} [operationId: {}]",
//                    ticker, report.getExecutionTimeMs(), report.isSuccessful(), operationId);
            fullReport.setSuccessful(true);
            log.info("Анализ завершен для тикера: {}. Успех: {}",
                    ticker, report.isSuccessful());

            return fullReport;

        } catch (Exception ex) {
            String error = "Непредвиденная ошибка во время анализа: " + ex.getMessage();
            log.error("Непредвиденная ошибка во время анализа для тикера: {}", ticker, ex);
            throw new OrchestrationException(ticker);
        }
    }

//    /**
//     * Анализирует несколько тикеров криптовалют параллельно.
//     *
//     * Каждый тикер анализируется независимо, используя полную конвейерную обработку агентов.
//     * Этот метод оптимизирует пропускную способность, выполняя несколько анализов параллельно.
//     *
//     * @param tickers коллекция тикеров криптовалют для анализа
//     * @return карта тикера к отчету об анализе
//     * @throws IllegalArgumentException если tickers равен null или пустой
//     */
//    public Map<String, AnalysisReport> analyzeMultiple(Collection<String> tickers) {
//        logger.info("Начало параллельного анализа для {} тикеров", tickers != null ? tickers.size() : 0);
//
//        if (tickers == null || tickers.isEmpty()) {
//            throw new IllegalArgumentException("Коллекция тикеров не может быть null или пустой");
//        }
//
//        // Создание будущих для параллельного выполнения
//        Map<String, CompletableFuture<AnalysisReport>> futures = tickers.stream()
//                .collect(Collectors.toMap(
//                        ticker -> ticker,
//                        ticker -> CompletableFuture.supplyAsync(() -> {
//                            try {
//                                return analyze(ticker);
//                            } catch (OrchestrationException e) {
//                                logger.error("Ошибка оркестрации для тикера: {} в параллельном выполнении", ticker, e);
//                                AnalysisReport errorReport = AnalysisReport.builder()
//                                        .ticker(ticker)
//                                        .analysisStartTime(LocalDateTime.now())
//                                        .analysisEndTime(LocalDateTime.now())
//                                        .successful(false)
//                                        .build();
//                                errorReport.addError("Ошибка оркестрации: " + e.getMessage());
//                                return errorReport;
//                            }
//                        }, executorService)
//                ));
//
//        // Ожидание завершения всех анализов и сбор результатов
//        Map<String, AnalysisReport> results = new HashMap<>();
//        futures.forEach((ticker, future) -> {
//            try {
//                results.put(ticker, future.get());
//            } catch (Exception e) {
//                logger.error("Не удалось завершить анализ для тикера: {}", ticker, e);
//                AnalysisReport errorReport = AnalysisReport.builder()
//                        .ticker(ticker)
//                        .analysisStartTime(LocalDateTime.now())
//                        .analysisEndTime(LocalDateTime.now())
//                        .successful(false)
//                        .build();
//                errorReport.addError("Параллельное выполнение завершено с ошибкой: " + e.getMessage());
//                results.put(ticker, errorReport);
//            }
//        });
//
//        logger.info("Параллельный анализ завершен для {} тикеров", results.size());
//        return results;
//    }

//    /**
//     * Получает список доступных агентов в порядке выполнения.
//     *
//     * @return список доступных агентов, отсортированных по приоритету/порядку выполнения
//     */
//    public List<Agent> getAvailableAgents() {
//        return AGENT_EXECUTION_ORDER.stream()
//                .map(agentFactory::createAgent)
//                .collect(Collectors.toList());
//    }

    //    /**
//     * Проверяет, готов ли оркестратор к выполнению анализа.
//     *
//     * @return true, если доступны все необходимые сервисы
//     */
//    public boolean isReady() {
//        try {
//            // Проверка доступности сервиса данных
//            boolean dataServiceReady = cryptoDataService.isServiceAvailable();
//
//            // Проверка создания агентов
//            boolean agentsReady = AGENT_EXECUTION_ORDER.stream()
//                    .allMatch(agentType -> {
//                        try {
//                            Agent agent = agentFactory.createAgent(agentType);
//                            return agent != null;
//                        } catch (Exception e) {
//                            logger.warn("Не удалось создать агента: {}", agentType, e);
//                            return false;
//                        }
//                    });
//
//            boolean ready = dataServiceReady && agentsReady;
//            logger.debug("Статус готовности оркестратора: dataService={}, agents={}, overall={}",
//                    dataServiceReady, agentsReady, ready);
//
//            return ready;
//        } catch (Exception e) {
//            logger.error("Ошибка проверки готовности оркестратора", e);
//            return false;
//        }
//    }
//
//    /**
//     * Подготавливает контекст анализа с необходимыми рыночными данными.
//     *
//     * @param ticker тикер криптовалюты
//     * @return подготовленный контекст анализа или null, если данные не могут быть получены
//     */
    private AnalysisContext prepareAnalysisContext(String ticker) {
        log.debug("Подготовка контекста анализа для тикера: {}", ticker);

        try {
            AnalysisContext context = new AnalysisContext();
            context.setTicker(ticker.toUpperCase());

            // Получение текущих рыночных данных
            Optional<MarketData> marketDataOpt = cryptoDataService.getMarketData(ticker);
            if (marketDataOpt.isPresent()) {
                context.setMarketData(marketDataOpt.get());
                log.debug("Рыночные данные получены для тикера: {}", ticker);
            } else {
                log.warn("Рыночные данные недоступны для тикера: {}", ticker);
                return null;
            }

            // Получение исторических данных (по умолчанию 30 дней)
            Optional<HistoricalData> historicalDataOpt = cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH);
            if (historicalDataOpt.isPresent()) {
                context.setHistoricalData(Arrays.asList(historicalDataOpt.get()));
                log.debug("Исторические данные получены для тикера: {}", ticker);
            } else {
                log.warn("Исторические данные недоступны для тикера: {}", ticker);
                // Исторические данные являются необязательными, поэтому продолжаем без них
            }

            context.setStartTime(System.currentTimeMillis());
            return context;

        } catch (Exception e) {
            log.error("Не удалось подготовить контекст анализа для тикера: {}", ticker, e);
            return null;
        }
    }

    /**
     * Выполняет всех агентов в предопределенной последовательности.
     *
     * @param context контекст анализа с рыночными данными
     * @param report  отчет для заполнения результатами
     */
    private AnalysisReport executeAgentsSequentially(AnalysisContext context, AnalysisReport report) throws OrchestrationException {
        String ticker = context.getTicker();
        log.debug("Выполнение агентов последовательно для тикера: {}", ticker);

        for (Agent.AgentType agentType : AGENT_EXECUTION_ORDER) {
            long agentStartTime = System.currentTimeMillis();
            String agentName = agentType.name();

            try {
                // Создание экземпляра агента
                Agent agent = agentFactory.createAgent(agentType);
                agentName = agent.getName(); // Использование фактического имени агента

                log.debug("Выполнение агента: {} для тикера: {}", agentName, ticker);

                // Проверка, может ли агент анализировать этот контекст
                if (!agent.canAnalyze(context)) {
                    String warning = String.format("Агент %s не может анализировать тикер: %s", agentName, ticker);
                    log.warn(warning);
                    report.setError(warning);

                    // Запись неудачного выполнения в метрики
                    long executionTime = System.currentTimeMillis() - agentStartTime;
                    //metrics.recordAgentExecution(agentName, executionTime, false);
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

                    // Запись успешного выполнения в метрики
                    //metrics.recordAgentExecution(agentName, executionTime, true);

                    log.info("Агент {} завершил анализ для тикера: {} за {}мс",
                            agentName, ticker, executionTime);
                } else {
                    String error = String.format("Агент %s вернул null результат для тикера: %s", agentName, ticker);
                    log.error(error);
                    report.setError(error);

                    // Запись неудачного выполнения в метрики
                    //metrics.recordAgentExecution(agentName, executionTime, false);

                    // Это критическая ошибка - мы не должны продолжать оркестрацию
//                    throw new OrchestrationException(ticker, "AGENT_NULL_RESULT",
//                            String.format("Агент %s вернул null результат", agentName));
                    throw new OrchestrationException(ticker);
                }
                return report;

            } catch (AgentAnalysisException aae) {
                long executionTime = System.currentTimeMillis() - agentStartTime;
                String error = String.format("Агент %s не удалось проанализировать: %s", agentName, aae.getMessage());

                log.error(error, aae);
                report.setError(error);

                // Запись неудачного выполнения в метрики
                //metrics.recordAgentExecution(agentName, executionTime, false);

                // Продолжение с следующим агентом, а не отказ всей оркестрации
                log.warn("Продолжение с следующим агентом после {} неудачи", agentName);

            } catch (Exception ex) {
                long executionTime = System.currentTimeMillis() - agentStartTime;
                String error = String.format("Непредвиденная ошибка в агенте %s: %s", agentName, ex.getMessage());

                log.error(error, ex);
                report.setError(error);

                // Запись неудачного выполнения в метрики
                //metrics.recordAgentExecution(agentName, executionTime, false);

                // Для неожиданных ошибок мы можем завершить всю оркестрацию
//                throw new OrchestrationException(ticker, "AGENT_UNEXPECTED_ERROR",
//                        String.format("Непредвиденная ошибка в агенте %s", agentName), ex);
                throw new OrchestrationException(ticker);
            }
        }
//
//        logger.debug("Последовательное выполнение агентов завершено для тикера: {}", ticker);
//
//                 // Проверка, производил ли хоть один агент результаты
//         if (report.getAgentResults().isEmpty()) {
////             throw new OrchestrationException(ticker, "NO_AGENT_RESULTS",
////                     "Ни один агент не произвел успешные результаты");
//             throw new OrchestrationException(ticker);
//         }
//    }
//
//    /**
//     * Получает метрики производительности оркестратора.
//     *
//     * @return текущий экземпляр метрик
//     */
//    public OrchestratorMetrics getMetrics() {
//        return metrics;
//    }
//
//    /**
//     * Логирует сводку текущих метрик.
//     * Полезно для мониторинга и отладки производительности оркестратора.
//     */
//    public void logMetricsSummary() {
//        metrics.logMetricsSummary();
//    }
    }
}