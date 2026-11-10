package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.ValidacaoException;
import br.com.fiap3esa.movesmart.domain.favorito.Favorito;
import br.com.fiap3esa.movesmart.domain.favorito.FavoritoNotFoundException;
import br.com.fiap3esa.movesmart.domain.favorito.FavoritoRepository;
import br.com.fiap3esa.movesmart.domain.linha.DadosListagemLinha;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import br.com.fiap3esa.movesmart.domain.linha.LinhaNotFoundException;
import br.com.fiap3esa.movesmart.domain.linha.LinhaRepository;
import br.com.fiap3esa.movesmart.domain.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritoService {
    private final FavoritoRepository repository;
    private final LinhaRepository linhaRepository;

    @Transactional(readOnly = true)
    public List<DadosListagemLinha> listarFavoritos(Usuario usuario) {
        return repository.findAllByUsuarioId(usuario.getId()).stream()
                .map(favorito -> new DadosListagemLinha(favorito.getLinha()))
                .toList();
    }

    @Transactional
    public DadosListagemLinha favoritar(Usuario usuario, Long idLinha) {
        Linha linha = linhaRepository.findById(idLinha)
                .orElseThrow(() ->
                        new LinhaNotFoundException("ID da linha informado não existe!"));
        if (repository.existsByUsuarioIdAndLinhaId(usuario.getId(), idLinha)) {
            throw new ValidacaoException("A linha informada já está nos seus favoritos!");
        }
        repository.save(new Favorito(usuario, linha));
        return new DadosListagemLinha(linha);
    }

    @Transactional
    public void desfavoritar(Usuario usuario, Long idLinha) {
        Favorito favorito = repository.findByUsuarioIdAndLinhaId(usuario.getId(), idLinha)
                .orElseThrow(() ->
                        new FavoritoNotFoundException("A linha informada não está nos seus favoritos!"));
        repository.delete(favorito);
    }
}
