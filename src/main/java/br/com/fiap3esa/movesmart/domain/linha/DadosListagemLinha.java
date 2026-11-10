package br.com.fiap3esa.movesmart.domain.linha;

public record DadosListagemLinha(
        Long id,
        Integer codigoSptrans,
        String letreiro,
        Integer sentido,
        String terminalPrincipal,
        String terminalSecundario) {
    public DadosListagemLinha(Linha linha) {
        this(
                linha.getId(),
                linha.getCodigoSptrans(),
                linha.getLetreiro(),
                linha.getSentido(),
                linha.getTerminalPrincipal(),
                linha.getTerminalSecundario()
        );
    }
}
