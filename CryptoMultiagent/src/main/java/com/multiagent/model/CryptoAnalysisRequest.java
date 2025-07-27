package com.multiagent.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoAnalysisRequest {

    @NotBlank(message = "Название криптовалюты не может быть пустым")
    @Size(min = 2, max = 50, message = "Название криптовалюты должно быть от 2 до 50 символов")
    private String cryptocurrency;

    @NotBlank(message = "Временной период не может быть пустым")
    private String timeframe;

}
