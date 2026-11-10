package br.com.fiap3esa.movesmart.domain.previsao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrevisaoChegadaRepository extends JpaRepository<PrevisaoChegada, Long> {
    List<PrevisaoChegada> findTop50ByLinhaIdOrderByConsultadoEmDesc(Long linhaId);
}
