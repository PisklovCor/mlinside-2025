package com.cryptoagents.service;

import com.cryptoagents.agent.*;
import com.cryptoagents.model.AnalysisReport;
import com.cryptoagents.model.AnalysisResult;
import com.cryptoagents.model.dto.MarketData;
import com.cryptoagents.model.dto.HistoricalData;
import com.cryptoagents.model.enums.TimePeriod;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Service that orchestrates the execution of cryptocurrency analysis agents.
 * 
 * This component coordinates the sequential execution of agents in the correct order:
 * 1. Analyst - Performs technical analysis
 * 2. Risk Manager - Assesses risks based on analyst findings
 * 3. Trader - Makes trading recommendations based on previous results
 * 
 * Supports both sequential single-token analysis and parallel multi-token analysis.
 */
@Service
@RequiredArgsConstructor
public class AgentOrchestrator {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentOrchestrator.class);
    
    // Dependencies injected via constructor
    private final AgentFactory agentFactory;
    private final CryptoDataService cryptoDataService;
    
    // Execution order for agents
    private static final List<Agent.AgentType> AGENT_EXECUTION_ORDER = Arrays.asList(
            Agent.AgentType.ANALYST,
            Agent.AgentType.RISK_MANAGER,
            Agent.AgentType.TRADER
    );
    
    // Thread pool for parallel execution
    private final Executor executorService = Executors.newCachedThreadPool();
    
    // Metrics for monitoring orchestrator performance
    private final OrchestratorMetrics metrics = new OrchestratorMetrics();
    
    /**
     * Analyze a single cryptocurrency ticker using all agents in sequence.
     * 
     * This is the main entry point for single-token analysis.
     * Agents are executed in the predefined order with results passed between them.
     * 
     * @param ticker the cryptocurrency ticker to analyze (e.g., "BTC", "ETH")
     * @return complete analysis report containing results from all agents
     * @throws IllegalArgumentException if ticker is null or empty
     */
    public AnalysisReport analyze(String ticker) throws OrchestrationException {
        // Set up logging context for this operation
        String operationId = java.util.UUID.randomUUID().toString().substring(0, 8);
        MDC.put("operationId", operationId);
        MDC.put("ticker", ticker);
        
        logger.info("Starting analysis for ticker: {} [operationId: {}]", ticker, operationId);
        metrics.recordAnalysisStart(ticker);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Validation
            if (!StringUtils.hasText(ticker)) {
                throw new OrchestrationException(ticker, "VALIDATION", "Ticker cannot be null or empty");
            }
            
            // Initialize report
            AnalysisReport report = AnalysisReport.builder()
                    .ticker(ticker.toUpperCase())
                    .analysisStartTime(LocalDateTime.now())
                    .successful(true)
                    .build();
            
            // Check if ticker is supported
            if (!cryptoDataService.isTickerSupported(ticker)) {
                String error = "Ticker not supported: " + ticker;
                logger.warn(error);
                report.addError(error);
                metrics.recordAnalysisFailure(ticker, error);
                return report;
            }
            
            // Prepare analysis context with market data
            AnalysisContext context = prepareAnalysisContext(ticker);
            if (context == null) {
                String error = "Failed to retrieve market data for ticker: " + ticker;
                logger.error(error);
                report.addError(error);
                metrics.recordAnalysisFailure(ticker, error);
                throw new OrchestrationException(ticker, "DATA_RETRIEVAL", operationId, error, null);
            }
            
            // Execute agents in sequence
            executeAgentsSequentially(context, report);
            
            // Record successful completion
            long totalTime = System.currentTimeMillis() - startTime;
            metrics.recordAnalysisSuccess(ticker, totalTime);
            
            // Finalize report
            report.setAnalysisEndTime(LocalDateTime.now());
            report.calculateExecutionTime();
            
            logger.info("Analysis completed for ticker: {} in {}ms. Success: {} [operationId: {}]", 
                    ticker, report.getExecutionTimeMs(), report.isSuccessful(), operationId);
            
            return report;
            
        } catch (OrchestrationException oe) {
            long executionTime = System.currentTimeMillis() - startTime;
            metrics.recordAnalysisFailure(ticker, oe.getMessage());
            logger.error("Orchestration failed for ticker: {} after {}ms [operationId: {}]", ticker, executionTime, operationId, oe);
            throw oe;
        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            String error = "Unexpected error during analysis: " + ex.getMessage();
            metrics.recordAnalysisFailure(ticker, error);
            logger.error("Unexpected error during analysis for ticker: {} after {}ms [operationId: {}]", ticker, executionTime, operationId, ex);
            throw new OrchestrationException(ticker, "UNEXPECTED_ERROR", operationId, error, ex);
        } finally {
            // Clean up MDC
            MDC.clear();
        }
    }
    
    /**
     * Analyze multiple cryptocurrency tickers in parallel.
     * 
     * Each ticker is analyzed independently using the full agent pipeline.
     * This method optimizes throughput by executing multiple analyses concurrently.
     * 
     * @param tickers collection of cryptocurrency tickers to analyze
     * @return map of ticker to analysis report
     * @throws IllegalArgumentException if tickers is null or empty
     */
    public Map<String, AnalysisReport> analyzeMultiple(Collection<String> tickers) {
        logger.info("Starting parallel analysis for {} tickers", tickers != null ? tickers.size() : 0);
        
        if (tickers == null || tickers.isEmpty()) {
            throw new IllegalArgumentException("Tickers collection cannot be null or empty");
        }
        
        // Create futures for parallel execution
        Map<String, CompletableFuture<AnalysisReport>> futures = tickers.stream()
                .collect(Collectors.toMap(
                        ticker -> ticker,
                        ticker -> CompletableFuture.supplyAsync(() -> {
                            try {
                                return analyze(ticker);
                            } catch (OrchestrationException e) {
                                logger.error("Orchestration failed for ticker: {} in parallel execution", ticker, e);
                                AnalysisReport errorReport = AnalysisReport.builder()
                                        .ticker(ticker)
                                        .analysisStartTime(LocalDateTime.now())
                                        .analysisEndTime(LocalDateTime.now())
                                        .successful(false)
                                        .build();
                                errorReport.addError("Orchestration failed: " + e.getMessage());
                                return errorReport;
                            }
                        }, executorService)
                ));
        
        // Wait for all analyses to complete and collect results
        Map<String, AnalysisReport> results = new HashMap<>();
        futures.forEach((ticker, future) -> {
            try {
                results.put(ticker, future.get());
            } catch (Exception e) {
                logger.error("Failed to complete analysis for ticker: {}", ticker, e);
                AnalysisReport errorReport = AnalysisReport.builder()
                        .ticker(ticker)
                        .analysisStartTime(LocalDateTime.now())
                        .analysisEndTime(LocalDateTime.now())
                        .successful(false)
                        .build();
                errorReport.addError("Parallel execution failed: " + e.getMessage());
                results.put(ticker, errorReport);
            }
        });
        
        logger.info("Parallel analysis completed for {} tickers", results.size());
        return results;
    }
    
    /**
     * Get the list of available agents in execution order.
     * 
     * @return list of available agents sorted by priority/execution order
     */
    public List<Agent> getAvailableAgents() {
        return AGENT_EXECUTION_ORDER.stream()
                .map(agentFactory::createAgent)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if the orchestrator is ready to perform analysis.
     * 
     * @return true if all required services are available
     */
    public boolean isReady() {
        try {
            // Check if data service is available
            boolean dataServiceReady = cryptoDataService.isServiceAvailable();
            
            // Check if agents can be created
            boolean agentsReady = AGENT_EXECUTION_ORDER.stream()
                    .allMatch(agentType -> {
                        try {
                            Agent agent = agentFactory.createAgent(agentType);
                            return agent != null;
                        } catch (Exception e) {
                            logger.warn("Failed to create agent: {}", agentType, e);
                            return false;
                        }
                    });
            
            boolean ready = dataServiceReady && agentsReady;
            logger.debug("Orchestrator ready status: dataService={}, agents={}, overall={}", 
                    dataServiceReady, agentsReady, ready);
            
            return ready;
        } catch (Exception e) {
            logger.error("Error checking orchestrator readiness", e);
            return false;
        }
    }
    
    /**
     * Prepare analysis context with required market data.
     * 
     * @param ticker the cryptocurrency ticker
     * @return prepared analysis context or null if data cannot be retrieved
     */
    private AnalysisContext prepareAnalysisContext(String ticker) {
        logger.debug("Preparing analysis context for ticker: {}", ticker);
        
        try {
            AnalysisContext context = new AnalysisContext();
            context.setTicker(ticker.toUpperCase());
            
            // Get current market data
            Optional<MarketData> marketDataOpt = cryptoDataService.getMarketData(ticker);
            if (marketDataOpt.isPresent()) {
                context.setMarketData(marketDataOpt.get());
                logger.debug("Market data retrieved for ticker: {}", ticker);
            } else {
                logger.warn("No market data available for ticker: {}", ticker);
                return null;
            }
            
            // Get historical data (default to 30 days)
            Optional<HistoricalData> historicalDataOpt = cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH);
            if (historicalDataOpt.isPresent()) {
                context.setHistoricalData(Arrays.asList(historicalDataOpt.get()));
                logger.debug("Historical data retrieved for ticker: {}", ticker);
            } else {
                logger.warn("No historical data available for ticker: {}", ticker);
                // Historical data is optional, so continue without it
            }
            
            context.setStartTime(System.currentTimeMillis());
            return context;
            
        } catch (Exception e) {
            logger.error("Failed to prepare analysis context for ticker: {}", ticker, e);
            return null;
        }
    }
    
    /**
     * Execute all agents in the predefined sequence.
     * 
     * @param context the analysis context with market data
     * @param report the report to populate with results
     */
    private void executeAgentsSequentially(AnalysisContext context, AnalysisReport report) throws OrchestrationException {
        String ticker = context.getTicker();
        logger.debug("Executing agents sequentially for ticker: {}", ticker);
        
        for (Agent.AgentType agentType : AGENT_EXECUTION_ORDER) {
            long agentStartTime = System.currentTimeMillis();
            String agentName = agentType.name();
            
            try {
                // Create agent instance
                Agent agent = agentFactory.createAgent(agentType);
                agentName = agent.getName(); // Use actual agent name
                
                logger.debug("Executing agent: {} for ticker: {}", agentName, ticker);
                
                // Check if agent can analyze this context
                if (!agent.canAnalyze(context)) {
                    String warning = String.format("Agent %s cannot analyze ticker: %s", agentName, ticker);
                    logger.warn(warning);
                    report.addError(warning);
                    
                    // Record failed execution in metrics
                    long executionTime = System.currentTimeMillis() - agentStartTime;
                    metrics.recordAgentExecution(agentName, executionTime, false);
                    continue;
                }
                
                // Execute agent analysis
                AnalysisResult result = agent.analyze(context);
                long executionTime = System.currentTimeMillis() - agentStartTime;
                
                if (result != null) {
                    // Add result to report
                    report.getAgentResults().add(result);
                    
                    // Add result to context for next agent
                    context.addAgentResult(agentName, result);
                    
                    // Record execution time
                    report.addAgentExecutionTime(agentName, executionTime);
                    
                    // Record successful execution in metrics
                    metrics.recordAgentExecution(agentName, executionTime, true);
                    
                    logger.info("Agent {} completed analysis for ticker: {} in {}ms", 
                            agentName, ticker, executionTime);
                } else {
                    String error = String.format("Agent %s returned null result for ticker: %s", agentName, ticker);
                    logger.error(error);
                    report.addError(error);
                    
                    // Record failed execution in metrics
                    metrics.recordAgentExecution(agentName, executionTime, false);
                    
                    // This is a critical error - we should not continue
                    throw new OrchestrationException(ticker, "AGENT_NULL_RESULT", 
                            String.format("Agent %s returned null result", agentName));
                }
                
            } catch (AgentAnalysisException aae) {
                long executionTime = System.currentTimeMillis() - agentStartTime;
                String error = String.format("Agent %s failed analysis: %s", agentName, aae.getMessage());
                
                logger.error(error, aae);
                report.addError(error);
                report.addAgentExecutionTime(agentName, executionTime);
                
                // Record failed execution in metrics
                metrics.recordAgentExecution(agentName, executionTime, false);
                
                // Continue with next agent rather than failing entire orchestration
                logger.warn("Continuing with next agent after {} failure", agentName);
                
            } catch (Exception ex) {
                long executionTime = System.currentTimeMillis() - agentStartTime;
                String error = String.format("Unexpected error in agent %s: %s", agentName, ex.getMessage());
                
                logger.error(error, ex);
                report.addError(error);
                report.addAgentExecutionTime(agentName, executionTime);
                
                // Record failed execution in metrics
                metrics.recordAgentExecution(agentName, executionTime, false);
                
                // For unexpected errors, we might want to fail the entire orchestration
                throw new OrchestrationException(ticker, "AGENT_UNEXPECTED_ERROR", 
                        String.format("Unexpected error in agent %s", agentName), ex);
            }
        }
        
        logger.debug("Sequential agent execution completed for ticker: {}", ticker);
        
                 // Check if at least one agent produced results
         if (report.getAgentResults().isEmpty()) {
             throw new OrchestrationException(ticker, "NO_AGENT_RESULTS", 
                     "No agents produced successful results");
         }
    }
    
    /**
     * Get orchestrator performance metrics.
     * 
     * @return current metrics instance
     */
    public OrchestratorMetrics getMetrics() {
        return metrics;
    }
    
    /**
     * Log current metrics summary.
     * Useful for monitoring and debugging orchestrator performance.
     */
    public void logMetricsSummary() {
        metrics.logMetricsSummary();
    }
} 