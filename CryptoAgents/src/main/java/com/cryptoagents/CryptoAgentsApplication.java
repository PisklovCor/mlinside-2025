package com.cryptoagents;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application class for CryptoAgents - Multi-agent cryptocurrency analysis system.
 * 
 * This application provides a comprehensive analysis of cryptocurrency tokens
 * using three specialized agents: Analyst, Risk Manager, and Trader.
 * 
 * @author CryptoAgents Team
 * @version 1.0.0
 */
@Slf4j
@SpringBootApplication
@EnableCaching
public class CryptoAgentsApplication {

    public static void main(String[] args) {
        log.info("🚀 Starting CryptoAgents application...");
        try {
            SpringApplication.run(CryptoAgentsApplication.class, args);
            log.info("✅ CryptoAgents application started successfully");
        } catch (Exception e) {
            log.error("❌ Failed to start CryptoAgents application: {}", e.getMessage(), e);
            throw e;
        }
    }
} 