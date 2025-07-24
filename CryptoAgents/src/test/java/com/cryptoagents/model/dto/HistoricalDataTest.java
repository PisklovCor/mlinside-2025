package com.cryptoagents.model.dto;

import com.cryptoagents.model.enums.TimePeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HistoricalData DTO Tests")
class HistoricalDataTest {

    private HistoricalData historicalData;

    @BeforeEach
    void setUp() {
        historicalData = new HistoricalData("BTC", TimePeriod.ONE_WEEK);
    }

    @Test
    @DisplayName("Should create HistoricalData with ticker and period")
    void testConstructor() {
        assertNotNull(historicalData);
        assertEquals("BTC", historicalData.getTicker());
        assertEquals(TimePeriod.ONE_WEEK, historicalData.getPeriod());
        assertNotNull(historicalData.getRetrievedAt());
        assertNotNull(historicalData.getPricePoints());
        assertTrue(historicalData.getPricePoints().isEmpty());
    }

    @Test
    @DisplayName("Should add price points correctly")
    void testAddPricePoint() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        BigDecimal price = new BigDecimal("50000.00");
        BigDecimal volume = new BigDecimal("1000000.00");
        
        HistoricalData.PricePoint pricePoint = new HistoricalData.PricePoint(timestamp, price, volume);

        // When
        historicalData.addPricePoint(pricePoint);

        // Then
        assertEquals(1, historicalData.getDataPointsCount());
        assertEquals(1, historicalData.getPricePoints().size());
        assertEquals(pricePoint, historicalData.getPricePoints().get(0));
    }

    @Test
    @DisplayName("Should calculate statistics correctly when adding multiple points")
    void testStatisticsCalculation() {
        // Given
        LocalDateTime baseTime = LocalDateTime.now();
        
        // When - Add multiple price points (statistics calculated automatically)
        historicalData.addPricePoint(new HistoricalData.PricePoint(
            baseTime.minusDays(3), new BigDecimal("45000.00")));
        historicalData.addPricePoint(new HistoricalData.PricePoint(
            baseTime.minusDays(2), new BigDecimal("47000.00")));
        historicalData.addPricePoint(new HistoricalData.PricePoint(
            baseTime.minusDays(1), new BigDecimal("49000.00")));
        historicalData.addPricePoint(new HistoricalData.PricePoint(
            baseTime, new BigDecimal("52000.00")));

        // Then - Statistics should be automatically calculated
        assertEquals(new BigDecimal("45000.00"), historicalData.getMinPrice());
        assertEquals(new BigDecimal("52000.00"), historicalData.getMaxPrice());
        assertEquals(new BigDecimal("45000.00"), historicalData.getStartPrice());
        assertEquals(new BigDecimal("52000.00"), historicalData.getEndPrice());
        
        // Average should be calculated correctly
        assertNotNull(historicalData.getAveragePrice());
        
        // Total change
        assertEquals(new BigDecimal("7000.00"), historicalData.getTotalChange());
        
        // Percentage change should be positive
        assertTrue(historicalData.getTotalChangePercentage().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should handle empty data set")
    void testEmptyDataSet() {
        // When - no price points added
        // Then - statistics should be null for empty data
        assertNull(historicalData.getMinPrice());
        assertNull(historicalData.getMaxPrice());
        assertNull(historicalData.getStartPrice());
        assertNull(historicalData.getEndPrice());
        assertNull(historicalData.getAveragePrice());
        assertNull(historicalData.getTotalChange());
        assertNull(historicalData.getTotalChangePercentage());
        assertFalse(historicalData.hasValidData());
    }

    @Test
    @DisplayName("Should handle single price point")
    void testSinglePricePoint() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        BigDecimal price = new BigDecimal("50000.00");
        
        // When
        historicalData.addPricePoint(new HistoricalData.PricePoint(timestamp, price));

        // Then - Statistics should be calculated for single point
        assertEquals(price, historicalData.getMinPrice());
        assertEquals(price, historicalData.getMaxPrice());
        assertEquals(price, historicalData.getStartPrice());
        assertEquals(price, historicalData.getEndPrice());
        assertEquals(price, historicalData.getAveragePrice());
        assertEquals(BigDecimal.ZERO, historicalData.getTotalChange());
        assertEquals(BigDecimal.ZERO, historicalData.getTotalChangePercentage());
        assertTrue(historicalData.hasValidData());
    }

    @Test
    @DisplayName("Should detect valid data correctly")
    void testHasValidData() {
        // Initially no data
        assertFalse(historicalData.hasValidData());
        
        // Add one price point
        historicalData.addPricePoint(new HistoricalData.PricePoint(
            LocalDateTime.now(), new BigDecimal("50000.00")));
        
        assertTrue(historicalData.hasValidData());
    }

    @Test
    @DisplayName("Should detect price increase correctly")
    void testIsPriceIncreasing() {
        // Given - increasing prices
        LocalDateTime baseTime = LocalDateTime.now();
        historicalData.addPricePoint(new HistoricalData.PricePoint(
            baseTime.minusDays(1), new BigDecimal("45000.00")));
        historicalData.addPricePoint(new HistoricalData.PricePoint(
            baseTime, new BigDecimal("50000.00")));

        // Then
        assertTrue(historicalData.isPriceIncreasing());
    }

    @Test
    @DisplayName("Should detect price decrease correctly")
    void testIsPriceDecreasing() {
        // Given - decreasing prices
        LocalDateTime baseTime = LocalDateTime.now();
        historicalData.addPricePoint(new HistoricalData.PricePoint(
            baseTime.minusDays(1), new BigDecimal("55000.00")));
        historicalData.addPricePoint(new HistoricalData.PricePoint(
            baseTime, new BigDecimal("50000.00")));

        // Then
        assertFalse(historicalData.isPriceIncreasing());
    }

    @Test
    @DisplayName("Should handle date range correctly")
    void testDateRange() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        
        historicalData.setStartDate(start);
        historicalData.setEndDate(end);

        // Then
        assertEquals(start, historicalData.getStartDate());
        assertEquals(end, historicalData.getEndDate());
    }

    @Test
    @DisplayName("PricePoint should work correctly")
    void testPricePoint() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        BigDecimal price = new BigDecimal("50000.00");
        BigDecimal volume = new BigDecimal("1000000.00");

        // When
        HistoricalData.PricePoint pricePoint = new HistoricalData.PricePoint(timestamp, price, volume);

        // Then
        assertEquals(timestamp, pricePoint.getTimestamp());
        assertEquals(price, pricePoint.getPrice());
        assertEquals(volume, pricePoint.getVolume());
    }

    @Test
    @DisplayName("PricePoint should work without volume")
    void testPricePointWithoutVolume() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        BigDecimal price = new BigDecimal("50000.00");

        // When
        HistoricalData.PricePoint pricePoint = new HistoricalData.PricePoint(timestamp, price);

        // Then
        assertEquals(timestamp, pricePoint.getTimestamp());
        assertEquals(price, pricePoint.getPrice());
        assertNull(pricePoint.getVolume());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void testToString() {
        // Given
        historicalData.addPricePoint(new HistoricalData.PricePoint(
            LocalDateTime.now(), new BigDecimal("50000.00")));

        // When
        String result = historicalData.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("BTC"));
        assertTrue(result.contains("ONE_WEEK"));
        assertTrue(result.contains("1"));
    }
} 