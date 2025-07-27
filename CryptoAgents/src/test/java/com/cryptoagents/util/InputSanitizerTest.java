//package com.cryptoagents.util;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class InputSanitizerTest {
//
//    private InputSanitizer inputSanitizer;
//
//    @BeforeEach
//    void setUp() {
//        inputSanitizer = new InputSanitizer();
//    }
//
//    @Test
//    void testSanitizeTicker_ValidTicker() {
//        String result = inputSanitizer.sanitizeTicker("BTC");
//        assertEquals("BTC", result);
//    }
//
//    @Test
//    void testSanitizeTicker_ValidTickerWithNumbers() {
//        String result = inputSanitizer.sanitizeTicker("USDT");
//        assertEquals("USDT", result);
//    }
//
//    @Test
//    void testSanitizeTicker_LowercaseTicker() {
//        String result = inputSanitizer.sanitizeTicker("btc");
//        assertEquals("BTC", result);
//    }
//
//    @Test
//    void testSanitizeTicker_WithSpaces() {
//        String result = inputSanitizer.sanitizeTicker("  BTC  ");
//        assertEquals("BTC", result);
//    }
//
//    @Test
//    void testSanitizeTicker_NullTicker() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            inputSanitizer.sanitizeTicker(null);
//        });
//    }
//
//    @Test
//    void testSanitizeTicker_EmptyTicker() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            inputSanitizer.sanitizeTicker("");
//        });
//    }
//
//    @Test
//    void testSanitizeTicker_InvalidCharacters() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            inputSanitizer.sanitizeTicker("BTC-USD");
//        });
//    }
//
//    @Test
//    void testSanitizeTicker_TooLong() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            inputSanitizer.sanitizeTicker("BITCOINTOOLONG");
//        });
//    }
//
//    @Test
//    void testSanitizeAlphanumeric_ValidInput() {
//        String result = inputSanitizer.sanitizeAlphanumeric("Hello World 123", 50);
//        assertEquals("Hello World 123", result);
//    }
//
//    @Test
//    void testSanitizeAlphanumeric_WithHyphens() {
//        String result = inputSanitizer.sanitizeAlphanumeric("test-input_123", 50);
//        assertEquals("test-input_123", result);
//    }
//
//    @Test
//    void testSanitizeAlphanumeric_Truncated() {
//        String result = inputSanitizer.sanitizeAlphanumeric("Very long input that should be truncated", 10);
//        assertEquals("Very long ", result);
//    }
//
//    @Test
//    void testSanitizeAlphanumeric_InvalidCharacters() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            inputSanitizer.sanitizeAlphanumeric("test@input", 50);
//        });
//    }
//
//    @Test
//    void testSanitizeEmail_ValidEmail() {
//        String result = inputSanitizer.sanitizeEmail("test@example.com");
//        assertEquals("test@example.com", result);
//    }
//
//    @Test
//    void testSanitizeEmail_WithSpaces() {
//        String result = inputSanitizer.sanitizeEmail("  test@example.com  ");
//        assertEquals("test@example.com", result);
//    }
//
//    @Test
//    void testSanitizeEmail_InvalidEmail() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            inputSanitizer.sanitizeEmail("invalid-email");
//        });
//    }
//
//    @Test
//    void testRemoveDangerousChars() {
//        String result = inputSanitizer.removeDangerousChars("test<script>alert('xss')</script>");
//        assertEquals("testscriptalert('xss')/script", result);
//    }
//
//    @Test
//    void testSanitizeLong_ValidNumber() {
//        Long result = inputSanitizer.sanitizeLong("123");
//        assertEquals(123L, result);
//    }
//
//    @Test
//    void testSanitizeLong_InvalidNumber() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            inputSanitizer.sanitizeLong("abc");
//        });
//    }
//
//    @Test
//    void testSanitizeDouble_ValidNumber() {
//        Double result = inputSanitizer.sanitizeDouble("123.45");
//        assertEquals(123.45, result);
//    }
//
//    @Test
//    void testSanitizeDouble_InvalidNumber() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            inputSanitizer.sanitizeDouble("abc");
//        });
//    }
//}