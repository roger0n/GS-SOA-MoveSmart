package br.com.fiap3esa.movesmart.domain.lotacao;

import br.com.fiap3esa.movesmart.domain.OrigemDado;

/** Lotação agregada a partir dos relatos recentes. Nível é nulo quando não há relatos na janela. */
public record DadosResumoLotacao(
        NivelLotacao nivel,
        int totalRelatos,
        int janelaMinutos,
        OrigemDado origem,
        String observacao) {
}
