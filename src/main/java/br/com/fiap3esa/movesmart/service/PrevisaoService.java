package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.linha.LinhaNotFoundException;
import br.com.fiap3esa.movesmart.domain.linha.LinhaRepository;
import br.com.fiap3esa.movesmart.domain.previsao.DadosDetalhamentoPrevisao;
import br.com.fiap3esa.movesmart.domain.previsao.PrevisaoChegadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrevisaoService {
    private final PrevisaoChegadaRepository repository;
    private final LinhaRepository linhaRepository;

    @Transactional(readOnly = true)
    public List<DadosDetalhamentoPrevisao> listarUltimasPrevisoes(Long idLinha) {
        if (!linhaRepository.existsById(idLinha)) {
            throw new LinhaNotFoundException("ID da linha informado não existe!");
        }
        return repository.findTop50ByLinhaIdOrderByConsultadoEmDesc(idLinha).stream()
                .map(DadosDetalhamentoPrevisao::new)
                .toList();
    }
}
