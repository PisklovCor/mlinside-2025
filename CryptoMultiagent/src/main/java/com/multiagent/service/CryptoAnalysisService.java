package com.multiagent.service;

import com.multiagent.model.AgentAnalysis;
import com.multiagent.model.CryptoAnalysisResponse;
import com.multiagent.agent.FundamentalAnalysisAgent;
import com.multiagent.agent.SentimentAnalysisAgent;
import com.multiagent.agent.TechnicalAnalysisAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoAnalysisService {

    private final TechnicalAnalysisAgent technicalAgent;

    private final FundamentalAnalysisAgent fundamentalAgent;

    private final SentimentAnalysisAgent sentimentAgent;

    public CryptoAnalysisResponse analyzeCryptocurrency(String cryptocurrency, String timeframe) {
        log.info("Начинаю синхронный анализ криптовалюты: {} с временным интервалом: {}", cryptocurrency, timeframe);
        
        // Получаем анализы от всех агентов синхронно
        List<AgentAnalysis> analyses = Arrays.asList(
                technicalAgent.analyze(cryptocurrency, timeframe),
                fundamentalAgent.analyze(cryptocurrency, timeframe),
                sentimentAgent.analyze(cryptocurrency, timeframe)
        );

        log.debug("Получены анализы от всех агентов для криптовалюты: {}", cryptocurrency);
        analyses.forEach(analysis -> 
            log.debug("Агент: {}, Рекомендация: {}, Уверенность: {}", 
                analysis.getAgentName(), analysis.getRecommendation(), analysis.getConfidence()));

        CryptoAnalysisResponse response = buildResponse(cryptocurrency, analyses);
        log.info("Синхронный анализ завершен для криптовалюты: {}, финальная рекомендация: {}, средняя уверенность: {}", 
                cryptocurrency, response.getFinalRecommendation(), response.getAverageConfidence());
        
        return response;
    }

    @Async
    public CompletableFuture<CryptoAnalysisResponse> analyzeCryptocurrencyAsync(String cryptocurrency, String timeframe) {
        log.info("Начинаю асинхронный анализ криптовалюты: {} с временным интервалом: {}", cryptocurrency, timeframe);
        
        // Выполняем анализы параллельно для лучшей производительности
        CompletableFuture<AgentAnalysis> technicalFuture = CompletableFuture.supplyAsync(() -> {
            log.debug("Запуск технического анализа для криптовалюты: {}", cryptocurrency);
            return technicalAgent.analyze(cryptocurrency, timeframe);
        });

        CompletableFuture<AgentAnalysis> fundamentalFuture = CompletableFuture.supplyAsync(() -> {
            log.debug("Запуск фундаментального анализа для криптовалюты: {}", cryptocurrency);
            return fundamentalAgent.analyze(cryptocurrency, timeframe);
        });

        CompletableFuture<AgentAnalysis> sentimentFuture = CompletableFuture.supplyAsync(() -> {
            log.debug("Запуск анализа настроений для криптовалюты: {}", cryptocurrency);
            return sentimentAgent.analyze(cryptocurrency, timeframe);
        });

        // Ждем завершения всех анализов
        return CompletableFuture.allOf(technicalFuture, fundamentalFuture, sentimentFuture)
                .thenApply(v -> {
                    List<AgentAnalysis> analyses = Arrays.asList(
                            technicalFuture.join(),
                            fundamentalFuture.join(),
                            sentimentFuture.join()
                    );
                    
                    log.debug("Все асинхронные анализы завершены для криптовалюты: {}", cryptocurrency);
                    analyses.forEach(analysis -> 
                        log.debug("Агент: {}, Рекомендация: {}, Уверенность: {}", 
                            analysis.getAgentName(), analysis.getRecommendation(), analysis.getConfidence()));

                    CryptoAnalysisResponse response = buildResponse(cryptocurrency, analyses);
                    log.info("Асинхронный анализ завершен для криптовалюты: {}, финальная рекомендация: {}, средняя уверенность: {}", 
                            cryptocurrency, response.getFinalRecommendation(), response.getAverageConfidence());
                    
                    return response;
                });
    }

    private CryptoAnalysisResponse buildResponse(String cryptocurrency, List<AgentAnalysis> analyses) {
        log.debug("Формирование ответа для криптовалюты: {}", cryptocurrency);
        
        // Вычисляем среднюю уверенность
        double averageConfidence = analyses.stream()
                .mapToDouble(AgentAnalysis::getConfidence)
                .average()
                .orElse(0.0);

        log.debug("Средняя уверенность агентов: {}", averageConfidence);

        // Определяем финальную рекомендацию на основе мнений агентов
        String finalRecommendation = determineFinalRecommendation(analyses);
        log.debug("Финальная рекомендация: {}", finalRecommendation);

        return new CryptoAnalysisResponse(cryptocurrency, analyses, finalRecommendation, averageConfidence);
    }

    private String determineFinalRecommendation(List<AgentAnalysis> analyses) {
        log.debug("Определение финальной рекомендации на основе {} анализов", analyses.size());
        
        // Подсчитываем рекомендации с учетом уверенности
        Map<String, Double> weightedRecommendations = analyses.stream()
                .collect(Collectors.groupingBy(
                        AgentAnalysis::getRecommendation,
                        Collectors.summingDouble(AgentAnalysis::getConfidence)
                ));

        log.debug("Взвешенные рекомендации: {}", weightedRecommendations);

        // Находим рекомендацию с наибольшим весом
        String topRecommendation = weightedRecommendations.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("ДЕРЖАТЬ");

        log.debug("Рекомендация с наибольшим весом: {}", topRecommendation);

        // Дополнительная логика для случаев неопределенности
        double totalConfidence = analyses.stream()
                .mapToDouble(AgentAnalysis::getConfidence)
                .sum();

        log.debug("Общая уверенность агентов: {}", totalConfidence);

        if (totalConfidence < 1.5) { // Если общая уверенность низкая
            log.debug("Низкая общая уверенность ({}), возвращаем 'ДЕРЖАТЬ'", totalConfidence);
            return "ДЕРЖАТЬ";
        }

        return topRecommendation;
    }

    public String getAgentsStatus() {
        log.debug("Запрос статуса агентов");
        String status = String.format(
                "Система мультиагентного анализа активна. Доступные агенты: %s, %s, %s",
                "Технический Аналитик",
                "Фундаментальный Аналитик",
                "Аналитик Настроений"
        );
        log.debug("Статус агентов: {}", status);
        return status;
    }
}

