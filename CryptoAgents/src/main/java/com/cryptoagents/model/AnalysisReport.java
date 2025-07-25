package com.cryptoagents.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Полный отчет об анализе, сгенерированный AgentOrchestrator.
 * 
 * Этот класс содержит результаты от всех агентов в конвейере анализа:
 * Аналитик → Риск-менеджер → Трейдер, вместе с метаданными выполнения.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisReport {
    
    /**
     * Тикер криптовалюты, который был проанализирован
     */
    private String ticker;
    
    /**
     * Временная метка начала анализа
     */
    @Builder.Default
    private LocalDateTime analysisStartTime = LocalDateTime.now();
    
    /**
     * Временная метка завершения анализа
     */
    private LocalDateTime analysisEndTime;
    
    /**
     * Общее время выполнения в миллисекундах
     */
    private Long executionTimeMs;
    
    /**
     * Список результатов анализа от всех агентов в порядке выполнения
     */
    @Builder.Default
    private List<AnalysisResult> agentResults = new ArrayList<>();
    
    /**
     * Общий статус успеха анализа
     */
    private boolean successful;
    
    /**
     * Сообщения об ошибках, если какой-либо агент не удался
     */
    @Builder.Default
    private List<String> errors = new ArrayList<>();
    
    /**
     * Метрики производительности для каждого агента
     */
    @Builder.Default
    private Map<String, Long> agentExecutionTimes = new HashMap<>();
    
    /**
     * Дополнительные метаданные
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
    
    /**
     * Получить результат анализа от конкретного агента по имени
     * 
     * @param agentName имя агента (например, "ANALYST", "RISK_MANAGER", "TRADER")
     * @return результат анализа или null, если не найден
     */
    public AnalysisResult getResultByAgent(String agentName) {
        return agentResults.stream()
                .filter(result -> agentName.equals(result.getAgentName()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Добавить сообщение об ошибке в отчет
     * 
     * @param error сообщение об ошибке для добавления
     */
    public void addError(String error) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(error);
        this.successful = false;
    }

    /**
     * Добавить время выполнения агента
     * 
     * @param agentName имя агента
     * @param executionTime время выполнения в миллисекундах
     */
    public void addAgentExecutionTime(String agentName, Long executionTime) {
        if (agentExecutionTimes == null) {
            agentExecutionTimes = new HashMap<>();
        }
        agentExecutionTimes.put(agentName, executionTime);
    }

    /**
     * Вычислить и установить общее время выполнения
     */
    public void calculateExecutionTime() {
        if (analysisStartTime != null && analysisEndTime != null) {
            this.executionTimeMs = java.time.Duration.between(analysisStartTime, analysisEndTime).toMillis();
        }
    }

    /**
     * Проверить, есть ли ошибки в анализе
     * 
     * @return true если есть ошибки, false в противном случае
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    /**
     * Получить общее количество агентов, которые выполнились
     * 
     * @return количество результатов агентов
     */
    public int getAgentCount() {
        return agentResults != null ? agentResults.size() : 0;
    }
} 