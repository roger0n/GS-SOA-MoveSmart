package br.com.fiap3esa.movesmart.domain.estimativa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstimativaAtrasoRepository extends JpaRepository<EstimativaAtraso, Long> {
    Optional<EstimativaAtraso> findFirstByLinhaIdOrderByGeradoEmDesc(Long linhaId);
}
