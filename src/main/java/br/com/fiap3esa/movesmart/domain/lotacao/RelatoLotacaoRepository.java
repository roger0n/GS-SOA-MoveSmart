package br.com.fiap3esa.movesmart.domain.lotacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatoLotacaoRepository extends JpaRepository<RelatoLotacao, Long> {
    List<RelatoLotacao> findByLinhaIdAndCriadoEmAfter(Long linhaId, LocalDateTime limite);
}
