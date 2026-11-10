package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.usuario.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    @Test
    void deveCadastrarUsuarioComSenhaCodificada() {
        when(repository.existsByLogin("joao")).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("hash123");
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosListagemUsuario resultado = service.cadastrarUsuario(new DadosCadastroUsuario("joao", "123", null));

        assertThat(resultado.login()).isEqualTo("joao");
        assertThat(resultado.perfil()).isEqualTo(Perfil.USER);
        verify(passwordEncoder).encode("123");
    }

    @Test
    void naoDeveCadastrarUsuarioComLoginRepetido() {
        when(repository.existsByLogin("joao")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrarUsuario(new DadosCadastroUsuario("joao", "123", null)))
                .isInstanceOf(ValidacaoUsuarioException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deveListarUsuarios() {
        Usuario admin = new Usuario(1L, "admin", "hash", Perfil.ADMIN);
        when(repository.findAllByOrderByLoginAsc()).thenReturn(List.of(admin));

        List<DadosListagemUsuario> resultado = service.listarUsuarios();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).perfil()).isEqualTo(Perfil.ADMIN);
    }

    @Test
    void deveAtualizarPerfilDoUsuario() {
        Usuario usuario = new Usuario(2L, "joao", "hash", Perfil.USER);
        when(repository.findById(2L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosListagemUsuario resultado = service.atualizarPerfil(new DadosAtualizacaoPerfilUsuario(2L, Perfil.ADMIN));

        assertThat(resultado.perfil()).isEqualTo(Perfil.ADMIN);
    }

    @Test
    void deveLancarExcecaoAoExcluirUsuarioInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.excluirUsuario(99L))
                .isInstanceOf(UsuarioNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void deveExcluirUsuarioExistente() {
        when(repository.existsById(2L)).thenReturn(true);

        service.excluirUsuario(2L);

        verify(repository).deleteById(2L);
    }

    @Test
    void deveAlterarSenhaQuandoSenhaAtualEstaCorreta() {
        Usuario usuario = new Usuario(2L, "joao", "hashAntigo", Perfil.USER);
        when(passwordEncoder.matches("antiga", "hashAntigo")).thenReturn(true);
        when(passwordEncoder.encode("nova")).thenReturn("hashNovo");

        service.alterarSenha(usuario, new DadosAlterarSenha("antiga", "nova"));

        assertThat(usuario.getPassword()).isEqualTo("hashNovo");
        verify(repository).save(usuario);
    }

    @Test
    void naoDeveAlterarSenhaQuandoSenhaAtualEstaIncorreta() {
        Usuario usuario = new Usuario(2L, "joao", "hashAntigo", Perfil.USER);
        when(passwordEncoder.matches("errada", "hashAntigo")).thenReturn(false);

        assertThatThrownBy(() -> service.alterarSenha(usuario, new DadosAlterarSenha("errada", "nova")))
                .isInstanceOf(ValidacaoUsuarioException.class);
        verify(repository, never()).save(any());
    }
}
