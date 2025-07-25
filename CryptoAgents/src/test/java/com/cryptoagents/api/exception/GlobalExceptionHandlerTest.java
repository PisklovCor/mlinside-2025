package com.cryptoagents.api.exception;

import com.cryptoagents.api.exception.InvalidTickerException;
import com.cryptoagents.api.exception.RateLimitExceededException;
import com.cryptoagents.api.exception.ServiceUnavailableException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should handle InvalidTickerException")
    void handleInvalidTickerException_ReturnsBadRequest() {
        // Given
        InvalidTickerException exception = new InvalidTickerException("Invalid ticker: XYZ");
        WebRequest request = mock(WebRequest.class);

        // When
        ResponseEntity<Object> response = exceptionHandler.handleInvalidTickerException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("Invalid ticker: XYZ");
    }

    @Test
    @DisplayName("Should handle RateLimitExceededException")
    void handleRateLimitExceededException_ReturnsTooManyRequests() {
        // Given
        RateLimitExceededException exception = new RateLimitExceededException("Rate limit exceeded");
        WebRequest request = mock(WebRequest.class);

        // When
        ResponseEntity<Object> response = exceptionHandler.handleRateLimitExceededException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("Rate limit exceeded");
    }

    @Test
    @DisplayName("Should handle ServiceUnavailableException")
    void handleServiceUnavailableException_ReturnsServiceUnavailable() {
        // Given
        ServiceUnavailableException exception = new ServiceUnavailableException("Service temporarily unavailable");
        WebRequest request = mock(WebRequest.class);

        // When
        ResponseEntity<Object> response = exceptionHandler.handleServiceUnavailableException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("Service temporarily unavailable");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException")
    void handleMethodArgumentNotValidException_ReturnsBadRequest() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = new MockBindingResult();
        bindingResult.addError(new FieldError("object", "field", "Field is required"));
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        WebRequest request = mock(WebRequest.class);

        // When
        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("Field is required");
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void handleGenericException_ReturnsInternalServerError() {
        // Given
        Exception exception = new Exception("Unexpected error occurred");
        WebRequest request = mock(WebRequest.class);

        // When
        ResponseEntity<Object> response = exceptionHandler.handleGenericException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("Unexpected error occurred");
    }

    // Mock BindingResult for testing
    private static class MockBindingResult implements BindingResult {
        private final java.util.List<FieldError> errors = new java.util.ArrayList<>();

        @Override
        public void addError(FieldError error) {
            errors.add(error);
        }

        @Override
        public void addError(ObjectError error) {
            // Можно добавить обработку ObjectError, если нужно
        }

        @Override
        public java.util.List<FieldError> getFieldErrors() {
            return errors;
        }

        // Реализуем все остальные методы интерфейса заглушками:
        @Override public String getObjectName() { return "object"; }
        @Override public void setNestedPath(String nestedPath) {}
        @Override public String getNestedPath() { return ""; }
        @Override public void pushNestedPath(String subPath) {}
        @Override public void popNestedPath() throws IllegalStateException {}
        @Override public void reject(String errorCode) {}
        @Override public void reject(String errorCode, String defaultMessage) {}
        @Override public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {}
        @Override public void rejectValue(String field, String errorCode) {}
        @Override public void rejectValue(String field, String errorCode, String defaultMessage) {}
        @Override public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {}
        @Override public void addAllErrors(org.springframework.validation.Errors errors) {}
        @Override public boolean hasErrors() { return !errors.isEmpty(); }
        @Override public int getErrorCount() { return errors.size(); }
        @Override public java.util.List<ObjectError> getAllErrors() { return new java.util.ArrayList<>(errors); }
        @Override public boolean hasGlobalErrors() { return false; }
        @Override public int getGlobalErrorCount() { return 0; }
        @Override public java.util.List<ObjectError> getGlobalErrors() { return java.util.Collections.emptyList(); }
        @Override public ObjectError getGlobalError() { return null; }
        @Override public boolean hasFieldErrors() { return !errors.isEmpty(); }
        @Override public int getFieldErrorCount() { return errors.size(); }
        @Override public FieldError getFieldError() { return errors.isEmpty() ? null : errors.get(0); }
        @Override public boolean hasFieldErrors(String field) { return false; }
        @Override public int getFieldErrorCount(String field) { return 0; }
        @Override public java.util.List<FieldError> getFieldErrors(String field) { return java.util.Collections.emptyList(); }
        @Override public FieldError getFieldError(String field) { return null; }
        @Override public Object getFieldValue(String field) { return null; }
        @Override public Class<?> getFieldType(String field) { return null; }
        @Override public Object getTarget() { return null; }
        @Override public java.util.Map<String, Object> getModel() { return java.util.Collections.emptyMap(); }
        @Override public Object getRawFieldValue(String field) { return null; }
        @Override public java.beans.PropertyEditorRegistry getPropertyEditorRegistry() { return null; }
        @Override public String[] resolveMessageCodes(String errorCode) { return new String[0]; }
        @Override public String[] resolveMessageCodes(String errorCode, String field) { return new String[0]; }
        @Override public void recordSuppressedField(String field) {}
        @Override public String[] getSuppressedFields() { return new String[0]; }
    }
} 