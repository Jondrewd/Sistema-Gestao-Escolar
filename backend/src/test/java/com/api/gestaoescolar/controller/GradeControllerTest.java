package com.api.gestaoescolar.controller;

import com.api.gestaoescolar.controllers.GradeController;
import com.api.gestaoescolar.dtos.EvaluationDTO;
import com.api.gestaoescolar.dtos.GradeDTO;
import com.api.gestaoescolar.services.GradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class GradeControllerTest {

    @InjectMocks
    private GradeController gradeController;

    @Mock
    private GradeService gradeService;

    private GradeDTO mockGrade;
    private EvaluationDTO mockEvaluation;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockEvaluation = new EvaluationDTO(1L, LocalDate.now(), 2L, "Matemática", 3L);
        mockGrade = new GradeDTO(1L, mockEvaluation, "12345678900", 8.5);
    }

    // Testa se conseguimos criar uma nota corretamente e o retorno vem com status 201
    @Test
    void shouldCreateGradeSuccessfully() {
        when(gradeService.create(any(GradeDTO.class))).thenReturn(mockGrade);

        ResponseEntity<GradeDTO> response = gradeController.createGrade(mockGrade);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(8.5, response.getBody().getScore());
    }

    // Testa a busca de uma nota por ID
    @Test
    void shouldReturnGradeById() {
        when(gradeService.findById(1L)).thenReturn(mockGrade);

        ResponseEntity<GradeDTO> response = gradeController.getGradeById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("12345678900", response.getBody().getStudent());
    }

    // Testa o retorno de todas as notas paginadas
    @Test
    void shouldReturnPagedGrades() {
        Page<GradeDTO> page = new PageImpl<>(List.of(mockGrade));

        when(gradeService.findAll(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<GradeDTO>> response = gradeController.getAllGrades(0, 10, "id");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    // Testa o retorno de notas por avaliação
    @Test
    void shouldReturnGradesByEvaluation() {
        when(gradeService.findByEvaluation(1L)).thenReturn(List.of(mockGrade));

        ResponseEntity<List<GradeDTO>> response = gradeController.getGradesByEvaluation(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    // Testa o retorno de notas por CPF do aluno
    @Test
    void shouldReturnGradesByStudentCpf() {
        when(gradeService.findByStudent("12345678900")).thenReturn(List.of(mockGrade));

        ResponseEntity<List<GradeDTO>> response = gradeController.getGradesByStudent("12345678900");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(8.5, response.getBody().get(0).getScore());
    }

    // Testa a atualização de uma nota
    @Test
    void shouldUpdateGrade() {
        when(gradeService.update(eq(1L), any(GradeDTO.class))).thenReturn(mockGrade);

        ResponseEntity<GradeDTO> response = gradeController.updateGrade(1L, mockGrade);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("12345678900", response.getBody().getStudent());
    }

    // Testa exclusão de uma nota existente
    @Test
    void shouldDeleteGradeById() {
        doNothing().when(gradeService).delete(1L);

        ResponseEntity<Void> response = gradeController.deleteGrade(1L);

        assertEquals(204, response.getStatusCode().value());
    }

    // Testa exclusão de todas as notas de uma avaliação
    @Test
    void shouldDeleteGradesByEvaluationId() {
        doNothing().when(gradeService).deleteByEvaluation(1L);

        ResponseEntity<Void> response = gradeController.deleteGradesByEvaluation(1L);

        assertEquals(204, response.getStatusCode().value());
    }

    // Testa cálculo de média do aluno (aprovado)
    @Test
    void shouldCalculateStudentAverageAsApproved() {
        when(gradeService.calculateStudentAverage("12345678900")).thenReturn(7.0);

        ResponseEntity<Map<String, Object>> response = gradeController.getStudentAverage("12345678900");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Aprovado", response.getBody().get("message"));
    }

    // Testa cálculo de média do aluno (reprovado)
    @Test
    void shouldCalculateStudentAverageAsFailed() {
        when(gradeService.calculateStudentAverage("12345678900")).thenReturn(4.5);

        ResponseEntity<Map<String, Object>> response = gradeController.getStudentAverage("12345678900");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Reprovado", response.getBody().get("message"));
    }

    // Testa tentativa de atualizar nota inexistente
    @Test
    void shouldFailToUpdateNonexistentGrade() {
        when(gradeService.update(eq(999L), any(GradeDTO.class)))
                .thenThrow(new RuntimeException("Nota não encontrada"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> gradeController.updateGrade(999L, mockGrade));

        assertEquals("Nota não encontrada", exception.getMessage());
    }

    // Testa tentativa de deletar nota inexistente
    @Test
    void shouldFailToDeleteNonexistentGrade() {
        doThrow(new RuntimeException("Nota não encontrada"))
                .when(gradeService).delete(999L);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> gradeController.deleteGrade(999L));

        assertEquals("Nota não encontrada", exception.getMessage());
    }
}
