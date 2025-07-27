package com.cryptoagents.old.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for rate limiting using bucket4j
 */
@Configuration
public class RateLimitConfig {
    
    @Value("${crypto.rate.limit.capacity:20}")
    private long capacity;
    
    @Value("${crypto.rate.limit.refill-tokens:2}")
    private long refillTokens;
    
    @Value("${crypto.rate.limit.refill-duration:60}")
    private long refillDurationSeconds;
    
    @Bean
    public Bucket createBucket() {
        Duration refillDuration = Duration.ofSeconds(refillDurationSeconds);
        
        Refill refill = Refill.intervally(refillTokens, refillDuration);
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        
        return Bucket.builder().addLimit(limit).build();
    }
} 