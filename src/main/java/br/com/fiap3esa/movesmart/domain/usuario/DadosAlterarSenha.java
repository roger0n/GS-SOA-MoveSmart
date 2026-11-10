package br.com.fiap3esa.movesmart.domain.usuario;

import jakarta.validation.constraints.NotBlank;

public record DadosAlterarSenha(
        @NotBlank
        String senhaAtual,

        @NotBlank
        String novaSenha) {
}
