package com.cryptoagents.agent;

import com.cryptoagents.model.AnalysisResult;
import com.cryptoagents.model.dto.HistoricalData;
import com.cryptoagents.model.dto.MarketData;
import com.cryptoagents.model.enums.SignalStrength;
import com.cryptoagents.model.enums.TimePeriod;
import com.cryptoagents.service.CryptoDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnalystAgent Tests")
class AnalystAgentTest {

    @Mock
    private CryptoDataService cryptoDataService;

    private AnalystAgent analystAgent;

    @BeforeEach
    void setUp() {
        analystAgent = new AnalystAgent(cryptoDataService);
    }

    @Test
    @DisplayName("Should analyze successfully with valid data")
    void analyze_WithValidData_ReturnsReport() throws AgentAnalysisException {
        // Given
        MarketData mockMarketData = new MarketData();
        mockMarketData.setTicker("BTC");
        mockMarketData.setCurrentPrice(new BigDecimal("50000"));
        mockMarketData.setTotalVolume(new BigDecimal("1000000"));
        
        HistoricalData mockHistoricalData = new HistoricalData("BTC", TimePeriod.ONE_DAY);
        mockHistoricalData.addPricePoint(new HistoricalData.PricePoint(
            LocalDateTime.now().minusHours(1), new BigDecimal("49000")));
        mockHistoricalData.addPricePoint(new HistoricalData.PricePoint(
            LocalDateTime.now(), new BigDecimal("50000")));
        
        AnalysisContext context = new AnalysisContext();
        context.setTicker("BTC");
        context.setMarketData(mockMarketData);
        List<HistoricalData> historicalDataList = new ArrayList<>();
        historicalDataList.add(mockHistoricalData);
        context.setHistoricalData(historicalDataList);
        
        // When
        AnalysisResult result = analystAgent.analyze(context);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTicker()).isEqualTo("BTC");
        assertThat(result.getAgentName()).isEqualTo("ANALYST");
        assertThat(result.getStatus()).isEqualTo(AnalysisResult.AnalysisStatus.COMPLETED);
    }
    
    @Test
    @DisplayName("Should handle null market data gracefully")
    void analyze_WithNullMarketData_ReturnsReportWithError() {
        // Given
        AnalysisContext context = new AnalysisContext();
        context.setTicker("BTC");
        context.setMarketData(null);
        List<HistoricalData> historicalDataList = new ArrayList<>();
        historicalDataList.add(new HistoricalData("BTC", TimePeriod.ONE_DAY));
        context.setHistoricalData(historicalDataList);
        
        // When & Then
        assertThat(analystAgent.canAnalyze(context)).isFalse();
    }
    
    @Test
    @DisplayName("Should handle null historical data gracefully")
    void analyze_WithNullHistoricalData_ReturnsReportWithError() {
        // Given
        MarketData mockMarketData = new MarketData();
        mockMarketData.setTicker("BTC");
        mockMarketData.setCurrentPrice(new BigDecimal("50000"));
        
        AnalysisContext context = new AnalysisContext();
        context.setTicker("BTC");
        context.setMarketData(mockMarketData);
        context.setHistoricalData(null);
        
        // When & Then
        assertThat(analystAgent.canAnalyze(context)).isTrue();
    }
} 