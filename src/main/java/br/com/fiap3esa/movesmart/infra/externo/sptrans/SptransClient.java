package br.com.fiap3esa.movesmart.infra.externo.sptrans;

import br.com.fiap3esa.movesmart.domain.sptrans.ServicoExternoIndisponivelException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cliente HTTP da API Olho Vivo (SPTrans). A API exige autenticar com o token
 * (POST /Login/Autenticar) e reenviar o cookie de sessão devolvido nas chamadas seguintes.
 * Se a sessão expirar, autentica de novo e tenta mais uma vez.
 */
@Component
public class SptransClient {
    private final RestClient restClient;
    private final String token;
    private volatile String cookie;

    public SptransClient(RestClient sptransRestClient, @Value("${sptrans.token:}") String token) {
        this.restClient = sptransRestClient;
        this.token = token;
    }

    public List<SptransLinha> buscarLinhas(String termo) {
        List<SptransLinha> linhas = executar(cookieDaSessao -> restClient.get()
                .uri("/Linha/Buscar?termosBusca={termo}", termo)
                .header(HttpHeaders.COOKIE, cookieDaSessao)
                .retrieve()
                .body(new ParameterizedTypeReference<List<SptransLinha>>() {
                }));
        return linhas == null ? List.of() : linhas;
    }

    public SptransPrevisaoLinha buscarPrevisaoDaLinha(int codigoLinha) {
        return executar(cookieDaSessao -> restClient.get()
                .uri("/Previsao/Linha?codigoLinha={codigo}", codigoLinha)
                .header(HttpHeaders.COOKIE, cookieDaSessao)
                .retrieve()
                .body(SptransPrevisaoLinha.class));
    }

    private <T> T executar(Function<String, T> chamada) {
        try {
            if (cookie == null) {
                autenticar();
            }
            try {
                return chamada.apply(cookie);
            } catch (RestClientException sessaoPossivelmenteExpirada) {
                autenticar();
                return chamada.apply(cookie);
            }
        } catch (RestClientException e) {
            throw new ServicoExternoIndisponivelException(
                    "Serviço da SPTrans (Olho Vivo) indisponível no momento!", e);
        }
    }

    private synchronized void autenticar() {
        if (token == null || token.isBlank()) {
            throw new ServicoExternoIndisponivelException(
                    "Token da SPTrans não configurado (defina a variável de ambiente SPTRANS_TOKEN)!");
        }
        ResponseEntity<Boolean> resposta = restClient.post()
                .uri("/Login/Autenticar?token={token}", token)
                .retrieve()
                .toEntity(Boolean.class);
        List<String> cookies = resposta.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (!Boolean.TRUE.equals(resposta.getBody()) || cookies == null || cookies.isEmpty()) {
            throw new ServicoExternoIndisponivelException("A SPTrans recusou o token informado!");
        }
        this.cookie = cookies.stream()
                .map(c -> c.split(";")[0])
                .collect(Collectors.joining("; "));
    }
}
