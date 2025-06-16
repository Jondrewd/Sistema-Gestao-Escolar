package com.api.gestaoescolar.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.gestaoescolar.dtos.SubjectDTO;
import com.api.gestaoescolar.services.SubjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "Gerenciamento de Cursos", 
    description = "Endpoint para operações de cursos acadêmicos"
)
@RestController
@RequestMapping("/api/v1/subjects")
@SecurityRequirement(name = "bearerAuth")
public class SubjectController {
    
    private final SubjectService service;

    public SubjectController(SubjectService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar cursos paginados",
        description = "Retorna todos os cursos cadastrados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cursos listados com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Page<SubjectDTO>> findAll(
            @Parameter(description = "Número da página (0-based)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Itens por página", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Campo de ordenação", example = "name") 
            @RequestParam(defaultValue = "name") String sort,
            
            @Parameter(description = "Direção (asc/desc)", example = "asc") 
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(
        summary = "Buscar curso por ID",
        description = "Recupera um curso específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso encontrado"),
        @ApiResponse(responseCode = "404", description = "Curso não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<SubjectDTO> findById(
            @Parameter(description = "ID do curso", example = "1") 
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
        summary = "Criar novo curso",
        description = "Cadastra um novo curso. Acesso exclusivo para ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Curso criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados do curso",
                required = true,
                content = @Content(schema = @Schema(implementation = SubjectDTO.class)))
            @Valid @RequestBody SubjectDTO subjectDTO) {
        
        SubjectDTO createdSubject = service.create(subjectDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdSubject.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdSubject);
    }

    @Operation(
        summary = "Atualizar curso",
        description = "Edita um curso existente. Acesso exclusivo para ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso atualizado"),
        @ApiResponse(responseCode = "404", description = "Curso não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectDTO> update(
            @Parameter(description = "ID do curso", example = "1") 
            @PathVariable Long id,
            
            @Valid @RequestBody SubjectDTO subjectDTO) {
        return ResponseEntity.ok(service.update(id, subjectDTO));
    }

    @Operation(
        summary = "Remover curso",
        description = "Exclui permanentemente um curso. Acesso exclusivo para ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Curso removido"),
        @ApiResponse(responseCode = "404", description = "Curso não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do curso", example = "1") 
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}