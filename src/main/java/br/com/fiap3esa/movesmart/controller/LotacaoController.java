package br.com.fiap3esa.movesmart.controller;

import br.com.fiap3esa.movesmart.domain.lotacao.DadosCadastroRelatoLotacao;
import br.com.fiap3esa.movesmart.domain.lotacao.DadosDetalhamentoRelatoLotacao;
import br.com.fiap3esa.movesmart.domain.lotacao.DadosResumoLotacao;
import br.com.fiap3esa.movesmart.domain.usuario.Usuario;
import br.com.fiap3esa.movesmart.service.LotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@Tag(name = "Lotação", description = "Relatos de lotação dos passageiros (origem RELATO_USUARIO)")
@RequestMapping("/linhas/{idLinha}/lotacao")
@RequiredArgsConstructor
public class LotacaoController {
    private final LotacaoService service;

    @Operation(summary = "Relatar a lotação da linha (BAIXA, MEDIA ou ALTA)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DadosDetalhamentoRelatoLotacao> relatarLotacao(
            @PathVariable Long idLinha,
            @RequestBody @Valid DadosCadastroRelatoLotacao dados,
            @AuthenticationPrincipal Usuario usuarioLogado,
            UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoRelatoLotacao dto = service.registrarRelato(idLinha, dados, usuarioLogado);
        URI uri = uriBuilder
                .path("/linhas/{idLinha}/lotacao")
                .buildAndExpand(idLinha)
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(summary = "Consultar a lotação da linha (relatos dos últimos 30 minutos)",
            description = "Não é contagem real de passageiros: é o nível mais relatado por usuários na janela recente.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DadosResumoLotacao> consultarLotacao(@PathVariable Long idLinha) {
        return ResponseEntity.ok(service.consultarLotacao(idLinha));
    }
}
