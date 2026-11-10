package br.com.fiap3esa.movesmart.controller;

import br.com.fiap3esa.movesmart.domain.linha.DadosDetalhamentoLinha;
import br.com.fiap3esa.movesmart.domain.linha.DadosListagemLinha;
import br.com.fiap3esa.movesmart.domain.linha.LinhaNotFoundException;
import br.com.fiap3esa.movesmart.infra.exception.TratadorGlobalErros;
import br.com.fiap3esa.movesmart.service.LinhaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LinhaControllerTest {

    @Mock
    private LinhaService service;

    @InjectMocks
    private LinhaController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TratadorGlobalErros())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    private static final String JSON_VALIDO = """
            {
              "codigoSptrans": 33000,
              "letreiro": "8000-10",
              "sentido": 1,
              "terminalPrincipal": "TERMINAL A",
              "terminalSecundario": "TERMINAL B",
              "circular": false
            }
            """;

    @Test
    void deveCadastrarLinhaERetornar201ComLocation() throws Exception {
        when(service.cadastrarLinha(any())).thenReturn(new DadosDetalhamentoLinha(
                1L, 33000, "8000-10", 1, "TERMINAL A", "TERMINAL B", false, true));

        mockMvc.perform(post("/linhas").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/linhas/1"))
                .andExpect(jsonPath("$.letreiro").value("8000-10"));
    }

    @Test
    void deveRetornar400QuandoCadastroForInvalido() throws Exception {
        mockMvc.perform(post("/linhas").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoSentidoForInvalido() throws Exception {
        String json = JSON_VALIDO.replace("\"sentido\": 1", "\"sentido\": 5");

        mockMvc.perform(post("/linhas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarLinhas() throws Exception {
        var pagina = new PageImpl<>(
                List.of(new DadosListagemLinha(1L, 33000, "8000-10", 1, "TERMINAL A", "TERMINAL B")),
                PageRequest.of(0, 10), 1);
        when(service.listarLinhas(isNull(), any())).thenReturn(pagina);

        mockMvc.perform(get("/linhas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].letreiro").value("8000-10"));
    }

    @Test
    void deveRetornar404QuandoLinhaNaoExiste() throws Exception {
        when(service.detalharLinha(anyLong()))
                .thenThrow(new LinhaNotFoundException("ID da linha informado não existe!"));

        mockMvc.perform(get("/linhas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("ID da linha informado não existe!"));
    }

    @Test
    void deveAtualizarLinha() throws Exception {
        when(service.atualizarLinha(any())).thenReturn(new DadosDetalhamentoLinha(
                1L, 33000, "8000-21", 1, "TERMINAL A", "TERMINAL B", false, true));

        mockMvc.perform(put("/linhas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"letreiro\":\"8000-21\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.letreiro").value("8000-21"));
    }

    @Test
    void deveExcluirLinhaERetornar204() throws Exception {
        mockMvc.perform(delete("/linhas/1"))
                .andExpect(status().isNoContent());
    }
}
