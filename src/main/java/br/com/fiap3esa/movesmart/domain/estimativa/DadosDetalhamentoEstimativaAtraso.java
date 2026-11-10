package br.com.fiap3esa.movesmart.domain.estimativa;

import br.com.fiap3esa.movesmart.domain.OrigemDado;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DadosDetalhamentoEstimativaAtraso(
        Long id,
        Long idLinha,
        Integer minutosEstimados,
        BigDecimal probabilidadeAtraso,
        String modeloVersao,
        LocalDateTime geradoEm,
        OrigemDado origem) {
    public DadosDetalhamentoEstimativaAtraso(EstimativaAtraso estimativa) {
        this(
                estimativa.getId(),
                estimativa.getLinha().getId(),
                estimativa.getMinutosEstimados(),
                estimativa.getProbabilidadeAtraso(),
                estimativa.getModeloVersao(),
                estimativa.getGeradoEm(),
                estimativa.getOrigem()
        );
    }
}
