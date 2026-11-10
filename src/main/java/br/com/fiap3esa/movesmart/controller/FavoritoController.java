package br.com.fiap3esa.movesmart.controller;

import br.com.fiap3esa.movesmart.domain.linha.DadosListagemLinha;
import br.com.fiap3esa.movesmart.domain.usuario.Usuario;
import br.com.fiap3esa.movesmart.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@Tag(name = "Favoritos", description = "Linhas favoritas do usuário logado")
@RequestMapping("/favoritos")
@RequiredArgsConstructor
public class FavoritoController {
    private final FavoritoService service;

    @Operation(summary = "Listar as linhas favoritas do usuário logado")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<DadosListagemLinha>> listarFavoritos(@AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(service.listarFavoritos(usuarioLogado));
    }

    @Operation(summary = "Favoritar uma linha")
    @PostMapping("/{idLinha}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DadosListagemLinha> favoritar(
            @PathVariable Long idLinha,
            @AuthenticationPrincipal Usuario usuarioLogado,
            UriComponentsBuilder uriBuilder) {
        DadosListagemLinha dto = service.favoritar(usuarioLogado, idLinha);
        URI uri = uriBuilder.path("/favoritos").build().toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(summary = "Remover uma linha dos favoritos")
    @DeleteMapping("/{idLinha}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> desfavoritar(
            @PathVariable Long idLinha,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        service.desfavoritar(usuarioLogado, idLinha);
        return ResponseEntity.noContent().build();
    }
}
