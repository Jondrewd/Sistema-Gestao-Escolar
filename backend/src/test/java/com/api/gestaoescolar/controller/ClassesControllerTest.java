package com.api.gestaoescolar.controller;

import com.api.gestaoescolar.controllers.ClassesController;
import com.api.gestaoescolar.dtos.ClassesDTO;
import com.api.gestaoescolar.services.ClassesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ClassesControllerTest {

    @Mock
    private ClassesService classesService;

    @InjectMocks
    private ClassesController classesController;

    private ClassesDTO validClass;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        validClass = new ClassesDTO();
        validClass.setId(1L);
        validClass.setName("Turma A");
    }

    // Testa criação de turma com sucesso
    @Test
    void shouldCreateClassSuccessfully() {
        when(classesService.createClass(validClass)).thenReturn(validClass);

        ResponseEntity<ClassesDTO> response = classesController.createClass(validClass);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(validClass, response.getBody());
    }

    // Testa busca de turma por ID com sucesso
    @Test
    void shouldGetClassByIdSuccessfully() {
        when(classesService.getClassById(1L)).thenReturn(validClass);

        ResponseEntity<ClassesDTO> response = classesController.getClassById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(validClass, response.getBody());
    }

    // Testa listagem paginada de turmas com sucesso
    @Test
    void shouldReturnPagedListOfClasses() {
        Page<ClassesDTO> page = new PageImpl<>(List.of(validClass));
        when(classesService.getAllClasses(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ClassesDTO>> response = classesController.getAllClasses(0, 10, "name", "asc");

        assertEquals(200, response.getStatusCode().value());
        assertFalse(response.getBody().isEmpty());
    }

    // Testa atualização de turma com sucesso
    @Test
    void shouldUpdateClassSuccessfully() {
        validClass.setName("Turma B");
        when(classesService.updateClass(eq(1L), any())).thenReturn(validClass);

        ResponseEntity<ClassesDTO> response = classesController.updateClass(1L, validClass);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Turma B", response.getBody().getName());
    }

    // Testa exclusão de turma com sucesso
    @Test
    void shouldDeleteClassSuccessfully() {
        ResponseEntity<Void> response = classesController.deleteClass(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(classesService, times(1)).deleteClass(1L);
    }

    // Testa adição de alunos em turma com sucesso
    @Test
    void shouldAddStudentsToClassSuccessfully() {
        validClass.setStudentCpfs(List.of("12345678900"));
        when(classesService.addStudentsToClass(eq(1L), any())).thenReturn(validClass);

        ResponseEntity<ClassesDTO> response = classesController.addStudentsToClass(1L, List.of("12345678900"));

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().getStudentCpfs().contains("12345678900"));
    }

    // Testa listagem de alunos da turma com sucesso
    @Test
    void shouldGetStudentsInClassSuccessfully() {
        List<String> cpfs = List.of("12345678900");
        when(classesService.getStudentsInClass(1L)).thenReturn(cpfs);

        ResponseEntity<List<String>> response = classesController.getStudentsInClass(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(cpfs, response.getBody());
    }

    // Testa retorno vazio ao buscar alunos de uma turma sem alunos
    @Test
    void shouldReturnEmptyListWhenNoStudentsInClass() {
        when(classesService.getStudentsInClass(2L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<String>> response = classesController.getStudentsInClass(2L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // Testa atualização de turma inexistente
    @Test
    void shouldReturnNotFoundWhenUpdatingNonexistentClass() {
        when(classesService.updateClass(eq(99L), any())).thenReturn(null);

        ResponseEntity<ClassesDTO> response = classesController.updateClass(99L, validClass);

        assertEquals(404, response.getStatusCode().value());
    }

    // Testa tentativa de deletar turma inexistente
    @Test
    void shouldHandleDeleteNonexistentClassGracefully() {
        doThrow(new RuntimeException("Class not found")).when(classesService).deleteClass(99L);

        assertThrows(RuntimeException.class, () -> classesController.deleteClass(99L));
    }
}
