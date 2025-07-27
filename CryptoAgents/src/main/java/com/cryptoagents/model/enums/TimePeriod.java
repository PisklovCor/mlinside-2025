package com.cryptoagents.model.enums;

/**
 * Перечисление временных периодов для исторических данных.
 * 
 * Определяет различные временные интервалы, которые могут быть использованы
 * для получения исторических данных о ценах криптовалют.
 */
public enum TimePeriod {
    
    DAY_1("1d", "1 день", 1),
    WEEK_1("7d", "1 неделя", 7),
    MONTH_1("30d", "1 месяц", 30),
    ONE_MONTH("30d", "1 месяц", 30),
    YEAR_1("1y", "1 год", 365),
    MAX("max", "Максимальный период", -1);
    
    private final String apiValue;
    private final String displayName;
    private final int days;
    
    TimePeriod(String apiValue, String displayName, int days) {
        this.apiValue = apiValue;
        this.displayName = displayName;
        this.days = days;
    }
    
    /**
     * Получает строковое значение для использования в API запросах.
     * 
     * @return Строковое значение, используемое в запросах API
     */
    public String getApiValue() {
        return apiValue;
    }
    
    /**
     * Получает отображаемое имя периода.
     * 
     * @return Отображаемое имя
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Получает количество дней в периоде.
     * 
     * @return Количество дней (-1 для неограниченного/максимального)
     */
    public int getDays() {
        return days;
    }
    
    /**
     * Находит TimePeriod по значению API.
     * 
     * @param apiValue Значение API для поиска
     * @return Соответствующий TimePeriod, или null если не найден
     */
    public static TimePeriod fromApiValue(String apiValue) {
        for (TimePeriod period : values()) {
            if (period.apiValue.equals(apiValue)) {
                return period;
            }
        }
        return null;
    }
} 