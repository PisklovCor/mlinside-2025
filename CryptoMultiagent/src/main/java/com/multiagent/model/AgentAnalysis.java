package com.multiagent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentAnalysis {

    private String agentName;
    private String analysis;
    private String recommendation;
    private double confidence;

}
