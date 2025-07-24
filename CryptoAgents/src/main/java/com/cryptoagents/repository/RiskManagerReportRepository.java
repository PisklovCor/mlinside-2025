package com.cryptoagents.repository;

import com.cryptoagents.model.RiskManagerReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RiskManagerReport entities.
 * 
 * This repository provides specialized query methods for risk analysis data
 * including risk scores, volatility metrics, and risk management recommendations.
 */
@Repository
public interface RiskManagerReportRepository extends JpaRepository<RiskManagerReport, Long> {
    
    /**
     * Find risk reports by risk level.
     *
     * @param riskLevel the risk level to filter by
     * @return list of risk reports with the specified risk level
     */
    List<RiskManagerReport> findByRiskLevel(RiskManagerReport.RiskLevel riskLevel);
    
    /**
     * Find risk reports for a ticker by risk level.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @param riskLevel the risk level to filter by
     * @return list of risk reports for the ticker with specified risk level
     */
    List<RiskManagerReport> findByTickerAndRiskLevel(String ticker, RiskManagerReport.RiskLevel riskLevel);
    
    /**
     * Find risk reports with risk score above threshold.
     *
     * @param minRiskScore minimum risk score threshold
     * @return list of risk reports with risk score >= threshold
     */
    List<RiskManagerReport> findByRiskScoreGreaterThanEqual(Double minRiskScore);
    
    /**
     * Find risk reports with volatility score above threshold.
     *
     * @param minVolatilityScore minimum volatility score threshold
     * @return list of risk reports with volatility score >= threshold
     */
    List<RiskManagerReport> findByVolatilityScoreGreaterThanEqual(Double minVolatilityScore);
    
    /**
     * Find the most recent risk report for a ticker.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @return optional containing the most recent risk report
     */
    Optional<RiskManagerReport> findFirstByTickerOrderByAnalysisTimeDesc(String ticker);
    
    /**
     * Find low-risk investment opportunities.
     *
     * @return list of risk reports with LOW or VERY_LOW risk levels
     */
    @Query("SELECT rmr FROM RiskManagerReport rmr WHERE rmr.riskLevel IN ('VERY_LOW', 'LOW')")
    List<RiskManagerReport> findLowRiskInvestments();
    
    /**
     * Find high-risk investments requiring attention.
     *
     * @return list of risk reports with HIGH, VERY_HIGH, or EXTREME risk levels
     */
    @Query("SELECT rmr FROM RiskManagerReport rmr WHERE rmr.riskLevel IN ('HIGH', 'VERY_HIGH', 'EXTREME')")
    List<RiskManagerReport> findHighRiskInvestments();
    
    /**
     * Find risk reports with Value at Risk above threshold.
     *
     * @param maxVaR maximum acceptable Value at Risk
     * @return list of risk reports with VaR > threshold
     */
    @Query("SELECT rmr FROM RiskManagerReport rmr WHERE rmr.valueAtRisk > :maxVaR")
    List<RiskManagerReport> findHighVaRReports(@Param("maxVaR") BigDecimal maxVaR);
    
    /**
     * Find risk reports with maximum drawdown above threshold.
     *
     * @param maxDrawdown maximum acceptable drawdown percentage
     * @return list of risk reports with drawdown > threshold
     */
    @Query("SELECT rmr FROM RiskManagerReport rmr WHERE rmr.maxDrawdown > :maxDrawdown")
    List<RiskManagerReport> findHighDrawdownReports(@Param("maxDrawdown") BigDecimal maxDrawdown);
    
    /**
     * Find risk reports with recommended position size within range.
     *
     * @param minSize minimum position size
     * @param maxSize maximum position size
     * @return list of risk reports with position size in range
     */
    @Query("SELECT rmr FROM RiskManagerReport rmr WHERE rmr.recommendedPositionSize BETWEEN :minSize AND :maxSize")
    List<RiskManagerReport> findByPositionSizeRange(@Param("minSize") BigDecimal minSize, 
                                                   @Param("maxSize") BigDecimal maxSize);
    
    /**
     * Find risk reports with high Sharpe ratio (good risk-adjusted returns).
     *
     * @param minSharpeRatio minimum Sharpe ratio threshold
     * @return list of risk reports with Sharpe ratio >= threshold
     */
    @Query("SELECT rmr FROM RiskManagerReport rmr WHERE rmr.sharpeRatio >= :minSharpeRatio")
    List<RiskManagerReport> findHighSharpeRatioReports(@Param("minSharpeRatio") BigDecimal minSharpeRatio);
    
    /**
     * Find risk reports for portfolio diversification analysis.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @param since date since when to look for reports
     * @return list of recent risk reports for diversification analysis
     */
    @Query("SELECT rmr FROM RiskManagerReport rmr WHERE rmr.ticker = :ticker AND rmr.analysisTime >= :since ORDER BY rmr.analysisTime DESC")
    List<RiskManagerReport> findRecentReportsForDiversification(@Param("ticker") String ticker, 
                                                               @Param("since") LocalDateTime since);
    
    /**
     * Find risk reports with beta coefficient within range.
     *
     * @param minBeta minimum beta coefficient
     * @param maxBeta maximum beta coefficient
     * @return list of risk reports with beta in specified range
     */
    @Query("SELECT rmr FROM RiskManagerReport rmr WHERE rmr.betaCoefficient BETWEEN :minBeta AND :maxBeta")
    List<RiskManagerReport> findByBetaRange(@Param("minBeta") BigDecimal minBeta, 
                                          @Param("maxBeta") BigDecimal maxBeta);
    
    /**
     * Count risk reports by risk level.
     *
     * @param riskLevel the risk level to count
     * @return number of reports with the specified risk level
     */
    long countByRiskLevel(RiskManagerReport.RiskLevel riskLevel);
    
    /**
     * Find risk reports suitable for conservative investors.
     *
     * @param maxRiskScore maximum acceptable risk score
     * @param maxVolatility maximum acceptable volatility
     * @return list of conservative investment options
     */
    @Query("SELECT rmr FROM RiskManagerReport rmr WHERE rmr.riskScore <= :maxRiskScore AND rmr.volatilityScore <= :maxVolatility AND rmr.riskLevel IN ('VERY_LOW', 'LOW', 'MODERATE')")
    List<RiskManagerReport> findConservativeInvestments(@Param("maxRiskScore") Double maxRiskScore, 
                                                       @Param("maxVolatility") Double maxVolatility);
    
    /**
     * Find risk reports with tight stop-loss requirements.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @param maxStopLossDistance maximum distance from current price to stop-loss
     * @return list of risk reports with tight stop-loss levels
     */
    @Query("SELECT rmr FROM RiskManagerReport rmr WHERE rmr.ticker = :ticker AND ABS(rmr.stopLossLevel - (SELECT ar.currentPrice FROM AnalystReport ar WHERE ar.ticker = rmr.ticker ORDER BY ar.analysisTime DESC LIMIT 1)) <= :maxStopLossDistance")
    List<RiskManagerReport> findTightStopLossReports(@Param("ticker") String ticker, 
                                                    @Param("maxStopLossDistance") BigDecimal maxStopLossDistance);
} 