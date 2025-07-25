package com.cryptoagents;

import org.junit.jupiter.api.Test;

class CryptoAgentsApplicationTests extends BaseSpringBootTest {

    @Test
    void contextLoads() {
        logTestStart("contextLoads");
        // Test that application context loads successfully
        // This validates the basic Spring configuration
        logTestEnd("contextLoads");
    }
} 