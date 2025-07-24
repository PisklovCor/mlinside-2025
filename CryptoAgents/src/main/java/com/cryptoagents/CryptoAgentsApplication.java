package com.cryptoagents;

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
@SpringBootApplication
@EnableCaching
public class CryptoAgentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoAgentsApplication.class, args);
    }
} 