package br.com.fiap3esa.movesmart.domain.lotacao;

import br.com.fiap3esa.movesmart.domain.OrigemDado;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import br.com.fiap3esa.movesmart.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Relato voluntário de lotação feito por um passageiro (não é contagem real de pessoas). */
@Entity(name = "RelatoLotacao")
@Table(name = "relatos_lotacao")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class RelatoLotacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linha_id")
    private Linha linha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private NivelLotacao nivel;

    private LocalDateTime criadoEm;

    @Enumerated(EnumType.STRING)
    private OrigemDado origem = OrigemDado.RELATO_USUARIO;

    public RelatoLotacao(Linha linha, Usuario usuario, NivelLotacao nivel, LocalDateTime criadoEm) {
        this.linha = linha;
        this.usuario = usuario;
        this.nivel = nivel;
        this.criadoEm = criadoEm;
        this.origem = OrigemDado.RELATO_USUARIO;
    }
}
