package com.cryptoagents.old.agent.old;

import com.cryptoagents.old.model.AnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Абстрактный базовый класс для всех агентов анализа криптовалют.
 * 
 * Предоставляет общую функциональность и логику валидации, необходимую всем агентам,
 * включая логирование, измерение времени и базовую валидацию.
 */
public abstract class AbstractAgent implements Agent {
    
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * Шаблонный метод для анализа с общей предварительной/последующей обработкой
     */
    @Override
    public final AnalysisResult analyze(AnalysisContext context) throws AgentAnalysisException {
        logger.info("Начало анализа для агента '{}' с тикером '{}'", getName(), context.getTicker());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Валидация контекста
            validateContext(context);
            
            // Выполнение специфичного для агента анализа
            AnalysisResult result = performAnalysis(context);
            
            // Установка общих полей результата
            setupCommonResultFields(result, context, startTime);
            
            logger.info("Завершен анализ для агента '{}' с тикером '{}' за {}мс", 
                       getName(), context.getTicker(), result.getProcessingTimeMs());
            
            return result;
            
        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            logger.error("Анализ не удался для агента '{}' с тикером '{}' после {}мс", 
                        getName(), context.getTicker(), processingTime, e);
            
            if (e instanceof AgentAnalysisException) {
                throw e;
            } else {
                throw new AgentAnalysisException(getName(), context.getTicker(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Специфичная для агента реализация анализа, которая должна быть переопределена конкретными агентами
     */
    protected abstract AnalysisResult performAnalysis(AnalysisContext context) throws AgentAnalysisException;
    
    /**
     * Базовая валидация контекста
     */
    protected void validateContext(AnalysisContext context) throws AgentAnalysisException {
        if (context == null) {
            throw new AgentAnalysisException(getName(), "unknown", "Контекст анализа не может быть null");
        }
        
        if (!StringUtils.hasText(context.getTicker())) {
            throw new AgentAnalysisException(getName(), context.getTicker(), "Тикер не может быть null или пустым");
        }
        
        if (context.getMarketData() == null) {
            throw new AgentAnalysisException(getName(), context.getTicker(), "Рыночные данные необходимы для анализа");
        }
        
        // Разрешить подклассам добавлять дополнительную валидацию
        performAdditionalValidation(context);
    }
    
    /**
     * Дополнительный хук валидации для подклассов
     */
    protected void performAdditionalValidation(AnalysisContext context) throws AgentAnalysisException {
        // Реализация по умолчанию ничего не делает
    }
    
    /**
     * Реализация по умолчанию canAnalyze на основе валидации контекста
     */
    @Override
    public boolean canAnalyze(AnalysisContext context) {
        try {
            validateContext(context);
            return true;
        } catch (AgentAnalysisException e) {
            logger.debug("Агент '{}' не может анализировать контекст: {}", getName(), e.getMessage());
            return false;
        }
    }
    
    /**
     * Установка общих полей в результате анализа
     */
    private void setupCommonResultFields(AnalysisResult result, AnalysisContext context, long startTime) {
        if (result != null) {
            result.setAgentName(getName());
            result.setTicker(context.getTicker());
            result.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            result.setStatus(AnalysisResult.AnalysisStatus.COMPLETED);
        }
    }
    
    /**
     * Утилита для создания результата анализа с ошибкой
     */
    protected AnalysisResult createFailureResult(AnalysisContext context, String errorMessage, long startTime) {
        // Этот метод должен быть реализован конкретными агентами, так как AnalysisResult абстрактный
        // Каждый агент создаст свой конкретный тип результата
        throw new UnsupportedOperationException("Конкретные агенты должны реализовать создание результата с ошибкой");
    }
} 