package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.ValidacaoException;
import br.com.fiap3esa.movesmart.domain.linha.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinhaServiceTest {

    @Mock
    private LinhaRepository repository;

    @InjectMocks
    private LinhaService service;

    private Linha linhaExistente() {
        return new Linha(1L, 33000, "8000-10", 1, "TERMINAL A", "TERMINAL B", false, true);
    }

    @Test
    void deveCadastrarLinha() {
        when(repository.existsByCodigoSptrans(33000)).thenReturn(false);
        when(repository.save(any(Linha.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoLinha resultado = service.cadastrarLinha(
                new DadosCadastroLinha(33000, "8000-10", 1, "TERMINAL A", "TERMINAL B", false));

        assertThat(resultado.letreiro()).isEqualTo("8000-10");
        assertThat(resultado.ativa()).isTrue();
        verify(repository).save(any(Linha.class));
    }

    @Test
    void naoDeveCadastrarLinhaComCodigoSptransRepetido() {
        when(repository.existsByCodigoSptrans(33000)).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrarLinha(
                new DadosCadastroLinha(33000, "8000-10", 1, null, null, false)))
                .isInstanceOf(ValidacaoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deveListarLinhasAtivasSemBusca() {
        Pageable paginacao = PageRequest.of(0, 10);
        Page<Linha> pagina = new PageImpl<>(List.of(linhaExistente()), paginacao, 1);
        when(repository.findAllByAtivaTrue(paginacao)).thenReturn(pagina);

        Page<DadosListagemLinha> resultado = service.listarLinhas(null, paginacao);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).letreiro()).isEqualTo("8000-10");
    }

    @Test
    void deveBuscarLinhasPeloLetreiro() {
        Pageable paginacao = PageRequest.of(0, 10);
        Page<Linha> pagina = new PageImpl<>(List.of(linhaExistente()), paginacao, 1);
        when(repository.findAllByAtivaTrueAndLetreiroContainingIgnoreCase("8000", paginacao)).thenReturn(pagina);

        Page<DadosListagemLinha> resultado = service.listarLinhas("  8000 ", paginacao);

        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    void deveDetalharLinha() {
        when(repository.findById(1L)).thenReturn(Optional.of(linhaExistente()));

        DadosDetalhamentoLinha resultado = service.detalharLinha(1L);

        assertThat(resultado.codigoSptrans()).isEqualTo(33000);
    }

    @Test
    void deveLancarExcecaoAoDetalharLinhaInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.detalharLinha(99L))
                .isInstanceOf(LinhaNotFoundException.class);
    }

    @Test
    void deveAtualizarLinha() {
        when(repository.findById(1L)).thenReturn(Optional.of(linhaExistente()));
        when(repository.save(any(Linha.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoLinha resultado = service.atualizarLinha(
                new DadosAtualizacaoLinha(1L, "8000-21", null, null, null, null));

        assertThat(resultado.letreiro()).isEqualTo("8000-21");
    }

    @Test
    void deveExcluirLinhaLogicamente() {
        Linha linha = linhaExistente();
        when(repository.findById(1L)).thenReturn(Optional.of(linha));

        service.excluirLinha(1L);

        assertThat(linha.isAtiva()).isFalse();
        verify(repository).save(linha);
    }
}
