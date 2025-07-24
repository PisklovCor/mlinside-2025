package com.cryptoagents.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Model class representing a comprehensive cryptocurrency analysis report.
 * 
 * This class aggregates results from all agents into a single report.
 */
@Entity
@Table(name = "analysis_reports")
public class AnalysisReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String symbol;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(columnDefinition = "TEXT")
    private String analysisResult;
    
    // Constructors
    public AnalysisReport() {
        this.createdAt = LocalDateTime.now();
    }
    
    public AnalysisReport(String symbol, String analysisResult) {
        this();
        this.symbol = symbol;
        this.analysisResult = analysisResult;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getAnalysisResult() {
        return analysisResult;
    }
    
    public void setAnalysisResult(String analysisResult) {
        this.analysisResult = analysisResult;
    }
} 