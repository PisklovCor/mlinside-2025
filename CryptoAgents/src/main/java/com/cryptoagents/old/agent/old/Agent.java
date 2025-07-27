package com.cryptoagents.old.agent.old;

import com.cryptoagents.old.model.AnalysisResult;

/**
 * Базовый интерфейс для всех агентов анализа криптовалют.
 * 
 * Каждый агент реализует специфические возможности анализа:
 * - Аналитик: Технический анализ
 * - Риск-менеджер: Оценка рисков
 * - Трейдер: Торговые рекомендации
 * 
 * Примечание: Реализации должны использовать SLF4J логгер:
 * private static final Logger logger = LoggerFactory.getLogger(AgentImplementation.class);
 */
public interface Agent {
    
    /**
     * Получить уникальное имя этого агента
     * @return имя агента (например, "ANALYST", "RISK_MANAGER", "TRADER")
     */
    String getName();
    
    /**
     * Получить тип агента для классификации
     * @return идентификатор типа агента
     */
    AgentType getType();
    
    /**
     * Выполнить анализ для заданного тикера криптовалюты
     * 
     * @param context контекст анализа, содержащий рыночные данные и результаты предыдущих агентов
     * @return результат анализа, специфичный для этого типа агента
     * @throws AgentAnalysisException если анализ не удался
     */
    AnalysisResult analyze(AnalysisContext context) throws AgentAnalysisException;
    
    /**
     * Проверить, может ли агент выполнить анализ с заданным контекстом
     * 
     * @param context контекст анализа для валидации
     * @return true если анализ может быть выполнен, false в противном случае
     */
    boolean canAnalyze(AnalysisContext context);
    
    /**
     * Получить приоритетный порядок для этого агента в конвейере анализа
     * Меньшие числа указывают на более высокий приоритет (выполняется первым)
     * 
     * @return порядок приоритета (Аналитик=1, Риск-менеджер=2, Трейдер=3)
     */
    int getPriority();
    
    /**
     * Перечисление для типов агентов
     */
    enum AgentType {
        ANALYST("Агент технического анализа"),
        RISK_MANAGER("Агент оценки рисков"), 
        TRADER("Агент торговых рекомендаций");
        
        private final String description;
        
        AgentType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
} 