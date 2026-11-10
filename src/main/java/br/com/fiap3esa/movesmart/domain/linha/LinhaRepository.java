package br.com.fiap3esa.movesmart.domain.linha;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinhaRepository extends JpaRepository<Linha, Long> {
    Page<Linha> findAllByAtivaTrue(Pageable paginacao);

    Page<Linha> findAllByAtivaTrueAndLetreiroContainingIgnoreCase(String letreiro, Pageable paginacao);

    Optional<Linha> findByCodigoSptrans(Integer codigoSptrans);

    boolean existsByCodigoSptrans(Integer codigoSptrans);
}
