package com.api.gestaoescolar.controllers;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.gestaoescolar.dtos.LessonDTO;
import com.api.gestaoescolar.entities.Lesson;
import com.api.gestaoescolar.entities.Teacher;
import com.api.gestaoescolar.entities.Enum.DayOfWeek;
import com.api.gestaoescolar.services.LessonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/lessons")
@Tag(name = "Lessons", description = "API para gerenciamento de aulas")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping
    @Operation(summary = "Criar uma nova aula",
               description = "Cria uma nova aula com os dados fornecidos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Aula criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "409", description = "Conflito de horário com outra aula")
    })
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
        @ApiResponse(responseCode = "404", description = "Aula não encontrada")
    })
    public ResponseEntity<LessonDTO> getLessonById(
            @PathVariable @Parameter(description = "ID da aula") Long id) {
        LessonDTO lessonDTO = lessonService.findById(id);
        return ResponseEntity.ok(lessonDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todas as aulas",
               description = "Retorna uma lista paginada de todas as aulas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de aulas retornada com sucesso")
    })
    public ResponseEntity<Page<LessonDTO>> getAllLessons(
            @Parameter(description = "Parâmetros de paginação") Pageable pageable) {
        Page<LessonDTO> lessons = lessonService.findAll(pageable);
        return ResponseEntity.ok(lessons);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma aula",
               description = "Atualiza os dados de uma aula existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aula atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "404", description = "Aula não encontrada"),
        @ApiResponse(responseCode = "409", description = "Conflito de horário com outra aula")
    })
    public ResponseEntity<LessonDTO> updateLesson(
            @PathVariable @Parameter(description = "ID da aula a ser atualizada") Long id,
            @RequestBody @Valid @Parameter(description = "Novos dados da aula") LessonDTO lessonDTO) {
        LessonDTO updatedLesson = lessonService.update(id, lessonDTO);
        return ResponseEntity.ok(updatedLesson);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma aula",
               description = "Remove uma aula do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Aula excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Aula não encontrada")
    })
    public ResponseEntity<Void> deleteLesson(
            @PathVariable @Parameter(description = "ID da aula a ser excluída") Long id) {
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Listar aulas por professor",
               description = "Retorna todas as aulas associadas a um professor específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de aulas retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Professor não encontrado")
    })
    public ResponseEntity<List<LessonDTO>> getLessonsByTeacher(
            @PathVariable @Parameter(description = "ID do professor") String teacherCpf) {
        List<LessonDTO> lessons = lessonService.findByTeacher(teacherCpf);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Listar aulas por matéria",
               description = "Retorna todas as aulas associadas a uma matéria específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de aulas retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Matéria não encontrada")
    })
    public ResponseEntity<List<LessonDTO>> getLessonsBySubject(
            @PathVariable @Parameter(description = "ID da matéria") Long subjectId) {
        List<LessonDTO> lessons = lessonService.findBySubject(subjectId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/schedule/conflicts")
    @Operation(summary = "Verificar conflitos de horário",
               description = "Verifica se existe algum conflito de horário para os parâmetros fornecidos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    })
    public ResponseEntity<Boolean> checkScheduleConflict(
            @RequestParam @Parameter(description = "Dia da semana") DayOfWeek dayOfWeek,
            @RequestParam @Parameter(description = "Horário de início") @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam @Parameter(description = "Horário de término") @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
            @RequestParam(required = false) @Parameter(description = "ID do professor (opcional)") Long teacherId) {
        
        Lesson lesson = new Lesson();
        lesson.setDayOfWeek(dayOfWeek);
        lesson.setStartTime(startTime);
        lesson.setEndTime(endTime);
        if (teacherId != null) {
            Teacher teacher = new Teacher();
            teacher.setId(teacherId);
            lesson.setTeacher(teacher);
        }

        try {
            lessonService.checkForScheduleConflicts(lesson);
            return ResponseEntity.ok(false); 
        } catch (IllegalStateException e) {
            return ResponseEntity.ok(true);
        }
    }
}