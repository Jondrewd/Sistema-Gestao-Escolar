package com.api.gestaoescolar.controller;

import com.api.gestaoescolar.controllers.AttendanceController;
import com.api.gestaoescolar.dtos.AttendanceDTO;
import com.api.gestaoescolar.services.AttendanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AttendanceControllerTest {

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private AttendanceController attendanceController;

    private AttendanceDTO validAttendance;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        validAttendance = new AttendanceDTO(
            Instant.now().plusSeconds(3600), // data futura válida
            1L,
            "Matemática",
            1L,
            true,
            "12345678901"
        );
    }

    // Testa listagem paginada de presenças com sucesso
    @Test
    void shouldReturnPagedAttendanceList() {
        Page<AttendanceDTO> page = new PageImpl<>(List.of(validAttendance));
        when(attendanceService.findAll(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<AttendanceDTO>> response = attendanceController.findAll(0, 10, "date", "asc");

        assertEquals(200, response.getStatusCode().value());
        assertFalse(response.getBody().isEmpty());
    }

    // Testa busca de presença por ID com sucesso
    @Test
    void shouldFindAttendanceById() {
        when(attendanceService.findById(1L)).thenReturn(validAttendance);

        ResponseEntity<AttendanceDTO> response = attendanceController.findById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(validAttendance, response.getBody());
    }

    // Testa criação de presença com sucesso
    @Test
    void shouldCreateAttendanceSuccessfully() {
        when(attendanceService.create(any(AttendanceDTO.class))).thenReturn(validAttendance);

        ResponseEntity<AttendanceDTO> response = attendanceController.create(validAttendance);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(validAttendance, response.getBody());
        assertTrue(response.getHeaders().getLocation().toString().contains("/" + validAttendance.getId()));
    }

    // Testa atualização de presença com sucesso
    @Test
    void shouldUpdateAttendanceSuccessfully() {
        AttendanceDTO updated = new AttendanceDTO(
            validAttendance.getDate(),
            validAttendance.getSubjectId(),
            validAttendance.getSubjectName(),
            validAttendance.getId(),
            false,
            validAttendance.getStudent()
        );
        when(attendanceService.update(eq(1L), any(AttendanceDTO.class))).thenReturn(updated);

        ResponseEntity<AttendanceDTO> response = attendanceController.update(1L, updated);

        assertEquals(200, response.getStatusCode().value());
        assertFalse(response.getBody().getPresent());
    }

    // Testa exclusão de presença com sucesso
    @Test
    void shouldDeleteAttendanceSuccessfully() {
        ResponseEntity<Void> response = attendanceController.delete(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(attendanceService, times(1)).delete(1L);
    }

    // Testa listagem de presenças por CPF de estudante com sucesso
    @Test
    void shouldReturnPagedAttendancesByStudentCpf() {
        Page<AttendanceDTO> page = new PageImpl<>(List.of(validAttendance));
        when(attendanceService.findByStudentCpf(eq("12345678901"), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<AttendanceDTO>> response = attendanceController.findAttendancesByStudent(
            0, 10, "date", "asc", "12345678901"
        );

        assertEquals(200, response.getStatusCode().value());
        assertFalse(response.getBody().isEmpty());
    }

    // Testa retorno vazio ao buscar presenças de estudante sem registros
    @Test
    void shouldReturnEmptyListWhenNoAttendancesForStudent() {
        when(attendanceService.findByStudentCpf(eq("00000000000"), any(Pageable.class)))
                .thenReturn(Page.empty());

        ResponseEntity<Page<AttendanceDTO>> response = attendanceController.findAttendancesByStudent(
            0, 10, "date", "asc", "00000000000"
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // Testa busca de presença por ID inexistente (serviço retorna null)
    @Test
    void shouldReturnNotFoundWhenAttendanceNotFound() {
        when(attendanceService.findById(99L)).thenReturn(null);

        ResponseEntity<AttendanceDTO> response = attendanceController.findById(99L);
        assertEquals(404, response.getStatusCode().value());
    }

    // Testa falha na atualização de presença inexistente
    @Test
    void shouldReturnNotFoundWhenUpdatingNonexistentAttendance() {
        when(attendanceService.update(eq(99L), any())).thenReturn(null);

        ResponseEntity<AttendanceDTO> response = attendanceController.update(99L, validAttendance);
        assertEquals(404, response.getStatusCode().value());
    }

    // Testa falha na exclusão de presença inexistente
    @Test
    void shouldHandleDeleteNonexistentAttendanceGracefully() {
        doThrow(new RuntimeException("Attendance not found")).when(attendanceService).delete(99L);

        assertThrows(RuntimeException.class, () -> attendanceController.delete(99L));
    }
}
