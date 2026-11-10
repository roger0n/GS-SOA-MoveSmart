package br.com.fiap3esa.movesmart.domain.previsao;

import br.com.fiap3esa.movesmart.domain.OrigemDado;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import br.com.fiap3esa.movesmart.domain.parada.Parada;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/** Previsão de chegada informada pela SPTrans. Não é uma estimativa do nosso sistema. */
@Entity(name = "PrevisaoChegada")
@Table(name = "previsoes_chegada")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PrevisaoChegada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linha_id")
    private Linha linha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parada_id")
    private Parada parada;

    private String prefixoVeiculo;
    private LocalTime horarioPrevisto;
    private Boolean acessivel;
    private LocalDateTime consultadoEm;

    @Enumerated(EnumType.STRING)
    private OrigemDado origem = OrigemDado.SPTRANS_OLHO_VIVO;

    public PrevisaoChegada(Linha linha, Parada parada, String prefixoVeiculo,
                           LocalTime horarioPrevisto, Boolean acessivel, LocalDateTime consultadoEm) {
        this.linha = linha;
        this.parada = parada;
        this.prefixoVeiculo = prefixoVeiculo;
        this.horarioPrevisto = horarioPrevisto;
        this.acessivel = acessivel;
        this.consultadoEm = consultadoEm;
        this.origem = OrigemDado.SPTRANS_OLHO_VIVO;
    }
}
