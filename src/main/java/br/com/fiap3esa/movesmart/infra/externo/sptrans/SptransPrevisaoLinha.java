package br.com.fiap3esa.movesmart.infra.externo.sptrans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Resposta de GET /Previsao/Linha da API Olho Vivo.
 * hr = hora da consulta, ps = paradas, vs = veículos previstos em cada parada.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SptransPrevisaoLinha(
        @JsonProperty("hr") String horaConsulta,
        @JsonProperty("ps") List<Parada> paradas) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Parada(
            @JsonProperty("cp") Integer codigo,
            @JsonProperty("np") String nome,
            @JsonProperty("py") Double latitude,
            @JsonProperty("px") Double longitude,
            @JsonProperty("vs") List<Veiculo> veiculos) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Veiculo(
            @JsonProperty("p") String prefixo,
            @JsonProperty("t") String horario,
            @JsonProperty("a") Boolean acessivel) {
    }
}
