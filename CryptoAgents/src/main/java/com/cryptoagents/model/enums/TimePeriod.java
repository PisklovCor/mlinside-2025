package com.cryptoagents.model.enums;

/**
 * Enumeration representing different time periods for historical data retrieval.
 * 
 * This enum is used to specify the time range when requesting historical
 * cryptocurrency data from external APIs.
 */
public enum TimePeriod {
    /**
     * Data for the last 24 hours
     */
    ONE_DAY("1", "1 Day", 1),
    
    /**
     * Data for the last 7 days
     */
    ONE_WEEK("7", "1 Week", 7),
    
    /**
     * Data for the last 30 days
     */
    ONE_MONTH("30", "1 Month", 30),
    
    /**
     * Data for the last 3 months
     */
    THREE_MONTHS("90", "3 Months", 90),
    
    /**
     * Data for the last 6 months
     */
    SIX_MONTHS("180", "6 Months", 180),
    
    /**
     * Data for the last year
     */
    ONE_YEAR("365", "1 Year", 365),
    
    /**
     * Maximum available historical data
     */
    MAX("max", "All Time", -1);

    private final String apiValue;
    private final String displayName;
    private final int days;

    TimePeriod(String apiValue, String displayName, int days) {
        this.apiValue = apiValue;
        this.displayName = displayName;
        this.days = days;
    }

    /**
     * Gets the API parameter value for this time period.
     * 
     * @return The string value used in API requests
     */
    public String getApiValue() {
        return apiValue;
    }

    /**
     * Gets the human-readable display name for this time period.
     * 
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the number of days represented by this time period.
     * 
     * @return The number of days (-1 for unlimited/max)
     */
    public int getDays() {
        return days;
    }

    /**
     * Finds a TimePeriod by its API value.
     * Supports case-insensitive matching and trims whitespace.
     * 
     * @param apiValue The API value to search for
     * @return The matching TimePeriod, or null if not found
     */
    public static TimePeriod fromApiValue(String apiValue) {
        if (apiValue == null) {
            return null;
        }
        
        String trimmedValue = apiValue.trim();
        if (trimmedValue.isEmpty()) {
            return null;
        }
        
        for (TimePeriod period : values()) {
            if (period.apiValue.equalsIgnoreCase(trimmedValue)) {
                return period;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 