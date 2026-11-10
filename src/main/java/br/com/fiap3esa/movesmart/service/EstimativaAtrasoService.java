package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.estimativa.*;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import br.com.fiap3esa.movesmart.domain.linha.LinhaNotFoundException;
import br.com.fiap3esa.movesmart.domain.linha.LinhaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

/** Guarda as estimativas geradas pelo modelo de ML (notebook do grupo) e as devolve ao aplicativo. */
@Service
@RequiredArgsConstructor
public class EstimativaAtrasoService {
    private final EstimativaAtrasoRepository repository;
    private final LinhaRepository linhaRepository;
    private final Clock clock;

    @Transactional
    public DadosDetalhamentoEstimativaAtraso cadastrarEstimativa(Long idLinha, DadosCadastroEstimativaAtraso dados) {
        Linha linha = linhaRepository.findById(idLinha)
                .orElseThrow(() ->
                        new LinhaNotFoundException("ID da linha informado não existe!"));
        EstimativaAtraso salva = repository.save(new EstimativaAtraso(linha, dados, LocalDateTime.now(clock)));
        return new DadosDetalhamentoEstimativaAtraso(salva);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoEstimativaAtraso consultarUltimaEstimativa(Long idLinha) {
        if (!linhaRepository.existsById(idLinha)) {
            throw new LinhaNotFoundException("ID da linha informado não existe!");
        }
        return repository.findFirstByLinhaIdOrderByGeradoEmDesc(idLinha)
                .map(DadosDetalhamentoEstimativaAtraso::new)
                .orElseThrow(() ->
                        new EstimativaAtrasoNotFoundException("Ainda não há estimativa de atraso para esta linha!"));
    }
}
