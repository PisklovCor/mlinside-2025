package com.cryptoagents.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Класс конфигурации для инфраструктуры кэширования.
 * 
 * Этот класс настраивает механизмы кэширования для улучшения производительности
 * операций получения и анализа данных криптовалют.
 * 
 * Категории кэша:
 * - Данные цен: Краткосрочный кэш для текущих цен (TTL 1 минута)
 * - Рыночные данные: Среднесрочный кэш для рыночной информации (TTL 5 минут)
 * - Исторические данные: Долгосрочный кэш для исторических данных цен (TTL 15 минут)
 * - Результаты анализа: Кэш для результатов анализа агентов (TTL 10 минут)
 */
@Configuration
public class CacheConfig {

    // Имена кэшей как константы
    public static final String PRICE_CACHE = "priceData";
    public static final String MARKET_CACHE = "marketData";
    public static final String HISTORICAL_CACHE = "historicalData";
    public static final String ANALYSIS_CACHE = "analysisResults";
    public static final String API_RESPONSE_CACHE = "apiResponseCache";

    // Значения TTL в миллисекундах
    private static final long PRICE_CACHE_TTL = 60_000L;        // 1 минута
    private static final long MARKET_CACHE_TTL = 300_000L;      // 5 минут
    private static final long HISTORICAL_CACHE_TTL = 900_000L;  // 15 минут
    private static final long ANALYSIS_CACHE_TTL = 600_000L;    // 10 минут
    private static final long API_RESPONSE_TTL = 180_000L;      // 3 минуты

    /**
     * Создает и настраивает менеджер кэша с несколькими именованными кэшами.
     * Каждый кэш имеет различные настройки TTL, оптимизированные для типа данных.
     * 
     * @return Настроенный менеджер кэша
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        
        cacheManager.setCaches(Arrays.asList(
            createTTLCache(PRICE_CACHE, PRICE_CACHE_TTL),
            createTTLCache(MARKET_CACHE, MARKET_CACHE_TTL),
            createTTLCache(HISTORICAL_CACHE, HISTORICAL_CACHE_TTL),
            createTTLCache(ANALYSIS_CACHE, ANALYSIS_CACHE_TTL),
            createTTLCache(API_RESPONSE_CACHE, API_RESPONSE_TTL)
        ));
        
        return cacheManager;
    }

    /**
     * Создает кэш с поддержкой TTL (Time To Live).
     * 
     * @param name Имя кэша
     * @param ttlMs Время жизни в миллисекундах
     * @return Настроенный кэш с поддержкой TTL
     */
    private Cache createTTLCache(String name, long ttlMs) {
        return new TTLConcurrentMapCache(name, ttlMs);
    }

    /**
     * Пользовательская реализация кэша с поддержкой TTL (Time To Live).
     * Оборачивает значения с временными метками истечения для автоматической очистки.
     */
    private static class TTLConcurrentMapCache extends ConcurrentMapCache {
        private final long ttlMs;

        public TTLConcurrentMapCache(String name, long ttlMs) {
            super(name, true);
            this.ttlMs = ttlMs;
        }

        @Override
        public void put(Object key, Object value) {
            long expirationTime = System.currentTimeMillis() + ttlMs;
            super.put(key, new TTLValueWrapper(value, expirationTime));
        }

        @Override
        public ValueWrapper get(Object key) {
            ValueWrapper wrapper = super.get(key);
            if (wrapper != null && wrapper.get() instanceof TTLValueWrapper) {
                TTLValueWrapper ttlWrapper = (TTLValueWrapper) wrapper.get();
                if (System.currentTimeMillis() > ttlWrapper.getExpirationTime()) {
                    evict(key);
                    return null;
                }
                return () -> ttlWrapper.getValue();
            }
            return wrapper;
        }

        @Override
        public <T> T get(Object key, Class<T> type) {
            ValueWrapper wrapper = get(key);
            return wrapper != null ? (T) wrapper.get() : null;
        }

        /**
         * Gets cache statistics for monitoring purposes.
         * 
         * @return Basic cache statistics
         */
        public CacheStats getStats() {
            int size = getNativeCache().size();
            long expiredCount = 0;
            long currentTime = System.currentTimeMillis();
            
            // Count expired entries (for monitoring)
            for (Object value : getNativeCache().values()) {
                if (value instanceof TTLValueWrapper) {
                    TTLValueWrapper wrapper = (TTLValueWrapper) value;
                    if (currentTime > wrapper.getExpirationTime()) {
                        expiredCount++;
                    }
                }
            }
            
            return new CacheStats(size, expiredCount);
        }

        /**
         * Cleans up expired entries manually (optional maintenance operation).
         */
        public void cleanupExpired() {
            long currentTime = System.currentTimeMillis();
            
            getNativeCache().entrySet().removeIf(entry -> {
                Object value = entry.getValue();
                if (value instanceof TTLValueWrapper) {
                    TTLValueWrapper wrapper = (TTLValueWrapper) value;
                    return currentTime > wrapper.getExpirationTime();
                }
                return false;
            });
        }
    }

    /**
     * Wrapper class to store values with expiration time.
     */
    @Getter
    @AllArgsConstructor
    private static class TTLValueWrapper {
        private final Object value;
        private final long expirationTime;
    }

    /**
     * Simple cache statistics holder.
     */
    @Getter
    @AllArgsConstructor
    public static class CacheStats {
        private final int size;
        private final long expiredCount;

        @Override
        public String toString() {
            return String.format("CacheStats{size=%d, expired=%d}", size, expiredCount);
        }
    }
} 