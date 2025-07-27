package com.cryptoagents.old.agent.old;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация по умолчанию для AgentFactory.
 * 
 * Эта реализация создает экземпляры агентов, используя Spring ApplicationContext
 * для использования dependency injection и правильного управления жизненным циклом агентов.
 */
@Component
@RequiredArgsConstructor
public class DefaultAgentFactory implements AgentFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultAgentFactory.class);

//    private final ApplicationContext applicationContext;

    private final List<Agent> agents;
    
//    @Autowired
//    public DefaultAgentFactory(ApplicationContext applicationContext) {
//        this.applicationContext = applicationContext;
//    }
    
    @Override
    public Agent createAgent(Agent.AgentType agentType) {
        logger.debug("Создание агента для типа: {}", agentType);
        
        try {
            switch (agentType) {
                case ANALYST:
                    return agents.stream().filter(a -> a.getType().equals(Agent.AgentType.ANALYST)).findFirst().orElse(null);
                case RISK_MANAGER:
                    return agents.stream().filter(a -> a.getType().equals(Agent.AgentType.RISK_MANAGER)).findFirst().orElse(null);
                case TRADER:
                    return agents.stream().filter(a -> a.getType().equals(Agent.AgentType.TRADER)).findFirst().orElse(null);
                default:
                    throw new IllegalArgumentException("Неподдерживаемый тип агента: " + agentType);
            }
        } catch (Exception e) {
            logger.error("Не удалось создать агента для типа {}: {}", agentType, e.getMessage(), e);
            throw new IllegalArgumentException("Не удалось создать агента для типа " + agentType, e);
        }
    }
    
    @Override
    public Agent createAgent(String agentName) {
        Agent.AgentType agentType;
        try {
            agentType = Agent.AgentType.valueOf(agentName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестное имя агента: " + agentName, e);
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
            // Попытка создать агента для проверки поддержки
            Agent agent = createAgent(agentType);
            return agent != null;
        } catch (Exception e) {
            logger.debug("Тип агента {} не поддерживается из-за ошибки: {}", agentType, e.getMessage());
            return false;
        }
    }
} 