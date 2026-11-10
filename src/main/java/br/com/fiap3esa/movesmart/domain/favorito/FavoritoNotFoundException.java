package br.com.fiap3esa.movesmart.domain.favorito;

public class FavoritoNotFoundException extends RuntimeException {
    public FavoritoNotFoundException(String message) {
        super(message);
    }
}
