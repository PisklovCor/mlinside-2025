package com.multiagent.agent;

import com.multiagent.model.AgentAnalysis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.messages.UserMessage;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseAgent {

    protected final ChatModel openAiChatModel;

    public abstract AgentAnalysis analyze(String cryptocurrency, String timeframe);

    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    protected String getAiResponse(String promptText, Map<String, Object> templateValues) {
        log.debug("Отправка запроса к AI с шаблоном для криптовалюты: {}", templateValues.get("cryptocurrency"));
        log.trace("Текст промпта: {}", promptText);
        
        try {
            PromptTemplate promptTemplate = new PromptTemplate(promptText);
            Prompt prompt = promptTemplate.create(templateValues);

            String response = openAiChatModel.call(prompt)
                    .getResult().toString();
            
            log.debug("Получен ответ от AI для криптовалюты: {}", templateValues.get("cryptocurrency"));
            log.trace("Ответ AI: {}", response);
            
            return response;
        } catch (Exception e) {
            log.error("Ошибка при получении ответа от AI для криптовалюты: {} - {}", 
                    templateValues.get("cryptocurrency"), e.getMessage(), e);
            return "Ошибка при получении анализа: " + e.getMessage();
        }
    }

    protected String getAiResponse(String promptText) {
        log.debug("Отправка простого запроса к AI");
        log.trace("Текст промпта: {}", promptText);
        
        try {
            UserMessage userMessage = new UserMessage(promptText);
            Prompt prompt = new Prompt(userMessage);

            String response = openAiChatModel.call(prompt)
                    .getResult().toString();
            
            log.debug("Получен ответ от AI");
            log.trace("Ответ AI: {}", response);
            
            return response;
        } catch (Exception e) {
            log.error("Ошибка при получении ответа от AI - {}", e.getMessage(), e);
            return "Ошибка при получении анализа: " + e.getMessage();
        }
    }
}
