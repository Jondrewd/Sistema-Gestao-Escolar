package com.api.gestaoescolar.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.gestaoescolar.dtos.EvaluationDTO;
import com.api.gestaoescolar.services.EvaluationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Gerenciamento de Avaliações", 
    description = "Endpoint para operações de avaliações acadêmicas"
)
@RestController
@RequestMapping("/api/v1/evaluations")
public class EvaluationController {
    
    private final EvaluationService service;

    public EvaluationController(EvaluationService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar avaliações paginadas",
        description = "Retorna uma lista paginada de todas as avaliações cadastradas no sistema. "
                    + "Permite ordenação por data, nota ou outros campos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos")
    })
    @GetMapping
    public ResponseEntity<Page<EvaluationDTO>> findAll(
            @Parameter(description = "Número da página (0-based)", example = "0") 
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            
            @Parameter(description = "Quantidade de itens por página", example = "10") 
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            
            @Parameter(description = "Direção da ordenação (asc/desc)", example = "desc") 
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            
            @Parameter(description = "Campo para ordenação (date, score)", example = "date") 
            @RequestParam(value = "sort", defaultValue = "date") String sort) {
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(
        summary = "Buscar avaliação por ID",
        description = "Recupera os detalhes de uma avaliação específica com base no seu ID único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avaliação encontrada com sucesso",
                    content = @Content(schema = @Schema(implementation = EvaluationDTO.class))),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationDTO> findById(
            @Parameter(description = "ID da avaliação", example = "1") 
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
        summary = "Criar nova avaliação",
        description = "Registra uma nova avaliação no sistema para um aluno em um curso específico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Avaliação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados da avaliação inválidos"),
        @ApiResponse(responseCode = "404", description = "Aluno ou curso não encontrado")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Dados da nova avaliação",
        required = true,
        content = @Content(
            schema = @Schema(implementation = EvaluationDTO.class)
        )
    )
    @PostMapping
    public ResponseEntity<EvaluationDTO> insert(@RequestBody EvaluationDTO evaluation) {
        EvaluationDTO createdEvaluation = service.create(evaluation);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEvaluation.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdEvaluation);
    }

    @Operation(
        summary = "Atualizar avaliação",
        description = "Atualiza os dados de uma avaliação existente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avaliação atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EvaluationDTO> update(
            @Parameter(description = "ID da avaliação", example = "1") 
            @PathVariable Long id, 
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados atualizados da avaliação",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = EvaluationDTO.class)
                )
            )
            @RequestBody EvaluationDTO evaluation) {
        return ResponseEntity.ok(service.update(id, evaluation));
    }

    @Operation(
        summary = "Remover avaliação",
        description = "Remove permanentemente uma avaliação do sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Avaliação removida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da avaliação a ser removida", example = "1") 
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}