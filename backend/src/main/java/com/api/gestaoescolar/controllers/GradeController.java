package com.api.gestaoescolar.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.gestaoescolar.dtos.GradeDTO;
import com.api.gestaoescolar.services.GradeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/grades")
@Tag(name = "Grades", description = "API para gerenciamento de notas dos alunos")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma nova nota",
               description = "Cria um novo registro de nota para um aluno em uma avaliação")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Nota cadastrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "404", description = "Avaliação ou aluno não encontrado")
    })
    public ResponseEntity<GradeDTO> createGrade(
            @RequestBody @Valid @Parameter(description = "Dados da nota a ser criada") GradeDTO gradeDTO) {
        GradeDTO createdGrade = gradeService.create(gradeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGrade);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar nota por ID",
               description = "Retorna os detalhes de uma nota específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota encontrada"),
        @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    public ResponseEntity<GradeDTO> getGradeById(
            @PathVariable @Parameter(description = "ID da nota") Long id) {
        GradeDTO gradeDTO = gradeService.findById(id);
        return ResponseEntity.ok(gradeDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todas as notas",
               description = "Retorna uma lista paginada de todas as notas registradas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de notas retornada com sucesso")
    })
    public ResponseEntity<Page<GradeDTO>> getAllGrades(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)") Pageable pageable) {
        Page<GradeDTO> grades = gradeService.findAll(pageable);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/evaluation/{evaluationId}")
    @Operation(summary = "Listar notas por avaliação",
               description = "Retorna todas as notas de uma avaliação específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de notas retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<List<GradeDTO>> getGradesByEvaluation(
            @PathVariable @Parameter(description = "ID da avaliação") Long evaluationId) {
        List<GradeDTO> grades = gradeService.findByEvaluation(evaluationId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/student/{studentCpf}")
    @Operation(summary = "Listar notas por aluno",
               description = "Retorna todas as notas de um aluno específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de notas retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    public ResponseEntity<List<GradeDTO>> getGradesByStudent(
            @PathVariable @Parameter(description = "CPF do aluno") String studentCpf) {
        List<GradeDTO> grades = gradeService.findByStudent(studentCpf);
        return ResponseEntity.ok(grades);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma nota",
               description = "Atualiza os dados de uma nota existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    public ResponseEntity<GradeDTO> updateGrade(
            @PathVariable @Parameter(description = "ID da nota a ser atualizada") Long id,
            @RequestBody @Valid @Parameter(description = "Novos dados da nota") GradeDTO gradeDTO) {
        GradeDTO updatedGrade = gradeService.update(id, gradeDTO);
        return ResponseEntity.ok(updatedGrade);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma nota",
               description = "Remove um registro de nota do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Nota excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    public ResponseEntity<Void> deleteGrade(
            @PathVariable @Parameter(description = "ID da nota a ser excluída") Long id) {
        gradeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/evaluation/{evaluationId}")
    @Operation(summary = "Excluir todas as notas de uma avaliação",
               description = "Remove todos os registros de notas associados a uma avaliação")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notas excluídas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<Void> deleteGradesByEvaluation(
            @PathVariable @Parameter(description = "ID da avaliação") Long evaluationId) {
        gradeService.deleteByEvaluation(evaluationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentCpf}/average")
    @Operation(summary = "Calcular média do aluno",
               description = "Calcula a média geral das notas de um aluno")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Média calculada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
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