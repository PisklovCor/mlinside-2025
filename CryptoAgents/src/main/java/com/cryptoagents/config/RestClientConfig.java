package com.cryptoagents.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Configuration class for RestClient beans used for external API calls.
 * 
 * This configuration provides pre-configured RestClient instances
 * with appropriate timeouts and error handling for cryptocurrency API integration.
 */
@Slf4j
@Configuration
public class RestClientConfig {

    /**
     * Creates a RestClient bean configured for CoinGecko API calls.
     * 
     * @return Configured RestClient instance for CoinGecko API
     */
    @Bean("coinGeckoRestClient")
    public RestClient coinGeckoRestClient() {
        log.info("🌐 Configuring CoinGecko RestClient with timeouts: connect=10s, read=30s");
        
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setConnectionRequestTimeout(Duration.ofSeconds(30));
        
        return RestClient.builder()
                .baseUrl("https://api.coingecko.com/api/v3")
                .requestFactory(factory)
                .build();
    }

    /**
     * Creates a general purpose RestClient bean for other external API calls.
     * 
     * @return Configured RestClient instance for general use
     */
    @Bean("generalRestClient")
    public RestClient generalRestClient() {
        log.info("🌐 Configuring general RestClient with timeouts: connect=5s, read=15s");
        
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setConnectionRequestTimeout(Duration.ofSeconds(15));
        
        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
} 