package com.cryptoagents.api.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    
    private GlobalExceptionHandler handler;
    private ServletWebRequest webRequest;
    
    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        webRequest = new ServletWebRequest(request);
    }
    
    @Test
    void testHandleValidationExceptions() {
        FieldError fieldError = new FieldError("object", "field", "default message");
        BindingResult bindingResult = new MockBindingResult();
        bindingResult.addError(fieldError);
        
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
            null, bindingResult);
        
        ResponseEntity<ErrorResponse> response = handler.handleValidationExceptions(ex, webRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getError());
    }
    
    @Test
    void testHandleInvalidTickerException() {
        InvalidTickerException ex = new InvalidTickerException("Invalid ticker format");
        
        ResponseEntity<ErrorResponse> response = handler.handleInvalidTickerException(ex, webRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_TICKER", response.getBody().getError());
        assertEquals("Invalid ticker format", response.getBody().getDetails());
    }
    
    @Test
    void testHandleRateLimitException() {
        RateLimitExceededException ex = new RateLimitExceededException("Rate limit exceeded");
        
        ResponseEntity<ErrorResponse> response = handler.handleRateLimitException(ex, webRequest);
        
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RATE_LIMIT_EXCEEDED", response.getBody().getError());
    }
    
    @Test
    void testHandleApiLimitException() {
        ApiLimitExceededException ex = new ApiLimitExceededException("API limit exceeded");
        
        ResponseEntity<ErrorResponse> response = handler.handleApiLimitException(ex, webRequest);
        
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("API_LIMIT_EXCEEDED", response.getBody().getError());
    }
    
    @Test
    void testHandleExternalServiceException() {
        ExternalServiceException ex = new ExternalServiceException("Service unavailable");
        
        ResponseEntity<ErrorResponse> response = handler.handleExternalServiceException(ex, webRequest);
        
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("EXTERNAL_SERVICE_ERROR", response.getBody().getError());
    }
    
    @Test
    void testHandleAnalysisException() {
        AnalysisException ex = new AnalysisException("Analysis failed");
        
        ResponseEntity<ErrorResponse> response = handler.handleAnalysisException(ex, webRequest);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ANALYSIS_ERROR", response.getBody().getError());
    }
    
    @Test
    void testHandleGeneralException() {
        Exception ex = new RuntimeException("Unexpected error");
        
        ResponseEntity<ErrorResponse> response = handler.handleGeneralException(ex, webRequest);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getError());
    }
    
    // Mock BindingResult for testing
    private static class MockBindingResult implements BindingResult {
        private final java.util.List<FieldError> errors = new java.util.ArrayList<>();
        
        @Override
        public void addError(FieldError error) {
            errors.add(error);
        }
        
        @Override
        public java.util.List<FieldError> getFieldErrors() {
            return errors;
        }
        
        // Implement other methods with default implementations
        @Override
        public String getObjectName() { return "object"; }
        
        @Override
        public void setNestedPath(String nestedPath) {}
        
        @Override
        public String getNestedPath() { return ""; }
        
        @Override
        public void pushNestedPath(String subPath) {}
        
        @Override
        public void popNestedPath() throws IllegalStateException {}
        
        @Override
        public void reject(String errorCode) {}
        
        @Override
        public void reject(String errorCode, String defaultMessage) {}
        
        @Override
        public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {}
        
        @Override
        public void rejectValue(String field, String errorCode) {}
        
        @Override
        public void rejectValue(String field, String errorCode, String defaultMessage) {}
        
        @Override
        public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {}
        
        @Override
        public void addAllErrors(BindingResult bindingResult) {}
        
        @Override
        public boolean hasErrors() { return !errors.isEmpty(); }
        
        @Override
        public int getErrorCount() { return errors.size(); }
        
        @Override
        public java.util.List<org.springframework.validation.ObjectError> getAllErrors() { return Collections.emptyList(); }
        
        @Override
        public boolean hasGlobalErrors() { return false; }
        
        @Override
        public int getGlobalErrorCount() { return 0; }
        
        @Override
        public java.util.List<ObjectError> getGlobalErrors() { return Collections.emptyList(); }
        
        @Override
        public ObjectError getGlobalError() { return null; }
        
        @Override
        public boolean hasFieldErrors() { return !errors.isEmpty(); }
        
        @Override
        public int getFieldErrorCount() { return errors.size(); }
        

        
        @Override
        public FieldError getFieldError() { return errors.isEmpty() ? null : errors.get(0); }
        
        @Override
        public boolean hasFieldErrors(String field) { return false; }
        
        @Override
        public int getFieldErrorCount(String field) { return 0; }
        
        @Override
        public java.util.List<FieldError> getFieldErrors(String field) { return Collections.emptyList(); }
        
        @Override
        public FieldError getFieldError(String field) { return null; }
        
        @Override
        public Object getFieldValue(String field) { return null; }
        
        @Override
        public Class<?> getFieldType(String field) { return null; }
    }
} 