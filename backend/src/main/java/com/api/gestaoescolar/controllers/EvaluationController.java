package com.api.gestaoescolar.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.gestaoescolar.dtos.EvaluationDTO;
import com.api.gestaoescolar.services.EvaluationService;

import java.net.URI;

@Tag(
    name = "Gerenciamento de Avaliações", 
    description = "Endpoint para operações de avaliações acadêmicas"
)
@RestController
@RequestMapping("/api/v1/evaluations")
@SecurityRequirement(name = "bearerAuth")
public class EvaluationController {
    
    private final EvaluationService service;

    public EvaluationController(EvaluationService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar avaliações paginadas",
        description = "Retorna todas as avaliações cadastradas"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Page<EvaluationDTO>> findAll(
            @Parameter(description = "Número da página (0-based)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Itens por página", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Direção (asc/desc)", example = "desc") 
            @RequestParam(defaultValue = "asc") String direction,
            
            @Parameter(description = "Campo para ordenação", example = "date") 
            @RequestParam(defaultValue = "date") String sort) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(
        summary = "Buscar avaliação por ID",
        description = "Recupera os detalhes de uma avaliação específica"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avaliação encontrada"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<EvaluationDTO> findById(
            @Parameter(description = "ID da avaliação", example = "1") 
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
        summary = "Criar nova avaliação",
        description = "Registra uma nova avaliação (ADMIN/TEACHER)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Avaliação criada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Aluno ou curso não encontrado")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<EvaluationDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados da avaliação",
                required = true,
                content = @Content(schema = @Schema(implementation = EvaluationDTO.class)))
            @Valid @RequestBody EvaluationDTO evaluationDTO) {
        
        EvaluationDTO createdEvaluation = service.create(evaluationDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEvaluation.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdEvaluation);
    }

    @Operation(
        summary = "Atualizar avaliação",
        description = "Atualiza os dados de uma avaliação (ADMIN/TEACHER)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avaliação atualizada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<EvaluationDTO> update(
            @Parameter(description = "ID da avaliação", example = "1") 
            @PathVariable Long id,
            @Valid @RequestBody EvaluationDTO evaluationDTO) {
        return ResponseEntity.ok(service.update(id, evaluationDTO));
    }

    @Operation(
        summary = "Remover avaliação",
        description = "Remove uma avaliação (ADMIN only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Avaliação removida"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da avaliação", example = "1") 
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Buscar avaliações por classe",
        description = "Recupera avaliações de uma classe específica"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avaliações encontradas"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Classe não encontrada")
    })
    @GetMapping("/class/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Page<EvaluationDTO>> findByClassId(
            @Parameter(description = "Número da página (0-based)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Itens por página", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Direção (asc/desc)", example = "desc") 
            @RequestParam(defaultValue = "asc") String direction,
            
            @Parameter(description = "Campo para ordenação", example = "date") 
            @RequestParam(defaultValue = "date") String sort,
            
            @Parameter(description = "ID da classe", example = "1") 
            @PathVariable Long id) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(service.getEvaluationByClass(id, pageable));
    }
}