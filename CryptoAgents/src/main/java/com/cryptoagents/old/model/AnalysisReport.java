package com.cryptoagents.old.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

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

    private boolean successful;

    private AnalystReport analystReport;

    private RiskManagerReport riskManagerReport;

    private TraderReport traderReport;

    private String error;

} 