package com.cryptoagents.service;

import com.cryptoagents.model.dto.CryptoCurrency;
import com.cryptoagents.model.dto.HistoricalData;
import com.cryptoagents.model.dto.MarketData;
import com.cryptoagents.model.enums.TimePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Fallback service providing default data when the primary CryptoDataService is unavailable.
 * 
 * This service implements various fallback strategies including cached data retrieval,
 * default values, and circuit breaker patterns to ensure system resilience.
 */
@Service
public class CryptoDataFallbackService {

    private static final Logger logger = LoggerFactory.getLogger(CryptoDataFallbackService.class);

    // Circuit breaker configuration
    private static final int FAILURE_THRESHOLD = 5;
    private static final long RECOVERY_TIMEOUT_MS = 60000; // 1 minute
    private static final long HALF_OPEN_MAX_CALLS = 3;

    // Circuit breaker state
    private volatile CircuitBreakerState circuitState = CircuitBreakerState.CLOSED;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong lastFailureTime = new AtomicLong(0);
    private final AtomicLong halfOpenCalls = new AtomicLong(0);

    // Emergency data storage
    private final Map<String, BigDecimal> emergencyPrices = new ConcurrentHashMap<>();
    private final Map<String, MarketData> emergencyMarketData = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastSuccessfulUpdate = new ConcurrentHashMap<>();

    /**
     * Circuit breaker states for managing API call failures.
     */
    public enum CircuitBreakerState {
        CLOSED,    // Normal operation
        OPEN,      // API calls blocked
        HALF_OPEN  // Testing recovery
    }

    /**
     * Records a successful API call, potentially closing the circuit breaker.
     */
    public void recordSuccess() {
        failureCount.set(0);
        if (circuitState == CircuitBreakerState.HALF_OPEN) {
            circuitState = CircuitBreakerState.CLOSED;
            halfOpenCalls.set(0);
            logger.info("Circuit breaker CLOSED - API service recovered");
        }
    }

    /**
     * Records a failed API call, potentially opening the circuit breaker.
     */
    public void recordFailure() {
        lastFailureTime.set(System.currentTimeMillis());
        int failures = failureCount.incrementAndGet();
        
        if (failures >= FAILURE_THRESHOLD && circuitState == CircuitBreakerState.CLOSED) {
            circuitState = CircuitBreakerState.OPEN;
            logger.error("Circuit breaker OPENED - API service appears to be down (failures: {})", failures);
        }
    }

    /**
     * Checks if API calls should be allowed based on circuit breaker state.
     * 
     * @return true if API calls are allowed, false otherwise
     */
    public boolean isCallAllowed() {
        switch (circuitState) {
            case CLOSED:
                return true;
                
            case OPEN:
                // Check if recovery timeout has passed
                if (System.currentTimeMillis() - lastFailureTime.get() > RECOVERY_TIMEOUT_MS) {
                    circuitState = CircuitBreakerState.HALF_OPEN;
                    halfOpenCalls.set(0);
                    logger.info("Circuit breaker HALF_OPEN - testing service recovery");
                    return true;
                }
                return false;
                
            case HALF_OPEN:
                // Allow limited calls to test recovery
                long calls = halfOpenCalls.incrementAndGet();
                if (calls <= HALF_OPEN_MAX_CALLS) {
                    return true;
                } else {
                    // Too many calls in half-open state, back to open
                    circuitState = CircuitBreakerState.OPEN;
                    lastFailureTime.set(System.currentTimeMillis());
                    logger.warn("Circuit breaker back to OPEN - recovery test failed");
                    return false;
                }
                
            default:
                return false;
        }
    }

    /**
     * Gets the current circuit breaker state.
     * 
     * @return Current circuit breaker state
     */
    public CircuitBreakerState getCircuitState() {
        return circuitState;
    }

