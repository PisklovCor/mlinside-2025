package com.cryptoagents.repository;

import com.cryptoagents.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for persisting analysis results.
 * 
 * This repository handles database operations for the base AnalysisResult entity
 * and provides common query methods for all analysis types.
 */
@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    
    /**
     * Find all analysis results for a specific ticker.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @return list of analysis results for the ticker
     */
    List<AnalysisResult> findByTicker(String ticker);
    
    /**
     * Find analysis results for a ticker within a specific time range.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @param start start time for the search range
     * @param end end time for the search range
     * @return list of analysis results within the time range
     */
    List<AnalysisResult> findByTickerAndAnalysisTimeBetween(String ticker, LocalDateTime start, LocalDateTime end);
    
    /**
     * Find the most recent analysis result for a specific ticker.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @return optional containing the most recent analysis result
     */
    Optional<AnalysisResult> findFirstByTickerOrderByAnalysisTimeDesc(String ticker);
    
    /**
     * Find analysis results by agent name.
     *
     * @param agentName the name of the agent that performed the analysis
     * @return list of analysis results from the specified agent
     */
    List<AnalysisResult> findByAgentName(String agentName);
    
    /**
     * Find analysis results by status.
     *
     * @param status the analysis status to filter by
     * @return list of analysis results with the specified status
     */
    List<AnalysisResult> findByStatus(AnalysisResult.AnalysisStatus status);
    
    /**
     * Find analysis results with confidence score above a threshold.
     *
     * @param minScore minimum confidence score
     * @return list of analysis results with confidence score >= minScore
     */
    @Query("SELECT ar FROM AnalysisResult ar WHERE ar.confidenceScore >= :minScore")
    List<AnalysisResult> findByConfidenceScoreGreaterThanEqual(@Param("minScore") Double minScore);
    
    /**
     * Count analysis results by ticker.
     *
     * @param ticker the cryptocurrency ticker symbol
     * @return number of analysis results for the ticker
     */
    long countByTicker(String ticker);
    
    /**
     * Delete old analysis results before a specified cutoff date.
     * This is useful for data cleanup and maintenance.
     *
     * @param cutoffDate the cutoff date - all results before this date will be deleted
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM AnalysisResult ar WHERE ar.analysisTime < :cutoffDate")
    void deleteByAnalysisTimeBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find analysis results that took longer than expected to process.
     *
     * @param maxProcessingTime maximum acceptable processing time in milliseconds
     * @return list of analysis results that exceeded the processing time threshold
     */
    @Query("SELECT ar FROM AnalysisResult ar WHERE ar.processingTimeMs > :maxProcessingTime")
    List<AnalysisResult> findSlowAnalyses(@Param("maxProcessingTime") Long maxProcessingTime);
} 