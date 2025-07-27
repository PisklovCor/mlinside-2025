package com.cryptoagents.model;

import com.cryptoagents.model.enums.MarketTrend;
import com.cryptoagents.model.enums.SignalStrength;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Класс сущности для результатов агента-аналитика.
 * 
 * Содержит данные технического анализа, рыночные тренды и торговые сигналы,
 * сгенерированные агентом-аналитиком.
 */
@Getter
@Setter
@Entity
@Table(name = "analyst_reports")
@DiscriminatorValue("ANALYST")
public class AnalystReport extends AnalysisResult {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "market_trend", nullable = false)
    private MarketTrend marketTrend;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "signal_strength", nullable = false)
    private SignalStrength signalStrength;
    
    @Column(name = "support_level", precision = 19, scale = 8)
    private BigDecimal supportLevel;
    
    @Column(name = "resistance_level", precision = 19, scale = 8)
    private BigDecimal resistanceLevel;
    
    @Column(name = "rsi_value", precision = 5, scale = 2)
    private BigDecimal rsiValue;
    
    @Column(name = "macd_signal", precision = 19, scale = 8)
    private BigDecimal macdSignal;
    
    @Column(name = "moving_average_20", precision = 19, scale = 8)
    private BigDecimal movingAverage20;
    
    @Column(name = "moving_average_50", precision = 19, scale = 8)
    private BigDecimal movingAverage50;
    
    @Column(name = "moving_average_200", precision = 19, scale = 8)
    private BigDecimal movingAverage200;
    
    @Column(name = "bollinger_upper", precision = 19, scale = 8)
    private BigDecimal bollingerUpper;
    
    @Column(name = "bollinger_lower", precision = 19, scale = 8)
    private BigDecimal bollingerLower;
    
    @Column(name = "volume_sma", precision = 19, scale = 2)
    private BigDecimal volumeSma;
    
    @Column(name = "price_momentum", precision = 5, scale = 2)
    private BigDecimal priceMomentum;
    
    @Column(name = "volatility_index", precision = 5, scale = 2)
    private BigDecimal volatilityIndex;
    
    @Column(name = "trend_strength", precision = 5, scale = 2)
    private BigDecimal trendStrength;
    
    @Column(name = "breakout_probability", precision = 5, scale = 2)
    private BigDecimal breakoutProbability;
    
    @Column(name = "technical_analysis", columnDefinition = "TEXT")
    private String technicalAnalysis;
    
    @Column(name = "chart_patterns", columnDefinition = "TEXT")
    private String chartPatterns;
    
    @Column(name = "indicator_signals", columnDefinition = "TEXT")
    private String indicatorSignals;
    
    @Column(name = "price_targets", columnDefinition = "TEXT")
    private String priceTargets;
    
    @Column(name = "risk_levels", columnDefinition = "TEXT")
    private String riskLevels;
    
    @Column(name = "market_sentiment", columnDefinition = "TEXT")
    private String marketSentiment;
    
    @Column(name = "volume_analysis", columnDefinition = "TEXT")
    private String volumeAnalysis;
    
    @Column(name = "momentum_analysis", columnDefinition = "TEXT")
    private String momentumAnalysis;
    
    @Column(name = "trend_analysis", columnDefinition = "TEXT")
    private String trendAnalysis;
    
    @Column(name = "support_resistance_analysis", columnDefinition = "TEXT")
    private String supportResistanceAnalysis;
    
    @Column(name = "fibonacci_levels", columnDefinition = "TEXT")
    private String fibonacciLevels;
    
    @Column(name = "elliot_wave_analysis", columnDefinition = "TEXT")
    private String elliotWaveAnalysis;
    
    @Column(name = "divergence_analysis", columnDefinition = "TEXT")
    private String divergenceAnalysis;
    
    @Column(name = "market_structure", columnDefinition = "TEXT")
    private String marketStructure;
    
    @Column(name = "time_analysis", columnDefinition = "TEXT")
    private String timeAnalysis;
    
    @Column(name = "analysis_confidence", precision = 5, scale = 2)
    private BigDecimal analysisConfidence;
    
    @Column(name = "next_analysis_due")
    private LocalDateTime nextAnalysisDue;
    
    @Column(name = "current_price", precision = 19, scale = 8)
    private BigDecimal currentPrice;
    
    @Column(name = "price_target", precision = 19, scale = 8)
    private BigDecimal priceTarget;
    
    @Column(name = "technical_indicators", columnDefinition = "TEXT")
    private String technicalIndicators;
    
    @Column(name = "momentum_indicators", columnDefinition = "TEXT")
    private String momentumIndicators;
    
    @Column(name = "pattern_recognition", columnDefinition = "TEXT")
    private String patternRecognition;
    
    @Column(name = "time_horizon_days")
    private Integer timeHorizonDays;
    
    // Конструкторы
    public AnalystReport() {
        super();
    }
    
    public AnalystReport(String ticker) {
        super(ticker, "ANALYST_AGENT");
    }
} 