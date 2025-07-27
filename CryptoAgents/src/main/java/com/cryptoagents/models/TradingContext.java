package com.cryptoagents.models;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradingContext {
    private String conversationId;
    private String userId;
    private BigDecimal accountBalance;
    private BigDecimal riskTolerance; // Percentage
    private List<String> watchlist;
    private Map<String, BigDecimal> currentPositions;
    private String tradingStrategy; // CONSERVATIVE, MODERATE, AGGRESSIVE
    private Map<String, Object> marketConditions;
}