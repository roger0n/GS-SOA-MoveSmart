package br.com.fiap3esa.movesmart.domain;

import br.com.fiap3esa.movesmart.domain.linha.DadosAtualizacaoLinha;
import br.com.fiap3esa.movesmart.domain.linha.DadosCadastroLinha;
import br.com.fiap3esa.movesmart.domain.linha.Linha;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LinhaTest {

    private Linha novaLinha() {
        return new Linha(new DadosCadastroLinha(33000, "8000-10", 1, "TERMINAL A", "TERMINAL B", false));
    }

    @Test
    void deveCriarLinhaAtivaAPartirDosDadosDeCadastro() {
        Linha linha = novaLinha();

        assertThat(linha.getCodigoSptrans()).isEqualTo(33000);
        assertThat(linha.getLetreiro()).isEqualTo("8000-10");
        assertThat(linha.isAtiva()).isTrue();
        assertThat(linha.isCircular()).isFalse();
    }

    @Test
    void deveAtualizarApenasOsCamposInformados() {
        Linha linha = novaLinha();

        linha.atualizarInformacoes(new DadosAtualizacaoLinha(1L, null, 2, null, null, true));

        assertThat(linha.getSentido()).isEqualTo(2);
        assertThat(linha.isCircular()).isTrue();
        assertThat(linha.getLetreiro()).isEqualTo("8000-10");
        assertThat(linha.getTerminalPrincipal()).isEqualTo("TERMINAL A");
    }

    @Test
    void deveAtualizarDadosVindosDaSptrans() {
        Linha linha = novaLinha();

        linha.atualizarDadosSptrans("8000-21", 2, "NOVO A", "NOVO B", true);

        assertThat(linha.getLetreiro()).isEqualTo("8000-21");
        assertThat(linha.getSentido()).isEqualTo(2);
        assertThat(linha.getTerminalSecundario()).isEqualTo("NOVO B");
    }

    @Test
    void exclusaoDeveSerLogica() {
        Linha linha = novaLinha();

        linha.excluir();

        assertThat(linha.isAtiva()).isFalse();
    }
}
