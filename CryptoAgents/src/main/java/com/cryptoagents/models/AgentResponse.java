package com.cryptoagents.models;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse {
    private String agentType; // ANALYST, RISK_MANAGER, EXECUTOR
    private String message;
    private Map<String, Object> analysis;
    private Map<String, Object> recommendations;
    private String decision; // BUY, SELL, HOLD, APPROVE, REJECT
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}