package com.cryptoagents.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Класс сущности для результатов агента-трейдера.
 * 
 * Содержит торговые рекомендации, размеры позиций и стратегии выполнения,
 * сгенерированные агентом-трейдером.
 */
@Getter
@Setter
@Entity
@Table(name = "trader_reports")
@DiscriminatorValue("TRADER")
public class TraderReport extends AnalysisResult {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action_recommendation", nullable = false)
    private TradingAction actionRecommendation;
    
    @Column(name = "entry_price", precision = 19, scale = 8)
    private BigDecimal entryPrice;
    
    @Column(name = "exit_price", precision = 19, scale = 8)
    private BigDecimal exitPrice;
    
    @Column(name = "stop_loss", precision = 19, scale = 8)
    private BigDecimal stopLoss;
    
    @Column(name = "take_profit", precision = 19, scale = 8)
    private BigDecimal takeProfit;
    
    @Column(name = "position_size", precision = 19, scale = 8)
    private BigDecimal positionSize;
    
    @Column(name = "risk_reward_ratio", precision = 5, scale = 2)
    private BigDecimal riskRewardRatio;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private OrderType orderType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "time_in_force")
    private TimeInForce timeInForce;
    
    @Column(name = "execution_strategy", columnDefinition = "TEXT")
    private String executionStrategy;
    
    @Column(name = "market_timing", columnDefinition = "TEXT")
    private String marketTiming;
    
    @Column(name = "expected_return", precision = 5, scale = 2)
    private BigDecimal expectedReturn;
    
    @Column(name = "holding_period_days")
    private Integer holdingPeriodDays;
    
    @Column(name = "urgency_level")
    private Integer urgencyLevel;
    
    @Column(name = "trading_rationale", columnDefinition = "TEXT")
    private String tradingRationale;
    
    @Column(name = "portfolio_allocation", precision = 5, scale = 2)
    private BigDecimal portfolioAllocation;
    
    @Column(name = "slippage_tolerance", precision = 5, scale = 4)
    private BigDecimal slippageTolerance;
    
    @Column(name = "execution_deadline")
    private LocalDateTime executionDeadline;
    
    @Column(name = "alternative_scenarios", columnDefinition = "TEXT")
    private String alternativeScenarios;
    
    // Конструкторы
    public TraderReport() {
        super();
    }
    
    public TraderReport(String ticker) {
        super(ticker, "TRADER_AGENT");
    }
    
    /**
     * Перечисление для торговых действий.
     */
    public enum TradingAction {
        BUY,
        SELL,
        HOLD,
        BUY_STRONG,
        SELL_STRONG,
        ACCUMULATE,
        DISTRIBUTE,
        WAIT
    }
    
    /**
     * Перечисление для типов ордеров.
     */
    public enum OrderType {
        MARKET,
        LIMIT,
        STOP,
        STOP_LIMIT,
        TRAILING_STOP,
        ICEBERG,
        TWAP,
        VWAP
    }
    
    /**
     * Перечисление для опций времени действия.
     */
    public enum TimeInForce {
        GTC,  // Действителен до отмены
        IOC,  // Немедленно или отмена
        FOK,  // Исполнить или отменить
        DAY,  // Дневной ордер
        GTD   // Действителен до даты
    }
} 