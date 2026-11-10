package br.com.fiap3esa.movesmart.controller;

import br.com.fiap3esa.movesmart.domain.linha.DadosListagemLinha;
import br.com.fiap3esa.movesmart.domain.previsao.DadosResultadoSincronizacao;
import br.com.fiap3esa.movesmart.domain.sptrans.ServicoExternoIndisponivelException;
import br.com.fiap3esa.movesmart.infra.exception.TratadorGlobalErros;
import br.com.fiap3esa.movesmart.service.SptransService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SptransControllerTest {

    @Mock
    private SptransService service;

    @InjectMocks
    private SptransController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TratadorGlobalErros())
                .build();
    }

    @Test
    void deveSincronizarLinhas() throws Exception {
        when(service.sincronizarLinhas("8000")).thenReturn(
                List.of(new DadosListagemLinha(1L, 33000, "8000-10", 1, "A", "B")));

        mockMvc.perform(post("/sptrans/linhas/sincronizar").param("termo", "8000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].letreiro").value("8000-10"));
    }

    @Test
    void deveSincronizarPrevisoes() throws Exception {
        when(service.sincronizarPrevisoes(1L)).thenReturn(new DadosResultadoSincronizacao(12));

        mockMvc.perform(post("/sptrans/linhas/1/previsoes/sincronizar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.previsoesSalvas").value(12));
    }

    @Test
    void deveRetornar503QuandoASptransEstiverIndisponivel() throws Exception {
        when(service.sincronizarLinhas("8000"))
                .thenThrow(new ServicoExternoIndisponivelException("Serviço da SPTrans (Olho Vivo) indisponível no momento!"));

        mockMvc.perform(post("/sptrans/linhas/sincronizar").param("termo", "8000"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Serviço da SPTrans (Olho Vivo) indisponível no momento!"));
    }

    @Test
    void deveRetornar400QuandoTermoNaoForInformado() throws Exception {
        mockMvc.perform(post("/sptrans/linhas/sincronizar"))
                .andExpect(status().isBadRequest());
    }
}
