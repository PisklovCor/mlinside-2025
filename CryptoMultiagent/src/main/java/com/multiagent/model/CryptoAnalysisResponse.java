package com.multiagent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoAnalysisResponse {
    private String cryptocurrency;
    private List<AgentAnalysis> agentAnalyses;
    private String finalRecommendation;
    private double averageConfidence;

}
