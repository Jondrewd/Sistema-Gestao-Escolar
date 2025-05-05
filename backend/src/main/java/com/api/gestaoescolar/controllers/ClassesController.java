package com.api.gestaoescolar.controllers;

import com.api.gestaoescolar.dtos.ClassesDTO;
import com.api.gestaoescolar.services.ClassesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/v1/classes")
@Tag(
    name = "Gerenciamento de Turmas", 
    description = "Endpoint para operações de turmas acadêmicas, incluindo gestão de alunos matriculados"
)
public class ClassesController {

    private final ClassesService classesService;

    public ClassesController(ClassesService classesService) {
        this.classesService = classesService;
    }

    @Operation(
        summary = "Criar nova turma",
        description = "Cadastra uma nova turma no sistema com professor, curso e alunos associados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Turma criada com sucesso",
                   content = @Content(schema = @Schema(implementation = ClassesDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
        @ApiResponse(responseCode = "404", description = "Professor, curso ou aluno(s) não encontrado(s)"),
        @ApiResponse(responseCode = "409", description = "Conflito na criação da turma")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Dados da nova turma",
        required = true,
        content = @Content(
            schema = @Schema(implementation = ClassesDTO.class)
        )
    )
    @PostMapping
    public ResponseEntity<ClassesDTO> createClass(@RequestBody @Valid ClassesDTO classesDTO) {
        ClassesDTO createdClass = classesService.createClass(classesDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdClass.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdClass);
    }

    @Operation(
        summary = "Buscar turma por ID",
        description = "Recupera todos os detalhes de uma turma específica incluindo alunos matriculados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Turma encontrada com sucesso",
                   content = @Content(schema = @Schema(implementation = ClassesDTO.class))),
        @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClassesDTO> getClassById(
            @Parameter(description = "ID da turma", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(classesService.getClassById(id));
    }

    @Operation(
        summary = "Listar turmas paginadas",
        description = "Retorna uma lista paginada de todas as turmas cadastradas no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de turmas retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos")
    })
    @GetMapping
    public ResponseEntity<Page<ClassesDTO>> getAllClasses(
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
        return ResponseEntity.ok(classesService.getAllClasses(pageable));
    }

    @Operation(
        summary = "Atualizar turma",
        description = "Atualiza os dados de uma turma existente incluindo alunos matriculados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Turma atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
        @ApiResponse(responseCode = "404", description = "Turma, professor, curso ou aluno(s) não encontrado(s)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClassesDTO> updateClass(
            @Parameter(description = "ID da turma a ser atualizada", example = "1")
            @PathVariable Long id,
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados atualizados da turma",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = ClassesDTO.class)
                )
            )
            @RequestBody @Valid ClassesDTO classesDTO) {
        return ResponseEntity.ok(classesService.updateClass(id, classesDTO));
    }

    @Operation(
        summary = "Excluir turma",
        description = "Remove permanentemente uma turma do sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Turma excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(
            @Parameter(description = "ID da turma a ser excluída", example = "1")
            @PathVariable Long id) {
        classesService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Adicionar alunos à turma",
        description = "Inclui novos alunos em uma turma existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Alunos adicionados com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
        @ApiResponse(responseCode = "404", description = "Turma ou aluno(s) não encontrado(s)")
    })
    @PostMapping("/{id}/students")
    public ResponseEntity<ClassesDTO> addStudentsToClass(
            @Parameter(description = "ID da turma", example = "1")
            @PathVariable Long id,
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Lista de usernames dos alunos a serem adicionados",
                required = true 
            )
            @RequestBody List<String> studentUsernames) {
        ClassesDTO updated = classesService.addStudentsToClass(id, studentUsernames);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "Listar alunos da turma",
        description = "Retorna a lista de usernames dos alunos matriculados em uma turma"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de alunos retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Turma não encontrada")
    })
    @GetMapping("/{id}/students")
    public ResponseEntity<List<String>> getStudentsInClass(
            @Parameter(description = "ID da turma", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(classesService.getClassById(id).getStudents());
    }
}