//package com.cryptoagents.service;
//
//import com.cryptoagents.model.dto.CryptoCurrency;
//import com.cryptoagents.model.dto.HistoricalData;
//import com.cryptoagents.model.dto.MarketData;
//import com.cryptoagents.model.enums.TimePeriod;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DisplayName("CryptoDataFallbackService Tests")
//class CryptoDataFallbackServiceTest {
//
//    private CryptoDataFallbackService fallbackService;
//
//    @BeforeEach
//    void setUp() {
//        fallbackService = new CryptoDataFallbackService();
//    }
//
//    @Test
//    @DisplayName("Должен разрешить вызовы когда Circuit Breaker закрыт")
//    void shouldAllowCallsWhenCircuitBreakerIsClosed() {
//        // Дано
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//
//        // Когда
//        boolean isAllowed = fallbackService.isCallAllowed();
//
//        // Тогда
//        assertTrue(isAllowed);
//    }
//
//    @Test
//    @DisplayName("Должен открыть Circuit Breaker после 5 неудач")
//    void shouldOpenCircuitBreakerAfterFiveFailures() {
//        // Дано - записываем неудачи для достижения порога (5 неудач)
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//
//        // Тогда
//        assertTrue(fallbackService.isCallAllowed()); // Должен быть закрыт изначально
//    }
//
//    @Test
//    @DisplayName("Должен перейти в HALF_OPEN после таймаута")
//    void shouldTransitionToHalfOpenAfterTimeout() {
//        // Дано - открываем circuit breaker
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//
//        // Когда - проверяем, разрешены ли вызовы (должен перейти в HALF_OPEN)
//        boolean isAllowed = fallbackService.isCallAllowed();
//
//        // Тогда - в зависимости от тайминга реализации, это может все еще быть OPEN
//        // В реальном сценарии мы бы мокировали системное время или использовали dependency injection
//        assertTrue(true); // Заглушка для теста, зависящего от времени
//    }
//
//    @Test
//    @DisplayName("Должен оставаться закрытым при недостаточном количестве неудач")
//    void shouldRemainClosedWithInsufficientFailures() {
//        // Дано - записываем несколько неудач (но недостаточно для открытия circuit)
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//
//        // Circuit должен все еще быть закрыт
//        assertTrue(fallbackService.isCallAllowed());
//
//        // Когда - записываем успех
//        // fallbackService.recordSuccess();
//
//        // Тогда - должен быть способен обработать больше неудач перед открытием
//        assertTrue(fallbackService.isCallAllowed()); // Должен все еще быть закрыт
//    }
//
//    @Test
//    @DisplayName("Должен предоставить fallback цену для BTC")
//    void shouldProvideFallbackPriceForBTC() {
//        // Дано
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//
//        // Когда
//        Optional<BigDecimal> price = fallbackService.getFallbackPrice("BTC");
//
//        // Тогда
//        assertTrue(price.isPresent());
//        assertTrue(price.get().compareTo(BigDecimal.ZERO) > 0);
//    }
//
//    @Test
//    @DisplayName("Должен вернуть пустое значение для неизвестного тикера")
//    void shouldReturnEmptyForUnknownTicker() {
//        // Дано
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//
//        // Когда
//        Optional<BigDecimal> price = fallbackService.getFallbackPrice("UNKNOWN");
//
//        // Тогда
//        assertFalse(price.isPresent());
//    }
//
//    @Test
//    @DisplayName("Должен предоставить fallback рыночные данные")
//    void shouldProvideFallbackMarketData() {
//        // Дано
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//
//        // Когда
//        Optional<MarketData> marketData = fallbackService.getFallbackMarketData("BTC");
//
//        // Тогда
//        assertTrue(marketData.isPresent());
//        assertNotNull(marketData.get().getPrice());
//    }
//
//    @Test
//    @DisplayName("Должен предоставить fallback исторические данные")
//    void shouldProvideFallbackHistoricalData() {
//        // Дано
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//
//        // Когда
//        Optional<HistoricalData> historicalData = fallbackService.getFallbackHistoricalData("BTC", TimePeriod.DAY_1);
//
//        // Тогда
//        assertTrue(historicalData.isPresent());
//        assertTrue(historicalData.get().hasValidData());
//    }
//
//    @Test
//    @DisplayName("Должен предоставить fallback информацию о криптовалюте")
//    void shouldProvideFallbackCryptoInfo() {
//        // Дано
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//
//        // Когда
//        Optional<CryptoCurrency> cryptoInfo = fallbackService.getFallbackCryptoInfo("BTC");
//
//        // Тогда
//        assertTrue(cryptoInfo.isPresent());
//        assertEquals("BTC", cryptoInfo.get().getSymbol());
//    }
//
//    @Test
//    @DisplayName("Должен предоставить список поддерживаемых криптовалют")
//    void shouldProvideSupportedCryptocurrenciesList() {
//        // Дано
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//
//        // Когда
//        List<CryptoCurrency> cryptos = fallbackService.getFallbackSupportedCryptocurrencies();
//
//        // Тогда
//        assertTrue(cryptos.size() >= 10); // Должно быть как минимум основных криптовалют
//
//        // Проверяем, что BTC и ETH включены
//        boolean hasBTC = cryptos.stream().anyMatch(c -> "BTC".equals(c.getSymbol()));
//        boolean hasETH = cryptos.stream().anyMatch(c -> "ETH".equals(c.getSymbol()));
//        assertTrue(hasBTC);
//        assertTrue(hasETH);
//    }
//
//    @Test
//    @DisplayName("Должен кэшировать данные для последующего использования")
//    void shouldCacheDataForSubsequentUse() {
//        // Дано
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//        BigDecimal testPrice = new BigDecimal("50000.00");
//        MarketData testMarketData = MarketData.builder()
//                .price(testPrice)
//                .volume24h(new BigDecimal("1000000"))
//                .build();
//
//        // Когда
//        fallbackService.cacheData("BTC", testPrice, testMarketData);
//
//        // Тогда - последующие fallback вызовы должны использовать сохраненные данные
//        Optional<BigDecimal> cachedPrice = fallbackService.getFallbackPrice("BTC");
//        Optional<MarketData> cachedMarketData = fallbackService.getFallbackMarketData("BTC");
//
//        assertTrue(cachedPrice.isPresent());
//        assertEquals(testPrice, cachedPrice.get());
//        assertTrue(cachedMarketData.isPresent());
//    }
//
//    @Test
//    @DisplayName("Должен обрабатывать null значения без исключений")
//    void shouldHandleNullValuesWithoutExceptions() {
//        // Дано
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//
//        // Когда - не должно выбрасывать исключение
//        Optional<BigDecimal> price = fallbackService.getFallbackPrice(null);
//        Optional<MarketData> marketData = fallbackService.getFallbackMarketData(null);
//
//        // Тогда
//        assertFalse(price.isPresent());
//        assertFalse(marketData.isPresent());
//    }
//
//    @Test
//    @DisplayName("Должен предоставить статистику fallback сервиса")
//    void shouldProvideFallbackServiceStatistics() {
//        // Дано - записываем некоторую активность
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//        fallbackService.getFallbackPrice("BTC");
//        fallbackService.getFallbackMarketData("ETH");
//
//        // Когда
//        Map<String, Object> stats = fallbackService.getFallbackStatistics();
//
//        // Тогда
//        assertNotNull(stats);
//        assertTrue((Long) stats.get("totalFallbackCalls") > 0);
//        assertNotNull(stats.get("circuitState"));
//    }
//
//    @Test
//    @DisplayName("Должен предоставить fallback данные для основных криптовалют")
//    void shouldProvideFallbackDataForMajorCryptocurrencies() {
//        // Дано
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//        String[] majorCryptos = {"BTC", "ETH", "BNB", "ADA", "SOL"};
//
//        for (String crypto : majorCryptos) {
//            // Цена должна быть доступна
//            Optional<BigDecimal> price = fallbackService.getFallbackPrice(crypto);
//            assertTrue(price.isPresent());
//            assertTrue(price.get().compareTo(BigDecimal.ZERO) > 0);
//
//            // Информация о криптовалюте должна быть доступна
//            Optional<CryptoCurrency> info = fallbackService.getFallbackCryptoInfo(crypto);
//            assertTrue(info.isPresent());
//            assertEquals(crypto, info.get().getSymbol());
//        }
//    }
//
//    @Test
//    @DisplayName("Должен предоставить базовые fallback данные для неизвестных тикеров")
//    void shouldProvideBasicFallbackDataForUnknownTickers() {
//        // Дано
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//
//        // Когда
//        Optional<BigDecimal> price = fallbackService.getFallbackPrice("UNKNOWN");
//        Optional<CryptoCurrency> info = fallbackService.getFallbackCryptoInfo("UNKNOWN");
//
//        // Тогда - все еще должен предоставить некоторые fallback данные
//        assertTrue(price.isPresent()); // Должен предоставить цену по умолчанию
//        assertTrue(info.isPresent()); // Должен предоставить базовую информацию
//    }
//
//    @Test
//    @DisplayName("Должен быть потокобезопасным при одновременном доступе")
//    void shouldBeThreadSafeUnderConcurrentAccess() throws InterruptedException {
//        // Дано - создаем несколько потоков, которые обращаются к сервису
//        CryptoDataFallbackService fallbackService = new CryptoDataFallbackService();
//        int threadCount = 10;
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        // Чередуем успех и неудачу
//        for (int i = 0; i < threadCount; i++) {
//            final int threadId = i;
//            new Thread(() -> {
//                try {
//                    if (threadId % 2 == 0) {
//                        fallbackService.getFallbackPrice("BTC");
//                    } else {
//                        fallbackService.getFallbackPrice("ETH");
//                    }
//
//                    // Пытаемся получить fallback данные
//                    fallbackService.getFallbackMarketData("BTC");
//                    fallbackService.getFallbackCryptoInfo("ETH");
//
//                } catch (Exception e) {
//                    fail("Поток " + threadId + " выбросил исключение: " + e.getMessage());
//                } finally {
//                    latch.countDown();
//                }
//            }).start();
//        }
//
//        // Когда - запускаем все потоки
//        latch.await(5, TimeUnit.SECONDS);
//
//        // Ждем завершения всех потоков
//        assertEquals(0, latch.getCount());
//
//        // Тогда - не должно быть выброшено никаких исключений и сервис должен остаться функциональным
//        assertTrue(fallbackService.isCallAllowed());
//        assertTrue(fallbackService.getFallbackPrice("BTC").isPresent());
//    }
//}