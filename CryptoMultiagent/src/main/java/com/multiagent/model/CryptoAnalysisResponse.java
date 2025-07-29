package com.multiagent.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с результатами анализа криптовалюты")
public class CryptoAnalysisResponse {
    @Schema(description = "Название проанализированной криптовалюты", example = "Bitcoin")
    private String cryptocurrency;
    
    @Schema(description = "Список анализов от различных агентов")
    private List<AgentAnalysis> agentAnalyses;
    
    @Schema(description = "Финальная рекомендация на основе всех анализов", example = "ПОКУПАТЬ")
    private String finalRecommendation;
    
    @Schema(description = "Средняя уверенность в рекомендации", example = "0.85")
    private double averageConfidence;

}
