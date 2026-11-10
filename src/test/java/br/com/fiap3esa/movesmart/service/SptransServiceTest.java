package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.ValidacaoException;
import br.com.fiap3esa.movesmart.domain.OrigemDado;
import br.com.fiap3esa.movesmart.domain.linha.DadosListagemLinha;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import br.com.fiap3esa.movesmart.domain.linha.LinhaNotFoundException;
import br.com.fiap3esa.movesmart.domain.linha.LinhaRepository;
import br.com.fiap3esa.movesmart.domain.parada.Parada;
import br.com.fiap3esa.movesmart.domain.parada.ParadaRepository;
import br.com.fiap3esa.movesmart.domain.previsao.DadosResultadoSincronizacao;
import br.com.fiap3esa.movesmart.domain.previsao.PrevisaoChegada;
import br.com.fiap3esa.movesmart.domain.previsao.PrevisaoChegadaRepository;
import br.com.fiap3esa.movesmart.infra.externo.sptrans.SptransClient;
import br.com.fiap3esa.movesmart.infra.externo.sptrans.SptransLinha;
import br.com.fiap3esa.movesmart.infra.externo.sptrans.SptransPrevisaoLinha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Testa a sincronização com a SPTrans sem acessar a internet: o cliente HTTP é simulado. */
@ExtendWith(MockitoExtension.class)
class SptransServiceTest {

    @Mock
    private SptransClient client;

    @Mock
    private LinhaRepository linhaRepository;

    @Mock
    private ParadaRepository paradaRepository;

    @Mock
    private PrevisaoChegadaRepository previsaoRepository;

    private SptransService service;

    private final Clock clock = Clock.fixed(Instant.parse("2026-11-10T15:00:00Z"), ZoneId.of("America/Sao_Paulo"));

    @BeforeEach
    void setUp() {
        service = new SptransService(client, linhaRepository, paradaRepository, previsaoRepository, clock);
    }

    @Test
    void deveCriarLinhaNovaComLetreiroCompleto() {
        when(client.buscarLinhas("8000")).thenReturn(List.of(
                new SptransLinha(33000, false, "8000", 10, 1, "TERMINAL A", "TERMINAL B")));
        when(linhaRepository.findByCodigoSptrans(33000)).thenReturn(Optional.empty());
        when(linhaRepository.save(any(Linha.class))).thenAnswer(inv -> inv.getArgument(0));

        List<DadosListagemLinha> resultado = service.sincronizarLinhas("8000");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).letreiro()).isEqualTo("8000-10");
        assertThat(resultado.get(0).codigoSptrans()).isEqualTo(33000);
    }

    @Test
    void naoDeveDuplicarLinhaQueJaExisteApenasAtualizar() {
        Linha existente = new Linha(1L, 33000, "ANTIGO", 1, "A", "B", false, true);
        when(client.buscarLinhas("8000")).thenReturn(List.of(
                new SptransLinha(33000, true, "8000", 10, 2, "NOVO A", "NOVO B")));
        when(linhaRepository.findByCodigoSptrans(33000)).thenReturn(Optional.of(existente));
        when(linhaRepository.save(any(Linha.class))).thenAnswer(inv -> inv.getArgument(0));

        service.sincronizarLinhas("8000");

        assertThat(existente.getLetreiro()).isEqualTo("8000-10");
        assertThat(existente.getSentido()).isEqualTo(2);
        assertThat(existente.isCircular()).isTrue();
        assertThat(existente.getId()).isEqualTo(1L);
    }

    @Test
    void naoDeveSincronizarLinhasSemTermoDeBusca() {
        assertThatThrownBy(() -> service.sincronizarLinhas("  "))
                .isInstanceOf(ValidacaoException.class);
        verify(linhaRepository, never()).save(any());
    }

    @Test
    void deveSalvarPrevisoesComOrigemSptransEIgnorarHorarioInvalido() {
        Linha linha = new Linha(1L, 33000, "8000-10", 1, "A", "B", false, true);
        when(linhaRepository.findById(1L)).thenReturn(Optional.of(linha));
        when(client.buscarPrevisaoDaLinha(33000)).thenReturn(new SptransPrevisaoLinha("12:00", List.of(
                new SptransPrevisaoLinha.Parada(700, "Parada Teste", -23.5, -46.6, List.of(
                        new SptransPrevisaoLinha.Veiculo("11001", "12:07", true),
                        new SptransPrevisaoLinha.Veiculo("11002", "invalido", false))))));
        when(paradaRepository.findByCodigoSptrans(700)).thenReturn(Optional.empty());
        when(paradaRepository.save(any(Parada.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosResultadoSincronizacao resultado = service.sincronizarPrevisoes(1L);

        assertThat(resultado.previsoesSalvas()).isEqualTo(1);
        ArgumentCaptor<PrevisaoChegada> captor = ArgumentCaptor.forClass(PrevisaoChegada.class);
        verify(previsaoRepository).save(captor.capture());
        assertThat(captor.getValue().getHorarioPrevisto()).isEqualTo(LocalTime.of(12, 7));
        assertThat(captor.getValue().getOrigem()).isEqualTo(OrigemDado.SPTRANS_OLHO_VIVO);
        assertThat(captor.getValue().getConsultadoEm()).isEqualTo(LocalDateTime.of(2026, 11, 10, 12, 0));
    }

    @Test
    void deveRetornarZeroQuandoNaoHaOnibusCirculando() {
        Linha linha = new Linha(1L, 33000, "8000-10", 1, "A", "B", false, true);
        when(linhaRepository.findById(1L)).thenReturn(Optional.of(linha));
        when(client.buscarPrevisaoDaLinha(33000)).thenReturn(new SptransPrevisaoLinha(null, null));

        assertThat(service.sincronizarPrevisoes(1L).previsoesSalvas()).isZero();
        verify(previsaoRepository, never()).save(any());
    }

    @Test
    void naoDeveSincronizarPrevisoesDeLinhaInexistente() {
        when(linhaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.sincronizarPrevisoes(99L))
                .isInstanceOf(LinhaNotFoundException.class);
    }
}
