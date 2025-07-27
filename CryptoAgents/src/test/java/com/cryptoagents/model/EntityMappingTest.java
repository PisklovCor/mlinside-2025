//package com.cryptoagents.model;
//
//import com.cryptoagents.model.dto.CryptoCurrency;
//import com.cryptoagents.model.dto.HistoricalData;
//import com.cryptoagents.model.enums.TimePeriod;
//import jakarta.persistence.EntityManager;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Тесты для маппинга сущностей и функциональности JPA.
// * Использует @DataJpaTest для оптимизированного тестирования с in-memory базой данных.
// */
//@DataJpaTest
//@ActiveProfiles("test")
//class EntityMappingTest {
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Test
//    void testAnalysisReportMapping() {
//        // Test basic AnalysisReport entity
//        AnalysisReport report = new AnalysisReport("BTC");
//        report.setAnalysisResult("Comprehensive analysis completed");
//
//        AnalysisReport saved = entityManager.persistAndFlush(report);
//
//        assertNotNull(saved.getId());
//        assertEquals("BTC", saved.getTicker());
//        assertEquals("Comprehensive analysis completed", saved.getAnalysisResult());
//        assertNotNull(saved.getAnalysisStartTime());
//    }
//
//    @Test
//    void testAnalystReportMapping() {
//        // Test AnalystReport entity (inherits from AnalysisResult)
//        AnalystReport analystReport = new AnalystReport("ETH");
//        analystReport.setMarketTrend(MarketTrend.BULLISH);
//        analystReport.setCurrentPrice(new BigDecimal("2500.50"));
//        analystReport.setSupportLevel(new BigDecimal("2400.00"));
//        analystReport.setResistanceLevel(new BigDecimal("2700.00"));
//        analystReport.setSignalStrength(SignalStrength.STRONG);
//        analystReport.setTimeHorizonDays(30);
//        analystReport.setConfidenceScore(0.85);
//
//        AnalystReport saved = entityManager.persistAndFlush(analystReport);
//
//        assertNotNull(saved.getId());
//        assertEquals("ETH", saved.getTicker());
//        assertEquals("ANALYST_AGENT", saved.getAgentName());
//        assertEquals(MarketTrend.BULLISH, saved.getMarketTrend());
//        assertEquals(0, new BigDecimal("2500.50").compareTo(saved.getCurrentPrice()));
//        assertEquals(SignalStrength.STRONG, saved.getSignalStrength());
//        assertEquals(30, saved.getTimeHorizonDays());
//        assertEquals(0.85, saved.getConfidenceScore());
//    }
//
//    @Test
//    void testRiskManagerReportMapping() {
//        // Test RiskManagerReport entity
//        RiskManagerReport riskReport = new RiskManagerReport("BTC");
//        riskReport.setRiskScore(new BigDecimal("7.5"));
//        riskReport.setRiskLevel(RiskManagerReport.RiskLevel.HIGH);
//        riskReport.setVolatilityScore(new BigDecimal("8.2"));
//        riskReport.setValueAtRisk(new BigDecimal("1500.00"));
//        riskReport.setRecommendedPositionSize(new BigDecimal("0.05"));
//        riskReport.setBetaCoefficient(new BigDecimal("1.2"));
//        riskReport.setSharpeRatio(new BigDecimal("0.8"));
//
//        RiskManagerReport saved = entityManager.persistAndFlush(riskReport);
//
//        assertNotNull(saved.getId());
//        assertEquals("BTC", saved.getTicker());
//        assertEquals("RISK_MANAGER_AGENT", saved.getAgentName());
//        assertEquals(0, new BigDecimal("7.5").compareTo(saved.getRiskScore()));
//        assertEquals(RiskManagerReport.RiskLevel.HIGH, saved.getRiskLevel());
//        assertEquals(0, new BigDecimal("8.2").compareTo(saved.getVolatilityScore()));
//        assertEquals(0, new BigDecimal("0.05").compareTo(saved.getRecommendedPositionSize()));
//    }
//
//    @Test
//    void testTraderReportMapping() {
//        // Test TraderReport entity
//        TraderReport traderReport = new TraderReport("ADA");
//        traderReport.setActionRecommendation(TraderReport.TradingAction.BUY);
//        traderReport.setEntryPrice(new BigDecimal("0.45"));
//        traderReport.setStopLoss(new BigDecimal("0.40"));
//        traderReport.setTakeProfit(new BigDecimal("0.55"));
//        traderReport.setPositionSize(new BigDecimal("1000.00"));
//        traderReport.setRiskRewardRatio(new BigDecimal("2.5"));
//        traderReport.setOrderType(TraderReport.OrderType.LIMIT);
//        traderReport.setTimeInForce(TraderReport.TimeInForce.GTC);
//
//        TraderReport saved = entityManager.persistAndFlush(traderReport);
//
//        assertNotNull(saved.getId());
//        assertEquals("ADA", saved.getTicker());
//        assertEquals("TRADER_AGENT", saved.getAgentName());
//        assertEquals(TraderReport.TradingAction.BUY, saved.getActionRecommendation());
//        assertEquals(0, new BigDecimal("0.45").compareTo(saved.getEntryPrice()));
//        assertEquals(0, new BigDecimal("0.40").compareTo(saved.getStopLoss()));
//        assertEquals(0, new BigDecimal("0.55").compareTo(saved.getTakeProfit()));
//        assertEquals(0, new BigDecimal("2.5").compareTo(saved.getRiskRewardRatio()));
//    }
//
//    @Test
//    void testAnalysisResultInheritance() {
//        // Test that all report types properly inherit from AnalysisResult
//        AnalystReport analystReport = new AnalystReport("BTC");
//        RiskManagerReport riskReport = new RiskManagerReport("ETH");
//        TraderReport traderReport = new TraderReport("ADA");
//
//        // All should be persistable as AnalysisResult
//        AnalysisResult savedAnalyst = entityManager.persistAndFlush(analystReport);
//        AnalysisResult savedRisk = entityManager.persistAndFlush(riskReport);
//        AnalysisResult savedTrader = entityManager.persistAndFlush(traderReport);
//
//        assertNotNull(savedAnalyst.getId());
//        assertNotNull(savedRisk.getId());
//        assertNotNull(savedTrader.getId());
//
//        assertEquals("BTC", savedAnalyst.getTicker());
//        assertEquals("ETH", savedRisk.getTicker());
//        assertEquals("ADA", savedTrader.getTicker());
//    }
//
//    @Test
//    void testTimestampGeneration() {
//        // Test that timestamps are automatically generated
//        AnalysisReport report = new AnalysisReport("BTC");
//        report.setAnalysisResult("Test analysis");
//
//        AnalysisReport saved = entityManager.persistAndFlush(report);
//
//        assertNotNull(saved.getAnalysisStartTime());
//        assertNotNull(saved.getAnalysisEndTime());
//        assertTrue(saved.getAnalysisStartTime().isBefore(saved.getAnalysisEndTime()) ||
//                  saved.getAnalysisStartTime().equals(saved.getAnalysisEndTime()));
//    }
//
//    @Test
//    void testExecutionTimeCalculation() {
//        // Test execution time calculation
//        AnalysisReport report = new AnalysisReport("BTC");
//        report.setAnalysisResult("Test analysis");
//        report.setAnalysisStartTime(LocalDateTime.now().minusSeconds(5));
//        report.setAnalysisEndTime(LocalDateTime.now());
//
//        AnalysisReport saved = entityManager.persistAndFlush(report);
//
//        assertNotNull(saved.getExecutionTimeMs());
//        assertTrue(saved.getExecutionTimeMs() >= 5000); // At least 5 seconds
//    }
//}