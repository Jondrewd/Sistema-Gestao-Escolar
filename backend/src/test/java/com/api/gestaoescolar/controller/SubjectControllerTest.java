package com.api.gestaoescolar.controller;

import com.api.gestaoescolar.controllers.SubjectController;
import com.api.gestaoescolar.dtos.SubjectDTO;
import com.api.gestaoescolar.services.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SubjectControllerTest {

    @InjectMocks
    private SubjectController subjectController;

    @Mock
    private SubjectService subjectService;

    private SubjectDTO mockSubject;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockSubject = new SubjectDTO(1L, "Matemática");

        // Mocka um contexto de requisição pro teste do .created() funcionar
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    // Testa se buscar todos os cursos retorna os dados corretamente com paginação
    @Test
    void shouldListAllSubjectsWithPagination() {
        List<SubjectDTO> list = List.of(mockSubject);
        Page<SubjectDTO> page = new PageImpl<>(list);

        when(subjectService.findAll(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<SubjectDTO>> response = subjectController.findAll(0, 10, "name", "asc");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getTotalElements());
        verify(subjectService).findAll(any(Pageable.class));
    }

    // Testa se consegue buscar um curso existente pelo ID
    @Test
    void shouldReturnSubjectById() {
        when(subjectService.findById(1L)).thenReturn(mockSubject);

        ResponseEntity<SubjectDTO> response = subjectController.findById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Matemática", response.getBody().getName());
    }

    // Testa se cria um curso novo e retorna status 201 com location no header
    @Test
    void shouldCreateSubjectAndReturnCreated() {
        when(subjectService.create(any(SubjectDTO.class))).thenReturn(mockSubject);

        ResponseEntity<SubjectDTO> response = subjectController.create(mockSubject);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Matemática", response.getBody().getName());
        assertTrue(response.getHeaders().getLocation().getPath().endsWith("/1"));
    }


    // Testa se atualiza um curso existente e retorna os dados atualizados
    @Test
    void shouldUpdateSubject() {
        SubjectDTO updated = new SubjectDTO(1L, "Física");
        when(subjectService.update(eq(1L), any(SubjectDTO.class))).thenReturn(updated);

        ResponseEntity<SubjectDTO> response = subjectController.update(1L, updated);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Física", response.getBody().getName());
    }

    // Testa se deleta um curso com sucesso e retorna 204
    @Test
    void shouldDeleteSubject() {
        doNothing().when(subjectService).delete(1L);

        ResponseEntity<Void> response = subjectController.delete(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(subjectService).delete(1L);
    }

    // Testa comportamento quando nenhum curso existe (lista vazia)
    @Test
    void shouldReturnEmptyPageIfNoSubjectsFound() {
        Page<SubjectDTO> emptyPage = Page.empty();
        when(subjectService.findAll(any(Pageable.class))).thenReturn(emptyPage);

        ResponseEntity<Page<SubjectDTO>> response = subjectController.findAll(0, 10, "name", "asc");

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
    }

    // Testa se o sort DESC está funcionando corretamente na ordenação
    @Test
    void shouldSortSubjectsDescending() {
        Page<SubjectDTO> page = new PageImpl<>(List.of(mockSubject));
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        when(subjectService.findAll(any(Pageable.class))).thenReturn(page);

        subjectController.findAll(0, 10, "name", "desc");

        verify(subjectService).findAll(captor.capture());
        Pageable usedPageable = captor.getValue();

        assertEquals(Sort.Direction.DESC, usedPageable.getSort().getOrderFor("name").getDirection());
    }
}
