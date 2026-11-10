package br.com.fiap3esa.movesmart.controller;

import br.com.fiap3esa.movesmart.domain.ValidacaoException;
import br.com.fiap3esa.movesmart.domain.favorito.FavoritoNotFoundException;
import br.com.fiap3esa.movesmart.domain.linha.DadosListagemLinha;
import br.com.fiap3esa.movesmart.domain.usuario.Perfil;
import br.com.fiap3esa.movesmart.domain.usuario.Usuario;
import br.com.fiap3esa.movesmart.infra.exception.TratadorGlobalErros;
import br.com.fiap3esa.movesmart.service.FavoritoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FavoritoControllerTest {

    @Mock
    private FavoritoService service;

    @InjectMocks
    private FavoritoController controller;

    private MockMvc mockMvc;

    private final Usuario usuario = new Usuario(1L, "ana", "hash", Perfil.USER);
    private final DadosListagemLinha linha = new DadosListagemLinha(5L, 33000, "8000-10", 1, "A", "B");

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
    void deveListarFavoritosDoUsuarioLogado() throws Exception {
        when(service.listarFavoritos(any())).thenReturn(List.of(linha));

        mockMvc.perform(get("/favoritos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].letreiro").value("8000-10"));
    }

    @Test
    void deveFavoritarLinhaERetornar201() throws Exception {
        when(service.favoritar(any(), org.mockito.ArgumentMatchers.eq(5L))).thenReturn(linha);

        mockMvc.perform(post("/favoritos/5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void deveRetornar422AoFavoritarLinhaRepetida() throws Exception {
        when(service.favoritar(any(), org.mockito.ArgumentMatchers.eq(5L)))
                .thenThrow(new ValidacaoException("A linha informada já está nos seus favoritos!"));

        mockMvc.perform(post("/favoritos/5"))
                .andExpect(status().is(422));
    }

    @Test
    void deveRemoverFavoritoERetornar204() throws Exception {
        mockMvc.perform(delete("/favoritos/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoRemoverFavoritoInexistente() throws Exception {
        doThrow(new FavoritoNotFoundException("A linha informada não está nos seus favoritos!"))
                .when(service).desfavoritar(any(), org.mockito.ArgumentMatchers.eq(9L));

        mockMvc.perform(delete("/favoritos/9"))
                .andExpect(status().isNotFound());
    }
}
