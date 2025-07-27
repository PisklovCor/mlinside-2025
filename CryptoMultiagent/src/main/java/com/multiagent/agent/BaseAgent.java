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

    protected double extractConfidence(String analysis) {
        String lowerAnalysis = analysis.toLowerCase();
        if (lowerAnalysis.contains("высокая уверенность") ||
                lowerAnalysis.contains("настоятельно рекомендую") ||
                lowerAnalysis.contains("очень уверен")) {
            return 0.9;
        } else if (lowerAnalysis.contains("умеренная уверенность") ||
                lowerAnalysis.contains("рекомендую") ||
                lowerAnalysis.contains("довольно уверен")) {
            return 0.7;
        } else if (lowerAnalysis.contains("низкая уверенность") ||
                lowerAnalysis.contains("осторожно") ||
                lowerAnalysis.contains("не уверен")) {
            return 0.5;
        } else if (lowerAnalysis.contains("неопределенность") ||
                lowerAnalysis.contains("сложно сказать")) {
            return 0.3;
        }
        return 0.6; // значение по умолчанию
    }
}
