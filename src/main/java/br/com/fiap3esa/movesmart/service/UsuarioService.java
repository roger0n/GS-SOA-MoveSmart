package br.com.fiap3esa.movesmart.service;

import br.com.fiap3esa.movesmart.domain.usuario.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DadosListagemUsuario cadastrarUsuario(DadosCadastroUsuario dados) {
        if (repository.existsByLogin(dados.login())) {
            throw new ValidacaoUsuarioException("Login informado já está em uso!");
        }
        String senhaCodificada = passwordEncoder.encode(dados.senha());
        Usuario usuario = new Usuario(dados, senhaCodificada);
        Usuario salvo = repository.save(usuario);
        return new DadosListagemUsuario(salvo);
    }

    public List<DadosListagemUsuario> listarUsuarios() {
        return repository
                .findAllByOrderByLoginAsc()
                .stream()
                .map(DadosListagemUsuario::new)
                .toList();
    }

    @Transactional
    public DadosListagemUsuario atualizarPerfil(DadosAtualizacaoPerfilUsuario dados) {
        Usuario usuario = repository.findById(dados.id())
                .orElseThrow(() ->
                        new UsuarioNotFoundException("ID do usuário informado não existe!"));
        usuario.atualizarPerfil(dados.perfil());
        Usuario salvo = repository.save(usuario);
        return new DadosListagemUsuario(salvo);
    }

    @Transactional
    public void excluirUsuario(Long id) {
        if (!repository.existsById(id)) {
            throw new UsuarioNotFoundException("ID do usuário informado não existe!");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void alterarSenha(Usuario usuarioLogado, DadosAlterarSenha dados) {
        if (!passwordEncoder.matches(dados.senhaAtual(), usuarioLogado.getPassword())) {
            throw new ValidacaoUsuarioException("Senha atual informada está incorreta!");
        }
        String novaSenhaCodificada = passwordEncoder.encode(dados.novaSenha());
        usuarioLogado.alterarSenha(novaSenhaCodificada);
        repository.save(usuarioLogado);
    }
}
