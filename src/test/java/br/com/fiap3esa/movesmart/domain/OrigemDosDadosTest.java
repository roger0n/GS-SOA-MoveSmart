package br.com.fiap3esa.movesmart.domain;

import br.com.fiap3esa.movesmart.domain.estimativa.DadosCadastroEstimativaAtraso;
import br.com.fiap3esa.movesmart.domain.estimativa.EstimativaAtraso;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import br.com.fiap3esa.movesmart.domain.lotacao.NivelLotacao;
import br.com.fiap3esa.movesmart.domain.lotacao.RelatoLotacao;
import br.com.fiap3esa.movesmart.domain.parada.Parada;
import br.com.fiap3esa.movesmart.domain.previsao.PrevisaoChegada;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/** Garante a regra central do projeto: cada tipo de informação carrega a sua origem. */
class OrigemDosDadosTest {

    private final LocalDateTime agora = LocalDateTime.of(2026, 11, 10, 12, 0);

    @Test
    void previsaoDeChegadaVemDaSptrans() {
        PrevisaoChegada previsao = new PrevisaoChegada(
                new Linha(), new Parada(700, "Parada Teste", -23.5, -46.6), "11001", LocalTime.of(12, 7), true, agora);

        assertThat(previsao.getOrigem()).isEqualTo(OrigemDado.SPTRANS_OLHO_VIVO);
    }

    @Test
    void relatoDeLotacaoVemDoUsuario() {
        RelatoLotacao relato = new RelatoLotacao(new Linha(), null, NivelLotacao.ALTA, agora);

        assertThat(relato.getOrigem()).isEqualTo(OrigemDado.RELATO_USUARIO);
        assertThat(relato.getNivel()).isEqualTo(NivelLotacao.ALTA);
    }

    @Test
    void estimativaDeAtrasoVemDoModeloDeMl() {
        EstimativaAtraso estimativa = new EstimativaAtraso(
                new Linha(), new DadosCadastroEstimativaAtraso(7, new BigDecimal("0.620"), "rf-v1"), agora);

        assertThat(estimativa.getOrigem()).isEqualTo(OrigemDado.MODELO_ML);
        assertThat(estimativa.getModeloVersao()).isEqualTo("rf-v1");
    }
}
