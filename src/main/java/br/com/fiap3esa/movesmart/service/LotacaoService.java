package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.OrigemDado;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import br.com.fiap3esa.movesmart.domain.linha.LinhaNotFoundException;
import br.com.fiap3esa.movesmart.domain.linha.LinhaRepository;
import br.com.fiap3esa.movesmart.domain.lotacao.*;
import br.com.fiap3esa.movesmart.domain.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LotacaoService {
    public static final int JANELA_MINUTOS = 30;

    private final RelatoLotacaoRepository repository;
    private final LinhaRepository linhaRepository;
    private final Clock clock;

    @Transactional
    public DadosDetalhamentoRelatoLotacao registrarRelato(Long idLinha, DadosCadastroRelatoLotacao dados, Usuario usuario) {
        Linha linha = linhaRepository.findById(idLinha)
                .orElseThrow(() ->
                        new LinhaNotFoundException("ID da linha informado não existe!"));
        RelatoLotacao relato = new RelatoLotacao(linha, usuario, dados.nivel(), LocalDateTime.now(clock));
        RelatoLotacao salvo = repository.save(relato);
        return new DadosDetalhamentoRelatoLotacao(salvo);
    }

    /** Nível mais relatado na janela recente. Em caso de empate, vale o mais crítico. */
    @Transactional(readOnly = true)
    public DadosResumoLotacao consultarLotacao(Long idLinha) {
        if (!linhaRepository.existsById(idLinha)) {
            throw new LinhaNotFoundException("ID da linha informado não existe!");
        }
        LocalDateTime limite = LocalDateTime.now(clock).minusMinutes(JANELA_MINUTOS);
        List<RelatoLotacao> relatos = repository.findByLinhaIdAndCriadoEmAfter(idLinha, limite);

        if (relatos.isEmpty()) {
            return new DadosResumoLotacao(null, 0, JANELA_MINUTOS, OrigemDado.RELATO_USUARIO,
                    "Sem relatos recentes para esta linha.");
        }

        Map<NivelLotacao, Integer> contagem = new EnumMap<>(NivelLotacao.class);
        for (RelatoLotacao relato : relatos) {
            contagem.merge(relato.getNivel(), 1, Integer::sum);
        }

        NivelLotacao maisRelatado = null;
        int maiorContagem = 0;
        for (NivelLotacao nivel : NivelLotacao.values()) { // BAIXA < MEDIA < ALTA
            int quantidade = contagem.getOrDefault(nivel, 0);
            if (quantidade > 0 && quantidade >= maiorContagem) {
                maiorContagem = quantidade;
                maisRelatado = nivel;
            }
        }

        return new DadosResumoLotacao(maisRelatado, relatos.size(), JANELA_MINUTOS, OrigemDado.RELATO_USUARIO,
                "Baseado em relatos voluntários de passageiros, não em contagem real.");
    }
}
