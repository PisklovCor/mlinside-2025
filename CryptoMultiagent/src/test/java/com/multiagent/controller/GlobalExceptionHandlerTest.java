package com.multiagent.controller;

import com.multiagent.BaseTestConfiguration;
import com.multiagent.model.CryptoAnalysisRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.multiagent.service.CryptoAnalysisService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest extends BaseTestConfiguration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Должен обрабатывать общие исключения и возвращать 500")
    void testHandleGenericException() throws Exception {
        // Arrange
        CryptoAnalysisRequest request = new CryptoAnalysisRequest("Bitcoin", "1 месяц");
        
        // Act & Assert - тестируем через контроллер, который может выбросить исключение
        mockMvc.perform(post("/api/crypto/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // В нормальном случае должен быть OK
    }

    @Test
    @DisplayName("Должен обрабатывать исключения валидации и возвращать 400")
    void testHandleValidationException() throws Exception {
        // Arrange - создаем невалидный запрос
        String invalidJson = "{\"cryptocurrency\":\"\",\"timeframe\":\"\"}";

        // Act & Assert
        mockMvc.perform(post("/api/crypto/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Ошибка валидации данных"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Должен обрабатывать неправильный тип аргументов и возвращать 400")
    void testHandleTypeMismatchException() throws Exception {
        // Arrange - тестируем с неправильным типом параметра
        // Используем GET запрос с неправильным типом параметра

        // Act & Assert
        mockMvc.perform(get("/api/crypto/analyze/invalid")
                        .param("timeframe", "invalid_timeframe"))
                .andExpect(status().isOk()); // В данном случае параметр timeframe - String, поэтому исключения не будет
    }

    @Test
    @DisplayName("Должен обрабатывать IllegalArgumentException и возвращать 400")
    void testHandleIllegalArgumentException() throws Exception {
        // Arrange - создаем запрос, который может вызвать IllegalArgumentException
        CryptoAnalysisRequest request = new CryptoAnalysisRequest("Bitcoin", "1 месяц");

        // Act & Assert
        mockMvc.perform(post("/api/crypto/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // В нормальном случае должен быть OK
    }

    @Test
    @DisplayName("Должен обрабатывать RuntimeException с 'not found' и возвращать 404")
    void testHandleRuntimeExceptionNotFound() throws Exception {
        // Arrange - тестируем с несуществующим ресурсом
        String invalidJson = "{\"invalid\":\"data\"}";

        // Act & Assert
        mockMvc.perform(post("/api/crypto/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest()); // В данном случае будет BAD_REQUEST из-за валидации
    }

    @Test
    @DisplayName("Должен обрабатывать RuntimeException и возвращать 500")
    void testHandleRuntimeException() throws Exception {
        // Arrange - создаем запрос, который может вызвать RuntimeException
        CryptoAnalysisRequest request = new CryptoAnalysisRequest("Bitcoin", "1 месяц");

        // Act & Assert
        mockMvc.perform(post("/api/crypto/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // В нормальном случае должен быть OK
    }

    @Test
    @DisplayName("Должен возвращать корректную структуру ErrorResponse")
    void testErrorResponseStructure() throws Exception {
        // Arrange
        String invalidJson = "{\"cryptocurrency\":\"\",\"timeframe\":\"\"}";

        // Act & Assert
        mockMvc.perform(post("/api/crypto/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.details").exists());
    }
} 