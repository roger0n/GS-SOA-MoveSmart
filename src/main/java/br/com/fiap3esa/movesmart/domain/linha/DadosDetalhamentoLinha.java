package br.com.fiap3esa.movesmart.domain.linha;

public record DadosDetalhamentoLinha(
        Long id,
        Integer codigoSptrans,
        String letreiro,
        Integer sentido,
        String terminalPrincipal,
        String terminalSecundario,
        boolean circular,
        boolean ativa) {
    public DadosDetalhamentoLinha(Linha linha) {
        this(
                linha.getId(),
                linha.getCodigoSptrans(),
                linha.getLetreiro(),
                linha.getSentido(),
                linha.getTerminalPrincipal(),
                linha.getTerminalSecundario(),
                linha.isCircular(),
                linha.isAtiva()
        );
    }
}
