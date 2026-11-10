package br.com.fiap3esa.movesmart.controller;

import br.com.fiap3esa.movesmart.domain.previsao.DadosDetalhamentoPrevisao;
import br.com.fiap3esa.movesmart.service.PrevisaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Previsões de chegada", description = "Previsões informadas pela SPTrans (origem SPTRANS_OLHO_VIVO)")
@RequestMapping("/linhas/{idLinha}/previsoes")
@RequiredArgsConstructor
public class PrevisaoController {
    private final PrevisaoService service;

    @Operation(summary = "Listar as últimas previsões de chegada guardadas para a linha")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<DadosDetalhamentoPrevisao>> listarPrevisoes(@PathVariable Long idLinha) {
        return ResponseEntity.ok(service.listarUltimasPrevisoes(idLinha));
    }
}
