package com.cryptoagents;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class CryptoAgentsApplicationTests {

	@Test
	void contextLoads() {
		log.info("🧪 Running application context load test...");
		// Test that application context loads successfully
		// This validates the basic Spring configuration
		log.info("✅ Application context loaded successfully");
	}

} 