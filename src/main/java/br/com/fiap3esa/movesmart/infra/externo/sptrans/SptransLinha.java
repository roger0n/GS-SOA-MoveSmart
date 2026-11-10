package br.com.fiap3esa.movesmart.infra.externo.sptrans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Linha retornada por GET /Linha/Buscar da API Olho Vivo (SPTrans).
 * cl = código, lc = circular, lt = letreiro, tl = tipo (sufixo do letreiro),
 * sl = sentido (1 ou 2), tp = terminal principal, ts = terminal secundário.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SptransLinha(
        @JsonProperty("cl") Integer codigo,
        @JsonProperty("lc") Boolean circular,
        @JsonProperty("lt") String letreiro,
        @JsonProperty("tl") Integer tipo,
        @JsonProperty("sl") Integer sentido,
        @JsonProperty("tp") String terminalPrincipal,
        @JsonProperty("ts") String terminalSecundario) {

    public String letreiroCompleto() {
        return tipo == null ? letreiro : letreiro + "-" + tipo;
    }
}
