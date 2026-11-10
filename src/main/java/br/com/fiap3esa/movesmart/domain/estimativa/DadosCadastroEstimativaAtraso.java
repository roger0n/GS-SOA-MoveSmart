package br.com.fiap3esa.movesmart.domain.estimativa;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DadosCadastroEstimativaAtraso(
        @NotNull
        @Min(0)
        Integer minutosEstimados,

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        BigDecimal probabilidadeAtraso,

        @NotBlank
        String modeloVersao) {
}
