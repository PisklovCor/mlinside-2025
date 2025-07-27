package com.cryptoagents.agents;

import com.cryptoagents.models.AgentResponse;
import com.cryptoagents.models.TradingContext;
import com.cryptoagents.tools.MarketDataTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AnalystAgent {

    private final ChatClient chatClient;
    private final MarketDataTools marketDataTools;

    @Value("${trading.agents.analyst.system-prompt}")
    private String systemPrompt;

    public AnalystAgent(ChatClient chatClient, MarketDataTools marketDataTools) {
        this.chatClient = chatClient;
        this.marketDataTools = marketDataTools;
    }

    public AgentResponse analyzeMarket(String symbol, TradingContext context) {
        log.info("Analyst agent analyzing market for symbol: {}", symbol);

        String prompt = String.format("""
            Analyze the market conditions for %s.
            Account Balance: $%s
            Risk Tolerance: %s%%
            Trading Strategy: %s

            Please provide:
            1. Technical analysis
            2. Market trend assessment
            3. Entry/exit recommendations
            4. Risk/reward analysis

            Use the available tools to gather market data and technical indicators.
            """, symbol, context.getAccountBalance(),
            context.getRiskTolerance(), context.getTradingStrategy());

        // Get market data first
        Map<String, Object> currentPrice = marketDataTools.getCurrentPrice(symbol);
        Map<String, Object> marketTrend = marketDataTools.getMarketTrend(symbol, "1D");

        String fullPrompt = systemPrompt + "\n\nCurrent Market Data:\n" +
                "Price: $" + currentPrice.get("price") + "\n" +
                "Trend: " + marketTrend.get("trend") + "\n\n" + prompt;

        String analysis = chatClient.prompt()
                .user(fullPrompt)
                .call()
                .content();

        return AgentResponse.builder()
                .agentType("ANALYST")
                .message(analysis)
                .timestamp(LocalDateTime.now())
                .analysis(extractAnalysisData(analysis))
                .recommendations(extractRecommendations(analysis))
                .build();
    }

    public AgentResponse evaluateOpportunity(String query, TradingContext context) {
        log.info("Analyst agent evaluating opportunity: {}", query);

        String fullPrompt = systemPrompt + "\n\n" + query;
        String analysis = chatClient.prompt()
                .user(fullPrompt)
                .call()
                .content();

        return AgentResponse.builder()
                .agentType("ANALYST")
                .message(analysis)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private Map<String, Object> extractAnalysisData(String analysis) {
        // In a real implementation, you might parse structured data from the response
        Map<String, Object> data = new HashMap<>();
        data.put("raw_analysis", analysis);
        return data;
    }

    private Map<String, Object> extractRecommendations(String analysis) {
        // In a real implementation, you might extract specific recommendations
        Map<String, Object> recommendations = new HashMap<>();

        // Simple pattern matching for demonstration
        if (analysis.toLowerCase().contains("buy")) {
            recommendations.put("action", "BUY");
        } else if (analysis.toLowerCase().contains("sell")) {
            recommendations.put("action", "SELL");
        } else {
            recommendations.put("action", "HOLD");
        }

        return recommendations;
    }
}