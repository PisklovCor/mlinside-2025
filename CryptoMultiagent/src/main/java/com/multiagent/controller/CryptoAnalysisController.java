package com.multiagent.controller;

import com.multiagent.model.CryptoAnalysisRequest;
import com.multiagent.model.CryptoAnalysisResponse;
import com.multiagent.service.CryptoAnalysisService;
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
public class CryptoAnalysisController {

    private final CryptoAnalysisService analysisService;

    @PostMapping("/analyze")
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
    public ResponseEntity<CryptoAnalysisResponse> analyzeCryptocurrency(
            @PathVariable
            @Size(min = 2, max = 50, message = "Название криптовалюты должно быть от 2 до 50 символов")
            @Pattern(regexp = "^[a-zA-Z0-9\\s-]+$", message = "Недопустимые символы в названии криптовалюты")
            String crypto,
            @RequestParam(defaultValue = "1 месяц") String timeframe) {

        try {
            CryptoAnalysisResponse response = analysisService.analyzeCryptocurrency(crypto, timeframe);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/analyze/{crypto}/async")
    public CompletableFuture<ResponseEntity<CryptoAnalysisResponse>> analyzeCryptocurrencyAsync(
            @PathVariable
            @Size(min = 2, max = 50, message = "Название криптовалюты должно быть от 2 до 50 символов")
            String crypto,
            @RequestParam(defaultValue = "1 месяц") String timeframe) {

        return analysisService.analyzeCryptocurrencyAsync(crypto, timeframe)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().build());
    }

    @GetMapping("/agents/status")
    public ResponseEntity<String> getAgentsStatus() {
        return ResponseEntity.ok(analysisService.getAgentsStatus());
    }
}
