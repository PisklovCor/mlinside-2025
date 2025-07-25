package com.cryptoagents.model.enums;

/**
 * Перечисление, представляющее различные временные периоды для получения исторических данных.
 * 
 * Это перечисление используется для указания временного диапазона при запросе исторических
 * данных криптовалют из внешних API.
 */
public enum TimePeriod {
    
    /**
     * Данные за последние 24 часа
     */
    ONE_DAY("1", "1 день", 1),
    
    /**
     * Данные за последние 7 дней
     */
    ONE_WEEK("7", "1 неделя", 7),
    
    /**
     * Данные за последние 30 дней
     */
    ONE_MONTH("30", "1 месяц", 30),
    
    /**
     * Данные за последние 3 месяца
     */
    THREE_MONTHS("90", "3 месяца", 90),
    
    /**
     * Данные за последние 6 месяцев
     */
    SIX_MONTHS("180", "6 месяцев", 180),
    
    /**
     * Данные за последний год
     */
    ONE_YEAR("365", "1 год", 365),
    
    /**
     * Максимальные доступные исторические данные
     */
    MAX("max", "Максимум", -1);

    private final String apiValue;
    private final String displayName;
    private final int days;

    TimePeriod(String apiValue, String displayName, int days) {
        this.apiValue = apiValue;
        this.displayName = displayName;
        this.days = days;
    }

    /**
     * Получает значение параметра API для этого временного периода.
     * 
     * @return Строковое значение, используемое в запросах API
     */
    public String getApiValue() {
        return apiValue;
    }

    /**
     * Получает человекочитаемое отображаемое имя для этого временного периода.
     * 
     * @return Отображаемое имя
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Получает количество дней, представленных этим временным периодом.
     * 
     * @return Количество дней (-1 для неограниченного/максимального)
     */
    public int getDays() {
        return days;
    }

    /**
     * Находит TimePeriod по его значению API.
     * Поддерживает поиск без учета регистра и обрезку пробелов.
     * 
     * @param apiValue Значение API для поиска
     * @return Соответствующий TimePeriod, или null если не найден
     */
    public static TimePeriod fromApiValue(String apiValue) {
        if (apiValue == null) {
            return null;
        }
        
        String normalizedValue = apiValue.trim().toLowerCase();
        
        for (TimePeriod period : values()) {
            if (period.apiValue.equals(normalizedValue)) {
                return period;
            }
        }
        
        return null;
    }
} 