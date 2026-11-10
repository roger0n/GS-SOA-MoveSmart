package br.com.fiap3esa.movesmart.controller;

import br.com.fiap3esa.movesmart.domain.estimativa.DadosCadastroEstimativaAtraso;
import br.com.fiap3esa.movesmart.domain.estimativa.DadosDetalhamentoEstimativaAtraso;
import br.com.fiap3esa.movesmart.service.EstimativaAtrasoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@Tag(name = "Estimativas de atraso", description = "Estimativas geradas pelo modelo de Machine Learning (origem MODELO_ML)")
@RequestMapping("/linhas/{idLinha}/estimativa-atraso")
@RequiredArgsConstructor
public class EstimativaAtrasoController {
    private final EstimativaAtrasoService service;

    @Operation(summary = "Registrar estimativa de atraso gerada pelo modelo de ML")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoEstimativaAtraso> cadastrarEstimativa(
            @PathVariable Long idLinha,
            @RequestBody @Valid DadosCadastroEstimativaAtraso dados,
            UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoEstimativaAtraso dto = service.cadastrarEstimativa(idLinha, dados);
        URI uri = uriBuilder
                .path("/linhas/{idLinha}/estimativa-atraso")
                .buildAndExpand(idLinha)
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(summary = "Consultar a estimativa de atraso mais recente da linha")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DadosDetalhamentoEstimativaAtraso> consultarEstimativa(@PathVariable Long idLinha) {
        return ResponseEntity.ok(service.consultarUltimaEstimativa(idLinha));
    }
}
