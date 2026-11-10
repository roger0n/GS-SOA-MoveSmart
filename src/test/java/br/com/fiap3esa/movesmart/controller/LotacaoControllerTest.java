package br.com.fiap3esa.movesmart.controller;

import br.com.fiap3esa.movesmart.domain.OrigemDado;
import br.com.fiap3esa.movesmart.domain.linha.LinhaNotFoundException;
import br.com.fiap3esa.movesmart.domain.lotacao.DadosDetalhamentoRelatoLotacao;
import br.com.fiap3esa.movesmart.domain.lotacao.DadosResumoLotacao;
import br.com.fiap3esa.movesmart.domain.lotacao.NivelLotacao;
import br.com.fiap3esa.movesmart.domain.usuario.Perfil;
import br.com.fiap3esa.movesmart.domain.usuario.Usuario;
import br.com.fiap3esa.movesmart.infra.exception.TratadorGlobalErros;
import br.com.fiap3esa.movesmart.service.LotacaoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LotacaoControllerTest {

    @Mock
    private LotacaoService service;

    @InjectMocks
    private LotacaoController controller;

    private MockMvc mockMvc;

    private final Usuario usuario = new Usuario(1L, "ana", "hash", Perfil.USER);

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TratadorGlobalErros())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveRegistrarRelatoERetornar201() throws Exception {
        when(service.registrarRelato(eq(1L), any(), any())).thenReturn(
                new DadosDetalhamentoRelatoLotacao(10L, 1L, NivelLotacao.ALTA, null, OrigemDado.RELATO_USUARIO));

        mockMvc.perform(post("/linhas/1/lotacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nivel\":\"ALTA\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nivel").value("ALTA"))
                .andExpect(jsonPath("$.origem").value("RELATO_USUARIO"));
    }

    @Test
    void deveRetornar400QuandoNivelForInvalido() throws Exception {
        mockMvc.perform(post("/linhas/1/lotacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nivel\":\"LOTADO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoNivelNaoForInformado() throws Exception {
        mockMvc.perform(post("/linhas/1/lotacao").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveConsultarLotacao() throws Exception {
        when(service.consultarLotacao(1L)).thenReturn(
                new DadosResumoLotacao(NivelLotacao.MEDIA, 3, 30, OrigemDado.RELATO_USUARIO, "obs"));

        mockMvc.perform(get("/linhas/1/lotacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivel").value("MEDIA"))
                .andExpect(jsonPath("$.totalRelatos").value(3))
                .andExpect(jsonPath("$.origem").value("RELATO_USUARIO"));
    }

    @Test
    void deveRetornar404QuandoLinhaNaoExiste() throws Exception {
        when(service.consultarLotacao(99L))
                .thenThrow(new LinhaNotFoundException("ID da linha informado não existe!"));

        mockMvc.perform(get("/linhas/99/lotacao"))
                .andExpect(status().isNotFound());
    }
}
