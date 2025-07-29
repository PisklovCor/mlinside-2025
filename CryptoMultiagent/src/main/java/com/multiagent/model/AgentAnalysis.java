package com.multiagent.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Анализ от конкретного агента")
public class AgentAnalysis {

    @Schema(description = "Название агента", example = "Technical Analysis Agent")
    private String agentName;
    
    @Schema(description = "Результат анализа от агента", example = "Технический анализ показывает восходящий тренд")
    private String analysis;
    
    @Schema(description = "Рекомендация от агента", example = "ПОКУПАТЬ")
    private String recommendation;
    
    @Schema(description = "Уверенность агента в рекомендации (0.0 - 1.0)", example = "0.85")
    private double confidence;

}
