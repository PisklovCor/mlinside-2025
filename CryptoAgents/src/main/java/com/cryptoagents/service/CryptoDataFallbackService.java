package com.cryptoagents.service;

import com.cryptoagents.model.dto.CryptoCurrency;
import com.cryptoagents.model.dto.HistoricalData;
import com.cryptoagents.model.dto.MarketData;
import com.cryptoagents.model.enums.TimePeriod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Сервис резервного копирования данных о криптовалютах.
 * 
 * Этот сервис предоставляет fallback данные, когда основной API недоступен.
 * Реализует паттерн Circuit Breaker для предотвращения каскадных сбоев.
 */
@Slf4j
@Service
public class CryptoDataFallbackService {
    
    // Circuit Breaker константы
    private static final int FAILURE_THRESHOLD = 5;
    private static final long RESET_TIMEOUT_MS = 60000; // 1 минута
    private static final int HALF_OPEN_MAX_CALLS = 3;
    
    // Состояния Circuit Breaker
    private enum CircuitState {
        CLOSED,     // Нормальная работа
        OPEN,       // Блокировка вызовов
        HALF_OPEN   // Тестовые вызовы
    }
    
    private volatile CircuitState circuitState = CircuitState.CLOSED;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private volatile long lastFailureTime = 0;
    
    // Кэш fallback данных
    private final Map<String, BigDecimal> priceCache = new ConcurrentHashMap<>();
    private final Map<String, MarketData> marketDataCache = new ConcurrentHashMap<>();
    private final Map<String, CryptoCurrency> cryptoInfoCache = new ConcurrentHashMap<>();
    
    // Статистика
    private final AtomicLong totalFallbackCalls = new AtomicLong(0);
    private final AtomicLong successfulFallbackCalls = new AtomicLong(0);
    
    /**
     * Проверяет, разрешены ли вызовы API в текущем состоянии Circuit Breaker.
     * 
     * @return true, если вызовы API разрешены, false в противном случае
     */
    public boolean isCallAllowed() {
        switch (circuitState) {
            case CLOSED:
                return true;
            case OPEN:
                // Проверяем, прошло ли достаточно времени для перехода в HALF_OPEN
                if (System.currentTimeMillis() - lastFailureTime > RESET_TIMEOUT_MS) {
                    circuitState = CircuitState.HALF_OPEN;
                    log.info("Circuit Breaker переходит в состояние HALF_OPEN");
                    return true;
                }
                return false;
            case HALF_OPEN:
                return successCount.get() < HALF_OPEN_MAX_CALLS;
            default:
                return false;
        }
    }
    
    /**
     * Получает текущее состояние Circuit Breaker.
     * 
     * @return Текущее состояние Circuit Breaker
     */
    public CircuitState getCircuitState() {
        return circuitState;
    }
    
