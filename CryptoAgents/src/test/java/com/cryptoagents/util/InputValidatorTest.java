package com.cryptoagents.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InputValidator Tests")
class InputValidatorTest {
    
    @Mock
    private InputSanitizer inputSanitizer;
    
    private InputValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new InputValidator(inputSanitizer);
    }
    
    @Nested
    @DisplayName("Ticker Validation Tests")
    class TickerValidationTests {
        
        @ParameterizedTest
        @CsvSource({
            "btc, BTC",
            "ETH, ETH", 
            "usdt123, USDT123",
            "ADA, ADA",
            "DOT, DOT"
        })
        @DisplayName("Should validate and sanitize valid tickers")
        void testValidateTicker_ValidTicker_ReturnsSanitized(String input, String expected) {
            // Дано
            when(inputSanitizer.sanitizeTicker(input)).thenReturn(expected);
            
            // Когда
            String result = validator.validateTicker(input);
            
            // Тогда
            assertThat(result).isEqualTo(expected);
        }
        
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("Should reject null, empty or whitespace tickers")
        void testValidateTicker_InvalidTicker_ThrowsException(String ticker) {
            // Дано
            when(inputSanitizer.sanitizeTicker(ticker)).thenThrow(new IllegalArgumentException("Ticker cannot be null or empty"));
            
            // Когда и Тогда
            assertThatThrownBy(() -> validator.validateTicker(ticker))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Ticker cannot be null or empty");
        }
        
        @ParameterizedTest
        @ValueSource(strings = {
            "btc-usd", "BTC/USD", "BTC@USD", "BTC.USD", 
            "BTC_USD", "BTC-USD", "BTC/USD", "BTC@USD"
        })
        @DisplayName("Should reject tickers with invalid characters")
        void testValidateTicker_InvalidCharacters_ThrowsException(String ticker) {
            // Дано
            when(inputSanitizer.sanitizeTicker(ticker)).thenThrow(new IllegalArgumentException("Invalid ticker format"));
            
            // Когда и Тогда
            assertThatThrownBy(() -> validator.validateTicker(ticker))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid ticker format");
        }
        
        @ParameterizedTest
        @ValueSource(strings = {
            "A", "AB", "VERYLONGTICKERNAME"
        })
        @DisplayName("Should reject tickers with invalid length")
        void testValidateTicker_InvalidLength_ThrowsException(String ticker) {
            // Дано
            when(inputSanitizer.sanitizeTicker(ticker)).thenThrow(new IllegalArgumentException("Invalid ticker length"));
            
            // Когда и Тогда
            assertThatThrownBy(() -> validator.validateTicker(ticker))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid ticker length");
        }
        
        @Test
        @DisplayName("Should handle single character valid ticker")
        void testValidateTicker_SingleCharacter_ThrowsException() {
            // Дано
            String ticker = "A";
            when(inputSanitizer.sanitizeTicker(ticker)).thenThrow(new IllegalArgumentException("Invalid ticker"));
            
            // Когда и Тогда
            assertThatThrownBy(() -> validator.validateTicker(ticker))
                    .isInstanceOf(IllegalArgumentException.class);
        }
        
        @Test
        @DisplayName("Should handle maximum length valid ticker")
        void testValidateTicker_MaximumLength_ReturnsSanitized() {
            // Дано
            String ticker = "A".repeat(10);
            String expected = "A".repeat(10);
            when(inputSanitizer.sanitizeTicker(ticker)).thenReturn(expected);
            
            // Когда
            String result = validator.validateTicker(ticker);
            
            // Тогда
            assertThat(result).isEqualTo(expected);
        }
    }
    
    @Nested
    @DisplayName("Timeframe Validation Tests")
    class TimeframeValidationTests {
        
        @ParameterizedTest
        @CsvSource({
            "1h, 1h",
            "4h, 4h", 
            "24h, 24h",
            "7d, 7d",
            "30d, 30d",
            "24H, 24h",
            "7D, 7d"
        })
        @DisplayName("Should validate and normalize valid timeframes")
        void testValidateTimeframe_ValidTimeframe_ReturnsNormalized(String input, String expected) {
            // When
            String result = validator.validateTimeframe(input);
            
            // Then
            assertThat(result).isEqualTo(expected);
        }
        
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"invalid", "25h", "8d", "0h", "-1h"})
        @DisplayName("Should return default for invalid timeframes")
        void testValidateTimeframe_InvalidTimeframe_ReturnsDefault(String timeframe) {
            // When
            String result = validator.validateTimeframe(timeframe);
            
            // Then
            assertThat(result).isEqualTo("24h");
        }
        
        @Test
        @DisplayName("Should handle all valid timeframes")
        void testValidateTimeframe_AllValidTimeframes() {
            // Given
            String[] validTimeframes = {"1h", "4h", "24h", "7d", "30d"};
            
            // When & Then
            for (String timeframe : validTimeframes) {
                String result = validator.validateTimeframe(timeframe);
                assertThat(result).isEqualTo(timeframe);
            }
        }
    }
    
    @Nested
    @DisplayName("String Sanitization Tests")
    class StringSanitizationTests {
        
        @Test
        @DisplayName("Should return null for null input")
        void testSanitizeString_NullInput_ReturnsNull() {
            // When
            String result = validator.sanitizeString(null);
            
            // Then
            assertThat(result).isNull();
        }
        
        @ParameterizedTest
        @CsvSource({
            "'  valid input  ', 'valid input'",
            "'\tvalid input\t', 'valid input'",
            "'\nvalid input\n', 'valid input'",
            "'valid input', 'valid input'"
        })
        @DisplayName("Should trim whitespace from input")
        void testSanitizeString_ValidInput_ReturnsTrimmed(String input, String expected) {
            // Given
            when(inputSanitizer.removeDangerousChars(input)).thenReturn(expected);
            
            // When
            String result = validator.sanitizeString(input);
            
            // Then
            assertThat(result).isEqualTo(expected);
        }
        
        @ParameterizedTest
        @CsvSource({
            "'<script>alert(''xss'')</script>', 'scriptalert(''xss'')/script'",
            "'<img src=x onerror=alert(1)>', 'img src=x onerror=alert(1)'",
            "'javascript:alert(1)', 'javascript:alert(1)'",
            "'<div>content</div>', 'divcontent/div'"
        })
        @DisplayName("Should remove dangerous HTML characters")
        void testSanitizeString_DangerousCharacters_RemovesThem(String input, String expected) {
            // Given
            when(inputSanitizer.removeDangerousChars(input)).thenReturn(expected);
            
            // When
            String result = validator.sanitizeString(input);
            
            // Then
            assertThat(result).isEqualTo(expected);
        }
        
        @Test
        @DisplayName("Should truncate very long input")
        void testSanitizeString_TooLong_Truncates() {
            // Given
            StringBuilder longInput = new StringBuilder();
            for (int i = 0; i < 1100; i++) {
                longInput.append("a");
            }
            String truncated = "a".repeat(1000);
            when(inputSanitizer.removeDangerousChars(longInput.toString())).thenReturn(truncated);
            
            // When
            String result = validator.sanitizeString(longInput.toString());
            
            // Then
            assertThat(result).hasSize(1000);
            assertThat(result).startsWith("a");
        }
        
        @Test
        @DisplayName("Should handle empty string")
        void testSanitizeString_EmptyString_ReturnsEmpty() {
            // Given
            when(inputSanitizer.removeDangerousChars("")).thenReturn("");
            
            // When
            String result = validator.sanitizeString("");
            
            // Then
            assertThat(result).isEmpty();
        }
        
        @Test
        @DisplayName("Should handle string with only whitespace")
        void testSanitizeString_WhitespaceOnly_ReturnsEmpty() {
            // Given
            when(inputSanitizer.removeDangerousChars("   ")).thenReturn("   ");
            
            // When
            String result = validator.sanitizeString("   ");
            
            // Then
            assertThat(result).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("Number Validation Tests")
    class NumberValidationTests {
        
        @ParameterizedTest
        @CsvSource({
            "123, true",
            "123.45, true", 
            "-123.45, true",
            "0, true",
            "0.0, true",
            "1e10, true",
            "1E-10, true"
        })
        @DisplayName("Should validate valid numbers")
        void testIsValidNumber_ValidNumber_ReturnsTrue(String input, boolean expected) {
            // When
            boolean result = validator.isValidNumber(input);
            
            // Then
            assertThat(result).isEqualTo(expected);
        }
        
        @ParameterizedTest
        @ValueSource(strings = {
            "not-a-number", "abc", "12.34.56", "123abc", 
            "abc123", "12,34", "12 34"
        })
        @DisplayName("Should reject invalid numbers")
        void testIsValidNumber_InvalidNumber_ReturnsFalse(String input) {
            // When
            boolean result = validator.isValidNumber(input);
            
            // Then
            assertThat(result).isFalse();
        }
        
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("Should reject null, empty or whitespace")
        void testIsValidNumber_InvalidInput_ReturnsFalse(String input) {
            // When
            boolean result = validator.isValidNumber(input);
            
            // Then
            assertThat(result).isFalse();
        }
        
        @Test
        @DisplayName("Should handle boundary values")
        void testIsValidNumber_BoundaryValues() {
            // When & Then
            assertThat(validator.isValidNumber("0")).isTrue();
            assertThat(validator.isValidNumber("0.0")).isTrue();
            assertThat(validator.isValidNumber("-0")).isTrue();
            assertThat(validator.isValidNumber("999999999999999999")).isTrue();
        }
    }
} 