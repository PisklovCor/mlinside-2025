package com.cryptoagents.model.dto;

import com.cryptoagents.model.enums.TimePeriod;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object, представляющий исторические данные цен криптовалют.
 * 
 * Этот класс содержит временные ряды данных для криптовалюты за указанный период,
 * включая ценовые точки, временные метки и статистическую информацию.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoricalData {

    private String ticker;
    private TimePeriod period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<PricePoint> pricePoints = new ArrayList<>();
    private LocalDateTime retrievedAt = LocalDateTime.now();
    
    // Статистические данные (вычисляемые, без сеттеров)
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal minPrice;
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal maxPrice;
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal averagePrice;
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal startPrice;
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal endPrice;
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal totalChange;
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal totalChangePercentage;

    // Конструктор с базовыми полями
    public HistoricalData(String ticker, TimePeriod period) {
        this.ticker = ticker;
        this.period = period;
        this.pricePoints = new ArrayList<>();
        this.retrievedAt = LocalDateTime.now();
    }

    /**
     * Внутренний класс, представляющий одну ценовую точку во времени.
     */
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PricePoint {
        private LocalDateTime timestamp;
        private BigDecimal price;
        private BigDecimal volume;

        public PricePoint(LocalDateTime timestamp, BigDecimal price) {
            this.timestamp = timestamp;
            this.price = price;
        }

        public PricePoint(LocalDateTime timestamp, BigDecimal price, BigDecimal volume) {
            this.timestamp = timestamp;
            this.price = price;
            this.volume = volume;
        }
    }

    // Пользовательский сеттер для pricePoints для запуска вычисления статистики
    public void setPricePoints(List<PricePoint> pricePoints) {
        this.pricePoints = pricePoints;
        calculateStatistics();
    }

    // Утилитарные методы

    /**
     * Добавляет ценовую точку к историческим данным и пересчитывает статистику.
     * 
     * @param pricePoint ценовая точка для добавления
     */
    public void addPricePoint(PricePoint pricePoint) {
        if (pricePoints == null) {
            pricePoints = new ArrayList<>();
        }
        pricePoints.add(pricePoint);
        calculateStatistics();
    }

    /**
     * Вычисляет статистические данные из ценовых точек.
     */
    private void calculateStatistics() {
        if (pricePoints == null || pricePoints.isEmpty()) {
            return;
        }

        // Находим минимальную и максимальную цены
        minPrice = pricePoints.stream()
                .map(PricePoint::getPrice)
                .filter(price -> price != null)
                .min(BigDecimal::compareTo)
                .orElse(null);

        maxPrice = pricePoints.stream()
                .map(PricePoint::getPrice)
                .filter(price -> price != null)
                .max(BigDecimal::compareTo)
                .orElse(null);

        // Вычисляем среднюю цену
        List<BigDecimal> validPrices = pricePoints.stream()
                .map(PricePoint::getPrice)
                .filter(price -> price != null)
                .collect(Collectors.toList());

        if (!validPrices.isEmpty()) {
            BigDecimal sum = validPrices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            averagePrice = sum.divide(BigDecimal.valueOf(validPrices.size()), 4, BigDecimal.ROUND_HALF_UP);
        }

        // Устанавливаем начальную и конечную цены
        if (!pricePoints.isEmpty()) {
            startPrice = pricePoints.get(0).getPrice();
            endPrice = pricePoints.get(pricePoints.size() - 1).getPrice();

            // Вычисляем общее изменение
            if (startPrice != null && endPrice != null) {
                totalChange = endPrice.subtract(startPrice);
                if (startPrice.compareTo(BigDecimal.ZERO) > 0) {
                    totalChangePercentage = totalChange.divide(startPrice, 4, BigDecimal.ROUND_HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                }
            }
        }

        // Устанавливаем даты начала и конца
        if (!pricePoints.isEmpty()) {
            startDate = pricePoints.get(0).getTimestamp();
            endDate = pricePoints.get(pricePoints.size() - 1).getTimestamp();
        }
    }

    /**
     * Возвращает количество точек данных в этих исторических данных.
     * 
     * @return Количество ценовых точек
     */
    public int getDataPointsCount() {
        return pricePoints != null ? pricePoints.size() : 0;
    }

    /**
     * Проверяет, содержат ли эти исторические данные валидную ценовую информацию.
     * 
     * @return true если есть ценовые точки с валидными данными
     */
    public boolean hasValidData() {
        return pricePoints != null && !pricePoints.isEmpty() &&
               pricePoints.stream().anyMatch(point -> point.getPrice() != null);
    }

    /**
     * Возвращает true, если цена увеличилась за период.
     * 
     * @return true если конечная цена выше начальной
     */
    public boolean isPriceIncreasing() {
        return startPrice != null && endPrice != null && 
               endPrice.compareTo(startPrice) > 0;
    }

    /**
     * Возвращает список цен из всех ценовых точек.
     * 
     * @return Список цен
     */
    public List<BigDecimal> getPrices() {
        if (pricePoints == null) {
            return new ArrayList<>();
        }
        return pricePoints.stream()
                .map(PricePoint::getPrice)
                .filter(price -> price != null)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "HistoricalData{" +
                "ticker='" + ticker + '\'' +
                ", period=" + period +
                ", dataPoints=" + getDataPointsCount() +
                ", startPrice=" + startPrice +
                ", endPrice=" + endPrice +
                ", totalChange=" + totalChange +
                ", totalChangePercentage=" + totalChangePercentage +
                '}';
    }
} 