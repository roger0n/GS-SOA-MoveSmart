package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.OrigemDado;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import br.com.fiap3esa.movesmart.domain.linha.LinhaNotFoundException;
import br.com.fiap3esa.movesmart.domain.linha.LinhaRepository;
import br.com.fiap3esa.movesmart.domain.lotacao.*;
import br.com.fiap3esa.movesmart.domain.usuario.Perfil;
import br.com.fiap3esa.movesmart.domain.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LotacaoServiceTest {

    @Mock
    private RelatoLotacaoRepository repository;

    @Mock
    private LinhaRepository linhaRepository;

    private LotacaoService service;

    // 15:00 UTC = 12:00 em São Paulo
    private final Clock clock = Clock.fixed(Instant.parse("2026-11-10T15:00:00Z"), ZoneId.of("America/Sao_Paulo"));
    private final LocalDateTime agora = LocalDateTime.of(2026, 11, 10, 12, 0);
    private final Linha linha = new Linha(1L, 33000, "8000-10", 1, "TERMINAL A", "TERMINAL B", false, true);
    private final Usuario usuario = new Usuario(1L, "ana", "hash", Perfil.USER);

    @BeforeEach
    void setUp() {
        service = new LotacaoService(repository, linhaRepository, clock);
    }

    private RelatoLotacao relato(NivelLotacao nivel) {
        return new RelatoLotacao(linha, usuario, nivel, agora.minusMinutes(5));
    }

    @Test
    void deveRegistrarRelatoComOrigemUsuarioEHorarioAtual() {
        when(linhaRepository.findById(1L)).thenReturn(Optional.of(linha));
        when(repository.save(any(RelatoLotacao.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoRelatoLotacao resultado = service.registrarRelato(
                1L, new DadosCadastroRelatoLotacao(NivelLotacao.ALTA), usuario);

        assertThat(resultado.nivel()).isEqualTo(NivelLotacao.ALTA);
        assertThat(resultado.origem()).isEqualTo(OrigemDado.RELATO_USUARIO);
        assertThat(resultado.criadoEm()).isEqualTo(agora);
        assertThat(resultado.idLinha()).isEqualTo(1L);
    }

    @Test
    void naoDeveRegistrarRelatoDeLinhaInexistente() {
        when(linhaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrarRelato(
                99L, new DadosCadastroRelatoLotacao(NivelLotacao.BAIXA), usuario))
                .isInstanceOf(LinhaNotFoundException.class);
    }

    @Test
    void semRelatosRecentesDeveRetornarNivelNulo() {
        when(linhaRepository.existsById(1L)).thenReturn(true);
        when(repository.findByLinhaIdAndCriadoEmAfter(eq(1L), any(LocalDateTime.class))).thenReturn(List.of());

        DadosResumoLotacao resumo = service.consultarLotacao(1L);

        assertThat(resumo.nivel()).isNull();
        assertThat(resumo.totalRelatos()).isZero();
        assertThat(resumo.origem()).isEqualTo(OrigemDado.RELATO_USUARIO);
    }

    @Test
    void deveRetornarONivelMaisRelatado() {
        when(linhaRepository.existsById(1L)).thenReturn(true);
        when(repository.findByLinhaIdAndCriadoEmAfter(eq(1L), any(LocalDateTime.class))).thenReturn(
                List.of(relato(NivelLotacao.MEDIA), relato(NivelLotacao.MEDIA), relato(NivelLotacao.BAIXA)));

        DadosResumoLotacao resumo = service.consultarLotacao(1L);

        assertThat(resumo.nivel()).isEqualTo(NivelLotacao.MEDIA);
        assertThat(resumo.totalRelatos()).isEqualTo(3);
    }

    @Test
    void emCasoDeEmpateDeveValerONivelMaisCritico() {
        when(linhaRepository.existsById(1L)).thenReturn(true);
        when(repository.findByLinhaIdAndCriadoEmAfter(eq(1L), any(LocalDateTime.class))).thenReturn(
                List.of(relato(NivelLotacao.BAIXA), relato(NivelLotacao.ALTA)));

        assertThat(service.consultarLotacao(1L).nivel()).isEqualTo(NivelLotacao.ALTA);
    }

    @Test
    void deveConsiderarApenasRelatosDosUltimos30Minutos() {
        when(linhaRepository.existsById(1L)).thenReturn(true);
        when(repository.findByLinhaIdAndCriadoEmAfter(eq(1L), any(LocalDateTime.class))).thenReturn(List.of());

        service.consultarLotacao(1L);

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(repository).findByLinhaIdAndCriadoEmAfter(eq(1L), captor.capture());
        assertThat(captor.getValue()).isEqualTo(agora.minusMinutes(30));
    }

    @Test
    void naoDeveConsultarLotacaoDeLinhaInexistente() {
        when(linhaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.consultarLotacao(99L))
                .isInstanceOf(LinhaNotFoundException.class);
    }
}
