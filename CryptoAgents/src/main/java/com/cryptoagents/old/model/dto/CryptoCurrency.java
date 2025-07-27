package com.cryptoagents.old.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object, представляющий базовую информацию о криптовалюте.
 * 
 * Этот класс используется для передачи данных криптовалюты между слоями
 * и может быть сериализован/десериализован из JSON ответов API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "retrievedAt")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoCurrency {

    @JsonProperty("id")
    private String id;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("name")
    private String name;

    @JsonProperty("current_price")
    private BigDecimal currentPrice;

    @JsonProperty("market_cap")
    private BigDecimal marketCap;

    @JsonProperty("market_cap_rank")
    private Integer marketCapRank;

    @JsonProperty("total_volume")
    private BigDecimal totalVolume;

    @JsonProperty("price_change_24h")
    private BigDecimal priceChange24h;

    @JsonProperty("price_change_percentage_24h")
    private BigDecimal priceChangePercentage24h;

    @JsonProperty("last_updated")
    private String lastUpdated;

    private LocalDateTime retrievedAt = LocalDateTime.now();

    // Конструктор с обязательными полями
    public CryptoCurrency(String id, String symbol, String name, BigDecimal currentPrice) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
        this.retrievedAt = LocalDateTime.now();
    }

    // Утилитарные методы

    /**
     * Возвращает тикер в верхнем регистре.
     * 
     * @return Тикер в верхнем регистре
     * @throws NullPointerException если symbol равен null
     */
    public String getTickerUpperCase() {
        if (symbol == null) {
            throw new NullPointerException("Symbol cannot be null");
        }
        return symbol.toUpperCase();
    }

    /**
     * Проверяет, имеет ли эта криптовалюта валидные данные о цене.
     * 
     * @return true если текущая цена не null и положительная
     */
    public boolean hasValidPrice() {
        return currentPrice != null && currentPrice.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Проверяет, увеличилась ли цена за последние 24 часа.
     * Использует priceChange24h, если priceChangePercentage24h недоступен.
     * 
     * @return true если изменение цены положительное
     */
    public boolean isPriceIncreasing() {
        // Сначала пробуем процентное изменение, затем абсолютное изменение
        if (priceChangePercentage24h != null) {
            return priceChangePercentage24h.compareTo(BigDecimal.ZERO) > 0;
        }
        if (priceChange24h != null) {
            return priceChange24h.compareTo(BigDecimal.ZERO) > 0;
        }
        return false;
    }
} 