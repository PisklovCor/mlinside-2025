package com.cryptoagents.repository;

import com.cryptoagents.model.TraderReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TraderReport entities.
 * 
 * This repository provides specialized query methods for trading recommendations,
 * position management, and trading strategy analysis.
 */
@Repository
public interface TraderReportRepository extends JpaRepository<TraderReport, Long> {
    
    /**
     * Find trader reports by trading action.
     *
     * @param actionRecommendation the trading action to filter by
     * @return list of trader reports with the specified action
     */
    List<TraderReport> findByActionRecommendation(TraderReport.TradingAction actionRecommendation);
    
    /**
     * Find trader reports for a ticker by trading action.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @param actionRecommendation the trading action to filter by
     * @return list of trader reports for the ticker with specified action
     */
    List<TraderReport> findByTickerAndActionRecommendation(String ticker, TraderReport.TradingAction actionRecommendation);
    
    /**
     * Find trader reports by order type.
     *
     * @param orderType the order type to filter by
     * @return list of trader reports with the specified order type
     */
    List<TraderReport> findByOrderType(TraderReport.OrderType orderType);
    
    /**
     * Find trader reports by time in force.
     *
     * @param timeInForce the time in force parameter to filter by
     * @return list of trader reports with the specified time in force
     */
    List<TraderReport> findByTimeInForce(TraderReport.TimeInForce timeInForce);
    
    /**
     * Find the most recent trader report for a ticker.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @return optional containing the most recent trader report
     */
    Optional<TraderReport> findFirstByTickerOrderByAnalysisTimeDesc(String ticker);
    
    /**
     * Find all BUY recommendations.
     *
     * @return list of trader reports with BUY action
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.actionRecommendation = 'BUY'")
    List<TraderReport> findBuyRecommendations();
    
    /**
     * Find all SELL recommendations.
     *
     * @return list of trader reports with SELL action
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.actionRecommendation = 'SELL'")
    List<TraderReport> findSellRecommendations();
    
    /**
     * Find all HOLD recommendations.
     *
     * @return list of trader reports with HOLD action
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.actionRecommendation = 'HOLD'")
    List<TraderReport> findHoldRecommendations();
    
    /**
     * Find trader reports with high risk-reward ratio.
     *
     * @param minRatio minimum acceptable risk-reward ratio
     * @return list of trader reports with ratio >= threshold
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.riskRewardRatio >= :minRatio")
    List<TraderReport> findHighRiskRewardReports(@Param("minRatio") BigDecimal minRatio);
    
    /**
     * Find trader reports with short-term positions.
     *
     * @param maxDays maximum holding period in days
     * @return list of trader reports with holding period <= maxDays
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.holdingPeriodDays <= :maxDays")
    List<TraderReport> findShortTermPositions(@Param("maxDays") Integer maxDays);
    
    /**
     * Find trader reports with long-term positions.
     *
     * @param minDays minimum holding period in days
     * @return list of trader reports with holding period >= minDays
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.holdingPeriodDays >= :minDays")
    List<TraderReport> findLongTermPositions(@Param("minDays") Integer minDays);
    
    /**
     * Find trader reports with high expected returns.
     *
     * @param minReturn minimum expected return percentage
     * @return list of trader reports with expected return >= threshold
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.expectedReturn >= :minReturn")
    List<TraderReport> findHighReturnOpportunities(@Param("minReturn") BigDecimal minReturn);
    
    /**
     * Find trader reports with entry price within range.
     *
     * @param minPrice minimum entry price
     * @param maxPrice maximum entry price
     * @return list of trader reports with entry price in range
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.entryPrice BETWEEN :minPrice AND :maxPrice")
    List<TraderReport> findByEntryPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                           @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * Find trader reports with small position sizes (conservative trades).
     *
     * @param maxPositionSize maximum position size for conservative trading
     * @return list of conservative trader reports
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.positionSize <= :maxPositionSize")
    List<TraderReport> findConservativeTrades(@Param("maxPositionSize") BigDecimal maxPositionSize);
    
    /**
     * Find trader reports with urgent execution requirements.
     *
     * @param urgencyLevel minimum urgency level
     * @return list of trader reports requiring urgent execution
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.urgencyLevel >= :urgencyLevel")
    List<TraderReport> findUrgentTrades(@Param("urgencyLevel") Integer urgencyLevel);
    
    /**
     * Find trader reports with execution deadlines approaching.
     *
     * @param deadlineBefore cutoff datetime for approaching deadlines
     * @return list of trader reports with deadlines before the cutoff
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.executionDeadline <= :deadlineBefore")
    List<TraderReport> findTradesWithApproachingDeadlines(@Param("deadlineBefore") LocalDateTime deadlineBefore);
    
    /**
     * Find trader reports by portfolio allocation range.
     *
     * @param minAllocation minimum portfolio allocation percentage
     * @param maxAllocation maximum portfolio allocation percentage
     * @return list of trader reports with allocation in range
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.portfolioAllocation BETWEEN :minAllocation AND :maxAllocation")
    List<TraderReport> findByPortfolioAllocationRange(@Param("minAllocation") BigDecimal minAllocation, 
                                                     @Param("maxAllocation") BigDecimal maxAllocation);
    
    /**
     * Find recent trading recommendations for trend analysis.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @param since date since when to look for reports
     * @return list of recent trader reports for trend analysis
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.ticker = :ticker AND tr.analysisTime >= :since ORDER BY tr.analysisTime DESC")
    List<TraderReport> findRecentRecommendationsForTrend(@Param("ticker") String ticker, 
                                                        @Param("since") LocalDateTime since);
    
    /**
     * Count trader reports by trading action.
     *
     * @param actionRecommendation the trading action to count
     * @return number of reports with the specified action
     */
    long countByActionRecommendation(TraderReport.TradingAction actionRecommendation);
    
    /**
     * Find trader reports with stop-loss levels close to entry price.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @param maxStopLossDistance maximum distance between entry and stop-loss
     * @return list of trader reports with tight stop-loss levels
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.ticker = :ticker AND ABS(tr.entryPrice - tr.stopLoss) <= :maxStopLossDistance")
    List<TraderReport> findTightStopLossPositions(@Param("ticker") String ticker, 
                                                 @Param("maxStopLossDistance") BigDecimal maxStopLossDistance);
    
    /**
     * Find trader reports with high slippage tolerance.
     *
     * @param minSlippageTolerance minimum slippage tolerance percentage
     * @return list of trader reports with high slippage tolerance
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.slippageTolerance >= :minSlippageTolerance")
    List<TraderReport> findHighSlippageToleranceReports(@Param("minSlippageTolerance") BigDecimal minSlippageTolerance);
    
    /**
     * Find trader reports suitable for automated execution.
     *
     * @return list of trader reports with MARKET or LIMIT orders and appropriate time in force
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.orderType IN ('MARKET', 'LIMIT') AND tr.timeInForce IN ('DAY', 'GTC') AND tr.urgencyLevel <= 5")
    List<TraderReport> findAutomatedExecutionCandidates();
} 