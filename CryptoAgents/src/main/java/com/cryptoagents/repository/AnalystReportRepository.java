package com.cryptoagents.repository;

import com.cryptoagents.model.AnalystReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AnalystReport entities.
 * 
 * This repository provides specialized query methods for technical analysis data
 * including market trends, technical indicators, and price targets.
 */
@Repository
public interface AnalystReportRepository extends JpaRepository<AnalystReport, Long> {
    
    /**
     * Find analyst reports for a ticker with confidence score above threshold.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @param minimumScore minimum confidence score threshold
     * @return list of analyst reports meeting the confidence criteria
     */
    List<AnalystReport> findByTickerAndConfidenceScoreGreaterThanEqual(String ticker, Double minimumScore);
    
    /**
     * Find analyst reports by market trend.
     *
     * @param marketTrend the market trend to filter by
     * @return list of analyst reports with the specified market trend
     */
    List<AnalystReport> findByMarketTrend(AnalystReport.MarketTrend marketTrend);
    
    /**
     * Find analyst reports by ticker and market trend.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @param marketTrend the market trend to filter by
     * @return list of analyst reports for the ticker with specified trend
     */
    List<AnalystReport> findByTickerAndMarketTrend(String ticker, AnalystReport.MarketTrend marketTrend);
    
    /**
     * Find analyst reports by signal strength.
     *
     * @param signalStrength the signal strength to filter by
     * @return list of analyst reports with the specified signal strength
     */
    List<AnalystReport> findBySignalStrength(AnalystReport.SignalStrength signalStrength);
    
    /**
     * Find the most recent analyst report for a ticker.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @return optional containing the most recent analyst report
     */
    Optional<AnalystReport> findFirstByTickerOrderByAnalysisTimeDesc(String ticker);
    
    /**
     * Find analyst reports with price targets above current price.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @return list of analyst reports where price target > current price
     */
    @Query("SELECT ar FROM AnalystReport ar WHERE ar.ticker = :ticker AND ar.priceTarget > ar.currentPrice")
    List<AnalystReport> findBullishReportsByTicker(@Param("ticker") String ticker);
    
    /**
     * Find analyst reports with price targets below current price.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @return list of analyst reports where price target < current price
     */
    @Query("SELECT ar FROM AnalystReport ar WHERE ar.ticker = :ticker AND ar.priceTarget < ar.currentPrice")
    List<AnalystReport> findBearishReportsByTicker(@Param("ticker") String ticker);
    
    /**
     * Find analyst reports within a specific price range.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @param minPrice minimum current price
     * @param maxPrice maximum current price
     * @return list of analyst reports within the price range
     */
    @Query("SELECT ar FROM AnalystReport ar WHERE ar.ticker = :ticker AND ar.currentPrice BETWEEN :minPrice AND :maxPrice")
    List<AnalystReport> findByTickerAndPriceRange(@Param("ticker") String ticker, 
                                                 @Param("minPrice") BigDecimal minPrice, 
                                                 @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * Find analyst reports with strong signals (STRONG or VERY_STRONG).
     *
     * @param ticker the cryptocurrency ticker symbol
     * @return list of analyst reports with strong signals
     */
    @Query("SELECT ar FROM AnalystReport ar WHERE ar.ticker = :ticker AND ar.signalStrength IN ('STRONG', 'VERY_STRONG')")
    List<AnalystReport> findStrongSignalsByTicker(@Param("ticker") String ticker);
    
    /**
     * Find analyst reports with short-term horizons (less than specified days).
     *
     * @param maxDays maximum time horizon in days
     * @return list of analyst reports with time horizon <= maxDays
     */
    @Query("SELECT ar FROM AnalystReport ar WHERE ar.timeHorizonDays <= :maxDays")
    List<AnalystReport> findShortTermReports(@Param("maxDays") Integer maxDays);
    
    /**
     * Find recent analyst reports for trend analysis.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @param since date since when to look for reports
     * @return list of recent analyst reports for trend analysis
     */
    @Query("SELECT ar FROM AnalystReport ar WHERE ar.ticker = :ticker AND ar.analysisTime >= :since ORDER BY ar.analysisTime DESC")
    List<AnalystReport> findRecentReportsForTrendAnalysis(@Param("ticker") String ticker, 
                                                          @Param("since") LocalDateTime since);
    
    /**
     * Count analyst reports by market trend.
     *
     * @param marketTrend the market trend to count
     * @return number of reports with the specified trend
     */
    long countByMarketTrend(AnalystReport.MarketTrend marketTrend);
    
    /**
     * Find analyst reports with high confidence and strong signals.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @param minConfidence minimum confidence score
     * @return list of high-confidence, strong-signal reports
     */
    @Query("SELECT ar FROM AnalystReport ar WHERE ar.ticker = :ticker AND ar.confidenceScore >= :minConfidence AND ar.signalStrength IN ('STRONG', 'VERY_STRONG')")
    List<AnalystReport> findHighConfidenceStrongSignals(@Param("ticker") String ticker, 
                                                        @Param("minConfidence") Double minConfidence);
} 