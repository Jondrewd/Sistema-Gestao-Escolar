package com.api.gestaoescolar.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import com.api.gestaoescolar.dtos.ClassScheduleDTO;
import com.api.gestaoescolar.dtos.ClassesDTO;
import com.api.gestaoescolar.dtos.SubjectGradeDTO;
import com.api.gestaoescolar.services.ClassesService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/classes")
@Tag(
    name = "Gerenciamento de Turmas", 
    description = "Endpoint para operações de turmas acadêmicas"
)
@SecurityRequirement(name = "bearerAuth")
public class ClassesController {

    private final ClassesService classesService;

    public ClassesController(ClassesService classesService) {
        this.classesService = classesService;
    }

    @Operation(
        summary = "Criar nova turma",
        description = "Cadastra uma nova turma (ADMIN/TEACHER)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Turma criada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Recursos não encontrados"),
        @ApiResponse(responseCode = "409", description = "Conflito na criação")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ClassesDTO> createClass(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados da turma",
                required = true,
                content = @Content(schema = @Schema(implementation = ClassesDTO.class)))
            @RequestBody @Valid ClassesDTO classesDTO) {
        ClassesDTO createdClass = classesService.createClass(classesDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdClass.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdClass);
    }

    @Operation(
        summary = "Buscar turma por ID",
        description = "Recupera detalhes de uma turma"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Turma encontrada"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Turma não encontrada")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ClassesDTO> getClassById(
            @Parameter(description = "ID da turma", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(classesService.getClassById(id));
    }

    @Operation(
        summary = "Listar turmas paginadas",
        description = "Retorna todas as turmas cadastradas"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Page<ClassesDTO>> getAllClasses(
            @Parameter(description = "Número da página (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Itens por página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Campo para ordenação", example = "name")
            @RequestParam(defaultValue = "name") String sort,
            
            @Parameter(description = "Direção (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(classesService.getAllClasses(pageable));
    }

    @Operation(
        summary = "Atualizar turma",
        description = "Edita uma turma existente (ADMIN/TEACHER)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Turma atualizada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Turma não encontrada")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ClassesDTO> updateClass(
            @Parameter(description = "ID da turma", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid ClassesDTO classesDTO) {
        return ResponseEntity.ok(classesService.updateClass(id, classesDTO));
    }

    @Operation(
        summary = "Excluir turma",
        description = "Remove uma turma (ADMIN only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Turma excluída"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Turma não encontrada")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClass(
            @Parameter(description = "ID da turma", example = "1")
            @PathVariable Long id) {
        classesService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Adicionar alunos à turma",
        description = "Inclui novos alunos em uma turma (ADMIN/TEACHER)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Alunos adicionados"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Turma ou alunos não encontrados")
    })
    @PatchMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ClassesDTO> addStudentsToClass(
            @Parameter(description = "ID da turma", example = "1")
            @PathVariable Long id,
            @RequestBody List<String> studentCpfs) {
        ClassesDTO updated = classesService.addStudentsToClass(id, studentCpfs);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "Listar alunos da turma",
        description = "Retorna alunos matriculados (ADMIN/TEACHER ou alunos da turma)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Turma não encontrada")
    })
    @GetMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or @classesSecurity.isStudentInClass(#id, principal.username)")
    public ResponseEntity<List<String>> getStudentsInClass(
            @Parameter(description = "ID da turma", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(classesService.getStudentsInClass(id));
    }

    @Operation(
        summary = "Obter horários da turma",
        description = "Retorna os horários de aula de uma turma"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horários encontrados"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Turma não encontrada")
    })
    @GetMapping("/{id}/schedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<ClassScheduleDTO>> getClassSchedule(
            @Parameter(description = "ID da turma", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(classesService.getClassSchedule(id));
    }

    @Operation(
        summary = "Obter notas por matéria",
        description = "Retorna médias e notas dos alunos por disciplina (ADMIN/TEACHER)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notas recuperadas",
                   content = @Content(array = @ArraySchema(schema = @Schema(implementation = SubjectGradeDTO.class)))),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Turma não encontrada")
    })
    @GetMapping("/{id}/grades")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<SubjectGradeDTO>> getGradesByClass(
            @Parameter(description = "ID da turma", example = "1")
            @PathVariable Long id) {
        List<SubjectGradeDTO> grades = classesService.getGradesByClass(id);
        return ResponseEntity.ok(grades);
    }
}