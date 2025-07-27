package com.cryptoagents.old.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Класс сущности для результатов агента риск-менеджера.
 * 
 * Содержит данные оценки рисков, анализ волатильности и метрики рисков,
 * сгенерированные агентом риск-менеджера.
 */
@Getter
@Setter
@Entity
@Table(name = "risk_manager_reports")
@DiscriminatorValue("RISK_MANAGER")
public class RiskManagerReport extends AnalysisResult {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;
    
    @Column(name = "volatility_24h", precision = 5, scale = 2)
    private BigDecimal volatility24h;
    
    @Column(name = "var_95", precision = 19, scale = 8)
    private BigDecimal var95; // Value at Risk 95%
    
    @Column(name = "max_drawdown", precision = 5, scale = 2)
    private BigDecimal maxDrawdown;
    
    @Column(name = "sharpe_ratio", precision = 5, scale = 2)
    private BigDecimal sharpeRatio;
    
    @Column(name = "beta", precision = 5, scale = 2)
    private BigDecimal beta;
    
    @Column(name = "correlation_btc", precision = 5, scale = 2)
    private BigDecimal correlationBtc;
    
    @Column(name = "liquidity_score", precision = 5, scale = 2)
    private BigDecimal liquidityScore;
    
    @Column(name = "market_cap_risk", precision = 5, scale = 2)
    private BigDecimal marketCapRisk;
    
    @Column(name = "concentration_risk", precision = 5, scale = 2)
    private BigDecimal concentrationRisk;
    
    @Column(name = "regulatory_risk", precision = 5, scale = 2)
    private BigDecimal regulatoryRisk;
    
    @Column(name = "technical_risk", precision = 5, scale = 2)
    private BigDecimal technicalRisk;
    
    @Column(name = "fundamental_risk", precision = 5, scale = 2)
    private BigDecimal fundamentalRisk;
    
    @Column(name = "overall_risk_score", precision = 5, scale = 2)
    private BigDecimal overallRiskScore;
    
    @Column(name = "risk_assessment", columnDefinition = "TEXT")
    private String riskAssessment;
    
    @Column(name = "risk_mitigation", columnDefinition = "TEXT")
    private String riskMitigation;
    
    @Column(name = "stress_test_results", columnDefinition = "TEXT")
    private String stressTestResults;
    
    @Column(name = "scenario_analysis", columnDefinition = "TEXT")
    private String scenarioAnalysis;
    
    @Column(name = "position_limit", precision = 19, scale = 8)
    private BigDecimal positionLimit;
    
    @Column(name = "stop_loss_recommendation", precision = 19, scale = 8)
    private BigDecimal stopLossRecommendation;
    
    @Column(name = "risk_adjusted_return", precision = 5, scale = 2)
    private BigDecimal riskAdjustedReturn;
    
    @Column(name = "risk_free_rate", precision = 5, scale = 2)
    private BigDecimal riskFreeRate;
    
    @Column(name = "market_risk_premium", precision = 5, scale = 2)
    private BigDecimal marketRiskPremium;
    
    @Column(name = "risk_decomposition", columnDefinition = "TEXT")
    private String riskDecomposition;
    
    @Column(name = "confidence_interval", columnDefinition = "TEXT")
    private String confidenceInterval;
    
    @Column(name = "risk_metrics_date")
    private LocalDateTime riskMetricsDate;
    
    @Column(name = "risk_score", precision = 5, scale = 2)
    private BigDecimal riskScore;
    
    @Column(name = "volatility_score", precision = 5, scale = 2)
    private BigDecimal volatilityScore;
    
    @Column(name = "liquidity_risk", precision = 5, scale = 2)
    private BigDecimal liquidityRisk;
    
    @Column(name = "recommended_position_size", precision = 19, scale = 8)
    private BigDecimal recommendedPositionSize;
    
    @Column(name = "stop_loss_level", precision = 19, scale = 8)
    private BigDecimal stopLossLevel;
    
    // Конструкторы
    public RiskManagerReport() {
        super();
    }
    
    public RiskManagerReport(String ticker) {
        super(ticker, "RISK_MANAGER_AGENT");
    }
    
    /**
     * Перечисление для уровней риска.
     */
    public enum RiskLevel {
        VERY_LOW,
        LOW,
        MODERATE,
        HIGH,
        VERY_HIGH,
        EXTREME
    }
} 