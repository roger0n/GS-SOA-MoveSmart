package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.OrigemDado;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import br.com.fiap3esa.movesmart.domain.linha.LinhaNotFoundException;
import br.com.fiap3esa.movesmart.domain.linha.LinhaRepository;
import br.com.fiap3esa.movesmart.domain.parada.Parada;
import br.com.fiap3esa.movesmart.domain.previsao.DadosDetalhamentoPrevisao;
import br.com.fiap3esa.movesmart.domain.previsao.PrevisaoChegada;
import br.com.fiap3esa.movesmart.domain.previsao.PrevisaoChegadaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrevisaoServiceTest {

    @Mock
    private PrevisaoChegadaRepository repository;

    @Mock
    private LinhaRepository linhaRepository;

    @InjectMocks
    private PrevisaoService service;

    @Test
    void deveListarUltimasPrevisoesDaLinha() {
        Linha linha = new Linha(1L, 33000, "8000-10", 1, "A", "B", false, true);
        PrevisaoChegada previsao = new PrevisaoChegada(linha, new Parada(700, "Parada Teste", -23.5, -46.6),
                "11001", LocalTime.of(12, 7), true, LocalDateTime.of(2026, 11, 10, 12, 0));
        when(linhaRepository.existsById(1L)).thenReturn(true);
        when(repository.findTop50ByLinhaIdOrderByConsultadoEmDesc(1L)).thenReturn(List.of(previsao));

        List<DadosDetalhamentoPrevisao> resultado = service.listarUltimasPrevisoes(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).parada()).isEqualTo("Parada Teste");
        assertThat(resultado.get(0).origem()).isEqualTo(OrigemDado.SPTRANS_OLHO_VIVO);
    }

    @Test
    void deveLancarExcecaoParaLinhaInexistente() {
        when(linhaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.listarUltimasPrevisoes(99L))
                .isInstanceOf(LinhaNotFoundException.class);
    }
}
