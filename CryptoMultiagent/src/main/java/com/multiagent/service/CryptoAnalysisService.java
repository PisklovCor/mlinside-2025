package com.multiagent.service;

import com.multiagent.model.AgentAnalysis;
import com.multiagent.model.CryptoAnalysisResponse;
import com.multiagent.agent.FundamentalAnalysisAgent;
import com.multiagent.agent.SentimentAnalysisAgent;
import com.multiagent.agent.TechnicalAnalysisAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CryptoAnalysisService {

    @Autowired
    private TechnicalAnalysisAgent technicalAgent;

    @Autowired
    private FundamentalAnalysisAgent fundamentalAgent;

    @Autowired
    private SentimentAnalysisAgent sentimentAgent;

    public CryptoAnalysisResponse analyzeCryptocurrency(String cryptocurrency, String timeframe) {
        // Получаем анализы от всех агентов синхронно
        List<AgentAnalysis> analyses = Arrays.asList(
                technicalAgent.analyze(cryptocurrency, timeframe),
                fundamentalAgent.analyze(cryptocurrency, timeframe),
                sentimentAgent.analyze(cryptocurrency, timeframe)
        );

        return buildResponse(cryptocurrency, analyses);
    }

    @Async
    public CompletableFuture<CryptoAnalysisResponse> analyzeCryptocurrencyAsync(String cryptocurrency, String timeframe) {
        // Выполняем анализы параллельно для лучшей производительности
        CompletableFuture<AgentAnalysis> technicalFuture = CompletableFuture.supplyAsync(() ->
                technicalAgent.analyze(cryptocurrency, timeframe));

        CompletableFuture<AgentAnalysis> fundamentalFuture = CompletableFuture.supplyAsync(() ->
                fundamentalAgent.analyze(cryptocurrency, timeframe));

        CompletableFuture<AgentAnalysis> sentimentFuture = CompletableFuture.supplyAsync(() ->
                sentimentAgent.analyze(cryptocurrency, timeframe));

        // Ждем завершения всех анализов
        return CompletableFuture.allOf(technicalFuture, fundamentalFuture, sentimentFuture)
                .thenApply(v -> {
                    List<AgentAnalysis> analyses = Arrays.asList(
                            technicalFuture.join(),
                            fundamentalFuture.join(),
                            sentimentFuture.join()
                    );
                    return buildResponse(cryptocurrency, analyses);
                });
    }

    private CryptoAnalysisResponse buildResponse(String cryptocurrency, List<AgentAnalysis> analyses) {
        // Вычисляем среднюю уверенность
        double averageConfidence = analyses.stream()
                .mapToDouble(AgentAnalysis::getConfidence)
                .average()
                .orElse(0.0);

        // Определяем финальную рекомендацию на основе мнений агентов
        String finalRecommendation = determineFinalRecommendation(analyses);

        return new CryptoAnalysisResponse(cryptocurrency, analyses, finalRecommendation, averageConfidence);
    }

    private String determineFinalRecommendation(List<AgentAnalysis> analyses) {
        // Подсчитываем рекомендации с учетом уверенности
        Map<String, Double> weightedRecommendations = analyses.stream()
                .collect(Collectors.groupingBy(
                        AgentAnalysis::getRecommendation,
                        Collectors.summingDouble(AgentAnalysis::getConfidence)
                ));

        // Находим рекомендацию с наибольшим весом
        String topRecommendation = weightedRecommendations.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("ДЕРЖАТЬ");

        // Дополнительная логика для случаев неопределенности
        double totalConfidence = analyses.stream()
                .mapToDouble(AgentAnalysis::getConfidence)
                .sum();

        if (totalConfidence < 1.5) { // Если общая уверенность низкая
            return "ДЕРЖАТЬ";
        }

        return topRecommendation;
    }

    public String getAgentsStatus() {
        return String.format(
                "Система мультиагентного анализа активна. Доступные агенты: %s, %s, %s",
                "Технический Аналитик",
                "Фундаментальный Аналитик",
                "Аналитик Настроений"
        );
    }
}

