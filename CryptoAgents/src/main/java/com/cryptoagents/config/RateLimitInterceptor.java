package com.cryptoagents.config;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Интерцептор для ограничения скорости API запросов
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);
    private final Bucket bucket;
    
    public RateLimitInterceptor(Bucket bucket) {
        this.bucket = bucket;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIp = getClientIpAddress(request);
        String endpoint = request.getRequestURI();
        
        logger.debug("Проверка ограничения скорости для IP: {} на эндпоинте: {}", clientIp, endpoint);
        
        if (bucket.tryConsume(1)) {
            logger.debug("Ограничение скорости пройдено для IP: {}", clientIp);
            return true;
        }
        
        logger.warn("Превышено ограничение скорости для IP: {} на эндпоинте: {}", clientIp, endpoint);
        
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        try {
            String errorResponse = String.format(
                "{\"error\":\"Rate limit exceeded\",\"status\":429,\"message\":\"Too many requests. Please try again later.\"}"
            );
            response.getWriter().write(errorResponse);
        } catch (IOException e) {
            logger.error("Ошибка записи ответа об ограничении скорости", e);
        }
        
        return false;
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
} 