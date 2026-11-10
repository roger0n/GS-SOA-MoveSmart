package br.com.fiap3esa.movesmart.domain.linha;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DadosAtualizacaoLinha(
        @NotNull
        Long id,

        @Size(max = 20)
        String letreiro,

        @Min(1)
        @Max(2)
        Integer sentido,

        @Size(max = 100)
        String terminalPrincipal,

        @Size(max = 100)
        String terminalSecundario,

        Boolean circular) {
}
