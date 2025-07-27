//package com.cryptoagents.model.dto;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DisplayName("CryptoCurrency DTO Tests")
//class CryptoCurrencyTest {
//
//    private CryptoCurrency cryptoCurrency;
//
//    @BeforeEach
//    void setUp() {
//        cryptoCurrency = new CryptoCurrency();
//    }
//
//    @Test
//    @DisplayName("Should create empty CryptoCurrency object")
//    void testDefaultConstructor() {
//        assertNotNull(cryptoCurrency);
//        assertNull(cryptoCurrency.getId());
//        assertNull(cryptoCurrency.getSymbol());
//        assertNull(cryptoCurrency.getName());
//        assertNull(cryptoCurrency.getCurrentPrice());
//    }
//
//    @Test
//    @DisplayName("Should set and get all properties correctly")
//    void testGettersAndSetters() {
//        // Given
//        String id = "bitcoin";
//        String symbol = "BTC";
//        String name = "Bitcoin";
//        BigDecimal currentPrice = new BigDecimal("50000.00");
//        BigDecimal marketCap = new BigDecimal("1000000000");
//        Integer marketCapRank = 1;
//        BigDecimal totalVolume = new BigDecimal("20000000");
//        BigDecimal priceChange24h = new BigDecimal("1000.00");
//        BigDecimal priceChangePercentage24h = new BigDecimal("2.5");
//        String lastUpdated = "2024-01-01T12:00:00Z";
//        LocalDateTime retrievedAt = LocalDateTime.now();
//
//        // When
//        cryptoCurrency.setId(id);
//        cryptoCurrency.setSymbol(symbol);
//        cryptoCurrency.setName(name);
//        cryptoCurrency.setCurrentPrice(currentPrice);
//        cryptoCurrency.setMarketCap(marketCap);
//        cryptoCurrency.setMarketCapRank(marketCapRank);
//        cryptoCurrency.setTotalVolume(totalVolume);
//        cryptoCurrency.setPriceChange24h(priceChange24h);
//        cryptoCurrency.setPriceChangePercentage24h(priceChangePercentage24h);
//        cryptoCurrency.setLastUpdated(lastUpdated);
//        cryptoCurrency.setRetrievedAt(retrievedAt);
//
//        // Then
//        assertEquals(id, cryptoCurrency.getId());
//        assertEquals(symbol, cryptoCurrency.getSymbol());
//        assertEquals(name, cryptoCurrency.getName());
//        assertEquals(currentPrice, cryptoCurrency.getCurrentPrice());
//        assertEquals(marketCap, cryptoCurrency.getMarketCap());
//        assertEquals(marketCapRank, cryptoCurrency.getMarketCapRank());
//        assertEquals(totalVolume, cryptoCurrency.getTotalVolume());
//        assertEquals(priceChange24h, cryptoCurrency.getPriceChange24h());
//        assertEquals(priceChangePercentage24h, cryptoCurrency.getPriceChangePercentage24h());
//        assertEquals(lastUpdated, cryptoCurrency.getLastUpdated());
//        assertEquals(retrievedAt, cryptoCurrency.getRetrievedAt());
//    }
//
//    @Test
//    @DisplayName("Should return uppercase ticker correctly")
//    void testGetTickerUpperCase() {
//        // Given
//        cryptoCurrency.setSymbol("btc");
//
//        // When
//        String result = cryptoCurrency.getTickerUpperCase();
//
//        // Then
//        assertEquals("BTC", result);
//    }
//
//    @Test
//    @DisplayName("Should handle null symbol in getTickerUpperCase")
//    void testGetTickerUpperCaseWithNullSymbol() {
//        // Given
//        cryptoCurrency.setSymbol(null);
//
//        // When & Then
//        assertThrows(NullPointerException.class, () -> cryptoCurrency.getTickerUpperCase());
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//        "50000.00, true",
//        "0.00, false",
//        "0.01, true"
//    })
//    @DisplayName("Should validate price correctly")
//    void testHasValidPrice(String price, boolean expected) {
//        // Given
//        cryptoCurrency.setCurrentPrice(new BigDecimal(price));
//
//        // When
//        boolean result = cryptoCurrency.hasValidPrice();
//
//        // Then
//        assertEquals(expected, result);
//    }
//
//    @Test
//    @DisplayName("Should return false for null price in hasValidPrice")
//    void testHasValidPriceWithNullPrice() {
//        // Given
//        cryptoCurrency.setCurrentPrice(null);
//
//        // When
//        boolean result = cryptoCurrency.hasValidPrice();
//
//        // Then
//        assertFalse(result);
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//        "1000.00, true",
//        "0.00, false",
//        "-500.00, false"
//    })
//    @DisplayName("Should detect price increase correctly")
//    void testIsPriceIncreasing(String priceChange, boolean expected) {
//        // Given
//        cryptoCurrency.setPriceChange24h(new BigDecimal(priceChange));
//
//        // When
//        boolean result = cryptoCurrency.isPriceIncreasing();
//
//        // Then
//        assertEquals(expected, result);
//    }
//
//    @Test
//    @DisplayName("Should return false for null price change in isPriceIncreasing")
//    void testIsPriceIncreasingWithNullPriceChange() {
//        // Given
//        cryptoCurrency.setPriceChange24h(null);
//
//        // When
//        boolean result = cryptoCurrency.isPriceIncreasing();
//
//        // Then
//        assertFalse(result);
//    }
//
//    @Test
//    @DisplayName("Should implement equals correctly")
//    void testEquals() {
//        // Given
//        CryptoCurrency crypto1 = new CryptoCurrency();
//        crypto1.setId("bitcoin");
//        crypto1.setSymbol("BTC");
//        crypto1.setName("Bitcoin");
//
//        CryptoCurrency crypto2 = new CryptoCurrency();
//        crypto2.setId("bitcoin");
//        crypto2.setSymbol("BTC");
//        crypto2.setName("Bitcoin");
//
//        CryptoCurrency crypto3 = new CryptoCurrency();
//        crypto3.setId("ethereum");
//        crypto3.setSymbol("ETH");
//        crypto3.setName("Ethereum");
//
//        // Then
//        assertEquals(crypto1, crypto2);
//        assertNotEquals(crypto1, crypto3);
//        assertNotEquals(crypto1, null);
//        assertNotEquals(crypto1, "not a crypto");
//    }
//
//    @Test
//    @DisplayName("Should implement hashCode correctly")
//    void testHashCode() {
//        // Given
//        CryptoCurrency crypto1 = new CryptoCurrency();
//        crypto1.setId("bitcoin");
//        crypto1.setSymbol("BTC");
//
//        CryptoCurrency crypto2 = new CryptoCurrency();
//        crypto2.setId("bitcoin");
//        crypto2.setSymbol("BTC");
//
//        // Then
//        assertEquals(crypto1.hashCode(), crypto2.hashCode());
//    }
//
//    @Test
//    @DisplayName("Should implement toString correctly")
//    void testToString() {
//        // Given
//        cryptoCurrency.setId("bitcoin");
//        cryptoCurrency.setSymbol("BTC");
//        cryptoCurrency.setName("Bitcoin");
//        cryptoCurrency.setCurrentPrice(new BigDecimal("50000.00"));
//
//        // When
//        String result = cryptoCurrency.toString();
//
//        // Then
//        assertNotNull(result);
//        assertTrue(result.contains("bitcoin"));
//        assertTrue(result.contains("BTC"));
//        assertTrue(result.contains("Bitcoin"));
//        assertTrue(result.contains("50000.00"));
//    }
//}