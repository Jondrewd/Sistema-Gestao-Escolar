package com.api.gestaoescolar.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.gestaoescolar.dtos.CourseDTO;
import com.api.gestaoescolar.services.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Gerenciamento de Cursos", 
    description = "Endpoint para operações de cursos acadêmicos"
)
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    
    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar cursos paginados",
        description = "Retorna uma lista paginada de todos os cursos cadastrados no sistema. "
                    + "Permite ordenação por nome, ID ou outros campos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de cursos retornada com sucesso",
                   content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos")
    })
    @GetMapping
    public ResponseEntity<Page<CourseDTO>> findAll(
            @Parameter(description = "Número da página (0-based)", example = "0") 
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            
            @Parameter(description = "Quantidade de itens por página", example = "10") 
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            
            @Parameter(description = "Direção da ordenação (asc/desc)", example = "asc") 
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            
            @Parameter(description = "Campo para ordenação (id, name)", example = "name") 
            @RequestParam(value = "sort", defaultValue = "id") String sort) {
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(
        summary = "Buscar curso por ID",
        description = "Recupera os detalhes de um curso específico com base no seu ID único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso encontrado com sucesso",
                   content = @Content(schema = @Schema(implementation = CourseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Curso não encontrado"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> findById(
            @Parameter(description = "ID do curso", example = "1") 
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
        summary = "Criar novo curso",
        description = "Cadastra um novo curso no sistema com os dados fornecidos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Curso criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados do curso inválidos"),
        @ApiResponse(responseCode = "409", description = "Curso já existe")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Dados do novo curso",
        required = true,
        content = @Content(
            schema = @Schema(implementation = CourseDTO.class)
        )
    )
    @PostMapping
    public ResponseEntity<CourseDTO> insert(@RequestBody CourseDTO course) {
        CourseDTO createdCourse = service.create(course);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCourse.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdCourse);
    }

    @Operation(
        summary = "Atualizar curso",
        description = "Atualiza os dados de um curso existente com base no ID fornecido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Curso não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> update(
            @Parameter(description = "ID do curso a ser atualizado", example = "1") 
            @PathVariable Long id, 
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados atualizados do curso",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = CourseDTO.class)
                )
            )
            @RequestBody CourseDTO course) {
        return ResponseEntity.ok(service.update(id, course));
    }

    @Operation(
        summary = "Remover curso",
        description = "Remove permanentemente um curso do sistema com base no ID fornecido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Curso removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Curso não encontrado"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do curso a ser removido", example = "1") 
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}