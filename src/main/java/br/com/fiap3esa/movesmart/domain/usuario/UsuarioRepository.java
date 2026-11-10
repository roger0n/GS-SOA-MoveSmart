package br.com.fiap3esa.movesmart.domain.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    UserDetails findByLogin(String username);

    boolean existsByLogin(String login);

    List<Usuario> findAllByOrderByLoginAsc();
}