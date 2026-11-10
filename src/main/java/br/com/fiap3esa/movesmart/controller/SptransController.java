package br.com.fiap3esa.movesmart.controller;

import br.com.fiap3esa.movesmart.domain.linha.DadosListagemLinha;
import br.com.fiap3esa.movesmart.domain.previsao.DadosResultadoSincronizacao;
import br.com.fiap3esa.movesmart.service.SptransService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "SPTrans (Olho Vivo)", description = "Integração com a API externa da SPTrans. Exige a variável de ambiente SPTRANS_TOKEN")
@RequestMapping("/sptrans")
@RequiredArgsConstructor
public class SptransController {
    private final SptransService service;

    @Operation(summary = "Buscar linhas na SPTrans pelo termo (ex: 8000) e salvar no banco",
            description = "Se a linha já existir (mesmo código SPTrans), ela é atualizada em vez de duplicada.")
    @PostMapping("/linhas/sincronizar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DadosListagemLinha>> sincronizarLinhas(@RequestParam String termo) {
        return ResponseEntity.ok(service.sincronizarLinhas(termo));
    }

    @Operation(summary = "Consultar a previsão de chegada da linha na SPTrans e salvar no banco",
            description = "Pode retornar 0 previsões se não houver ônibus circulando no momento.")
    @PostMapping("/linhas/{idLinha}/previsoes/sincronizar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosResultadoSincronizacao> sincronizarPrevisoes(@PathVariable Long idLinha) {
        return ResponseEntity.ok(service.sincronizarPrevisoes(idLinha));
    }
}
