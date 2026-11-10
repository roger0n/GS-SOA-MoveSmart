package br.com.fiap3esa.movesmart.controller;

import br.com.fiap3esa.movesmart.domain.linha.*;
import br.com.fiap3esa.movesmart.service.LinhaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@Tag(name = "Linhas", description = "CRUD de linhas de ônibus (exclusão lógica)")
@RequestMapping("/linhas")
@RequiredArgsConstructor
public class LinhaController {
    private final LinhaService service;

    @Operation(summary = "Cadastrar linha")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoLinha> cadastrarLinha(
            @RequestBody @Valid DadosCadastroLinha dados,
            UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoLinha dto = service.cadastrarLinha(dados);
        URI uri = uriBuilder
                .path("/linhas/{id}")
                .buildAndExpand(dto.id())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(summary = "Listar linhas ativas (paginado), com busca opcional pelo letreiro (ex: 8000)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<DadosListagemLinha>> listarLinhas(
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 10, sort = "letreiro") Pageable paginacao) {
        return ResponseEntity.ok(service.listarLinhas(busca, paginacao));
    }

    @Operation(summary = "Detalhar linha")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DadosDetalhamentoLinha> detalharLinha(@PathVariable Long id) {
        return ResponseEntity.ok(service.detalharLinha(id));
    }

    @Operation(summary = "Atualizar linha")
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoLinha> atualizarLinha(
            @RequestBody @Valid DadosAtualizacaoLinha dados) {
        return ResponseEntity.ok(service.atualizarLinha(dados));
    }

    @Operation(summary = "Excluir linha (inativa o cadastro)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirLinha(@PathVariable Long id) {
        service.excluirLinha(id);
        return ResponseEntity.noContent().build();
    }
}
