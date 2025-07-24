package com.cryptoagents.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JPA entity mapping and inheritance structure.
 * Validates that all entity classes are properly configured.
 */
@DataJpaTest
@ActiveProfiles("test")
class EntityMappingTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testAnalysisReportMapping() {
        // Test basic AnalysisReport entity
        AnalysisReport report = new AnalysisReport("BTC", "Comprehensive analysis completed");
        
        AnalysisReport saved = entityManager.persistAndFlush(report);
        
        assertNotNull(saved.getId());
        assertEquals("BTC", saved.getSymbol());
        assertEquals("Comprehensive analysis completed", saved.getAnalysisResult());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testAnalystReportMapping() {
        // Test AnalystReport entity (inherits from AnalysisResult)
        AnalystReport analystReport = new AnalystReport("ETH");
        analystReport.setMarketTrend(AnalystReport.MarketTrend.BULLISH);
        analystReport.setCurrentPrice(new BigDecimal("2500.50"));
        analystReport.setSupportLevel(new BigDecimal("2400.00"));
        analystReport.setResistanceLevel(new BigDecimal("2700.00"));
        analystReport.setSignalStrength(AnalystReport.SignalStrength.STRONG);
        analystReport.setTimeHorizonDays(30);
        analystReport.setConfidenceScore(0.85);
        
        AnalystReport saved = entityManager.persistAndFlush(analystReport);
        
        assertNotNull(saved.getId());
        assertEquals("ETH", saved.getTicker());
        assertEquals("ANALYST_AGENT", saved.getAgentName());
        assertEquals(AnalystReport.MarketTrend.BULLISH, saved.getMarketTrend());
        assertEquals(0, new BigDecimal("2500.50").compareTo(saved.getCurrentPrice()));
        assertEquals(AnalystReport.SignalStrength.STRONG, saved.getSignalStrength());
        assertEquals(30, saved.getTimeHorizonDays());
        assertEquals(0.85, saved.getConfidenceScore());
    }

    @Test
    void testRiskManagerReportMapping() {
        // Test RiskManagerReport entity
        RiskManagerReport riskReport = new RiskManagerReport("BTC");
        riskReport.setRiskScore(new BigDecimal("7.5"));
        riskReport.setRiskLevel(RiskManagerReport.RiskLevel.HIGH);
        riskReport.setVolatilityScore(new BigDecimal("8.2"));
        riskReport.setValueAtRisk(new BigDecimal("1500.00"));
        riskReport.setRecommendedPositionSize(new BigDecimal("0.05"));
        riskReport.setBetaCoefficient(new BigDecimal("1.2"));
        riskReport.setSharpeRatio(new BigDecimal("0.8"));
        
        RiskManagerReport saved = entityManager.persistAndFlush(riskReport);
        
        assertNotNull(saved.getId());
        assertEquals("BTC", saved.getTicker());
        assertEquals("RISK_MANAGER_AGENT", saved.getAgentName());
        assertEquals(0, new BigDecimal("7.5").compareTo(saved.getRiskScore()));
        assertEquals(RiskManagerReport.RiskLevel.HIGH, saved.getRiskLevel());
        assertEquals(0, new BigDecimal("8.2").compareTo(saved.getVolatilityScore()));
        assertEquals(0, new BigDecimal("0.05").compareTo(saved.getRecommendedPositionSize()));
    }

    @Test
    void testTraderReportMapping() {
        // Test TraderReport entity
        TraderReport traderReport = new TraderReport("ADA");
        traderReport.setActionRecommendation(TraderReport.TradingAction.BUY);
        traderReport.setEntryPrice(new BigDecimal("0.45"));
        traderReport.setStopLoss(new BigDecimal("0.40"));
        traderReport.setTakeProfit(new BigDecimal("0.55"));
        traderReport.setPositionSize(new BigDecimal("1000.00"));
        traderReport.setRiskRewardRatio(new BigDecimal("2.5"));
        traderReport.setOrderType(TraderReport.OrderType.LIMIT);
        traderReport.setTimeInForce(TraderReport.TimeInForce.GTC);
        traderReport.setHoldingPeriodDays(14);
        traderReport.setUrgencyLevel(3);
        
        TraderReport saved = entityManager.persistAndFlush(traderReport);
        
        assertNotNull(saved.getId());
        assertEquals("ADA", saved.getTicker());
        assertEquals("TRADER_AGENT", saved.getAgentName());
        assertEquals(TraderReport.TradingAction.BUY, saved.getActionRecommendation());
        assertEquals(0, new BigDecimal("0.45").compareTo(saved.getEntryPrice()));
        assertEquals(0, new BigDecimal("0.40").compareTo(saved.getStopLoss()));
        assertEquals(TraderReport.OrderType.LIMIT, saved.getOrderType());
        assertEquals(TraderReport.TimeInForce.GTC, saved.getTimeInForce());
        assertEquals(14, saved.getHoldingPeriodDays());
        assertEquals(3, saved.getUrgencyLevel());
    }

    @Test
    void testAnalysisResultStatusEnum() {
        // Test AnalysisResult status enumeration
        AnalystReport report = new AnalystReport("DOT");
        
        assertEquals(AnalysisResult.AnalysisStatus.PENDING, report.getStatus());
        
        report.setStatus(AnalysisResult.AnalysisStatus.COMPLETED);
        AnalystReport saved = entityManager.persistAndFlush(report);
        
        assertEquals(AnalysisResult.AnalysisStatus.COMPLETED, saved.getStatus());
    }

    @Test
    void testInheritanceStructure() {
        // Test that inheritance works correctly
        AnalystReport analystReport = new AnalystReport("SOL");
        RiskManagerReport riskReport = new RiskManagerReport("SOL");
        TraderReport traderReport = new TraderReport("SOL");
        traderReport.setActionRecommendation(TraderReport.TradingAction.HOLD); // Required field
        
        entityManager.persistAndFlush(analystReport);
        entityManager.persistAndFlush(riskReport);
        entityManager.persistAndFlush(traderReport);
        
        // All should be instances of AnalysisResult
        assertTrue(analystReport instanceof AnalysisResult);
        assertTrue(riskReport instanceof AnalysisResult);
        assertTrue(traderReport instanceof AnalysisResult);
        
        // Each should have their specific type
        assertTrue(analystReport instanceof AnalystReport);
        assertTrue(riskReport instanceof RiskManagerReport);
        assertTrue(traderReport instanceof TraderReport);
    }
} 