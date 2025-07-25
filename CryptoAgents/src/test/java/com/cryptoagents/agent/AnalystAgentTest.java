package com.cryptoagents.agent;

import com.cryptoagents.model.AnalystReport;
import com.cryptoagents.model.AnalysisResult;
import com.cryptoagents.model.dto.HistoricalData;
import com.cryptoagents.model.dto.MarketData;
import com.cryptoagents.model.enums.MarketTrend;
import com.cryptoagents.model.enums.SignalStrength;
import com.cryptoagents.model.enums.TimePeriod;
import com.cryptoagents.service.CryptoDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalystAgentTest {

    @Mock
    private CryptoDataService cryptoDataService;

    @InjectMocks
    private AnalystAgent analystAgent;

    private MarketData mockMarketData;
    private HistoricalData mockHistoricalData;

    @BeforeEach
    void setUp() {
        // Setup mock market data
        mockMarketData = new MarketData();
        mockMarketData.setCurrentPrice(new BigDecimal("50000.00"));
        mockMarketData.setVolume24h(new BigDecimal("1000000000.00"));
        mockMarketData.setMarketCap(new BigDecimal("1000000000000.00"));

        // Setup mock historical data with increasing prices (bullish trend)
        List<BigDecimal> prices = Arrays.asList(
            new BigDecimal("45000.00"),
            new BigDecimal("46000.00"),
            new BigDecimal("47000.00"),
            new BigDecimal("48000.00"),
            new BigDecimal("49000.00"),
            new BigDecimal("50000.00")
        );

        mockHistoricalData = new HistoricalData();
        mockHistoricalData.setPrices(prices);
    }

    @Test
    void testAnalyze_SuccessfulAnalysis() {
        // Given
        String ticker = "BTC";
        when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(mockMarketData));
        when(cryptoDataService.getHistoricalData(eq(ticker), eq(TimePeriod.ONE_MONTH)))
            .thenReturn(Optional.of(mockHistoricalData));

        // When
        AnalysisResult result = analystAgent.analyze(ticker, new AnalysisContext());

        // Then
        assertNotNull(result);
        assertTrue(result instanceof AnalystReport);
        AnalystReport report = (AnalystReport) result;
        
        assertEquals(ticker, report.getTicker());
        assertEquals("AnalystAgent", report.getAgentName());
        assertEquals(AnalysisResult.AnalysisStatus.COMPLETED, report.getStatus());
        assertNotNull(report.getAnalysisTime());
        assertNotNull(report.getResultSummary());
        assertTrue(report.getConfidenceScore() > 0);
    }

    @Test
    void testAnalyze_MarketDataUnavailable() {
        // Given
        String ticker = "BTC";
        when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AgentAnalysisException.class, () -> {
            analystAgent.analyze(ticker, new AnalysisContext());
        });
    }

    @Test
    void testAnalyze_HistoricalDataUnavailable() {
        // Given
        String ticker = "BTC";
        when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(mockMarketData));
        when(cryptoDataService.getHistoricalData(eq(ticker), eq(TimePeriod.ONE_MONTH)))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(AgentAnalysisException.class, () -> {
            analystAgent.analyze(ticker, new AnalysisContext());
        });
    }

    @Test
    void testCalculateSMA() {
        // Given
        List<BigDecimal> prices = Arrays.asList(
            new BigDecimal("100.00"),
            new BigDecimal("110.00"),
            new BigDecimal("120.00")
        );

        // When
        BigDecimal sma = analystAgent.calculateSMA(prices, 3);

        // Then
        assertEquals(new BigDecimal("110.00000000"), sma);
    }

    @Test
    void testCalculateSMA_InsufficientData() {
        // Given
        List<BigDecimal> prices = Arrays.asList(
            new BigDecimal("100.00"),
            new BigDecimal("110.00")
        );

        // When
        BigDecimal sma = analystAgent.calculateSMA(prices, 3);

        // Then
        assertEquals(BigDecimal.ZERO, sma);
    }

    @Test
    void testCalculateRSI() {
        // Given - prices with gains and losses
        List<BigDecimal> prices = Arrays.asList(
            new BigDecimal("100.00"), // Base price
            new BigDecimal("110.00"), // +10 gain
            new BigDecimal("105.00"), // -5 loss
            new BigDecimal("115.00"), // +10 gain
            new BigDecimal("110.00"), // -5 loss
            new BigDecimal("120.00")  // +10 gain
        );

        // When
        BigDecimal rsi = analystAgent.calculateRSI(prices, 5);

        // Then - should be above 50 due to more gains than losses
        assertTrue(rsi.compareTo(new BigDecimal("50")) > 0);
        assertTrue(rsi.compareTo(new BigDecimal("100")) <= 0);
    }

    @Test
    void testCalculateRSI_AllGains() {
        // Given - all prices increasing
        List<BigDecimal> prices = Arrays.asList(
            new BigDecimal("100.00"),
            new BigDecimal("110.00"),
            new BigDecimal("120.00"),
            new BigDecimal("130.00"),
            new BigDecimal("140.00"),
            new BigDecimal("150.00")
        );

        // When
        BigDecimal rsi = analystAgent.calculateRSI(prices, 5);

        // Then - should be 100 (all gains, no losses)
        assertEquals(new BigDecimal("100"), rsi);
    }

    @Test
    void testCalculateMACD() {
        // Given
        List<BigDecimal> prices = Arrays.asList(
            new BigDecimal("100.00"),
            new BigDecimal("110.00"),
            new BigDecimal("120.00"),
            new BigDecimal("130.00"),
            new BigDecimal("140.00"),
            new BigDecimal("150.00"),
            new BigDecimal("160.00"),
            new BigDecimal("170.00"),
            new BigDecimal("180.00"),
            new BigDecimal("190.00"),
            new BigDecimal("200.00"),
            new BigDecimal("210.00"),
            new BigDecimal("220.00"),
            new BigDecimal("230.00"),
            new BigDecimal("240.00"),
            new BigDecimal("250.00"),
            new BigDecimal("260.00"),
            new BigDecimal("270.00"),
            new BigDecimal("280.00"),
            new BigDecimal("290.00"),
            new BigDecimal("300.00"),
            new BigDecimal("310.00"),
            new BigDecimal("320.00"),
            new BigDecimal("330.00"),
            new BigDecimal("340.00"),
            new BigDecimal("350.00"),
            new BigDecimal("360.00"),
            new BigDecimal("370.00"),
            new BigDecimal("380.00"),
            new BigDecimal("390.00"),
            new BigDecimal("400.00")
        );

        // When
        BigDecimal macd = analystAgent.calculateMACD(prices);

        // Then - should be positive for increasing trend
        assertTrue(macd.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testAnalyzeMarketTrend_Bullish() {
        // Given - bullish indicators
        BigDecimal currentPrice = new BigDecimal("50000.00");
        BigDecimal smaShort = new BigDecimal("48000.00"); // Price above short SMA
        BigDecimal smaLong = new BigDecimal("47000.00");  // Short SMA above long SMA
        BigDecimal rsi = new BigDecimal("25.00");         // Oversold (bullish signal)

        // When
        MarketTrend trend = analystAgent.analyzeMarketTrend(currentPrice, smaShort, smaLong, rsi);

        // Then
        assertEquals(MarketTrend.BULLISH, trend);
    }

    @Test
    void testAnalyzeMarketTrend_Bearish() {
        // Given - bearish indicators
        BigDecimal currentPrice = new BigDecimal("45000.00");
        BigDecimal smaShort = new BigDecimal("47000.00"); // Price below short SMA
        BigDecimal smaLong = new BigDecimal("48000.00");  // Short SMA below long SMA
        BigDecimal rsi = new BigDecimal("75.00");         // Overbought (bearish signal)

        // When
        MarketTrend trend = analystAgent.analyzeMarketTrend(currentPrice, smaShort, smaLong, rsi);

        // Then
        assertEquals(MarketTrend.BEARISH, trend);
    }

    @Test
    void testDetermineSignalStrength_VeryStrong() {
        // Given - very strong bullish signals
        BigDecimal rsi = new BigDecimal("25.00"); // Oversold
        BigDecimal macd = new BigDecimal("100.00"); // Positive
        MarketTrend trend = MarketTrend.BULLISH;

        // When
        SignalStrength strength = analystAgent.determineSignalStrength(rsi, macd, trend);

        // Then
        assertEquals(SignalStrength.VERY_STRONG, strength);
    }

    @Test
    void testDetermineSignalStrength_VeryWeak() {
        // Given - very weak signals
        BigDecimal rsi = new BigDecimal("50.00"); // Neutral
        BigDecimal macd = new BigDecimal("-100.00"); // Negative
        MarketTrend trend = MarketTrend.BEARISH;

        // When
        SignalStrength strength = analystAgent.determineSignalStrength(rsi, macd, trend);

        // Then
        assertEquals(SignalStrength.VERY_WEAK, strength);
    }

    @Test
    void testCalculatePriceTarget_Bullish() {
        // Given
        BigDecimal currentPrice = new BigDecimal("50000.00");
        MarketTrend trend = MarketTrend.BULLISH;
        BigDecimal rsi = new BigDecimal("60.00");

        // When
        BigDecimal priceTarget = analystAgent.calculatePriceTarget(currentPrice, trend, rsi);

        // Then - should be 15% higher for bullish trend
        assertEquals(new BigDecimal("57500.00"), priceTarget);
    }

    @Test
    void testCalculatePriceTarget_Bearish() {
        // Given
        BigDecimal currentPrice = new BigDecimal("50000.00");
        MarketTrend trend = MarketTrend.BEARISH;
        BigDecimal rsi = new BigDecimal("40.00");

        // When
        BigDecimal priceTarget = analystAgent.calculatePriceTarget(currentPrice, trend, rsi);

        // Then - should be 15% lower for bearish trend
        assertEquals(new BigDecimal("42500.00"), priceTarget);
    }

    @Test
    void testAnalyzeVolume_HighVolume() {
        // Given - high volume relative to market cap
        MarketData marketData = new MarketData();
        marketData.setVolume24h(new BigDecimal("100000000000.00")); // 100B volume
        marketData.setMarketCap(new BigDecimal("1000000000000.00")); // 1T market cap

        // When
        String analysis = analystAgent.analyzeVolume(marketData);

        // Then
        assertTrue(analysis.contains("High volume"));
        assertTrue(analysis.contains("strong liquidity"));
    }

    @Test
    void testAnalyzeVolume_LowVolume() {
        // Given - low volume relative to market cap
        MarketData marketData = new MarketData();
        marketData.setVolume24h(new BigDecimal("1000000000.00")); // 1B volume
        marketData.setMarketCap(new BigDecimal("1000000000000.00")); // 1T market cap

        // When
        String analysis = analystAgent.analyzeVolume(marketData);

        // Then
        assertTrue(analysis.contains("Low volume"));
        assertTrue(analysis.contains("limited liquidity"));
    }

    @Test
    void testCalculateMomentumIndicators_PositiveMomentum() {
        // Given - increasing prices
        List<BigDecimal> prices = Arrays.asList(
            new BigDecimal("100.00"),
            new BigDecimal("105.00"),
            new BigDecimal("110.00"),
            new BigDecimal("115.00"),
            new BigDecimal("120.00"),
            new BigDecimal("125.00"),
            new BigDecimal("130.00"),
            new BigDecimal("135.00"),
            new BigDecimal("140.00"),
            new BigDecimal("145.00"),
            new BigDecimal("150.00") // 50% increase from 100
        );

        // When
        String momentum = analystAgent.calculateMomentumIndicators(prices);

        // Then
        assertTrue(momentum.contains("Strong positive momentum"));
        assertTrue(momentum.contains("50.00%"));
    }

    @Test
    void testCalculateMomentumIndicators_InsufficientData() {
        // Given - insufficient data
        List<BigDecimal> prices = Arrays.asList(
            new BigDecimal("100.00"),
            new BigDecimal("110.00")
        );

        // When
        String momentum = analystAgent.calculateMomentumIndicators(prices);

        // Then
        assertTrue(momentum.contains("Insufficient data"));
    }

    @Test
    void testDetermineTimeHorizon() {
        // Given
        MarketTrend trend = MarketTrend.BULLISH;
        SignalStrength strength = SignalStrength.VERY_STRONG;

        // When
        int timeHorizon = analystAgent.determineTimeHorizon(trend, strength);

        // Then - should be 30 days for very strong bullish signal
        assertEquals(30, timeHorizon);
    }

    @Test
    void testCalculateConfidenceScore() {
        // Given
        HistoricalData historicalData = new HistoricalData();
        historicalData.setPrices(Arrays.asList(
            new BigDecimal("100.00"),
            new BigDecimal("110.00"),
            new BigDecimal("120.00"),
            new BigDecimal("130.00"),
            new BigDecimal("140.00"),
            new BigDecimal("150.00"),
            new BigDecimal("160.00"),
            new BigDecimal("170.00"),
            new BigDecimal("180.00"),
            new BigDecimal("190.00"),
            new BigDecimal("200.00"),
            new BigDecimal("210.00"),
            new BigDecimal("220.00"),
            new BigDecimal("230.00"),
            new BigDecimal("240.00"),
            new BigDecimal("250.00"),
            new BigDecimal("260.00"),
            new BigDecimal("270.00"),
            new BigDecimal("280.00"),
            new BigDecimal("290.00"),
            new BigDecimal("300.00"),
            new BigDecimal("310.00"),
            new BigDecimal("320.00"),
            new BigDecimal("330.00"),
            new BigDecimal("340.00"),
            new BigDecimal("350.00"),
            new BigDecimal("360.00"),
            new BigDecimal("370.00"),
            new BigDecimal("380.00"),
            new BigDecimal("390.00"),
            new BigDecimal("400.00"),
            new BigDecimal("410.00"),
            new BigDecimal("420.00"),
            new BigDecimal("430.00"),
            new BigDecimal("440.00"),
            new BigDecimal("450.00"),
            new BigDecimal("460.00"),
            new BigDecimal("470.00"),
            new BigDecimal("480.00"),
            new BigDecimal("490.00"),
            new BigDecimal("500.00"),
            new BigDecimal("510.00"),
            new BigDecimal("520.00"),
            new BigDecimal("530.00"),
            new BigDecimal("540.00"),
            new BigDecimal("550.00"),
            new BigDecimal("560.00"),
            new BigDecimal("570.00"),
            new BigDecimal("580.00"),
            new BigDecimal("590.00"),
            new BigDecimal("600.00"),
            new BigDecimal("610.00"),
            new BigDecimal("620.00"),
            new BigDecimal("630.00"),
            new BigDecimal("640.00"),
            new BigDecimal("650.00")
        ));

        MarketData marketData = new MarketData();
        marketData.setVolume24h(new BigDecimal("1000000000.00"));

        BigDecimal rsi = new BigDecimal("60.00");
        BigDecimal macd = new BigDecimal("100.00");

        // When
        double confidence = analystAgent.calculateConfidenceScore(historicalData, marketData, rsi, macd);

        // Then - should be high confidence due to good data quality
        assertTrue(confidence > 0.8);
        assertTrue(confidence <= 1.0);
    }

    @Test
    void testGenerateAnalysisSummary() {
        // Given
        String ticker = "BTC";
        MarketTrend trend = MarketTrend.BULLISH;
        SignalStrength strength = SignalStrength.STRONG;
        double confidence = 0.85;
        BigDecimal priceTarget = new BigDecimal("57500.00");

        // When
        String summary = analystAgent.generateAnalysisSummary(ticker, trend, strength, confidence, priceTarget);

        // Then
        assertTrue(summary.contains("BTC"));
        assertTrue(summary.contains("bullish"));
        assertTrue(summary.contains("strong"));
        assertTrue(summary.contains("85.0%"));
        assertTrue(summary.contains("57500.00000000"));
    }
} 