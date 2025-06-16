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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.gestaoescolar.dtos.LessonDTO;
import com.api.gestaoescolar.entities.Enum.DayOfWeek;
import com.api.gestaoescolar.services.LessonService;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@Tag(name = "Lessons", description = "API para gerenciamento de aulas")
@SecurityRequirement(name = "bearerAuth")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping
    @Operation(summary = "Criar uma nova aula",
               description = "Cria uma nova aula com os dados fornecidos (ADMIN/TEACHER only)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Aula criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "409", description = "Conflito de horário com outra aula")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<LessonDTO> createLesson(
            @RequestBody @Valid @Parameter(description = "Dados da aula a ser criada") LessonDTO lessonDTO) {
        LessonDTO createdLesson = lessonService.create(lessonDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLesson);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter uma aula por ID",
               description = "Retorna os detalhes de uma aula específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aula encontrada"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Aula não encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<LessonDTO> getLessonById(
            @PathVariable @Parameter(description = "ID da aula") Long id) {
        LessonDTO lessonDTO = lessonService.findById(id);
        return ResponseEntity.ok(lessonDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todas as aulas",
               description = "Retorna uma lista paginada de todas as aulas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de aulas retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Page<LessonDTO>> getAllLessons(
            @Parameter(description = "Parâmetros de paginação") Pageable pageable) {
        Page<LessonDTO> lessons = lessonService.findAll(pageable);
        return ResponseEntity.ok(lessons);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma aula",
               description = "Atualiza os dados de uma aula existente (ADMIN/TEACHER only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aula atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Aula não encontrada"),
        @ApiResponse(responseCode = "409", description = "Conflito de horário com outra aula")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<LessonDTO> updateLesson(
            @PathVariable @Parameter(description = "ID da aula a ser atualizada") Long id,
            @RequestBody @Valid @Parameter(description = "Novos dados da aula") LessonDTO lessonDTO) {
        LessonDTO updatedLesson = lessonService.update(id, lessonDTO);
        return ResponseEntity.ok(updatedLesson);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma aula",
               description = "Remove uma aula do sistema (ADMIN only)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Aula excluída com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Aula não encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable @Parameter(description = "ID da aula a ser excluída") Long id) {
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/teacher/{teacherCpf}")
    @Operation(summary = "Listar aulas por professor",
               description = "Retorna todas as aulas associadas a um professor específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de aulas retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Professor não encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<LessonDTO>> getLessonsByTeacher(
            @PathVariable @Parameter(description = "CPF do professor") String teacherCpf) {
        List<LessonDTO> lessons = lessonService.findByTeacher(teacherCpf);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Listar aulas por matéria",
               description = "Retorna todas as aulas associadas a uma matéria específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de aulas retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Matéria não encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<LessonDTO>> getLessonsBySubject(
            @PathVariable @Parameter(description = "ID da matéria") Long subjectId) {
        List<LessonDTO> lessons = lessonService.findBySubject(subjectId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/schedule/conflicts")
    @Operation(summary = "Verificar conflitos de horário",
               description = "Verifica se existe algum conflito de horário (ADMIN/TEACHER only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Boolean> checkScheduleConflict(
            @RequestParam @Parameter(description = "Dia da semana") DayOfWeek dayOfWeek,
            @RequestParam @Parameter(description = "Horário de início") @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam @Parameter(description = "Horário de término") @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
            @RequestParam(required = false) @Parameter(description = "ID do professor (opcional)") Long teacherId) {
        
        boolean hasConflict = lessonService.checkForScheduleConflict(dayOfWeek, startTime, endTime, teacherId);
        return ResponseEntity.ok(hasConflict);
    }
}