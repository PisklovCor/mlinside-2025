package com.cryptoagents.old.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object, представляющий комплексные рыночные данные для криптовалюты.
 * 
 * Этот класс содержит детальную рыночную информацию, включая цены, объемы,
 * рыночную капитализацию и различные торговые метрики.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketData {

    // Базовая идентификация
    private String ticker;
    private String name;
    private String id;

    // Данные о ценах
    @JsonProperty("current_price")
    private BigDecimal currentPrice;

    @JsonProperty("price_change_24h")
    private BigDecimal priceChange24h;

    @JsonProperty("price_change_percentage_24h")
    private BigDecimal priceChangePercentage24h;

    @JsonProperty("price_change_percentage_7d")
    private BigDecimal priceChangePercentage7d;

    @JsonProperty("price_change_percentage_30d")
    private BigDecimal priceChangePercentage30d;

    // Данные о рыночной капитализации
    @JsonProperty("market_cap")
    private BigDecimal marketCap;

    @JsonProperty("market_cap_rank")
    private Integer marketCapRank;

    @JsonProperty("market_cap_change_24h")
    private BigDecimal marketCapChange24h;

    @JsonProperty("market_cap_change_percentage_24h")
    private BigDecimal marketCapChangePercentage24h;

    // Данные об объеме
    @JsonProperty("total_volume")
    private BigDecimal totalVolume;

    @JsonProperty("volume_change_24h")
    private BigDecimal volumeChange24h;

    // Данные о максимумах/минимумах
    @JsonProperty("high_24h")
    private BigDecimal high24h;

    @JsonProperty("low_24h")
    private BigDecimal low24h;

    @JsonProperty("ath")
    private BigDecimal allTimeHigh;

    @JsonProperty("ath_change_percentage")
    private BigDecimal athChangePercentage;

    @JsonProperty("ath_date")
    private String athDate;

    @JsonProperty("atl")
    private BigDecimal allTimeLow;

    @JsonProperty("atl_change_percentage")
    private BigDecimal atlChangePercentage;

    @JsonProperty("atl_date")
    private String atlDate;

    // Данные о предложении
    @JsonProperty("circulating_supply")
    private BigDecimal circulatingSupply;

    @JsonProperty("total_supply")
    private BigDecimal totalSupply;

    @JsonProperty("max_supply")
    private BigDecimal maxSupply;

    // Дополнительные метрики
    @JsonProperty("fully_diluted_valuation")
    private BigDecimal fullyDilutedValuation;

    @JsonProperty("last_updated")
    private String lastUpdated;

    // Время получения данных
    private LocalDateTime retrievedAt = LocalDateTime.now();

    /**
     * Конструктор для создания объекта с базовыми данными
     */
    public MarketData(String ticker, String name, BigDecimal currentPrice) {
        this.ticker = ticker;
        this.name = name;
        this.currentPrice = currentPrice;
    }

    /**
     * Вычисляет волатильность цены на основе диапазона максимум/минимум за 24 часа.
     * 
     * @return Процент волатильности, или null если данные недоступны
     */
    public BigDecimal getVolatility24h() {
        if (high24h != null && low24h != null && high24h.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal range = high24h.subtract(low24h);
            return range.divide(high24h, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return null;
    }

    /**
     * Проверяет, является ли эта криптовалюта в данный момент бычьей (цена растет).
     * 
     * @return true если изменение цены за 24 часа положительное
     */
    public boolean isBullish() {
        return priceChangePercentage24h != null && 
               priceChangePercentage24h.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Проверяет, имеет ли эта криптовалюта высокий торговый объем.
     * 
     * @return true если отношение объема к рыночной капитализации выше 5%
     */
    public boolean hasHighVolume() {
        if (totalVolume != null && marketCap != null && 
            marketCap.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal volumeRatio = totalVolume.divide(marketCap, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            return volumeRatio.compareTo(BigDecimal.valueOf(5)) > 0;
        }
        return false;
    }

    /**
     * Проверяет, находится ли текущая цена близко к историческому максимуму (в пределах 10%).
     * 
     * @return true если текущая цена в пределах 10% от исторического максимума
     */
    public boolean isNearAllTimeHigh() {
        if (currentPrice != null && allTimeHigh != null && 
            allTimeHigh.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal difference = allTimeHigh.subtract(currentPrice);
            BigDecimal percentage = difference.divide(allTimeHigh, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            return percentage.compareTo(BigDecimal.valueOf(10)) <= 0;
        }
        return false;
    }

    /**
     * Проверяет, имеет ли эта криптовалюта ограниченное предложение.
     * 
     * @return true если максимальное предложение определено и конечно
     */
    public boolean hasLimitedSupply() {
        return maxSupply != null && maxSupply.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Вычисляет процент использования предложения.
     * 
     * @return Процент предложения в обращении, или null если данные недоступны
     */
    public BigDecimal getSupplyUtilization() {
        if (circulatingSupply != null && maxSupply != null && 
            maxSupply.compareTo(BigDecimal.ZERO) > 0) {
            return circulatingSupply.divide(maxSupply, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return null;
    }

    /**
     * Возвращает 24-часовой торговый объем.
     * 
     * @return 24-часовой объем, или null если недоступен
     */
    public BigDecimal getVolume24h() {
        return totalVolume;
    }
} 