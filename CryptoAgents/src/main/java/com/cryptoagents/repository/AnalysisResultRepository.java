package com.cryptoagents.repository;

import com.cryptoagents.model.AnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for persisting analysis results.
 * 
 * This repository will handle database operations for analysis data.
 */
@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisReport, Long> {
    
    // TODO: Custom query methods will be defined in later tasks
    
} 