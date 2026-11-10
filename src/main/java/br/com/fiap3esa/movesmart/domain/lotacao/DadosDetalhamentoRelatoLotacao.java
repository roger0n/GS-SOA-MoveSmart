package br.com.fiap3esa.movesmart.domain.lotacao;

import br.com.fiap3esa.movesmart.domain.OrigemDado;

import java.time.LocalDateTime;

public record DadosDetalhamentoRelatoLotacao(
        Long id,
        Long idLinha,
        NivelLotacao nivel,
        LocalDateTime criadoEm,
        OrigemDado origem) {
    public DadosDetalhamentoRelatoLotacao(RelatoLotacao relato) {
        this(
                relato.getId(),
                relato.getLinha().getId(),
                relato.getNivel(),
                relato.getCriadoEm(),
                relato.getOrigem()
        );
    }
}
