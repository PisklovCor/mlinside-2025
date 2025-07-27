//package com.cryptoagents.config;
//
//import io.github.bucket4j.Bandwidth;
//import io.github.bucket4j.Bucket;
//import io.github.bucket4j.Refill;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.time.Duration;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class RateLimitInterceptorTest {
//
//    @Mock
//    private HttpServletRequest request;
//
//    @Mock
//    private HttpServletResponse response;
//
//    @Mock
//    private Object handler;
//
//    private RateLimitInterceptor interceptor;
//    private Bucket bucket;
//    private StringWriter stringWriter;
//    private PrintWriter printWriter;
//
//    @BeforeEach
//    void setUp() {
//        // Create a bucket with 2 tokens, refilling 1 token every minute
//        Refill refill = Refill.intervally(1, Duration.ofMinutes(1));
//        Bandwidth limit = Bandwidth.classic(2, refill);
//        bucket = Bucket.builder().addLimit(limit).build();
//
//        interceptor = new RateLimitInterceptor(bucket);
//
//        stringWriter = new StringWriter();
//        printWriter = new PrintWriter(stringWriter);
//    }
//
//    @Test
//    void testPreHandle_WithinLimit_ReturnsTrue() throws Exception {
//        // Given
//        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
//        when(request.getRequestURI()).thenReturn("/api/crypto/analyze");
//        when(response.getWriter()).thenReturn(printWriter);
//
//        // When
//        boolean result = interceptor.preHandle(request, response, handler);
//
//        // Then
//        assertTrue(result);
//        verify(response, never()).setStatus(anyInt());
//    }
//
//    @Test
//    void testPreHandle_ExceedsLimit_ReturnsFalse() throws Exception {
//        // Given
//        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
//        when(request.getRequestURI()).thenReturn("/api/crypto/analyze");
//        when(response.getWriter()).thenReturn(printWriter);
//
//        // Consume all tokens
//        bucket.tryConsume(2);
//
//        // When
//        boolean result = interceptor.preHandle(request, response, handler);
//
//        // Then
//        assertFalse(result);
//        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
//        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
//        assertTrue(stringWriter.toString().contains("Rate limit exceeded"));
//    }
//
//    @Test
//    void testPreHandle_WithXForwardedFor_ExtractsCorrectIp() throws Exception {
//        // Given
//        when(request.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1, 192.168.1.1");
//        when(request.getRequestURI()).thenReturn("/api/crypto/analyze");
//        when(response.getWriter()).thenReturn(printWriter);
//
//        // When
//        boolean result = interceptor.preHandle(request, response, handler);
//
//        // Then
//        assertTrue(result);
//        verify(request).getHeader("X-Forwarded-For");
//    }
//
//    @Test
//    void testPreHandle_WithXRealIp_ExtractsCorrectIp() throws Exception {
//        // Given
//        when(request.getHeader("X-Real-IP")).thenReturn("172.16.0.1");
//        when(request.getRequestURI()).thenReturn("/api/crypto/analyze");
//        when(response.getWriter()).thenReturn(printWriter);
//
//        // When
//        boolean result = interceptor.preHandle(request, response, handler);
//
//        // Then
//        assertTrue(result);
//        verify(request).getHeader("X-Real-IP");
//    }
//
//    @Test
//    void testPreHandle_ExcludesHealthEndpoint() throws Exception {
//        // Given
//        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
//        when(request.getRequestURI()).thenReturn("/api/health");
//        when(response.getWriter()).thenReturn(printWriter);
//
//        // Consume all tokens
//        bucket.tryConsume(2);
//
//        // When
//        boolean result = interceptor.preHandle(request, response, handler);
//
//        // Then
//        // Should still pass as health endpoint is excluded in WebConfig
//        assertTrue(result);
//    }
//}