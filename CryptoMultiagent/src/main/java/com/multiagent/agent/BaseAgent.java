package com.multiagent.agent;

import com.multiagent.model.AgentAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.messages.UserMessage;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Map;

@RequiredArgsConstructor
public abstract class BaseAgent {

    protected final ChatModel openAiChatModel;

    public abstract AgentAnalysis analyze(String cryptocurrency, String timeframe);

    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    protected String getAiResponse(String promptText, Map<String, Object> templateValues) {
        try {
            PromptTemplate promptTemplate = new PromptTemplate(promptText);
            Prompt prompt = promptTemplate.create(templateValues);

            return openAiChatModel.call(prompt)
                    .getResult().toString();
        } catch (Exception e) {
            return "Ошибка при получении анализа: " + e.getMessage();
        }
    }

    protected String getAiResponse(String promptText) {
        try {
            UserMessage userMessage = new UserMessage(promptText);
            Prompt prompt = new Prompt(userMessage);

            return openAiChatModel.call(prompt)
                    .getResult().toString();
        } catch (Exception e) {
            return "Ошибка при получении анализа: " + e.getMessage();
        }
    }
}
