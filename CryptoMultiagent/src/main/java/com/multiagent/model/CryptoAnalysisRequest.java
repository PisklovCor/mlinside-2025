package com.multiagent.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на анализ криптовалюты")
public class CryptoAnalysisRequest {

    @Schema(description = "Название криптовалюты для анализа", example = "Bitcoin")
    @NotBlank(message = "Название криптовалюты не может быть пустым")
    @Size(min = 2, max = 50, message = "Название криптовалюты должно быть от 2 до 50 символов")
    private String cryptocurrency;

    @Schema(description = "Временной период для анализа", example = "1 месяц")
    @NotBlank(message = "Временной период не может быть пустым")
    private String timeframe;

}
