package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.ValidacaoException;
import br.com.fiap3esa.movesmart.domain.linha.DadosListagemLinha;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import br.com.fiap3esa.movesmart.domain.linha.LinhaNotFoundException;
import br.com.fiap3esa.movesmart.domain.linha.LinhaRepository;
import br.com.fiap3esa.movesmart.domain.parada.Parada;
import br.com.fiap3esa.movesmart.domain.parada.ParadaRepository;
import br.com.fiap3esa.movesmart.domain.previsao.DadosResultadoSincronizacao;
import br.com.fiap3esa.movesmart.domain.previsao.PrevisaoChegada;
import br.com.fiap3esa.movesmart.domain.previsao.PrevisaoChegadaRepository;
import br.com.fiap3esa.movesmart.infra.externo.sptrans.SptransClient;
import br.com.fiap3esa.movesmart.infra.externo.sptrans.SptransLinha;
import br.com.fiap3esa.movesmart.infra.externo.sptrans.SptransPrevisaoLinha;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/** Traz dados da SPTrans (Olho Vivo) para o nosso banco. Tudo que vem daqui tem origem SPTRANS_OLHO_VIVO. */
@Service
@RequiredArgsConstructor
public class SptransService {
    private final SptransClient client;
    private final LinhaRepository linhaRepository;
    private final ParadaRepository paradaRepository;
    private final PrevisaoChegadaRepository previsaoRepository;
    private final Clock clock;

    @Transactional
    public List<DadosListagemLinha> sincronizarLinhas(String termo) {
        if (termo == null || termo.isBlank()) {
            throw new ValidacaoException("Informe o termo de busca da linha (ex: 8000)!");
        }
        return client.buscarLinhas(termo.trim()).stream()
                .filter(l -> l.codigo() != null)
                .map(this::salvarLinha)
                .map(DadosListagemLinha::new)
                .toList();
    }

    @Transactional
    public DadosResultadoSincronizacao sincronizarPrevisoes(Long idLinha) {
        Linha linha = linhaRepository.findById(idLinha)
                .orElseThrow(() ->
                        new LinhaNotFoundException("ID da linha informado não existe!"));

        SptransPrevisaoLinha previsao = client.buscarPrevisaoDaLinha(linha.getCodigoSptrans());
        if (previsao == null || previsao.paradas() == null) {
            return new DadosResultadoSincronizacao(0);
        }

        LocalDateTime agora = LocalDateTime.now(clock);
        int salvas = 0;
        for (SptransPrevisaoLinha.Parada p : previsao.paradas()) {
            if (p.codigo() == null || p.veiculos() == null) {
                continue;
            }
            Parada parada = salvarParada(p);
            for (SptransPrevisaoLinha.Veiculo v : p.veiculos()) {
                LocalTime horario = lerHorario(v.horario());
                if (horario == null) {
                    continue;
                }
                previsaoRepository.save(new PrevisaoChegada(
                        linha, parada, v.prefixo(), horario, v.acessivel(), agora));
                salvas++;
            }
        }
        return new DadosResultadoSincronizacao(salvas);
    }

    private Linha salvarLinha(SptransLinha dados) {
        boolean circular = Boolean.TRUE.equals(dados.circular());
        int sentido = dados.sentido() == null ? 1 : dados.sentido();
        Linha linha = linhaRepository.findByCodigoSptrans(dados.codigo()).orElse(null);
        if (linha == null) {
            linha = new Linha(dados.codigo(), dados.letreiroCompleto(), sentido,
                    dados.terminalPrincipal(), dados.terminalSecundario(), circular);
        } else {
            linha.atualizarDadosSptrans(dados.letreiroCompleto(), sentido,
                    dados.terminalPrincipal(), dados.terminalSecundario(), circular);
        }
        return linhaRepository.save(linha);
    }

    private Parada salvarParada(SptransPrevisaoLinha.Parada dados) {
        String nome = dados.nome() == null ? "Parada " + dados.codigo() : dados.nome();
        Parada parada = paradaRepository.findByCodigoSptrans(dados.codigo()).orElse(null);
        if (parada == null) {
            parada = new Parada(dados.codigo(), nome, dados.latitude(), dados.longitude());
        } else {
            parada.atualizarInformacoes(nome, dados.latitude(), dados.longitude());
        }
        return paradaRepository.save(parada);
    }

    private LocalTime lerHorario(String texto) {
        try {
            return texto == null ? null : LocalTime.parse(texto);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
