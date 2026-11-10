package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.OrigemDado;
import br.com.fiap3esa.movesmart.domain.estimativa.*;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import br.com.fiap3esa.movesmart.domain.linha.LinhaNotFoundException;
import br.com.fiap3esa.movesmart.domain.linha.LinhaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstimativaAtrasoServiceTest {

    @Mock
    private EstimativaAtrasoRepository repository;

    @Mock
    private LinhaRepository linhaRepository;

    private EstimativaAtrasoService service;

    private final Clock clock = Clock.fixed(Instant.parse("2026-11-10T15:00:00Z"), ZoneId.of("America/Sao_Paulo"));
    private final Linha linha = new Linha(1L, 33000, "8000-10", 1, "TERMINAL A", "TERMINAL B", false, true);

    @BeforeEach
    void setUp() {
        service = new EstimativaAtrasoService(repository, linhaRepository, clock);
    }

    @Test
    void deveCadastrarEstimativaComOrigemModeloMl() {
        when(linhaRepository.findById(1L)).thenReturn(Optional.of(linha));
        when(repository.save(any(EstimativaAtraso.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoEstimativaAtraso resultado = service.cadastrarEstimativa(
                1L, new DadosCadastroEstimativaAtraso(7, new BigDecimal("0.620"), "rf-v1"));

        assertThat(resultado.minutosEstimados()).isEqualTo(7);
        assertThat(resultado.origem()).isEqualTo(OrigemDado.MODELO_ML);
        assertThat(resultado.geradoEm()).isEqualTo(LocalDateTime.of(2026, 11, 10, 12, 0));
    }

    @Test
    void naoDeveCadastrarEstimativaParaLinhaInexistente() {
        when(linhaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cadastrarEstimativa(
                99L, new DadosCadastroEstimativaAtraso(7, new BigDecimal("0.5"), "rf-v1")))
                .isInstanceOf(LinhaNotFoundException.class);
    }

    @Test
    void deveConsultarUltimaEstimativa() {
        EstimativaAtraso estimativa = new EstimativaAtraso(
                linha, new DadosCadastroEstimativaAtraso(12, new BigDecimal("0.800"), "rf-v2"),
                LocalDateTime.of(2026, 11, 10, 11, 0));
        when(linhaRepository.existsById(1L)).thenReturn(true);
        when(repository.findFirstByLinhaIdOrderByGeradoEmDesc(1L)).thenReturn(Optional.of(estimativa));

        DadosDetalhamentoEstimativaAtraso resultado = service.consultarUltimaEstimativa(1L);

        assertThat(resultado.modeloVersao()).isEqualTo("rf-v2");
        assertThat(resultado.probabilidadeAtraso()).isEqualByComparingTo("0.8");
    }

    @Test
    void deveLancarExcecaoQuandoNaoHaEstimativa() {
        when(linhaRepository.existsById(1L)).thenReturn(true);
        when(repository.findFirstByLinhaIdOrderByGeradoEmDesc(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultarUltimaEstimativa(1L))
                .isInstanceOf(EstimativaAtrasoNotFoundException.class);
    }

    @Test
    void deveLancarExcecaoAoConsultarEstimativaDeLinhaInexistente() {
        when(linhaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.consultarUltimaEstimativa(99L))
                .isInstanceOf(LinhaNotFoundException.class);
    }
}
