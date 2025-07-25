package com.cryptoagents.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Model class representing a comprehensive cryptocurrency analysis report.
 * 
 * This class aggregates results from all agents into a single report.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "analysis_reports")
public class AnalysisReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String symbol;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(columnDefinition = "TEXT")
    private String analysisResult;
    
    // Constructors
    public AnalysisReport(String symbol) {
        this.symbol = symbol;
        this.createdAt = LocalDateTime.now();
    }
    
    public AnalysisReport(String symbol, String analysisResult) {
        this.symbol = symbol;
        this.analysisResult = analysisResult;
        this.createdAt = LocalDateTime.now();
    }
    
    // toString method for debugging
    @Override
    public String toString() {
        return "AnalysisReport{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", createdAt=" + createdAt +
                ", analysisResult='" + analysisResult + '\'' +
                '}';
    }
} 