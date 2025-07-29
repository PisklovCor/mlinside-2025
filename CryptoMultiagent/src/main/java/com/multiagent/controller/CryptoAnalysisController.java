package com.multiagent.controller;

import com.multiagent.model.CryptoAnalysisRequest;
import com.multiagent.model.CryptoAnalysisResponse;
import com.multiagent.service.CryptoAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Validated
@RestController
@RequestMapping("/api/crypto")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Crypto Analysis", description = "API для анализа криптовалют с использованием мульти-агентной системы")
public class CryptoAnalysisController {

    private final CryptoAnalysisService analysisService;

    @PostMapping("/analyze")
    @Operation(
            summary = "Анализ криптовалюты (синхронный)",
            description = "Выполняет анализ криптовалюты с использованием мульти-агентной системы в синхронном режиме"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Анализ успешно выполнен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CryptoAnalysisResponse.class),
                            examples = @ExampleObject(
                                    name = "Успешный анализ",
                                    value = """
                                            {
                                              "cryptocurrency": "Bitcoin",
                                              "agentAnalyses": [
                                                {
                                                  "agentName": "Technical Analysis Agent",
                                                  "analysis": "Технический анализ показывает восходящий тренд",
                                                  "recommendation": "ПОКУПАТЬ",
                                                  "confidence": 0.85
                                                }
                                              ],
                                              "finalRecommendation": "ПОКУПАТЬ",
                                              "averageConfidence": 0.85
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Ошибка валидации",
                                    value = """
                                            {
                                              "timestamp": "2024-01-15T10:30:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Название криптовалюты не может быть пустым"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    public ResponseEntity<CryptoAnalysisResponse> analyzeCryptocurrency(
            @Valid @RequestBody CryptoAnalysisRequest request) {

        try {
            CryptoAnalysisResponse response = analysisService.analyzeCryptocurrency(
                    request.getCryptocurrency(),
                    request.getTimeframe()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/analyze/async")
    @Operation(
            summary = "Анализ криптовалюты (асинхронный)",
            description = "Выполняет анализ криптовалюты с использованием мульти-агентной системы в асинхронном режиме"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Анализ успешно выполнен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CryptoAnalysisResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные запроса"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    public CompletableFuture<ResponseEntity<CryptoAnalysisResponse>> analyzeCryptocurrencyAsync(
            @Valid @RequestBody CryptoAnalysisRequest request) {

        return analysisService.analyzeCryptocurrencyAsync(
                        request.getCryptocurrency(),
                        request.getTimeframe()
                )
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().build());
    }

    @GetMapping("/analyze/{crypto}")
    @Operation(
            summary = "Анализ криптовалюты по названию (синхронный)",
            description = "Выполняет анализ криптовалюты по названию с использованием мульти-агентной системы в синхронном режиме"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Анализ успешно выполнен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CryptoAnalysisResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные параметры запроса"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    public ResponseEntity<CryptoAnalysisResponse> analyzeCryptocurrency(
            @Parameter(description = "Название криптовалюты", example = "Bitcoin")
            @PathVariable
            @Size(min = 2, max = 50, message = "Название криптовалюты должно быть от 2 до 50 символов")
            @Pattern(regexp = "^[a-zA-Z0-9\\s-]+$", message = "Недопустимые символы в названии криптовалюты")
            String crypto,
            @Parameter(description = "Временной период для анализа", example = "1 месяц")
            @RequestParam(defaultValue = "1 месяц") String timeframe) {

        try {
            CryptoAnalysisResponse response = analysisService.analyzeCryptocurrency(crypto, timeframe);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/analyze/{crypto}/async")
    @Operation(
            summary = "Анализ криптовалюты по названию (асинхронный)",
            description = "Выполняет анализ криптовалюты по названию с использованием мульти-агентной системы в асинхронном режиме"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Анализ успешно выполнен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CryptoAnalysisResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные параметры запроса"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    public CompletableFuture<ResponseEntity<CryptoAnalysisResponse>> analyzeCryptocurrencyAsync(
            @Parameter(description = "Название криптовалюты", example = "Bitcoin")
            @PathVariable
            @Size(min = 2, max = 50, message = "Название криптовалюты должно быть от 2 до 50 символов")
            String crypto,
            @Parameter(description = "Временной период для анализа", example = "1 месяц")
            @RequestParam(defaultValue = "1 месяц") String timeframe) {

        return analysisService.analyzeCryptocurrencyAsync(crypto, timeframe)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().build());
    }

    @GetMapping("/agents/status")
    @Operation(
            summary = "Статус агентов",
            description = "Получает текущий статус всех агентов в мульти-агентной системе"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Статус агентов успешно получен",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "Статус агентов",
                                    value = "Technical Analysis Agent: ACTIVE\nFundamental Analysis Agent: ACTIVE\nSentiment Analysis Agent: ACTIVE"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    public ResponseEntity<String> getAgentsStatus() {
        return ResponseEntity.ok(analysisService.getAgentsStatus());
    }
}
