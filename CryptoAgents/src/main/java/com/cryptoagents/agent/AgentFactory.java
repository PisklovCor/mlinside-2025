package com.cryptoagents.agent;

import java.util.List;

/**
 * Factory interface for creating and managing agent instances.
 * 
 * Provides methods to create individual agents or retrieve all available agents
 * for the analysis pipeline.
 */
public interface AgentFactory {
    
    /**
     * Create an agent instance by type
     * 
     * @param agentType the type of agent to create
     * @return agent instance
     * @throws IllegalArgumentException if agent type is not supported
     */
    Agent createAgent(Agent.AgentType agentType);
    
    /**
     * Create an agent instance by name
     * 
     * @param agentName the name of the agent to create (e.g., "ANALYST", "RISK_MANAGER", "TRADER")
     * @return agent instance
     * @throws IllegalArgumentException if agent name is not recognized
     */
    Agent createAgent(String agentName);
    
    /**
     * Get all available agents in priority order
     * 
     * @return list of all available agents sorted by priority
     */
    List<Agent> getAllAgents();
    
    /**
     * Get all available agent types
     * 
     * @return list of supported agent types
     */
    List<Agent.AgentType> getSupportedAgentTypes();
    
    /**
     * Check if an agent type is supported
     * 
     * @param agentType the agent type to check
     * @return true if supported, false otherwise
     */
    boolean isAgentTypeSupported(Agent.AgentType agentType);
} 