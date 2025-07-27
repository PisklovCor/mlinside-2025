//package com.cryptoagents.model.enums;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.EnumSource;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DisplayName("TimePeriod Enum Tests")
//class TimePeriodTest {
//
//    @Test
//    @DisplayName("Should have all expected time periods")
//    void testAllTimePeriods() {
//        TimePeriod[] periods = TimePeriod.values();
//
//        assertEquals(7, periods.length);
//        assertArrayEquals(new TimePeriod[] {
//            TimePeriod.DAY_1,
//            TimePeriod.WEEK_1,
//            TimePeriod.ONE_MONTH,
//            TimePeriod.YEAR_1,
//            TimePeriod.MAX
//        }, periods);
//    }
//
//    @Test
//    @DisplayName("Should have correct API values")
//    void testApiValues() {
//        assertEquals("1", TimePeriod.DAY_1.getApiValue());
//        assertEquals("7", TimePeriod.WEEK_1.getApiValue());
//        assertEquals("30", TimePeriod.ONE_MONTH.getApiValue());
//        assertEquals("365", TimePeriod.YEAR_1.getApiValue());
//        assertEquals("max", TimePeriod.MAX.getApiValue());
//    }
//
//    @Test
//    @DisplayName("Should have correct display names")
//    void testDisplayNames() {
//        assertEquals("1 Day", TimePeriod.DAY_1.getDisplayName());
//        assertEquals("1 Week", TimePeriod.ONE_WEEK.getDisplayName());
//        assertEquals("1 Month", TimePeriod.ONE_MONTH.getDisplayName());
//        assertEquals("3 Months", TimePeriod.THREE_MONTHS.getDisplayName());
//        assertEquals("6 Months", TimePeriod.SIX_MONTHS.getDisplayName());
//        assertEquals("1 Year", TimePeriod.ONE_YEAR.getDisplayName());
//        assertEquals("All Time", TimePeriod.MAX.getDisplayName());
//    }
//
//    @Test
//    @DisplayName("Should have correct days values")
//    void testDays() {
//        assertEquals(1, TimePeriod.DAY_1.getDays());
//        assertEquals(7, TimePeriod.ONE_WEEK.getDays());
//        assertEquals(30, TimePeriod.ONE_MONTH.getDays());
//        assertEquals(90, TimePeriod.THREE_MONTHS.getDays());
//        assertEquals(180, TimePeriod.SIX_MONTHS.getDays());
//        assertEquals(365, TimePeriod.ONE_YEAR.getDays());
//        assertEquals(-1, TimePeriod.MAX.getDays()); // Special case for unlimited
//    }
//
//    @ParameterizedTest
//    @EnumSource(TimePeriod.class)
//    @DisplayName("Should have non-null properties for all periods")
//    void testNonNullProperties(TimePeriod period) {
//        assertNotNull(period.getApiValue());
//        assertNotNull(period.getDisplayName());
//        assertNotNull(period.name());
//
//        // Days can be -1 for MAX period
//        assertTrue(period.getDays() >= -1);
//    }
//
//    @Test
//    @DisplayName("Should find time period by API value")
//    void testFromApiValue() {
//        assertEquals(TimePeriod.DAY_1, TimePeriod.fromApiValue("1"));
//        assertEquals(TimePeriod.ONE_WEEK, TimePeriod.fromApiValue("7"));
//        assertEquals(TimePeriod.ONE_MONTH, TimePeriod.fromApiValue("30"));
//        assertEquals(TimePeriod.THREE_MONTHS, TimePeriod.fromApiValue("90"));
//        assertEquals(TimePeriod.SIX_MONTHS, TimePeriod.fromApiValue("180"));
//        assertEquals(TimePeriod.ONE_YEAR, TimePeriod.fromApiValue("365"));
//        assertEquals(TimePeriod.MAX, TimePeriod.fromApiValue("max"));
//    }
//
//    @Test
//    @DisplayName("Should handle case-insensitive API value lookup")
//    void testFromApiValueCaseInsensitive() {
//        assertEquals(TimePeriod.MAX, TimePeriod.fromApiValue("MAX"));
//        assertEquals(TimePeriod.MAX, TimePeriod.fromApiValue("Max"));
//        assertEquals(TimePeriod.MAX, TimePeriod.fromApiValue("max"));
//    }
//
//    @Test
//    @DisplayName("Should return null for invalid API value")
//    void testFromApiValueInvalid() {
//        assertNull(TimePeriod.fromApiValue("invalid"));
//        assertNull(TimePeriod.fromApiValue("999"));
//        assertNull(TimePeriod.fromApiValue(""));
//        assertNull(TimePeriod.fromApiValue(null));
//    }
//
//    @Test
//    @DisplayName("Should handle whitespace in API value")
//    void testFromApiValueWithWhitespace() {
//        assertEquals(TimePeriod.DAY_1, TimePeriod.fromApiValue(" 1 "));
//        assertEquals(TimePeriod.ONE_WEEK, TimePeriod.fromApiValue("\t7\n"));
//        assertEquals(TimePeriod.MAX, TimePeriod.fromApiValue(" max "));
//    }
//
//    @Test
//    @DisplayName("Should maintain order from shortest to longest period")
//    void testOrderConsistency() {
//        TimePeriod[] periods = TimePeriod.values();
//
//        // Check that days are in ascending order (except MAX which is -1)
//        for (int i = 0; i < periods.length - 2; i++) {
//            assertTrue(periods[i].getDays() < periods[i + 1].getDays(),
//                String.format("%s should have fewer days than %s",
//                    periods[i].name(), periods[i + 1].name()));
//        }
//
//        // MAX should be last and have -1 days
//        assertEquals(TimePeriod.MAX, periods[periods.length - 1]);
//        assertEquals(-1, TimePeriod.MAX.getDays());
//    }
//
//    @Test
//    @DisplayName("Should have consistent string representation")
//    void testStringRepresentation() {
//        assertEquals("ONE_DAY", TimePeriod.ONE_DAY.name());
//        assertEquals("ONE_WEEK", TimePeriod.ONE_WEEK.name());
//        assertEquals("ONE_MONTH", TimePeriod.ONE_MONTH.name());
//        assertEquals("THREE_MONTHS", TimePeriod.THREE_MONTHS.name());
//        assertEquals("SIX_MONTHS", TimePeriod.SIX_MONTHS.name());
//        assertEquals("ONE_YEAR", TimePeriod.ONE_YEAR.name());
//        assertEquals("MAX", TimePeriod.MAX.name());
//    }
//
//    @Test
//    @DisplayName("Should work with valueOf")
//    void testValueOf() {
//        assertEquals(TimePeriod.DAY_1, TimePeriod.valueOf("ONE_DAY"));
//        assertEquals(TimePeriod.WEEK_1, TimePeriod.valueOf("ONE_WEEK"));
//        assertEquals(TimePeriod.MAX, TimePeriod.valueOf("MAX"));
//
//        assertThrows(IllegalArgumentException.class, () -> TimePeriod.valueOf("INVALID"));
//    }
//
//    @Test
//    @DisplayName("Should be suitable for API integration")
//    void testApiIntegrationReadiness() {
//        // All API values should be suitable for URL parameters
//        for (TimePeriod period : TimePeriod.values()) {
//            String apiValue = period.getApiValue();
//
//            // Should not contain spaces or special characters (except max)
//            if (!period.equals(TimePeriod.MAX)) {
//                assertTrue(apiValue.matches("\\d+"),
//                    "API value should be numeric for " + period.name());
//            }
//
//            // Should not be empty
//            assertFalse(apiValue.trim().isEmpty());
//        }
//    }
//}