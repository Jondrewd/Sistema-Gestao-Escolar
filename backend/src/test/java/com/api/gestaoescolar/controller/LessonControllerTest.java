package com.api.gestaoescolar.controller;

import com.api.gestaoescolar.controllers.LessonController;
import com.api.gestaoescolar.dtos.LessonDTO;
import com.api.gestaoescolar.dtos.SubjectDTO;
import com.api.gestaoescolar.entities.Enum.DayOfWeek;
import com.api.gestaoescolar.services.LessonService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LessonControllerTest {

    @InjectMocks
    private LessonController lessonController;

    @Mock
    private LessonService lessonService;

    private LessonDTO lessonDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Cria uma aula fictícia para usar como base nos testes
        SubjectDTO subjectDTO = new SubjectDTO();
        lessonDTO = new LessonDTO(1L, subjectDTO, 5L,"Maria Oliveira",
        DayOfWeek.SEGUNDA_FEIRA, "08:30", "10:00");
    }

    // Testa se conseguimos criar uma aula corretamente e o retorno vem com status 201
    @Test
    void createLessonSuccess() {
        when(lessonService.create(any())).thenReturn(lessonDTO);

        ResponseEntity<LessonDTO> response = lessonController.createLesson(lessonDTO);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Maria Oliveira", response.getBody().getTeacher());
        verify(lessonService, times(1)).create(any());
    }

    // Simula conflito de horário e garante que a exceção é lançada
    @Test
    void createLessonConflict() {
        when(lessonService.create(any())).thenThrow(new RuntimeException("Conflito de horário"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> lessonController.createLesson(lessonDTO));
        assertEquals("Conflito de horário", ex.getMessage());
        verify(lessonService, times(1)).create(any());
    }

    // Testa a busca de uma aula pelo ID com sucesso, espera status 200
    @Test
    void getLessonByIdSuccess() {
        when(lessonService.findById(1L)).thenReturn(lessonDTO);

        ResponseEntity<LessonDTO> response = lessonController.getLessonById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
        verify(lessonService, times(1)).findById(1L);
    }

    // Simula busca por aula inexistente e verifica lançamento da exceção
    @Test
    void getLessonByIdNotFound() {
        when(lessonService.findById(999L)).thenThrow(new RuntimeException("Aula não encontrada"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> lessonController.getLessonById(999L));
        assertEquals("Aula não encontrada", ex.getMessage());
        verify(lessonService, times(1)).findById(999L);
    }

    // Testa paginação e listagem de todas as aulas, espera lista não vazia
    @Test
    void getAllLessonsSuccess() {
        Page<LessonDTO> page = new PageImpl<>(List.of(lessonDTO));
        when(lessonService.findAll(any(PageRequest.class))).thenReturn(page);

        ResponseEntity<Page<LessonDTO>> response = lessonController.getAllLessons(0, 10, "id");

        assertEquals(200, response.getStatusCode().value());
        assertFalse(response.getBody().isEmpty());
        verify(lessonService, times(1)).findAll(any(PageRequest.class));
    }

    // Testa atualização de aula existente com sucesso, status 200 esperado
    @Test
    void updateLessonSuccess() {
        when(lessonService.update(eq(1L), any())).thenReturn(lessonDTO);

        ResponseEntity<LessonDTO> response = lessonController.updateLesson(1L, lessonDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Maria Oliveira", response.getBody().getTeacher());
        verify(lessonService, times(1)).update(eq(1L), any());
    }

    // Simula atualização falha para aula inexistente, lança exceção
    @Test
    void updateLessonNotFound() {
        when(lessonService.update(eq(999L), any())).thenThrow(new RuntimeException("Aula não encontrada"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> lessonController.updateLesson(999L, lessonDTO));
        assertEquals("Aula não encontrada", ex.getMessage());
        verify(lessonService, times(1)).update(eq(999L), any());
    }

    // Testa exclusão de aula com sucesso, espera status 204 No Content
    @Test
    void deleteLessonSuccess() {
        doNothing().when(lessonService).delete(1L);

        ResponseEntity<Void> response = lessonController.deleteLesson(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(lessonService, times(1)).delete(1L);
    }

    // Simula erro ao excluir aula inexistente, espera exceção lançada
    @Test
    void deleteLessonNotFound() {
        doThrow(new RuntimeException("Aula não encontrada")).when(lessonService).delete(999L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> lessonController.deleteLesson(999L));
        assertEquals("Aula não encontrada", ex.getMessage());
        verify(lessonService, times(1)).delete(999L);
    }

    // Testa busca das aulas de um professor, retorno deve conter pelo menos uma aula
    @Test
    void getLessonsByTeacherSuccess() {
        List<LessonDTO> lessons = List.of(lessonDTO);
        when(lessonService.findByTeacher("professor@email.com")).thenReturn(lessons);

        ResponseEntity<List<LessonDTO>> response = lessonController.getLessonsByTeacher("professor@email.com");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        verify(lessonService, times(1)).findByTeacher("professor@email.com");
    }

    // Testa busca das aulas de uma matéria específica, espera lista com resultados
    @Test
    void getLessonsBySubjectSuccess() {
        List<LessonDTO> lessons = List.of(lessonDTO);
        when(lessonService.findBySubject(10L)).thenReturn(lessons);

        ResponseEntity<List<LessonDTO>> response = lessonController.getLessonsBySubject(10L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        verify(lessonService, times(1)).findBySubject(10L);
    }
}
