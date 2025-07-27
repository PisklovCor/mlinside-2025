package com.cryptoagents.old.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Базовый класс сущности для результатов анализа от различных агентов.
 * 
 * Этот класс служит основой для конкретных типов результатов агентов
 * и содержит общие поля, используемые во всех результатах анализа.
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
    
    // Конструктор с базовыми полями
    public AnalysisResult(String ticker, String agentName) {
        this.ticker = ticker;
        this.agentName = agentName;
        this.analysisTime = LocalDateTime.now();
        this.status = AnalysisStatus.PENDING;
    }
    
    /**
     * Перечисление для значений статуса анализа.
     */
    public enum AnalysisStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELLED
    }
} 