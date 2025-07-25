package com.cryptoagents.service;

import com.cryptoagents.model.dto.CryptoCurrency;
import com.cryptoagents.model.dto.HistoricalData;
import com.cryptoagents.model.dto.MarketData;
import com.cryptoagents.model.enums.TimePeriod;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для получения данных о криптовалютах из внешних API.
 * 
 * Этот интерфейс определяет методы для получения текущих цен, исторических данных,
 * рыночной информации и базовой информации о криптовалютах.
 */
public interface CryptoDataService {
    
    /**
     * Получает текущую цену криптовалюты в USD.
     * 
     * @param ticker Символ тикера криптовалюты (например, "BTC", "ETH")
     * @return Текущая цена в USD, или пустое значение, если недоступна
     * @throws IllegalArgumentException если тикер равен null или пустой
     */
    Optional<BigDecimal> getCurrentPrice(String ticker);
    
    /**
     * Получает исторические данные о ценах криптовалюты.
     * 
     * @param ticker Символ тикера криптовалюты
     * @param period Временной период для исторических данных
     * @return Исторические данные, содержащие точки цен, или пустое значение, если недоступны
     * @throws IllegalArgumentException если тикер равен null/пустой или период равен null
     */
    Optional<HistoricalData> getHistoricalData(String ticker, TimePeriod period);
    
    /**
     * Получает рыночные данные криптовалюты.
     * 
     * @param ticker Символ тикера криптовалюты
     * @return Рыночные данные, включающие цену, объем, рыночную капитализацию и т.д., или пустое значение, если недоступны
     * @throws IllegalArgumentException если тикер равен null или пустой
     */
    Optional<MarketData> getMarketData(String ticker);
    
    /**
     * Получает базовую информацию о криптовалюте.
     * 
     * @param ticker Символ тикера криптовалюты
     * @return Базовая информация о криптовалюте, или пустое значение, если недоступна
     * @throws IllegalArgumentException если тикер равен null или пустой
     */
    Optional<CryptoCurrency> getCryptoInfo(String ticker);
    
    /**
     * Получает список поддерживаемых криптовалют.
     * 
     * @return Список поддерживаемых криптовалют с базовой информацией
     */
    List<CryptoCurrency> getSupportedCryptocurrencies();
    
    /**
     * Проверяет доступность сервиса.
     * 
     * @return true, если сервис доступен, false в противном случае
     */
    boolean isServiceAvailable();
    
    /**
     * Проверяет, поддерживается ли указанный тикер.
     * 
     * @param ticker Символ тикера криптовалюты для валидации
     * @return true, если тикер поддерживается, false в противном случае
     */
    boolean isTickerSupported(String ticker);
} 