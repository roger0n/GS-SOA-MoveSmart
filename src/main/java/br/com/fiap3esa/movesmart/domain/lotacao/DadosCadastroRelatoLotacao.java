package br.com.fiap3esa.movesmart.domain.lotacao;

import jakarta.validation.constraints.NotNull;

public record DadosCadastroRelatoLotacao(
        @NotNull
        NivelLotacao nivel) {
}
