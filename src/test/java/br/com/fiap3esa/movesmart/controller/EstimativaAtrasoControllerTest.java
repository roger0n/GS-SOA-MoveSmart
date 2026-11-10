package br.com.fiap3esa.movesmart.controller;

import br.com.fiap3esa.movesmart.domain.OrigemDado;
import br.com.fiap3esa.movesmart.domain.estimativa.DadosDetalhamentoEstimativaAtraso;
import br.com.fiap3esa.movesmart.domain.estimativa.EstimativaAtrasoNotFoundException;
import br.com.fiap3esa.movesmart.infra.exception.TratadorGlobalErros;
import br.com.fiap3esa.movesmart.service.EstimativaAtrasoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EstimativaAtrasoControllerTest {

    @Mock
    private EstimativaAtrasoService service;

    @InjectMocks
    private EstimativaAtrasoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TratadorGlobalErros())
                .build();
    }

    private DadosDetalhamentoEstimativaAtraso estimativa() {
        return new DadosDetalhamentoEstimativaAtraso(
                1L, 1L, 7, new BigDecimal("0.620"), "rf-v1", null, OrigemDado.MODELO_ML);
    }

    @Test
    void deveRegistrarEstimativaERetornar201() throws Exception {
        when(service.cadastrarEstimativa(eq(1L), any())).thenReturn(estimativa());

        mockMvc.perform(post("/linhas/1/estimativa-atraso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"minutosEstimados\":7,\"probabilidadeAtraso\":0.62,\"modeloVersao\":\"rf-v1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.origem").value("MODELO_ML"))
                .andExpect(jsonPath("$.minutosEstimados").value(7));
    }

    @Test
    void deveRetornar400QuandoProbabilidadeForMaiorQueUm() throws Exception {
        mockMvc.perform(post("/linhas/1/estimativa-atraso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"minutosEstimados\":7,\"probabilidadeAtraso\":1.5,\"modeloVersao\":\"rf-v1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoMinutosForemNegativos() throws Exception {
        mockMvc.perform(post("/linhas/1/estimativa-atraso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"minutosEstimados\":-1,\"probabilidadeAtraso\":0.5,\"modeloVersao\":\"rf-v1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveConsultarUltimaEstimativa() throws Exception {
        when(service.consultarUltimaEstimativa(1L)).thenReturn(estimativa());

        mockMvc.perform(get("/linhas/1/estimativa-atraso"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modeloVersao").value("rf-v1"));
    }

    @Test
    void deveRetornar404QuandoNaoHaEstimativa() throws Exception {
        when(service.consultarUltimaEstimativa(1L))
                .thenThrow(new EstimativaAtrasoNotFoundException("Ainda não há estimativa de atraso para esta linha!"));

        mockMvc.perform(get("/linhas/1/estimativa-atraso"))
                .andExpect(status().isNotFound());
    }
}
