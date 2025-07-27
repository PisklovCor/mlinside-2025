//package com.cryptoagents.service.impl;
//
//import com.cryptoagents.config.CacheConfig;
//import com.cryptoagents.model.dto.CryptoCurrency;
//import com.cryptoagents.model.dto.MarketData;
//import com.cryptoagents.model.enums.TimePeriod;
//import com.cryptoagents.service.CryptoDataService;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.ResourceAccessException;
//import org.springframework.web.client.RestClient;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * CoinGecko API implementation for cryptocurrency data retrieval.
// *
// * This service provides real-time and historical cryptocurrency data
// * from the CoinGecko API with fallback mechanisms and circuit breaker pattern.
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CoinGeckoDataService implements CryptoDataService {
//
//    private final RestClient coinGeckoRestClient;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    // Простой кэш в памяти для поддерживаемых криптовалют
//    private final Map<String, String> tickerToIdMapping = new ConcurrentHashMap<>();
//    private volatile LocalDateTime lastMappingUpdate = null;
//    private static final long MAPPING_CACHE_DURATION_HOURS = 24;
//
//    @Override
//    @Cacheable(value = CacheConfig.PRICE_CACHE, key = "#ticker.toLowerCase()", unless = "#result.isEmpty()")
//    public Optional<BigDecimal> getCurrentPrice(String ticker) {
//        try {
//            validateTicker(ticker);
//
//
//            String coinId = getCoinIdFromTicker(ticker);
//            if (coinId == null) {
//                log.warn("Ticker '{}' not found in supported cryptocurrencies", ticker);
//            }
//
//            String url = "/simple/price?ids={coinId}&vs_currencies=usd";
//
//            ResponseEntity<String> response = coinGeckoRestClient.get()
//                    .uri(url, coinId)
//                    .retrieve()
//                    .toEntity(String.class);
//
//            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//                JsonNode jsonNode = objectMapper.readTree(response.getBody());
//                JsonNode priceNode = jsonNode.path(coinId).path("usd");
//
//                if (!priceNode.isMissingNode()) {
//                    BigDecimal price = priceNode.decimalValue();
//
//                    log.debug("Retrieved current price for {}: ${}", ticker, price);
//                    return Optional.of(price);
//                }
//            } else {
//
//            }
//
//            log.warn("No price data found for ticker: {}", ticker);
//            return fallbackService.getFallbackPrice(ticker);
//
//        } catch (HttpClientErrorException e) {
//            fallbackService.recordFailure();
//            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
//                log.error("Rate limit exceeded for getCurrentPrice: {}", ticker);
//            } else {
//                log.error("Client error getting current price for {}: {}", ticker, e.getMessage());
//            }
//            return fallbackService.getFallbackPrice(ticker);
//        } catch (HttpServerErrorException e) {
//            fallbackService.recordFailure();
//            log.error("Server error getting current price for {}: {}", ticker, e.getMessage());
//            return fallbackService.getFallbackPrice(ticker);
//        } catch (ResourceAccessException e) {
//            fallbackService.recordFailure();
//            log.error("Network error getting current price for {}: {}", ticker, e.getMessage());
//            return fallbackService.getFallbackPrice(ticker);
//        } catch (Exception e) {
//            fallbackService.recordFailure();
//            log.error("Unexpected error getting current price for {}: {}", ticker, e.getMessage(), e);
//            return fallbackService.getFallbackPrice(ticker);
//        }
//    }
//
//    @Override
//    @Cacheable(value = CacheConfig.HISTORICAL_CACHE, key = "#ticker.toLowerCase() + '_' + #period.name()", unless = "#result.isEmpty()")
//    public Optional<HistoricalData> getHistoricalData(String ticker, TimePeriod period) {
//        try {
//            validateTicker(ticker);
//            validateTimePeriod(period);
//
//            // Check circuit breaker
//            if (!fallbackService.isCallAllowed()) {
//                log.debug("Circuit breaker is OPEN, using fallback for getHistoricalData: {}", ticker);
//                return fallbackService.getFallbackHistoricalData(ticker, period);
//            }
//
//            String coinId = getCoinIdFromTicker(ticker);
//            if (coinId == null) {
//                log.warn("Ticker '{}' not found in supported cryptocurrencies", ticker);
//                return fallbackService.getFallbackHistoricalData(ticker, period);
//            }
//
//            String url = "/coins/{coinId}/market_chart?vs_currency=usd&days={days}";
//
//            ResponseEntity<String> response = coinGeckoRestClient.get()
//                    .uri(url, coinId, period.getApiValue())
//                    .retrieve()
//                    .toEntity(String.class);
//
//            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//                JsonNode jsonNode = objectMapper.readTree(response.getBody());
//
//                HistoricalData historicalData = new HistoricalData(ticker, period);
//
//                JsonNode pricesNode = jsonNode.path("prices");
//                if (pricesNode.isArray()) {
//                    for (JsonNode pricePoint : pricesNode) {
//                        if (pricePoint.isArray() && pricePoint.size() >= 2) {
//                            long timestamp = pricePoint.get(0).asLong();
//                            BigDecimal price = pricePoint.get(1).decimalValue();
//
//                            LocalDateTime dateTime = LocalDateTime.ofInstant(
//                                Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
//
//                            historicalData.addPricePoint(
//                                new HistoricalData.PricePoint(dateTime, price));
//                        }
//                    }
//                }
//
//                if (historicalData.hasValidData()) {
//                    // Record success
//                    fallbackService.recordSuccess();
//
//                    log.debug("Retrieved historical data for {} ({}): {} points",
//                        ticker, period, historicalData.getDataPointsCount());
//                    return Optional.of(historicalData);
//                }
//            }
//
//            log.warn("No historical data found for ticker: {} (period: {})", ticker, period);
//            return fallbackService.getFallbackHistoricalData(ticker, period);
//
//        } catch (HttpClientErrorException e) {
//            fallbackService.recordFailure();
//            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
//                log.error("Rate limit exceeded for getHistoricalData: {} ({})", ticker, period);
//            } else {
//                log.error("Client error getting historical data for {} ({}): {}",
//                    ticker, period, e.getMessage());
//            }
//            return fallbackService.getFallbackHistoricalData(ticker, period);
//        } catch (HttpServerErrorException e) {
//            fallbackService.recordFailure();
//            log.error("Server error getting historical data for {} ({}): {}",
//                ticker, period, e.getMessage());
//            return fallbackService.getFallbackHistoricalData(ticker, period);
//        } catch (ResourceAccessException e) {
//            fallbackService.recordFailure();
//            log.error("Network error getting historical data for {} ({}): {}",
//                ticker, period, e.getMessage());
//            return fallbackService.getFallbackHistoricalData(ticker, period);
//        } catch (Exception e) {
//            fallbackService.recordFailure();
//            log.error("Unexpected error getting historical data for {} ({}): {}",
//                ticker, period, e.getMessage(), e);
//            return fallbackService.getFallbackHistoricalData(ticker, period);
//        }
//    }
//
//    @Override
//    @Cacheable(value = CacheConfig.MARKET_CACHE, key = "#ticker.toLowerCase()", unless = "#result.isEmpty()")
//    public Optional<MarketData> getMarketData(String ticker) {
//        try {
//            validateTicker(ticker);
//
//            // Check circuit breaker
//            if (!fallbackService.isCallAllowed()) {
//                log.debug("Circuit breaker is OPEN, using fallback for getMarketData: {}", ticker);
//                return fallbackService.getFallbackMarketData(ticker);
//            }
//
//            String coinId = getCoinIdFromTicker(ticker);
//            if (coinId == null) {
//                log.warn("Ticker '{}' not found in supported cryptocurrencies", ticker);
//                return fallbackService.getFallbackMarketData(ticker);
//            }
//
//            String url = "/coins/markets?vs_currency=usd&ids={coinId}&order=market_cap_desc" +
//                        "&per_page=1&page=1&sparkline=false&price_change_percentage=24h,7d,30d";
//
//            ResponseEntity<String> response = coinGeckoRestClient.get()
//                    .uri(url, coinId)
//                    .retrieve()
//                    .toEntity(String.class);
//
//            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//                List<MarketData> marketDataList = objectMapper.readValue(
//                    response.getBody(), new TypeReference<List<MarketData>>() {});
//
//                if (!marketDataList.isEmpty()) {
//                    MarketData marketData = marketDataList.get(0);
//                    marketData.setTicker(ticker.toUpperCase());
//
//                    // Record success and store emergency data
//                    fallbackService.recordSuccess();
//                    fallbackService.storeEmergencyData(ticker, marketData.getCurrentPrice(), marketData);
//
//                    log.debug("Retrieved market data for {}: {} (rank: {})",
//                        ticker, marketData.getCurrentPrice(), marketData.getMarketCapRank());
//                    return Optional.of(marketData);
//                }
//            }
//
//            log.warn("No market data found for ticker: {}", ticker);
//            return fallbackService.getFallbackMarketData(ticker);
//
//        } catch (HttpClientErrorException e) {
//            fallbackService.recordFailure();
//            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
//                log.error("Rate limit exceeded for getMarketData: {}", ticker);
//            } else {
//                log.error("Client error getting market data for {}: {}", ticker, e.getMessage());
//            }
//            return fallbackService.getFallbackMarketData(ticker);
//        } catch (HttpServerErrorException e) {
//            fallbackService.recordFailure();
//            log.error("Server error getting market data for {}: {}", ticker, e.getMessage());
//            return fallbackService.getFallbackMarketData(ticker);
//        } catch (ResourceAccessException e) {
//            fallbackService.recordFailure();
//            log.error("Network error getting market data for {}: {}", ticker, e.getMessage());
//            return fallbackService.getFallbackMarketData(ticker);
//        } catch (Exception e) {
//            fallbackService.recordFailure();
//            log.error("Unexpected error getting market data for {}: {}", ticker, e.getMessage(), e);
//            return fallbackService.getFallbackMarketData(ticker);
//        }
//    }
//
//    @Override
//    @Cacheable(value = CacheConfig.API_RESPONSE_CACHE, key = "#ticker.toLowerCase()", unless = "#result.isEmpty()")
//    public Optional<CryptoCurrency> getCryptoInfo(String ticker) {
//        try {
//            validateTicker(ticker);
//
//            // Check circuit breaker
//            if (!fallbackService.isCallAllowed()) {
//                log.debug("Circuit breaker is OPEN, using fallback for getCryptoInfo: {}", ticker);
//                return fallbackService.getFallbackCryptoInfo(ticker);
//            }
//
//            String coinId = getCoinIdFromTicker(ticker);
//            if (coinId == null) {
//                log.warn("Ticker '{}' not found in supported cryptocurrencies", ticker);
//                return fallbackService.getFallbackCryptoInfo(ticker);
//            }
//
//            String url = "/coins/{coinId}?localization=false&tickers=false&market_data=true" +
//                        "&community_data=false&developer_data=false&sparkline=false";
//
//            ResponseEntity<String> response = coinGeckoRestClient.get()
//                    .uri(url, coinId)
//                    .retrieve()
//                    .toEntity(String.class);
//
//            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//                JsonNode jsonNode = objectMapper.readTree(response.getBody());
//
//                CryptoCurrency crypto = new CryptoCurrency();
//                crypto.setId(jsonNode.path("id").asText());
//                crypto.setSymbol(jsonNode.path("symbol").asText().toUpperCase());
//                crypto.setName(jsonNode.path("name").asText());
//
//                JsonNode marketData = jsonNode.path("market_data");
//                if (!marketData.isMissingNode()) {
//                    JsonNode currentPrice = marketData.path("current_price").path("usd");
//                    if (!currentPrice.isMissingNode()) {
//                        crypto.setCurrentPrice(currentPrice.decimalValue());
//                    }
//
//                    JsonNode marketCap = marketData.path("market_cap").path("usd");
//                    if (!marketCap.isMissingNode()) {
//                        crypto.setMarketCap(marketCap.decimalValue());
//                    }
//
//                    JsonNode marketCapRank = marketData.path("market_cap_rank");
//                    if (!marketCapRank.isMissingNode()) {
//                        crypto.setMarketCapRank(marketCapRank.asInt());
//                    }
//                }
//
//                // Record success
//                fallbackService.recordSuccess();
//
//                log.debug("Retrieved crypto info for {}: {}", ticker, crypto.getName());
//                return Optional.of(crypto);
//            }
//
//            log.warn("No crypto info found for ticker: {}", ticker);
//            return fallbackService.getFallbackCryptoInfo(ticker);
//
//        } catch (HttpClientErrorException e) {
//            fallbackService.recordFailure();
//            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
//                log.error("Rate limit exceeded for getCryptoInfo: {}", ticker);
//            } else {
//                log.error("Client error getting crypto info for {}: {}", ticker, e.getMessage());
//            }
//            return fallbackService.getFallbackCryptoInfo(ticker);
//        } catch (HttpServerErrorException e) {
//            fallbackService.recordFailure();
//            log.error("Server error getting crypto info for {}: {}", ticker, e.getMessage());
//            return fallbackService.getFallbackCryptoInfo(ticker);
//        } catch (ResourceAccessException e) {
//            fallbackService.recordFailure();
//            log.error("Network error getting crypto info for {}: {}", ticker, e.getMessage());
//            return fallbackService.getFallbackCryptoInfo(ticker);
//        } catch (Exception e) {
//            fallbackService.recordFailure();
//            log.error("Unexpected error getting crypto info for {}: {}", ticker, e.getMessage(), e);
//            return fallbackService.getFallbackCryptoInfo(ticker);
//        }
//    }
//
//    @Override
//    @Cacheable(value = CacheConfig.API_RESPONSE_CACHE, key = "'all'", unless = "#result.isEmpty()")
//    public List<CryptoCurrency> getSupportedCryptocurrencies() {
//        try {
//            // Check circuit breaker
//            if (!fallbackService.isCallAllowed()) {
//                log.debug("Circuit breaker is OPEN, using fallback for getSupportedCryptocurrencies");
//                return fallbackService.getFallbackSupportedCryptocurrencies();
//            }
//
//            String url = "/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=100&page=1&sparkline=false";
//
//            ResponseEntity<String> response = coinGeckoRestClient.get()
//                    .uri(url)
//                    .retrieve()
//                    .toEntity(String.class);
//
//            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//                List<CryptoCurrency> cryptocurrencies = objectMapper.readValue(
//                    response.getBody(), new TypeReference<List<CryptoCurrency>>() {});
//
//                // Update ticker to ID mapping
//                updateTickerMapping(cryptocurrencies);
//
//                // Record success
//                fallbackService.recordSuccess();
//
//                log.debug("Retrieved {} supported cryptocurrencies", cryptocurrencies.size());
//                return cryptocurrencies;
//            }
//
//            log.warn("Failed to retrieve supported cryptocurrencies");
//            return fallbackService.getFallbackSupportedCryptocurrencies();
//
//        } catch (HttpClientErrorException e) {
//            fallbackService.recordFailure();
//            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
//                log.error("Rate limit exceeded for getSupportedCryptocurrencies");
//            } else {
//                log.error("Client error getting supported cryptocurrencies: {}", e.getMessage());
//            }
//            return fallbackService.getFallbackSupportedCryptocurrencies();
//        } catch (HttpServerErrorException e) {
//            fallbackService.recordFailure();
//            log.error("Server error getting supported cryptocurrencies: {}", e.getMessage());
//            return fallbackService.getFallbackSupportedCryptocurrencies();
//        } catch (ResourceAccessException e) {
//            fallbackService.recordFailure();
//            log.error("Network error getting supported cryptocurrencies: {}", e.getMessage());
//            return fallbackService.getFallbackSupportedCryptocurrencies();
//        } catch (Exception e) {
//            fallbackService.recordFailure();
//            log.error("Unexpected error getting supported cryptocurrencies: {}", e.getMessage(), e);
//            return fallbackService.getFallbackSupportedCryptocurrencies();
//        }
//    }
//
//    @Override
//    @Cacheable(value = CacheConfig.API_RESPONSE_CACHE, key = "'status'")
//    public boolean isServiceAvailable() {
//        try {
//            // Check circuit breaker state - if it's open, service is considered unavailable
//            if (!fallbackService.isCallAllowed()) {
//                log.debug("Circuit breaker is OPEN, service considered unavailable");
//                return false;
//            }
//
//            String url = "/ping";
//            ResponseEntity<String> response = coinGeckoRestClient.get()
//                    .uri(url)
//                    .retrieve()
//                    .toEntity(String.class);
//            boolean available = response.getStatusCode() == HttpStatus.OK;
//
//            if (available) {
//                fallbackService.recordSuccess();
//            } else {
//                fallbackService.recordFailure();
//            }
//
//            log.debug("CoinGecko service availability check: {}", available);
//            return available;
//
//        } catch (HttpClientErrorException e) {
//            fallbackService.recordFailure();
//            log.warn("CoinGecko service availability check failed (client error): {}", e.getMessage());
//            return false;
//        } catch (HttpServerErrorException e) {
//            fallbackService.recordFailure();
//            log.warn("CoinGecko service availability check failed (server error): {}", e.getMessage());
//            return false;
//        } catch (ResourceAccessException e) {
//            fallbackService.recordFailure();
//            log.warn("CoinGecko service availability check failed (network error): {}", e.getMessage());
//            return false;
//        } catch (Exception e) {
//            fallbackService.recordFailure();
//            log.warn("CoinGecko service availability check failed (unexpected error): {}", e.getMessage());
//            return false;
//        }
//    }
//
//    @Override
//    public boolean isTickerSupported(String ticker) {
//        if (ticker == null || ticker.trim().isEmpty()) {
//            return false;
//        }
//
//        try {
//            // Check if we have a recent mapping
//            ensureTickerMappingIsUpToDate();
//
//            boolean isSupported = tickerToIdMapping.containsKey(ticker.toLowerCase());
//
//            // If not found in our mapping and circuit breaker allows, try fallback
//            if (!isSupported && fallbackService.isCallAllowed()) {
//                // Check if fallback service has this ticker
//                List<CryptoCurrency> fallbackList = fallbackService.getFallbackSupportedCryptocurrencies();
//                isSupported = fallbackList.stream()
//                    .anyMatch(crypto -> ticker.equalsIgnoreCase(crypto.getSymbol()));
//            }
//
//            return isSupported;
//
//        } catch (Exception e) {
//            log.warn("Error checking ticker support for {}: {}", ticker, e.getMessage());
//            // Fallback to checking only our local mapping
//            return tickerToIdMapping.containsKey(ticker.toLowerCase());
//        }
//    }
//
//    // Private helper methods
//
//    private void validateTicker(String ticker) {
//        if (ticker == null || ticker.trim().isEmpty()) {
//            throw new IllegalArgumentException("Ticker cannot be null or empty");
//        }
//    }
//
//    private void validateTimePeriod(TimePeriod period) {
//        if (period == null) {
//            throw new IllegalArgumentException("Time period cannot be null");
//        }
//    }
//
//    private String getCoinIdFromTicker(String ticker) {
//        ensureTickerMappingIsUpToDate();
//        return tickerToIdMapping.get(ticker.toLowerCase());
//    }
//
//    private void ensureTickerMappingIsUpToDate() {
//        if (lastMappingUpdate == null ||
//            LocalDateTime.now().isAfter(lastMappingUpdate.plusHours(MAPPING_CACHE_DURATION_HOURS))) {
//
//            log.debug("Updating ticker to coin ID mapping");
//            getSupportedCryptocurrencies(); // This will update the mapping
//        }
//    }
//
//    private void updateTickerMapping(List<CryptoCurrency> cryptocurrencies) {
//        tickerToIdMapping.clear();
//
//        for (CryptoCurrency crypto : cryptocurrencies) {
//            if (crypto.getSymbol() != null && crypto.getId() != null) {
//                tickerToIdMapping.put(crypto.getSymbol().toLowerCase(), crypto.getId());
//            }
//        }
//
//        lastMappingUpdate = LocalDateTime.now();
//        log.debug("Updated ticker mapping with {} cryptocurrencies", tickerToIdMapping.size());
//    }
//
//    // Cache management methods
//
//    /**
//     * Evicts all cached price data.
//     * Useful when market conditions change rapidly or for testing purposes.
//     */
//    @CacheEvict(value = CacheConfig.PRICE_CACHE, allEntries = true)
//    public void evictAllPriceCache() {
//        log.info("Evicted all entries from price cache");
//    }
//
//    /**
//     * Evicts cached price data for a specific ticker.
//     *
//     * @param ticker The cryptocurrency ticker to evict from cache
//     */
//    @CacheEvict(value = CacheConfig.PRICE_CACHE, key = "#ticker.toLowerCase()")
//    public void evictPriceCache(String ticker) {
//        log.debug("Evicted price cache for ticker: {}", ticker);
//    }
//
//    /**
//     * Evicts all cached market data.
//     */
//    @CacheEvict(value = CacheConfig.MARKET_CACHE, allEntries = true)
//    public void evictAllMarketDataCache() {
//        log.info("Evicted all entries from market data cache");
//    }
//
//    /**
//     * Evicts cached market data for a specific ticker.
//     *
//     * @param ticker The cryptocurrency ticker to evict from cache
//     */
//    @CacheEvict(value = CacheConfig.MARKET_CACHE, key = "#ticker.toLowerCase()")
//    public void evictMarketDataCache(String ticker) {
//        log.debug("Evicted market data cache for ticker: {}", ticker);
//    }
//
//    /**
//     * Evicts all cached historical data.
//     */
//    @CacheEvict(value = CacheConfig.HISTORICAL_CACHE, allEntries = true)
//    public void evictAllHistoricalDataCache() {
//        log.info("Evicted all entries from historical data cache");
//    }
//
//    /**
//     * Evicts cached historical data for a specific ticker and period.
//     *
//     * @param ticker The cryptocurrency ticker
//     * @param period The time period
//     */
//    @CacheEvict(value = CacheConfig.HISTORICAL_CACHE, key = "#ticker.toLowerCase() + '_' + #period.name()")
//    public void evictHistoricalDataCache(String ticker, TimePeriod period) {
//        log.debug("Evicted historical data cache for ticker: {} (period: {})", ticker, period);
//    }
//
//    /**
//     * Evicts all caches. Use with caution as this will force all subsequent
//     * requests to hit the external API until caches are repopulated.
//     */
//            @CacheEvict(value = {
//            CacheConfig.PRICE_CACHE,
//            CacheConfig.MARKET_CACHE,
//            CacheConfig.HISTORICAL_CACHE,
//            CacheConfig.ANALYSIS_CACHE,
//            CacheConfig.API_RESPONSE_CACHE
//        }, allEntries = true)
//    public void evictAllCaches() {
//        log.warn("Evicted ALL caches - all subsequent requests will hit the external API");
//    }
//
//    /**
//     * Warms up the cache by fetching data for popular cryptocurrencies.
//     * This method can be called at application startup or scheduled intervals.
//     */
//    public void warmupCache() {
//        log.info("Starting cache warmup...");
//
//        // Popular cryptocurrencies to warm up
//        String[] popularTickers = {"BTC", "ETH", "BNB", "ADA", "DOT", "SOL", "MATIC", "AVAX"};
//
//        for (String ticker : popularTickers) {
//            try {
//                // Warm up price and market data
//                getCurrentPrice(ticker);
//                getMarketData(ticker);
//
//                // Small delay to avoid hitting rate limits
//                Thread.sleep(100);
//
//            } catch (Exception e) {
//                log.warn("Failed to warm up cache for ticker {}: {}", ticker, e.getMessage());
//            }
//        }
//
//        log.info("Cache warmup completed for {} tickers", popularTickers.length);
//    }
//}