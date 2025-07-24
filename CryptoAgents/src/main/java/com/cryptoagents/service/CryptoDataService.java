package com.cryptoagents.service;

import com.cryptoagents.model.dto.CryptoCurrency;
import com.cryptoagents.model.dto.HistoricalData;
import com.cryptoagents.model.dto.MarketData;
import com.cryptoagents.model.enums.TimePeriod;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for cryptocurrency data retrieval.
 * 
 * This interface defines methods for fetching real-time and historical
 * cryptocurrency data from external APIs (such as CoinGecko).
 * Implementations should handle error cases, caching, and rate limiting.
 */
public interface CryptoDataService {

    /**
     * Retrieves the current price for a specific cryptocurrency.
     * 
     * @param ticker The cryptocurrency ticker symbol (e.g., "BTC", "ETH")
     * @return The current price in USD, or empty if not available
     * @throws IllegalArgumentException if ticker is null or empty
     */
    Optional<BigDecimal> getCurrentPrice(String ticker);

    /**
     * Retrieves historical price data for a cryptocurrency over a specified period.
     * 
     * @param ticker The cryptocurrency ticker symbol
     * @param period The time period for historical data
     * @return Historical data containing price points, or empty if not available
     * @throws IllegalArgumentException if ticker is null/empty or period is null
     */
    Optional<HistoricalData> getHistoricalData(String ticker, TimePeriod period);

    /**
     * Retrieves comprehensive market data for a cryptocurrency.
     * 
     * @param ticker The cryptocurrency ticker symbol
     * @return Market data including price, volume, market cap, etc., or empty if not available
     * @throws IllegalArgumentException if ticker is null or empty
     */
    Optional<MarketData> getMarketData(String ticker);

    /**
     * Retrieves basic cryptocurrency information.
     * 
     * @param ticker The cryptocurrency ticker symbol
     * @return Basic cryptocurrency info, or empty if not available
     * @throws IllegalArgumentException if ticker is null or empty
     */
    Optional<CryptoCurrency> getCryptoInfo(String ticker);

    /**
     * Retrieves a list of supported cryptocurrencies.
     * 
     * @return List of supported cryptocurrencies with basic info
     */
    List<CryptoCurrency> getSupportedCryptocurrencies();

    /**
     * Checks if the external API service is currently available.
     * 
     * @return true if the service is available, false otherwise
     */
    boolean isServiceAvailable();

    /**
     * Validates if a ticker symbol is supported by the service.
     * 
     * @param ticker The cryptocurrency ticker symbol to validate
     * @return true if the ticker is supported, false otherwise
     */
    boolean isTickerSupported(String ticker);
} 