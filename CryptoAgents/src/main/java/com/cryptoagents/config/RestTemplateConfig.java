package com.cryptoagents.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration class for RestTemplate beans used for external API calls.
 * 
 * This configuration provides pre-configured RestTemplate instances
 * with appropriate timeouts and error handling for cryptocurrency API integration.
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    /**
     * Creates a RestTemplate bean configured for CoinGecko API calls.
     * 
     * @param builder RestTemplateBuilder provided by Spring Boot
     * @return Configured RestTemplate instance
     */
    @Bean("coinGeckoRestTemplate")
    public RestTemplate coinGeckoRestTemplate(RestTemplateBuilder builder) {
        log.info("🌐 Configuring CoinGecko RestTemplate with timeouts: connect=10s, read=30s");
        return builder
                .rootUri("https://api.coingecko.com/api/v3")
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .requestFactory(HttpComponentsClientHttpRequestFactory.class)
                .build();
    }

    /**
     * Creates a general purpose RestTemplate bean for other external API calls.
     * 
     * @param builder RestTemplateBuilder provided by Spring Boot
     * @return Configured RestTemplate instance
     */
    @Bean("generalRestTemplate")
    public RestTemplate generalRestTemplate(RestTemplateBuilder builder) {
        log.info("🌐 Configuring general RestTemplate with timeouts: connect=5s, read=15s");
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(15))
                .requestFactory(HttpComponentsClientHttpRequestFactory.class)
                .build();
    }
} 