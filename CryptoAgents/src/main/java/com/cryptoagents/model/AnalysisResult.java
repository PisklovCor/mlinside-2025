package com.cryptoagents.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Base entity class for analysis results from different agents.
 * 
 * This class serves as the foundation for specific agent result types
 * and contains common fields shared across all analysis results.
 */
@Entity
@Table(name = "analysis_results")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "agent_type", discriminatorType = DiscriminatorType.STRING)
public abstract class AnalysisResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ticker", nullable = false, length = 10)
    private String ticker;
    
    @Column(name = "analysis_time", nullable = false)
    private LocalDateTime analysisTime;
    
    @Column(name = "agent_name", nullable = false, length = 50)
    private String agentName;
    
    @Column(name = "result_summary", columnDefinition = "TEXT")
    private String resultSummary;
    
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AnalysisStatus status;
    
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    // Constructors
    public AnalysisResult() {
        this.analysisTime = LocalDateTime.now();
        this.status = AnalysisStatus.PENDING;
    }
    
    public AnalysisResult(String ticker, String agentName) {
        this();
        this.ticker = ticker;
        this.agentName = agentName;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTicker() {
        return ticker;
    }
    
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
    
    public LocalDateTime getAnalysisTime() {
        return analysisTime;
    }
    
    public void setAnalysisTime(LocalDateTime analysisTime) {
        this.analysisTime = analysisTime;
    }
    
    public String getAgentName() {
        return agentName;
    }
    
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
    
    public String getResultSummary() {
        return resultSummary;
    }
    
    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }
    
    public Double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    public AnalysisStatus getStatus() {
        return status;
    }
    
    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }
    
    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    /**
     * Enumeration for analysis status values.
     */
    public enum AnalysisStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELLED
    }
} 