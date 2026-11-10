package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.ValidacaoException;
import br.com.fiap3esa.movesmart.domain.favorito.Favorito;
import br.com.fiap3esa.movesmart.domain.favorito.FavoritoNotFoundException;
import br.com.fiap3esa.movesmart.domain.favorito.FavoritoRepository;
import br.com.fiap3esa.movesmart.domain.linha.DadosListagemLinha;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import br.com.fiap3esa.movesmart.domain.linha.LinhaNotFoundException;
import br.com.fiap3esa.movesmart.domain.linha.LinhaRepository;
import br.com.fiap3esa.movesmart.domain.usuario.Perfil;
import br.com.fiap3esa.movesmart.domain.usuario.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoritoServiceTest {

    @Mock
    private FavoritoRepository repository;

    @Mock
    private LinhaRepository linhaRepository;

    @InjectMocks
    private FavoritoService service;

    private final Usuario usuario = new Usuario(1L, "ana", "hash", Perfil.USER);
    private final Linha linha = new Linha(5L, 33000, "8000-10", 1, "TERMINAL A", "TERMINAL B", false, true);

    @Test
    void deveFavoritarLinha() {
        when(linhaRepository.findById(5L)).thenReturn(Optional.of(linha));
        when(repository.existsByUsuarioIdAndLinhaId(1L, 5L)).thenReturn(false);

        DadosListagemLinha resultado = service.favoritar(usuario, 5L);

        assertThat(resultado.letreiro()).isEqualTo("8000-10");
        verify(repository).save(any(Favorito.class));
    }

    @Test
    void naoDeveFavoritarLinhaInexistente() {
        when(linhaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.favoritar(usuario, 99L))
                .isInstanceOf(LinhaNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveFavoritarDuasVezesAMesmaLinha() {
        when(linhaRepository.findById(5L)).thenReturn(Optional.of(linha));
        when(repository.existsByUsuarioIdAndLinhaId(1L, 5L)).thenReturn(true);

        assertThatThrownBy(() -> service.favoritar(usuario, 5L))
                .isInstanceOf(ValidacaoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deveListarFavoritosDoUsuario() {
        when(repository.findAllByUsuarioId(1L)).thenReturn(List.of(new Favorito(usuario, linha)));

        List<DadosListagemLinha> resultado = service.listarFavoritos(usuario);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).id()).isEqualTo(5L);
    }

    @Test
    void deveRemoverFavorito() {
        Favorito favorito = new Favorito(usuario, linha);
        when(repository.findByUsuarioIdAndLinhaId(1L, 5L)).thenReturn(Optional.of(favorito));

        service.desfavoritar(usuario, 5L);

        verify(repository).delete(favorito);
    }

    @Test
    void naoDeveRemoverFavoritoQueNaoExiste() {
        when(repository.findByUsuarioIdAndLinhaId(1L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.desfavoritar(usuario, 5L))
                .isInstanceOf(FavoritoNotFoundException.class);
    }
}
