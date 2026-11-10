package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.ValidacaoException;
import br.com.fiap3esa.movesmart.domain.linha.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LinhaService {
    private final LinhaRepository repository;

    @Transactional
    public DadosDetalhamentoLinha cadastrarLinha(DadosCadastroLinha dados) {
        if (repository.existsByCodigoSptrans(dados.codigoSptrans())) {
            throw new ValidacaoException("Já existe uma linha com o código SPTrans informado!");
        }
        Linha saved = repository.save(new Linha(dados));
        return new DadosDetalhamentoLinha(saved);
    }

    public Page<DadosListagemLinha> listarLinhas(String busca, Pageable paginacao) {
        Page<Linha> pagina = (busca == null || busca.isBlank())
                ? repository.findAllByAtivaTrue(paginacao)
                : repository.findAllByAtivaTrueAndLetreiroContainingIgnoreCase(busca.trim(), paginacao);
        return pagina.map(DadosListagemLinha::new);
    }

    public DadosDetalhamentoLinha detalharLinha(Long id) {
        Linha linha = repository.findById(id)
                .orElseThrow(() ->
                        new LinhaNotFoundException("ID da linha informado não existe!"));
        return new DadosDetalhamentoLinha(linha);
    }

    @Transactional
    public DadosDetalhamentoLinha atualizarLinha(DadosAtualizacaoLinha dados) {
        Linha linha = repository.findById(dados.id())
                .orElseThrow(() ->
                        new LinhaNotFoundException("ID da linha informado não existe!"));
        linha.atualizarInformacoes(dados);
        Linha saved = repository.save(linha);
        return new DadosDetalhamentoLinha(saved);
    }

    @Transactional
    public void excluirLinha(Long id) {
        Linha linha = repository.findById(id)
                .orElseThrow(() ->
                        new LinhaNotFoundException("ID da linha informado não existe!"));
        linha.excluir();
        repository.save(linha);
    }
}
