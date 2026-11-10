package br.com.fiap3esa.movesmart.domain;

import br.com.fiap3esa.movesmart.domain.usuario.DadosCadastroUsuario;
import br.com.fiap3esa.movesmart.domain.usuario.Perfil;
import br.com.fiap3esa.movesmart.domain.usuario.Usuario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioTest {

    @Test
    void perfilPadraoDeveSerUserQuandoNaoInformado() {
        Usuario usuario = new Usuario(new DadosCadastroUsuario("joao", "123", null), "hash");

        assertThat(usuario.getPerfil()).isEqualTo(Perfil.USER);
        assertThat(usuario.getUsername()).isEqualTo("joao");
        assertThat(usuario.getPassword()).isEqualTo("hash");
    }

    @Test
    void deveExporPerfilComoAuthorityComPrefixoRole() {
        Usuario admin = new Usuario(new DadosCadastroUsuario("admin", "123", Perfil.ADMIN), "hash");

        assertThat(admin.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void deveAtualizarPerfilESenha() {
        Usuario usuario = new Usuario(new DadosCadastroUsuario("joao", "123", Perfil.USER), "hash");

        usuario.atualizarPerfil(Perfil.ADMIN);
        usuario.alterarSenha("novoHash");

        assertThat(usuario.getPerfil()).isEqualTo(Perfil.ADMIN);
        assertThat(usuario.getPassword()).isEqualTo("novoHash");
    }
}
