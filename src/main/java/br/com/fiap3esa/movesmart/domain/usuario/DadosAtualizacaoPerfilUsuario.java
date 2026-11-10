package br.com.fiap3esa.movesmart.domain.usuario;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoPerfilUsuario(
        @NotNull
        Long id,

        @NotNull
        Perfil perfil) {
}
