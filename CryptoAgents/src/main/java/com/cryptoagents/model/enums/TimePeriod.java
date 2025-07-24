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
    ONE_DAY("1", "24 hours", 1),
    
    /**
     * Data for the last 7 days
     */
    ONE_WEEK("7", "7 days", 7),
    
    /**
     * Data for the last 30 days
     */
    ONE_MONTH("30", "30 days", 30),
    
    /**
     * Data for the last 3 months
     */
    THREE_MONTHS("90", "3 months", 90),
    
    /**
     * Data for the last 6 months
     */
    SIX_MONTHS("180", "6 months", 180),
    
    /**
     * Data for the last year
     */
    ONE_YEAR("365", "1 year", 365),
    
    /**
     * Maximum available historical data
     */
    MAX("max", "All available", Integer.MAX_VALUE);

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
     * @return The number of days
     */
    public int getDays() {
        return days;
    }

    /**
     * Finds a TimePeriod by its API value.
     * 
     * @param apiValue The API value to search for
     * @return The matching TimePeriod, or null if not found
     */
    public static TimePeriod fromApiValue(String apiValue) {
        for (TimePeriod period : values()) {
            if (period.apiValue.equals(apiValue)) {
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