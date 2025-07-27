package com.cryptoagents.old.service;

/**
 * Исключение, возникающее при ошибках в процессе оркестрации агентов.
 * 
 * Это исключение используется для инкапсуляции ошибок, которые могут возникнуть
 * во время координации и выполнения агентов анализа криптовалют.
 */
public class OrchestrationException extends RuntimeException {
    
    /**
     * Конструктор с сообщением об ошибке.
     * 
     * @param message сообщение об ошибке
     */
    public OrchestrationException(String message) {
        super(message);
    }
    
    /**
     * Конструктор с сообщением об ошибке и причиной.
     * 
     * @param message сообщение об ошибке
     * @param cause причина исключения
     */
    public OrchestrationException(String message, Throwable cause) {
        super(message, cause);
    }
} 