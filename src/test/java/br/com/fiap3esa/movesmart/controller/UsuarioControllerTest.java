package br.com.fiap3esa.movesmart.controller;

import br.com.fiap3esa.movesmart.domain.usuario.DadosListagemUsuario;
import br.com.fiap3esa.movesmart.domain.usuario.Perfil;
import br.com.fiap3esa.movesmart.domain.usuario.UsuarioNotFoundException;
import br.com.fiap3esa.movesmart.infra.exception.TratadorGlobalErros;
import br.com.fiap3esa.movesmart.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService service;

    @InjectMocks
    private UsuarioController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TratadorGlobalErros())
                .build();
    }

    @Test
    void deveCadastrarUsuarioERetornar201() throws Exception {
        when(service.cadastrarUsuario(any())).thenReturn(new DadosListagemUsuario(1L, "joao", Perfil.USER));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"joao\",\"senha\":\"123\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/usuarios/1"))
                .andExpect(jsonPath("$.login").value("joao"))
                .andExpect(jsonPath("$.perfil").value("USER"));
    }

    @Test
    void deveRetornar400QuandoCadastroForInvalido() throws Exception {
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarUsuarios() throws Exception {
        when(service.listarUsuarios()).thenReturn(List.of(new DadosListagemUsuario(1L, "admin", Perfil.ADMIN)));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("admin"));
    }

    @Test
    void deveAtualizarPerfil() throws Exception {
        when(service.atualizarPerfil(any())).thenReturn(new DadosListagemUsuario(2L, "joao", Perfil.ADMIN));

        mockMvc.perform(put("/usuarios/perfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":2,\"perfil\":\"ADMIN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil").value("ADMIN"));
    }

    @Test
    void deveExcluirUsuarioERetornar204() throws Exception {
        mockMvc.perform(delete("/usuarios/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoExcluirUsuarioInexistente() throws Exception {
        doThrow(new UsuarioNotFoundException("ID do usuário informado não existe!"))
                .when(service).excluirUsuario(99L);

        mockMvc.perform(delete("/usuarios/99"))
                .andExpect(status().isNotFound());
    }
}
