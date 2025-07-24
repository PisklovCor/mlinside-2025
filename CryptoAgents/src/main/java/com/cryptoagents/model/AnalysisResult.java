package com.cryptoagents.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Base entity class for analysis results from different agents.
 * 
 * This class serves as the foundation for specific agent result types
 * and contains common fields shared across all analysis results.
 */
@Data
@NoArgsConstructor
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
    private LocalDateTime analysisTime = LocalDateTime.now();
    
    @Column(name = "agent_name", nullable = false, length = 50)
    private String agentName;
    
    @Column(name = "result_summary", columnDefinition = "TEXT")
    private String resultSummary;
    
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AnalysisStatus status = AnalysisStatus.PENDING;
    
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    // Constructor with basic fields
    public AnalysisResult(String ticker, String agentName) {
        this.ticker = ticker;
        this.agentName = agentName;
        this.analysisTime = LocalDateTime.now();
        this.status = AnalysisStatus.PENDING;
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