    /**
     * Получает резервную цену криптовалюты.
     * 
     * @param ticker Тикер криптовалюты
     * @return Резервные данные о цене, если доступны
     */
    public Optional<BigDecimal> getFallbackPrice(String ticker) {
        totalFallbackCalls.incrementAndGet();
        
        try {
            String normalizedTicker = ticker.toUpperCase();
            BigDecimal price = priceCache.get(normalizedTicker);
            
            if (price != null) {
                successfulFallbackCalls.incrementAndGet();
                log.debug("Fallback цена для {}: {}", ticker, price);
                return Optional.of(price);
            }
            
            // Попытка получить цену по умолчанию
            Optional<BigDecimal> defaultPrice = getDefaultPrice(normalizedTicker);
            if (defaultPrice.isPresent()) {
                successfulFallbackCalls.incrementAndGet();
                log.debug("Использована цена по умолчанию для {}: {}", ticker, defaultPrice.get());
            }
            
            return defaultPrice;
            
        } catch (Exception e) {
            log.warn("Ошибка при получении fallback цены для {}: {}", ticker, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Получает резервные рыночные данные криптовалюты.
     * 
     * @param ticker Тикер криптовалюты
     * @return Резервные рыночные данные, если доступны
     */
    public Optional<MarketData> getFallbackMarketData(String ticker) {
        totalFallbackCalls.incrementAndGet();
        
        try {
            String normalizedTicker = ticker.toUpperCase();
            MarketData marketData = marketDataCache.get(normalizedTicker);
            
            if (marketData != null) {
                successfulFallbackCalls.incrementAndGet();
                log.debug("Fallback рыночные данные для {}: {}", ticker, marketData);
                return Optional.of(marketData);
            }
            
            // Создание базовых рыночных данных с fallback ценой
            Optional<BigDecimal> price = getFallbackPrice(ticker);
            if (price.isPresent()) {
                MarketData fallbackData = MarketData.builder()
                        .price(price.get())
                        .volume24h(BigDecimal.ZERO)
                        .marketCap(BigDecimal.ZERO)
                        .priceChange24h(BigDecimal.ZERO)
                        .priceChangePercentage24h(BigDecimal.ZERO)
                        .lastUpdated(LocalDateTime.now())
                        .build();
                
                successfulFallbackCalls.incrementAndGet();
                log.debug("Созданы базовые рыночные данные для {}: {}", ticker, fallbackData);
                return Optional.of(fallbackData);
            }
            
            return Optional.empty();
            
        } catch (Exception e) {
            log.warn("Ошибка при получении fallback рыночных данных для {}: {}", ticker, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Получает резервные исторические данные криптовалюты.
     * 
     * @param ticker Тикер криптовалюты
     * @param period Временной период
     * @return Резервные исторические данные с базовыми данными валидации
     */
    public Optional<HistoricalData> getFallbackHistoricalData(String ticker, TimePeriod period) {
        totalFallbackCalls.incrementAndGet();
        
        try {
            // Создание базовых исторических данных с текущей ценой
            Optional<BigDecimal> currentPrice = getFallbackPrice(ticker);
            if (currentPrice.isPresent()) {
                List<HistoricalData.PricePoint> pricePoints = Arrays.asList(
                        new HistoricalData.PricePoint(LocalDateTime.now(), currentPrice.get())
                );
                
                HistoricalData fallbackData = HistoricalData.builder()
                        .ticker(ticker.toUpperCase())
                        .period(period)
                        .pricePoints(pricePoints)
                        .dataPoints(1)
                        .build();
                
                successfulFallbackCalls.incrementAndGet();
                log.debug("Созданы базовые исторические данные для {}: {}", ticker, fallbackData);
                return Optional.of(fallbackData);
            }
            
            return Optional.empty();
            
        } catch (Exception e) {
            log.warn("Ошибка при получении fallback исторических данных для {}: {}", ticker, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Получает резервную информацию о криптовалюте.
     * 
     * @param ticker Тикер криптовалюты
     * @return Резервная информация о криптовалюте, если доступна
     */
    public Optional<CryptoCurrency> getFallbackCryptoInfo(String ticker) {
        totalFallbackCalls.incrementAndGet();
        
        try {
            String normalizedTicker = ticker.toUpperCase();
            CryptoCurrency cryptoInfo = cryptoInfoCache.get(normalizedTicker);
            
            if (cryptoInfo != null) {
                successfulFallbackCalls.incrementAndGet();
                log.debug("Fallback информация о криптовалюте для {}: {}", ticker, cryptoInfo);
                return Optional.of(cryptoInfo);
            }
            
            // Создание базовой информации о криптовалюте
            String fullName = getDefaultCryptoName(normalizedTicker);
            CryptoCurrency fallbackInfo = CryptoCurrency.builder()
                    .id(normalizedTicker.toLowerCase())
                    .symbol(normalizedTicker)
                    .name(fullName)
                    .build();
            
            successfulFallbackCalls.incrementAndGet();
            log.debug("Создана базовая информация о криптовалюте для {}: {}", ticker, fallbackInfo);
            return Optional.of(fallbackInfo);
            
        } catch (Exception e) {
            log.warn("Ошибка при получении fallback информации о криптовалюте для {}: {}", ticker, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Получает список основных криптовалют.
     * 
     * @return Список основных криптовалют
     */
    public List<CryptoCurrency> getFallbackSupportedCryptocurrencies() {
        totalFallbackCalls.incrementAndGet();
        
        try {
            List<CryptoCurrency> fallbackCryptos = Arrays.asList(
                    CryptoCurrency.builder().id("bitcoin").symbol("BTC").name("Bitcoin").build(),
                    CryptoCurrency.builder().id("ethereum").symbol("ETH").name("Ethereum").build(),
                    CryptoCurrency.builder().id("binancecoin").symbol("BNB").name("BNB").build(),
                    CryptoCurrency.builder().id("cardano").symbol("ADA").name("Cardano").build(),
                    CryptoCurrency.builder().id("solana").symbol("SOL").name("Solana").build(),
                    CryptoCurrency.builder().id("ripple").symbol("XRP").name("XRP").build(),
                    CryptoCurrency.builder().id("polkadot").symbol("DOT").name("Polkadot").build(),
                    CryptoCurrency.builder().id("dogecoin").symbol("DOGE").name("Dogecoin").build(),
                    CryptoCurrency.builder().id("avalanche-2").symbol("AVAX").name("Avalanche").build(),
                    CryptoCurrency.builder().id("polygon").symbol("MATIC").name("Polygon").build()
            );
            
            successfulFallbackCalls.incrementAndGet();
            log.debug("Возвращен список fallback криптовалют: {}", fallbackCryptos.size());
            return fallbackCryptos;
            
        } catch (Exception e) {
            log.warn("Ошибка при получении fallback списка криптовалют: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * Сохраняет данные в кэш для использования в качестве fallback.
     * 
     * @param ticker Тикер криптовалюты
     * @param price Текущая цена для сохранения
     * @param marketData Рыночные данные для сохранения (опционально)
     */
    public void cacheData(String ticker, BigDecimal price, MarketData marketData) {
        try {
            String normalizedTicker = ticker.toUpperCase();
            
            if (price != null) {
                priceCache.put(normalizedTicker, price);
                log.debug("Сохранена цена в кэш для {}: {}", ticker, price);
            }
            
            if (marketData != null) {
                marketDataCache.put(normalizedTicker, marketData);
                log.debug("Сохранены рыночные данные в кэш для {}: {}", ticker, marketData);
            }
            
        } catch (Exception e) {
            log.warn("Ошибка при сохранении данных в кэш для {}: {}", ticker, e.getMessage());
        }
    }
    
    /**
     * Получает цену по умолчанию для тикера.
     * 
     * @param normalizedTicker Нормализованный символ тикера
     * @return Цена по умолчанию, если доступна
     */
    private Optional<BigDecimal> getDefaultPrice(String normalizedTicker) {
        // Базовые цены по умолчанию для основных криптовалют
        Map<String, BigDecimal> defaultPrices = Map.of(
                "BTC", new BigDecimal("45000.00"),
                "ETH", new BigDecimal("3000.00"),
                "BNB", new BigDecimal("300.00"),
                "ADA", new BigDecimal("0.50"),
                "SOL", new BigDecimal("100.00"),
                "XRP", new BigDecimal("0.60"),
                "DOT", new BigDecimal("7.00"),
                "DOGE", new BigDecimal("0.08"),
                "AVAX", new BigDecimal("25.00"),
                "MATIC", new BigDecimal("0.80")
        );
        
        return Optional.ofNullable(defaultPrices.get(normalizedTicker));
    }
    
    /**
     * Получает полное имя криптовалюты по умолчанию.
     * 
     * @param normalizedTicker Нормализованный символ тикера
     * @return Полное имя криптовалюты
     */
    private String getDefaultCryptoName(String normalizedTicker) {
        Map<String, String> defaultNames = Map.of(
                "BTC", "Bitcoin",
                "ETH", "Ethereum",
                "BNB", "BNB",
                "ADA", "Cardano",
                "SOL", "Solana",
                "XRP", "XRP",
                "DOT", "Polkadot",
                "DOGE", "Dogecoin",
                "AVAX", "Avalanche",
                "MATIC", "Polygon"
        );
        
        return defaultNames.getOrDefault(normalizedTicker, normalizedTicker);
    }
    
    /**
     * Получает статистику fallback сервиса.
     * 
     * @return Статистика fallback сервиса
     */
    public Map<String, Object> getFallbackStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFallbackCalls", totalFallbackCalls.get());
        stats.put("successfulFallbackCalls", successfulFallbackCalls.get());
        stats.put("circuitState", circuitState.name());
        stats.put("failureCount", failureCount.get());
        stats.put("successCount", successCount.get());
        stats.put("cachedPrices", priceCache.size());
        stats.put("cachedMarketData", marketDataCache.size());
        stats.put("cachedCryptoInfo", cryptoInfoCache.size());
        
        return stats;
    }
} 