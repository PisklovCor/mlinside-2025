package com.cryptoagents.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for Spring Cache management.
 * 
 * This configuration sets up different cache regions with appropriate TTL (Time To Live)
 * settings for cryptocurrency data to optimize API calls and respect rate limits.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    // Cache names constants
    public static final String CRYPTO_PRICE_CACHE = "cryptoPrice";
    public static final String CRYPTO_MARKET_DATA_CACHE = "cryptoMarketData";
    public static final String CRYPTO_HISTORICAL_DATA_CACHE = "cryptoHistoricalData";
    public static final String CRYPTO_INFO_CACHE = "cryptoInfo";
    public static final String CRYPTO_SUPPORTED_LIST_CACHE = "cryptoSupportedList";
    public static final String CRYPTO_SERVICE_STATUS_CACHE = "cryptoServiceStatus";

    /**
     * Creates and configures the main cache manager with different cache regions.
     * 
     * @return Configured CacheManager with TTL-enabled caches
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        
        cacheManager.setCaches(Arrays.asList(
            // Price cache - 5 minutes TTL (frequent updates needed)
            createTTLCache(CRYPTO_PRICE_CACHE, TimeUnit.MINUTES.toMillis(5)),
            
            // Market data cache - 10 minutes TTL (moderate update frequency)
            createTTLCache(CRYPTO_MARKET_DATA_CACHE, TimeUnit.MINUTES.toMillis(10)),
            
            // Historical data cache - 1 hour TTL (stable data, expensive to fetch)
            createTTLCache(CRYPTO_HISTORICAL_DATA_CACHE, TimeUnit.HOURS.toMillis(1)),
            
            // Crypto info cache - 1 hour TTL (rarely changes)
            createTTLCache(CRYPTO_INFO_CACHE, TimeUnit.HOURS.toMillis(1)),
            
            // Supported cryptocurrencies list cache - 24 hours TTL (very stable)
            createTTLCache(CRYPTO_SUPPORTED_LIST_CACHE, TimeUnit.HOURS.toMillis(24)),
            
            // Service status cache - 2 minutes TTL (health check)
            createTTLCache(CRYPTO_SERVICE_STATUS_CACHE, TimeUnit.MINUTES.toMillis(2))
        ));
        
        return cacheManager;
    }

    /**
     * Creates a TTL-enabled cache with the specified name and expiration time.
     * 
     * @param cacheName The name of the cache
     * @param ttlMillis Time to live in milliseconds
     * @return TTL-enabled ConcurrentMapCache
     */
    private TTLConcurrentMapCache createTTLCache(String cacheName, long ttlMillis) {
        return new TTLConcurrentMapCache(cacheName, ttlMillis);
    }

    /**
     * Custom ConcurrentMapCache implementation with TTL (Time To Live) support.
     * 
     * This cache automatically expires entries after the specified TTL period,
     * ensuring that cryptocurrency data doesn't become stale.
     */
    public static class TTLConcurrentMapCache extends ConcurrentMapCache {
        
        private final long ttlMillis;

        public TTLConcurrentMapCache(String name, long ttlMillis) {
            super(name);
            this.ttlMillis = ttlMillis;
        }

        @Override
        public void put(Object key, Object value) {
            if (value != null) {
                TTLValueWrapper wrapper = new TTLValueWrapper(value, System.currentTimeMillis() + ttlMillis);
                super.put(key, wrapper);
            }
        }

        @Override
        public ValueWrapper get(Object key) {
            ValueWrapper wrapper = super.get(key);
            
            if (wrapper != null && wrapper.get() instanceof TTLValueWrapper) {
                TTLValueWrapper ttlWrapper = (TTLValueWrapper) wrapper.get();
                
                if (System.currentTimeMillis() > ttlWrapper.getExpirationTime()) {
                    // Entry has expired, remove it and return null
                    evict(key);
                    return null;
                }
                
                // Return the actual value wrapped in a new ValueWrapper
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
    private static class TTLValueWrapper {
        private final Object value;
        private final long expirationTime;

        public TTLValueWrapper(Object value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }

        public Object getValue() {
            return value;
        }

        public long getExpirationTime() {
            return expirationTime;
        }
    }

    /**
     * Simple cache statistics holder.
     */
    public static class CacheStats {
        private final int size;
        private final long expiredCount;

        public CacheStats(int size, long expiredCount) {
            this.size = size;
            this.expiredCount = expiredCount;
        }

        public int getSize() {
            return size;
        }

        public long getExpiredCount() {
            return expiredCount;
        }

        @Override
        public String toString() {
            return String.format("CacheStats{size=%d, expired=%d}", size, expiredCount);
        }
    }
} 