    /**
     * Provides fallback price data when the main service is unavailable.
     * 
     * @param ticker The cryptocurrency ticker
     * @return Fallback price data if available
     */
    public Optional<BigDecimal> getFallbackPrice(String ticker) {
        if (ticker == null || ticker.trim().isEmpty()) {
            return Optional.empty();
        }

        String normalizedTicker = ticker.toLowerCase();
        
        // Try emergency cached price first
        BigDecimal emergencyPrice = emergencyPrices.get(normalizedTicker);
        if (emergencyPrice != null) {
            logger.debug("Returning emergency cached price for {}: ${}", ticker, emergencyPrice);
            return Optional.of(emergencyPrice);
        }

        // Return default prices for major cryptocurrencies (very rough estimates)
        BigDecimal defaultPrice = getDefaultPrice(normalizedTicker);
        if (defaultPrice != null) {
            logger.warn("Returning default fallback price for {}: ${}", ticker, defaultPrice);
            return Optional.of(defaultPrice);
        }

        logger.warn("No fallback price available for ticker: {}", ticker);
        return Optional.empty();
    }

    /**
     * Provides fallback market data when the main service is unavailable.
     * 
     * @param ticker The cryptocurrency ticker
     * @return Fallback market data if available
     */
    public Optional<MarketData> getFallbackMarketData(String ticker) {
        if (ticker == null || ticker.trim().isEmpty()) {
            return Optional.empty();
        }

        String normalizedTicker = ticker.toLowerCase();
        
        // Try emergency cached market data
        MarketData emergencyData = emergencyMarketData.get(normalizedTicker);
        if (emergencyData != null) {
            logger.debug("Returning emergency cached market data for {}", ticker);
            return Optional.of(emergencyData);
        }

        // Create minimal fallback market data
        Optional<BigDecimal> fallbackPrice = getFallbackPrice(ticker);
        if (fallbackPrice.isPresent()) {
            MarketData fallbackData = new MarketData(ticker.toUpperCase(), 
                getCryptoName(normalizedTicker), fallbackPrice.get());
            
            logger.warn("Returning minimal fallback market data for {}", ticker);
            return Optional.of(fallbackData);
        }

        logger.warn("No fallback market data available for ticker: {}", ticker);
        return Optional.empty();
    }

    /**
     * Provides fallback historical data (empty with warning).
     * 
     * @param ticker The cryptocurrency ticker
     * @param period The time period
     * @return Empty optional with logged warning
     */
    public Optional<HistoricalData> getFallbackHistoricalData(String ticker, TimePeriod period) {
        logger.warn("Historical data not available in fallback mode for {} ({})", ticker, period);
        return Optional.empty();
    }

    /**
     * Provides fallback crypto info when the main service is unavailable.
     * 
     * @param ticker The cryptocurrency ticker
     * @return Fallback crypto info if available
     */
    public Optional<CryptoCurrency> getFallbackCryptoInfo(String ticker) {
        if (ticker == null || ticker.trim().isEmpty()) {
            return Optional.empty();
        }

        String normalizedTicker = ticker.toLowerCase();
        
        Optional<BigDecimal> fallbackPrice = getFallbackPrice(ticker);
        if (fallbackPrice.isPresent()) {
            CryptoCurrency fallbackCrypto = new CryptoCurrency(
                normalizedTicker, 
                ticker.toUpperCase(), 
                getCryptoName(normalizedTicker), 
                fallbackPrice.get()
            );
            
            logger.warn("Returning minimal fallback crypto info for {}", ticker);
            return Optional.of(fallbackCrypto);
        }

        logger.warn("No fallback crypto info available for ticker: {}", ticker);
        return Optional.empty();
    }

    /**
     * Returns a minimal list of supported cryptocurrencies in fallback mode.
     * 
     * @return List of major cryptocurrencies
     */
    public List<CryptoCurrency> getFallbackSupportedCryptocurrencies() {
        List<CryptoCurrency> fallbackList = new ArrayList<>();
        
        String[] majorCryptos = {"btc", "eth", "bnb", "ada", "dot", "sol", "matic", "avax"};
        
        for (String ticker : majorCryptos) {
            Optional<BigDecimal> price = getFallbackPrice(ticker);
            if (price.isPresent()) {
                fallbackList.add(new CryptoCurrency(
                    ticker, 
                    ticker.toUpperCase(), 
                    getCryptoName(ticker), 
                    price.get()
                ));
            }
        }
        
        logger.warn("Returning fallback supported cryptocurrencies list with {} entries", fallbackList.size());
        return fallbackList;
    }

