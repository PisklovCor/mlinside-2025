package com.cryptoagents.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of AgentFactory.
 * 
 * This implementation creates agent instances using Spring's ApplicationContext
 * to leverage dependency injection and manage the agent lifecycle properly.
 */
@Component
public class DefaultAgentFactory implements AgentFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultAgentFactory.class);
    
    private final ApplicationContext applicationContext;
    
    @Autowired
    public DefaultAgentFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Override
    public Agent createAgent(Agent.AgentType agentType) {
        logger.debug("Creating agent for type: {}", agentType);
        
        try {
            switch (agentType) {
                case ANALYST:
                    return applicationContext.getBean(AnalystAgent.class);
                case RISK_MANAGER:
                    return applicationContext.getBean(MockRiskManagerAgent.class);
                case TRADER:
                    return applicationContext.getBean(MockTraderAgent.class);
                default:
                    throw new IllegalArgumentException("Unsupported agent type: " + agentType);
            }
        } catch (Exception e) {
            logger.error("Failed to create agent for type {}: {}", agentType, e.getMessage(), e);
            throw new IllegalArgumentException("Failed to create agent for type " + agentType, e);
        }
    }
    
    @Override
    public Agent createAgent(String agentName) {
        Agent.AgentType agentType;
        try {
            agentType = Agent.AgentType.valueOf(agentName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown agent name: " + agentName, e);
        }
        
        return createAgent(agentType);
    }
    
    @Override
    public List<Agent> getAllAgents() {
        return Arrays.stream(Agent.AgentType.values())
                .filter(this::isAgentTypeSupported)
                .map(this::createAgent)
                .sorted(Comparator.comparingInt(Agent::getPriority))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Agent.AgentType> getSupportedAgentTypes() {
        return Arrays.stream(Agent.AgentType.values())
                .filter(this::isAgentTypeSupported)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isAgentTypeSupported(Agent.AgentType agentType) {
        try {
            // Try to create the agent to check if it's supported
            Agent agent = createAgent(agentType);
            return agent != null;
        } catch (Exception e) {
            logger.debug("Agent type {} is not supported due to error: {}", agentType, e.getMessage());
            return false;
        }
    }
} 