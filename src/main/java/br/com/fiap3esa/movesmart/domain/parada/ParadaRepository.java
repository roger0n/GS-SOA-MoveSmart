package br.com.fiap3esa.movesmart.domain.parada;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParadaRepository extends JpaRepository<Parada, Long> {
    Optional<Parada> findByCodigoSptrans(Integer codigoSptrans);
}
