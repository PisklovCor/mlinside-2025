package com.cryptoagents.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InputValidatorTest {
    
    private InputValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new InputValidator();
    }
    
    @Test
    void testValidateTicker_ValidTicker_ReturnsSanitized() {
        // Given
        String ticker = "btc";
        
        // When
        String result = validator.validateTicker(ticker);
        
        // Then
        assertEquals("BTC", result);
    }
    
    @Test
    void testValidateTicker_ValidTickerWithNumbers_ReturnsSanitized() {
        // Given
        String ticker = "usdt123";
        
        // When
        String result = validator.validateTicker(ticker);
        
        // Then
        assertEquals("USDT123", result);
    }
    
    @Test
    void testValidateTicker_NullTicker_ThrowsException() {
        // Given
        String ticker = null;
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateTicker(ticker));
    }
    
    @Test
    void testValidateTicker_EmptyTicker_ThrowsException() {
        // Given
        String ticker = "";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateTicker(ticker));
    }
    
    @Test
    void testValidateTicker_WhitespaceTicker_ThrowsException() {
        // Given
        String ticker = "   ";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateTicker(ticker));
    }
    
    @Test
    void testValidateTicker_InvalidCharacters_ThrowsException() {
        // Given
        String ticker = "btc-usd";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateTicker(ticker));
    }
    
    @Test
    void testValidateTicker_TooLong_ThrowsException() {
        // Given
        String ticker = "verylongticker";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateTicker(ticker));
    }
    
    @Test
    void testValidateTimeframe_ValidTimeframe_ReturnsSanitized() {
        // Given
        String timeframe = "24H";
        
        // When
        String result = validator.validateTimeframe(timeframe);
        
        // Then
        assertEquals("24h", result);
    }
    
    @Test
    void testValidateTimeframe_NullTimeframe_ReturnsDefault() {
        // Given
        String timeframe = null;
        
        // When
        String result = validator.validateTimeframe(timeframe);
        
        // Then
        assertEquals("24h", result);
    }
    
    @Test
    void testValidateTimeframe_EmptyTimeframe_ReturnsDefault() {
        // Given
        String timeframe = "";
        
        // When
        String result = validator.validateTimeframe(timeframe);
        
        // Then
        assertEquals("24h", result);
    }
    
    @Test
    void testValidateTimeframe_InvalidTimeframe_ReturnsDefault() {
        // Given
        String timeframe = "invalid";
        
        // When
        String result = validator.validateTimeframe(timeframe);
        
        // Then
        assertEquals("24h", result);
    }
    
    @Test
    void testValidateTimeframe_AllValidTimeframes() {
        // Given
        String[] validTimeframes = {"1h", "4h", "24h", "7d", "30d"};
        
        // When & Then
        for (String timeframe : validTimeframes) {
            String result = validator.validateTimeframe(timeframe);
            assertEquals(timeframe, result);
        }
    }
    
    @Test
    void testSanitizeString_NullInput_ReturnsNull() {
        // Given
        String input = null;
        
        // When
        String result = validator.sanitizeString(input);
        
        // Then
        assertNull(result);
    }
    
    @Test
    void testSanitizeString_ValidInput_ReturnsTrimmed() {
        // Given
        String input = "  valid input  ";
        
        // When
        String result = validator.sanitizeString(input);
        
        // Then
        assertEquals("valid input", result);
    }
    
    @Test
    void testSanitizeString_DangerousCharacters_RemovesThem() {
        // Given
        String input = "<script>alert('xss')</script>";
        
        // When
        String result = validator.sanitizeString(input);
        
        // Then
        assertEquals("scriptalert('xss')/script", result);
    }
    
    @Test
    void testSanitizeString_TooLong_Truncates() {
        // Given
        StringBuilder longInput = new StringBuilder();
        for (int i = 0; i < 1100; i++) {
            longInput.append("a");
        }
        
        // When
        String result = validator.sanitizeString(longInput.toString());
        
        // Then
        assertEquals(1000, result.length());
    }
    
    @Test
    void testIsValidNumber_ValidNumber_ReturnsTrue() {
        // Given
        String input = "123.45";
        
        // When
        boolean result = validator.isValidNumber(input);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testIsValidNumber_InvalidNumber_ReturnsFalse() {
        // Given
        String input = "not-a-number";
        
        // When
        boolean result = validator.isValidNumber(input);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testIsValidNumber_NullInput_ReturnsFalse() {
        // Given
        String input = null;
        
        // When
        boolean result = validator.isValidNumber(input);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testIsValidNumber_EmptyInput_ReturnsFalse() {
        // Given
        String input = "";
        
        // When
        boolean result = validator.isValidNumber(input);
        
        // Then
        assertFalse(result);
    }
} 