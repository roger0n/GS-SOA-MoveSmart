package br.com.fiap3esa.movesmart.domain.favorito;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findAllByUsuarioId(Long idUsuario);

    Optional<Favorito> findByUsuarioIdAndLinhaId(Long idUsuario, Long idLinha);

    boolean existsByUsuarioIdAndLinhaId(Long idUsuario, Long idLinha);
}
