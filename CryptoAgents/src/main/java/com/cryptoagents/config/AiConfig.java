package com.cryptoagents.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConfig {
    
    @Value("${ai.provider:ollama}")
    private String aiProvider;
    
    @Bean
    @Primary
    public ChatModel chatModel(
            @Qualifier("ollamaChatModel") ChatModel ollamaChatModel,
            @Qualifier("openAiChatModel") ChatModel openAiChatModel) {
        // OpenRouter uses the same OpenAI client with different base URL
        if ("openrouter".equalsIgnoreCase(aiProvider)) {
            return openAiChatModel;
        }
        return "openai".equalsIgnoreCase(aiProvider) ? openAiChatModel : ollamaChatModel;
    }
    
    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }
    
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.create(chatModel);
    }
}