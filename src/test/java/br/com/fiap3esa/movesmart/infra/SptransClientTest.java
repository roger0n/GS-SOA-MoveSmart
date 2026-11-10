package br.com.fiap3esa.movesmart.infra;

import br.com.fiap3esa.movesmart.domain.sptrans.ServicoExternoIndisponivelException;
import br.com.fiap3esa.movesmart.infra.externo.sptrans.SptransClient;
import br.com.fiap3esa.movesmart.infra.externo.sptrans.SptransLinha;
import br.com.fiap3esa.movesmart.infra.externo.sptrans.SptransPrevisaoLinha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/** Testa o cliente HTTP sem acessar a internet: as respostas da SPTrans são simuladas. */
class SptransClientTest {

    private static final String BASE = "https://api.olhovivo.sptrans.com.br/v2.1";

    private MockRestServiceServer server;
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        server = MockRestServiceServer.bindTo(builder).ignoreExpectOrder(true).build();
        restClient = builder.build();
    }

    private void esperarLoginComSucesso() {
        server.expect(requestTo(BASE + "/Login/Autenticar?token=token-teste"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.SET_COOKIE, "apiCredentials=abc123; path=/; HttpOnly"));
    }

    @Test
    void deveAutenticarEBuscarLinhasEnviandoOCookieDeSessao() {
        esperarLoginComSucesso();
        String json = """
                [{"cl":33000,"lc":false,"lt":"8000","sl":1,"tl":10,"tp":"TERMINAL A","ts":"TERMINAL B"}]
                """;
        server.expect(requestTo(BASE + "/Linha/Buscar?termosBusca=8000"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.COOKIE, "apiCredentials=abc123"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<SptransLinha> linhas = new SptransClient(restClient, "token-teste").buscarLinhas("8000");

        assertThat(linhas).hasSize(1);
        assertThat(linhas.get(0).codigo()).isEqualTo(33000);
        assertThat(linhas.get(0).letreiroCompleto()).isEqualTo("8000-10");
        assertThat(linhas.get(0).terminalPrincipal()).isEqualTo("TERMINAL A");
        server.verify();
    }

    @Test
    void deveBuscarPrevisaoDaLinha() {
        esperarLoginComSucesso();
        String json = """
                {"hr":"12:00","ps":[{"cp":700,"np":"Parada Teste","py":-23.5,"px":-46.6,
                 "vs":[{"p":"11001","t":"12:07","a":true,"ta":"2026-11-10T15:07:00Z","py":-23.5,"px":-46.6}]}]}
                """;
        server.expect(requestTo(BASE + "/Previsao/Linha?codigoLinha=33000"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        SptransPrevisaoLinha previsao = new SptransClient(restClient, "token-teste").buscarPrevisaoDaLinha(33000);

        assertThat(previsao.paradas()).hasSize(1);
        assertThat(previsao.paradas().get(0).nome()).isEqualTo("Parada Teste");
        assertThat(previsao.paradas().get(0).veiculos().get(0).horario()).isEqualTo("12:07");
        server.verify();
    }

    @Test
    void deveLancarExcecaoQuandoOTokenNaoEstiverConfigurado() {
        SptransClient client = new SptransClient(restClient, "");

        assertThatThrownBy(() -> client.buscarLinhas("8000"))
                .isInstanceOf(ServicoExternoIndisponivelException.class)
                .hasMessageContaining("SPTRANS_TOKEN");
    }

    @Test
    void deveLancarExcecaoQuandoASptransRecusarOToken() {
        server.expect(requestTo(BASE + "/Login/Autenticar?token=token-invalido"))
                .andRespond(withSuccess("false", MediaType.APPLICATION_JSON));

        SptransClient client = new SptransClient(restClient, "token-invalido");

        assertThatThrownBy(() -> client.buscarLinhas("8000"))
                .isInstanceOf(ServicoExternoIndisponivelException.class);
    }

    @Test
    void deveLancarExcecaoQuandoOServicoFalhar() {
        server.expect(ExpectedCount.twice(), requestTo(BASE + "/Login/Autenticar?token=token-teste"))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.SET_COOKIE, "apiCredentials=abc123; path=/"));
        server.expect(ExpectedCount.twice(), requestTo(BASE + "/Linha/Buscar?termosBusca=8000"))
                .andRespond(withServerError());

        SptransClient client = new SptransClient(restClient, "token-teste");

        assertThatThrownBy(() -> client.buscarLinhas("8000"))
                .isInstanceOf(ServicoExternoIndisponivelException.class);
    }
}
