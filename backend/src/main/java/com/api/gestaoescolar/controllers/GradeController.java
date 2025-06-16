package com.api.gestaoescolar.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.gestaoescolar.dtos.GradeDTO;
import com.api.gestaoescolar.services.GradeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/grades")
@Tag(name = "Grades", description = "API para gerenciamento de notas dos alunos")
@SecurityRequirement(name = "bearerAuth")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma nova nota",
               description = "Cria um novo registro de nota (ADMIN/TEACHER)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Nota cadastrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Avaliação ou aluno não encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<GradeDTO> createGrade(
            @RequestBody @Valid @Parameter(description = "Dados da nota") GradeDTO gradeDTO) {
        GradeDTO createdGrade = gradeService.create(gradeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGrade);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar nota por ID",
               description = "Retorna os detalhes de uma nota específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota encontrada"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or (hasRole('STUDENT') and @gradeSecurity.isStudentGrade(#id, principal.username))")
    public ResponseEntity<GradeDTO> getGradeById(
            @PathVariable @Parameter(description = "ID da nota") Long id) {
        GradeDTO gradeDTO = gradeService.findById(id);
        return ResponseEntity.ok(gradeDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todas as notas",
               description = "Retorna lista paginada (ADMIN/TEACHER)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Page<GradeDTO>> getAllGrades(
            @Parameter(description = "Parâmetros de paginação") Pageable pageable) {
        Page<GradeDTO> grades = gradeService.findAll(pageable);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/evaluation/{evaluationId}")
    @Operation(summary = "Listar notas por avaliação",
               description = "Retorna notas de uma avaliação (ADMIN/TEACHER)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<GradeDTO>> getGradesByEvaluation(
            @PathVariable @Parameter(description = "ID da avaliação") Long evaluationId) {
        List<GradeDTO> grades = gradeService.findByEvaluation(evaluationId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/student/{studentCpf}")
    @Operation(summary = "Listar notas por aluno",
               description = "Retorna notas de um aluno (ADMIN/TEACHER ou próprio aluno)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or (hasRole('STUDENT') and #studentCpf == principal.username)")
    public ResponseEntity<List<GradeDTO>> getGradesByStudent(
            @PathVariable @Parameter(description = "CPF do aluno") String studentCpf) {
        List<GradeDTO> grades = gradeService.findByStudent(studentCpf);
        return ResponseEntity.ok(grades);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma nota",
               description = "Atualiza os dados de uma nota (ADMIN/TEACHER)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota atualizada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<GradeDTO> updateGrade(
            @PathVariable @Parameter(description = "ID da nota") Long id,
            @RequestBody @Valid @Parameter(description = "Novos dados") GradeDTO gradeDTO) {
        GradeDTO updatedGrade = gradeService.update(id, gradeDTO);
        return ResponseEntity.ok(updatedGrade);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma nota",
               description = "Remove um registro de nota (ADMIN only)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Nota excluída"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGrade(
            @PathVariable @Parameter(description = "ID da nota") Long id) {
        gradeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/evaluation/{evaluationId}")
    @Operation(summary = "Excluir notas de avaliação",
               description = "Remove todas notas de uma avaliação (ADMIN only)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notas excluídas"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGradesByEvaluation(
            @PathVariable @Parameter(description = "ID da avaliação") Long evaluationId) {
        gradeService.deleteByEvaluation(evaluationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentCpf}/average")
    @Operation(summary = "Calcular média do aluno",
               description = "Calcula a média geral (ADMIN/TEACHER ou próprio aluno)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Média calculada"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or (hasRole('STUDENT') and #studentCpf == principal.username)")
    public ResponseEntity<Map<String, Object>> getStudentAverage(
            @PathVariable @Parameter(description = "CPF do aluno") String studentCpf) {
        Double average = gradeService.calculateStudentAverage(studentCpf);
        
        Map<String, Object> response = new HashMap<>();
        response.put("studentCpf", studentCpf);
        response.put("average", average);
        response.put("message", average >= 6 ? "Aprovado" : "Reprovado");
        
        return ResponseEntity.ok(response);
    }
}