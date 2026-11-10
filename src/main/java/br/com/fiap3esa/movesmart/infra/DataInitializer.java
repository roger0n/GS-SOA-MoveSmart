package br.com.fiap3esa.movesmart.infra;

import br.com.fiap3esa.movesmart.domain.usuario.DadosCadastroUsuario;
import br.com.fiap3esa.movesmart.domain.usuario.Perfil;
import br.com.fiap3esa.movesmart.domain.usuario.Usuario;
import br.com.fiap3esa.movesmart.domain.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria um usuário ADMIN inicial na primeira execução da aplicação, apenas se
 * a tabela de usuários estiver vazia. Isso evita o problema de "ovo e galinha"
 * em que seria necessário estar autenticado como ADMIN para cadastrar o
 * primeiro usuário ADMIN.
 *
 * Login padrão: admin / Senha padrão: admin123
 * Troque essa senha (PUT /usuarios/senha) assim que fizer o primeiro login.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        DadosCadastroUsuario dadosAdmin = new DadosCadastroUsuario("admin", "admin123", Perfil.ADMIN);
        Usuario admin = new Usuario(dadosAdmin, passwordEncoder.encode(dadosAdmin.senha()));
        repository.save(admin);

        log.warn("Nenhum usuário encontrado. Usuário ADMIN inicial criado -> login: admin | senha: admin123. Troque essa senha assim que possível.");
    }
}
