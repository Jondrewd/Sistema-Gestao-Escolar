package com.api.gestaoescolar.controller;

import com.api.gestaoescolar.controllers.EvaluationController;
import com.api.gestaoescolar.dtos.EvaluationDTO;
import com.api.gestaoescolar.services.EvaluationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class EvaluationControllerTest {

    @Mock
    private EvaluationService evaluationService;

    @InjectMocks
    private EvaluationController evaluationController;

    private EvaluationDTO validEvaluation;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        validEvaluation = new EvaluationDTO();
        validEvaluation.setId(1L);
        validEvaluation.setDate(LocalDate.now().plusDays(1));
        validEvaluation.setSubjectId(1L);
        validEvaluation.setClassId(1L);
    }

    // Testa se conseguimos criar uma avaliação corretamente e o retorno vem com status 201
    @Test
    void shouldCreateEvaluationSuccessfully() {
        RequestAttributes requestAttributes = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(requestAttributes);

        when(evaluationService.create(validEvaluation)).thenReturn(validEvaluation);

        ResponseEntity<EvaluationDTO> response = evaluationController.create(validEvaluation);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(validEvaluation, response.getBody());
    }

    // Testa se conseguimos buscar todas as avaliações de forma paginada
    @Test
    void shouldGetAllEvaluationsPaged() {
        Page<EvaluationDTO> page = new PageImpl<>(List.of(validEvaluation));
        when(evaluationService.findAll(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<EvaluationDTO>> response = evaluationController.findAll(0, 10, "asc", "id");

        assertEquals(200, response.getStatusCode().value());
        assertFalse(response.getBody().isEmpty());
    }

    // Testa se conseguimos buscar uma avaliação pelo ID
    @Test
    void shouldGetEvaluationById() {
        when(evaluationService.findById(1L)).thenReturn(validEvaluation);

        ResponseEntity<EvaluationDTO> response = evaluationController.findById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(validEvaluation, response.getBody());
    }

    // Testa se retorna 404 ao buscar avaliação inexistente
    @Test
    void shouldThrowExceptionWhenEvaluationDoesNotExist() {
        when(evaluationService.findById(99L)).thenThrow(new RuntimeException("Avaliação não encontrada"));

        RuntimeException thrown = assertThrows(RuntimeException.class,
            () -> evaluationController.findById(99L));

        assertEquals("Avaliação não encontrada", thrown.getMessage());
    }
        // Testa se conseguimos atualizar uma avaliação existente
    @Test
    void shouldUpdateEvaluationSuccessfully() {
        validEvaluation.setDate(LocalDate.now().plusDays(3));
        when(evaluationService.update(1L, validEvaluation)).thenReturn(validEvaluation);

        ResponseEntity<EvaluationDTO> response = evaluationController.update(1L, validEvaluation);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(validEvaluation.getDate(), response.getBody().getDate());
    }

    // Testa se retorna 404 ao tentar atualizar uma avaliação inexistente
    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingEvaluation() {
        when(evaluationService.update(eq(99L), any())).thenThrow(new RuntimeException("Avaliação não encontrada"));

        RuntimeException thrown = assertThrows(RuntimeException.class,
            () -> evaluationController.update(99L, validEvaluation));

        assertEquals("Avaliação não encontrada", thrown.getMessage());
    }
    
    // Testa se conseguimos deletar uma avaliação existente
    @Test
    void shouldDeleteEvaluationSuccessfully() {
        ResponseEntity<Void> response = evaluationController.delete(1L);

        assertEquals(204, response.getStatusCode().value());
    }

    // Testa se retorna 404 ao tentar deletar avaliação inexistente
    // Metodo convencional nao funcionava entao precisei de try catch
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingEvaluation() {
    try {
        doThrow(new RuntimeException("Avaliação não encontrada")).when(evaluationService).delete(99L);

        evaluationController.delete(99L);
        fail("Não lançou a exceção");
    } catch (RuntimeException ex) {
        assertEquals("Avaliação não encontrada", ex.getMessage());
    }
}

    // Testa se conseguimos buscar avaliações por turma de forma paginada
    @Test
    void shouldFindByClassIdPaged() {
        Page<EvaluationDTO> page = new PageImpl<>(List.of(validEvaluation));
        when(evaluationService.getEvaluationByClass(eq(1L), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<EvaluationDTO>> response = evaluationController.findByClassId(0, 10, "asc", "id", 1L);

        assertEquals(200, response.getStatusCode().value());
        assertFalse(response.getBody().isEmpty());
    }
}
