package com.cryptoagents.old.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Класс конфигурации для RestClient бинов, используемых для внешних API вызовов.
 * 
 * Эта конфигурация предоставляет предварительно настроенные экземпляры RestClient
 * с соответствующими таймаутами и обработкой ошибок для интеграции с API криптовалют.
 */
@Slf4j
@Configuration
public class RestClientConfig {

    /**
     * Создает RestClient бин, настроенный для вызовов CoinGecko API.
     * 
     * @return Настроенный экземпляр RestClient для CoinGecko API
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
     * Создает универсальный RestClient бин для других внешних API вызовов.
     * 
     * @return Настроенный экземпляр RestClient для общего использования
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