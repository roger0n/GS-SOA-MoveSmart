package br.com.fiap3esa.movesmart.domain.sptrans;

public class ServicoExternoIndisponivelException extends RuntimeException {
    public ServicoExternoIndisponivelException(String message) {
        super(message);
    }

    public ServicoExternoIndisponivelException(String message, Throwable cause) {
        super(message, cause);
    }
}
