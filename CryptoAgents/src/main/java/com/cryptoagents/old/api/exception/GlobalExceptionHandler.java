package com.cryptoagents.old.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для централизованной обработки ошибок
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Обработка ошибок валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            "Ошибка валидации входных данных",
            fieldErrors.toString(),
            LocalDateTime.now()
        );
        
        logger.warn("Ошибка валидации для запроса {}: {}", request.getDescription(false), fieldErrors);
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Обработка исключений неверного тикера
     */
    @ExceptionHandler(InvalidTickerException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTickerException(
            InvalidTickerException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INVALID_TICKER",
            "Предоставлен неверный тикер криптовалюты",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        logger.warn("Ошибка неверного тикера для запроса {}: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Обработка исключений превышения лимита запросов
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitException(
            RateLimitExceededException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "RATE_LIMIT_EXCEEDED",
            "Слишком много запросов. Попробуйте позже.",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        logger.warn("Превышен лимит запросов для запроса {}: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }
    
    /**
     * Обработка исключений превышения лимита API
     */
    @ExceptionHandler(ApiLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleApiLimitException(
            ApiLimitExceededException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "API_LIMIT_EXCEEDED",
            "Превышен лимит запросов внешнего API",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        logger.warn("Превышен лимит запросов внешнего API для запроса {}: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
    
    /**
     * Обработка исключений внешних сервисов
     */
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceException(
            ExternalServiceException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "EXTERNAL_SERVICE_ERROR",
            "Внешний сервис временно недоступен",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        logger.error("Ошибка внешнего сервиса для запроса {}: {}", request.getDescription(false), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
    
    /**
     * Обработка исключений анализа
     */
    @ExceptionHandler(AnalysisException.class)
    public ResponseEntity<ErrorResponse> handleAnalysisException(
            AnalysisException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "ANALYSIS_ERROR",
            "Обработка анализа не удалась",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        logger.error("Ошибка анализа для запроса {}: {}", request.getDescription(false), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Обработка общих исключений
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "Произошла неожиданная ошибка",
            "Пожалуйста, попробуйте позже или обратитесь в поддержку, если проблема не исчезнет",
            LocalDateTime.now()
        );
        
        logger.error("Неожиданная ошибка для запроса {}: {}", request.getDescription(false), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
} 