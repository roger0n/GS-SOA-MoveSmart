package br.com.fiap3esa.movesmart.domain.linha;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Linha")
@Table(name = "linhas")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Linha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer codigoSptrans;
    private String letreiro;
    private Integer sentido;
    private String terminalPrincipal;
    private String terminalSecundario;
    private boolean circular;
    private boolean ativa = true;

    public Linha(DadosCadastroLinha dados) {
        this.codigoSptrans = dados.codigoSptrans();
        this.letreiro = dados.letreiro();
        this.sentido = dados.sentido();
        this.terminalPrincipal = dados.terminalPrincipal();
        this.terminalSecundario = dados.terminalSecundario();
        this.circular = dados.circular();
    }

    /** Usado na sincronização com a SPTrans (Olho Vivo). */
    public Linha(Integer codigoSptrans, String letreiro, Integer sentido,
                 String terminalPrincipal, String terminalSecundario, boolean circular) {
        this.codigoSptrans = codigoSptrans;
        this.letreiro = letreiro;
        this.sentido = sentido;
        this.terminalPrincipal = terminalPrincipal;
        this.terminalSecundario = terminalSecundario;
        this.circular = circular;
    }

    public void atualizarInformacoes(DadosAtualizacaoLinha dados) {
        if (dados.letreiro() != null && !dados.letreiro().isBlank()) {
            this.letreiro = dados.letreiro();
        }
        if (dados.sentido() != null) {
            this.sentido = dados.sentido();
        }
        if (dados.terminalPrincipal() != null && !dados.terminalPrincipal().isBlank()) {
            this.terminalPrincipal = dados.terminalPrincipal();
        }
        if (dados.terminalSecundario() != null && !dados.terminalSecundario().isBlank()) {
            this.terminalSecundario = dados.terminalSecundario();
        }
        if (dados.circular() != null) {
            this.circular = dados.circular();
        }
    }

    public void atualizarDadosSptrans(String letreiro, Integer sentido,
                                      String terminalPrincipal, String terminalSecundario, boolean circular) {
        this.letreiro = letreiro;
        this.sentido = sentido;
        this.terminalPrincipal = terminalPrincipal;
        this.terminalSecundario = terminalSecundario;
        this.circular = circular;
    }

    public void excluir() {
        this.ativa = false;
    }
}
