package com.multiagent.controller;

import com.multiagent.BaseTestConfiguration;
import com.multiagent.model.CryptoAnalysisRequest;
import com.multiagent.model.CryptoAnalysisResponse;
import com.multiagent.service.CryptoAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CryptoAnalysisControllerTest extends BaseTestConfiguration {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CryptoAnalysisService cryptoAnalysisService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/crypto/analyze должен возвращать успешный анализ")
    void testAnalyzeCryptocurrencyPost() throws Exception {
        // Arrange
        CryptoAnalysisRequest request = new CryptoAnalysisRequest("Bitcoin", "1 месяц");
        CryptoAnalysisResponse mockResponse = new CryptoAnalysisResponse(
                "Bitcoin", Collections.emptyList(), "ПОКУПАТЬ", 0.8);

        when(cryptoAnalysisService.analyzeCryptocurrency(anyString(), anyString()))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/crypto/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cryptocurrency").value("Bitcoin"))
                .andExpect(jsonPath("$.finalRecommendation").value("ПОКУПАТЬ"))
                .andExpect(jsonPath("$.averageConfidence").value(0.8));
    }

    @Test
    @DisplayName("POST /api/crypto/analyze должен возвращать 400 при невалидных данных")
    void testAnalyzeCryptocurrencyPostValidation() throws Exception {
        // Arrange
        CryptoAnalysisRequest invalidRequest = new CryptoAnalysisRequest("", ""); // Пустые поля

        // Act & Assert
        mockMvc.perform(post("/api/crypto/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/crypto/analyze/{crypto} должен возвращать анализ")
    void testAnalyzeCryptocurrencyGet() throws Exception {
        // Arrange
        CryptoAnalysisResponse mockResponse = new CryptoAnalysisResponse(
                "Ethereum", Collections.emptyList(), "ДЕРЖАТЬ", 0.6);

        when(cryptoAnalysisService.analyzeCryptocurrency(anyString(), anyString()))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/crypto/analyze/Ethereum")
                        .param("timeframe", "2 недели"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cryptocurrency").value("Ethereum"))
                .andExpect(jsonPath("$.finalRecommendation").value("ДЕРЖАТЬ"));
    }
}