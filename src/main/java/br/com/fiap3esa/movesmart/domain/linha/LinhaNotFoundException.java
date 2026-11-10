package br.com.fiap3esa.movesmart.domain.linha;

public class LinhaNotFoundException extends RuntimeException {
    public LinhaNotFoundException(String message) {
        super(message);
    }
}
