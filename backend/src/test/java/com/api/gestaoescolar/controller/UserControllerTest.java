package com.api.gestaoescolar.controller;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.api.gestaoescolar.controllers.UserController;
import com.api.gestaoescolar.dtos.StudentDTO;
import com.api.gestaoescolar.dtos.TeacherDTO;
import com.api.gestaoescolar.dtos.UserDTO;
import com.api.gestaoescolar.services.UserService;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private UserDTO mockUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // Criando um usuário fictício só pra usar nos testes
        mockUser = new UserDTO("ADMIN", 1L, "João Silva", "12345678909", "joao@email.com", "senha123", Instant.now());
    }

    // Testa se retorna todos os usuários paginados corretamente
    @Test
    void shouldReturnAllUsers() {
        Page<UserDTO> page = new PageImpl<>(Collections.singletonList(mockUser));
        when(userService.findAll(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<UserDTO>> response = userController.findAll(0, 10, "id");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getTotalElements());
    }

    // Busca um usuário específico pelo ID
    @Test
    void shouldReturnUserById() {
        when(userService.findById(anyLong())).thenReturn(mockUser);

        ResponseEntity<UserDTO> response = userController.findById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("João Silva", response.getBody().getFullName());
    }

    // Busca o usuário pelo CPF, algo bem comum no sistema
    @Test
    void shouldReturnUserByCpf() {
        when(userService.findByCpf(anyString())).thenReturn(mockUser);

        ResponseEntity<UserDTO> response = userController.findByCpf("12345678909");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("12345678909", response.getBody().getCpf());
    }

    // Testa o filtro por tipo de usuário (admin, aluno e professor)
    @Test
    void shouldGetUsersByType() {
        Page<UserDTO> page = new PageImpl<>(Collections.singletonList(mockUser));
        when(userService.getUsersByType(anyString(), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<UserDTO>> response = userController.getUsersByType("ADMIN", 0, 10, "id");

        assertEquals(200, response.getStatusCode().value());
    }

    // Verifica se conseguimos criar um novo admin sem erro
    @Test
    void shouldCreateAdmin() {
        when(userService.createAdmin(any(UserDTO.class))).thenReturn(mockUser);
        when(userService.extractIdFromCreatedUser(any(UserDTO.class))).thenReturn(1L);

        // Simula o ServletRequestAttributes pra nao dar erro
        RequestAttributes attrs = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(attrs);

        ResponseEntity<UserDTO> response = userController.createAdmin(mockUser);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("João Silva", response.getBody().getFullName());

        // Limpa o contexto depois do teste
        RequestContextHolder.resetRequestAttributes();
    }


    // Atualiza os dados de um usuário com base no CPF
    @Test
    void shouldUpdateUser() {
        when(userService.updateUserByType(anyString(), any(UserDTO.class))).thenReturn(mockUser);

        ResponseEntity<Object> response = userController.update("12345678909", mockUser);

        assertEquals(200, response.getStatusCode().value());
    }

    // Remove um usuário usando ID
    @Test
    void shouldDeleteUserById() {
        doNothing().when(userService).delete(anyLong());

        ResponseEntity<Void> response = userController.delete(1L);

        assertEquals(204, response.getStatusCode().value());
    }

    // Remove um usuário usando CPF
    @Test
    void shouldDeleteUserByCpf() {
        doNothing().when(userService).deleteByCpf(anyString());

        ResponseEntity<Void> response = userController.deleteByCpf("12345678909");

        assertEquals(204, response.getStatusCode().value());
    }

    // Verifica se a busca por professores por especialidade funciona
    @Test
    void shouldFindTeachersBySpeciality() {
        Page<TeacherDTO> page = new PageImpl<>(Collections.emptyList());
        when(userService.findBySpeciality(anyString(), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<TeacherDTO>> response = userController.findTeachersBySpeciality("Matemática", 0, 10);

        assertEquals(200, response.getStatusCode().value());
    }

    // Busca aluno com base no número de matrícula
    @Test
    void shouldFindStudentByRegistrationNumber() {
        StudentDTO studentDTO = new StudentDTO();
        when(userService.findByRegistrationNumber(anyString())).thenReturn(studentDTO);

        ResponseEntity<StudentDTO> response = userController.findByRegistrationNumber("RA123456");

        assertEquals(200, response.getStatusCode().value());
    }
}
