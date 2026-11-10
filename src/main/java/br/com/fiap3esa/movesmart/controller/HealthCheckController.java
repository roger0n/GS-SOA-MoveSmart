package br.com.fiap3esa.movesmart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health Check", description = "Verificação de integridade da API")
@RequestMapping("/health_check")
public class HealthCheckController {
    @Operation(summary = "Verificar se a API está no ar")
    @GetMapping
    public String healthCheck() {
        return "Verificação de integridade da API do MoveSmart!";
    }
}