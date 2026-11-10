package br.com.fiap3esa.movesmart.domain.previsao;

import br.com.fiap3esa.movesmart.domain.OrigemDado;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record DadosDetalhamentoPrevisao(
        String parada,
        String prefixoVeiculo,
        LocalTime horarioPrevisto,
        Boolean acessivel,
        LocalDateTime consultadoEm,
        OrigemDado origem) {
    public DadosDetalhamentoPrevisao(PrevisaoChegada previsao) {
        this(
                previsao.getParada().getNome(),
                previsao.getPrefixoVeiculo(),
                previsao.getHorarioPrevisto(),
                previsao.getAcessivel(),
                previsao.getConsultadoEm(),
                previsao.getOrigem()
        );
    }
}
