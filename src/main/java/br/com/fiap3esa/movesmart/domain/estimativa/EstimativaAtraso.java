package br.com.fiap3esa.movesmart.domain.estimativa;

import br.com.fiap3esa.movesmart.domain.OrigemDado;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Estimativa de atraso produzida pelo modelo de Machine Learning do grupo. */
@Entity(name = "EstimativaAtraso")
@Table(name = "estimativas_atraso")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class EstimativaAtraso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linha_id")
    private Linha linha;

    private Integer minutosEstimados;

    @Column(precision = 4, scale = 3)
    private BigDecimal probabilidadeAtraso;

    private String modeloVersao;
    private LocalDateTime geradoEm;

    @Enumerated(EnumType.STRING)
    private OrigemDado origem = OrigemDado.MODELO_ML;

    public EstimativaAtraso(Linha linha, DadosCadastroEstimativaAtraso dados, LocalDateTime geradoEm) {
        this.linha = linha;
        this.minutosEstimados = dados.minutosEstimados();
        this.probabilidadeAtraso = dados.probabilidadeAtraso();
        this.modeloVersao = dados.modeloVersao();
        this.geradoEm = geradoEm;
        this.origem = OrigemDado.MODELO_ML;
    }
}