    /**
     * Stores emergency backup data for fallback use.
     * 
     * @param ticker The cryptocurrency ticker
     * @param price The current price to store
     * @param marketData The market data to store (optional)
     */
    public void storeEmergencyData(String ticker, BigDecimal price, MarketData marketData) {
        if (ticker != null && price != null) {
            String normalizedTicker = ticker.toLowerCase();
            emergencyPrices.put(normalizedTicker, price);
            lastSuccessfulUpdate.put(normalizedTicker, LocalDateTime.now());
            
            if (marketData != null) {
                emergencyMarketData.put(normalizedTicker, marketData);
            }
            
            logger.debug("Stored emergency data for {}: ${}", ticker, price);
        }
    }

    /**
     * Gets default hardcoded prices for major cryptocurrencies (rough estimates).
     * These should be periodically updated but serve as last resort fallbacks.
     * 
     * @param normalizedTicker The lowercase ticker symbol
     * @return Default price if available
     */
    private BigDecimal getDefaultPrice(String normalizedTicker) {
        Map<String, BigDecimal> defaultPrices = new HashMap<>();
        defaultPrices.put("btc", new BigDecimal("45000"));
        defaultPrices.put("eth", new BigDecimal("3000"));
        defaultPrices.put("bnb", new BigDecimal("400"));
        defaultPrices.put("ada", new BigDecimal("0.50"));
        defaultPrices.put("dot", new BigDecimal("8.00"));
        defaultPrices.put("sol", new BigDecimal("100"));
        defaultPrices.put("matic", new BigDecimal("1.00"));
        defaultPrices.put("avax", new BigDecimal("25.00"));
        
        return defaultPrices.get(normalizedTicker);
    }

    /**
     * Gets the full name for a cryptocurrency ticker.
     * 
     * @param normalizedTicker The lowercase ticker symbol
     * @return The full cryptocurrency name
     */
    private String getCryptoName(String normalizedTicker) {
        Map<String, String> cryptoNames = new HashMap<>();
        cryptoNames.put("btc", "Bitcoin");
        cryptoNames.put("eth", "Ethereum");
        cryptoNames.put("bnb", "BNB");
        cryptoNames.put("ada", "Cardano");
        cryptoNames.put("dot", "Polkadot");
        cryptoNames.put("sol", "Solana");
        cryptoNames.put("matic", "Polygon");
        cryptoNames.put("avax", "Avalanche");
        
        return cryptoNames.getOrDefault(normalizedTicker, 
            normalizedTicker.toUpperCase() + " Token");
    }

    /**
     * Gets statistics about the fallback service and circuit breaker.
     * 
     * @return Fallback service statistics
     */
    public FallbackStats getStats() {
        return new FallbackStats(
            circuitState,
            failureCount.get(),
            emergencyPrices.size(),
            emergencyMarketData.size(),
            lastFailureTime.get()
        );
    }

    /**
     * Statistics holder for fallback service monitoring.
     */
    public static class FallbackStats {
        private final CircuitBreakerState circuitState;
        private final int failureCount;
        private final int emergencyPricesCount;
        private final int emergencyMarketDataCount;
        private final long lastFailureTime;

        public FallbackStats(CircuitBreakerState circuitState, int failureCount, 
                           int emergencyPricesCount, int emergencyMarketDataCount, 
                           long lastFailureTime) {
            this.circuitState = circuitState;
            this.failureCount = failureCount;
            this.emergencyPricesCount = emergencyPricesCount;
            this.emergencyMarketDataCount = emergencyMarketDataCount;
            this.lastFailureTime = lastFailureTime;
        }

        // Getters
        public CircuitBreakerState getCircuitState() { return circuitState; }
        public int getFailureCount() { return failureCount; }
        public int getEmergencyPricesCount() { return emergencyPricesCount; }
        public int getEmergencyMarketDataCount() { return emergencyMarketDataCount; }
        public long getLastFailureTime() { return lastFailureTime; }

        @Override
        public String toString() {
            return String.format("FallbackStats{circuit=%s, failures=%d, emergencyPrices=%d, emergencyMarketData=%d}", 
                    circuitState, failureCount, emergencyPricesCount, emergencyMarketDataCount);
        }
    }
} 